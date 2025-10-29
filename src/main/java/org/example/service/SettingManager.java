package org.example.service;

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import javafx.stage.Stage;
import org.example.model.SettingData;

public class SettingManager {
    final String fileName = "setting.ser";
    SettingData currentSettings;
    private ColorManager colorManager;
    private KeySettingManager keySettingManager;
    private DisplayManager displayManager;

    public SettingManager() {
        this.colorManager = ColorManager.getInstance();
        this.keySettingManager = KeySettingManager.getInstance();
        this.displayManager = DisplayManager.getInstance();
        
        // KeySettingManager에 자신을 주입
        this.keySettingManager.setSettingManager(this);
        
        boolean success = loadSettingData();
        if (!success) {
            currentSettings = new SettingData();
        }
        applyColorSetting();
    }

    public SettingData getCurrentSettings() {
        return currentSettings;
    }

    public void resetToDefault() {
        currentSettings = new SettingData();
    }
    
    public void resetScoreboard() {
        ScoreManager.getInstance().clearScores();
    }

    public void setColorSetting(SettingData.ColorBlindMode mode) {
        currentSettings.colorBlindMode = mode;
    }

    public void setScreenSize(SettingData.ScreenSize size) {
        currentSettings.screenSize = size;
    }

    public void applyColorSetting() {
        colorManager.setColorMode(currentSettings.colorBlindMode);
    }

    /**
     * DisplayManager를 활용하여 화면 크기를 적용합니다.
     * @param stage 크기를 적용할 Stage
     */
    public void applyScreenSize(Stage stage) {
        displayManager.applyDisplayMode(stage, currentSettings.screenSize);
    }

    /**
     * DisplayManager 인스턴스를 반환합니다.
     * @return DisplayManager 인스턴스
     */
    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public void saveSettingData() {
        try (var fos = new FileOutputStream(fileName);
             var bos = new BufferedOutputStream(fos);
             var objStream = new ObjectOutputStream(bos)) {
            
            objStream.writeObject(currentSettings);
            
        } catch (Exception e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    public boolean loadSettingData() {
        try (var fis = new FileInputStream(fileName);
             var bis = new BufferedInputStream(fis);
             var objStream = new ObjectInputStream(bis)) {

            currentSettings = (SettingData) objStream.readObject();

            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
        catch (Exception e) {
            System.err.println("Error loading settings: " + e.getMessage());
            return false;
        }
    }
}