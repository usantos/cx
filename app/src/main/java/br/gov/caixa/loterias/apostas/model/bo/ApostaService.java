package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;

public interface ApostaService {
    void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener);
    void getModalidades(final RequestListener<ModalidadeDTOResponse> listener);
    void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener);
    void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoResponse> listener);
}
