package org.example.view;

import java.util.function.Consumer;

import org.example.service.DisplayManager;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ClientConnectionView extends BaseView {

    private Text searchedUsersTitle;
    private ListView<String> searchedUsersList;
    private Button refreshButton;
    private Text connectionHistoryTitle;
    private ListView<String> connectionHistoryList;
    private Button clearHistoryButton;
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
            Runnable onGoBack,
            Runnable onClearHistory) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_LEFT);
        root.getStyleClass().add("root-dark");
        root.setPadding(new javafx.geometry.Insets(40));

        HBox ipInputSection = createIpInputSection(onIpSubmit);

        VBox spacer1 = new VBox();
        spacer1.setMinHeight(15);

        HBox searchedUsersSection = createSearchedUsersSection(onSearchedUserSelect, onRefresh);

        VBox spacer2 = new VBox();
        spacer2.setMinHeight(15);

        HBox connectionHistorySection = createConnectionHistorySection(onHistorySelect, onClearHistory);

        VBox spacer3 = new VBox();
        spacer3.setMinHeight(15);

        goBackButton = new Button("Go Back");
        goBackButton.getStyleClass().addAll("secondary-button", "text-body-medium");
        goBackButton.setPrefSize(150 * currentScale, 40 * currentScale);
        goBackButton.setOnAction(event -> onGoBack.run());

        root.getChildren().addAll(
                title,
                ipInputSection,
                spacer1,
                searchedUsersTitle,
                searchedUsersSection,
                spacer2,
                connectionHistoryTitle,
                connectionHistorySection,
                spacer3,
                goBackButton);

        return root;
    }

    private HBox createIpInputSection(Consumer<String> onIpSubmit) {
        title = new Text("Please enter server's IP address:");
        title.getStyleClass().addAll("text-primary", "text-title-medium");
        var dm = DisplayManager.getInstance();
        title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);

        double fieldWidth = dm.getWidth(dm.getCurrentSize()) * 0.5;
        ipAddressField = new TextField();
        ipAddressField.setPromptText("Enter IP address");
        ipAddressField.setPrefWidth(fieldWidth);
        ipAddressField.setMinWidth(fieldWidth);
        ipAddressField.setMaxWidth(fieldWidth);
        ipAddressField.setAlignment(Pos.CENTER);
        ipAddressField.getStyleClass().addAll("text-input", "text-body-large");
        ipAddressField.setOnAction(event -> {
            onIpSubmit.accept(ipAddressField.getText().trim());
        });

        submitButton = new Button("Connect");
        submitButton.getStyleClass().addAll("secondary-button", "text-body-medium");
        submitButton.setPrefSize(150 * currentScale, 40 * currentScale);
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
        searchedUsersTitle.getStyleClass().addAll("text-primary", "text-body-large");
        searchedUsersTitle.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        var dm = DisplayManager.getInstance();
        searchedUsersList = new ListView<>();
        searchedUsersList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.16);
        searchedUsersList.setMinWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        searchedUsersList.setPrefWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        searchedUsersList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        searchedUsersList.getStyleClass().add("list-view");
        searchedUsersList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedIp = searchedUsersList.getSelectionModel().getSelectedItem();
                if (selectedIp != null) {
                    onSearchedUserSelect.accept(selectedIp);
                }
            }
        });

        refreshButton = new Button("↻");
        refreshButton.getStyleClass().addAll("secondary-button", "text-body-large");
        refreshButton.setPrefSize(40 * currentScale, 40 * currentScale);
        refreshButton.setOnAction(event -> onRefresh.run());

        HBox searchedUsersBox = new HBox(10);
        searchedUsersBox.setAlignment(Pos.BOTTOM_LEFT);
        searchedUsersBox.getChildren().addAll(searchedUsersList, refreshButton);

        return searchedUsersBox;
    }

    private HBox createConnectionHistorySection(Consumer<String> onHistorySelect, Runnable onClearHistory) {
        connectionHistoryTitle = new Text("IP connection history");
        connectionHistoryTitle.getStyleClass().addAll("text-primary", "text-body-large");
        connectionHistoryTitle.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        var dm = DisplayManager.getInstance();
        connectionHistoryList = new ListView<>();
        connectionHistoryList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.16);
        connectionHistoryList.setMinWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        connectionHistoryList.setPrefWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        connectionHistoryList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.5);
        connectionHistoryList.getStyleClass().add("list-view");
        connectionHistoryList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedIp = connectionHistoryList.getSelectionModel().getSelectedItem();
                if (selectedIp != null) {
                    onHistorySelect.accept(selectedIp);
                }
            }
        });

        clearHistoryButton = new Button("Clear History");
        clearHistoryButton.getStyleClass().addAll("danger-button", "text-body-medium");
        clearHistoryButton.setPrefSize(150 * currentScale, 40 * currentScale);
        clearHistoryButton.setOnAction(event -> onClearHistory.run());

        HBox historyBox = new HBox(10);
        historyBox.setAlignment(Pos.BOTTOM_LEFT);
        historyBox.getChildren().addAll(connectionHistoryList, clearHistoryButton);

        return historyBox;
    }

    public void setConnectionHistoryItems(java.util.List<String> items) {
        if (connectionHistoryList == null)
            return;
        connectionHistoryList.getItems().setAll(items);
    }

    public void addSearchedUsersItems(String item) {
        if (searchedUsersList == null)
            return;
        searchedUsersList.getItems().add(item);
    }

    public void resetSearchedUsersItems() {
        if (searchedUsersList == null)
            return;
        searchedUsersList.getItems().clear();
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

    public void setRefreshButtonText(String text) {
        if (refreshButton == null)
            return;
        refreshButton.setText(text);
    }

    @Override
    protected void onScaleChanged(double scale) {
        var dm = DisplayManager.getInstance();
        
        if (searchedUsersList != null) {
            searchedUsersList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.15);
            searchedUsersList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.4);
        }
        if (refreshButton != null) {
            refreshButton.setPrefSize(150 * scale, 40 * scale);
        }
        if (submitButton != null) {
            submitButton.setPrefSize(150 * scale, 40 * scale);
        }
        if (goBackButton != null) {
            goBackButton.setPrefSize(150 * scale, 40 * scale);
        }
        if (connectionHistoryList != null) {
            connectionHistoryList.setMaxHeight(dm.getHeight(dm.getCurrentSize()) * 0.15);
            connectionHistoryList.setMaxWidth(dm.getWidth(dm.getCurrentSize()) * 0.4);
        }
        if (clearHistoryButton != null) {
            clearHistoryButton.setPrefSize(150 * scale, 40 * scale);
        }
        if (title != null) {
            title.setWrappingWidth(dm.getWidth(dm.getCurrentSize()) * 0.8);
        }
        if (ipAddressField != null) {
            double fieldWidth = dm.getWidth(dm.getCurrentSize()) * 0.3;
            ipAddressField.setPrefWidth(fieldWidth);
            ipAddressField.setMinWidth(fieldWidth);
            ipAddressField.setMaxWidth(fieldWidth);
        }
    }
}
