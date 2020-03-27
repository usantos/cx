package br.gov.caixa.loterias.apostar.interno.controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.AuthorizationServiceFactory;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class TokenActivity extends AppCompatActivity {


    private AlertDialog loadViewProgress;
    private AuthorizationService authorizationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_token);
        loadViewProgress = LoadingViewLoterias.show(this);
        atualizaLimiteDiario();
        try {
            AuthorizationResponse authorizationResponse = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException authorizationException = AuthorizationException.fromIntent(getIntent());
            if (authorizationException == null && authorizationResponse != null) {

                authorizationService =  AuthorizationServiceFactory.getAuthorizationService(this);
                authorizationService.performTokenRequest(authorizationResponse.createTokenExchangeRequest(), (response, ex) -> {
                    if (response != null) {
                        if (response.accessToken == null || response.refreshToken == null) {
                            atualizaLimiteDiario();
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

                            final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(TokenActivity.this);
                            DBLoteriasCrud crud = new DBLoteriasCrud(TokenActivity.this);
                            List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
                            dadosUsuarioBO.postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

                                @Override
                                public void onResponse(UsuarioLogadoResponse response) {
                                    if (response.getRedirect() == null) {
                                        SessaoUsuario.getInstance().setValorMaximoAposta(response.getPayload().getLimiteDiario());
                                        if(response!= null && response.getPayload() != null && response.getPayload().getLimiteDiario() != null){
                                            SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                                        }
                                        SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                                        CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());
                                        UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(response.getPayload().getUltimaNotificacao());

                                        crud.deleteAll();
                                        finish();
                                    } else {
                                        finish();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    finish();
                                }
                            });
                        }
                    } else {
                        finish();
                    }
                });
            }
        } catch (Exception e) {
            finish();
         }
    }
    @Override
    public void finish() {

        if(loadViewProgress != null){
            loadViewProgress.dismiss();
        }
        super.finish();
    }

    private void atualizaLimiteDiario(){
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(TokenActivity.this);

        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = new ArrayList<>();
        dadosUsuarioBO.postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                if(response!= null && response.getPayload() != null && response.getPayload().getLimiteDiario() != null){
                    SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
}
