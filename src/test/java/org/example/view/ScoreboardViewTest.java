package org.example.view;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import org.example.model.ScoreRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardViewTest extends ApplicationTest {
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @Test
    @DisplayName("ScoreboardView 생성자 - 단일 파라미터")
    void testConstructorSingleParam() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(true);
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("ScoreboardView 생성자 - 두 파라미터")
    void testConstructorTwoParams() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(true, false);
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("ScoreboardView 생성자 - 두 파라미터 (게임 플레이 후)")
    void testConstructorTwoParamsAfterGamePlay() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(true, true);
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 게임 플레이 전 (Clear Scores 버튼 포함)")
    void testCreateViewBeforeGamePlay() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false, false);
            boolean[] backCalled = {false};
            boolean[] clearCalled = {false};
            
            BorderPane root = view.createView(
                () -> backCalled[0] = true,
                () -> clearCalled[0] = true
            );
            
            assertNotNull(root);
            assertFalse(backCalled[0]);
            assertFalse(clearCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - 게임 플레이 후 (Clear Scores 버튼 없음)")
    void testCreateViewAfterGamePlay() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false, true);
            boolean[] backCalled = {false};
            
            BorderPane root = view.createView(
                () -> backCalled[0] = true,
                null // 게임 플레이 후에는 Clear Scores가 null일 수 있음
            );
            
            assertNotNull(root);
            assertFalse(backCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - onClearScores null일 때 (게임 플레이 전)")
    void testCreateViewWithNullClearScores() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false, false);
            
            // onClearScores가 null이고 afterGamePlay가 false일 때 분기 커버
            BorderPane root = view.createView(() -> {}, null);
            
            assertNotNull(root);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 빈 리스트")
    void testUpdateScoreboardEmpty() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> emptyList = new ArrayList<>();
            assertDoesNotThrow(() -> view.updateScoreboard(emptyList));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 단일 기록")
    void testUpdateScoreboardSingleRecord() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
            record.setPlayerName("ABC");
            records.add(record);
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 여러 기록 (10개 이하)")
    void testUpdateScoreboardMultipleRecords() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ScoreRecord record = new ScoreRecord(1000 * (5 - i), 10 * (5 - i), i + 1, 1);
                record.setPlayerName("P" + i);
                records.add(record);
            }
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 10개 이상 기록 (최대 10개만 표시)")
    void testUpdateScoreboardMaxRecords() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                ScoreRecord record = new ScoreRecord(1000 * (15 - i), 10 * (15 - i), i + 1, 1);
                record.setPlayerName("P" + i);
                records.add(record);
            }
            
            // 15개 입력했지만 10개만 표시됨
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 새로 추가된 하이라이트 포함")
    void testUpdateScoreboardWithHighlight() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(true); // 하이라이트 활성화
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord newRecord = new ScoreRecord(10000, 100, 15, 1);
            newRecord.setPlayerName("NEW");
            newRecord.setNewlyAdded(true); // 새로 추가된 기록으로 표시
            records.add(newRecord);
            
            ScoreRecord oldRecord = new ScoreRecord(5000, 50, 10, 1);
            oldRecord.setPlayerName("OLD");
            records.add(oldRecord);
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 1등 색상 (GOLD)")
    void testUpdateScoreboardFirstPlace() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord record = new ScoreRecord(10000, 100, 15, 1);
            record.setPlayerName("1ST");
            records.add(record);
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 2등 색상 (SILVER)")
    void testUpdateScoreboardSecondPlace() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord record1 = new ScoreRecord(10000, 100, 15, 1);
            record1.setPlayerName("1ST");
            records.add(record1);
            
            ScoreRecord record2 = new ScoreRecord(8000, 80, 12, 1);
            record2.setPlayerName("2ND");
            records.add(record2);
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 3등 색상 (BRONZE)")
    void testUpdateScoreboardThirdPlace() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord record1 = new ScoreRecord(10000, 100, 15, 1);
            record1.setPlayerName("1ST");
            records.add(record1);
            
            ScoreRecord record2 = new ScoreRecord(8000, 80, 12, 1);
            record2.setPlayerName("2ND");
            records.add(record2);
            
            ScoreRecord record3 = new ScoreRecord(6000, 60, 10, 1);
            record3.setPlayerName("3RD");
            records.add(record3);
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateScoreboard - 4등 이하 색상 (WHITE)")
    void testUpdateScoreboardFourthPlaceAndBelow() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord record1 = new ScoreRecord(10000, 100, 15, 1);
            record1.setPlayerName("1ST");
            records.add(record1);
            
            ScoreRecord record2 = new ScoreRecord(8000, 80, 12, 1);
            record2.setPlayerName("2ND");
            records.add(record2);
            
            ScoreRecord record3 = new ScoreRecord(6000, 60, 10, 1);
            record3.setPlayerName("3RD");
            records.add(record3);
            
            ScoreRecord record4 = new ScoreRecord(4000, 40, 8, 1);
            record4.setPlayerName("4TH");
            records.add(record4);
            
            assertDoesNotThrow(() -> view.updateScoreboard(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("refresh 테스트")
    void testRefresh() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false);
            view.createView(() -> {}, () -> {});
            
            List<ScoreRecord> records = new ArrayList<>();
            ScoreRecord record = new ScoreRecord(5000, 50, 10, 1);
            record.setPlayerName("ABC");
            records.add(record);
            
            assertDoesNotThrow(() -> view.refresh(records));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("NavigableButtonSystem 사용 테스트 - 게임 플레이 전")
    void testNavigableButtonSystemBeforeGame() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false, false);
            BorderPane root = view.createView(() -> {}, () -> {});
            
            assertNotNull(root);
            // 2개 버튼: Back to Menu, Clear Scores
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("NavigableButtonSystem 사용 테스트 - 게임 플레이 후")
    void testNavigableButtonSystemAfterGame() {
        Platform.runLater(() -> {
            ScoreboardView view = new ScoreboardView(false, true);
            BorderPane root = view.createView(() -> {}, null);
            
            assertNotNull(root);
            // 1개 버튼만: Continue
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
