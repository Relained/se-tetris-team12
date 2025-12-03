package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NetworkUtility 클래스의 단위 테스트
 * IP 주소 검증 로직과 네트워크 유틸리티 함수들을 테스트합니다.
 */
class NetworkUtilityTest {

    @Test
    @DisplayName("유효한 IPv4 주소 - 일반적인 IP 주소들")
    void testValidIPv4Addresses() {
        assertTrue(NetworkUtility.isValidIPv4("192.168.0.1"));
        assertTrue(NetworkUtility.isValidIPv4("10.0.0.1"));
        assertTrue(NetworkUtility.isValidIPv4("172.16.0.1"));
        assertTrue(NetworkUtility.isValidIPv4("127.0.0.1"));
        assertTrue(NetworkUtility.isValidIPv4("255.255.255.255"));
        assertTrue(NetworkUtility.isValidIPv4("0.0.0.0"));
    }

    @Test
    @DisplayName("유효한 IPv4 주소 - 경계값 테스트")
    void testValidIPv4BoundaryValues() {
        assertTrue(NetworkUtility.isValidIPv4("0.0.0.0"));
        assertTrue(NetworkUtility.isValidIPv4("255.255.255.255"));
        assertTrue(NetworkUtility.isValidIPv4("1.1.1.1"));
        assertTrue(NetworkUtility.isValidIPv4("192.168.1.100"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("무효한 IPv4 주소 - null 및 빈 문자열")
    void testInvalidIPv4NullAndEmpty(String ipAddress) {
        assertFalse(NetworkUtility.isValidIPv4(ipAddress));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "256.1.1.1",        // 옥텟 범위 초과
        "192.168.1.256",    // 마지막 옥텟 범위 초과
        "-1.0.0.0",         // 음수
        "192.168.1",        // 옥텟 부족
        "192.168.1.1.1",    // 옥텟 초과
        "192.168..1",       // 빈 옥텟
        "a.b.c.d",          // 문자
        "192.168.1.a",      // 일부 문자
        "192.168.1.1a",     // 숫자+문자
        "192 168 1 1",      // 공백
        "192.168.1.1 ",     // 뒤 공백 (trim 되어있지 않은 경우)
        " 192.168.1.1",     // 앞 공백
        "2001:0db8::1",     // IPv6
        "::1",              // IPv6 loopback
    })
    @DisplayName("무효한 IPv4 주소 - 다양한 잘못된 형식")
    void testInvalidIPv4Formats(String ipAddress) {
        assertFalse(NetworkUtility.isValidIPv4(ipAddress));
    }

    @Test
    @DisplayName("getLocalIPAddress는 비어있지 않은 문자열을 반환해야 함")
    void testGetLocalIPAddressReturnsNonEmpty() {
        String ipAddress = NetworkUtility.getLocalIPAddress();
        // 로컬 환경에 따라 빈 문자열일 수 있으므로, 반환 타입만 확인
        // 실제 네트워크 인터페이스가 있다면 유효한 IP를 반환해야 함
        assertNotNull(ipAddress);
        
        // 네트워크 인터페이스가 있을 경우 유효한 IPv4 형식이어야 함
        if (!ipAddress.isEmpty()) {
            assertTrue(NetworkUtility.isValidIPv4(ipAddress), 
                "반환된 IP 주소는 유효한 IPv4 형식이어야 합니다: " + ipAddress);
        }
    }

    @Test
    @DisplayName("getBroadcastAddress는 null이거나 유효한 IPv4 주소를 반환해야 함")
    void testGetBroadcastAddressReturnsValidOrNull() {
        InetAddress broadcastAddress = NetworkUtility.getBroadcastAddress();
        
        // 네트워크 환경에 따라 null일 수 있음
        if (broadcastAddress != null) {
            // 브로드캐스트 주소는 유효한 IPv4 주소여야 함
            String addressStr = broadcastAddress.getHostAddress();
            assertNotNull(addressStr);
            assertTrue(NetworkUtility.isValidIPv4(addressStr),
                "브로드캐스트 주소는 유효한 IPv4 형식이어야 합니다: " + addressStr);
            
            // 브로드캐스트 주소는 일반적으로 마지막 옥텟이 255
            // (예: 192.168.1.255, 10.0.255.255 등)
            assertTrue(addressStr.endsWith("255") || addressStr.contains(".255."),
                "브로드캐스트 주소는 일반적으로 255를 포함해야 합니다: " + addressStr);
        }
    }

    @Test
    @DisplayName("getLocalIPAddress와 getBroadcastAddress의 일관성 테스트")
    void testLocalIPAndBroadcastAddressConsistency() {
        String localIP = NetworkUtility.getLocalIPAddress();
        InetAddress broadcastAddr = NetworkUtility.getBroadcastAddress();
        
        // 둘 다 값이 있을 경우, 같은 네트워크 대역에 속해야 함
        if (!localIP.isEmpty() && broadcastAddr != null) {
            String[] localOctets = localIP.split("\\.");
            String[] broadcastOctets = broadcastAddr.getHostAddress().split("\\.");
            
            // 최소한 첫 번째 옥텟은 같아야 함 (같은 클래스 네트워크)
            assertEquals(localOctets[0], broadcastOctets[0],
                "로컬 IP와 브로드캐스트 주소는 같은 네트워크 클래스여야 합니다");
        }
    }

    @Test
    @DisplayName("특수 IP 주소 검증")
    void testSpecialIPAddresses() {
        // Loopback
        assertTrue(NetworkUtility.isValidIPv4("127.0.0.1"));
        assertTrue(NetworkUtility.isValidIPv4("127.0.0.0"));
        assertTrue(NetworkUtility.isValidIPv4("127.255.255.255"));
        
        // Private networks
        assertTrue(NetworkUtility.isValidIPv4("10.0.0.0"));
        assertTrue(NetworkUtility.isValidIPv4("172.16.0.0"));
        assertTrue(NetworkUtility.isValidIPv4("192.168.0.0"));
        
        // Broadcast
        assertTrue(NetworkUtility.isValidIPv4("255.255.255.255"));
        
        // Default route
        assertTrue(NetworkUtility.isValidIPv4("0.0.0.0"));
    }
}
