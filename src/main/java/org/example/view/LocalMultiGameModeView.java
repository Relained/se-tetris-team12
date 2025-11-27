package org.example.view.DEPRECATED;

import java.util.List;

import org.example.view.BaseView;
import org.example.view.LocalMultiSetupView;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Local MultiPlay Game Mode 선택 화면의 UI를 담당하는 View 클래스
 * 
 * @deprecated Use {@link LocalMultiSetupView} instead. This view has been merged
 *             with LocalMultiDifficultyView into a unified setup flow.
 * @see LocalMultiSetupView
 */
@Deprecated(since = "1.0", forRemoval = true)
public class LocalMultiGameModeView extends BaseView {

    public LocalMultiGameModeView() {
        super(true);
    }

    public VBox createView(Runnable onNormal, Runnable onItem, Runnable onGoBack) {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        Text title = new Text("Select Local MultiPlay Mode");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(Font.font("Arial", 36));

        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Normal", "Item", "Go Back"),
            List.of(onNormal, onItem, onGoBack)
        );

        root.getChildren().add(title);
        root.getChildren().addAll(created);

        return root;
    }
}
