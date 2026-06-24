package bankcredits.model.dao;

import bankcredits.model.entity.CreditOperationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreditOperationTypeDao implements CrudDao<CreditOperationType> {
    @Override
    public List<CreditOperationType> findAll() throws SQLException {
        String sql = "SELECT credit_type_id, credit_type_name, credit_conditions, interest_rate, return_period_days " +
                "FROM credit_operation_types ORDER BY credit_type_id";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<CreditOperationType> types = new ArrayList<>();
            while (resultSet.next()) {
                types.add(map(resultSet));
            }
            return types;
        }
    }

    @Override
    public void insert(CreditOperationType type) throws SQLException {
        String sql = "INSERT INTO credit_operation_types (credit_type_name, credit_conditions, interest_rate, return_period_days) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, type.getCreditTypeName());
            statement.setString(2, type.getCreditConditions());
            statement.setBigDecimal(3, type.getInterestRate());
            statement.setInt(4, type.getReturnPeriodDays());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(CreditOperationType type) throws SQLException {
        String sql = "UPDATE credit_operation_types SET credit_type_name = ?, credit_conditions = ?, " +
                "interest_rate = ?, return_period_days = ? WHERE credit_type_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, type.getCreditTypeName());
            statement.setString(2, type.getCreditConditions());
            statement.setBigDecimal(3, type.getInterestRate());
            statement.setInt(4, type.getReturnPeriodDays());
            statement.setInt(5, type.getCreditTypeId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM credit_operation_types WHERE credit_type_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private CreditOperationType map(ResultSet resultSet) throws SQLException {
        return new CreditOperationType(
                resultSet.getInt("credit_type_id"),
                resultSet.getString("credit_type_name"),
                resultSet.getString("credit_conditions"),
                resultSet.getBigDecimal("interest_rate"),
                resultSet.getInt("return_period_days")
        );
    }
}
