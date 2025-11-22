package org.example.service;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 애플리케이션 전체의 폰트를 관리하는 Service
 * Singleton 패턴으로 구현되어 일관된 폰트 사용을 보장
 */
public class FontManager {
    private static FontManager instance;
    
    private static final String PRIMARY_FONT = "Arial";
    private static final String MONOSPACE_FONT = "Courier New";
    
    // Title 크기
    public static final double SIZE_TITLE_LARGE = 48.0;  // StartView Title
    public static final double SIZE_TITLE_MEDIUM = 28.0; // ScoreInputView Title
    public static final double SIZE_TITLE_SMALL = 24.0;  // ScoreboardView Title
    
    // Body 크기
    public static final double SIZE_BODY_LARGE = 20.0;    // ScoreInputView rank, ScoreNotEligibleView score
    public static final double SIZE_BODY_MEDIUM = 16.0;   // StartView subtitle, ScoreInputView score, ScoreboardView empty message
    public static final double SIZE_BODY_SMALL = 14.0;    // StartView controls, ScoreInputView guidance/input, ScoreboardView header
    public static final double SIZE_BODY_MESSAGE = 15.0;  // ScoreNotEligibleView message
    public static final double SIZE_BODY_DETAIL = 13.0;   // ScoreboardView content
    
    // Caption/Hint 크기
    public static final double SIZE_CAPTION = 12.0;       // ScoreInputView hint
    
    private FontManager() {
    }
    
    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }
    
    /**
     * Arial 폰트를 생성합니다.
     * @param size 폰트 크기
     * @return Font 객체
     */
    public Font getFont(double size) {
        return Font.font(PRIMARY_FONT, size);
    }
    
    /**
     * Arial Bold 폰트를 생성합니다.
     * @param size 폰트 크기
     * @return Font 객체
     */
    public Font getBoldFont(double size) {
        return Font.font(PRIMARY_FONT, FontWeight.BOLD, size);
    }
    
    /**
     * Courier New 폰트를 생성합니다 (고정폭 폰트).
     * @param size 폰트 크기
     * @return Font 객체
     */
    public Font getMonospaceFont(double size) {
        return Font.font(MONOSPACE_FONT, size);
    }
    
    /**
     * Courier New Bold 폰트를 생성합니다.
     * @param size 폰트 크기
     * @return Font 객체
     */
    public Font getMonospaceBoldFont(double size) {
        return Font.font(MONOSPACE_FONT, FontWeight.BOLD, size);
    }
    
    /**
     * 기본 폰트 패밀리 이름을 반환합니다.
     * @return 폰트 패밀리 이름
     */
    public String getPrimaryFontFamily() {
        return PRIMARY_FONT;
    }
    
    /**
     * 고정폭 폰트 패밀리 이름을 반환합니다.
     * @return 폰트 패밀리 이름
     */
    public String getMonospaceFontFamily() {
        return MONOSPACE_FONT;
    }
}
