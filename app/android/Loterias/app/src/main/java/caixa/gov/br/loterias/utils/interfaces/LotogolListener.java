package caixa.gov.br.loterias.utils.interfaces;

import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;

/**
 * Created by joafilho on 10/01/2018.
 */

public interface LotogolListener {
    void partidaSelecionada();

    void atualizarParametrosLotogol(ParametroPartida parametroPartida);
}
