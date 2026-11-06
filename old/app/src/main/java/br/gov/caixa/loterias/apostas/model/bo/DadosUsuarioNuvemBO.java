package br.gov.caixa.loterias.apostas.model.bo;

import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutenticacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

/**
 * Created by cedesbr450 on 07/12/17.
 */
public class DadosUsuarioNuvemBO extends NuvemBO implements DadosUsuarioRepository {

    private static DadosUsuarioNuvemBO instance;

    private DadosUsuarioNuvemBO(){
        super();
    }

    public static DadosUsuarioNuvemBO getInstance(){
        if (instance == null){
            instance= new DadosUsuarioNuvemBO();
        }
        return instance;
    }

    @Override
    public void postVerificarCadastro(final List<IdentificaoDeUmaApostaDas8Modalidades> listaApostas, final RequestListener<UsuarioLogadoResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setApostas(listaApostas);
        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.USUARIOS_VERIFICAR_CADASTRO_PATH, new LinkedHashMap<>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

}
