package br.gov.caixa.loterias.apostar.interno.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class TokenActivity extends AppCompatActivity {


    private AlertDialog loadViewProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        loadViewProgress = LoadingViewLoterias.show(this);
        try {
            AuthorizationResponse authorizationResponse = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException authorizationException = AuthorizationException.fromIntent(getIntent());
            if (authorizationException == null && authorizationResponse != null) {
                AuthorizationService authorizationService = new AuthorizationService(this);
                authorizationService.performTokenRequest(authorizationResponse.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
                        if (response.accessToken == null || response.refreshToken == null) {
                            AcessoBO_.getInstance_(TokenActivity.this).iniciarLoginSSO(TokenActivity.this);
                        } else {
                            if (!"".equals(response.accessToken) && !"".equals(response.refreshToken)) {
                                LoginSP.loginRealizado(true);
                                String separar[] = response.accessToken.split("\\.");
                                if (separar.length > 0) {
                                    String tokenCorpo = separar[1];
                                    byte bytes[] = Base64.decode(tokenCorpo, Base64.URL_SAFE);
                                    tokenCorpo = new String(bytes, Charset.forName("UTF-8"));
                                    try {
                                        JSONObject jsonObject = new JSONObject(tokenCorpo);

                                        String cpf = jsonObject.getString("cpf");
                                        if (cpf.length() < 11) {
                                            StringBuilder zeros = new StringBuilder();
                                            int totalIncluirZero = 11 - cpf.length();
                                            for (int contador = 0; contador < totalIncluirZero; contador++) {
                                                zeros.append("0");
                                            }
                                            zeros.append(cpf);
                                            cpf = zeros.toString();
                                        }

                                        DadosUsuarioBO.salvarCpfUsuario(cpf);
                                        DadosUsuarioBO.salvarNomeUsuairo(jsonObject.getString("name"));
                                        KeycloakBO_.getInstance_(TokenActivity.this).setAccessToken(response.accessToken);
                                        KeycloakBO_.getInstance_(TokenActivity.this).setRefreshToken(response.refreshToken);
                                        FirebaseManager.registraEvento(TokenActivity.this ,"EFETUOU_LOGIN_SUCESSO");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                DadosUsuarioBO.updateUsuarioLogado(TokenActivity.this, true);
                                if (AcessoBO.PRIMEIRO_LOGIN) {
                                    AcessoBO.PRIMEIRO_LOGIN = false;
                                }
                            }
                        }
                        finish();
                    }
                });
            }
        } catch (Exception e) {
            finish();
        }
    }

}
