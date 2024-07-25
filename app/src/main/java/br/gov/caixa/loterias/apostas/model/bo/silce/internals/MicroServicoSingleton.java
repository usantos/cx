package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import android.content.Context;

import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.SSLPinningUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoEnum;

public class MicroServicoSingleton {
    private static MicroServicoSingleton instance;
    private RequestQueue queue;

    private MicroServicoSingleton(){
        initQueue();
    }

    public static MicroServicoSingleton getInstance(){
        if (instance == null){
            instance = new MicroServicoSingleton();
        }
        return instance;
    }
    public void initQueue() {
        Context ctx = Aplicacao.application.getApplicationContext();

        if (BuildVersionUtil.isPRD()){
            this.queue = SSLPinningUtil.pinagemPRD(ctx, ServicoEnum.MICROSERVICO);
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
