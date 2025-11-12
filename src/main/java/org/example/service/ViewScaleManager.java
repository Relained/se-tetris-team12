package org.example.service;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.view.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * View의 화면 크기 스케일링을 관리하는 Service
 * Singleton 패턴으로 구현되어 애플리케이션 전역에서 하나의 인스턴스만 사용
 */
public class ViewScaleManager {
    private static ViewScaleManager instance;
    private List<BaseView> registeredViews;

    private ViewScaleManager() {
        this.registeredViews = new ArrayList<>();
    }

    public static ViewScaleManager getInstance() {
        if (instance == null) {
            instance = new ViewScaleManager();
        }
        return instance;
    }

    /**
     * Stage를 설정합니다.
     * @param stage 메인 JavaFX Stage
     */
    public void initialize(Stage stage) {
        // Stage 참조는 필요 없음 (DisplayManager가 관리)
    }

    /**
     * Scene이 변경될 때 호출되어 현재 Scene의 모든 View를 업데이트합니다.
     * @param scene 새로운 Scene
     */
    public void onSceneChanged(Scene scene) {
        // Scene 변경 시 View 목록은 유지 (stack에 있는 Scene들도 관리)
        // 초기 스케일 적용
        updateAllViews();
    }

    /**
     * View를 등록하여 화면 크기 변경 시 자동으로 업데이트되도록 합니다.
     * @param view 등록할 View
     */
    public void registerView(BaseView view) {
        if (view != null && !registeredViews.contains(view)) {
            registeredViews.add(view);
            // 등록 즉시 현재 스케일 적용
            updateView(view);
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
     * 화면 크기 변경 시 호출됩니다.
     */
    public void updateAllViews() {
        ScreenSize currentSize = DisplayManager.getInstance().getCurrentSize();
        for (BaseView view : new ArrayList<>(registeredViews)) {
            view.updateScale(currentSize);
        }
    }

    /**
     * 특정 View의 스케일을 업데이트합니다.
     * @param view 업데이트할 View
     */
    private void updateView(BaseView view) {
        if (view != null) {
            ScreenSize currentSize = DisplayManager.getInstance().getCurrentSize();
            view.updateScale(currentSize);
        }
    }

    /**
     * 모든 등록된 View를 제거합니다.
     * 주로 테스트나 초기화 시 사용됩니다.
     */
    public void clearAllViews() {
        registeredViews.clear();
    }
}
