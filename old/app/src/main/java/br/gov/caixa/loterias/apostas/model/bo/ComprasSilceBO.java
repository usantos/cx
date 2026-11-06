package br.gov.caixa.loterias.apostas.model.bo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

public class ComprasSilceBO extends SilceBO implements ComprasLegadoRepository {
    private static ComprasSilceBO instance;

    private ComprasSilceBO(){
        super();
    }

    public static ComprasSilceBO getInstance(){
        if (instance == null){
            instance = new ComprasSilceBO();
        }
        return instance;
    }

    @Override
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

    @Override
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

}