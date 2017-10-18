package caixa.gov.br.loterias.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ColorEnum;

public class CarrinhoApostasListViewAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<String> valoresList;

    public CarrinhoApostasListViewAdapter(ArrayList<String> valoresList, Context context){
        this.context = context;
        this.valoresList = valoresList;
    }


    public static class ApostaHolder extends RecyclerView.ViewHolder{
        final TextView textViewTipoAposta;
        final TextView numerosAposta;
        final TextView valorAposta;

        public ApostaHolder(View view){
            super(view);

            textViewTipoAposta = view.findViewById(R.id.tipoAposta);
            numerosAposta = view.findViewById(R.id.numerosText);
            valorAposta = view.findViewById(R.id.valorAposta);
        }
    }

    @Override
    public CarrinhoApostasListViewAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);

        ApostaHolder holder = new ApostaHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, int position) {
        holder.textViewTipoAposta.setTextColor(Color.parseColor(ColorEnum.MEGASENACLARO.toString()));
        holder.valorAposta.setTextColor(Color.parseColor(ColorEnum.MEGASENACLARO.toString()));
        holder.textViewTipoAposta.setText("Mega-sena\nConc. 433");
        holder.numerosAposta.setText("01  02  03  04  05  06  07  08  09  10  11  12  13  14  15");
        holder.valorAposta.setText("R$ 10.000,00");
    }

    @Override
    public int getItemCount() {
        return valoresList.size();
    }

}