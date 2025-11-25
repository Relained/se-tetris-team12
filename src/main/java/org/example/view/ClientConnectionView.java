package org.example.view;

import java.util.function.Consumer;

import org.example.service.DisplayManager;
import org.example.service.FontManager;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ClientConnectionView extends BaseView {

    private Text searchedUsersTitle;
    private ListView<String> searchedUsersList;
    private Button refreshButton;
    private Text connectionHistoryTitle;
    private ListView<String> connectionHistoryList;
    private Text title;
    private TextField ipAddressField;
    private Button submitButton;
    private Button goBackButton;

    public ClientConnectionView() {
        super(false);
    }

    public VBox createView(
            Consumer<String> onSearchedUserSelect,
            Runnable onRefresh,
            Consumer<String> onHistorySelect,
            Consumer<String> onIpSubmit,
            Runnable onGoBack) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_LEFT);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)));
        root.setPadding(new javafx.geometry.Insets(40));

        HBox ipInputSection = createIpInputSection(onIpSubmit);

        VBox spacer1 = new VBox();
        spacer1.setMinHeight(15);

        HBox searchedUsersSection = createSearchedUsersSection(onSearchedUserSelect, onRefresh);

        VBox spacer2 = new VBox();
        spacer2.setMinHeight(15);

        createConnectionHistorySection(onHistorySelect);

        VBox spacer3 = new VBox();
        spacer3.setMinHeight(15);

        goBackButton = new Button("Go Back");
        goBackButton.setPrefSize(150 * currentScale, 40 * currentScale);
        goBackButton.setStyle(String.format(
                "-fx-font-size: %dpx; -fx-background-color: #4a4a4a; -fx-text-fill: white;",
                (int) (16 * currentScale)));
        goBackButton.setOnAction(event -> onGoBack.run());

        root.getChildren().addAll(
                title,
                ipInputSection,
                spacer1,
                searchedUsersTitle,
                searchedUsersSection,
                spacer2,
                connectionHistoryTitle,
                connectionHistoryList,
                spacer3,
                goBackButton);

        return root;
    }

    private HBox createIpInputSection(Consumer<String> onIpSubmit) {
        title = new Text("Please enter server's IP address:");
        title.setFill(colorManager.getPrimaryTextColor());
        title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_MEDIUM * currentScale));
        var dm = DisplayManager.getInstance();
        title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);

        double fieldWidth = dm.getWidth(dm.getCurrentSize()) * 0.5;
        ipAddressField = new TextField();
        ipAddressField.setPromptText("Enter IP address");
        ipAddressField.setPrefWidth(fieldWidth);
        ipAddressField.setMinWidth(fieldWidth);
        ipAddressField.setMaxWidth(fieldWidth);
        ipAddressField.setAlignment(Pos.CENTER);
        ipAddressField.setFont(fontManager.getMonospaceBoldFont(FontManager.SIZE_BODY_LARGE * currentScale));
        ipAddressField.setStyle("-fx-padding: 10px;");
        ipAddressField.setOnAction(event -> {
            onIpSubmit.accept(ipAddressField.getText().trim());
        });

        submitButton = new Button("Connect");
        submitButton.setPrefSize(150 * currentScale, 40 * currentScale);
        submitButton.setStyle(String.format(
                "-fx-font-size: %dpx; -fx-background-color: #4a4a4a; -fx-text-fill: white;",
                (int) (16 * currentScale)));
        submitButton.setOnAction(event -> {
            onIpSubmit.accept(ipAddressField.getText().trim());
        });

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.getChildren().addAll(ipAddressField, submitButton);

        return inputBox;
    }

    private HBox createSearchedUsersSection(Consumer<String> onSearchedUserSelect, Runnable onRefresh) {
        searchedUsersTitle = new Text("Discovered users on local network");
        searchedUsersTitle.setFill(colorManager.getPrimaryTextColor());
        searchedUsersTitle.setFont(
                fontManager.getFont(FontManager.SIZE_BODY_LARGE * currentScale));
        searchedUsersTitle.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        var dm = DisplayManager.getInstance();
        searchedUsersList = new ListView<>();
        searchedUsersList.getItems().addAll("192.168.0.100", "192.168.0.101", "10.0.0.50");
        searchedUsersList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.16);
        searchedUsersList.setMinWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        searchedUsersList.setPrefWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        searchedUsersList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        searchedUsersList.setStyle(String.format(
                "-fx-font-family: '%s'; -fx-font-size: %dpx; -fx-font-weight: bold; -fx-cell-size: %dpx;",
                fontManager.getMonospaceFontFamily(),
                (int) (16 * currentScale),
                (int) (40 * currentScale)));
        searchedUsersList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedIp = searchedUsersList.getSelectionModel().getSelectedItem();
                if (selectedIp != null) {
                    onSearchedUserSelect.accept(selectedIp);
                }
            }
        });

        refreshButton = new Button("↻");
        refreshButton.setPrefSize(40 * currentScale, 40 * currentScale);
        refreshButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white;");
        refreshButton.setFont(fontManager.getFont(FontManager.SIZE_BODY_LARGE * currentScale));
        refreshButton.setOnAction(event -> onRefresh.run());

        HBox searchedUsersBox = new HBox(10);
        searchedUsersBox.setAlignment(Pos.BOTTOM_LEFT);
        searchedUsersBox.getChildren().addAll(searchedUsersList, refreshButton);

        return searchedUsersBox;
    }

    private void createConnectionHistorySection(Consumer<String> onHistorySelect) {
        connectionHistoryTitle = new Text("IP connection history");
        connectionHistoryTitle.setFill(colorManager.getPrimaryTextColor());
        connectionHistoryTitle.setFont(
                fontManager.getFont(FontManager.SIZE_BODY_LARGE * currentScale));
        connectionHistoryTitle.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        var dm = DisplayManager.getInstance();
        connectionHistoryList = new ListView<>();
        connectionHistoryList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.16);
        connectionHistoryList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        connectionHistoryList.setStyle(String.format(
                "-fx-font-family: '%s'; -fx-font-size: %dpx; -fx-font-weight: bold; -fx-cell-size: %dpx;",
                fontManager.getMonospaceFontFamily(),
                (int) (16 * currentScale),
                (int) (40 * currentScale)));
        connectionHistoryList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedIp = connectionHistoryList.getSelectionModel().getSelectedItem();
                if (selectedIp != null) {
                    onHistorySelect.accept(selectedIp);
                }
            }
        });
    }

    public void setConnectionHistoryItems(java.util.List<String> items) {
        if (connectionHistoryList == null)
            return;
        connectionHistoryList.getItems().setAll(items);
    }

    public void setTitleText(String text) {
        if (title == null)
            return;
        title.setText(text);
    }

    public void setIpAddressField(String text) {
        if (ipAddressField == null)
            return;
        ipAddressField.setText(text);
    }

    @Override
    protected void onScaleChanged(double scale) {
        var dm = DisplayManager.getInstance();
        
        if (searchedUsersTitle != null) {
            searchedUsersTitle.setFont(
                    fontManager.getFont(FontManager.SIZE_BODY_LARGE * scale));
        }
        if (searchedUsersList != null) {
            searchedUsersList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.15);
            searchedUsersList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.4);
        }
        if (refreshButton != null) {
            refreshButton.setPrefSize(150 * scale, 40 * scale);
            refreshButton.setStyle(String.format(
                    "-fx-font-size: %dpx; -fx-background-color: #4a4a4a; -fx-text-fill: white;",
                    (int) (16 * scale)));
        }
        if (submitButton != null) {
            submitButton.setPrefSize(150 * scale, 40 * scale);
            submitButton.setStyle(String.format(
                    "-fx-font-size: %dpx; -fx-background-color: #4a4a4a; -fx-text-fill: white;",
                    (int) (16 * scale)));
        }
        if (goBackButton != null) {
            goBackButton.setPrefSize(150 * scale, 40 * scale);
            goBackButton.setStyle(String.format(
                    "-fx-font-size: %dpx; -fx-background-color: #4a4a4a; -fx-text-fill: white;",
                    (int) (16 * scale)));
        }
        if (connectionHistoryTitle != null) {
            connectionHistoryTitle.setFont(
                    fontManager.getFont(FontManager.SIZE_BODY_LARGE * scale));
        }
        if (connectionHistoryList != null) {
            connectionHistoryList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.15);
            connectionHistoryList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.4);
        }
        if (title != null) {
            title.setFont(fontManager.getFont(FontManager.SIZE_TITLE_LARGE * scale));
            title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);
        }
        if (ipAddressField != null) {
            double fieldWidth = dm.getWidth(dm.getCurrentSize()) * 0.3;
            ipAddressField.setPrefWidth(fieldWidth);
            ipAddressField.setMinWidth(fieldWidth);
            ipAddressField.setMaxWidth(fieldWidth);
            ipAddressField.setFont(
                    fontManager.getMonospaceBoldFont(FontManager.SIZE_BODY_LARGE * scale));
        }
    }
}
