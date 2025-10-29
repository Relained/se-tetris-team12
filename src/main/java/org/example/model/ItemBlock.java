package org.example.model;

/**
 * 아이템 블록의 타입 및 아이템 모드 설정을 정의하는 enum
 */
public enum ItemBlock {
    LINE_CLEAR('L'),    // 가로줄 삭제 아이템 - 블록이 고정되면 해당 가로줄 삭제
    COLUMN_CLEAR('I'),  // 세로줄 삭제 아이템 - 블록이 고정되면 해당 세로줄 삭제
    CROSS_CLEAR('X'),   // 십자 삭제 아이템 - 블록이 고정되면 해당 가로줄과 세로줄 모두 삭제
    NONE(' ');          // 아이템 없음
    
    /**
     * 아이템 생성 주기 (삭제된 줄 수 기준)
     * 10줄을 삭제할 때마다 새로운 아이템 블록 생성
     */
    public static final int LINES_FOR_ITEM_GENERATION = 10;
    
    private final char symbol;
    
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
