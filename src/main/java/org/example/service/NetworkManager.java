package org.example.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

import javafx.util.Pair;

public class NetworkManager {

    Socket socket;
    Runnable onDisconnect;

    public NetworkManager(Socket socket, Runnable onDisconnect) {
        this.socket = socket;
        this.onDisconnect = onDisconnect;
    }

    public void startReceiving() {
        Thread receiveThread = new Thread(this::receiveLoop);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void receiveLoop() {
        int expectedTick = 0;
        long lastPacketTime = System.currentTimeMillis();
        DataInputStream inputStream;

        try {
            socket.setSoTimeout(20);
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("[Connection lost]");
            closeConnection(true);
            return;
        }

        while (true) {
            long loopStart = System.currentTimeMillis();
            Pair<Integer, byte[]> payload;
            try {
                payload = tryRead(inputStream);
            } catch (IOException e) {
                System.err.println("[Connection lost]");
                closeConnection(true);
                break;
            }

            // 지연이 100ms를 넘으면 끊김으로 간주
            if (System.currentTimeMillis() - lastPacketTime > 100) {
                System.err.println("[excessive delay (over 100ms)]");
                closeConnection(true);
                break;
            }
            
            expectedTick++;
            if (payload != null) {
                lastPacketTime = System.currentTimeMillis();
                
                // tick이 맞지 않으면 맞을 때까지 consume
                while (payload.getKey() != expectedTick) {
                    System.err.println("[Tick mismatch: expected " + expectedTick + ", got " + payload.getKey() + "]");
                    try {
                        payload = tryRead(inputStream);
                        if (payload == null) {
                            break;
                        }
                    } catch (IOException e) {
                        System.err.println("[Connection lost while consuming]");
                        closeConnection(true);
                        return;
                    }
                }
                
                if (payload != null && payload.getKey() == expectedTick) {
                    //payload 처리 로직 추가
                }
            }

            // 20ms 틱 속도 유지
            long sleep = 20 - (System.currentTimeMillis() - loopStart);
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.err.println("[Connection thread forcibly terminated]");
                    closeConnection(false);
                    break;
                }
            }
        }
    }

    private Pair<Integer, byte[]> tryRead(DataInputStream in) throws IOException {
        try {
            int length = in.readInt(); // 페이로드 길이
            int tick = in.readInt();   // 패킷 타임스탬프

            byte[] data = new byte[length];
            int totalRead = 0;
            while (totalRead < length) {
                int bytesRead = in.read(data, totalRead, length - totalRead);
                if (bytesRead == -1) {
                    throw new IOException("[EOF: packet body incomplete]");
                }
                totalRead += bytesRead;
            }
            //혹시 모를 메모:
            //각 read당 20ms를 줬는데, read와 read 사이에서 20ms 이상 지연되면 다음 tryRead 호출에서 문제가 생김
            //하나의 패킷 안에서 간격이 20ms까지 안 갈 것 같긴한데 원인 모를 셧다운이 생기면 이를 의심해볼것

            return new Pair<>(tick, data);

        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    private void closeConnection(boolean remoteDisconnect) {
        if (remoteDisconnect) {
            onDisconnect.run();
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("[Error while closing socket]");
            e.printStackTrace();
        }
    }

    /**
     * 로컬 머신의 IP 주소를 반환합니다.
     * 
     * @return 로컬 IP 주소 문자열
     */
    public static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // 루프백이 아니고 활성화된 인터페이스만 확인
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    System.out.println(addr.getHostAddress());
                    // IPv4 주소만 선택
                    if (addr.getHostAddress().contains(":")) {
                        continue; // IPv6 주소는 스킵
                    }
                    return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unable to detect IP";
    }

    /**
     * 주어진 문자열이 올바른 IPv4 주소 형식인지 검사합니다.
     * 
     * @param ipAddress 검사할 IP 주소 문자열
     * @return 올바른 IPv4 주소이면 true, 아니면 false
     */
    public static boolean isValidIPv4(String ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        String trimmed = ipAddress.trim();

        if (trimmed.isEmpty()) {
            return false;
        }

        // 각 옥텟이 0-255 범위인지 확인
        String[] octets = trimmed.split("\\.");
        if (octets.length != 4) {
            return false;
        }

        try {
            for (String octet : octets) {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
