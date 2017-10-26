package caixa.gov.br.loterias.bo;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.hamcrest.CoreMatchers;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;


import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.ui.LoginActivity_;

import static java.lang.Thread.sleep;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class KeycloakBOTest {
    public final static String TAG =  KeycloakBOTest.class.getName();
    private Context context;

    public class ActivityCallback<T> extends AbstractActivityCallback {
        Class<T> klazz;
        Semaphore pendingRequests;
        T data;
        Exception error;

        public ActivityCallback(Class<T> klazz, Semaphore pendingRequests, Object... params) {
            super(params);
            this.klazz = klazz;
            this.pendingRequests = pendingRequests;
        }

        @Override
        public void onSuccess(Object data) {
            Log.i(TAG, "ActivityCallback.onSuccess: " + data);
            this.data = klazz.cast(data);
            pendingRequests.release();
        }

        @Override
        public void onFailure(Exception e) {
            Log.i(TAG, "ActivityCallback.onFailure: " + e);
            this.error = e;
            pendingRequests.release();
        }
    }

    @Rule
    public ActivityTestRule<LoginActivity_> loginActivityRule =
            new ActivityTestRule<>(LoginActivity_.class, true, true);

    @Before
    public void setUp() throws Exception {
//        this.context = InstrumentationRegistry.getTargetContext();
//        Log.e(TAG, "Package name = " + context.getPackageName());
//        Intent startIntent = new Intent(context, LoginActivity_.class);
//        context.startActivity(startIntent);
//        loginActivityRule.launchActivity(startIntent);
    }

    @After
    public void tearDown() throws Exception {
        //loginActivityRule.getActivity().finish();
    }

    @Test
    public void connect() throws Exception {
        Semaphore pendingRequests = new Semaphore(1, true);
        ActivityCallback<Object> callback = new ActivityCallback<>(Object.class, pendingRequests);

        Log.e(TAG, loginActivityRule.getActivity().toString());
        Log.e(TAG, loginActivityRule.getActivity().getApplicationContext().toString());

        KeycloakBO.connect(loginActivityRule.getActivity(), callback);

        //sleep(60000);

        assertTrue(pendingRequests.tryAcquire(300, TimeUnit.SECONDS));
        pendingRequests.acquire();
        Log.e(TAG, "Data = " + callback.data);
        Log.e(TAG, "Error = + " + callback.error);

        assertThat(callback.data, CoreMatchers.is(notNullValue()));
        assertThat(callback.error, CoreMatchers.is(nullValue()));
    }

    @Test
    public void isConnected() throws Exception {
        onView(withId(R.id.buttonLogin)).perform(click());
    }

}