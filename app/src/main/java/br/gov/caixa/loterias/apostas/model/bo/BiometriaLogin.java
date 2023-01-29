package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;

import java.util.List;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TokenActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;

public class BiometriaLogin {

    public void loginBiometria(Activity activity, final OnRefreshTokenListener listener) {

        BiometriaDTO biometriaDTO = DadosBiometriaBO.obterBiometria();
        if (biometriaDTO == null || biometriaDTO.getRefresh() == null) {
            listener.errorRefresh(setErrorUnauthorized());
        }

        AlertDialogUtils.show(activity);

        AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(BuildConfig.URL_SSO_AUTH),
                Uri.parse(BuildConfig.URL_SSO_TOKEN)
        );

        AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(Aplicacao.application.getApplicationContext());
        TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfig.SSO_CLIENT_ID);
        tokenBuilder.setRefreshToken(biometriaDTO.getRefresh());
        tokenBuilder.setGrantType("refresh_token");

        TokenRequest tokenRequest = tokenBuilder.build();

        authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
            if (response == null || response.accessToken == null || response.refreshToken == null) {
                listener.errorRefresh(setErrorUnauthorized());
            }
            DadosBiometriaBO.salvarBiometria(response.refreshToken);
            KeycloakBO_.getInstance_(Aplicacao.application.getApplicationContext()).setAccessToken(response.accessToken);
            KeycloakBO_.getInstance_(Aplicacao.application.getApplicationContext()).setRefreshToken(response.refreshToken);

            LoginSP.loginRealizado(true);
            String separar[] = response.accessToken.split(activity.getString(R.string.barra_barra_ponto));
            if (separar.length > 0) {
                String tokenCorpo = separar[1];
                // ** Verificar salvaDadosUsuario(response, tokenCorpo);
            }
            DadosUsuarioBO.updateUsuarioLogado(activity, true);
            if (AcessoBO.PRIMEIRO_LOGIN) {
                AcessoBO.PRIMEIRO_LOGIN = false;
            }

            final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(activity);
            DBLoteriasCrud crud = new DBLoteriasCrud(activity);
            List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
            dadosUsuarioBO.postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

                @Override
                public void onResponse(UsuarioLogadoResponse response) {
                    if (response.getRedirect() != null) {
                        listener.errorRefresh(setErrorUnauthorized());
                    }
                    SessaoUsuario.getInstance().setValorMaximoAposta(response.getPayload().getLimiteDiario());
                    if (response != null && response.getPayload() != null && response.getPayload().getLimiteDiario() != null) {
                        SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                    }
                    SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                    CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());
                    UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(response.getPayload().getUltimaNotificacao());

                    crud.deleteAll();

                    AlertDialogUtils.dismiss();
                    listener.successRefresh();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.errorRefresh(setErrorUnauthorized());
                }
            });
        });
    }

    private VolleyError setErrorUnauthorized() {
        AlertDialogUtils.dismiss();
        return new VolleyError(new NetworkResponse(401, null, false, 0L, null));
    }

}
