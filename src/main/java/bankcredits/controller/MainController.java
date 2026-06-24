package bankcredits.controller;

import bankcredits.model.dao.CreditDao;
import bankcredits.model.dao.CreditOperationTypeDao;
import bankcredits.model.dao.CreditRepaymentDao;
import bankcredits.model.dao.FineDao;
import bankcredits.model.dao.LegalEntityDao;
import bankcredits.model.dao.SystemUserDao;
import bankcredits.model.entity.Credit;
import bankcredits.model.entity.CreditOperationType;
import bankcredits.model.entity.CreditRepayment;
import bankcredits.model.entity.Fine;
import bankcredits.model.entity.LegalEntity;
import bankcredits.model.entity.SystemUser;
import bankcredits.model.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class MainController {
    private final SystemUser currentUser;
    private final LegalEntityDao legalEntityDao = new LegalEntityDao();
    private final CreditOperationTypeDao creditOperationTypeDao = new CreditOperationTypeDao();
    private final CreditDao creditDao = new CreditDao();
    private final CreditRepaymentDao repaymentDao = new CreditRepaymentDao();
    private final FineDao fineDao = new FineDao();
    private final SystemUserDao userDao = new SystemUserDao();

    public MainController(SystemUser currentUser) {
        this.currentUser = currentUser;
    }

    public SystemUser getCurrentUser() {
        return currentUser;
    }

    public boolean canCreate(ScreenType screen) {
        if (currentUser.isAdmin()) {
            return true;
        }
        return screen == ScreenType.CREDITS || screen == ScreenType.REPAYMENTS || screen == ScreenType.FINES;
    }

    public boolean canUpdate(ScreenType screen) {
        if (currentUser.isAdmin()) {
            return true;
        }
        return screen == ScreenType.CREDITS || screen == ScreenType.REPAYMENTS || screen == ScreenType.FINES;
    }

    public boolean canDelete(ScreenType screen) {
        return currentUser.isAdmin();
    }

    public boolean canView(ScreenType screen) {
        return currentUser.isAdmin() || screen != ScreenType.USERS;
    }

    public List<LegalEntity> findLegalEntities() throws SQLException {
        return legalEntityDao.findAll();
    }

    public List<CreditOperationType> findCreditTypes() throws SQLException {
        return creditOperationTypeDao.findAll();
    }

    public List<Credit> findCredits() throws SQLException {
        return creditDao.findAll();
    }

    public List<CreditRepayment> findRepayments() throws SQLException {
        return repaymentDao.findAll();
    }

    public List<Fine> findFines() throws SQLException {
        return fineDao.findAll();
    }

    public List<SystemUser> findUsers() throws SQLException {
        requireAdmin();
        return userDao.findAll();
    }

    public void saveLegalEntity(LegalEntity entity, boolean update) throws SQLException {
        requirePermission(update ? "update" : "create", ScreenType.LEGAL_ENTITIES);
        if (update) legalEntityDao.update(entity); else legalEntityDao.insert(entity);
    }

    public void saveCreditType(CreditOperationType type, boolean update) throws SQLException {
        requirePermission(update ? "update" : "create", ScreenType.CREDIT_TYPES);
        if (update) creditOperationTypeDao.update(type); else creditOperationTypeDao.insert(type);
    }

    public void saveCredit(Credit credit, boolean update) throws SQLException {
        requirePermission(update ? "update" : "create", ScreenType.CREDITS);
        if (update) creditDao.update(credit); else creditDao.insert(credit);
    }

    public void saveRepayment(CreditRepayment repayment, boolean update) throws SQLException {
        requirePermission(update ? "update" : "create", ScreenType.REPAYMENTS);
        if (update) repaymentDao.update(repayment); else repaymentDao.insert(repayment);
    }

    public void saveFine(Fine fine, boolean update) throws SQLException {
        requirePermission(update ? "update" : "create", ScreenType.FINES);
        if (update) fineDao.update(fine); else fineDao.insert(fine);
    }

    public void saveUser(SystemUser user, boolean update, String plainPassword) throws SQLException {
        requireAdmin();
        if (update) {
            SystemUser existing = userDao.findAll().stream()
                    .filter(item -> item.getUserId() == user.getUserId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            if (plainPassword != null && !plainPassword.isBlank()) {
                if (!PasswordUtil.isStrong(plainPassword)) {
                    throw new IllegalArgumentException("Слабый пароль");
                }
                user.setPasswordHash(PasswordUtil.hash(plainPassword));
            } else {
                user.setPasswordHash(existing.getPasswordHash());
            }
            userDao.update(user);
        } else {
            if (!PasswordUtil.isStrong(plainPassword)) {
                throw new IllegalArgumentException("Слабый пароль");
            }
            user.setPasswordHash(PasswordUtil.hash(plainPassword));
            userDao.insert(user);
        }
    }

    public void delete(ScreenType screen, int id) throws SQLException {
        requirePermission("delete", screen);
        switch (screen) {
            case LEGAL_ENTITIES -> legalEntityDao.delete(id);
            case CREDIT_TYPES -> creditOperationTypeDao.delete(id);
            case CREDITS -> creditDao.delete(id);
            case REPAYMENTS -> repaymentDao.delete(id);
            case FINES -> fineDao.delete(id);
            case USERS -> {
                if (id == currentUser.getUserId()) {
                    throw new IllegalArgumentException("Нельзя удалить текущего пользователя");
                }
                userDao.delete(id);
            }
        }
    }

    public void updateProfile(String newLogin, String newPassword) throws SQLException {
        if (newLogin == null || newLogin.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        currentUser.setLogin(newLogin.trim());
        if (newPassword != null && !newPassword.isBlank()) {
            if (!PasswordUtil.isStrong(newPassword)) {
                throw new IllegalArgumentException("Пароль должен быть не короче 8 символов и содержать цифру, заглавную букву и спецсимвол");
            }
            currentUser.setPasswordHash(PasswordUtil.hash(newPassword));
        }
        userDao.update(currentUser);
    }

    private void requireAdmin() {
        if (!currentUser.isAdmin()) {
            throw new SecurityException("Операция доступна только администратору");
        }
    }

    private void requirePermission(String action, ScreenType screen) {
        boolean allowed = switch (action) {
            case "create" -> canCreate(screen);
            case "update" -> canUpdate(screen);
            case "delete" -> canDelete(screen);
            default -> false;
        };
        if (!allowed) {
            throw new SecurityException("Недостаточно прав для операции");
        }
    }
}
