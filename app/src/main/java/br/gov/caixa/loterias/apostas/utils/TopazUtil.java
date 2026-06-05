package br.gov.caixa.loterias.apostas.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import br.com.topaz.heartbeat.Heartbeat;

public class TopazUtil {
	private static int REQUEST_LIST_PERMISSIONS = 1014;

	public static void check(Activity activity) {
		PermissaoUtil.checkList(activity, getListaPermissoes(), REQUEST_LIST_PERMISSIONS);
		LocalizacaoUtils.incrementContLocation();
	}

	private static String[] getListaPermissoes() {
		return new String[]{
				Manifest.permission.INTERNET,
				Manifest.permission.ACCESS_NETWORK_STATE,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.RECEIVE_BOOT_COMPLETED,
				Manifest.permission.ACCESS_WIFI_STATE,
				Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.GET_ACCOUNTS,
				Manifest.permission.READ_CONTACTS,
				Manifest.permission.BLUETOOTH,
				Manifest.permission.BLUETOOTH_CONNECT,
				Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
				Manifest.permission.POST_NOTIFICATIONS
		};
	}

	public static String getDevieId(Context context){
		String digest = "";
		try {
			digest = Heartbeat.getInfo(context, 8001);
		}catch (Exception e){
			Log.d("",e.getLocalizedMessage());
		}
		return digest;
	}

	public static Map<String, String> getParams(String deviceID){
		Map<String, String> params = new HashMap<>();
		params.put("deviceid", deviceID);
		params.put("nivel", "10");
		params.put("app", "br.gov.caixa.loterias.apostas");
		params.put("origem","tofd");

		return params;
	}

}
