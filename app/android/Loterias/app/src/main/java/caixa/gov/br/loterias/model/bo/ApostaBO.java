package caixa.gov.br.loterias.model.bo;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import caixa.gov.br.loterias.model.bean.ApostaPageRequest;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.RequestListenerHelper;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.IncluirSurpresinhaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.RapidaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.RapidaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.RetornoPadraoDTO;
import caixa.gov.br.loterias.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class ApostaBO extends BaseBO{

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

        RequestListenerHelper<CompletaJogoRsp> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.COMPLETA_JOGO_PATH, queryParams, CompletaJogoRsp.class
                , silceClient.volleyListener(helper.getSilceListener(), CompletaJogoRsp.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<AgrupadorDTOApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.INCLUIR_APOSTA_CARRINHO_PATH, new LinkedHashMap<String, String>(),
                aposta, AgrupadorDTOApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), AgrupadorDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void adicionarApostasNoCarrinho(final List<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                           final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<AgrupadorDTOApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.INCLUIR_APOSTAS_CARRINHO_PATH, new LinkedHashMap<String, String>(),
                apostas, AgrupadorDTOApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), AgrupadorDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<AgrupadorDTOApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.INCLUIR_SURPRESINHA_CARRINHO_PATH, new LinkedHashMap<String, String>(),
                aposta, AgrupadorDTOApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), AgrupadorDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<CarrinhoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.BUSCA_CARRINHO_PATH, new LinkedHashMap<String, String>(), CarrinhoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), CarrinhoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<CarrinhoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildDeleteRequest(SilceClient.DELETE_APOSTA_CARRINHO_PATH, id, CarrinhoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), CarrinhoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void obterEscudoTime(String id, final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        String url = SilceClient.EQUIPES_ESPORTIVAS_ESCUDO_PATH.replace("{id}", id);

        RequestListenerHelper<EscudoEquipeEsportivaDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(url, new LinkedHashMap<String, String>(), EscudoEquipeEsportivaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), EscudoEquipeEsportivaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    private static String join(List<Integer> collection, String delimiter) {
        StringBuffer result = new StringBuffer("");
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
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (quantidade > 0){
            queryParams.put("quantidade", String.valueOf(quantidade));
        }

        RequestListenerHelper<CarrinhoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.GERAR_APOSTAS_RAPIDAO_PATH, queryParams,
                "", CarrinhoDTOResponse.class, silceClient.volleyListener(helper.getSilceListener(), CarrinhoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getRapidoes(final RequestListener<RapidaoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<RapidaoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.RAPIDOES_PATH, new LinkedHashMap<String, String>(), RapidaoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), RapidaoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<ModalidadeDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.MODALIDADES_PATH, new LinkedHashMap<String, String>(), ModalidadeDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ModalidadeDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }


    public void postRapidoes(final RapidaoDTO rapidao,
                             final RequestListener<RapidaoDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<RapidaoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.RAPIDOES_PATH, new LinkedHashMap<String, String>(),
                rapidao, RapidaoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), RapidaoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        RequestListenerHelper<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.APOSTAS_FAVORITAS_LISTA_UNICA_PATH, queryParams, ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void postConferirBilhetes(final String codigoBilhete,
                                     final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        Map<String,String> params = new HashMap<String, String>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", " ");

        RequestListenerHelper<ResultadoConsultaBilheteDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.BILHETES_CONFERIR_BILHETE_PATH, new LinkedHashMap<String, String>(),
                params, ResultadoConsultaBilheteDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoConsultaBilheteDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getCompras(final int mes, final int ano, final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("5"));

        RequestListenerHelper<ResultadoPesquisaPaginadaDTOCompraDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.COMPRAS_PATH, queryParams, ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoPesquisaPaginadaDTOCompraDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        String url = SilceClient.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);

        RequestListenerHelper<AgrupadorApostaCompraDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(url,  new LinkedHashMap<String, String>(), AgrupadorApostaCompraDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), AgrupadorApostaCompraDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getMesesCompras(final RequestListener<MesAnoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<MesAnoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.COMPRAS_MESES_PATH,
                new LinkedHashMap<String, String>(),
                MesAnoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), MesAnoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<RetornoPadraoDTO> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildDeleteRequest(SilceClient.APOSTAS_FAVORITAS_PATH, idAposta, RetornoPadraoDTO.class,
                silceClient.volleyListener(helper.getSilceListener(), RetornoPadraoDTO.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void postApostasFavoritas(final ApostaFavoritaDTO apostaFavoritaDTO, final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<RetornoPadraoDTO> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildPostRequest(SilceClient.APOSTAS_FAVORITAS_PATH, new LinkedHashMap<String, String>(),
                apostaFavoritaDTO, RetornoPadraoDTO.class,
                silceClient.volleyListener(helper.getSilceListener(), RetornoPadraoDTO.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getDatasApostasConfirmadas(final RequestListener<MesAnoDTOResponse> listener) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<MesAnoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.APOSTAS_MES_ANO_PATH,
                new LinkedHashMap<String, String>(),
                MesAnoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), MesAnoDTOResponse.class),
                null, headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getDatasApostasHistorico(final RequestListener<MesAnoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<MesAnoDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.APOSTAS_HISTORICO_MES_ANO_PATH,
                new LinkedHashMap<String, String>(),
                MesAnoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), MesAnoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getApostasConfirmadas(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        final Map<String, String> queryParams = new HashMap<String, String>();
        final RequestListenerHelper<ResultadoPesquisaPaginadaDTOApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
        queryParams.put( "mes", String.valueOf(page.getMes()));
        queryParams.put("ano", String.valueOf(page.getAno()));
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));

        //TODO: pegar situação dinamicamente
        queryParams.put("situacao", "1");
//        queryParams.put("idsApostas", idsApostas);
//        queryParams.put("modalidade", "0");

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.APOSTAS_CONFIRMADAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoPesquisaPaginadaDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }

    public void getApostasHistorico(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put( "mes", String.valueOf(page.getMes()));
        queryParams.put("ano", String.valueOf(page.getAno()));
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));

        //TODO: pegar situação dinamicamente
        queryParams.put("situacao", "1");

        //TODO: utilizar paginação
//        queryParams.put("idsApostas", idsApostas);
//        queryParams.put("modalidade", modalidade);

        RequestListenerHelper<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        final GsonRequest request = silceClient.getConnection().buildGetRequest(SilceClient.APOSTAS_CONFIRMADAS_HISTORICO_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

        request.setErrorListener(silceClient.volleyErrorListener(interceptError(request, headers, listener)));
        silceClient.getConnection().request(request);
    }
}
