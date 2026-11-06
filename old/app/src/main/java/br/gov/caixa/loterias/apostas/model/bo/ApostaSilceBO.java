package br.gov.caixa.loterias.apostas.model.bo;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoRespostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConsultaDadosChavePixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericasFavoritasResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.FiltroMarketPlace;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public class ApostaSilceBO extends SilceBO {
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

        if(page.isSurpresinha() != 0){
            queryParams.put("surpresinha", String.valueOf(page.isSurpresinha()));
        }

        if(page.isTeimosinha() != 0){
            queryParams.put("teimosinha", String.valueOf(page.isTeimosinha()));
        }

        if(page.isCombo() != 0){
            queryParams.put("combo", String.valueOf(page.isCombo()));
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

//    public void getComprasFiltradaMeioPagamentoSituacao(final int mes, final int ano, final int offset, final int size, Long meioPagamento, Long situacao,  final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
//        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
//
//        queryParams.put("mes", String.valueOf(mes));
//        queryParams.put("ano", String.valueOf(ano));
//        queryParams.put("offset", String.valueOf(offset));
//        queryParams.put("size", String.valueOf(size));
//        if(meioPagamento !=null){
//            queryParams.put("meio", String.valueOf(meioPagamento));
//        }
//        if(situacao != null){
//            queryParams.put("situacao", String.valueOf(situacao));
//        }
//
//        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.COMPRAS_PATH,
//                queryParams,
//                ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
//                listener.getSilceListener(),
//                listener.getErrorListener(),
//                getHeaderToken());
//
//        request.setErrorListener(interceptError(request, getHeader(), listener));
//        getServiceConnection().request(request, listener);
//    }

//    public void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener) {
//        String url = ServerMethods.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);
//        final GsonRequest request = getServiceConnection().buildGetRequest(url,
//                new LinkedHashMap<>(),
//                AgrupadorApostaCompraDTOResponse.class,
//                listener.getSilceListener(),
//                listener.getErrorListener(),
//                getHeaderToken());
//
//        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
//        getServiceConnection().request(request, listener);
//    }

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

    public void getResultadoModalidadeConcurso(final String descricaoModalidade, final Integer concursoInicial, final Integer concursoFinal, final RequestListener<ResultadoConcursosDTOResponse> listener) {
//        //Map não aceita parametros repetidos
//        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
//        String queryString = "modalidade=" + descricaoModalidade;
//        for (int i = concursoInicial; i <= concursoFinal; i++) {
//            queryString += "&concursos=" + i;
//        }
//        String url = ServerMethods.RESULTADOS_MODALIDADE_CONCURSO+"?"+queryString;

        List<Map.Entry<String, String>> queryParams = new ArrayList<>();
        queryParams.add(new AbstractMap.SimpleEntry<>("modalidade", descricaoModalidade));
        for (int i = concursoInicial; i <= concursoFinal; i++) {
            queryParams.add(new AbstractMap.SimpleEntry<>("concursos", String.valueOf(i)));
        }
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
            queryParams.put("quantidadeDezena", String.valueOf(filtro.getQtdDezenas()));
        }
        if(filtro.getQtdApostas() > 0){
            queryParams.put("quantidadeAposta", String.valueOf(filtro.getQtdApostas()));
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
