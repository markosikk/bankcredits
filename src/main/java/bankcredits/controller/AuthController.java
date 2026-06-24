package bankcredits.controller;

import bankcredits.model.dao.SystemUserDao;
import bankcredits.model.entity.SystemUser;
import bankcredits.model.util.PasswordUtil;

import java.sql.SQLException;

public class AuthController {
    private final SystemUserDao userDao = new SystemUserDao();

    public SystemUser login(String login, String password) throws SQLException {
        SystemUser user = userDao.findByLogin(login);
        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        return user;
    }

    public void register(String login, String password) throws SQLException {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        String cleanLogin = login.trim();
        if (userDao.findByLogin(cleanLogin) != null) {
            throw new IllegalArgumentException("Пользователь с таким логином уже есть");
        }
        if (!PasswordUtil.isStrong(password)) {
            throw new IllegalArgumentException("Пароль должен быть не короче 8 символов и содержать цифру, заглавную букву и спецсимвол");
        }
        SystemUser user = new SystemUser();
        user.setLogin(cleanLogin);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setUserRole("USER");
        userDao.insert(user);
    }
}
