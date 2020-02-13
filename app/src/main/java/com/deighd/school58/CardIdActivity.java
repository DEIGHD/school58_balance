package com.deighd.school58;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;

import com.deighd.school58.utils.Preferences;

public class CardIdActivity extends AppCompatActivity {
    private static final String TAG = "CardIdActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardid_activity);
        AlarmService.stopAlarmService(this);
        requestStoragePermission();

        final Preferences preferences = new Preferences(this);

        Button checkBalance = findViewById(R.id.button_check);
        checkBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText editCardId = findViewById(R.id.edit_text_card_id);
                String cardId = editCardId.getText().toString();
                preferences.saveCardId(cardId);

                Intent intent = new Intent(CardIdActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PERMISSION_REQUEST_CODE) {
            requestStoragePermission();
        }
    }

    public void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"WRITE_EXTERNAL_STORAGE Permission is granted");
            } else {
                Log.v(TAG,"WRITE_EXTERNAL_STORAGE Permission is revoked");
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
        else {
            Log.v(TAG,"WRITE_EXTERNAL_STORAGE Permission is granted");
        }
    }

    private void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        boolean allowed = true;

        if(requestCode == PERMISSION_REQUEST_CODE) {
            for (int res : grantResults){
                allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
            }
        }
        else {
            allowed = false;
        }

        if (!allowed){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardIdActivity.this, R.style.AlertDialog);
                    builder.setMessage(getResources().getString(R.string.alert_dialog_permission))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.alert_dialog_permission_positive_button),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            requestStoragePermission();
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton(
                                    getResources().getString(R.string.alert_dialog_permission_negative_button),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardIdActivity.this, R.style.AlertDialog);
                    builder.setMessage(getResources().getString(R.string.alert_dialog_permission_not_show))
                            .setCancelable(false)
                            .setPositiveButton(
                                    getResources().getString(R.string.alert_dialog_permission_not_show_positive_button),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            openApplicationSettings();
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton(
                                    getResources().getString(R.string.alert_dialog_permission_not_show_negative_button),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else {
                finish();
            }
        }
    }
}