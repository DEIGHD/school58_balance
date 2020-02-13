package com.deighd.school58;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.deighd.school58.balance.Balance;
import com.deighd.school58.balance.BalanceController;
import com.deighd.school58.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String cardId;
    private BalanceController balanceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AlarmService.stopAlarmService(this);
        setContentView(R.layout.activity_main);
        if(!hasStoragePermission()) {
            Intent intent = new Intent(MainActivity.this, CardIdActivity.class);
            startActivity(intent);
            finish();
        }

        setActionBar();

        Preferences preferences = new Preferences(this);
        cardId = preferences.loadCardId();
        if(cardId == null) {
            Intent intent = new Intent(MainActivity.this, CardIdActivity.class);
            startActivity(intent);
            finish();
        }

        balanceController = new BalanceController(this, cardId);
        Intent intent = getIntent();
        if(intent.getIntExtra("action", 0) == 1) {
            balanceController.load();
        } else {
            balanceController.update();
            if (balanceController.hasError()) {
                showAlertDialog(balanceController.getErrorDescription());
            }
        }
        setRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmService.restartAlarmService(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AlarmService.stopAlarmService(this);
        if(!hasStoragePermission()) {
            balanceController.clearBalance();
            Intent intent = new Intent(MainActivity.this, CardIdActivity.class);
            startActivity(intent);
            finish();
        }
        balanceController.load();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_devices);
        recyclerView.setItemAnimator(null);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        final RecyclerViewAdapter resultsAdapter = new RecyclerViewAdapter(new ArrayList<Balance>());
        recyclerView.setAdapter(resultsAdapter);
        balanceController.getBalances().observe(this, new Observer<List<Balance>>() {
            @Override
            public void onChanged(List<Balance> balances) {
                resultsAdapter.addItems(balances);
                resultsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setActionBar() {
        AppCompatActivity activity = (AppCompatActivity) this;
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        View customView = inflater.inflate(R.layout.actionbar_main, null);
        TextView actionBarTitle = customView.findViewById(R.id.text_view_title);
        actionBarTitle.setText(getString(R.string.title_main_activity));

        actionBar.setCustomView(customView, layout);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ImageButton buttonRefresh = customView.findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast toast = Toast.makeText(v.getContext(),
                        getString(R.string.refresh_balance_status_in_process), Toast.LENGTH_SHORT);
                toast.show();
                if(balanceController.update()) {
                    toast = Toast.makeText(v.getContext(),
                            getString(R.string.refresh_balance_status_success), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    toast = Toast.makeText(v.getContext(),
                            getString(R.string.refresh_balance_status_failed), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        ImageButton buttonLogout = customView.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                balanceController.clearBalance();
                Intent intent = new Intent(MainActivity.this, CardIdActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"WRITE_EXTERNAL_STORAGE Permission is granted");
                return true;
            } else {
                Log.v(TAG,"WRITE_EXTERNAL_STORAGE Permission is revoked");
                return false;
            }
        }
        else {
            Log.v(TAG,"WRITE_EXTERNAL_STORAGE Permission is granted");
            return true;
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                balanceController.clearBalance();
                                Intent intent = new Intent(MainActivity.this, CardIdActivity.class);
                                startActivity(intent);
                                finish();
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
