package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.model.GameMode;
import org.example.view.P2PGameOverView;
import java.net.Socket;

/**
 * P2P 모드의 게임 오버 화면 처리를 담당하는 Controller
 * winnerText만 받아서 표시, Go Waiting Room 버튼 제공
 */
public class P2PGameOverController extends BaseController {

    private P2PGameOverView p2pGameOverView;
    private String winnerText;
    private Socket socket;
    private boolean isServer;
    private GameMode gameMode;
    private int difficulty;

    /**
     * @param winnerText 화면에 표시할 승자 텍스트
     * @param socket P2P 연결 소켓
     * @param isServer 서버 여부
     * @param gameMode 게임 모드
     * @param difficulty 난이도
     */
    public P2PGameOverController(String winnerText, Socket socket, boolean isServer, GameMode gameMode, int difficulty) {
        this.p2pGameOverView = new P2PGameOverView();
        this.winnerText = winnerText;
        this.socket = socket;
        this.isServer = isServer;
        this.gameMode = gameMode;
        this.difficulty = difficulty;
    }

    @Override
    protected Scene createScene() {
        var root = p2pGameOverView.createView(
            winnerText,
            this::handlePlayAgain,
            this::handleGoWaitingRoom,
            this::handleMainMenu,
            this::handleExit
        );
        createDefaultScene(root);
        return scene;
    }

    public void handlePlayAgain() {
        setState(new P2PMultiPlayController(socket, isServer, gameMode, difficulty));
    }

    public void handleGoWaitingRoom() {
        // 수정필요
        setState(new WaitingRoomController(socket, isServer));
    }

    public void handleMainMenu() {
        try {
            socket.close();
        } catch (Exception e) {}
        setState(new StartController());
    }

    public void handleExit() {
        System.exit(0);
    }
    
    @Override
    public void handleKeyInput(KeyEvent event) {
        p2pGameOverView.getButtonSystem().handleInput(event);
    }
}
