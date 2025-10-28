package org.example.service;

import javafx.scene.input.KeyCode;
import org.example.model.ControlData;

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;

/**
 * 키 설정을 관리하는 Manager 클래스
 * SettingManager의 ControlData를 직접 참조하여 키 바인딩을 관리합니다.
 */
public class KeySettingManager {
    private static final String KEY_SETTING_FILE = "keysetting.ser";
    private SettingManager settingManager;
    private static KeySettingManager instance;

    /**
     * KeySettingManager의 싱글톤 인스턴스를 반환합니다.
     * @return KeySettingManager 인스턴스
     */
    public static KeySettingManager getInstance() {
        if (instance == null) {
            instance = new KeySettingManager();
        }
        return instance;
    }

    /**
     * KeySettingManager 생성자
     * SettingManager를 주입받기 전까지는 초기화를 보류합니다.
     */
    private KeySettingManager() {
        // SettingManager가 주입될 때까지 대기
    }

    /**
     * SettingManager를 주입합니다.
     * @param settingManager SettingManager 인스턴스
     */
    public void setSettingManager(SettingManager settingManager) {
        this.settingManager = settingManager;
        // 저장된 키 설정이 있으면 불러오기
        loadKeySettings();
    }

    /**
     * 현재 키 설정 데이터를 반환합니다.
     * @return ControlData 객체
     */
    public ControlData getControlData() {
        if (settingManager == null) {
            return new ControlData(); // 기본값 반환
        }
        return settingManager.getCurrentSettings().controlData;
    }

    /**
     * 특정 액션에 대한 키 바인딩을 설정합니다.
     * @param action 설정할 액션 이름
     * @param keyCode 설정할 키코드
     * @return 설정 성공 여부 (중복 키가 있으면 false)
     */
    public boolean setKeyBinding(String action, KeyCode keyCode) {
        if (settingManager == null) {
            return false;
        }

        ControlData controlData = getControlData();
        
        // 중복 키 체크
        if (isDuplicateKey(keyCode, action)) {
            return false;
        }

        switch (action) {
            case "moveLeft":
                controlData.moveLeft = keyCode;
                break;
            case "moveRight":
                controlData.moveRight = keyCode;
                break;
            case "softDrop":
                controlData.softDrop = keyCode;
                break;
            case "hardDrop":
                controlData.hardDrop = keyCode;
                break;
            case "rotateCounterClockwise":
                controlData.rotateCounterClockwise = keyCode;
                break;
            case "rotateClockwise":
                controlData.rotateClockwise = keyCode;
                break;
            case "hold":
                controlData.hold = keyCode;
                break;
            case "pause":
                controlData.pause = keyCode;
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * 특정 액션에 대한 현재 키 바인딩을 반환합니다.
     * @param action 조회할 액션 이름
     * @return 해당 액션에 바인딩된 KeyCode (없으면 null)
     */
    public KeyCode getKeyBinding(String action) {
        ControlData controlData = getControlData();
        
        switch (action) {
            case "moveLeft":
                return controlData.moveLeft;
            case "moveRight":
                return controlData.moveRight;
            case "softDrop":
                return controlData.softDrop;
            case "hardDrop":
                return controlData.hardDrop;
            case "rotateCounterClockwise":
                return controlData.rotateCounterClockwise;
            case "rotateClockwise":
                return controlData.rotateClockwise;
            case "hold":
                return controlData.hold;
            case "pause":
                return controlData.pause;
            default:
                return null;
        }
    }

    /**
     * 키 설정을 기본값으로 초기화합니다.
     */
    public void resetToDefault() {
        if (settingManager == null) {
            return;
        }
        settingManager.getCurrentSettings().controlData = new ControlData();
    }

    /**
     * 키 설정을 파일에 저장합니다.
     */
    public void saveKeySettings() {
        if (settingManager == null) {
            return;
        }
        
        try (var fos = new FileOutputStream(KEY_SETTING_FILE);
             var bos = new BufferedOutputStream(fos);
             var objStream = new ObjectOutputStream(bos)) {
            
            objStream.writeObject(getControlData());
            
        } catch (Exception e) {
            System.err.println("Error saving key settings: " + e.getMessage());
        }
    }

    /**
     * 키 설정을 파일에서 불러옵니다.
     * @return 불러오기 성공 여부
     */
    public boolean loadKeySettings() {
        if (settingManager == null) {
            return false;
        }
        
        try (var fis = new FileInputStream(KEY_SETTING_FILE);
             var bis = new BufferedInputStream(fis);
             var objStream = new ObjectInputStream(bis)) {

            ControlData loadedData = (ControlData) objStream.readObject();
            settingManager.getCurrentSettings().controlData = loadedData;
            return true;

        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            System.err.println("Error loading key settings: " + e.getMessage());
            return false;
        }
    }

    /**
     * 해당 키코드가 이미 다른 액션에 사용되고 있는지 확인합니다.
     * @param keyCode 확인할 키코드
     * @param excludeAction 제외할 액션 (현재 설정하려는 액션)
     * @return 중복 여부
     */
    private boolean isDuplicateKey(KeyCode keyCode, String excludeAction) {
        ControlData controlData = getControlData();
        
        if (!excludeAction.equals("moveLeft") && controlData.moveLeft == keyCode) {
            return true;
        }
        if (!excludeAction.equals("moveRight") && controlData.moveRight == keyCode) {
            return true;
        }
        if (!excludeAction.equals("softDrop") && controlData.softDrop == keyCode) {
            return true;
        }
        if (!excludeAction.equals("hardDrop") && controlData.hardDrop == keyCode) {
            return true;
        }
        if (!excludeAction.equals("rotateCounterClockwise") && controlData.rotateCounterClockwise == keyCode) {
            return true;
        }
        if (!excludeAction.equals("rotateClockwise") && controlData.rotateClockwise == keyCode) {
            return true;
        }
        if (!excludeAction.equals("hold") && controlData.hold == keyCode) {
            return true;
        }
        if (!excludeAction.equals("pause") && controlData.pause == keyCode) {
            return true;
        }
        return false;
    }

    /**
     * 모든 액션 이름 목록을 반환합니다.
     * @return 액션 이름 배열
     */
    public String[] getAllActions() {
        return new String[] {
            "moveLeft",
            "moveRight",
            "softDrop",
            "hardDrop",
            "rotateCounterClockwise",
            "rotateClockwise",
            "hold",
            "pause"
        };
    }

    /**
     * 액션 이름을 사용자 친화적인 표시 이름으로 변환합니다.
     * @param action 액션 이름
     * @return 표시 이름
     */
    public String getActionDisplayName(String action) {
        switch (action) {
            case "moveLeft":
                return "Move Left";
            case "moveRight":
                return "Move Right";
            case "softDrop":
                return "Soft Drop";
            case "hardDrop":
                return "Hard Drop";
            case "rotateCounterClockwise":
                return "Rotate CCW";
            case "rotateClockwise":
                return "Rotate CW";
            case "hold":
                return "Hold";
            case "pause":
                return "Pause";
            default:
                return action;
        }
    }
}
