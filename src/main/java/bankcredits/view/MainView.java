package bankcredits.view;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import bankcredits.controller.MainController;
import bankcredits.controller.ScreenType;
import bankcredits.model.entity.Credit;
import bankcredits.model.entity.CreditOperationType;
import bankcredits.model.entity.CreditRepayment;
import bankcredits.model.entity.Fine;
import bankcredits.model.entity.LegalEntity;
import bankcredits.model.entity.SystemUser;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class MainView {
    private final Stage stage;
    private final MainController controller;
    private final TableView<Object> table = new TableView<>();
    private final Label titleLabel = new Label();
    private final Button addButton = new Button("Добавить");
    private final Button editButton = new Button("Изменить");
    private final Button deleteButton = new Button("Удалить");
    private ScreenType currentScreen = ScreenType.LEGAL_ENTITIES;

    public MainView(Stage stage, SystemUser user) {
        this.stage = stage;
        this.controller = new MainController(user);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label userLabel = new Label("Пользователь: " + controller.getCurrentUser());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Button profileButton = new Button("Мои данные");
        Button logoutButton = new Button("Выйти");
        HBox top = new HBox(15, titleLabel, userLabel, profileButton, logoutButton);
        top.setAlignment(Pos.CENTER_LEFT);
        root.setTop(top);

        List<ScreenType> availableScreens = List.of(ScreenType.values()).stream()
                .filter(controller::canView)
                .toList();
        ListView<ScreenType> menu = new ListView<>(FXCollections.observableArrayList(availableScreens));
        menu.setPrefWidth(230);
        menu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentScreen = newValue;
                refresh();
            }
        });
        root.setLeft(menu);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        root.setCenter(table);

        Button refreshButton = new Button("Обновить");
        HBox bottom = new HBox(10, addButton, editButton, deleteButton, refreshButton);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        bottom.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(bottom);

        addButton.setOnAction(event -> handleAdd());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
        refreshButton.setOnAction(event -> refresh());
        profileButton.setOnAction(event -> showProfileDialog());
        logoutButton.setOnAction(event -> new LoginView(stage).show());

        stage.setTitle("Учет банковских кредитов");
        stage.setScene(new Scene(root, 1100, 650));
        stage.show();
        menu.getSelectionModel().select(currentScreen);
    }

    private void refresh() {
        try {
            titleLabel.setText(currentScreen.getTitle());
            addButton.setDisable(!controller.canCreate(currentScreen));
            editButton.setDisable(!controller.canUpdate(currentScreen));
            deleteButton.setDisable(!controller.canDelete(currentScreen));
            switch (currentScreen) {
                case LEGAL_ENTITIES -> showLegalEntities();
                case CREDIT_TYPES -> showCreditTypes();
                case CREDITS -> showCredits();
                case REPAYMENTS -> showRepayments();
                case FINES -> showFines();
                case USERS -> showUsers();
            }
        } catch (Exception exception) {
            showError("Ошибка загрузки данных", exception.getMessage());
        }
    }

    private void showLegalEntities() throws SQLException {
        table.getColumns().setAll(
                column("ID", value -> ((LegalEntity) value).getLegalEntityId()),
                column("Название", value -> ((LegalEntity) value).getLegalEntityName()),
                column("Вид собственности", value -> ((LegalEntity) value).getOwnershipType()),
                column("Адрес", value -> ((LegalEntity) value).getLegalAddress()),
                column("Телефон", value -> ((LegalEntity) value).getPhoneNumber()),
                column("Контактное лицо", value -> ((LegalEntity) value).getContactPerson())
        );
        setRows(controller.findLegalEntities());
    }

    private void showCreditTypes() throws SQLException {
        table.getColumns().setAll(
                column("ID", value -> ((CreditOperationType) value).getCreditTypeId()),
                column("Название", value -> ((CreditOperationType) value).getCreditTypeName()),
                column("Условие получения", value -> ((CreditOperationType) value).getCreditConditions()),
                column("Процентная ставка", value -> ((CreditOperationType) value).getInterestRate()),
                column("Срок возврата, дней", value -> ((CreditOperationType) value).getReturnPeriodDays())
        );
        setRows(controller.findCreditTypes());
    }

    private void showCredits() throws SQLException {
        table.getColumns().setAll(
                column("ID", value -> ((Credit) value).getCreditId()),
                column("ID юрлица", value -> ((Credit) value).getLegalEntityId()),
                column("ID вида", value -> ((Credit) value).getCreditTypeId()),
                column("Сумма", value -> ((Credit) value).getAmount()),
                column("Дата выдачи", value -> ((Credit) value).getIssueDate()),
                column("Дата фактического возврата", value -> nullableDate(((Credit) value).getActualReturnDate()))
        );
        setRows(controller.findCredits());
    }

    private void showRepayments() throws SQLException {
        table.getColumns().setAll(
                column("ID", value -> ((CreditRepayment) value).getRepaymentId()),
                column("ID кредита", value -> ((CreditRepayment) value).getCreditId()),
                column("Сумма погашения", value -> ((CreditRepayment) value).getRepaymentAmount()),
                column("Дата погашения", value -> ((CreditRepayment) value).getRepaymentDate())
        );
        setRows(controller.findRepayments());
    }

    private void showFines() throws SQLException {
        table.getColumns().setAll(
                column("ID", value -> ((Fine) value).getFineId()),
                column("ID кредита", value -> ((Fine) value).getCreditId()),
                column("Сумма штрафа", value -> ((Fine) value).getFineAmount())
        );
        setRows(controller.findFines());
    }

    private void showUsers() throws SQLException {
        table.getColumns().setAll(
                column("ID", value -> ((SystemUser) value).getUserId()),
                column("Логин", value -> ((SystemUser) value).getLogin()),
                column("Роль", value -> ((SystemUser) value).getUserRole())
        );
        setRows(controller.findUsers());
    }

    private TableColumn<Object, Object> column(String title, Function<Object, Object> mapper) {
        TableColumn<Object, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(mapper.apply(data.getValue())));
        return column;
    }

    private void setRows(List<?> rows) {
        table.setItems(FXCollections.observableArrayList(rows.stream().map(item -> (Object) item).toList()));
    }

    private void handleAdd() {
        try {
            switch (currentScreen) {
                case LEGAL_ENTITIES -> showLegalEntityDialog(null).ifPresent(entity -> save(() -> controller.saveLegalEntity(entity, false)));
                case CREDIT_TYPES -> showCreditTypeDialog(null).ifPresent(type -> save(() -> controller.saveCreditType(type, false)));
                case CREDITS -> showCreditDialog(null).ifPresent(credit -> save(() -> controller.saveCredit(credit, false)));
                case REPAYMENTS -> showRepaymentDialog(null).ifPresent(repayment -> save(() -> controller.saveRepayment(repayment, false)));
                case FINES -> showFineDialog(null).ifPresent(fine -> save(() -> controller.saveFine(fine, false)));
                case USERS -> showUserDialog(null).ifPresent(result -> save(() -> controller.saveUser(result.user(), false, result.password())));
            }
        } catch (Exception exception) {
            showError("Ошибка добавления", exception.getMessage());
        }
    }

    private void handleEdit() {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Выберите запись для изменения");
            return;
        }
        try {
            switch (currentScreen) {
                case LEGAL_ENTITIES -> showLegalEntityDialog((LegalEntity) selected).ifPresent(entity -> save(() -> controller.saveLegalEntity(entity, true)));
                case CREDIT_TYPES -> showCreditTypeDialog((CreditOperationType) selected).ifPresent(type -> save(() -> controller.saveCreditType(type, true)));
                case CREDITS -> showCreditDialog((Credit) selected).ifPresent(credit -> save(() -> controller.saveCredit(credit, true)));
                case REPAYMENTS -> showRepaymentDialog((CreditRepayment) selected).ifPresent(repayment -> save(() -> controller.saveRepayment(repayment, true)));
                case FINES -> showFineDialog((Fine) selected).ifPresent(fine -> save(() -> controller.saveFine(fine, true)));
                case USERS -> showUserDialog((SystemUser) selected).ifPresent(result -> save(() -> controller.saveUser(result.user(), true, result.password())));
            }
        } catch (Exception exception) {
            showError("Ошибка изменения", exception.getMessage());
        }
    }

    private void handleDelete() {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Выберите запись для удаления");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Удаление");
        confirm.setHeaderText(null);
        confirm.setContentText("Удалить выбранную запись?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            controller.delete(currentScreen, getId(selected));
            refresh();
        } catch (Exception exception) {
            showError("Ошибка удаления", exception.getMessage());
        }
    }

    private int getId(Object selected) {
        if (selected instanceof LegalEntity entity) return entity.getLegalEntityId();
        if (selected instanceof CreditOperationType type) return type.getCreditTypeId();
        if (selected instanceof Credit credit) return credit.getCreditId();
        if (selected instanceof CreditRepayment repayment) return repayment.getRepaymentId();
        if (selected instanceof Fine fine) return fine.getFineId();
        if (selected instanceof SystemUser user) return user.getUserId();
        throw new IllegalArgumentException("Неизвестный тип записи");
    }

    private void save(SqlAction action) {
        try {
            action.execute();
            refresh();
        } catch (Exception exception) {
            showError("Ошибка сохранения", exception.getMessage());
        }
    }

    private Optional<LegalEntity> showLegalEntityDialog(LegalEntity original) {
        TextField name = field(original == null ? "" : original.getLegalEntityName());
        TextField ownership = field(original == null ? "" : original.getOwnershipType());
        TextField address = field(original == null ? "" : original.getLegalAddress());
        TextField phone = field(original == null ? "" : original.getPhoneNumber());
        TextField contact = field(original == null ? "" : original.getContactPerson());
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Название:"), name);
        grid.addRow(1, new Label("Вид собственности:"), ownership);
        grid.addRow(2, new Label("Адрес:"), address);
        grid.addRow(3, new Label("Телефон:"), phone);
        grid.addRow(4, new Label("Контактное лицо:"), contact);
        if (!showDialog(original == null ? "Добавление юрлица" : "Изменение юрлица", grid)) return Optional.empty();
        try {
            LegalEntity entity = new LegalEntity(
                    original == null ? 0 : original.getLegalEntityId(),
                    required(name, "Название"), required(ownership, "Вид собственности"),
                    required(address, "Адрес"), required(phone, "Телефон"), required(contact, "Контактное лицо")
            );
            return Optional.of(entity);
        } catch (Exception exception) {
            showError("Ошибка заполнения", exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<CreditOperationType> showCreditTypeDialog(CreditOperationType original) {
        TextField name = field(original == null ? "" : original.getCreditTypeName());
        TextField conditions = field(original == null ? "" : original.getCreditConditions());
        TextField rate = field(original == null ? "" : original.getInterestRate().toPlainString());
        TextField days = field(original == null ? "" : String.valueOf(original.getReturnPeriodDays()));
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Название:"), name);
        grid.addRow(1, new Label("Условие получения:"), conditions);
        grid.addRow(2, new Label("Процентная ставка:"), rate);
        grid.addRow(3, new Label("Срок возврата, дней:"), days);
        if (!showDialog(original == null ? "Добавление вида кредита" : "Изменение вида кредита", grid)) return Optional.empty();
        try {
            CreditOperationType type = new CreditOperationType(
                    original == null ? 0 : original.getCreditTypeId(),
                    required(name, "Название"), required(conditions, "Условие получения"),
                    positiveMoney(rate, "Процентная ставка"), positiveInt(days, "Срок возврата")
            );
            return Optional.of(type);
        } catch (Exception exception) {
            showError("Ошибка заполнения", exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Credit> showCreditDialog(Credit original) throws SQLException {
        ComboBox<LegalEntity> legalBox = new ComboBox<>(FXCollections.observableArrayList(controller.findLegalEntities()));
        ComboBox<CreditOperationType> typeBox = new ComboBox<>(FXCollections.observableArrayList(controller.findCreditTypes()));
        TextField amount = field(original == null ? "" : original.getAmount().toPlainString());
        DatePicker issueDate = new DatePicker(original == null ? LocalDate.now() : original.getIssueDate());
        DatePicker returnDate = new DatePicker(original == null ? null : original.getActualReturnDate());
        if (original != null) {
            select(legalBox, item -> item.getLegalEntityId() == original.getLegalEntityId());
            select(typeBox, item -> item.getCreditTypeId() == original.getCreditTypeId());
        }
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Юридическое лицо:"), legalBox);
        grid.addRow(1, new Label("Вид кредита:"), typeBox);
        grid.addRow(2, new Label("Сумма:"), amount);
        grid.addRow(3, new Label("Дата выдачи:"), issueDate);
        grid.addRow(4, new Label("Дата фактического возврата:"), returnDate);
        if (!showDialog(original == null ? "Добавление кредита" : "Изменение кредита", grid)) return Optional.empty();
        try {
            if (legalBox.getValue() == null || typeBox.getValue() == null || issueDate.getValue() == null) {
                throw new IllegalArgumentException("Заполните юрлицо, вид кредита и дату выдачи");
            }
            if (returnDate.getValue() != null && returnDate.getValue().isBefore(issueDate.getValue())) {
                throw new IllegalArgumentException("Дата возврата не может быть раньше даты выдачи");
            }
            Credit credit = new Credit(
                    original == null ? 0 : original.getCreditId(),
                    legalBox.getValue().getLegalEntityId(), typeBox.getValue().getCreditTypeId(),
                    positiveMoney(amount, "Сумма"), issueDate.getValue(), returnDate.getValue()
            );
            return Optional.of(credit);
        } catch (Exception exception) {
            showError("Ошибка заполнения", exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<CreditRepayment> showRepaymentDialog(CreditRepayment original) throws SQLException {
        ComboBox<Credit> creditBox = new ComboBox<>(FXCollections.observableArrayList(controller.findCredits()));
        TextField amount = field(original == null ? "" : original.getRepaymentAmount().toPlainString());
        DatePicker date = new DatePicker(original == null ? LocalDate.now() : original.getRepaymentDate());
        if (original != null) {
            select(creditBox, item -> item.getCreditId() == original.getCreditId());
        }
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Кредит:"), creditBox);
        grid.addRow(1, new Label("Сумма погашения:"), amount);
        grid.addRow(2, new Label("Дата погашения:"), date);
        if (!showDialog(original == null ? "Добавление погашения" : "Изменение погашения", grid)) return Optional.empty();
        try {
            if (creditBox.getValue() == null || date.getValue() == null) {
                throw new IllegalArgumentException("Заполните кредит и дату погашения");
            }
            CreditRepayment repayment = new CreditRepayment(
                    original == null ? 0 : original.getRepaymentId(),
                    creditBox.getValue().getCreditId(), positiveMoney(amount, "Сумма погашения"), date.getValue()
            );
            return Optional.of(repayment);
        } catch (Exception exception) {
            showError("Ошибка заполнения", exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Fine> showFineDialog(Fine original) throws SQLException {
        ComboBox<Credit> creditBox = new ComboBox<>(FXCollections.observableArrayList(controller.findCredits()));
        TextField amount = field(original == null ? "" : original.getFineAmount().toPlainString());
        if (original != null) {
            select(creditBox, item -> item.getCreditId() == original.getCreditId());
        }
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Кредит:"), creditBox);
        grid.addRow(1, new Label("Сумма штрафа:"), amount);
        if (!showDialog(original == null ? "Добавление штрафа" : "Изменение штрафа", grid)) return Optional.empty();
        try {
            if (creditBox.getValue() == null) {
                throw new IllegalArgumentException("Выберите кредит");
            }
            Fine fine = new Fine(
                    original == null ? 0 : original.getFineId(),
                    creditBox.getValue().getCreditId(), positiveMoney(amount, "Сумма штрафа")
            );
            return Optional.of(fine);
        } catch (Exception exception) {
            showError("Ошибка заполнения", exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<UserDialogResult> showUserDialog(SystemUser original) {
        TextField login = field(original == null ? "" : original.getLogin());
        PasswordField password = new PasswordField();
        password.setPromptText(original == null ? "Пароль обязателен" : "Оставьте пустым, если не менять");
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "USER"));
        role.setValue(original == null ? "USER" : original.getUserRole());
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Логин:"), login);
        grid.addRow(1, new Label("Пароль:"), password);
        grid.addRow(2, new Label("Роль:"), role);
        if (!showDialog(original == null ? "Добавление пользователя" : "Изменение пользователя", grid)) return Optional.empty();
        try {
            if (original == null && password.getText().isBlank()) {
                throw new IllegalArgumentException("Для нового пользователя нужен пароль");
            }
            SystemUser user = new SystemUser(
                    original == null ? 0 : original.getUserId(),
                    required(login, "Логин"), original == null ? null : original.getPasswordHash(), role.getValue()
            );
            return Optional.of(new UserDialogResult(user, password.getText()));
        } catch (Exception exception) {
            showError("Ошибка заполнения", exception.getMessage());
            return Optional.empty();
        }
    }

    private void showProfileDialog() {
        TextField login = field(controller.getCurrentUser().getLogin());
        PasswordField password = new PasswordField();
        password.setPromptText("Новый пароль, если нужно изменить");
        GridPane grid = formGrid();
        grid.addRow(0, new Label("Логин:"), login);
        grid.addRow(1, new Label("Новый пароль:"), password);
        if (!showDialog("Изменение регистрационных данных", grid)) return;
        try {
            controller.updateProfile(login.getText(), password.getText());
            showInfo("Данные пользователя обновлены");
        } catch (Exception exception) {
            showError("Ошибка изменения данных", exception.getMessage());
        }
    }

    private boolean showDialog(String title, GridPane content) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> button == ButtonType.OK);
        return dialog.showAndWait().orElse(false);
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        return grid;
    }

    private TextField field(String value) {
        TextField field = new TextField(value == null ? "" : value);
        field.setPrefWidth(320);
        return field;
    }

    private String required(TextField field, String name) {
        String value = field.getText() == null ? "" : field.getText().trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Поле \"" + name + "\" обязательно");
        }
        return value;
    }

    private BigDecimal positiveMoney(TextField field, String name) {
        try {
            BigDecimal value = new BigDecimal(required(field, name).replace(',', '.'));
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Поле \"" + name + "\" должно быть больше нуля");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Поле \"" + name + "\" должно быть числом");
        }
    }

    private int positiveInt(TextField field, String name) {
        try {
            int value = Integer.parseInt(required(field, name));
            if (value <= 0) {
                throw new IllegalArgumentException("Поле \"" + name + "\" должно быть больше нуля");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Поле \"" + name + "\" должно быть целым числом");
        }
    }

    private <T> void select(ComboBox<T> comboBox, Predicate<T> predicate) {
        comboBox.getItems().stream().filter(predicate).findFirst().ifPresent(comboBox::setValue);
    }

    private String nullableDate(LocalDate date) {
        return date == null ? "" : date.toString();
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

    @FunctionalInterface
    private interface SqlAction {
        void execute() throws Exception;
    }

    private record UserDialogResult(SystemUser user, String password) {
    }
}
