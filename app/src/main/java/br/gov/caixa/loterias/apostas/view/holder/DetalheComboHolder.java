package br.gov.caixa.loterias.apostas.view.holder;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.CustomTypefaceSpan;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;

import org.jetbrains.annotations.NotNull;

public class DetalheComboHolder extends RecyclerView.ViewHolder {

    private final TextView  txtQtdJogos, txtNomeModalidade, txtNumeroConcurso;
    private final ImageView backgroundTrevo;

    public DetalheComboHolder(View itemView) {
        super(itemView);

        txtQtdJogos = itemView.findViewById(R.id.txtQtdJogosDetalheCombo);
        txtNomeModalidade = itemView.findViewById(R.id.txtModalidadeDetalheCombo);
        txtNumeroConcurso = itemView.findViewById(R.id.txtConcursoDetalheCombo);
        backgroundTrevo = itemView.findViewById(R.id.imgBackgroundDetalheCombo);
    }

    public void bind(final ModalidadeComboDTO item) {
        ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(item.getModalidade().getValor());
        if (modalidade == null) {
            modalidade = ModalidadeEnum.QUINA; // Coloca Quina para não dar erro se for null.
        }
        EstiloModalidadeMKP estiloModalidade = new EstiloModalidadeMKP(modalidade);

        txtQtdJogos.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidade.getCorLetraLista()));

        String textoQtdJogos = item.getQuantidadeApostas() == 1 ? "1 jogo" : item.getQuantidadeApostas() + " jogos";
        txtQtdJogos.setText(textoQtdJogos);

        txtNomeModalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidade.getCorLetraLista()));

        Typeface semiBoldTypeface = ResourcesCompat.getFont(itemView.getContext(), R.font.caixa_std_semi_bold);

        if(item.getTipoConcurso().toString().equalsIgnoreCase("ESPECIAL")) {
            txtNomeModalidade.setTypeface(semiBoldTypeface);
            if(EspecialUtils.isMega30(item.getNumero(), item.getTipoConcurso())){
                txtNomeModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, ""));
            } else {
                txtNomeModalidade.setText(item.getModalidade().getDescricaoEspecial().toLowerCase());
            }
        } else {
            if(ModalidadeEnum.fromModalidadeDTO(item.getModalidade()) == ModalidadeEnum.LOTOMANIA) {
                Typeface regularTypeface = ResourcesCompat.getFont(itemView.getContext(), R.font.caixa_std_regular);

                SpannableString spannable = getLabelLotomaniaEspelho(item, semiBoldTypeface, regularTypeface);
                txtNomeModalidade.setText(spannable);

            }else{
                txtNomeModalidade.setTypeface(semiBoldTypeface);
                txtNomeModalidade.setText(item.getModalidade().getDescricaoGestor().toLowerCase());
            }
        }

        txtNumeroConcurso.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidade.getCorLetraLista()));
        String textoConcurso = getLabelConcurso(item);
        txtNumeroConcurso.setText(textoConcurso);
        backgroundTrevo.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), estiloModalidade.getTrevoFundoDetalheCombo()));
    }

    private static @NotNull String getLabelConcurso(ModalidadeComboDTO item) {
        String textoConcurso = "";
        if(item.getQuantidadeTeimosinhas() != null && item.getQuantidadeTeimosinhas() == 0) {
            textoConcurso = item.getNumero() == null ? "concurso 0000" : "concurso " + item.getNumero().toString();
        }else if (item.getQuantidadeTeimosinhas() == null || item.getQuantidadeTeimosinhas() > 0) {
            int concursoFinal = item.getNumero() + (item.getQuantidadeTeimosinhas() - 1);
            textoConcurso = item.getNumero() == null ? "concurso 0000" : "concurso " + item.getNumero().toString() + " a " + concursoFinal;
        }
        return textoConcurso;
    }

    private static @NotNull SpannableString getLabelLotomaniaEspelho(ModalidadeComboDTO item, Typeface semiBoldTypeface, Typeface regularTypeface) {
        TypefaceSpan semiBold = new CustomTypefaceSpan(null, semiBoldTypeface);
        TypefaceSpan regular = new CustomTypefaceSpan(null, regularTypeface);

        String descricaoGestor = item.getModalidade().getDescricaoGestor();
        String textoCompleto = descricaoGestor + " + espelho";

        SpannableString spannable = new SpannableString(textoCompleto);

        spannable.setSpan(semiBold, 0, descricaoGestor.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(regular, descricaoGestor.length(), textoCompleto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}


