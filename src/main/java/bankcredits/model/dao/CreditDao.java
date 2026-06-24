package bankcredits.model.dao;

import bankcredits.model.entity.Credit;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreditDao implements CrudDao<Credit> {
    @Override
    public List<Credit> findAll() throws SQLException {
        String sql = "SELECT credit_id, legal_entity_id, credit_type_id, amount, issue_date, actual_return_date " +
                "FROM credits ORDER BY credit_id";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Credit> credits = new ArrayList<>();
            while (resultSet.next()) {
                credits.add(map(resultSet));
            }
            return credits;
        }
    }

    @Override
    public void insert(Credit credit) throws SQLException {
        String sql = "INSERT INTO credits (legal_entity_id, credit_type_id, amount, issue_date, actual_return_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            fillStatement(statement, credit);
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Credit credit) throws SQLException {
        String sql = "UPDATE credits SET legal_entity_id = ?, credit_type_id = ?, amount = ?, issue_date = ?, " +
                "actual_return_date = ? WHERE credit_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            fillStatement(statement, credit);
            statement.setInt(6, credit.getCreditId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM credits WHERE credit_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private void fillStatement(PreparedStatement statement, Credit credit) throws SQLException {
        statement.setInt(1, credit.getLegalEntityId());
        statement.setInt(2, credit.getCreditTypeId());
        statement.setBigDecimal(3, credit.getAmount());
        statement.setDate(4, Date.valueOf(credit.getIssueDate()));
        if (credit.getActualReturnDate() == null) {
            statement.setDate(5, null);
        } else {
            statement.setDate(5, Date.valueOf(credit.getActualReturnDate()));
        }
    }

    private Credit map(ResultSet resultSet) throws SQLException {
        Date returnDate = resultSet.getDate("actual_return_date");
        return new Credit(
                resultSet.getInt("credit_id"),
                resultSet.getInt("legal_entity_id"),
                resultSet.getInt("credit_type_id"),
                resultSet.getBigDecimal("amount"),
                resultSet.getDate("issue_date").toLocalDate(),
                returnDate == null ? null : returnDate.toLocalDate()
        );
    }
}
