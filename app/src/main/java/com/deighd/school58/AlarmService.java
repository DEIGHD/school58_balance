package com.deighd.school58;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.widget.Toast;


import com.deighd.school58.balance.Balance;
import com.deighd.school58.balance.BalanceController;
import com.deighd.school58.utils.Notifications;
import com.deighd.school58.utils.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlarmService extends BroadcastReceiver {
    private Integer notificationCounter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Preferences preferences = new Preferences(context);
        String cardId = preferences.loadCardId();
        BalanceController balanceController = new BalanceController(context, cardId);
        balanceController.update();
        if(!balanceController.hasError()) {
            balanceController.save();

            /*Toast toast = Toast.makeText(
                    context,
                    "Save",
                    Toast.LENGTH_SHORT
            );
            toast.show();*/

            List<Balance> balancesList = balanceController.getBalancesList();
            for(Balance balance : balancesList) {
                if(!balance.getResidue().equals(balance.getPreviousResidue())) {
                    String residueStatus;
                    Float residueLastChange;
                    if(balance.getResidue() > balance.getPreviousResidue()) {
                        residueStatus = context.getString(R.string.residue_status_enrolled);
                        residueLastChange = balance.getResidue() - balance.getPreviousResidue();
                    } else {
                        residueStatus = context.getString(R.string.residue_status_spent);
                        residueLastChange = balance.getPreviousResidue() - balance.getResidue();
                    }

                    String message = context.getString(
                            R.string.notification_message,
                            residueStatus,
                            residueLastChange.toString(),
                            balance.getCurrency(),
                            balance.getResidueWithCurrency()
                    );
                    notificationCounter = Notifications.createNotification(
                            context,
                            message,
                            balance.getName(),
                            notificationCounter
                    );
                }
            }
        } /*else {
            Toast toast = Toast.makeText(
                    context,
                    "Error: " + balanceController.getErrorDescription(),
                    Toast.LENGTH_SHORT
            );
            toast.show();
        }*/

        Intent intent1 = new Intent(context, AlarmService.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public static void restartAlarmService(@NotNull Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public static void stopAlarmService(@NotNull Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(pendingIntent);
    }
}