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
}
