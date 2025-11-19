package org.example.view;

import org.example.model.SettingData.ScreenSize;
import org.example.service.ColorManager;
import org.example.service.DisplayManager;
import org.example.service.FontManager;
import org.example.view.component.NavigableButtonSystem;

/**
 * 모든 View 클래스의 공통 기능을 제공하는 추상 클래스
 */
public abstract class BaseView {
    
    protected NavigableButtonSystem buttonSystem;
    protected static ColorManager colorManager;
    protected static FontManager fontManager;
    protected double currentScale = 1.0; // 기본값: MEDIUM
    
    public static void Initialize(ColorManager colorManager) {
        BaseView.colorManager = colorManager;
        BaseView.fontManager = FontManager.getInstance();
    }
    
    /**
     * BaseView 생성자 (NavigableButtonSystem 포함)
     * ColorManager와 NavigableButtonSystem을 초기화합니다.
     * @param useButtonSystem true인 경우 NavigableButtonSystem을 초기화
     */
    public BaseView(boolean useButtonSystem) {
        if (useButtonSystem) {
            this.buttonSystem = new NavigableButtonSystem();
        }
        
        // DisplayManager에 자동 등록
        DisplayManager.getInstance().registerView(this);
    }
    
    /**
     * NavigableButtonSystem을 반환합니다.
     * Controller에서 키보드 입력을 처리하는 데 사용됩니다.
     * @return NavigableButtonSystem 인스턴스, 없으면 null
     */
    public NavigableButtonSystem getButtonSystem() {
        return buttonSystem;
    }
    
    /**
     * 화면 크기에 따라 스케일을 업데이트합니다.
     * ViewScaleManager에 의해 자동으로 호출됩니다.
     * @param screenSize 현재 화면 크기
     */
    public void updateScale(ScreenSize screenSize) {
        switch (screenSize) {
            case SMALL:
                currentScale = 0.9;
                break;
            case MEDIUM:
                currentScale = 1.0;
                break;
            case LARGE:
                currentScale = 1.1;
                break;
        }
        
        // 버튼 시스템 스케일 업데이트
        if (buttonSystem != null) {
            buttonSystem.setScale(currentScale);
        }
        
        // 서브클래스에서 추가 레이아웃 업데이트 가능
        onScaleChanged(currentScale);
    }
    
    /**
     * 스케일이 변경되었을 때 호출됩니다.
     * 서브클래스에서 오버라이드하여 추가 레이아웃 업데이트를 구현합니다.
     * @param scale 새로운 스케일 값
     */
    protected void onScaleChanged(double scale) {
        // 기본 구현은 비어있음 - 서브클래스에서 필요시 오버라이드
    }
}
