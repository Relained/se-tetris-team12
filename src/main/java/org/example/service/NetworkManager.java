package org.example.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

public class NetworkManager {

    Socket socket;

    // public NetworkManager(Socket socket) {
    //     this.socket = socket;
    //     socket.setSoTimeout(1);
    // }

    // private Packet tryRead(DataInputStream in) {
    //     try {
    //         int length = in.readInt();  // 패킷 전체 길이 (tick 포함)
    //         int tick   = in.readInt();  // tick = 4 bytes

    //         int bodyLength = length - 4;
    //         if (bodyLength < 0) {
    //             throw new IOException("Invalid packet length: " + length);
    //         }

    //         byte[] data = in.readNBytes(bodyLength);

    //         // readNBytes는 EOF 시 짧은 배열을 반환하므로 반드시 길이 검사
    //         if (data.length != bodyLength) {
    //             throw new IOException("EOF: packet body incomplete");
    //         }

    //         return new Packet(tick, data);

    //     } catch (SocketTimeoutException e) {
    //         // 이번 틱에 패킷을 못 받은 것 → 정상
    //         return null;

    //     } catch (IOException e) {
    //         // 여기로 오면 반드시 '연결 끊김/EOF/스트림 종료/비정상 종료'
    //         throw new RuntimeException("연결 끊김", e);
    //     }
    // }

    // public void startReceiving(InputStream inputStream) throws InterruptedException {
    //     int expectedTick = 0;
    //     long lastPacketTime = System.currentTimeMillis();

    //     while (true) {
    //         long loopStart = System.currentTimeMillis();

    //         Packet p = tryRead(in);
    //         if (p != null) {
    //             if (p.tick != expectedTick) {
    //                 System.out.println("Tick mismatch: expected=" + expectedTick + " got=" + p.tick);
    //                 break;
    //             }
    //             lastPacketTime = System.currentTimeMillis();
    //             expectedTick++;
    //         }

    //         // 100ms 동안 패킷이 아예 안 오면 끊김으로 간주
    //         if (System.currentTimeMillis() - lastPacketTime > 100) {
    //             System.out.println("연결 끊김 or 지연 과다");
    //             break;
    //         }

    //         // 20ms 틱 속도 유지
    //         long sleep = 20 - (System.currentTimeMillis() - loopStart);
    //         if (sleep > 0) Thread.sleep(sleep);
    //     }
    // }

    /**
     * 로컬 머신의 IP 주소를 반환합니다.
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
