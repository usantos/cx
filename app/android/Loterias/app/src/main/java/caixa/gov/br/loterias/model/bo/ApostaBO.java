package caixa.gov.br.loterias.model.bo;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.RequestListenerHelper;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.model.bo.silce.data.UsuarioLogadoSingleton;
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
import caixa.gov.br.loterias.model.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceSingleton;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class ApostaBO {

    private SilceClient silceClient;

    @Bean
    protected SilceSingleton silceSingleton;

    @RootContext
    protected void setContext(Context context){
        SilceConnection conn = SilceConnection.create(silceSingleton.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
        silceClient = SilceClient.create(conn);
    }

    public void completaJogo(final int quantidadeTotalNumeros,
                             final List<Integer> numerosMarcados,
                             final int menorPrognostico,
                             final int maiorPrognostico,
                             final SilceClient.Listener<CompletaJogoRsp> listener,
                             final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcados", join(numerosMarcados, ","));
        queryParams.put("menorPrognostico", String.valueOf(menorPrognostico));
        queryParams.put("maiorPrognostico", String.valueOf(maiorPrognostico));

        silceClient.getConnection().get(SilceClient.COMPLETA_JOGO_PATH, queryParams, CompletaJogoRsp.class
                , silceClient.volleyListener(listener, CompletaJogoRsp.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final SilceClient.Listener<AgrupadorDTOApostaDTOResponse> listener,
                                          final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);


        silceClient.getConnection().post(SilceClient.INCLUIR_APOSTA_CARRINHO_PATH, new LinkedHashMap<String, String>(),
                aposta, AgrupadorDTOApostaDTOResponse.class,
                silceClient.volleyListener(listener, AgrupadorDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void adicionarApostasNoCarrinho(final List<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                           final SilceClient.Listener<AgrupadorDTOApostaDTOResponse> listener,
                                           final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);


        silceClient.getConnection().post(SilceClient.INCLUIR_APOSTAS_CARRINHO_PATH, new LinkedHashMap<String, String>(),
                apostas, AgrupadorDTOApostaDTOResponse.class,
                silceClient.volleyListener(listener, AgrupadorDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final SilceClient.Listener<AgrupadorDTOApostaDTOResponse> listener,
                                               final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);


        silceClient.getConnection().post(SilceClient.INCLUIR_SURPRESINHA_CARRINHO_PATH, new LinkedHashMap<String, String>(),
                aposta, AgrupadorDTOApostaDTOResponse.class,
                silceClient.volleyListener(listener, AgrupadorDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void buscaCarrinho(final SilceClient.Listener<CarrinhoDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().get(SilceClient.BUSCA_CARRINHO_PATH, new LinkedHashMap<String, String>(), CarrinhoDTOResponse.class,
                silceClient.volleyListener(listener, CarrinhoDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void deleteApostaCarrinho(String id, final SilceClient.Listener<CarrinhoDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().delete(SilceClient.DELETE_APOSTA_CARRINHO_PATH, id, CarrinhoDTOResponse.class,
                silceClient.volleyListener(listener, CarrinhoDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void obterEscudoTime(String id, final SilceClient.Listener<EscudoEquipeEsportivaDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        String url = SilceClient.EQUIPES_ESPORTIVAS_ESCUDO_PATH.replace("{id}", id);

        silceClient.getConnection().get(url, new LinkedHashMap<String, String>(), EscudoEquipeEsportivaDTOResponse.class,
                silceClient.volleyListener(listener, EscudoEquipeEsportivaDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
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

    public void gerarApostas(final int quantidade, final SilceClient.Listener<CarrinhoDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (quantidade > 0){
            queryParams.put("quantidade", String.valueOf(quantidade));
        }
        silceClient.getConnection().post(SilceClient.GERAR_APOSTAS_RAPIDAO_PATH, queryParams,
                "", CarrinhoDTOResponse.class, silceClient.volleyListener(listener, CarrinhoDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void getRapidoes(final SilceClient.Listener<RapidaoDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().get(SilceClient.RAPIDOES_PATH, new LinkedHashMap<String, String>(), RapidaoDTOResponse.class,
                silceClient.volleyListener(listener, RapidaoDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void getModalidades(final SilceClient.Listener<ModalidadeDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().get(SilceClient.MODALIDADES_PATH, new LinkedHashMap<String, String>(), ModalidadeDTOResponse.class,
                silceClient.volleyListener(listener, ModalidadeDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
    }


    public void postRapidoes(final RapidaoDTO rapidao,
                             final SilceClient.Listener<RapidaoDTOResponse> listener,
                             final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().post(SilceClient.RAPIDOES_PATH, new LinkedHashMap<String, String>(),
                rapidao, RapidaoDTOResponse.class,
                silceClient.volleyListener(listener, RapidaoDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void getApostasFavoritas(final int offset, final SilceClient.Listener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        silceClient.getConnection().get(SilceClient.APOSTAS_FAVORITAS_LISTA_UNICA_PATH, queryParams, ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                silceClient.volleyListener(listener, ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class), silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void postConferirBilhetes(final String codigoBilhete,
                             final SilceClient.Listener<ResultadoConsultaBilheteDTOResponse> listener,
                             final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        Map<String,String> params = new HashMap<String, String>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", " ");


        silceClient.getConnection().post(SilceClient.BILHETES_CONFERIR_BILHETE_PATH, new LinkedHashMap<String, String>(),
                params, ResultadoConsultaBilheteDTOResponse.class,
                silceClient.volleyListener(listener, ResultadoConsultaBilheteDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void getCompras(final int mes, final int ano, final int offset, final SilceClient.Listener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("5"));

        silceClient.getConnection().get(SilceClient.COMPRAS_PATH, queryParams, ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                silceClient.volleyListener(listener, ResultadoPesquisaPaginadaDTOCompraDTOResponse.class),
                silceClient.volleyErrorListener(errorListerner), headers);

    }

    public void getDetalhesCompras(final String idSelecionado, final SilceClient.Listener<AgrupadorApostaCompraDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        String url = SilceClient.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);

        silceClient.getConnection().get(url,  new LinkedHashMap<String, String>(), AgrupadorApostaCompraDTOResponse.class,
                silceClient.volleyListener(listener, AgrupadorApostaCompraDTOResponse.class),
                silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void getMesesCompras(final SilceClient.Listener<MesAnoDTOResponse> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().get(SilceClient.COMPRAS_MESES_PATH,
                new LinkedHashMap<String, String>(),
                MesAnoDTOResponse.class,
                silceClient.volleyListener(listener, MesAnoDTOResponse.class),
                silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void deleteApostasFavoritas(final String idAposta, final SilceClient.Listener<RetornoPadraoDTO> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().delete(SilceClient.APOSTAS_FAVORITAS_PATH, idAposta, RetornoPadraoDTO.class,
                silceClient.volleyListener(listener, RetornoPadraoDTO.class), silceClient.volleyErrorListener(errorListerner), headers);
    }

    public void postApostasFavoritas(final ApostaFavoritaDTO apostaFavoritaDTO, final SilceClient.Listener<RetornoPadraoDTO> listener, final SilceClient.ErrorListener errorListerner) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().post(SilceClient.APOSTAS_FAVORITAS_PATH, new LinkedHashMap<String, String>(),
                apostaFavoritaDTO, RetornoPadraoDTO.class,
                silceClient.volleyListener(listener, RetornoPadraoDTO.class),
                silceClient.volleyErrorListener(errorListerner), headers);

    }

    public void getDatasApostasConfirmadas(final RequestListener<MesAnoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        RequestListenerHelper<MesAnoDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.APOSTAS_MES_ANO_PATH,
                new LinkedHashMap<String, String>(),
                MesAnoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), MesAnoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

    }

    public void getDatasApostasHistorico(final RequestListener<MesAnoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        RequestListenerHelper<MesAnoDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.APOSTAS_HISTORICO_MES_ANO_PATH,
                new LinkedHashMap<String, String>(),
                MesAnoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), MesAnoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

    }

    public void getApostasConfirmadas(int mes, int ano, final RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("modalidade", "0");
        queryParams.put( "mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));

        //TODO: pegar situação dinamicamente
        queryParams.put("situacao", "1");
        queryParams.put("offset", "0");
        queryParams.put("size", "10");
//        queryParams.put("idsApostas", idsApostas);

        RequestListenerHelper<ResultadoPesquisaPaginadaDTOApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.APOSTAS_CONFIRMADAS_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoPesquisaPaginadaDTOApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

    }

    public void getApostasHistorico(int mes, int ano, final RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        Map<String, String> queryParams = new HashMap<String, String>();
//        queryParams.put("modalidade", modalidade);
        queryParams.put( "mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
//        queryParams.put("situacao", situacao);
//        queryParams.put("offset", offset);
//        queryParams.put("size", size);
//        queryParams.put("idsApostas", idsApostas);

        RequestListenerHelper<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.APOSTAS_CONFIRMADAS_HISTORICO_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

    }
}
