package org.example.service;

import javafx.scene.paint.Color;
import org.example.model.SettingData.ColorBlindMode;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 색상 관리를 담당하는 싱글톤 매니저 클래스
 * CSS 파일에서 색상을 로드하여 색상 블라인드 모드에 따라 적절한 색상을 제공
 */
public class ColorManager {
    private static ColorManager instance;
    private Color[] currentColorSet;
    private ColorBlindMode currentMode;

    // CSS에서 로드한 색상 저장
    private final Map<String, Color> cssColors = new HashMap<>();

    // 테트로미노 타입 문자
    private static final char[] TETROMINO_TYPES = {'I', 'O', 'T', 'S', 'Z', 'J', 'L'};

    private ColorManager() {
        // CSS에서 색상 로드
        loadColorsFromCSS();
        // 기본 색상 세트로 초기화
        setColorMode(ColorBlindMode.Default);
    }

    /**
     * CSS 파일에서 색상 정의를 로드
     */
    private void loadColorsFromCSS() {
        try (InputStream is = getClass().getResourceAsStream("/styles/tetris.css")) {
            if (is == null) {
                System.err.println("CSS file not found, using fallback colors");
                loadFallbackColors();
                return;
            }

            String cssContent = new String(is.readAllBytes());
            Pattern pattern = Pattern.compile("\\.color-([\\w-]+)\\s*\\{\\s*-fx-fill:\\s*([#\\w]+);");
            Matcher matcher = pattern.matcher(cssContent);

            while (matcher.find()) {
                String colorKey = matcher.group(1);
                String colorValue = matcher.group(2);
                try {
                    Color color = Color.web(colorValue);
                    cssColors.put(colorKey, color);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid color value: " + colorValue + " for key: " + colorKey);
                }
            }

            System.out.println("Loaded " + cssColors.size() + " colors from CSS");
        } catch (Exception e) {
            System.err.println("Error loading CSS colors: " + e.getMessage());
            loadFallbackColors();
        }
    }

    /**
     * CSS 로드 실패 시 대체 색상 로드
     */
    private void loadFallbackColors() {
        // Default colors
        cssColors.put("default-I", Color.CYAN);
        cssColors.put("default-O", Color.YELLOW);
        cssColors.put("default-T", Color.PURPLE);
        cssColors.put("default-S", Color.LIME);
        cssColors.put("default-Z", Color.RED);
        cssColors.put("default-J", Color.BLUE);
        cssColors.put("default-L", Color.ORANGE);

        // Item colors
        cssColors.put("item-line-clear", Color.GOLD);
        cssColors.put("item-column-clear", Color.SILVER);
        cssColors.put("item-cross-clear", Color.HOTPINK);
        cssColors.put("item-weight", Color.GOLD);
        cssColors.put("item-bomb", Color.DARKRED);

        // Special colors
        cssColors.put("gray", Color.GRAY);
        cssColors.put("background", Color.BLACK);
        cssColors.put("game-background", Color.DARKSLATEGRAY);
    }
    
    /**
     * ColorManager 싱글톤 인스턴스 반환
     */
    public static ColorManager getInstance() {
        if (instance == null) {
            instance = new ColorManager();
        }
        return instance;
    }
    
    /**
     * 색상 블라인드 모드 설정
     */
    public void setColorMode(ColorBlindMode mode) {
        this.currentMode = mode;
        currentColorSet = new Color[7];

        String modePrefix = mode.toString().toLowerCase();

        // CSS에서 해당 모드의 색상 로드
        for (int i = 0; i < TETROMINO_TYPES.length; i++) {
            String colorKey = modePrefix + "-" + TETROMINO_TYPES[i];
            Color color = cssColors.get(colorKey);

            if (color == null) {
                System.err.println("Color not found for key: " + colorKey + ", using fallback");
                color = getFallbackColor(i);
            }

            currentColorSet[i] = color;
        }
    }

    /**
     * 대체 색상 반환 (CSS 로드 실패 시)
     */
    private Color getFallbackColor(int index) {
        return switch (index) {
            case 0 -> Color.CYAN;
            case 1 -> Color.YELLOW;
            case 2 -> Color.PURPLE;
            case 3 -> Color.LIME;
            case 4 -> Color.RED;
            case 5 -> Color.BLUE;
            case 6 -> Color.ORANGE;
            default -> Color.WHITE;
        };
    }
    
    /**
     * 현재 색상 블라인드 모드 반환
     */
    public ColorBlindMode getCurrentMode() {
        return currentMode;
    }
    
    /**
     * 인덱스로 색상 가져오기
     * @param index 1부터 7까지의 테트로미노 타입 인덱스, 8은 회색(AdderBoard용), 또는 아이템 char 값
     * @return 해당 인덱스의 색상
     */
    public Color getColorFromIndex(int index) {
        // 아이템 블록 색상 (char 값)
        if (index == 'L') {
            return cssColors.getOrDefault("item-line-clear", Color.GOLD);
        }
        if (index == 'I') {
            return cssColors.getOrDefault("item-column-clear", Color.SILVER);
        }
        if (index == 'X') {
            return cssColors.getOrDefault("item-cross-clear", Color.HOTPINK);
        }
        if (index == 'W') {
            return cssColors.getOrDefault("item-weight", Color.BROWN);
        }
        if (index == 'B') {
            return cssColors.getOrDefault("item-bomb", Color.DARKRED);
        }

        // 8번 인덱스는 AdderBoard용 회색
        if (index == 8) {
            return cssColors.getOrDefault("gray", Color.GRAY);
        }

        if (index < 1 || index > currentColorSet.length) {
            throw new IllegalArgumentException("Invalid color index: " + index);
        }
        return currentColorSet[index - 1];
    }
    
    /**
     * 현재 색상 세트 전체 반환
     */
    public Color[] getCurrentColorSet() {
        return currentColorSet.clone();
    }
    
    /**
     * Canvas용 배경 색상 반환
     * (CSS로 스타일링할 수 없는 Canvas에서만 사용)
     */
    public Color getCanvasBackgroundColor() {
        return cssColors.getOrDefault("background", Color.BLACK);
    }
}
