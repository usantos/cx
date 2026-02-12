package br.gov.caixa.loterias.apostas.model.bo;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoRespostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConsultaDadosChavePixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericasFavoritasResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.FiltroMarketPlace;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public class ApostaSilceBO extends SilceBO implements ApostaRepository {
    private static ApostaSilceBO instance;

    private ApostaSilceBO(){
        super();
    }

    public static ApostaSilceBO getInstance(){
        if (instance == null){
            instance = new ApostaSilceBO();
        }
        return instance;
    }

    //Carrinho Favorito
    @Override
    public void validarCarrinhoFavorito(String nomeCarrinho, RequestListener<CarrinhoFavoritoDTOResponse> listener){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("nome", nomeCarrinho);

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.CARRINHO_FAVORITO_VALIDAR_NOME,
                new LinkedHashMap<>(),
                params, CarrinhoFavoritoDTOResponse.class,
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

        final GsonRequest request = getServiceConnection().buildPostRequest(url,
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
    public void deletaApostaCarrinhoFavorito(final String idCarrinho,
                                             final String idAposta,
                                             final RequestListener<ListApostaCarrinhoFavoritoResponse> listener) {
        String url = ServerMethods.CARRINHO_FAVORITO_DELETAR_APOSTA.replace("{id}", idCarrinho);
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
    public void deletaCarrinhoFavorito(final String idCarrinho,
                                       final RequestListener<ListCarrinhoFavoritoResponse> listener) {
        String url = ServerMethods.CARRINHO_FAVORITO_DELETAR_CARRINHO.replace("{id}", idCarrinho);

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
    public void salvarCarrinhoFavorito(final String nomeCarrinho,
                                       final String idCompra,
                                       final Boolean mantemSurpresinhas,
                                       final RequestListener<CarrinhoFavoritoDTOResponse> listener) {

        Map<String, String> params = new HashMap<>();
        params.put("nome", nomeCarrinho);
        params.put("manterSurpresinhas", mantemSurpresinhas.toString());

        String url;
        if(idCompra != null){
            url = ServerMethods.CARRINHO_FAVORITO_SALVAR_COMPRA.replace("{idCompra}", idCompra);
        } else {
            url = ServerMethods.CARRINHO_FAVORITO_SALVAR;
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
    public void buscaCarrinhosFavoritos(RequestListener<ListCarrinhoFavoritoResponse> listener){
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.CARRINHO_FAVORITO_BUSCAR_CARRINHOS,

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
        String url = ServerMethods.CARRINHO_FAVORITO_BUSCA_APOSTAS.replace("{idCarrinho}", idCarrinho.toString());
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                ListApostaCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    //APOSTA
    public void getApostasConfirmadasSilce(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final Map<String, String> queryParams = getQueryParams(page);

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    private Map<String, String> getQueryParams(ApostaPageRequest page) {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        if(page.getAno() > 0){
            queryParams.put("ano", String.valueOf(page.getAno()));
        }
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));
//        if(page.getSituacao() != 0){
//            queryParams.put("situacao", String.valueOf(page.getSituacao()));
//        } else {
//            queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
//        }
        if(page.getSituacao() != 0) {
            if (page.getSituacao() == 1) {
                queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
            } else if (page.getSituacao() == 2) {
                queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.PAGAS));
            } else if (page.getSituacao() == 3) {
                queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.PRESCRITAS));
            }
        }
        if(page.getModalidade() != 0){
            queryParams.put("modalidade", String.valueOf(page.getModalidade()));
        }

        if(page.getOrdenarPor() != 0) {
            queryParams.put("ordenacao", String.valueOf(page.getOrdenarPor()));
        }

        if(page.getTipoAposta() != 0) {
            queryParams.put("tipoAposta", String.valueOf(page.getTipoAposta()));
        }
        if(page.getTipoConcurso() != 0) {
            queryParams.put("tipoConcurso", String.valueOf(page.getTipoConcurso()));
        }
        return queryParams;
    }

    public void getApostasHistorico(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        queryParams.put("ano", String.valueOf(page.getAno()));
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));
        queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));

        //        queryParams.put("idsApostas", idsApostas);
        //        queryParams.put("modalidade", modalidade);

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }


    public void postResgatarAposta(final Long idAposta,
                                   final InformacaoPagamentoDTO infoPgtoDTO,
                                   final RequestListener<RetornoPadraoResponse> listener) {

        String url = ServerMethods.APOSTAS_RESGATAR_PATH.replace("{idAposta}", String.valueOf( idAposta ));
        final GsonRequest request = getServiceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                infoPgtoDTO, RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void  getMercadoPagoToken(final String email, final RequestListener<StringResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("email", email);

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.TOKEN_CADASTRO_MP_PATH,
                queryParams,
                StringResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void  getRecargaPayToken(final RequestListener<StringResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.TOKEN_APOSTADOR_RP_PATH,
                queryParams,
                StringResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void buscaConfigConsultaApostasConfirmadas(final RequestListener<ConfigConsultaResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIG_CONSULTA,
                new LinkedHashMap<>(),
                ConfigConsultaResponse.class,
                listener.getSilceListener(),
                null,
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_FAVORITAS_LISTA_UNICA_PATH,
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

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_FAVORITAS_MODALIDADE_PATH,
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

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_FAVORITAS_PATH,
                queryParams,
                AgrupadorDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getApostaHistoricoComprovante(Long id, final RequestListener<DetalhesHistoricoPremioDTOResponse> listener) {
        String url = ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                DetalhesHistoricoPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }


    public void getApostaGerarCodigoResgate(Long id, final RequestListener<CodigoResgateDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_GERAR_CODIGO_RESGATE_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                CodigoResgateDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getApostaDetalhePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_DETALHES_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                DetalhesPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void baixarComprovanteAposta(Long id, final RequestListener<String> listener) {
        String endPoint = ServerMethods.APOSTAS_CONFIRMADAS_GERAR_COMPROVANTE_PATH.replace("{id}", id.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                BuildConfigManager.getVariavel("CAIXA_BASE_URL_SILCE") + endPoint, response -> {
            listener.onResponse(response);
        }, error -> {
            listener.onErrorResponse(error);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = (Map<String, String>) getHeaderToken().clone();
                header.put("Disable-Crypto", "true");
                return header;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    public void baixarComprovantePremio(Long id, final RequestListener<String> listener) {
        String endPoint = ServerMethods.APOSTAS_CONFIRMADAS_GERAR_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                BuildConfigManager.getVariavel("CAIXA_BASE_URL_SILCE") + endPoint, response -> {
            listener.onResponse(response);
        }, error -> {
            listener.onErrorResponse(error);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = (Map<String, String>) getHeaderToken().clone();
                header.put("Disable-Crypto", "true");
                return header;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    public void getApostaDetalheComprovante(Long id, final RequestListener<ComprovanteApostaDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_COMPROVANTE_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                ComprovanteApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }


    public void getApostaComprovantePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                DetalhesPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getApostaConferir(Long id, final RequestListener<DTOEnumIntegerResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFERIR_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                DTOEnumIntegerResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getApresentaHistorico(final RequestListener<ParametrosConfiguraveisDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_VERIFICA_APRESENTA_HISTORICO_PATH,
                new LinkedHashMap<>(),
                ParametrosConfiguraveisDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    //APOSTAS FAVORITAS
    @Override
    public void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("id", idAposta);
        final GsonRequest request = getServiceConnection().buildDeleteRequest(ServerMethods.APOSTAS_FAVORITAS_PATH + idAposta,
                queryParams,
                RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void postApostasFavoritas(final ApostaFavoritaDTO apostaFavoritaDTO, final RequestListener<RetornoPadraoResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.APOSTAS_FAVORITAS_PATH,
                new LinkedHashMap<>(),
                apostaFavoritaDTO, RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void postIncluirApostaFavoritaCarrinho(final Long idApostaFavorita,
                                                  final Integer valorTipoConcurso,
                                                  final Integer qtdTeimosinhas,
                                                  final Boolean espelho,
                                                  final RequestListener<CarrinhoDTOResponse> listener) {
        String url = ServerMethods.CARRINHOS_INCLUIR_APOSTA_FAVORITA_PATH.replace("{idApostaFavorita}", String.valueOf( idApostaFavorita ));
        url = url.replace("{valorTipoConcurso}", String.valueOf(valorTipoConcurso));
        url = url.replace("{qtdTeimosinhas}", String.valueOf(qtdTeimosinhas));

        final GsonRequest request = getServiceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                "",
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    //CARRINHO
    @Override
    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            //Incluido por causa do carrinho na NUVEM
            if (aposta != null) {
                aposta.setId(null);
            }
            final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.INCLUIR_APOSTA_CARRINHO_PATH,
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

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.INCLUIR_COMBO_CARRINHO_PATH,
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
    public void limparCarrinho(RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.LIMPAR_CARRINHO_PATH,
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

    @Override
    public void validarCarrinho(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.VALIDAR_CARRINHO,
                new LinkedHashMap<>(),
                ResourceResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void adicionarApostasNoCarrinho(final List<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                           final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.INCLUIR_APOSTAS_CARRINHO_PATH,
                new LinkedHashMap<>(),
                apostas, AgrupadorDTOApostaDTOResponse.class,
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
            final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.INCLUIR_SURPRESINHA_CARRINHO_PATH,
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
    public void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.BUSCA_CARRINHO_PATH,
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
    public void buscaCombo(final RequestListener<CombosDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.BUSCA_COMBOS_PATH,
            new LinkedHashMap<>(),
            CombosDTOResponse.class,
            listener.getSilceListener(),
            listener.getErrorListener(),
            getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void deleteComboCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildDeleteRequest(ServerMethods.DELETE_APOSTA_COMBO_CARRINHO_PATH + id,
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
    public void  deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getServiceConnection().buildDeleteRequest(ServerMethods.DELETE_APOSTA_CARRINHO_PATH + id,
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
    public void postRegistrarApostaCarrinhoAsync(final MeioPagamento meioPagamento, final RequestListener<CompraAsyncResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_ASYNC_PATH,
                new LinkedHashMap<>(),
                meioPagamento, CompraAsyncResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void verificaCompraProcessamento(final Long idCompraCarrinho, final RequestListener<CompraAsyncResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("idCompraCarrinho", String.valueOf(idCompraCarrinho));
        final GsonRequest request = getServiceConnection().buildGetRequest(
                ServerMethods.CARRINHO_VERIFICA_COMPRA_PROCESSAMENTO,
                queryParams,
                CompraAsyncResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    //RAPIDAO
    public void getRapidoes(final RequestListener<RapidaoDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.RAPIDOES_PATH,
                new LinkedHashMap<>(),
                RapidaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void postRapidoes(final RapidaoDTO rapidao,
                             final RequestListener<RapidaoDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.RAPIDOES_PATH,
                new LinkedHashMap<>(),
                rapidao, RapidaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }


    public void gerarApostas(final int quantidade, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (quantidade > 0) {
            queryParams.put("quantidade", String.valueOf(quantidade));
        }

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.GERAR_APOSTAS_RAPIDAO_PATH,
                queryParams,
                "",
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    //COMPRAS
    public void getCompras(final int mes, final int ano, final int offset, final int size,  final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf(size));

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.COMPRAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
    public void getComprasFiltradaMeioPagamentoSituacao(final int mes, final int ano, final int offset, final int size, Long meioPagamento, Long situacao,  final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf(size));
        if(meioPagamento !=null){
            queryParams.put("meio", String.valueOf(meioPagamento));
        }
        if(situacao != null){
            queryParams.put("situacao", String.valueOf(situacao));
        }

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.COMPRAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener) {
        String url = ServerMethods.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                AgrupadorApostaCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getMesesCompras(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.COMPRAS_MESES_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getSituacoesCompra(final Long idMeioPagamento, final RequestListener<SituacoesCompraResponse> listener){
        Map<String, String> params = new HashMap<>();
        if(idMeioPagamento != null){
            params.put("meioPagamento", idMeioPagamento.toString());
        }

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.SITUACOES_COMPRA_PATH,
                params,
                SituacoesCompraResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request,getHeader(),listener));
        getServiceConnection().request(request,listener);
    }
    //RESULTADOS
    public void getResultadoModalidade(final String modalidade, final String concurso, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        String url = ServerMethods.RESULTADOS_MODALIDADE_CONCURSO_PATH.replace("{modalidade}", modalidade);
        url = url.replace("{concurso}",concurso);

        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                ResultadoConcursoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getResultadoModalidadeConcurso(final String modalidade, final String concurso, final RequestListener<ResultadoConcursosDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("modalidade", String.valueOf(modalidade));
        queryParams.put("concursos", String.valueOf(concurso));

        String url = ServerMethods.RESULTADOS_MODALIDADE_CONCURSO;

        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                ResultadoConcursosDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getResultadoModalidade(final String modalidade, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        getResultadoModalidade( modalidade, "", listener);
    }

    @Override
    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.MODALIDADES_PATH,
                new LinkedHashMap<>(),
                ModalidadeDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void postConferirBilhetes(final String codigoBilhete,
                                     final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", "");

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.BILHETES_CONFERIR_BILHETE_PATH,
                new LinkedHashMap<>(),
                params,
                ResultadoConsultaBilheteDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void postConferirBilhetesNovo(final String codigoBilhete,
                                         final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", "");

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.BILHETES_CONFERIR_BILHETE_NOVO_PATH,
                new LinkedHashMap<>(),
                params,
                ResultadoConsultaBilheteDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void obterEscudoTime(String id, final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        String url = ServerMethods.EQUIPES_ESPORTIVAS_ESCUDO_PATH.replace("{id}", id);
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.EQUIPES_ESPORTIVAS_LISTA_EQUIPES_PATH,
                new LinkedHashMap<>(),
                EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void gerarCodigoPix(MeioPagamentoPix meioPagamento, final RequestListener<GerarPixResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.GERAR_CODIGO_PIX,
                new LinkedHashMap<>(),
                meioPagamento,
                GerarPixResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }




    private static String join(List<Integer> collection, String delimiter) {
        StringBuilder result = new StringBuilder("");

        for (Integer e : collection) {
            if (!(e.equals( collection.get(0)))) {
                result.append(delimiter);
            }
            result.append(e);

        }

        return result.toString();
    }

    private <T> boolean verificaAcessToken(final RequestListener<T> listener) {
        if (KeycloakBO.getInstance().getAccessToken() != null) {
            return true;
        }
        listener.onErrorResponse(new VolleyError(new NetworkResponse(401, null, false, 0L, null)));
        return false;
    }

    //LOTERICAS FAVORITAS

    public void getLotericasFavoritas(final RequestListener<LotericasFavoritasResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        final GsonRequest request = getServiceConnection().buildGetRequest(
                ServerMethods.LOTERICAS_FAVORITAS,
                queryParams,
                LotericasFavoritasResponse.class,
                response -> {
                    Log.d("HTTP-SUCCESS", "Status: 200 (OK)");
                    Log.d("HTTP-SUCCESS", "Response: " + new Gson().toJson(response));
                    listener.getSilceListener().onResponse(response);
                },

                error -> {
                    if (error.networkResponse != null) {
                        Log.d("HTTP-ERROR", "Status: " + error.networkResponse.statusCode);
                        String body = error.networkResponse.data != null
                                ? new String(error.networkResponse.data, StandardCharsets.UTF_8)
                                : "<sem body>";
                        Log.d("HTTP-ERROR", "Body: " + body);
                    } else {
                        Log.d("HTTP-ERROR", "Sem resposta do servidor (networkResponse == null)");
                    }

                    Log.d("HTTP-ERROR", "networkTimeMs: " + error.getNetworkTimeMs());

                    Log.d("HTTP-ERROR", "message: " + error.getMessage());
                    Throwable cause = error.getCause();
                    if (cause != null) {
                        Log.d("HTTP-ERROR", "cause class: " + cause.getClass().getName());
                        Log.d("HTTP-ERROR", "cause message: " + cause.getMessage());
                        Log.d("HTTP-ERROR", Log.getStackTraceString(cause));
                    } else {
                        Log.d("HTTP-ERROR", Log.getStackTraceString(error));
                    }

                    if (error.networkResponse != null && error.networkResponse.headers != null) {
                        Log.d("HTTP-ERROR", "headers: " + error.networkResponse.headers.toString());
                    }

                    listener.getErrorListener().onErrorResponse(error);
                }
                ,
                getHeaderToken()
        );

        getServiceConnection().request(request, listener);
    }

    // BOLAO
    public void getBoloesDisponiveis(FiltroMarketPlace filtro,
                                     final RequestListener<BoloesDisponiveisResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("tipoConsulta", String.valueOf(filtro.getTipoConsulta().getValor()));
        queryParams.put("idMunicipio", String.valueOf(filtro.getIdMunicipio()));
        queryParams.put("idUf", String.valueOf(filtro.getIdUf()));
        queryParams.put("pagina", String.valueOf(filtro.getPagina()));
        queryParams.put("qtdPorPagina", String.valueOf(filtro.getQtdPorPagina()));

        if (filtro.getValorMinimoCota() != null){
            queryParams.put("valorMinimoCota",String.valueOf(filtro.getValorMinimoCota()));
        }
        if (filtro.getValorMaximoCota() != null){
            queryParams.put("valorMaximoCota",String.valueOf(filtro.getValorMaximoCota()));
        }
        if (filtro.getQtdMinimaCota() > 0){
            queryParams.put("qtdMinimaCota",String.valueOf(filtro.getQtdMinimaCota()));
        }
        if (filtro.getQtdMaximaCota() > 0){
            queryParams.put("qtdMaximaCota",String.valueOf(filtro.getQtdMaximaCota()));
        }
        if(filtro.getQtdDezenas() > 0){
            queryParams.put("qtdDezenas", String.valueOf(filtro.getQtdDezenas()));
        }
        if(filtro.getNumerosQuero() != null && !filtro.getNumerosQuero().isEmpty()){
            queryParams.put("numerosEscolhidos", filtro.getNumerosQuero());
        }
        if(filtro.getNumerosNaoQuero() != null && !filtro.getNumerosNaoQuero().isEmpty()){
            queryParams.put("numerosNaoEscolhidos", filtro.getNumerosNaoQuero());
        }
        if (filtro.getNumeroLoterico() != null){
            queryParams.put("numeroLoterico",String.valueOf(filtro.getNumeroLoterico()));
        }
        if (filtro.getIdModalidade() != null){
            queryParams.put("idModalidade",String.valueOf(filtro.getIdModalidade()));
        }
        if (filtro.getTipoConcurso() != null){
            queryParams.put("tipoConcurso",String.valueOf(filtro.getTipoConcurso()));
        }

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.BOLOES_DISPONIVEIS,
                queryParams,
                BoloesDisponiveisResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getDetalhesBolao(String codigoBolao,
                                     final RequestListener<DetalheBolaoResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("idBolao", codigoBolao);


        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.DETALHE_BOLAO,
                queryParams,
                DetalheBolaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getAutoavaliacaoPerguntas(final RequestListener<AutoavaliacaoResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.AUTOAVALIACAO_PERGUNTAS,
                new LinkedHashMap<>(),
                AutoavaliacaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void postAutoavaliacaoRespostas(
                                   final List<AutoavaliacaoRespostaDTO> listAutoavaliacaoRespostaDTO,
                                   final RequestListener<AutoavaliacaoResponse> listener) {

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.AUTOAVALIACAO_RESPOSTAS,
                new LinkedHashMap<>(),
                listAutoavaliacaoRespostaDTO, AutoavaliacaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void postAutoSuspensaoApostador(
            final AutoSuspensaoRequest autoSuspensaoRequest,
            final RequestListener<AutoSuspensaoResponse> listener) {

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.AUTOSUSPENSAO_APOSTADOR,
                new LinkedHashMap<>(),
                autoSuspensaoRequest, AutoSuspensaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void consultaDadosChave(String id, InformacaoPagamentoDTO informacao,
                                 final RequestListener<ConsultaDadosChavePixResponse> listener) {
        String url = ServerMethods.CONSULTA_DADOS_CHAVE.replace("{id}", id);

        final GsonRequest request = getServiceConnection().buildPostRequest(url,
                new LinkedHashMap<>(), informacao,
                ConsultaDadosChavePixResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void cancelarPix(String idAposta, final RequestListener<ConsultaDadosChavePixResponse> listener) {
        String url = ServerMethods.CANCELA_RESGATE_PIX.replace("{id}", idAposta);

        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                ConsultaDadosChavePixResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void incluirLotericaFavorita(Long idLoterica, final RequestListener<RetornoPadraoResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("nuCd", String.valueOf(idLoterica));
        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.INCLUIR_LOTERICA_FAVORITA,
                queryParams,
                "",
                RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void excluirLotericaFavorita(Long idLoterica, final RequestListener<RetornoPadraoResponse> listener) {
        String url = ServerMethods.EXCLUIR_LOTERICA_FAVORITA.replace("{nuCd}", String.valueOf(idLoterica));

        final GsonRequest request = getServiceConnection().buildDeleteRequest(url,
                new LinkedHashMap<>(),
                RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }


}