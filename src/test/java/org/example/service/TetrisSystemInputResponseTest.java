package org.example.service;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 비기능적 요구사항 테스트: 입력 응답 속도
 * 
 * 요구사항: 블록 이동/회전의 입력 response는 100ms 이하로 유지되어야 한다
 * 
 * Reference: 
 * - 일반적인 게임 입력 반응 시간 기준: 100ms 이하
 * - 테트리스와 같은 빠른 액션이 필요한 게임에서는 더 빠른 반응이 필요
 * - 사용자 경험(UX) 관점에서 100ms 이하의 지연은 거의 감지되지 않음
 */
@DisplayName("TetrisSystem 입력 응답 성능 테스트")
class TetrisSystemInputResponseTest {
    
    private static final long MAX_RESPONSE_TIME_MS = 100; // 최대 허용 응답 시간 (밀리초)
    private static final int TEST_ITERATIONS = 100; // 테스트 반복 횟수
    
    private TetrisSystem tetrisSystem;
    
    @BeforeEach
    void setUp() {
        tetrisSystem = new TetrisSystem();
    }
    
    @Test
    @DisplayName("moveLeft() 응답 시간 테스트 - 평균 100ms 이하")
    void testMoveLeftResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime = System.nanoTime();
            tetrisSystem.moveLeft();
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000; // 나노초를 밀리초로 변환
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("moveRight() 응답 시간 테스트 - 평균 100ms 이하")
    void testMoveRightResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime = System.nanoTime();
            tetrisSystem.moveRight();
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000;
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("moveDown() 응답 시간 테스트 - 평균 100ms 이하")
    void testMoveDownResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime = System.nanoTime();
            tetrisSystem.moveDown();
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000;
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("rotateClockwise() 응답 시간 테스트 - 평균 100ms 이하")
    void testRotateClockwiseResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime = System.nanoTime();
            tetrisSystem.rotateClockwise();
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000;
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("rotateCounterClockwise() 응답 시간 테스트 - 평균 100ms 이하")
    void testRotateCounterClockwiseResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime = System.nanoTime();
            tetrisSystem.rotateCounterClockwise();
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000;
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("hardDrop() 응답 시간 테스트 - 평균 100ms 이하")
    void testHardDropResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime = System.nanoTime();
            tetrisSystem.hardDrop();
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000;
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @RepeatedTest(10)
    @DisplayName("복합 입력 시퀀스 응답 시간 테스트")
    void testComplexInputSequenceResponseTime() {
        tetrisSystem.reset();
        
        List<Long> responseTimes = new ArrayList<>();
        
        // 실제 게임 플레이와 유사한 입력 시퀀스
        long startTime, endTime;
        
        // 좌로 이동
        startTime = System.nanoTime();
        tetrisSystem.moveLeft();
        endTime = System.nanoTime();
        responseTimes.add((endTime - startTime) / 1_000_000);
        
        // 회전
        startTime = System.nanoTime();
        tetrisSystem.rotateClockwise();
        endTime = System.nanoTime();
        responseTimes.add((endTime - startTime) / 1_000_000);
        
        // 우로 이동
        startTime = System.nanoTime();
        tetrisSystem.moveRight();
        endTime = System.nanoTime();
        responseTimes.add((endTime - startTime) / 1_000_000);
        
        // 소프트 드롭
        startTime = System.nanoTime();
        tetrisSystem.moveDown();
        endTime = System.nanoTime();
        responseTimes.add((endTime - startTime) / 1_000_000);
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("복합 입력의 평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("SuperRotationSystem 직접 호출 응답 시간 테스트")
    void testSuperRotationSystemResponseTime() {
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            GameBoard board = new GameBoard();
            TetrominoPosition position = new TetrominoPosition(Tetromino.T, 4, 0, 0);
            
            long startTime = System.nanoTime();
            SuperRotationSystem.attemptRotation(position, board, true);
            long endTime = System.nanoTime();
            
            long responseTimeMs = (endTime - startTime) / 1_000_000;
            responseTimes.add(responseTimeMs);
        }
        
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        assertTrue(averageTime < MAX_RESPONSE_TIME_MS, 
            String.format("SRS 회전 평균 응답 시간(%.3f ms)이 요구사항(%d ms)을 초과했습니다.", averageTime, MAX_RESPONSE_TIME_MS));
    }
    
    @Test
    @DisplayName("전체 입력 작업의 99 퍼센타일 응답 시간 확인")
    void testNinetyNinthPercentileResponseTime() {
        List<Long> allResponseTimes = new ArrayList<>();
        
        // 모든 입력 타입에 대해 여러 번 테스트
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            tetrisSystem.reset();
            
            long startTime, endTime;
            
            startTime = System.nanoTime();
            tetrisSystem.moveLeft();
            endTime = System.nanoTime();
            allResponseTimes.add((endTime - startTime) / 1_000_000);
            
            startTime = System.nanoTime();
            tetrisSystem.moveRight();
            endTime = System.nanoTime();
            allResponseTimes.add((endTime - startTime) / 1_000_000);
            
            startTime = System.nanoTime();
            tetrisSystem.rotateClockwise();
            endTime = System.nanoTime();
            allResponseTimes.add((endTime - startTime) / 1_000_000);
            
            startTime = System.nanoTime();
            tetrisSystem.moveDown();
            endTime = System.nanoTime();
            allResponseTimes.add((endTime - startTime) / 1_000_000);
        }
        
        allResponseTimes.sort(Long::compareTo);
        int percentile99Index = (int) Math.ceil(allResponseTimes.size() * 0.99) - 1;
        long percentile99Time = allResponseTimes.get(percentile99Index);
        
        assertTrue(percentile99Time < MAX_RESPONSE_TIME_MS * 1.5, 
            String.format("99 퍼센타일 응답 시간(%d ms)이 허용 범위(%.0f ms)를 초과했습니다.", 
                percentile99Time, MAX_RESPONSE_TIME_MS * 1.5));
    }
}
