package bankcredits.model.entity;

public class SystemUser {
    private int userId;
    private String login;
    private String passwordHash;
    private String userRole;

    public SystemUser() {
    }

    public SystemUser(int userId, String login, String passwordHash, String userRole) {
        this.userId = userId;
        this.login = login;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(userRole);
    }

    @Override
    public String toString() {
        return login + " (" + userRole + ")";
    }
}
