package bankcredits.view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import bankcredits.controller.AuthController;
import bankcredits.model.entity.SystemUser;

public class LoginView {
    private final Stage stage;
    private final AuthController authController = new AuthController();

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label title = new Label("Учет банковских кредитов");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField loginField = new TextField();
        loginField.setPromptText("Логин");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Логин:"), loginField);
        form.addRow(1, new Label("Пароль:"), passwordField);

        Button loginButton = new Button("Войти");
        Button registerButton = new Button("Регистрация");
        HBox buttons = new HBox(10, loginButton, registerButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label hint = new Label("Первый вход: admin / Admin123!");
        VBox root = new VBox(15, title, form, buttons, hint);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        loginButton.setOnAction(event -> login(loginField.getText(), passwordField.getText()));
        registerButton.setOnAction(event -> showRegisterDialog());

        stage.setTitle("Вход в систему");
        stage.setScene(new Scene(root, 430, 260));
        stage.show();
    }

    private void login(String login, String password) {
        try {
            SystemUser user = authController.login(login, password);
            new MainView(stage, user).show();
        } catch (Exception exception) {
            showError("Ошибка входа", exception.getMessage());
        }
    }

    private void showRegisterDialog() {
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField repeatPasswordField = new PasswordField();

        Label passwordHint = new Label("Пароль: минимум 8 символов, цифра, заглавная буква и спецсимвол.");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #b00020;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.addRow(0, new Label("Логин:"), loginField);
        grid.addRow(1, new Label("Пароль:"), passwordField);
        grid.addRow(2, new Label("Повтор пароля:"), repeatPasswordField);

        VBox content = new VBox(8, grid, passwordHint, errorLabel);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Регистрация");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button registerButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        registerButton.setText("Зарегистрироваться");
        registerButton.addEventFilter(ActionEvent.ACTION, event -> {
            errorLabel.setText("");
            try {
                if (!passwordField.getText().equals(repeatPasswordField.getText())) {
                    throw new IllegalArgumentException("Пароли не совпадают");
                }
                authController.register(loginField.getText(), passwordField.getText());
                showInfo("Пользователь зарегистрирован. Теперь можно войти.");
            } catch (Exception exception) {
                errorLabel.setText(exception.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Сообщение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
