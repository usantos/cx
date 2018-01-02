package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.utils.PartidaView;
import caixa.gov.br.loterias.utils.PartidaView_;

/**
 * Created by joafilho on 26/12/2017.
 */

public class LotecaAdapter extends RecyclerView.Adapter<LotecaAdapter.LotecaHolder> {

    private List<ParametroPartida> listaPartidas;
    private LocateAdapterListener listener;

    public LotecaAdapter(List<ParametroPartida> listaPartidas, LocateAdapterListener listener) {
        this.listaPartidas = listaPartidas;
        this.listener = listener;
    }

    @Override
    public LotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PartidaView view = PartidaView_.build(parent.getContext());

        return new LotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(LotecaHolder holder, int position) {
        holder.partidaRow.setListener(listener);
        holder.partidaRow.setLayout(listaPartidas.get(position));
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    public class LotecaHolder extends RecyclerView.ViewHolder {

        private PartidaView partidaRow;

        public LotecaHolder(View itemView) {
            super(itemView);
            partidaRow = (PartidaView) itemView;
        }
    }

    public interface LocateAdapterListener{
        void clickEquipeLoteca(ParametroPartida parametroPartida);
    }

}
