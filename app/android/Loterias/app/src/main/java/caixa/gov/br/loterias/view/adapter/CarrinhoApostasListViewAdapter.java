package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.Modalidade;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

public class CarrinhoApostasListViewAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;

    public CarrinhoApostasListViewAdapter(ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas, Context context){
        this.context = context;
        this.apostas = apostas;
    }


    static class ApostaHolder extends RecyclerView.ViewHolder{
        final TextView textViewTipoAposta;
        final TextView numerosAposta;
        final TextView valorAposta;

        ApostaHolder(View view){
            super(view);

            textViewTipoAposta = view.findViewById(R.id.tipoAposta);
            numerosAposta = view.findViewById(R.id.numerosText);
            valorAposta = view.findViewById(R.id.valorAposta);
        }
    }

    @Override
    public CarrinhoApostasListViewAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);

        return  new ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, int position) {
        EstiloModalidade estilo = new EstiloModalidade(apostas.get(position).getModalidade());

        holder.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.textViewTipoAposta.setText(ViewUtils.capitalized(ModalidadeEnum.fromString(apostas.get(position).getModalidade())));

        String numeroStr = "";
        if (apostas.get(position).getIndicadorSurpresinha().getValor() == 2) {
            for (int i = 0; i < apostas.get(position).getQuantidadeNumeros(); i++) {
                if (i == apostas.get(position).getQuantidadeNumeros() - 1) {
                    numeroStr += "XX";
                }else {
                    numeroStr += "XX - ";
                }
            }
        } else {
            for (Integer num: apostas.get(position).getNumerosSelecionados()) {
                if (num == (apostas.get(position).getNumerosSelecionados().get(apostas.get(position).getNumerosSelecionados().size() - 1))) {
                    numeroStr += String.format("%02d", num);
                }else {
                    numeroStr += String.format("%02d - ", num);
                }
            }
        }

        holder.numerosAposta.setText(numeroStr);
        holder.valorAposta.setText(caixa.gov.br.loterias.utils.ViewUtils.getMoedaFormat(apostas.get(position).getValor()));
    }


    @Override
    public int getItemCount() {
        return apostas.size();
    }

}