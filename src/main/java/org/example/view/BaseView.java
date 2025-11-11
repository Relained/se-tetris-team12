package org.example.view;

import org.example.service.ColorManager;
import org.example.view.component.NavigableButtonSystem;

/**
 * 모든 View 클래스의 공통 기능을 제공하는 추상 클래스
 */
public abstract class BaseView {
    
    protected NavigableButtonSystem buttonSystem;
    protected static ColorManager colorManager;
    
    public static void Initialize(ColorManager colorManager) {
        BaseView.colorManager = colorManager;
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
    }
    
    /**
     * NavigableButtonSystem을 반환합니다.
     * Controller에서 키보드 입력을 처리하는 데 사용됩니다.
     * @return NavigableButtonSystem 인스턴스, 없으면 null
     */
    public NavigableButtonSystem getButtonSystem() {
        return buttonSystem;
    }
}
