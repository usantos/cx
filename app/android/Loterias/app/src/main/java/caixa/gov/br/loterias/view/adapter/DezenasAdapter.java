package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by joafilho on 28/03/2018.
 * Class DezenasAdapter
 */

public class DezenasAdapter extends RecyclerView.Adapter<DezenasAdapter.ViewHolder> {

    private List<Integer> dezenas;

    public DezenasAdapter(List<Integer> dezenas) {
        this.dezenas = dezenas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View textView = new TextView(parent.getContext());
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String numero = ViewUtils.getNumberIntegerToString(dezenas.get(position));
        if (position == 0 || (position % 5 == 0)) {
            holder.dezenaView.setText(numero);
            holder.dezenaView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            holder.dezenaView.setText(String.format("-  %s", numero));
            holder.dezenaView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
    }

    @Override
    public int getItemCount() {
        return dezenas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dezenaView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.dezenaView = (TextView) itemView;
        }
    }
}
