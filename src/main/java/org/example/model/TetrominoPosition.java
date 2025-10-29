package org.example.model;

public class TetrominoPosition {
    private int x, y;
    private int rotation;
    private Tetromino type;
    private Integer itemBlockIndex; // 아이템이 부착된 블록의 인덱스 (rotation 0 기준, 0부터 시작)
    private ItemBlock itemType;     // 아이템 타입 (LINE_CLEAR, COLUMN_CLEAR, NONE)
    // 커스텀(아이템 전용) 테트로미노 지원
    private int[][] customShape;     // 회전 무시, 현재 회전에 관계없이 이 shape 사용
    private boolean rotationLocked;  // 회전 금지 여부
    private javafx.scene.paint.Color customColor; // 표시용 색상 (null이면 기본 Tetromino 색)
    private SpecialKind specialKind = SpecialKind.NONE; // 특수 동작 구분

    public TetrominoPosition(Tetromino type, int x, int y, int rotation) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.itemBlockIndex = null;
        this.itemType = ItemBlock.NONE;
        this.customShape = null;
        this.rotationLocked = false;
        this.customColor = null;
    }
    
    private TetrominoPosition(Tetromino type, int x, int y, int rotation, Integer itemBlockIndex, ItemBlock itemType) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.itemBlockIndex = itemBlockIndex;
        this.itemType = itemType;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRotation() { return rotation; }
    public Tetromino getType() { return type; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setRotation(int rotation) { 
        if (!rotationLocked) {
            this.rotation = Math.floorMod(rotation, 4);
        }
    }

    public int[][] getCurrentShape() {
        if (customShape != null) {
            return customShape;
        }
        return type.getShape(rotation);
    }

    public TetrominoPosition copy() {
        TetrominoPosition cp = new TetrominoPosition(type, x, y, rotation, itemBlockIndex, itemType);
        cp.customShape = this.customShape;
        cp.rotationLocked = this.rotationLocked;
        cp.customColor = this.customColor;
        cp.specialKind = this.specialKind;
        return cp;
    }
    
    // ===== 아이템 관련 메서드 =====
    
    /**
     * 테트로미노의 특정 블록에 아이템을 부착 (기본: LINE_CLEAR)
     * 아이템은 rotation 0 기준의 블록 인덱스로 저장되어 회전과 무관하게 동일한 물리적 블록을 추적
     */
    public void setItemAtBlockIndex(int blockIndex) {
        this.itemBlockIndex = blockIndex;
        this.itemType = ItemBlock.LINE_CLEAR;
    }
    
    /**
     * 테트로미노의 특정 블록에 특정 타입의 아이템을 부착
     * 아이템은 rotation 0 기준의 블록 인덱스로 저장되어 회전과 무관하게 동일한 물리적 블록을 추적
     * 
     * @param blockIndex 블록 인덱스 (rotation 0 기준)
     * @param itemType 아이템 타입 (LINE_CLEAR, COLUMN_CLEAR)
     */
    public void setItemAtBlockIndex(int blockIndex, ItemBlock itemType) {
        this.itemBlockIndex = blockIndex;
        this.itemType = itemType;
    }
    
    /**
     * 현재 rotation에서 특정 shape 좌표에 아이템이 있는지 확인
     * itemBlockIndex는 rotation 0 기준으로 저장되어 있으므로,
     * 현재 rotation의 블록을 rotation 0 기준으로 변환하여 비교
     */
    public ItemBlock getItemAt(int row, int col) {
        if (itemBlockIndex == null) {
            return ItemBlock.NONE;
        }
        
        // 현재 rotation의 (row, col)이 rotation 0 기준 어느 인덱스에 해당하는지 계산
        int rotation0Index = convertToRotation0Index(row, col);
        
        if (rotation0Index == itemBlockIndex) {
            return itemType;
        }
        return ItemBlock.NONE;
    }
    
    /**
     * 현재 rotation의 shape 좌표를 rotation 0 기준의 블록 인덱스로 변환
     * Tetromino의 물리적 회전 매핑 테이블을 사용하여 변환
     */
    private int convertToRotation0Index(int row, int col) {
        int[][] currentShape = getCurrentShape();
        
        // 현재 위치가 유효한 블록인지 확인
        if (row < 0 || row >= currentShape.length || 
            col < 0 || col >= currentShape[row].length ||
            currentShape[row][col] == 0) {
            return -1;
        }
        
        // 현재 rotation에서 이 위치의 블록 인덱스 계산 (왼쪽 위부터 스캔)
        int currentBlockIndex = 0;
        for (int r = 0; r < currentShape.length; r++) {
            for (int c = 0; c < currentShape[r].length; c++) {
                if (currentShape[r][c] == 1) {
                    if (r == row && c == col) {
                        // Tetromino의 매핑 테이블을 사용하여 rotation 0 기준으로 변환
                        int[][] mappings = type.getBlockIndexMappings();
                        return mappings[rotation][currentBlockIndex];
                    }
                    currentBlockIndex++;
                }
            }
        }
        return -1;
    }
    
    /**
     * 아이템을 포함하고 있는지 확인
     */
    public boolean hasItems() {
        return itemBlockIndex != null;
    }
    
    /**
     * 아이템 블록 인덱스를 반환(rotation 0 기준)
     */
    public Integer getItemBlockIndex() {
        return itemBlockIndex;
    }

    // ====== 아이템 테트로미노 생성기 ======
    public static TetrominoPosition createWeightPiece(int x, int y) {
        TetrominoPosition p = new TetrominoPosition(Tetromino.O, x, y, 0);
        // 형태: 
        //   0 0
        // 0 0 0 0
        p.customShape = new int[][] {
            {0,1,1,0},
            {1,1,1,1}
        };
        p.rotationLocked = true;
        p.customColor = javafx.scene.paint.Color.DARKGRAY;
        p.specialKind = SpecialKind.WEIGHT;
        return p;
    }

    public static TetrominoPosition createBombPiece(int x, int y) {
        TetrominoPosition p = new TetrominoPosition(Tetromino.O, x, y, 0);
        // 형태: 2x2 블록
        p.customShape = new int[][] {
            {1,1},
            {1,1}
        };
        p.rotationLocked = true;
        p.customColor = javafx.scene.paint.Color.ORANGERED;
        p.specialKind = SpecialKind.BOMB;
        return p;
    }

    public boolean isRotationLocked() { return rotationLocked; }
    public boolean hasCustomShape() { return customShape != null; }
    public SpecialKind getSpecialKind() { return specialKind; }

    public javafx.scene.paint.Color getDisplayColor(org.example.service.ColorManager cm) {
        if (customColor != null) return customColor;
        return type.getColor();
    }

    public enum SpecialKind { NONE, WEIGHT, BOMB }
}