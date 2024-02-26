package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;


public class LocalizacaoUtils {

    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static final int VERSAO_MIN_LOCALIZACAO = android.os.Build.VERSION_CODES.M;
    public static final int LOC_NAO_PERMITIDA = 102;
    public static final int LOC_NEG_PERMANENTEMENTE = 103;
    public static final int LOC_DESATIVADA = 104;
    public static final int LOC_FORA_DO_BRASIL = 105;
    public static final int LOC_NO_BRASIL = 106;
    public static final int LOC_ERRO = 404;

    public static final int PERMISSAO_LOCALIZACAO_COD = 400;

    private static Location locationUpdated;

    public static int checaStatusPermissao(Activity activity) {
        //Verifica versão mínima para verificação em tempo de execução
        if (checaVersaoMinima()) {
            if (checaPermissao(activity)) {
                return checaStatusGps(activity);
            } else {
                if (checaNegadaDefinitivamente(activity)) {
                    return LOC_NEG_PERMANENTEMENTE;
                } else {
                    return LOC_NAO_PERMITIDA;
                }
            }
        } else {
            return checaStatusGps(activity);
        }
    }

    public static int checaStatusGps(Activity activity){
        if (LocalizacaoUtils.verificaGpsAtivo(activity)) {
            int statusGps = LocalizacaoUtils.verificaRegiao(activity);
            if(statusGps == LocalizacaoUtils.LOC_NO_BRASIL){
                return LOC_NO_BRASIL;
            } else {
                if(statusGps == LocalizacaoUtils.LOC_FORA_DO_BRASIL) {
                    return LOC_FORA_DO_BRASIL;
                } else {
                    return LOC_ERRO;
                }
            }
        } else {
            return LOC_DESATIVADA;
        }
    }

    public static boolean verificaGpsAtivo(Activity activity){
        final LocationManager manager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }

    @SuppressLint("MissingPermission")
    public static int verificaRegiao(Activity activity){
        final LocationManager manager  = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );
        String bestProvider;
        LocationListener      listener = getListener();

        Criteria              criteria = new Criteria();
        bestProvider = manager.getBestProvider(criteria, false);

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
        manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 10, listener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, listener);
        Location location = getLastLocation(manager);
        if(location == null) {
            location = locationUpdated;
        }
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
        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
                == PackageManager.PERMISSION_GRANTED);
    }

    public static Boolean checaVersaoMinima() {
        return android.os.Build.VERSION.SDK_INT >= VERSAO_MIN_LOCALIZACAO;
    }

    public static Boolean checaNegadaDefinitivamente(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION);
    }

    public static void solicitaPermissao(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{ACCESS_FINE_LOCATION}, PERMISSAO_LOCALIZACAO_COD);
    }

    public static void redirecionaConfiguracaoGPS(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(intent);
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

}
