package br.gov.caixa.loterias.apostas.model.bo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

public class ComprasBffBO extends BffBO implements ComprasRepository {
    private static ComprasBffBO instance;

    private ComprasBffBO(){
        super();
    }

    public static ComprasBffBO getInstance(){
        if (instance == null){
            instance = new ComprasBffBO();
        }
        return instance;
    }

    @Override
    public void postRegistrarApostaCarrinhoAsync(final MeioPagamento meioPagamento, final RequestListener<CompraAsyncResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_ASYNC_PATH,
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
        final GsonRequest request = getServiceConnection().buildGetRequest(LOGADO + ServerMethods.CARRINHO_VERIFICA_COMPRA_PROCESSAMENTO,
                queryParams,
                CompraAsyncResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void gerarCodigoPix(MeioPagamentoPix meioPagamento, final RequestListener<GerarPixResponse> listener) {
        final GsonRequest request = getServiceConnection().buildPostRequest(LOGADO + ServerMethods.GERAR_CODIGO_PIX,
                new LinkedHashMap<>(),
                meioPagamento,
                GerarPixResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

}