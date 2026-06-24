package bankcredits.model.dao;

import bankcredits.model.entity.SystemUser;
import bankcredits.model.util.PasswordUtil;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {
    private static Database instance;

    private final Properties properties = new Properties();
    private Connection connection;

    private Database() {
        loadProperties();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );
        }
        return connection;
    }

    public void initialize() throws SQLException {
        createDefaultAdminIfNeeded();
    }

    private void createDefaultAdminIfNeeded() throws SQLException {
        String sql = "SELECT COUNT(*) FROM system_users";

        try (PreparedStatement statement = getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next() && resultSet.getInt(1) == 0) {
                SystemUser admin = new SystemUser();
                admin.setLogin("admin");
                admin.setPasswordHash(PasswordUtil.hash("Admin123!"));
                admin.setUserRole("ADMIN");

                new SystemUserDao().insert(admin);
            }
        }
    }

    private void loadProperties() {
        try (InputStream inputStream = Database.class.getResourceAsStream("/database.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Файл database.properties не найден");
            }

            properties.load(inputStream);
        } catch (Exception exception) {
            throw new IllegalStateException("Не удалось прочитать настройки БД", exception);
        }
    }
}