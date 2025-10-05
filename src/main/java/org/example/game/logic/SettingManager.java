package org.example.game.logic;

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import org.example.model.ColorModel;

public class SettingManager {
    final String fileName = "setting.ser";
    SettingData currentSettings;

    public SettingManager() {
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

    public void setColorSetting(SettingData.ColorBlindMode mode) {
        currentSettings.colorBlindMode = mode;
    }

    public void applyColorSetting() {
        ColorModel.setColorSet(currentSettings.colorBlindMode);
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