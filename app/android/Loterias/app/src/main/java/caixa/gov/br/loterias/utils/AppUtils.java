package caixa.gov.br.loterias.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;


/**
 * Created by lsimas on 10/04/2017.
 */

public class AppUtils {

    private AppUtils(){
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    public static void requestFingerprintPermission(Activity activity, int requestCode){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, requestCode);
    }

    public static boolean checkFingerprintPermission(Activity activity){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED;
    }


    public static String getOSVersion(){
        return Build.VERSION.RELEASE;
    }

    public static String getApplicationVersion(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }


    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static boolean isStringEmpty(String string) {
        // Null-safe, short-circuit evaluation.
        return string == null || string.trim().isEmpty();
    }


    public static String getEditTextValue(EditText editText) {
        assert editText != null;

        if (editText.getText() != null) {
            return editText.getText().toString();
        }
        return null;
    }

}
