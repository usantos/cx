package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.content.Context;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.SSLPinningUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoEnum;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

public class ApostadorSingleton {
    private static ApostadorSingleton instance;
    private RequestQueue queue;

    private ApostadorSingleton(){
        initQueue();
    }

    public static ApostadorSingleton getInstance(){
        if (instance == null){
            instance = new ApostadorSingleton();
        }
        return instance;
    }

    public void initQueue() {
        Context ctx = Aplicacao.application.getApplicationContext();

        if (BuildVersionUtil.isPRD()){
            this.queue = SSLPinningUtil.pinagemPRD(ctx, ServicoEnum.APOSTADOR);
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