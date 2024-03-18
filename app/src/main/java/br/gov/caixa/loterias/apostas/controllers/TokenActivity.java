package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosBiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;

public class TokenActivity extends AppCompatActivity {


    private AuthorizationService authorizationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_token);
        AlertDialogUtils.show(this);
        try {
            AuthorizationResponse authorizationResponse = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException authorizationException = AuthorizationException.fromIntent(getIntent());
            if (authorizationException == null && authorizationResponse != null) {

                authorizationService =  AuthorizationServiceFactory.getAuthorizationService(this);
                authorizationService.performTokenRequest(authorizationResponse.createTokenExchangeRequest(), (response, ex) -> {
                    if (response != null) {
                        if (response.accessToken == null || response.refreshToken == null) {
                            AcessoBO_.getInstance_(TokenActivity.this).iniciarLoginSSO(TokenActivity.this);
                        } else {
                            if (conseguiuLogar(response)) {
                                LoginSP.loginRealizado(true);
                                String separar[] = response.accessToken.split(getResources().getString(R.string.barra_barra_ponto));
                                if (separar.length > 0) {
                                    String tokenCorpo = separar[1];
                                    salvaDadosUsuario(response, tokenCorpo);
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
                                        //SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                                        SessaoUsuario.getInstance().setResponderAutoavaliacao(response.getPayload().getResponderAutoavaliacao());
                                        SessaoUsuario.getInstance().setSuspensaoTemporariaApostador(response.getPayload().getSuspensaoTemporariaApostador());

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

    private void salvaDadosUsuario(TokenResponse response, String tokenCorpo) {
        byte bytes[] = Base64.decode(tokenCorpo, Base64.URL_SAFE);
        tokenCorpo = new String(bytes, Charset.forName(getResources().getString(R.string.utf_8)));
        try {
            JSONObject jsonObject = new JSONObject(tokenCorpo);

            String cpf = jsonObject.getString(getResources().getString(R.string.cpf_minusculo));
            if (cpf.length() < 11) {
                StringBuilder zeros = new StringBuilder();
                int totalIncluirZero = 11 - cpf.length();
                for (int contador = 0; contador < totalIncluirZero; contador++) {
                    zeros.append(getResources().getString(R.string.zero));
                }
                zeros.append(cpf);
                cpf = zeros.toString();
            }

            DadosUsuarioBO.salvarUsuairo(jsonObject.getString(getResources().getString(R.string.name)), cpf);
            if (BiometriaBO.getInstance().getBiometriaHabilitada()) {
                DadosBiometriaBO.salvarBiometria(response.refreshToken, jsonObject.getString(getResources().getString(R.string.name)), cpf);
            }
            KeycloakBO_.getInstance_(TokenActivity.this).setAccessToken(response.accessToken);
            KeycloakBO_.getInstance_(TokenActivity.this).setRefreshToken(response.refreshToken);
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_login_sucesso));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean conseguiuLogar(TokenResponse response) {
        return !getResources().getString(R.string.string_vazia).equals(response.accessToken) && !getResources().getString(R.string.string_vazia).equals(response.refreshToken);
    }

    @Override
    public void finish() {
        AlertDialogUtils.dismiss();
        super.finish();
    }

}
