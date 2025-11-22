package org.example.service;

import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.view.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * 화면 크기 및 디스플레이 설정을 관리하는 매니저 클래스
 * View의 스케일링도 함께 관리합니다.
 * Singleton 패턴으로 구현되어 애플리케이션 전역에서 하나의 인스턴스만 사용
 */
public class DisplayManager {
    private static DisplayManager instance;
    private ScreenSize currentSize;
    private Stage primaryStage;
    private List<BaseView> registeredViews;

    // 각 크기별 화면 설정
    private static final int SMALL_WIDTH = 512;
    private static final int SMALL_HEIGHT = 768;
    private static final int MEDIUM_WIDTH = 576;
    private static final int MEDIUM_HEIGHT = 864;
    private static final int LARGE_WIDTH = 640;
    private static final int LARGE_HEIGHT = 960;

    private DisplayManager() {
        this.currentSize = ScreenSize.MEDIUM; // 기본값
        this.registeredViews = new ArrayList<>();
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
     * 등록된 모든 View의 스케일도 함께 업데이트됩니다.
     * @param size 설정할 화면 크기
     */
    public void setDisplayMode(ScreenSize size) {
        this.currentSize = size;
        applyDisplayMode();
        updateAllViews();
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

    /**
     * View를 등록하여 화면 크기 변경 시 자동으로 업데이트되도록 합니다.
     * @param view 등록할 View
     */
    public void registerView(BaseView view) {
        if (view != null) {
            registeredViews.add(view);
            view.updateScale(currentSize);
        }
    }

    /**
     * View 등록을 해제합니다.
     * @param view 해제할 View
     */
    public void unregisterView(BaseView view) {
        registeredViews.remove(view);
    }

    /**
     * 등록된 모든 View의 스케일을 즉시 업데이트합니다.
     * 화면 크기 변경 시 자동으로 호출됩니다.
     */
    public void updateAllViews() {
        for (BaseView view : registeredViews) {
            view.updateScale(currentSize);
        }
    }

    /**
     * 모든 등록된 View를 제거합니다.
     * 주로 Scene 전환 시 사용됩니다.
     */
    public void clearAllViews() {
        registeredViews.clear();
    }
}
