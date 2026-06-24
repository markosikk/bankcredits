package bankcredits.model.entity;

import java.math.BigDecimal;

public class CreditOperationType {
    private int creditTypeId;
    private String creditTypeName;
    private String creditConditions;
    private BigDecimal interestRate;
    private int returnPeriodDays;

    public CreditOperationType() {
    }

    public CreditOperationType(int creditTypeId, String creditTypeName, String creditConditions,
                               BigDecimal interestRate, int returnPeriodDays) {
        this.creditTypeId = creditTypeId;
        this.creditTypeName = creditTypeName;
        this.creditConditions = creditConditions;
        this.interestRate = interestRate;
        this.returnPeriodDays = returnPeriodDays;
    }

    public int getCreditTypeId() {
        return creditTypeId;
    }

    public void setCreditTypeId(int creditTypeId) {
        this.creditTypeId = creditTypeId;
    }

    public String getCreditTypeName() {
        return creditTypeName;
    }

    public void setCreditTypeName(String creditTypeName) {
        this.creditTypeName = creditTypeName;
    }

    public String getCreditConditions() {
        return creditConditions;
    }

    public void setCreditConditions(String creditConditions) {
        this.creditConditions = creditConditions;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public int getReturnPeriodDays() {
        return returnPeriodDays;
    }

    public void setReturnPeriodDays(int returnPeriodDays) {
        this.returnPeriodDays = returnPeriodDays;
    }

    @Override
    public String toString() {
        return creditTypeId + " - " + creditTypeName;
    }
}
