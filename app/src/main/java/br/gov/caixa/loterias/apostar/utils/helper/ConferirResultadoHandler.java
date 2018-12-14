package br.gov.caixa.loterias.apostar.utils.helper;

import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DTOEnumLong;

/**
 * Created by joafilho on 10/04/2018.
 * Class Helper ConferirResultadoHelper
 */


public interface ConferirResultadoHandler {
    public void handle(DTOEnumLong situacao);
}
