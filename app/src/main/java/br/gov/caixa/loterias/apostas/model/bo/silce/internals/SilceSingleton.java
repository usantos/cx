package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class SilceSingleton {
    private RequestQueue queue;

    @AfterInject
    protected void init(){
        this.queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        this.queue.start();
    }

    public RequestQueue getRequestQueue() {
        return queue;
    }
}
