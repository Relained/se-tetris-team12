package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.model.P2PGameResult;
import org.example.view.P2PGameOverView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

/**
 * P2P 모드의 게임 오버 화면 처리를 담당하는 Controller
 * 점수를 받아서 승패 결과와 점수를 표시, Go Waiting Room 버튼 제공
 */
public class P2PGameOverController extends BaseController {

    private static final byte SIGNAL_PLAY_AGAIN = 0x01;
    private static final byte SIGNAL_GO_WAITING_ROOM = 0x02;

    private P2PGameOverView p2pGameOverView;
    private P2PGameResult gameResult;
    private Thread receiveThread;

    /**
     * @param gameResult P2P 게임 결과 데이터
     */
    public P2PGameOverController(P2PGameResult gameResult) {
        this.p2pGameOverView = new P2PGameOverView();
        this.gameResult = gameResult;
        if (gameResult.isServer)
            receiveThread = null;
        else
            startReceiveThread();
    }

    @Override
    protected Scene createScene() {
        var root = p2pGameOverView.createView(
            gameResult.myScore,
            gameResult.opponentScore,
            gameResult.gameOverStatus,
            gameResult.isServer,
            this::handlePlayAgain,
            this::handleGoWaitingRoom,
            this::handleMainMenu,
            this::handleExit
        );
        createDefaultScene(root);
        return scene;
    }

    private void startReceiveThread() {
        receiveThread = Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(100);
                DataInputStream in = new DataInputStream(gameResult.socket.getInputStream());
                byte signal = in.readByte();
                
                switch (signal) {
                    case SIGNAL_PLAY_AGAIN -> Platform.runLater(this::handlePlayAgain);
                    case SIGNAL_GO_WAITING_ROOM -> Platform.runLater(this::handleGoWaitingRoom);
                }
                
            }
            catch (EOFException | InterruptedException e) {
                System.err.println("Receive thread terminated");
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("Receive thread interrupted - graceful shutdown");
                    return;
                }
                System.err.println("Failed to receive signal: " + e.getMessage());
            }
        });
    }

    private void sendSignal(byte signal) {
        if (gameResult.isServer) {
            try {
                DataOutputStream out = new DataOutputStream(gameResult.socket.getOutputStream());
                out.writeByte(signal);
                out.flush();
            } catch (Exception e) {
                System.err.println("Failed to send signal: " + e.getMessage());
            }
        }
    }

    public void handlePlayAgain() {
        if (gameResult.isServer) {
            sendSignal(SIGNAL_PLAY_AGAIN);
        }
        swapState(new P2PMultiPlayController(gameResult.socket, gameResult.isServer, gameResult.gameMode, gameResult.difficulty));
    }

    public void handleGoWaitingRoom() {
        if (gameResult.isServer) {
            sendSignal(SIGNAL_GO_WAITING_ROOM);
        }
        swapState(new WaitingRoomController(gameResult.socket, gameResult.isServer));
    }

    public void handleMainMenu() {
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
        try {
            gameResult.socket.close();
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