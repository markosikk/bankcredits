package bankcredits.model.dao;

import bankcredits.model.entity.LegalEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LegalEntityDao implements CrudDao<LegalEntity> {
    @Override
    public List<LegalEntity> findAll() throws SQLException {
        String sql = "SELECT legal_entity_id, legal_entity_name, ownership_type, legal_address, phone_number, contact_person " +
                "FROM legal_entities ORDER BY legal_entity_id";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<LegalEntity> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(map(resultSet));
            }
            return entities;
        }
    }

    @Override
    public void insert(LegalEntity entity) throws SQLException {
        String sql = "INSERT INTO legal_entities (legal_entity_name, ownership_type, legal_address, phone_number, contact_person) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, entity.getLegalEntityName());
            statement.setString(2, entity.getOwnershipType());
            statement.setString(3, entity.getLegalAddress());
            statement.setString(4, entity.getPhoneNumber());
            statement.setString(5, entity.getContactPerson());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(LegalEntity entity) throws SQLException {
        String sql = "UPDATE legal_entities SET legal_entity_name = ?, ownership_type = ?, legal_address = ?, " +
                "phone_number = ?, contact_person = ? WHERE legal_entity_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setString(1, entity.getLegalEntityName());
            statement.setString(2, entity.getOwnershipType());
            statement.setString(3, entity.getLegalAddress());
            statement.setString(4, entity.getPhoneNumber());
            statement.setString(5, entity.getContactPerson());
            statement.setInt(6, entity.getLegalEntityId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM legal_entities WHERE legal_entity_id = ?";
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private LegalEntity map(ResultSet resultSet) throws SQLException {
        return new LegalEntity(
                resultSet.getInt("legal_entity_id"),
                resultSet.getString("legal_entity_name"),
                resultSet.getString("ownership_type"),
                resultSet.getString("legal_address"),
                resultSet.getString("phone_number"),
                resultSet.getString("contact_person")
        );
    }
}
