package br.gov.caixa.loterias.apostar.interno.utils.helper;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ApostasConfirmadasAdapter;

/**
 * Created by joafilho on 10/04/2018.
 * Class Helper ConferirResultadoHelper
 */


public interface ConferirResultadoHandler {
    void handle(DTOEnumLong situacao);
    void handle(ApostasConfirmadasAdapter.ViewHolder holder, DTOEnumLong situacao);
    void handleError(ApostasConfirmadasAdapter.ViewHolder holder);
}
