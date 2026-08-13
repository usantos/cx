package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;

public interface ComprasLegadoRepository {

    void getComprasFiltradaMeioPagamentoSituacao(final int mes, final int ano, final int offset, final int size, Long meioPagamento, Long situacao,  final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener);
    void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener);

}
