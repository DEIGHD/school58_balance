package com.deighd.school58.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.deighd.school58.balance.Balance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Preferences {
    private static SharedPreferences sharedPreferences;

    public Preferences(@NotNull Context context) {
        sharedPreferences = context.getSharedPreferences("school58", Context.MODE_PRIVATE);
    }

    private class BalancesList {
        private BalancesList(List<Balance> balances) {
            list = balances;
        }
        List<Balance> list;
    }

    public void saveBalances(List<Balance> balances) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        BalancesList balancesList = new BalancesList(balances);

        editor.putString("balances", gson.toJson(balancesList)).apply();
    }

    public List<Balance> loadBalances() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        if(sharedPreferences.contains("balances")) {
            return gson.fromJson(sharedPreferences.getString("balances", ""), BalancesList.class).list;
        } else {
            return null;
        }
    }

    public void saveCardId(String cardId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("card_id", cardId).apply();
    }

    public String loadCardId() {
        if(sharedPreferences.contains("card_id")) {
            return sharedPreferences.getString("card_id", "");
        } else {
            return null;
        }
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }
}