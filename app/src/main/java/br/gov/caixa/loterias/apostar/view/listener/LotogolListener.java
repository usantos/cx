package br.gov.caixa.loterias.apostar.view.listener;

import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ParametroPartida;

/**
 * Created by joafilho on 10/01/2018.
 * Interface LotogolListener
 */

public interface LotogolListener {
    void partidaSelecionada();

    void atualizarParametrosLotogol(ParametroPartida parametroPartida);
}
