package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class DetalheComboHolder extends LoteriasHolder<ModalidadeComboDTO>{

    private final TextView  txtQtdJogos, txtNomeModalidade, txtNumeroConcurso;
    private final ImageView backgroundTrevo;
    private final Activity parentActivity;

    public DetalheComboHolder(View itemView, Activity parentActivity) {
        super(itemView);
        this.parentActivity = parentActivity;

        txtQtdJogos = itemView.findViewById(R.id.txtQtdJogosDetalheCombo);
        txtNomeModalidade = itemView.findViewById(R.id.txtModalidadeDetalheCombo);
        txtNumeroConcurso = itemView.findViewById(R.id.txtConcursoDetalheCombo);
        backgroundTrevo = itemView.findViewById(R.id.imgBackgroundDetalheCombo);
    }

    @Override
    public void bind(ModalidadeComboDTO item, int position) {
        ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(item.getModalidade().getValor());
        if (modalidade == null) {
            modalidade = ModalidadeEnum.QUINA; // Coloca Quina para não dar erro se for null.
        }
        EstiloModalidadeMKP estiloModalidade = new EstiloModalidadeMKP(modalidade);

        txtQtdJogos.setTextColor(ContextCompat.getColor(parentActivity, estiloModalidade.getCorLetraLista()));

        String textoQtdJogos = item.getQuantidadeApostas() == 1 ? "1 jogo" : item.getQuantidadeApostas() + " jogos";
        txtQtdJogos.setText(textoQtdJogos);

        txtNomeModalidade.setTextColor(ContextCompat.getColor(parentActivity, estiloModalidade.getCorLetraLista()));

        if(item.getTipoConcurso() == ModalidadeComboDTO.TipoConcursoEnum.ESPECIAL) {
            txtNomeModalidade.setText(item.getModalidade().getDescricaoEspecial().toLowerCase());
        } else {
            txtNomeModalidade.setText(item.getModalidade().getDescricaoGestor().toLowerCase());
        }

        txtNumeroConcurso.setTextColor(ContextCompat.getColor(parentActivity, estiloModalidade.getCorLetraLista()));
        String textoConcurso = "";
        if(item.getQuantidadeTeimosinhas() != null && item.getQuantidadeTeimosinhas() == 0) {
            textoConcurso = item.getNumero() == null ? "concurso 0000" : "concurso " + item.getNumero().toString();
        }else if (item.getQuantidadeTeimosinhas() == null || item.getQuantidadeTeimosinhas() > 0) {
            int concursoFinal = item.getNumero() + item.getQuantidadeTeimosinhas();
            textoConcurso = item.getNumero() == null ? "concurso 0000" : "concurso " + item.getNumero().toString() + " a " + concursoFinal;
        }
        txtNumeroConcurso.setText(textoConcurso);
        backgroundTrevo.setImageDrawable(ContextCompat.getDrawable(parentActivity, estiloModalidade.getTrevoFundoDetalheCombo()));
    }
}


