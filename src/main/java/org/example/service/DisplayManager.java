package org.example.service;

import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;

/**
 * 화면 크기 및 디스플레이 설정을 관리하는 매니저 클래스
 * Singleton 패턴으로 구현되어 애플리케이션 전역에서 하나의 인스턴스만 사용
 */
public class DisplayManager {
    private static DisplayManager instance;
    private ScreenSize currentSize;
    private Stage primaryStage;

    // 각 크기별 화면 설정
    private static final int SMALL_WIDTH = 512;
    private static final int SMALL_HEIGHT = 768;
    private static final int MEDIUM_WIDTH = 576;
    private static final int MEDIUM_HEIGHT = 864;
    private static final int LARGE_WIDTH = 640;
    private static final int LARGE_HEIGHT = 960;

    private DisplayManager() {
        this.currentSize = ScreenSize.MEDIUM; // 기본값
    }

    public static DisplayManager getInstance() {
        if (instance == null) {
            instance = new DisplayManager();
        }
        return instance;
    }

    /**
     * Stage 참조를 설정합니다.
     * @param stage 메인 JavaFX Stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * 화면 크기 모드를 설정하고 즉시 적용합니다.
     * @param size 설정할 화면 크기
     */
    public void setDisplayMode(ScreenSize size) {
        this.currentSize = size;
        applyDisplayMode();
    }

    /**
     * 현재 설정된 화면 크기를 Stage에 적용합니다.
     */
    private void applyDisplayMode() {
        if (primaryStage == null) {
            return;
        }

        switch (currentSize) {
            case SMALL:
                primaryStage.setWidth(SMALL_WIDTH);
                primaryStage.setHeight(SMALL_HEIGHT);
                break;
            case MEDIUM:
                primaryStage.setWidth(MEDIUM_WIDTH);
                primaryStage.setHeight(MEDIUM_HEIGHT);
                break;
            case LARGE:
                primaryStage.setWidth(LARGE_WIDTH);
                primaryStage.setHeight(LARGE_HEIGHT);
                break;
        }
    }

    /**
     * Stage에 화면 크기를 적용합니다. (외부에서 Stage를 전달받는 버전)
     * @param stage 크기를 적용할 Stage
     * @param size 적용할 화면 크기
     */
    public void applyDisplayMode(Stage stage, ScreenSize size) {
        this.primaryStage = stage;
        this.currentSize = size;
        applyDisplayMode();
    }

    /**
     * 현재 화면 크기를 반환합니다.
     * @return 현재 설정된 화면 크기
     */
    public ScreenSize getCurrentSize() {
        return currentSize;
    }

    /**
     * 특정 크기의 너비를 반환합니다.
     * @param size 조회할 화면 크기
     * @return 해당 크기의 너비
     */
    public int getWidth(ScreenSize size) {
        switch (size) {
            case SMALL: return SMALL_WIDTH;
            case MEDIUM: return MEDIUM_WIDTH;
            case LARGE: return LARGE_WIDTH;
            default: return MEDIUM_WIDTH;
        }
    }

    /**
     * 특정 크기의 높이를 반환합니다.
     * @param size 조회할 화면 크기
     * @return 해당 크기의 높이
     */
    public int getHeight(ScreenSize size) {
        switch (size) {
            case SMALL: return SMALL_HEIGHT;
            case MEDIUM: return MEDIUM_HEIGHT;
            case LARGE: return LARGE_HEIGHT;
            default: return MEDIUM_HEIGHT;
        }
    }
}
