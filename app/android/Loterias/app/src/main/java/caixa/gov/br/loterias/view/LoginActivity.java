package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import caixa.gov.br.loterias.R;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @AfterViews
    protected void init() {
        Button button = (Button) findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectKeycloak();
            }
        });
    }

    private void connectKeycloak() {
//        if (!KeycloakBO.isConnected()) {
//
//            KeycloakBO.connect(LoginActivity.this, new Callback() {
//                        @Override
//                        public void onSuccess(Object o) {
//                            loginSucess();
//                        }
//
//                        @Override
//                        public void onFailure(Exception e) {
//
//                        }
//                    }
//            );
//
//        } else {
            loginSucess();
//        }
    }

    private void loginSucess(){
        Intent activity = new Intent(this, HomeActivity.class);
        startActivity(activity);
    }

}
