package caixa.gov.br.loterias.model.bo;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.HashMap;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.internals.GsonRequest;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceSingleton;

/**
 * Created by pmotta on 21/03/2018.
 */

@EBean
public class BaseBO {

    protected SilceClient silceClient;

    @Bean
    protected SilceSingleton silceSingleton;

    @Bean
    protected KeycloakBO keycloakBO;

    @AfterInject
    protected void init(){
        SilceConnection conn = SilceConnection.create(silceSingleton.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
        silceClient = SilceClient.create(conn);
    }

    protected <T> SilceClient.ErrorListener interceptError(final GsonRequest request, final HashMap<String, String> headers, final RequestListener<T> listener){
        return new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                //Caso não esteja autorizado tenta atualizar o token de acesso
                if(MensagensNetwork.isUnauthorizedError(error)){
                    keycloakBO.refreshAccess(new KeycloakBO.OnRefreshAccess() {
                        @Override
                        public void onRefreshAccess(String token) {
                            //Se retornar o token, tenta fazer a mesma requisição
                            if(token != null){
                                //Atualiza o token com os headers
                                headers.put("Authorization", "Bearer " + token);
                                request.setHeaders(headers);
                                //Refaz a conexão
                                silceClient.getConnection().request(request);
                            }else{
                                listener.onErrorResponse(error);
                            }
                        }
                    });
                }else{
                    listener.onErrorResponse(error);
                }
            }
        };
    }
}
