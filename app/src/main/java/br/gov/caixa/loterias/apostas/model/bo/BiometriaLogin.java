package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.net.Uri;
import android.util.Base64;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AuthorizationServiceFactory;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

public class BiometriaLogin {

    public void loginBiometria(Activity activity, final OnRefreshTokenListener listener) {

        BiometriaDTO biometriaDTO = DadosBiometriaBO.obterBiometria();
        if (biometriaDTO == null || biometriaDTO.getRefresh() == null) {
            listener.errorRefresh(setErrorUnauthorized());
            return;
        }

        AlertDialogUtils.show(activity);

        AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(BuildConfigManager.getVariavel("URL_SSO_AUTH")),
                Uri.parse(BuildConfigManager.getVariavel("URL_SSO_TOKEN"))
        );

        AuthorizationService authorizationService = AuthorizationServiceFactory.getAuthorizationService(Aplicacao.application.getApplicationContext());
        TokenRequest.Builder tokenBuilder = new TokenRequest.Builder(authorizationServiceConfiguration, BuildConfigManager.getVariavel("SSO_CLIENT_ID"));
        tokenBuilder.setRefreshToken(biometriaDTO.getRefresh());
        tokenBuilder.setGrantType("refresh_token");

        TokenRequest tokenRequest = tokenBuilder.build();

        authorizationService.performTokenRequest(tokenRequest, (response, ex) -> {
            if (response == null || response.accessToken == null || response.refreshToken == null) {
                listener.errorRefresh(setErrorUnauthorized());
                return;
            }
            //Mudado pra dentro salvaUsuario
            //if (BiometriaBO.getInstance().getBiometriaHabilitada()) {
            //    DadosBiometriaBO.salvarBiometria(response.refreshToken);
            //}
            KeycloakBO.getInstance().setAccessToken(response.accessToken);
            KeycloakBO.getInstance().setRefreshToken(response.refreshToken);

            LoginSP.loginRealizado(true);
            String separar[] = response.accessToken.split(activity.getString(R.string.barra_barra_ponto));
            if (separar.length > 0) {
                String tokenCorpo = separar[1];
                if (! salvaDadosUsuario(activity, response, tokenCorpo)) {
                    listener.errorRefresh(setErrorUnauthorized());
                    return;
                }
            }
            DadosUsuarioBO.updateUsuarioLogado(activity, true);
            if (AcessoBO.PRIMEIRO_LOGIN) {
                AcessoBO.PRIMEIRO_LOGIN = false;
            }


            DadosUsuarioBO.getInstance().getVerificaUsuarioBloqueado(new RequestListener<RetornoPadraoResponse>() {
                @Override
                public void onResponse(RetornoPadraoResponse response) {
                    verificarCadastro(activity, listener);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (AlertDialogUtils.isShow()){
                        AlertDialogUtils.dismiss();
                    }
                    RedirectNetwork.trataFalhaVerificacaoBloqueio(error, activity, "BiometriaLogin");
                }
            });

        });
    }

    private void verificarCadastro(Activity activity, OnRefreshTokenListener listener) {
        DBLoteriasCrud crud = new DBLoteriasCrud(activity);
        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();

        ServicoFactoryUtil.getDadosUsuarioService().postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                if (response.getRedirect() != null) {
                    listener.errorRefresh(setErrorUnauthorized());
                    return;
                }
                SessaoUsuario.getInstance().setValorMaximoAposta(response.getPayload().getLimiteDiario());
                if (response != null && response.getPayload() != null && response.getPayload().getLimiteDiario() != null) {
                    SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                }
                //SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                SessaoUsuario.getInstance().setResponderAutoavaliacao(response.getPayload().getResponderAutoavaliacao());
                SessaoUsuario.getInstance().setSuspensaoTemporariaApostador(response.getPayload().getSuspensaoTemporariaApostador());
                SessaoUsuario.getInstance().setSituacaoApostador(response.getPayload().getSituacaoApostador());

                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());
                UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(response.getPayload().getUltimaNotificacao());
                UltimaNotificacaoSingleton.getInstance().setPagamentoNaoIdentificado(response.getPayload().getPagamentoNaoIdentificado());

                crud.deleteAll();

                AlertDialogUtils.dismiss();
                listener.successRefresh();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.errorRefresh(setErrorUnauthorized());
            }
        });
    }

    private VolleyError setErrorUnauthorized() {
        AlertDialogUtils.dismiss();
        return new VolleyError(new NetworkResponse(401, null, false, 0L, null));
    }

    private Boolean salvaDadosUsuario(Activity activity, TokenResponse response, String tokenCorpo) {
        byte bytes[] = Base64.decode(tokenCorpo, Base64.URL_SAFE);
        tokenCorpo = new String(bytes, Charset.forName(activity.getString(R.string.utf_8)));
        try {
            JSONObject jsonObject = new JSONObject(tokenCorpo);

            String cpf = jsonObject.getString(activity.getString(R.string.cpf_minusculo));
            if (cpf.length() < 11) {
                StringBuilder zeros = new StringBuilder();
                int totalIncluirZero = 11 - cpf.length();
                for (int contador = 0; contador < totalIncluirZero; contador++) {
                    zeros.append(activity.getString(R.string.zero));
                }
                zeros.append(cpf);
                cpf = zeros.toString();
            }

            DadosUsuarioBO.salvarUsuario(jsonObject.getString(activity.getString(R.string.name)), cpf);
            if (BiometriaBO.getInstance().getBiometriaHabilitada()) {
                DadosBiometriaBO.salvarBiometria(response.refreshToken, jsonObject.getString(activity.getString(R.string.name)), cpf);
            }

            AppCenterManager.registraEvento(activity.getString(R.string.evento_efetuou_login_sucesso));
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

}
