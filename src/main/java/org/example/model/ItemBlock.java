package org.example.model;

/**
 * 아이템 블록의 타입 및 아이템 모드 설정을 정의하는 enum
 */
public enum ItemBlock {
    LINE_CLEAR('L'),    // 가로줄 삭제 아이템 - 블록이 고정되면 해당 가로줄 삭제
    COLUMN_CLEAR('I'),  // 세로줄 삭제 아이템 - 블록이 고정되면 해당 세로줄 삭제
    CROSS_CLEAR('X'),   // 십자 삭제 아이템 - 블록이 고정되면 해당 가로줄과 세로줄 모두 삭제
    WEIGHT('W'),       // 무게 아이템 - 아래 방향으로 다 삭제
    BOMB('O'),          // 폭탄 아이템 - 주변 6*6 영역 삭제
    NONE(' ');          // 아이템 없음
    
    private final char symbol;
    public static final int LINES_FOR_ITEM_GENERATION = 1; // 아이템 생성 주기 (10줄마다)
    
    ItemBlock(char symbol) {
        this.symbol = symbol;
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    public boolean isItem() {
        return this != NONE;
    }
}
