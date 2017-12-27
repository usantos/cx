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

/**
 * Created by ramluz on 27/12/2017.
 */

public class TimeCoracaoAdapter extends BaseAdapter {
    private Context context;
    private List<String> rows;

    private class ViewHolder {
        TextView textTimeNome;
        ImageView imageTime;

        public ViewHolder (View itemView){
            textTimeNome = itemView.findViewById(R.id.time_coracao_image);
            imageTime = itemView.findViewById(R.id.time_coracao_nome);
        }
    }

   public TimeCoracaoAdapter(Context context, List<String> times) {
        this.context = context;
        this.rows = times;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Object getItem(int i) {
        return rows.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        TimeCoracaoAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_time_coracao, null);
        }

        TextView textTimeNome = convertView.findViewById(R.id.time_coracao_nome);
        ImageView imageTime = convertView.findViewById(R.id.time_coracao_image);

        imageTime.setImageResource(R.drawable.time_acf);
        textTimeNome.setText(rows.get(i));

        return convertView;
    }
}
