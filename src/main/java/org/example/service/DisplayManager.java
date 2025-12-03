package org.example.service;

import javafx.scene.Parent;
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
    private boolean isMultiplayerMode; // 멀티플레이 모드 여부

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
        this.isMultiplayerMode = false;
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
     * 멀티플레이 모드인 경우 너비가 2배가 됩니다.
     */
    private void applyDisplayMode() {
        if (primaryStage == null) {
            return;
        }

        int widthMultiplier = isMultiplayerMode ? 2 : 1;

        switch (currentSize) {
            case SMALL:
                primaryStage.setWidth(SMALL_WIDTH * widthMultiplier);
                primaryStage.setHeight(SMALL_HEIGHT);
                break;
            case MEDIUM:
                primaryStage.setWidth(MEDIUM_WIDTH * widthMultiplier);
                primaryStage.setHeight(MEDIUM_HEIGHT);
                break;
            case LARGE:
                primaryStage.setWidth(LARGE_WIDTH * widthMultiplier);
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
     * 멀티플레이 모드를 설정합니다.
     * 멀티플레이 모드에서는 화면 너비가 2배가 됩니다.
     * @param enabled 멀티플레이 모드 활성화 여부
     */
    public void setMultiplayerMode(boolean enabled) {
        this.isMultiplayerMode = enabled;
        applyDisplayMode();
    }

    /**
     * 현재 멀티플레이 모드인지 반환합니다.
     * @return 멀티플레이 모드 여부
     */
    public boolean isMultiplayerMode() {
        return isMultiplayerMode;
    }

    /**
     * 특정 크기의 너비를 반환합니다.
     * 멀티플레이 모드인 경우 2배의 너비를 반환합니다.
     * @param size 조회할 화면 크기
     * @return 해당 크기의 너비
     */
    public int getWidth(ScreenSize size) {
        int baseWidth;
        switch (size) {
            case SMALL: baseWidth = SMALL_WIDTH; break;
            case MEDIUM: baseWidth = MEDIUM_WIDTH; break;
            case LARGE: baseWidth = LARGE_WIDTH; break;
            default: baseWidth = MEDIUM_WIDTH; break;
        }
        return isMultiplayerMode ? baseWidth * 2 : baseWidth;
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

    public void popView() {
        if (registeredViews.isEmpty()) {
            return;
        }
        registeredViews.remove(registeredViews.size() - 1);
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
    public void clearAllViewsExceptLatest() {
        if (registeredViews.isEmpty()) {
            return;
        }
        BaseView latestView = registeredViews.get(registeredViews.size() - 1);
        registeredViews.clear();
        registeredViews.add(latestView);
    }

    /**
     * 현재 스크린 사이즈에 맞는 CSS 클래스를 root에 적용합니다.
     * @param root Scene의 root 노드
     */
    public void applyScreenSizeClass(Parent root) {
        root.getStyleClass().removeAll("screen-small", "screen-medium", "screen-large");
        root.getStyleClass().add("screen-" + currentSize.name().toLowerCase());
    }
}
