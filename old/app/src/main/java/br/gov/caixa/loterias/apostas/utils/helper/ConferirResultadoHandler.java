package br.gov.caixa.loterias.apostas.utils.helper;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;

/**
 * Created by joafilho on 10/04/2018.
 * Class Helper ConferirResultadoHelper
 */


public interface ConferirResultadoHandler {
    void handle(DTOEnumLong situacao);
    void handle(int position, ApostaConfirmadaHolder holder, DTOEnumLong situacao);
    void handleError(ApostaConfirmadaHolder holder, VolleyError error);
}
