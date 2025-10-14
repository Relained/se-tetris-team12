package org.example.view;

import org.example.service.ColorManager;
import org.example.view.component.NavigableButtonSystem;

/**
 * 모든 View 클래스의 공통 기능을 제공하는 추상 클래스
 */
public abstract class BaseView {
    
    protected NavigableButtonSystem buttonSystem;
    protected ColorManager colorManager;
    
    /**
     * BaseView 생성자
     * ColorManager 인스턴스를 초기화합니다.
     */
    public BaseView() {
        this.colorManager = ColorManager.getInstance();
    }
    
    /**
     * BaseView 생성자 (NavigableButtonSystem 포함)
     * ColorManager와 NavigableButtonSystem을 초기화합니다.
     * @param useButtonSystem true인 경우 NavigableButtonSystem을 초기화
     */
    public BaseView(boolean useButtonSystem) {
        this.colorManager = ColorManager.getInstance();
        if (useButtonSystem) {
            this.buttonSystem = new NavigableButtonSystem();
        }
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
     * ColorManager를 반환합니다.
     * @return ColorManager 인스턴스
     */
    protected ColorManager getColorManager() {
        return colorManager;
    }
    
    /**
     * 색상 설정이 변경되었을 때 호출하여 UI를 갱신합니다.
     * 필요한 경우 서브클래스에서 오버라이드하여 구현합니다.
     */
    public void refreshColors() {
        // 기본 구현: 비어있음
        // 서브클래스에서 필요시 오버라이드
    }
}
