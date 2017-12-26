package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.mock.TimesLotecaMock;

/**
 * Created by joafilho on 26/12/2017.
 */

public class LotecaAdapter extends RecyclerView.Adapter<LotecaAdapter.LotecaHolder> {

    private Context context;
    private List<TimesLotecaMock> listaTimes;

    public LotecaAdapter(Context context, List<TimesLotecaMock> listaTimes) {
        this.context = context;
        this.listaTimes = listaTimes;
    }

    @Override
    public LotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_time_loteca, parent, false);
        return new LotecaHolder(v);
    }

    @Override
    public void onBindViewHolder(LotecaHolder holder, int position) {

        TimesLotecaMock timesLotecaMock = listaTimes.get(position);

        if (position % 2 == 0) {
            ((ImageView) holder.layoutTimeUm.findViewById(R.id.item_image)).setImageResource(timesLotecaMock.getImage());
            ((TextView) holder.layoutTimeUm.findViewById(R.id.item_title)).setText(timesLotecaMock.getTitle());
            ((TextView) holder.layoutTimeUm.findViewById(R.id.item_description)).setText(timesLotecaMock.getDescription());
        } else {
            ((ImageView) holder.layoutTimeDois.findViewById(R.id.item_image)).setImageResource(timesLotecaMock.getImage());
            ((TextView) holder.layoutTimeDois.findViewById(R.id.item_title)).setText(timesLotecaMock.getTitle());
            ((TextView) holder.layoutTimeDois.findViewById(R.id.item_description)).setText(timesLotecaMock.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LotecaHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutTimeUm;
        private LinearLayout layoutEmpate;
        private LinearLayout layoutTimeDois;

        public LotecaHolder(View itemView) {
            super(itemView);

            layoutTimeUm = (LinearLayout) itemView.findViewById(R.id.loteca_time_um);
            layoutEmpate = (LinearLayout) itemView.findViewById(R.id.loteca_empate);
            layoutTimeDois = (LinearLayout) itemView.findViewById(R.id.loteca_time_dois);
        }
    }

}
