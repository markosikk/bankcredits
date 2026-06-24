package bankcredits.model.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDao<T> {
    List<T> findAll() throws SQLException;
    void insert(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(int id) throws SQLException;
}
