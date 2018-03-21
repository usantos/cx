package caixa.gov.br.loterias.model.bo.silce.internals;

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
    private GsonBuilder gsonBuilder;

    @AfterInject
    protected void init(){
        this.queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        this.gsonBuilder = new GsonBuilder();
    }

    public RequestQueue getRequestQueue() {
        return queue;
    }

    public <T> void add(Request<T> req) {
        queue.add(req);
    }

    public Gson gson() {
        return gsonBuilder.create();
    }
}
