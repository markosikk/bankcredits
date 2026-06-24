package bankcredits.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Credit {
    private int creditId;
    private int legalEntityId;
    private int creditTypeId;
    private BigDecimal amount;
    private LocalDate issueDate;
    private LocalDate actualReturnDate;

    public Credit() {
    }

    public Credit(int creditId, int legalEntityId, int creditTypeId, BigDecimal amount,
                  LocalDate issueDate, LocalDate actualReturnDate) {
        this.creditId = creditId;
        this.legalEntityId = legalEntityId;
        this.creditTypeId = creditTypeId;
        this.amount = amount;
        this.issueDate = issueDate;
        this.actualReturnDate = actualReturnDate;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public int getLegalEntityId() {
        return legalEntityId;
    }

    public void setLegalEntityId(int legalEntityId) {
        this.legalEntityId = legalEntityId;
    }

    public int getCreditTypeId() {
        return creditTypeId;
    }

    public void setCreditTypeId(int creditTypeId) {
        this.creditTypeId = creditTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    @Override
    public String toString() {
        return creditId + " - сумма " + amount;
    }
}
