package org.example.controller;

import org.example.model.GameMode;

/**
 * Local MultiPlay 게임 설정 Controller
 * 
 * GameSetupController를 상속하여 Local MultiPlay 모드에서의 설정을 관리합니다.
 * 제목과 게임 시작 동작만 재정의합니다.
 */
public class LocalMultiSetupController extends GameSetupController {

    @Override
    protected String getTitle() {
        return "Local MultiPlay Setup";
    }

    /**
     * Start 버튼 클릭 시 처리
     * 선택된 게임 모드와 난이도로 Local MultiPlay 게임 시작
     */
    @Override
    public void handleStart() {
        GameMode selectedMode = view.getSelectedGameMode();
        int selectedDifficulty = view.getSelectedDifficulty();
        boolean isItemMode = selectedMode == GameMode.ITEM;
        setState(new LocalMultiPlayController(isItemMode, selectedDifficulty));
    }
}
