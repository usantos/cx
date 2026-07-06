package br.gov.caixa.loterias.apostas.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.viewModel.ResultadoApostaConfirmadaViewModel;

public class ResultadoApostaConfirmadaFactory implements ViewModelProvider.Factory {

    private final DetalhesPremioDTO detalhesPremio;
    private final ComprovanteApostaDTO comprovanteAposta;

    private final SessaoUsuario sessaoUsuario;

    public ResultadoApostaConfirmadaFactory(DetalhesPremioDTO detalhesPremio, ComprovanteApostaDTO comprovanteAposta, SessaoUsuario sessaoUsuario) {
        this.detalhesPremio = detalhesPremio;
        this.comprovanteAposta = comprovanteAposta;
        this.sessaoUsuario = sessaoUsuario;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ResultadoApostaConfirmadaViewModel.class)) {
            return (T) new ResultadoApostaConfirmadaViewModel(detalhesPremio, comprovanteAposta, sessaoUsuario);
        }
        throw new IllegalArgumentException("ViewModel desconhecida");
    }
}
