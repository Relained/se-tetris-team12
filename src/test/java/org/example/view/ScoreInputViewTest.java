package org.example.view;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

class ScoreInputViewTest extends ApplicationTest {
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @Test
    @DisplayName("ScoreInputView 생성자 테스트")
    void testConstructor() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView로 UI 생성 테스트")
    void testCreateView() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            boolean[] submitCalled = {false};
            boolean[] skipCalled = {false};
            
            VBox root = view.createView(
                1, 5000, 50, 10,
                () -> submitCalled[0] = true,
                () -> skipCalled[0] = true
            );
            
            assertNotNull(root);
            assertFalse(submitCalled[0]);
            assertFalse(skipCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 다양한 순위 테스트")
    void testCreateViewVariousRanks() {
        Platform.runLater(() -> {
            ScoreInputView view1 = new ScoreInputView();
            ScoreInputView view2 = new ScoreInputView();
            ScoreInputView view3 = new ScoreInputView();
            
            VBox root1 = view1.createView(1, 10000, 100, 15, () -> {}, () -> {});
            VBox root2 = view2.createView(5, 3000, 30, 7, () -> {}, () -> {});
            VBox root3 = view3.createView(10, 1500, 15, 3, () -> {}, () -> {});
            
            assertNotNull(root1);
            assertNotNull(root2);
            assertNotNull(root3);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("nameInput 텍스트 필터링 - 3글자 제한")
    void testNameInputMaxLength() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            TextField nameInput = view.getNameInput();
            assertNotNull(nameInput);
            
            // 4글자 입력 시도 - 3글자만 남아야 함
            nameInput.setText("ABCD");
            assertEquals("ABC", nameInput.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("nameInput 텍스트 필터링 - 공백 제거")
    void testNameInputSpaceFiltering() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            TextField nameInput = view.getNameInput();
            
            // 공백 포함 텍스트 입력
            nameInput.setText("A B C");
            assertEquals("ABC", nameInput.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("nameInput 텍스트 필터링 - 공백과 길이 제한 동시 테스트")
    void testNameInputSpaceAndLengthFiltering() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            TextField nameInput = view.getNameInput();
            
            // 공백 포함 + 4글자 이상 입력
            nameInput.setText("A B C D");
            assertEquals("ABC", nameInput.getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("nameInput ENTER 키 - 비어있지 않을 때 submit 호출")
    void testNameInputEnterWithText() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            boolean[] submitCalled = {false};
            
            view.createView(1, 5000, 50, 10, () -> submitCalled[0] = true, () -> {});
            
            TextField nameInput = view.getNameInput();
            nameInput.setText("ABC");
            
            // TextField의 onAction을 직접 호출
            if (nameInput.getOnAction() != null) {
                nameInput.getOnAction().handle(null);
                assertTrue(submitCalled[0]);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("nameInput ENTER 키 - 빈 텍스트일 때 submit 호출 안됨")
    void testNameInputEnterWithEmptyText() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            boolean[] submitCalled = {false};
            
            view.createView(1, 5000, 50, 10, () -> submitCalled[0] = true, () -> {});
            
            TextField nameInput = view.getNameInput();
            nameInput.setText("");
            
            // TextField의 onAction을 직접 호출
            if (nameInput.getOnAction() != null) {
                nameInput.getOnAction().handle(null);
                assertFalse(submitCalled[0]);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getPlayerName 테스트")
    void testGetPlayerName() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            TextField nameInput = view.getNameInput();
            nameInput.setText("ABC");
            
            assertEquals("ABC", view.getPlayerName());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getPlayerName - trim 테스트")
    void testGetPlayerNameTrim() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            // 공백은 이미 필터링되어 있지만 trim 동작 확인
            assertEquals("", view.getPlayerName());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("focusNameInput 테스트")
    void testFocusNameInput() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.focusNameInput());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("focusNameInput - null nameInput 테스트")
    void testFocusNameInputWithNull() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            // createView를 호출하지 않아 nameInput이 null인 상태
            
            // null nameInput일 때 분기 커버
            assertDoesNotThrow(() -> view.focusNameInput());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getNameInput 테스트")
    void testGetNameInput() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            view.createView(1, 5000, 50, 10, () -> {}, () -> {});
            
            assertNotNull(view.getNameInput());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 0점 케이스")
    void testCreateViewWithZeroScore() {
        Platform.runLater(() -> {
            ScoreInputView view = new ScoreInputView();
            VBox root = view.createView(10, 0, 0, 1, () -> {}, () -> {});
            
            assertNotNull(root);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
