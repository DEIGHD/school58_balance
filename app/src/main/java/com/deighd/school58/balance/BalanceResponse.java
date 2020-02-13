package com.deighd.school58.balance;

import java.util.List;

public class BalanceResponse {
    private List<Balance> balances;
    private boolean hasError = false;
    private String errorDescription;

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
