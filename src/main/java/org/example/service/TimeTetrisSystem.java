package org.example.service;

/**
 * 타임 어택 모드용 테트리스 시스템
 * TetrisSystem을 상속하여 타이머 관련 기능을 추가합니다.
 * - 2분 제한 시간
 * - 일시정지 시 타이머 정지
 * - 남은 시간 조회
 */
public class TimeTetrisSystem extends TetrisSystem {
    
    private static final long DEFAULT_DURATION = 2 * 60 * 1000; // 2분 (밀리초)
    
    private final long duration;
    private long startTime;
    private long pausedRemainingTime;
    private boolean isPaused;
    private boolean timeUp;
    
    public TimeTetrisSystem() {
        this(DEFAULT_DURATION);
    }
    
    public TimeTetrisSystem(long durationMillis) {
        super();
        this.duration = durationMillis;
        this.startTime = System.currentTimeMillis();
        this.pausedRemainingTime = durationMillis;
        this.isPaused = false;
        this.timeUp = false;
    }
    
    /**
     * 타이머 일시정지
     */
    public void pauseTimer() {
        if (!isPaused) {
            pausedRemainingTime = getRemainingTime();
            isPaused = true;
        }
    }
    
    /**
     * 타이머 재개
     */
    public void resumeTimer() {
        if (isPaused) {
            startTime = System.currentTimeMillis() - (duration - pausedRemainingTime);
            isPaused = false;
        }
    }
    
    /**
     * 남은 시간 반환 (밀리초)
     */
    @Override
    public long getRemainingTime() {
        if (isPaused) {
            return pausedRemainingTime;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = duration - elapsed;
        return Math.max(0, remaining);
    }
    
    /**
     * 시간이 종료되었는지 확인
     */
    public boolean isTimeUp() {
        if (timeUp) {
            return true;
        }
        
        if (getRemainingTime() <= 0) {
            timeUp = true;
            return true;
        }
        
        return false;
    }
    
    /**
     * 게임 리셋 시 타이머도 리셋
     */
    @Override
    public void reset() {
        super.reset();
        startTime = System.currentTimeMillis();
        pausedRemainingTime = duration;
        isPaused = false;
        timeUp = false;
    }
    
    /**
     * 제한 시간 반환 (밀리초)
     */
    public long getDuration() {
        return duration;
    }
    
    /**
     * 일시정지 상태 반환
     */
    public boolean isPaused() {
        return isPaused;
    }
}
