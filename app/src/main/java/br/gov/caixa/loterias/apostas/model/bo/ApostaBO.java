package br.gov.caixa.loterias.apostas.model.bo;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoNovoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoRespostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
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

public class ApostaBO extends BaseBO {
    private static ApostaBO instance;

    private ApostaBO(){}

    public static ApostaBO getInstance(){
        if (instance == null){
            instance = new ApostaBO();
        }
        return instance;
    }

    //Carrinho Favorito
    public void validarCarrinhoFavorito(String nomeCarrinho, RequestListener<CarrinhoFavoritoDTOResponse> listener){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("nome", nomeCarrinho);

        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.CARRINHO_FAVORITO_VALIDAR_NOME,
                new LinkedHashMap<>(),
                params, CarrinhoFavoritoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void transformarCarrinho(Long idCarrinhoFavorito, Integer opcaoSelecionada, RequestListener<CarrinhoDTOResponse> listener){
        String url = ServerMethods.CARRINHO_FAVORITO_TRANSFORMAR.replace("{idCarrinhoFavorito}", idCarrinhoFavorito.toString());
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("incluirEspecial", opcaoSelecionada.toString());

        final GsonRequest request = getSilceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                params,
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }



    public void deletaApostaCarrinhoFavorito(final String idCarrinho,
                                             final String idAposta,
                                             final RequestListener<ListApostaCarrinhoFavoritoResponse> listener) {
        String url = ServerMethods.CARRINHO_FAVORITO_DELETAR_APOSTA.replace("{id}", idCarrinho);
        url = url.replace("{idAposta}",idAposta);

        final GsonRequest request = getSilceConnection().buildDeleteRequest(url ,
                new LinkedHashMap<>(),
                ListApostaCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void deletaCarrinhoFavorito(final String idCarrinho,
                                       final RequestListener<ListCarrinhoFavoritoResponse> listener) {
        String url = ServerMethods.CARRINHO_FAVORITO_DELETAR_CARRINHO.replace("{id}", idCarrinho);

        final GsonRequest request = getSilceConnection().buildDeleteRequest(url ,
                new LinkedHashMap<>(),
                ListCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void salvarCarrinhoFavorito(final String nomeCarrinho,
                                       final RequestListener<CarrinhoFavoritoDTOResponse> listener) {
        salvarCarrinhoFavorito(nomeCarrinho,null,true, listener);
    }

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


        final GsonRequest request = getSilceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                params,
                CarrinhoFavoritoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void buscaCarrinhosFavoritos(RequestListener<ListCarrinhoFavoritoResponse> listener){
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.CARRINHO_FAVORITO_BUSCAR_CARRINHOS,

                new LinkedHashMap<>(),
                ListCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void buscaApostasCarrinhoFavorito(Long idCarrinho,RequestListener<ListApostaCarrinhoFavoritoResponse> listener){
        String url = ServerMethods.CARRINHO_FAVORITO_BUSCA_APOSTAS.replace("{idCarrinho}", idCarrinho.toString());
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                ListApostaCarrinhoFavoritoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void completaJogo(final int quantidadeTotalNumeros,
                             final List<Integer> numerosMarcados,
                             final int menorPrognostico,
                             final int maiorPrognostico,
                             final RequestListener<CompletaJogoRsp> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcados", join(numerosMarcados, ","));
        queryParams.put("menorPrognostico", String.valueOf(menorPrognostico));
        queryParams.put("maiorPrognostico", String.valueOf(maiorPrognostico));

        getSilceConnection().get(ServerMethods.COMPLETA_JOGO_PATH,
                queryParams,
                CompletaJogoRsp.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                new LinkedHashMap<>(), listener);
    }

    public void completaJogoNovo(final int quantidadeTotalNumeros,
                                 final ArrayList<ArrayList<Integer>> numerosMarcados,
                                 final String modalidade,
                                 final RequestListener<CompletaJogoNovoRsp> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        String url = ServerMethods.COMPLETA_JOGO_NOVO_PATH.replace("{modalidade}",modalidade);
        Gson gsonNumeros = new Gson();
        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcadosMatriz",gsonNumeros.toJson(numerosMarcados));

        getSilceConnection().get(url,
                queryParams,
                CompletaJogoNovoRsp.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                new LinkedHashMap<>(), listener);
    }

    //APOSTA
    public void getApostasConfirmadasSilce(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final Map<String, String> queryParams = getQueryParams(page);

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    private Map<String, String> getQueryParams(ApostaPageRequest page) {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        if(page.getAno() > 0){
            queryParams.put("ano", String.valueOf(page.getAno()));
        }
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));
        if(page.getSituacao() != 0){
            queryParams.put("situacao", String.valueOf(page.getSituacao()));
        } else {
            queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
        }
        if(page.getModalidade() != 0){
            queryParams.put("modalidade", String.valueOf(page.getModalidade()));
        }

        if(page.getOrdenarPor() != 0) {
            queryParams.put("ordenacao", String.valueOf(page.getOrdenarPor()));
        }
        //queryParams.put("situacao", String.valueOf(page.getSituacao()));

        //        queryParams.put("idsApostas", idsApostas);
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

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void postResgatarAposta(final Long idAposta,
                                   final InformacaoPagamentoDTO infoPgtoDTO,
                                   final RequestListener<RetornoPadraoResponse> listener) {

        String url = ServerMethods.APOSTAS_RESGATAR_PATH.replace("{idAposta}", String.valueOf( idAposta ));
        final GsonRequest request = getSilceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                infoPgtoDTO, RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void  getMercadoPagoToken(final String email, final RequestListener<StringResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("email", email);

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.TOKEN_CADASTRO_MP_PATH,
                queryParams,
                StringResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void  getRecargaPayToken(final RequestListener<StringResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.TOKEN_APOSTADOR_RP_PATH,
                queryParams,
                StringResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void getDatasApostasConfirmadas(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.APOSTAS_MES_ANO_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                null,
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getDatasApostasHistorico(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.APOSTAS_HISTORICO_MES_ANO_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.APOSTAS_FAVORITAS_LISTA_UNICA_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void getApostaHistoricoComprovante(Long id, final RequestListener<DetalhesHistoricoPremioDTOResponse> listener) {
        String url = ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                DetalhesHistoricoPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void getApostaGerarCodigoResgate(Long id, final RequestListener<CodigoResgateDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_GERAR_CODIGO_RESGATE_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                CodigoResgateDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getApostaDetalhePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_DETALHES_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                DetalhesPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getApostaDetalheComprovante(Long id, final RequestListener<ComprovanteApostaDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_COMPROVANTE_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                ComprovanteApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void getApostaComprovantePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                DetalhesPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getApostaConferir(Long id, final RequestListener<DTOEnumIntegerResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFERIR_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                DTOEnumIntegerResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getApresentaHistorico(final RequestListener<ParametrosConfiguraveisDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.APOSTAS_VERIFICA_APRESENTA_HISTORICO_PATH,
                new LinkedHashMap<>(),
                ParametrosConfiguraveisDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    //APOSTAS FAVORITAS
    public void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("id", idAposta);
        final GsonRequest request = getSilceConnection().buildDeleteRequest(ServerMethods.APOSTAS_FAVORITAS_PATH + idAposta,
                queryParams,
                RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postApostasFavoritas(final ApostaFavoritaDTO apostaFavoritaDTO, final RequestListener<RetornoPadraoResponse> listener) {
        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.APOSTAS_FAVORITAS_PATH,
                new LinkedHashMap<>(),
                apostaFavoritaDTO, RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postIncluirApostaFavoritaCarrinho(final Long idApostaFavorita,
                                                  final Integer valorTipoConcurso,
                                                  final Integer qtdTeimosinhas,
                                                  final RequestListener<CarrinhoDTOResponse> listener) {
        String url = ServerMethods.CARRINHOS_INCLUIR_APOSTA_FAVORITA_PATH.replace("{idApostaFavorita}", String.valueOf( idApostaFavorita ));
        url = url.replace("{valorTipoConcurso}", String.valueOf(valorTipoConcurso));
        url = url.replace("{qtdTeimosinhas}", String.valueOf(qtdTeimosinhas));

        final GsonRequest request = getSilceConnection().buildPostRequest(url,
                new LinkedHashMap<>(),
                "",
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    //CARRINHO
    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.INCLUIR_APOSTA_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    aposta,
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeader());

            request.setErrorListener(interceptError(request, getHeader(), listener));
            getSilceConnection().request(request, listener);
        }
    }

    public void limparCarrinho(RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.LIMPAR_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    null,
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeader());

            request.setErrorListener(interceptError(request, getHeader(), listener));
            getSilceConnection().request(request, listener);
        }
    }

    public void validarCarrinho(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.VALIDAR_CARRINHO,
                new LinkedHashMap<>(),
                ResourceResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void adicionarApostasNoCarrinho(final List<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                           final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.INCLUIR_APOSTAS_CARRINHO_PATH,
                new LinkedHashMap<>(),
                apostas, AgrupadorDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.INCLUIR_SURPRESINHA_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    aposta,
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeader());

            request.setErrorListener(interceptError(request, getHeader(), listener));
            getSilceConnection().request(request, listener);
        }
    }

    public void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.BUSCA_CARRINHO_PATH,
                    new LinkedHashMap<>(),
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeader());

            request.setErrorListener(interceptError(request, getHeader(), listener));
            getSilceConnection().request(request, listener);
        }
    }

    public void deleteComboCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getSilceConnection().buildDeleteRequest(ServerMethods.DELETE_APOSTA_COMBO_CARRINHO_PATH + id,
                    new LinkedHashMap<>(),
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeader());
            request.setErrorListener(interceptError(request, getHeader(), listener));
            getSilceConnection().request(request, listener);
        }
    }
    public void  deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        if (verificaAcessToken(listener)) {
            final GsonRequest request = getSilceConnection().buildDeleteRequest(ServerMethods.DELETE_APOSTA_CARRINHO_PATH + id,
                    new LinkedHashMap<>(),
                    CarrinhoDTOResponse.class,
                    listener.getSilceListener(),
                    listener.getErrorListener(),
                    getHeader());
            request.setErrorListener(interceptError(request, getHeader(), listener));
            getSilceConnection().request(request, listener);
        }
    }

    public void postRegistrarApostaCarrinho(final MeioPagamento meioPagamento, final RequestListener<RetornoPadraoResponse> listener) {
        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_PATH,

                new LinkedHashMap<>(),
                meioPagamento, RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postRegistrarApostaCarrinhoAsync(final MeioPagamento meioPagamento, final RequestListener<CompraAsyncResponse> listener) {
        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_ASYNC_PATH,
                new LinkedHashMap<>(),
                meioPagamento, CompraAsyncResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void verificaCompraProcessamento(final Long idCompraCarrinho, final RequestListener<CompraAsyncResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("idCompraCarrinho", String.valueOf(idCompraCarrinho));
        final GsonRequest request = getSilceConnection().buildGetRequest(
                ServerMethods.CARRINHO_VERIFICA_COMPRA_PROCESSAMENTO,
                queryParams,
                CompraAsyncResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    //RAPIDAO
    public void getRapidoes(final RequestListener<RapidaoDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.RAPIDOES_PATH,
                new LinkedHashMap<>(),
                RapidaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postRapidoes(final RapidaoDTO rapidao,
                             final RequestListener<RapidaoDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.RAPIDOES_PATH,
                new LinkedHashMap<>(),
                rapidao, RapidaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }


    public void gerarApostas(final int quantidade, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (quantidade > 0) {
            queryParams.put("quantidade", String.valueOf(quantidade));
        }

        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.GERAR_APOSTAS_RAPIDAO_PATH,
                queryParams,
                "",
                CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    //COMPRAS
    public void getCompras(final int mes, final int ano, final int offset, final int size,  final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf(size));

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.COMPRAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
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

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.COMPRAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener) {
        String url = ServerMethods.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                AgrupadorApostaCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getMesesCompras(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.COMPRAS_MESES_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getSituacoesCompra(final Long idMeioPagamento, final RequestListener<SituacoesCompraResponse> listener){
        Map<String, String> params = new HashMap<>();
        if(idMeioPagamento != null){
            params.put("meioPagamento", idMeioPagamento.toString());
        }

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.SITUACOES_COMPRA_PATH,
                params,
                SituacoesCompraResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request,getHeader(),listener));
        getSilceConnection().request(request,listener);
    }
    //RESULTADOS
    public void getResultadoModalidade(final String modalidade, final String concurso, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        String url = ServerMethods.RESULTADOS_MODALIDADE_CONCURSO_PATH.replace("{modalidade}", modalidade);
        url = url.replace("{concurso}",concurso);

        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                queryParams,
                ResultadoConcursoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getResultadoModalidade(final String modalidade, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        getResultadoModalidade( modalidade, "", listener);
    }

    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.MODALIDADES_PATH,
                new LinkedHashMap<>(),
                ModalidadeDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postConferirBilhetes(final String codigoBilhete,
                                     final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", "");

        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.BILHETES_CONFERIR_BILHETE_PATH,
                new LinkedHashMap<>(),
                params,
                ResultadoConsultaBilheteDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getSilceConnection().request(request, listener);
    }

    public void postConferirBilhetesNovo(final String codigoBilhete,
                                         final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", "");

        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.BILHETES_CONFERIR_BILHETE_NOVO_PATH,
                new LinkedHashMap<>(),
                params,
                ResultadoConsultaBilheteDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getSilceConnection().request(request, listener);
    }

    public void obterEscudoTime(String id, final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        String url = ServerMethods.EQUIPES_ESPORTIVAS_ESCUDO_PATH.replace("{id}", id);
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                new LinkedHashMap<>(),
                EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.EQUIPES_ESPORTIVAS_LISTA_EQUIPES_PATH,
                new LinkedHashMap<>(),
                EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void gerarCodigoPix(MeioPagamentoPix meioPagamento, final RequestListener<GerarPixResponse> listener) {
        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.GERAR_CODIGO_PIX,
                new LinkedHashMap<>(),
                meioPagamento,
                GerarPixResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
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

        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.BOLOES_DISPONIVEIS,
                queryParams,
                BoloesDisponiveisResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getDetalhesBolao(String codigoBolao,
                                     final RequestListener<DetalheBolaoResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("idBolao", codigoBolao);


        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.DETALHE_BOLAO,
                queryParams,
                DetalheBolaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void getAutoavaliacaoPerguntas(final RequestListener<AutoavaliacaoResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.AUTOAVALIACAO_PERGUNTAS,
                new LinkedHashMap<>(),
                AutoavaliacaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postAutoavaliacaoRespostas(
                                   final List<AutoavaliacaoRespostaDTO> listAutoavaliacaoRespostaDTO,
                                   final RequestListener<AutoavaliacaoResponse> listener) {

        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.AUTOAVALIACAO_RESPOSTAS,
                new LinkedHashMap<>(),
                listAutoavaliacaoRespostaDTO, AutoavaliacaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void postAutoSuspensaoApostador(
            final AutoSuspensaoRequest autoSuspensaoRequest,
            final RequestListener<AutoSuspensaoResponse> listener) {

        final GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.AUTOSUSPENSAO_APOSTADOR,
                new LinkedHashMap<>(),
                autoSuspensaoRequest, AutoSuspensaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }
}