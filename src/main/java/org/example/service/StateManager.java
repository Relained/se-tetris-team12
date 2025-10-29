package org.example.service;

import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.example.state.BaseState;

public class StateManager {
    private final Stage primaryStage;
    private final Map<String, BaseState> states;
    private BaseState currentState;
    private Stack<BaseState> stateStack;
    public SettingManager settingManager;
    private String currentPlayStateName; // 현재 플레이 중인 state 이름 추적

    public StateManager(Stage primaryStage, SettingManager settingManager) {
        this.primaryStage = primaryStage;
        this.states = new HashMap<>();
        this.stateStack = new Stack<>();
        this.settingManager = settingManager;
        this.currentPlayStateName = "play"; // 기본값
    }

    public void addState(String name, BaseState state) {
        states.put(name, state);
    }

    public void popState() {
        if (stateStack.isEmpty()) {
            return;
        }

        if (currentState != null) {
            currentState.exit();
        }
        currentState = stateStack.pop();
        currentState.resume();
        primaryStage.setScene(currentState.getScene());
    }

    public void stackState(String stateName) {
        BaseState newState = states.get(stateName);
        if (newState == null) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }
        if (currentState != null) {
            currentState.exit();
            stateStack.push(currentState);
        }

        newState.enter();
        currentState = newState;
        Scene scene = newState.createScene();
        primaryStage.setScene(scene);
    }

    public void setState(String stateName) {
        BaseState newState = states.get(stateName);
        if (newState == null) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }
        if (currentState != null) {
            currentState.exit();
        }
        stateStack.clear();

        // play 또는 playItem state로 전환되는 경우 현재 플레이 state 이름 저장
        if (stateName.equals("play") || stateName.equals("playItem")) {
            currentPlayStateName = stateName;
        }

        newState.enter(); 
        //반드시 enter를 먼저 호출하고 currentState를 변경 해야함.
        //그렇지 않으면 이전 state가 뭔지 잃어버려서 enter할 때 문제가 생김
        currentState = newState;
        Scene scene = newState.createScene();
        primaryStage.setScene(scene);
    }

    public BaseState getCurrentState() {
        return currentState;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * 현재 게임을 동일한 모드로 재시작
     */
    public void restartCurrentGame() {
        setState(currentPlayStateName);
    }
    
    /**
     * StateStack의 가장 최근 상태(pause하기 직전의 상태)를 반환
     * @return 스택의 최상단 상태, 스택이 비어있으면 null
     */
    public BaseState getPreviousState() {
        if (stateStack.isEmpty()) {
            return null;
        }
        return stateStack.peek();
    }
}