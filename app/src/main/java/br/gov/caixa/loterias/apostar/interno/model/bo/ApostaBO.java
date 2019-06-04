package br.gov.caixa.loterias.apostar.interno.model.bo;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostar.interno.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostar.interno.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.data.CompletaJogoRsp;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class ApostaBO extends BaseBO {

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

        silceConnection.get(ServerMethods.COMPLETA_JOGO_PATH, queryParams,
                CompletaJogoRsp.class,
                listener.getSilceListener(),
                listener.getErrorListener(), new LinkedHashMap<>());
    }

    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_APOSTA_CARRINHO_PATH, new LinkedHashMap<>(),
                aposta, AgrupadorDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void validarCarrinho(final RequestListener<ResourceResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.VALIDAR_CARRINHO, new LinkedHashMap<>(), ResourceResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }


    public void adicionarApostasNoCarrinho(final List<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                           final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_APOSTAS_CARRINHO_PATH, new LinkedHashMap<>(),
                apostas, AgrupadorDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_SURPRESINHA_CARRINHO_PATH, new LinkedHashMap<>(),
                aposta, AgrupadorDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.BUSCA_CARRINHO_PATH, new LinkedHashMap<>(), CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildDeleteRequest(ServerMethods.DELETE_APOSTA_CARRINHO_PATH, id, CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void obterEscudoTime(String id, final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        String url = ServerMethods.EQUIPES_ESPORTIVAS_ESCUDO_PATH.replace("{id}", id);

        final GsonRequest request = silceConnection.buildGetRequest(url, new LinkedHashMap<>(), EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    private static String join(List<Integer> collection, String delimiter) {
        StringBuilder result = new StringBuilder("");
        boolean first = false;

        for (Integer e : collection) {
            if (!(e == collection.get(0))) {
                result.append(delimiter);
            }
            result.append(e);
        }

        return result.toString();
    }

    public void gerarApostas(final int quantidade, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (quantidade > 0) {
            queryParams.put("quantidade", String.valueOf(quantidade));
        }

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.GERAR_APOSTAS_RAPIDAO_PATH, queryParams,
                "", CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getRapidoes(final RequestListener<RapidaoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.RAPIDOES_PATH, new LinkedHashMap<>(), RapidaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.MODALIDADES_PATH, new LinkedHashMap<>(), ModalidadeDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void postRapidoes(final RapidaoDTO rapidao,
                             final RequestListener<RapidaoDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.RAPIDOES_PATH, new LinkedHashMap<>(),
                rapidao, RapidaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_FAVORITAS_LISTA_UNICA_PATH, queryParams, ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void postConferirBilhetes(final String codigoBilhete,
                                     final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        Map<String, String> params = new HashMap<>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", "");

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.BILHETES_CONFERIR_BILHETE_PATH, new LinkedHashMap<>(),
                params, ResultadoConsultaBilheteDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getCompras(final int mes, final int ano, final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("5"));

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.COMPRAS_PATH, queryParams, ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        String url = ServerMethods.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);

        final GsonRequest request = silceConnection.buildGetRequest(url, new LinkedHashMap<>(), AgrupadorApostaCompraDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getMesesCompras(final RequestListener<MesAnoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.COMPRAS_MESES_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildDeleteRequest(ServerMethods.APOSTAS_FAVORITAS_PATH, idAposta, RetornoPadraoDTO.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void postApostasFavoritas(final ApostaFavoritaDTO apostaFavoritaDTO, final RequestListener<Boolean> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.APOSTAS_FAVORITAS_PATH, new LinkedHashMap<>(),
                apostaFavoritaDTO, Boolean.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getDatasApostasConfirmadas(final RequestListener<MesAnoDTOResponse> listener) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_MES_ANO_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                null, headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getDatasApostasHistorico(final RequestListener<MesAnoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_HISTORICO_MES_ANO_PATH,
                new LinkedHashMap<>(),
                MesAnoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostasConfirmadas(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
        queryParams.put("mes", String.valueOf(page.getMes()));
        queryParams.put("ano", String.valueOf(page.getAno()));
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));

        //TODO: pegar situação dinamicamente
        queryParams.put("situacao", "1");
//        queryParams.put("idsApostas", idsApostas);
//        queryParams.put("modalidade", "0");

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostasHistorico(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        queryParams.put("ano", String.valueOf(page.getAno()));
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));

        //TODO: pegar situação dinamicamente
        queryParams.put("situacao", "1");

        //TODO: utilizar paginação
//        queryParams.put("idsApostas", idsApostas);
//        queryParams.put("modalidade", modalidade);

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void limparCarrinho(RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.LIMPAR_CARRINHO_PATH, new LinkedHashMap<>(),
                null, CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getResultadoModalidadeConcurso(final String modalidade, final int concurso, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        Map<String, String> queryParams = new HashMap<>();

        String url = ServerMethods.RESULTADOS_MODALIDADE_CONCURSO_PATH.replace("{modalidade}", modalidade);
        url = url.replace("{concurso}", String.valueOf(concurso));

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                ResultadoConcursoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getResultadoModalidade(final String modalidade, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        Map<String, String> queryParams = new HashMap<>();

        String url = ServerMethods.RESULTADOS_MODALIDADE_UTLIMO.replace("{modalidade}", modalidade);

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                ResultadoConcursoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostaDetalhePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_DETALHES_PATH.replace("{id}", id.toString());
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                DetalhesPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostaDetalheComprovante(Long id, final RequestListener<ComprovanteApostaDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_COMPROVANTE_PATH.replace("{id}", id.toString());
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                ComprovanteApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }


    public void getApostaComprovantePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                DetalhesPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostaConferir(Long id, final RequestListener<DTOEnumIntegerResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFERIR_PATH.replace("{id}", id.toString());
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                DTOEnumIntegerResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }


    public void postRegistrarApostaCarrinho(final MeioPagamento meioPagamento, final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_PATH, new LinkedHashMap<>(),
                meioPagamento, RetornoPadraoDTO.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostaGerarCodigoResgate(Long id, final RequestListener<CodigoResgateDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_GERAR_CODIGO_RESGATE_PATH.replace("{id}", id.toString());
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                CodigoResgateDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void postIncluirApostaFavoritaCarrinho(final Long idApostaFavorita, final Integer valorTipoConcurso, final Integer qtdTeimosinhas, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        String url = ServerMethods.CARRINHOS_INCLUIR_APOSTA_FAVORITA_PATH.replace("{idApostaFavorita}", String.valueOf( idApostaFavorita ));
        url = url.replace("{valorTipoConcurso}", String.valueOf(valorTipoConcurso));
        url = url.replace("{qtdTeimosinhas}", String.valueOf(qtdTeimosinhas));

        final GsonRequest request = silceConnection.buildPostRequest(url, new LinkedHashMap<>(),
                "", CarrinhoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void getApostaHistoricoComprovante(Long id, final RequestListener<DetalhesHistoricoPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final Map<String, String> queryParams = new HashMap<>();

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildGetRequest(url,
                queryParams,
                DetalhesHistoricoPremioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void postResgatarAposta(final Long idAposta, final InformacaoPagamentoDTO informacaoPagamentoDTO, final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        String url = ServerMethods.APOSTAS_RESGATAR_PATH.replace("{idAposta}", String.valueOf( idAposta ));

        final GsonRequest request = silceConnection.buildPostRequest(url, new LinkedHashMap<>(),
                informacaoPagamentoDTO, RetornoPadraoDTO.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

}
