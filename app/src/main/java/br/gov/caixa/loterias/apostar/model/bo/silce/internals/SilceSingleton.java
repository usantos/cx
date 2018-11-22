package br.gov.caixa.loterias.apostar.model.bo.silce.internals;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
