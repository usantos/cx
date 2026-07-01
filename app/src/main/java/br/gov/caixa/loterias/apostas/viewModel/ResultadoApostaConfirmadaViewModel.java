package br.gov.caixa.loterias.apostas.viewModel;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.model.ItemLoteria;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ResultadoApostaConfirmadaViewModel extends ViewModel {

    private SessaoUsuario sessaoUsuario;

    private DetalhesPremioDTO detalhesPremio;

    private ComprovanteApostaDTO comprovanteAposta;

    private EstiloModalidadeMKP estiloModalidadeMKP;

    private ParametroSimulacao parametroSimulacao;

    private ModalidadeEnum modalidadeEnum;

    public ResultadoApostaConfirmadaViewModel(DetalhesPremioDTO detalhesPremio, ComprovanteApostaDTO comprovanteAposta, SessaoUsuario sessaoUsuario) {
        this.sessaoUsuario = sessaoUsuario;
        this.detalhesPremio = detalhesPremio;
        this.comprovanteAposta = comprovanteAposta;
        this.modalidadeEnum = detalhesPremio.getAposta().getModalidade();
        this.estiloModalidadeMKP = new EstiloModalidadeMKP(modalidadeEnum);
        this.parametroSimulacao = ViewUtils.getParametroSimulacao(this.sessaoUsuario, modalidadeEnum);
    }

    public SessaoUsuario getSessaoUsuario() {
        return sessaoUsuario;
    }

    public DetalhesPremioDTO getDetalhesPremio() {
        return detalhesPremio;
    }

    public ComprovanteApostaDTO getComprovanteAposta() {
        return comprovanteAposta;
    }

    public EstiloModalidadeMKP getEstiloModalidadeMKP() {
        return estiloModalidadeMKP;
    }

    public ParametroSimulacao getParametroSimulacao() {
        return parametroSimulacao;
    }

    public ModalidadeEnum getModalidadeEnum() {
        return modalidadeEnum;
    }

    public List<ItemLoteria> getListItemLoteria() {
        List<ItemLoteria> itens = new ArrayList<>();

        if (detalhesPremio.getPremio() != null && detalhesPremio.getPremio().getConcursos() != null) {

            // Agrupa faixas pelo número do concurso
            LinkedHashMap<String, List<FaixaPremiadaDTO>> agrupado = new LinkedHashMap<>();
            for (ConcursoPremiadoDTO concurso : detalhesPremio.getPremio().getConcursos()) {
                String numero = concurso.getConcurso().getNumero().toString();
                if (!agrupado.containsKey(numero)) {
                    agrupado.put(numero, new ArrayList<>());
                }
                agrupado.get(numero).addAll(concurso.getFaixas());
            }

            // Monta a lista achatada
            List<String> chaves = new ArrayList<>(agrupado.keySet());
            for (int i = 0; i < chaves.size(); i++) {
                String numero = chaves.get(i);

                if (detalhesPremio.getAposta().getQuantidadeTeimosinhas() > 0) {
                    itens.add(new ItemLoteria.Titulo("Concurso: " + numero));
                }

                for (FaixaPremiadaDTO faixa : agrupado.get(numero)) {
                    itens.add(new ItemLoteria.Corpo(
                            getAcertosNormalized(faixa),
                            ViewUtils.getMoedaFormat(faixa.getValorPremioCota())
                    ));
                }

                itens.add(new ItemLoteria.Divider());
            }
        }

        return itens;
    }

    private String getAcertosNormalized(FaixaPremiadaDTO faixa) {
        String posFixQtd = "";
        if (faixa.getQuantidadeAcertos() >= 2) {
            posFixQtd = " (" + faixa.getQuantidadeAcertos() + " vezes)";
        }

        String nfx = faixa.getDescricaoAcerto();
        if (nfx.contains("1º") || nfx.contains("2º")
                && detalhesPremio.getAposta().getModalidade() == ModalidadeEnum.LOTECA){
            nfx = nfx.replace("1º (", "")
                    .replace("2º (", "")
                    .replace(")", "");
        }

        if (detalhesPremio.getAposta().getModalidade() == ModalidadeEnum.DUPLA_SENA) {
            if (faixa.getSorteio() == 1) {
                nfx = "1º Sorteio - " + nfx;
            } else if (faixa.getSorteio() == 2) {
                nfx = "2º Sorteio - " + nfx;
            }
        }

        return nfx + posFixQtd;
    }

    public String getConcursoRange() {
        ConcursoPremiadoDTO primeiro = detalhesPremio.getPremio().getConcursos().get(0);
        ConcursoPremiadoDTO ultimo = detalhesPremio.getPremio().getConcursos().get(detalhesPremio.getPremio().getConcursos().size() -1);

        return primeiro.getConcurso().getNumero().toString() +  " - " + ultimo.getConcurso().getNumero().toString();
    }
}
