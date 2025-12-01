package org.example.model;

import java.net.Socket;

/**
 * P2P 게임 결과 데이터를 담는 클래스
 */
public class P2PGameResult {
    public final int myScore;
    public final int opponentScore;
    public final byte gameOverStatus;
    public final Socket socket;
    public final boolean isServer;
    public final GameMode gameMode;
    public final int difficulty;

    public P2PGameResult(int myScore, int opponentScore, byte gameOverStatus, Socket socket, boolean isServer, GameMode gameMode, int difficulty) {
        this.myScore = myScore;
        this.opponentScore = opponentScore;
        this.gameOverStatus = gameOverStatus;
        this.socket = socket;
        this.isServer = isServer;
        this.gameMode = gameMode;
        this.difficulty = difficulty;
    }
}
