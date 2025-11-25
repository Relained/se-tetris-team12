package org.example.service;

import java.io.File;

public class TetrisUtil {

    public static String getAppDataPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;
        
        if (os.contains("win")) {
            // Windows: C:\Users\사용자명\AppData\Roaming\Tetris
            basePath = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/Tetris
            basePath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            // Linux: ~/.config/Tetris 또는 ~/.local/share/Tetris
            basePath = System.getProperty("user.home") + "/.config";
        }
        
        String appDir = basePath + File.separator + "SE12Tetris";
        
        // 디렉토리가 없으면 생성
        File dir = new File(appDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        return appDir + File.separator;
    }
}
