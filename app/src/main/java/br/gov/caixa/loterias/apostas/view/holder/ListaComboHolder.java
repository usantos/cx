package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.onComboClickListener;


public class ListaComboHolder extends LoteriasHolder<CombosDTO>{

    private final ImageView imgTrevoHeader,imgTarjaTrevo;
    private final TextView txtNomeCombo, txtValorCombo;
    private final Activity parentActivity;
    private final ConstraintLayout cardCombo;
    private final onComboClickListener listener;

    public ListaComboHolder(View itemView, Activity parentActivity, onComboClickListener listener) {
        super(itemView);
        this.parentActivity = parentActivity;
        this.listener = listener;

        imgTarjaTrevo = itemView.findViewById(R.id.imgTarjaTrevo);
        imgTrevoHeader = itemView.findViewById(R.id.img_trevo_lista_combo);
        txtNomeCombo = itemView.findViewById(R.id.txtNomeCombo);
        txtValorCombo = itemView.findViewById(R.id.txtValorCombo);
        cardCombo = itemView.findViewById(R.id.layout_card_combo);

    }

    @Override
    public void bind(CombosDTO item, int position) {
        boolean isMega30 = false;
        if (item.getModalidadesCombo() != null && !item.getModalidadesCombo().isEmpty()){
            isMega30 = EspecialUtils.isMega30(item.getModalidadesCombo().get(0).getNumero(), item.getModalidadesCombo().get(0).getTipoConcurso());
        }

        ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(item.getModalidadesCombo().get(0).getModalidade().getValor());
        if (modalidade == null) {
            modalidade = ModalidadeEnum.QUINA; // Coloca Quina para não dar erro se for null.
        }
        EstiloModalidadeMKP estiloModalidade = new EstiloModalidadeMKP(modalidade, isMega30);
        if (item.getTipoCombo().getCodigo().longValue() == CombosDTO.TipoComboEnum.ESPECIAL.getValue()) {
            imgTrevoHeader.setBackground(ContextCompat.getDrawable(parentActivity, estiloModalidade.getTrevoFundoEscuro()));
            imgTarjaTrevo.setBackground(ContextCompat.getDrawable(parentActivity, estiloModalidade.getTarjaEspecialTrevoCombo()));
            txtNomeCombo.setTextColor(ContextCompat.getColor(parentActivity, estiloModalidade.getCorLetraLista()));
            if (isMega30){
                txtNomeCombo.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, ""));
            } else {
                txtNomeCombo.setText(item.getModalidadesCombo().get(0).getModalidade().getDescricaoEspecial());
            }

        } else {
            txtNomeCombo.setTextColor(ContextCompat.getColor(parentActivity, R.color.azul_combo));
            txtNomeCombo.setText(item.getTipoCombo().getNome());
        }

        txtValorCombo.setText(item.getValor() != null ? ViewUtils.getMoedaFormat(item.getValor())  : "R$ 0,00");

        cardCombo.setOnClickListener(v -> {
            if(listener != null){
                listener.onComboClick(item);
            }
        });
    }
}

