package caixa.gov.br.loterias.bo.silce;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.VolleyError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import caixa.gov.br.loterias.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.bo.silce.internals.SilceRequestQueue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(AndroidJUnit4.class)
public class SilceClientAndroidTest {
    private static final String TAG = SilceClientAndroidTest.class.getName();
    private class Listener<T> implements SilceClient.Listener<T> {
        T response = null;

        @Override
        public void onResponse(T response) {
            this.response = response;
            if (pendingRequests != null) {
                pendingRequests.release();
            }
        }

        public T getResponse() {
            return response;
        }
    };

    private class ErrorListener implements SilceClient.ErrorListener {
        VolleyError error = null;

        @Override
        public void onErrorResponse(VolleyError error) {
            this.error = error;
            Log.e(TAG, "Network error: " + SilceConnection.errorToString(error));

            if (pendingRequests != null) {
                pendingRequests.release();
            }
        }

        public VolleyError getError() {
            return error;
        }
    };

    //public static final String SILCE_BASE_URL = "https://simlo.des.caixa/silce-servico-rest/rest/v1/";
    public static final String SILCE_BASE_URL = "http://10.130.9.22:81/silce-servico-rest/rest/v1/";
    Context context;
    SilceClient silceClient;
    Listener listener;
    ErrorListener errorListener;
    Semaphore pendingRequests;

    @Before
    public void setup() {
        this.context = InstrumentationRegistry.getTargetContext();
        SilceRequestQueue queue = new SilceRequestQueue(context);
        SilceConnection conn = SilceConnection.create(queue.getRequestQueue(), SILCE_BASE_URL);
        this.silceClient = SilceClient.create(conn);
        listener = new Listener();
        errorListener = new ErrorListener();
    }

    @Test
    public void context() {
        assertThat(context, is(notNullValue()));
        assertThat(silceClient, is(notNullValue()));
    }

    @Test
    public void completaJogo() throws Exception {
        boolean ok = true;
        pendingRequests = new Semaphore(1, true);

        pendingRequests.acquire();

        silceClient.completaJogo(6, Arrays.asList(15, 18, 19, 48, 52, 58), 1, 60,
                listener, errorListener);
        pendingRequests.tryAcquire(60, TimeUnit.SECONDS);

        Log.d(TAG, "response = " + listener.getResponse());
        Log.d(TAG, "error = " + errorListener.getError());

        assertThat(listener.getResponse(), is(notNullValue()));
        assertThat(errorListener.getError(), is(nullValue()));
    }
}
