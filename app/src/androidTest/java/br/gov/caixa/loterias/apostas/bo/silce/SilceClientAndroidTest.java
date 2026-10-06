// package br.gov.caixa.loterias.apostar.bo.silce;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.android.volley.VolleyError;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.Arrays;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.TimeUnit;
//
//import br.gov.caixa.loterias.apostar.BuildConfig;
//import ApostaBO;
//import CompletaJogoRsp;
//import UsuarioLogadoResponse;
//import SilceConnection;
//import SilceSingleton;
//
//import static junit.framework.Assert.assertTrue;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.CoreMatchers.*;
//import static org.hamcrest.Matchers.isOneOf;
//
//@RunWith(AndroidJUnit4.class)
//public class SilceClientAndroidTest {
//    private static final String SILCE_USERNAME = "vinicius.ferraz@gmail.com";
//    private static final String SILCE_PASSWORD = "Vaibombar1";
//    private static final String TAG = SilceClientAndroidTest.class.getName();
//    private class Listener<T> implements SilceClient.Listener<T> {
//        T response = null;
//
//        @Override
//        public void onResponse(T response) {
//            this.response = response;
//            if (pendingRequests != null) {
//                pendingRequests.release();
//            }
//        }
//
//        public T getResponse() {
//            return response;
//        }
//    };
//
//    private class ErrorListener implements SilceClient.ErrorListener {
//        VolleyError error = null;
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            this.error = error;
//            Log.e(TAG, "Network error: " + SilceConnection.errorToString(error));
//
//            if (pendingRequests != null) {
//                pendingRequests.release();
//            }
//        }
//
//        public VolleyError getError() {
//            return error;
//        }
//    };
//
//    Context context;
//    SilceClient silceClient;
//    Semaphore pendingRequests;
//    ApostaBO apostaBO;
//
//    @Before
//    public void setup() {
//        this.context = InstrumentationRegistry.getTargetContext();
//        SilceSingleton queue = new SilceSingleton(context);
//        SilceConnection conn = SilceConnection.create(queue.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
//        this.silceClient = SilceClient.create(conn);
//        this.apostaBO = new ApostaBO(this.context);
//    }
//
//    @Test
//    public void context() {
//        assertThat(context, is(notNullValue()));
//        assertThat(silceClient, is(notNullValue()));
//    }
//
//    @Test
//    public void completaJogo() throws Exception {
//        ApostaBO apostaBO = new ApostaBO(InstrumentationRegistry.getTargetContext());
//        boolean ok = true;
//        Listener<CompletaJogoRsp> listener = new Listener();
//        ErrorListener errorListener = new ErrorListener();
//        pendingRequests = new Semaphore(1, true);
//
//        pendingRequests.acquire();
//
//        apostaBO.completaJogo(6, Arrays.asList(15, 18, 19, 48, 52, 58), 1, 60,
//                listener, errorListener);
//        pendingRequests.tryAcquire(60, TimeUnit.SECONDS);
//
//        Log.d(TAG, "response = " + listener.getResponse());
//        Log.d(TAG, "error = " + errorListener.getError());
//
//        assertThat(listener.getResponse(), is(notNullValue()));
//        assertThat(errorListener.getError(), is(nullValue()));
//    }
//
//    @Test()
//    public void autenticaCrypto() throws Exception {
//        // for now, only tests the cryptography
//        boolean ok = true;
//        Listener<UsuarioLogadoResponse> listener = new Listener<>();
//        ErrorListener errorListener = new ErrorListener();
//        pendingRequests = new Semaphore(1, true);
//
//        pendingRequests.acquire();
//        silceClient.autenticar(SILCE_USERNAME, SILCE_PASSWORD,
//                listener, errorListener);
//        pendingRequests.tryAcquire(60, TimeUnit.SECONDS);
//
//        Log.d(TAG, "response = " + listener.getResponse());
//        Log.d(TAG, "error = " + errorListener.getError());
//
//        assertTrue((listener.getResponse() != null)
//            || ((errorListener.getError() != null)
//                && (errorListener.getError().networkResponse != null)
//                && (errorListener.getError().networkResponse.statusCode == 401)));
//    }
//
//    @Test
//    public void autentica() throws Exception {
//        // for now, only tests the cryptography
//        boolean ok = true;
//        Listener<UsuarioLogadoResponse> listener = new Listener<>();
//        ErrorListener errorListener = new ErrorListener();
//        pendingRequests = new Semaphore(1, true);
//
//        pendingRequests.acquire();
//        silceClient.autenticar(SILCE_USERNAME, SILCE_PASSWORD,
//                listener, errorListener);
//        //silceClient.autenticar("email@gmail.com", "a",
//        //                listener, errorListener);
//        pendingRequests.tryAcquire(60, TimeUnit.SECONDS);
//
//        Log.d(TAG, "response = " + listener.getResponse());
//        Log.d(TAG, "error = " + errorListener.getError());
//
//        assertThat(listener.getResponse(), is(notNullValue()));
//        assertThat(errorListener.getError(), is(nullValue()));
//    }
//}
