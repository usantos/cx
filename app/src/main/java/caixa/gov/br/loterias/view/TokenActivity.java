package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.RetornoListaCarrinho;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import io.swagger.annotations.Authorization;

public class TokenActivity extends AppCompatActivity {

    private AlertDialog loadViewProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        loadViewProgress = LoadingViewLoterias.show(this);
        try{
            AuthorizationResponse authorizationResponse = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException authorizationException = AuthorizationException.fromIntent(getIntent());
            if(authorizationException == null && authorizationResponse != null) {
                AuthorizationService authorizationService = new AuthorizationService(this);
                authorizationService.performTokenRequest(authorizationResponse.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
                        if(!"".equals(response.accessToken) && !"".equals(response.refreshToken)) {
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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            DadosUsuarioBO.updateUsuarioLogado(TokenActivity.this, true);
                            verificarCadastroWebservice();
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void verificarCadastroWebservice() {
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(this);
        final RetornoListaCarrinho retornoListaCarrinho = new RetornoListaCarrinho();
        DBLoteriasCrud crud = new DBLoteriasCrud(this);
//        carrinhoDTO.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        List<ApostaDTO> listApostas = new ArrayList<>();
        retornoListaCarrinho.setApostas( listApostas );
        dadosUsuarioBO.postVerificarCadastro(retornoListaCarrinho, new RequestListener<UsuarioLogadoResponse>() {
            @Override
            public void onResponse(UsuarioLogadoResponse response) {
//                buttonLogin.setEnabled( true );
                loadViewProgress.dismiss();
                if (response.getRedirect() == null){
                    PrincipalActivity_.intent(TokenActivity.this).start();
                    TokenActivity.this.finish();
                }else{
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), TokenActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
//                buttonLogin.setEnabled( true );
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, TokenActivity.this);

            }
        });
    }

}
