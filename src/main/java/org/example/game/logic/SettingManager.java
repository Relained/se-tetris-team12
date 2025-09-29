package org.example.game.logic;

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import org.example.model.Tetromino;

public class SettingManager {
    final String fileName = "C:\\Codes\\Java\\se-tetris-team12\\setting.ser";
    SettingData currentSettings;

    public SettingManager() {
        boolean success = loadSettingData();
        if (!success) {
            currentSettings = new SettingData();
        }
    }

    public SettingData getCurrentSettings() {
        return currentSettings;
    }

    public void resetToDefault() {
        currentSettings = new SettingData();
    }

    public void setColorSetting(SettingData.ColorBlindMode mode) {
        currentSettings.colorBlindMode = mode;
    }

    public void applyColorSetting() {
        //GameBoard에 있는 색상도 바꾸도록 수정해야 함 (보드 배열을 수정해야해서 의존관계 짜기가 매우 어려움)
        //GameBoard는 GameLogic에 있고, GameLogic은 PlayState에 있음... (세팅 메니저가 이걸 어케 접근하냐)

        setDefaultColors();
        switch (currentSettings.colorBlindMode) {
            case Default -> {
                
            }
            //아래 색 데이터도 제대로 된 색맹 컬러셋을 찾아서 바꿔야 함
            case PROTANOPIA -> {
                // 적색맹: 빨강-초록 구분 어려움
                Tetromino.Z.setColor(0xffa500); // 빨강 → 주황
                Tetromino.L.setColor(0xffc0cb); // 주황 → 분홍
            }
            case DEUTERANOPIA -> {
                // 녹색맹: 빨강-초록 구분 어려움 (적색맹과 동일)
                Tetromino.Z.setColor(0xffa500); // 빨강 → 주황
                Tetromino.L.setColor(0xffc0cb); // 주황 → 분홍
            }
            case TRITANOPIA -> {
                Tetromino.O.setColor(0xffffff); //???
            }
        }
    }
    
    private void setDefaultColors() {
        Tetromino.I.setColor(0x00f0f0); // Cyan
        Tetromino.O.setColor(0xffff00); // Yellow
        Tetromino.T.setColor(0xa000ff); // Purple
        Tetromino.S.setColor(0x00ff00); // Green
        Tetromino.Z.setColor(0xff0000); // Red
        Tetromino.J.setColor(0x0000ff); // Blue
        Tetromino.L.setColor(0xff8000); // Orange
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