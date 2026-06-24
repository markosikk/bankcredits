package bankcredits.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditRepayment {
    private int repaymentId;
    private int creditId;
    private BigDecimal repaymentAmount;
    private LocalDate repaymentDate;

    public CreditRepayment() {
    }

    public CreditRepayment(int repaymentId, int creditId, BigDecimal repaymentAmount, LocalDate repaymentDate) {
        this.repaymentId = repaymentId;
        this.creditId = creditId;
        this.repaymentAmount = repaymentAmount;
        this.repaymentDate = repaymentDate;
    }

    public int getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(int repaymentId) {
        this.repaymentId = repaymentId;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public BigDecimal getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public LocalDate getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(LocalDate repaymentDate) {
        this.repaymentDate = repaymentDate;
    }
}
