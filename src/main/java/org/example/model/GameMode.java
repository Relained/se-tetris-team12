package org.example.model;

/**
 * 게임 모드를 정의하는 enum
 */
public enum GameMode {
    NORMAL,  // 기본 테트리스 모드
    ITEM,     // 아이템 모드 (10줄마다 아이템 블록 생성)
    TIME_ATTACK; // 타임 어택 모드 (제한 시간 내에 최대 점수 획득)

    @Override
    public String toString() {
        return switch (this) {
            case NORMAL -> "Normal";
            case ITEM -> "Item";
            case TIME_ATTACK -> "Time Attack";
        };
    }
}
