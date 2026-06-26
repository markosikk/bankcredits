package bankcredits.model.dao;

import bankcredits.model.entity.SystemUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemUserDao implements CrudDao<SystemUser> {
    @Override
    public List<SystemUser> findAll() throws SQLException {
        String sql = "SELECT user_id, login, password_hash, role FROM system_users ORDER BY user_id";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<SystemUser> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
            return users;
        }
    }

    public SystemUser findByLogin(String login) throws SQLException {
        String sql = "SELECT user_id, login, password_hash, role FROM system_users WHERE login = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
                return null;
            }
        }
    }

    @Override
    public void insert(SystemUser user) throws SQLException {
        String sql = "INSERT INTO system_users (login, password_hash, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getUserRole());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(SystemUser user) throws SQLException {
        String sql = "UPDATE system_users SET login = ?, password_hash = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getUserRole());
            statement.setInt(4, user.getUserId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM system_users WHERE user_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private SystemUser map(ResultSet resultSet) throws SQLException {
        return new SystemUser(
                resultSet.getInt("user_id"),
                resultSet.getString("login"),
                resultSet.getString("password_hash"),
                resultSet.getString("role")
        );
    }
}
