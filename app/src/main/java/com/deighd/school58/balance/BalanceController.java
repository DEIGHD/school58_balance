package com.deighd.school58.balance;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deighd.school58.utils.Connection;
import com.deighd.school58.utils.Preferences;

import java.util.List;

public class BalanceController {
    private MutableLiveData<List<Balance>> balances = new MutableLiveData<>();
    private Connection connection;
    private Preferences preferences;
    private String cardId;
    private boolean hasError = false;
    private String errorDescription;

    public BalanceController(Context context, String cardId) {
        connection = new Connection();
        preferences = new Preferences(context);
        this.cardId = cardId;
    }

    public LiveData<List<Balance>> getBalances() {
        return balances;
    }

    public boolean update() {
        List<Balance> previousBalances = preferences.loadBalances();
        BalanceResponse balanceResponse = connection.getBalanceResponse(cardId);
        if(balanceResponse == null) {
            hasError = true;
            errorDescription = "Ошибка соединения или внутренняя ошибка";
            return false;
        }
        if(balanceResponse.hasError()) {
            hasError = balanceResponse.hasError();
            errorDescription = balanceResponse.getErrorDescription();
            return false;
        }
        List<Balance> balances = balanceResponse.getBalances();

        if(previousBalances != null) {
            if (!previousBalances.equals(balances)) {
                for (Balance previousBalance : previousBalances) {
                    for (Balance balance : balances) {
                        if (previousBalance.getName().equals(balance.getName())) {
                            balance.setPreviousResidue(previousBalance.getResidue());
                        }
                    }
                }
            }
        }

        preferences.saveBalances(balances);
        this.balances.setValue(balances);
        return true;
    }

    public List<Balance> getBalancesList() {
        return balances.getValue();
    }

    public void clearBalance() {
        preferences.clear();
    }

    public boolean hasError() {
        return hasError;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void save() {
        preferences.saveBalances(balances.getValue());
    }

    public void load() {
        balances.setValue(preferences.loadBalances());
    }
}
