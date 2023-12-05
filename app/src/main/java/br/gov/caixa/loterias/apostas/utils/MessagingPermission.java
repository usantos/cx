package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MessagingPermission {

    private final FragmentActivity activity;
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    public MessagingPermission(FragmentActivity activity) {
        this.activity = activity;
        this.requestPermissionLauncher =
                activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        //Toast.makeText(activity, "Notifications permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(activity, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                        //        Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void requestNotificationPermission() {
        //Pede permissao para msg apos a TIRAMISSU antes ja vinha habilitada
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

    }
}