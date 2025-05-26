package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;

public interface ApostaService {
    void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener);
    void getModalidades(final RequestListener<ModalidadeDTOResponse> listener);
}
