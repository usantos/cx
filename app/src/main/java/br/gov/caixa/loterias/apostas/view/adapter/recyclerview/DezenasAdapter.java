package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

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
        String numero = dezenas.get(position);
        if (numero.contains("100")){
            numero = numero.replace("100", "00");
        }
        ((TextView) holder.dezenaView.findViewById(R.id.lineDezenasTextView)).setText(ViewUtils.fromHtml(numero));
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
