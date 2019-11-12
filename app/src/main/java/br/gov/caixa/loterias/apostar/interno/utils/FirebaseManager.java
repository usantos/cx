package br.gov.caixa.loterias.apostar.interno.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public final class FirebaseManager {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void registraEvento(Context context, String title) {
        mFirebaseAnalytics =  FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id-" + title);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, title);
        mFirebaseAnalytics.logEvent(title, bundle);
    }

    public static void registraErro(Context context, String codigoErro, String endpoint) {
        String title = "ERRO_WEBSERVICE";
        mFirebaseAnalytics =  FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();

        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id-" + title);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, title);
        bundle.putString("ENDPOINT", endpoint);
        bundle.putString("DATAEHORA", DateUtils.getDateTime());
        bundle.putString("CODIGOERRO", codigoErro);

        mFirebaseAnalytics.logEvent(title, bundle);
    }
}
