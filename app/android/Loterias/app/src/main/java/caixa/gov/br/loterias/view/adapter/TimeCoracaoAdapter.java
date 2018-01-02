package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;

/**
 * Created by ramluz on 27/12/2017.
 */

public class TimeCoracaoAdapter extends BaseAdapter {
    private Context context;
    private List<ParametroEquipe> times;


    public TimeCoracaoAdapter(Context context, List<ParametroEquipe> times) {
        this.context = context;
        this.times = times;
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

        imageTime.setImageResource(R.drawable.time);
        nameTime.setText(times.get(i).getNome());
        ufTime.setText(times.get(i).getUf());

        return convertView;
    }

    public List<ParametroEquipe> getTimes() {
        return times;
    }

    public void setTimes(List<ParametroEquipe> times) {
        this.times = times;
    }
}
