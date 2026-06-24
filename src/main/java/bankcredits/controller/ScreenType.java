package bankcredits.controller;

public enum ScreenType {
    LEGAL_ENTITIES("Юридические лица"),
    CREDIT_TYPES("Виды кредитных операций"),
    CREDITS("Кредиты"),
    REPAYMENTS("Погашения кредитов"),
    FINES("Штрафы"),
    USERS("Пользователи системы");

    private final String title;

    ScreenType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
