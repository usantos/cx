package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by joafilho on 28/03/2018.
 * Class DezenasAdapter
 */

public class DezenasAdapter extends RecyclerView.Adapter<DezenasAdapter.ViewHolder> {

    private List<String> dezenas;

    public DezenasAdapter(List<String> dezenas) {
        this.dezenas = dezenas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_dezenas, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            holder.dezenaView.findViewById(R.id.lineDivisiveView).setVisibility(View.GONE);
        }
        ((TextView) holder.dezenaView.findViewById(R.id.lineDezenasTextView)).setText(ViewUtils.fromHtml(dezenas.get(position)));
    }

    @Override
    public int getItemCount() {
        return dezenas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View dezenaView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.dezenaView = itemView;
        }
    }
}
