package org.example.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 네트워크 유틸리티 클래스 (추후 슈퍼클래스로 수정 예정)
 */
public class NetworkManager {

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
     * 주어진 문자열이 올바른 IPv4 주소 형식인지 검사합니다. (trim 되어있다고 가정)
     * 
     * @param ipAddress 검사할 IP 주소 문자열
     * @return 올바른 IPv4 주소이면 true, 아니면 false
     */
    public static boolean isValidIPv4(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }

        // 각 옥텟이 0-255 범위인지 확인
        String[] octets = ipAddress.split("\\.");
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

