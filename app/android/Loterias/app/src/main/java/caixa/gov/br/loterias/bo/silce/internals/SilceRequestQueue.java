package caixa.gov.br.loterias.bo.silce.internals;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import org.androidannotations.annotations.EBean;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class SilceRequestQueue {
    Context context;
    RequestQueue queue;

    public SilceRequestQueue(Context context) {
        this.context = context;
        this.queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
    }

    public RequestQueue getRequestQueue() {
        return queue;
    }

    public <T> void add(Request<T> req) {
        this.getRequestQueue().add(req);
    }
}
