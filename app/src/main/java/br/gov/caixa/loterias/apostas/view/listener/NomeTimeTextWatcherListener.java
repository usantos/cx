package br.gov.caixa.loterias.apostas.view.listener;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;

public interface NomeTimeTextWatcherListener {

  void isValido(boolean isValid);
  void limparFiltro();

  void addFiltro(List<ParametroEquipe> listaFriltrada);

}
