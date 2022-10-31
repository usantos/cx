package br.gov.caixa.loterias.apostas.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import br.com.topaz.heartbeat.Heartbeat;

public class TopazUtil {

	private static final int REQUEST_INTERNET = 1001;
	private static final int REQUEST_ACCESS_NETWORK_STATE = 1002;
	private static final int REQUEST_ACCESS_FINE_LOCATION = 1003;
	private static final int REQUEST_RECEIVE_BOOT_COMPLETED = 1004;
	private static final int REQUEST_ACCESS_WIFI_STATE = 1005;
	private static final int REQUEST_READ_PHONE_STATE = 1006;
	private static final int REQUEST_ACCESS_COARSE_LOCATION = 1007;
	private static final int REQUEST_READ_EXTERNAL_STORAGE = 1008;
	private static final int REQUEST_GET_ACCOUNTS = 1009;
	private static final int REQUEST_READ_CONTACTS = 1010;
	private static final int REQUEST_BLUETOOTH = 1011;
	private static final int REQUEST_BLUETOOTH_CONNECT = 1012;
	private static final int REQUEST_BIND_NOTIFICATION_LISTENER_SERVICE = 1013;

	private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
	private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
	private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final String PERMISSION_RECEIVE_BOOT_COMPLETED = Manifest.permission.RECEIVE_BOOT_COMPLETED;
	private static final String PERMISSION_ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
	private static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
	private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
	private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
	private static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
	private static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
	private static final String PERMISSION_BLUETOOTH = Manifest.permission.BLUETOOTH;
	private static final String PERMISSION_BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT;
	private static final String PERMISSION_BIND_NOTIFICATION_LISTENER_SERVICE = Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE;

	public static void check(Activity activity) {

		PermissaoUtil.check(activity, PERMISSION_INTERNET, REQUEST_INTERNET);
		PermissaoUtil.check(activity, PERMISSION_ACCESS_NETWORK_STATE, REQUEST_ACCESS_NETWORK_STATE);
		PermissaoUtil.check(activity, PERMISSION_ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION);
		PermissaoUtil.check(activity, PERMISSION_RECEIVE_BOOT_COMPLETED, REQUEST_RECEIVE_BOOT_COMPLETED);
		PermissaoUtil.check(activity, PERMISSION_ACCESS_WIFI_STATE, REQUEST_ACCESS_WIFI_STATE);
		PermissaoUtil.check(activity, PERMISSION_READ_PHONE_STATE, REQUEST_READ_PHONE_STATE);
		PermissaoUtil.check(activity, PERMISSION_ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
		PermissaoUtil.check(activity, PERMISSION_READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE);
		PermissaoUtil.check(activity, PERMISSION_GET_ACCOUNTS, REQUEST_GET_ACCOUNTS);
		PermissaoUtil.check(activity, PERMISSION_READ_CONTACTS, REQUEST_READ_CONTACTS);
		PermissaoUtil.check(activity, PERMISSION_BLUETOOTH, REQUEST_BLUETOOTH);
		PermissaoUtil.check(activity, PERMISSION_BLUETOOTH_CONNECT, REQUEST_BLUETOOTH_CONNECT);
		PermissaoUtil.check(activity, PERMISSION_BIND_NOTIFICATION_LISTENER_SERVICE, REQUEST_BIND_NOTIFICATION_LISTENER_SERVICE);

	}

	public static boolean isGranted(Activity activity){
		if(
				PermissaoUtil.isGranted(activity, PERMISSION_INTERNET) &&
				PermissaoUtil.isGranted(activity, PERMISSION_ACCESS_NETWORK_STATE) &&
				PermissaoUtil.isGranted(activity, PERMISSION_ACCESS_FINE_LOCATION) &&
				PermissaoUtil.isGranted(activity, PERMISSION_RECEIVE_BOOT_COMPLETED) &&
				PermissaoUtil.isGranted(activity, PERMISSION_ACCESS_WIFI_STATE) &&
				PermissaoUtil.isGranted(activity, PERMISSION_READ_PHONE_STATE) &&
				PermissaoUtil.isGranted(activity, PERMISSION_ACCESS_COARSE_LOCATION) &&
				PermissaoUtil.isGranted(activity, PERMISSION_READ_EXTERNAL_STORAGE) &&
				PermissaoUtil.isGranted(activity, PERMISSION_GET_ACCOUNTS) &&
				PermissaoUtil.isGranted(activity, PERMISSION_READ_CONTACTS) &&
				PermissaoUtil.isGranted(activity, PERMISSION_BLUETOOTH) &&
				PermissaoUtil.isGranted(activity, PERMISSION_BLUETOOTH_CONNECT) &&
				PermissaoUtil.isGranted(activity, PERMISSION_BIND_NOTIFICATION_LISTENER_SERVICE)

		){
			return true;
		}
		return false;
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
		params.put("nivel", "20");
		params.put("app", "br.gov.caixa.loterias.apostas");
		params.put("origem","tofd");

		return params;
	}

}
