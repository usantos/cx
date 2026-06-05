package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.onStatusPermissaoChecked;


public class LocalizacaoUtils {

    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int VERSAO_MIN_LOCALIZACAO = Build.VERSION_CODES.M;
    public static final int LOC_NAO_PERMITIDA = 102;
    public static final int LOC_NEG_PERMANENTEMENTE = 103;
    public static final int LOC_DESATIVADA = 104;
    public static final int LOC_FORA_DO_BRASIL = 105;
    public static final int LOC_NO_BRASIL = 106;
    public static final int LOC_ERRO = 404;
    public static final int PERMISSAO_LOCALIZACAO_COD = 400;
    private static Location locationUpdated;
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static String CONT_PERMISSION_LOCATION = "CONT_PERMISSION_LOCATION";
    private static final int NULL_VALUE_LOCATION = 0;
    private static final int MAX_VALUE_LOCATION = 2;

    public static void checaStatusPermissaoAsync(Activity activity, onStatusPermissaoChecked checked) {
        final WeakReference<Activity> activityRef = new WeakReference<>(activity);
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = getListener();


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!verificaGpsAtivo(activity)){
                checked.statusPermissaoChecked(LOC_DESATIVADA);
            } else if (getCont() < MAX_VALUE_LOCATION){
                checked.statusPermissaoChecked(LOC_NAO_PERMITIDA);
            } else {
                checked.statusPermissaoChecked(LOC_NEG_PERMANENTEMENTE);
            }

            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
        manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 10, listener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, listener);
        Location location = getLastLocation(manager);
        if (location == null) {
            location = locationUpdated;
        }

        Location finalLocation = location;
        executor.execute(() -> {
            int resultado;

            Activity act = activityRef.get();
            if (act == null || act.isFinishing()) {
                return;
            }
            if (checaVersaoMinima()) {
                if (checaPermissao(act)) {
                    resultado = checaStatusGps(act, finalLocation);
                } else {
                    if (checaNegadaDefinitivamente(act)) {
                        resultado = LOC_NEG_PERMANENTEMENTE;
                    } else {
                        resultado = LOC_NAO_PERMITIDA;
                    }
                }
            } else {
                resultado = checaStatusGps(act, finalLocation);
            }

            final int finalResultado = resultado;
            mainHandler.post(() -> {
                Activity a = activityRef.get();
                if (a == null || a.isFinishing()) return;
                checked.statusPermissaoChecked(finalResultado);
            });
        });
    }

    public static int checaStatusGps(Activity activity, Location location) {
        if (LocalizacaoUtils.verificaGpsAtivo(activity)) {
            int statusGps = LocalizacaoUtils.verificaRegiao(activity, location);
            if (statusGps == LocalizacaoUtils.LOC_NO_BRASIL) {
                return LOC_NO_BRASIL;
            } else if (statusGps == LOC_NAO_PERMITIDA){
                return LOC_NAO_PERMITIDA;
            } else {
                if (statusGps == LocalizacaoUtils.LOC_FORA_DO_BRASIL) {
                    return LOC_FORA_DO_BRASIL;
                } else {
                    return LOC_ERRO;
                }
            }
        } else {
            return LOC_DESATIVADA;
        }
    }

    public static boolean verificaGpsAtivo(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public static int verificaRegiao(Activity activity, Location location) {

        if (location != null ){
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address obj = addresses.get(0);
                if (obj.getCountryCode().equalsIgnoreCase(activity.getResources().getString(R.string.bloq_local_brasil))) {
                    return LOC_NO_BRASIL;
                } else {
                    return LOC_FORA_DO_BRASIL;
                }
            } catch (IOException e) {
                return LOC_ERRO;
            }
        } else {
            return LOC_ERRO;
        }

    }

    @SuppressLint("MissingPermission")
    private static Location getLastLocation(LocationManager manager) {
        int qtdVerificacoes = 0;
        Location  location = null;
        while (qtdVerificacoes <= 20 && location == null) {
            qtdVerificacoes++;
            List<String> providers = manager.getProviders(true);
            for (String providerFor : providers) {
                Location l = manager.getLastKnownLocation(providerFor);
                if (l == null) {
                    continue;
                }
                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l;
                }
            }
        }
        return location;
    }

    public static void intentConfigGps(Activity activity) {
        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static void intentConfigPermissao(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static boolean checaPermissao(Activity activity) {
        return (ContextCompat.checkSelfPermission(activity,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(activity,
                ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static Boolean checaVersaoMinima() {
        return Build.VERSION.SDK_INT >= VERSAO_MIN_LOCALIZACAO;
    }

    public static Boolean checaNegadaDefinitivamente(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_COARSE_LOCATION);
    }

    public static void solicitaPermissao(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSAO_LOCALIZACAO_COD);
    }

    private static LocationListener getListener(){
      return  new LocationListener() {
          @Override
          public void onLocationChanged(Location location) {
              locationUpdated = location;
          }

          @Override
          public void onStatusChanged(String s, int i, Bundle bundle) {

          }

          @Override
          public void onProviderEnabled(String s) {

          }

          @Override
          public void onProviderDisabled(String s) {

          }
      };
    }

    public static void incrementContLocation() {
        int cont = getCont();
        cont++;
        SharedPreferencesUtils.setValor(CONT_PERMISSION_LOCATION, cont);
    }

    private static int getCont() {
        return SharedPreferencesUtils.getValorInt(CONT_PERMISSION_LOCATION, NULL_VALUE_LOCATION);
    }

}
