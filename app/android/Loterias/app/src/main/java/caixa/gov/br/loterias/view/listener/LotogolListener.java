package caixa.gov.br.loterias.view.listener;

import caixa.gov.br.loterias.model.bo.silce.dto.ParametroPartida;

/**
 * Created by joafilho on 10/01/2018.
 * Interface LotogolListener
 */

public interface LotogolListener {
    void partidaSelecionada();

    void atualizarParametrosLotogol(ParametroPartida parametroPartida);
}
