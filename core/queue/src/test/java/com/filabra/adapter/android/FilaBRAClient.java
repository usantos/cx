package com.filabra.adapter.android;

import android.app.Activity;

/**
 * Substituto do transporte do fornecedor apenas para testes. O cliente ofuscado não pode ser
 * instrumentado pelo ASM do Robolectric. Preserva o construtor público exato, o campo activityClass
 * e os contratos de go() consumidos pelo gateway; as funções de retorno ainda executam o gateway
 * real de produção e o adaptador de concessão da composição. Não valida rede nem SDK do dispositivo.
 */
public final class FilaBRAClient {
    public static FilaBRAClient latest;
    public final Activity activity;
    public final String url;
    public final String target;
    public final String account;
    public final String queue;
    public final FilaBRAClientListener listener;
    public Class activityClass;
    public boolean started;

    public FilaBRAClient(Activity activity, String url, String account, String queue,
                         String target, FilaBRAClientListener listener) {
        this.activity = activity;
        this.url = url;
        this.target = target;
        this.account = account;
        this.queue = queue;
        this.listener = listener;
        latest = this;
    }

    public void go() { started = true; }
}
