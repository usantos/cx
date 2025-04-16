package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.SSLPinningUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoEnum;

public class NovaAPISingleton {
    private static NovaAPISingleton instance;
    private RequestQueue queue;

    private NovaAPISingleton(){
        initQueue();
    }

    public static NovaAPISingleton getInstance(){
        if (instance == null){
            instance = new NovaAPISingleton();
        }
        return instance;
    }
    public void initQueue() {
        Context ctx = Aplicacao.application.getApplicationContext();

        if (BuildVersionUtil.isPRD()){
            this.queue = SSLPinningUtil.pinagemPRD(ctx, ServicoEnum.NOVA_API);
        } else {
            SSLPinningUtil.pinagemTQS();
            this.queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        }
        this.queue.start();
    }

    public RequestQueue getRequestQueue() {
        return queue;
    }
}
