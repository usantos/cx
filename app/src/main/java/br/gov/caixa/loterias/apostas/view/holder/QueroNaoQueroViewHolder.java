package br.gov.caixa.loterias.apostas.view.holder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class QueroNaoQueroViewHolder extends LoteriasHolder<Integer> implements View.OnClickListener {
    private final ConstraintLayout layoutDezena;
    private final TextView txtNumeroDezena;
    private final OnItemClickListener listener;

    public QueroNaoQueroViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        this.listener = listener;
        layoutDezena = itemView.findViewById(R.id.layout_excluir_num_quero_nao_quero);
        txtNumeroDezena = itemView.findViewById(R.id.txt_item_numero);

    }

    @Override
    public void bind(Integer dezena, int position) {

        try{
            String numero = dezena.toString();
            if (numero.length() == 1) {
                txtNumeroDezena.setText(String.format("0%s", numero));
            } else {
                txtNumeroDezena.setText(numero);
            }
        }catch (Exception e){
            Log.d("", Objects.requireNonNull(e.getLocalizedMessage()));
        }

        layoutDezena.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.itemClick(this, getAbsoluteAdapterPosition());
    }
}