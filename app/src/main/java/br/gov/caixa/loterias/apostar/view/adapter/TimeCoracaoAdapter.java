package br.gov.caixa.loterias.apostar.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostar.utils.EscudoEquipeUtil;

/**
 * Created by ramluz on 27/12/2017.
 */

public class TimeCoracaoAdapter extends BaseAdapter {
    private Context context;
    private List<ParametroEquipe> times;
    private int bordaTimeSelecionado;
    private int bordaTimeDesselecionado;

    public TimeCoracaoAdapter(Context context, List<ParametroEquipe> times) {
        this.context = context;
        this.times = times;
        this.bordaTimeSelecionado = context.getResources().getColor(R.color.borderImageTimeColor);
        this.bordaTimeDesselecionado = context.getResources().getColor(R.color.cinza);
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public Object getItem(int i) {
        return times.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_time_coracao, null);
        }

        TextView nameTime = convertView.findViewById(R.id.timeCoracaoNome);
        TextView ufTime = convertView.findViewById(R.id.timeCoracaoUf);
        ImageView imageTime = convertView.findViewById(R.id.time_coracao_image);

        ParametroEquipe equipe = times.get(i);
        equipe.setBrasaoSelecionadoId(R.drawable.time_selecionado);
        equipe.setBrasaoId(R.drawable.time);

        EscudoEquipeUtil.setEscudo(context, times.get(i), imageTime);
        if (equipe.isSelecionado()) {
//            imageTime.setImageResource(equipe.getBrasaoSelecionadoId());

            nameTime.setTextColor(bordaTimeSelecionado);
            ufTime.setTextColor(bordaTimeSelecionado);
        } else {
//            imageTime.setImageResource(equipe.getBrasaoId());
            nameTime.setTextColor(bordaTimeDesselecionado);
            ufTime.setTextColor(bordaTimeDesselecionado);
        }

        nameTime.setText(equipe.getNome());
        ufTime.setText(equipe.getUf());

        return convertView;
    }

    public List<ParametroEquipe> getTimes() {
        return times;
    }

}
