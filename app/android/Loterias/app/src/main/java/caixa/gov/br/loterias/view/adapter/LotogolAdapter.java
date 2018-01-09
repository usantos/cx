package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.utils.PartidaViewLotogol;
import caixa.gov.br.loterias.utils.PartidaViewLotogol_;

/**
 * Created by joafilho on 26/12/2017.
 */

public class LotogolAdapter extends RecyclerView.Adapter<LotogolAdapter.LotogolHolder> {

    private List<ParametroPartida> listaPartidas;
    private LotogolAdapterListener listener;

    public LotogolAdapter(List<ParametroPartida> listaPartidas, LotogolAdapterListener listener) {
        this.listaPartidas = listaPartidas;
        this.listener = listener;
    }

    @Override
    public LotogolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PartidaViewLotogol view = PartidaViewLotogol_.build(parent.getContext());

        return new LotogolHolder(view);
    }

    @Override
    public void onBindViewHolder(LotogolHolder holder, int position) {
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

    public class LotogolHolder extends RecyclerView.ViewHolder {

        private PartidaViewLotogol partidaRow;

        public LotogolHolder(View itemView) {
            super(itemView);
            partidaRow = (PartidaViewLotogol) itemView;
        }
    }

    public interface LotogolAdapterListener{
        void click(ParametroPartida parametroPartida);
    }

}
