package br.gov.caixa.loterias.apostas.model.bo;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

public class ApostaNuvemBO extends NuvemBO implements ApostaRepository {
    private static ApostaNuvemBO instance;

    private ApostaNuvemBO(){
        super();
    }

    public static ApostaNuvemBO getInstance(){
        if (instance == null){
            instance = new ApostaNuvemBO();
        }
        return instance;
    }

    @Override
    public void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(PUBLICO + ServerMethods.EQUIPES_ESPORTIVAS_LISTA_EQUIPES_PATH,
                new LinkedHashMap<>(),
                EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

//    @Override
//    public void gerarCodigoPix(MeioPagamentoPix meioPagamento, final RequestListener<GerarPixResponse> listener) {
//        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.GERAR_CODIGO_PIX,
//                new LinkedHashMap<>(),
//                meioPagamento,
//                GerarPixResponse.class,
//                listener.getSilceListener(),
//                listener.getErrorListener(),
//                getHeaderToken());
//
//        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
//        getServiceConnection().request(request, listener);
//    }

    @Override
    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(PUBLICO + ServerMethods.MODALIDADES_PATH,
                new LinkedHashMap<>(),
                ModalidadeDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.APOSTAS_FAVORITAS_LISTA_UNICA_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void getApostasFavoritasModalidades(final int modalidade, final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("modalidade", String.valueOf(modalidade));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.APOSTAS_FAVORITAS_MODALIDADE_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
    @Override
    public void getApostasFavoritasAgrupadas(final RequestListener<AgrupadorDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.APOSTAS_FAVORITAS_PATH,
                queryParams,
                AgrupadorDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
    @Override
    public void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        //queryParams.put("id", idAposta); //Ja passa na linha abaixo
        final GsonRequest request = getServiceConnection().buildDeleteRequest(LOGADO + ServerMethods.APOSTAS_FAVORITAS_PATH + idAposta,
                queryParams,
                RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void buscaCarrinhosFavoritos(RequestListener<ListCarrinhoFavoritoResponse> listener){
        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.CARRINHO_FAVORITO_BUSCAR_CARRINHOS,

                new LinkedHashMap<>(),
                ListCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void validarCarrinhoFavorito(String nomeCarrinho, RequestListener<CarrinhoFavoritoDTOResponse> listener){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("nome", nomeCarrinho);

        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.CARRINHO_FAVORITO_VALIDAR_NOME,
                new LinkedHashMap<>(),
                params, CarrinhoFavoritoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void deletaCarrinhoFavorito(final String idCarrinho,
                                       final RequestListener<ListCarrinhoFavoritoResponse> listener) {
        String url = LOGADO + ServerMethods.CARRINHO_FAVORITO_DELETAR_CARRINHO.replace("{id}", idCarrinho);

        final GsonRequest request = getServiceConnection().buildDeleteRequest(url ,
                new LinkedHashMap<>(),
                ListCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void buscaApostasCarrinhoFavorito(Long idCarrinho,RequestListener<ListApostaCarrinhoFavoritoResponse> listener){
        String url = LOGADO + ServerMethods.CARRINHO_FAVORITO_BUSCA_APOSTAS.replace("{idCarrinho}", idCarrinho.toString());
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                ListApostaCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void deletaApostaCarrinhoFavorito(final String idCarrinho,
                                             final String idAposta,
                                             final RequestListener<ListApostaCarrinhoFavoritoResponse> listener) {
        String url = LOGADO + ServerMethods.CARRINHO_FAVORITO_DELETAR_APOSTA.replace("{id}", idCarrinho);
        url = url.replace("{idAposta}",idAposta);

        final GsonRequest request = getServiceConnection().buildDeleteRequest(url ,
                new LinkedHashMap<>(),
                ListApostaCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void salvarCarrinhoFavorito(final String nomeCarrinho,
                                       final String idCompra,
                                       final Boolean mantemSurpresinhas,
                                       final RequestListener<CarrinhoFavoritoDTOResponse> listener) {

        Map<String, String> params = new HashMap<>();
        params.put("nome", nomeCarrinho);
        params.put("manterSurpresinhas", mantemSurpresinhas.toString());

        String url;
        if(idCompra != null){
            url = LOGADO + ServerMethods.CARRINHO_FAVORITO_SALVAR_COMPRA.replace("{idCompra}", idCompra);
        } else {
            url = LOGADO + ServerMethods.CARRINHO_FAVORITO_SALVAR;
        }

        final GsonRequest request = getServiceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                params,
                CarrinhoFavoritoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            //Incluido por causa do carrinho na NUVEM
            if (aposta != null) {
                aposta.setId(null);
            }
            final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.INCLUIR_APOSTA_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    aposta,
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeaderToken());

            request.setErrorListener(interceptError(request, getHeaderToken(), listener));
            getServiceConnection().request(request, listener);
        }
    }

    @Override
    public void adicionarComboNoCarrinho(final IncluirComboDTO incluirComboDTO,
                                         final RequestListener<CarrinhoDTOResponse> listener) {

        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.INCLUIR_COMBO_CARRINHO_PATH,
                new LinkedHashMap<>(),
                incluirComboDTO,
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.INCLUIR_SURPRESINHA_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    aposta,
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeaderToken());

            request.setErrorListener(interceptError(request, getHeaderToken(), listener));
            getServiceConnection().request(request, listener);
        }
    }

    @Override
    public void limparCarrinho(RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.LIMPAR_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    "",                    //null-Não funcionava no Servico Nuvem, ""-funciona nos 2
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeaderToken());

            request.setErrorListener(interceptError(request, getHeaderToken(), listener));
            getServiceConnection().request(request, listener);
        }
    }

//    @Override
//    public void postRegistrarApostaCarrinhoAsync(final MeioPagamento meioPagamento, final RequestListener<CompraAsyncResponse> listener) {
//        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_ASYNC_PATH,
//                new LinkedHashMap<>(),
//                meioPagamento, CompraAsyncResponse.class,
//                listener.getSilceListener(),
//                listener.getErrorListener(),
//                getHeaderToken());
//
//        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
//        getServiceConnection().request(request, listener);
//    }

    @Override
    public void validarCarrinho(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.VALIDAR_CARRINHO,
                new LinkedHashMap<>(),
                ResourceResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

//    @Override
//    public void verificaCompraProcessamento(final Long idCompraCarrinho, final RequestListener<CompraAsyncResponse> listener) {
//        Map<String, String> queryParams = new HashMap<>();
//        queryParams.put("idCompraCarrinho", String.valueOf(idCompraCarrinho));
//        final GsonRequest request = getServiceConnection().buildGetRequest(
//                LOGADO + ServerMethods.CARRINHO_VERIFICA_COMPRA_PROCESSAMENTO,
//                queryParams,
//                CompraAsyncResponse.class,
//                listener.getSilceListener(),
//                listener.getErrorListener(),
//                getHeaderToken());
//
//        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
//        getServiceConnection().request(request, listener);
//    }

    @Override
    public void  deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildDeleteRequest(LOGADO + ServerMethods.DELETE_APOSTA_CARRINHO_PATH + id,
                    new LinkedHashMap<>(),
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeaderToken());
            request.setErrorListener(interceptError(request, getHeaderToken(), listener));
            getServiceConnection().request(request, listener);
        }
    }

    @Override
    public void deleteComboCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildDeleteRequest(LOGADO + ServerMethods.DELETE_APOSTA_COMBO_CARRINHO_PATH + id,
                    new LinkedHashMap<>(),
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeaderToken());
            request.setErrorListener(interceptError(request, getHeader(), listener));
            getServiceConnection().request(request, listener);
        }
    }

    @Override
    public void postIncluirApostaFavoritaCarrinho(final Long idApostaFavorita,
                                                  final Integer valorTipoConcurso,
                                                  final Integer qtdTeimosinhas,
                                                  final Boolean espelho,
                                                  final RequestListener<CarrinhoDTOResponse> listener) {
        String url;
        Map<String, String> queryParams = new HashMap<>();
        if (espelho != null){
            url = ServerMethods.CARRINHOS_INCLUIR_APOSTA_FAVORITA;
            queryParams.put("idApostaFavorita", String.valueOf( idApostaFavorita ));
            queryParams.put("valorTipoConcurso", String.valueOf( valorTipoConcurso ));
            queryParams.put("qtdTeimosinhas", String.valueOf( qtdTeimosinhas ));
            queryParams.put("espelho", String.valueOf(espelho));
        } else {
            url = ServerMethods.CARRINHOS_INCLUIR_APOSTA_FAVORITA_PATH.replace("{idApostaFavorita}", String.valueOf( idApostaFavorita ));
            url = url.replace("{valorTipoConcurso}", String.valueOf(valorTipoConcurso));
            url = url.replace("{qtdTeimosinhas}", String.valueOf(qtdTeimosinhas));
        }

        final GsonRequest request = getServiceConnection().buildPostRequest( LOGADO + url,
                new LinkedHashMap<>(),
                queryParams,
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void transformarCarrinho(Long idCarrinhoFavorito, Integer opcaoSelecionada, RequestListener<CarrinhoDTOResponse> listener){
        String url = ServerMethods.CARRINHO_FAVORITO_TRANSFORMAR.replace("{idCarrinhoFavorito}", idCarrinhoFavorito.toString());
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("incluirEspecial", opcaoSelecionada.toString());

        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + url,
                new LinkedHashMap<>(),
                params,
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.BUSCA_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeaderToken());

            request.setErrorListener(interceptError(request, getHeaderToken(), listener));
            getServiceConnection().request(request, listener);
        }
    }

//    @Override
//    public void buscaCombo(final RequestListener<CombosDTOResponse> listener) {
//        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.BUSCA_COMBOS_PATH,
//                    new LinkedHashMap<>(),
//                    CombosDTOResponse.class,
//                    listener.getSilceListener(),
//                    listener.getErrorListener(),
//                    getHeaderToken());
//
//            request.setErrorListener(interceptError(request, getHeaderToken(), listener));
//            getServiceConnection().request(request, listener);
//    }

    private <T> boolean verificaAcessToken(final RequestListener<T> listener) {
        if (KeycloakBO.getInstance().getAccessToken() != null) {
            return true;
        }
        listener.onErrorResponse(new VolleyError(new NetworkResponse(401, null, false, 0L, null)));
        return false;
    }
}