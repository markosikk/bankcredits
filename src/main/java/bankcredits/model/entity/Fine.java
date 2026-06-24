package bankcredits.model.entity;

import java.math.BigDecimal;

public class Fine {
    private int fineId;
    private int creditId;
    private BigDecimal fineAmount;

    public Fine() {
    }

    public Fine(int fineId, int creditId, BigDecimal fineAmount) {
        this.fineId = fineId;
        this.creditId = creditId;
        this.fineAmount = fineAmount;
    }

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }
}
