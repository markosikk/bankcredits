package bankcredits.model.dao;

import bankcredits.model.entity.CreditRepayment;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreditRepaymentDao implements CrudDao<CreditRepayment> {
    @Override
    public List<CreditRepayment> findAll() throws SQLException {
        String sql = "SELECT repayment_id, credit_id, repayment_amount, repayment_date " +
                "FROM credit_repayments ORDER BY repayment_id";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<CreditRepayment> repayments = new ArrayList<>();
            while (resultSet.next()) {
                repayments.add(map(resultSet));
            }
            return repayments;
        }
    }

    @Override
    public void insert(CreditRepayment repayment) throws SQLException {
        String sql = "INSERT INTO credit_repayments (credit_id, repayment_amount, repayment_date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, repayment.getCreditId());
            statement.setBigDecimal(2, repayment.getRepaymentAmount());
            statement.setDate(3, Date.valueOf(repayment.getRepaymentDate()));
            statement.executeUpdate();
        }
    }

    @Override
    public void update(CreditRepayment repayment) throws SQLException {
        String sql = "UPDATE credit_repayments SET credit_id = ?, repayment_amount = ?, repayment_date = ? WHERE repayment_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, repayment.getCreditId());
            statement.setBigDecimal(2, repayment.getRepaymentAmount());
            statement.setDate(3, Date.valueOf(repayment.getRepaymentDate()));
            statement.setInt(4, repayment.getRepaymentId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM credit_repayments WHERE repayment_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private CreditRepayment map(ResultSet resultSet) throws SQLException {
        return new CreditRepayment(
                resultSet.getInt("repayment_id"),
                resultSet.getInt("credit_id"),
                resultSet.getBigDecimal("repayment_amount"),
                resultSet.getDate("repayment_date").toLocalDate()
        );
    }
}
