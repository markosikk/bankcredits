package bankcredits;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import bankcredits.model.dao.Database;
import bankcredits.view.LoginView;
public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Database.getInstance().initialize();
            new LoginView(stage).show();
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка запуска");
            alert.setHeaderText("Не удалось подключиться к базе данных");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
