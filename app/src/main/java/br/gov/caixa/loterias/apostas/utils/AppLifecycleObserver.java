package br.gov.caixa.loterias.apostas.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class AppLifecycleObserver implements DefaultLifecycleObserver {

    // Explicit implementations also work with instrumented lifecycle interfaces in Debug.
    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {}

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {}

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {}

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {}

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // app -> background
        AppState.wasInBackground = true;
    }


    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // app -> foreground (do NOT reset here; let the first resumed activity consume it)
    }
}
