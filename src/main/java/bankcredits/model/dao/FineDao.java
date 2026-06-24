package bankcredits.model.dao;

import bankcredits.model.entity.Fine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FineDao implements CrudDao<Fine> {
    @Override
    public List<Fine> findAll() throws SQLException {
        String sql = "SELECT fine_id, credit_id, fine_amount FROM fines ORDER BY fine_id";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Fine> fines = new ArrayList<>();
            while (resultSet.next()) {
                fines.add(map(resultSet));
            }
            return fines;
        }
    }

    @Override
    public void insert(Fine fine) throws SQLException {
        String sql = "INSERT INTO fines (credit_id, fine_amount) VALUES (?, ?)";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, fine.getCreditId());
            statement.setBigDecimal(2, fine.getFineAmount());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Fine fine) throws SQLException {
        String sql = "UPDATE fines SET credit_id = ?, fine_amount = ? WHERE fine_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, fine.getCreditId());
            statement.setBigDecimal(2, fine.getFineAmount());
            statement.setInt(3, fine.getFineId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM fines WHERE fine_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Fine map(ResultSet resultSet) throws SQLException {
        return new Fine(
                resultSet.getInt("fine_id"),
                resultSet.getInt("credit_id"),
                resultSet.getBigDecimal("fine_amount")
        );
    }
}
