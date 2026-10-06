package br.gov.caixa.loterias.apostas.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissaoUtil {

	public static boolean check(Activity activity, String permission, int code) {
		if (!isGranted(activity, permission)){
			ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
			return false;
		}
		return true;
	}

	public static boolean isGranted(Activity activity, String permission) {
		int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED){
			return false;
		}
		return true;
	}

	public static boolean checkList(Activity activity, String[] permissoes, int code){

		if (!isListGranted(activity, permissoes)){
			ActivityCompat.requestPermissions(activity, permissoes, code);
			return false;
		}
		return true;
	}

	public static boolean isListGranted(Activity activity, String[] permissions) {
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	public static String[] getListaLocalizacaoPermissoes() {
		return new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
		};
	}



}
