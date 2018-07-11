package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.view.custom.PartidaViewLotogol;
import caixa.gov.br.loterias.view.listener.LotogolListener;
import caixa.gov.br.loterias.view.custom.PartidaViewLotogol_;

/**
 * Created by joafilho on 26/12/2017.
 * Class LotogolAdapter
 */

public class LotogolAdapter extends RecyclerView.Adapter<LotogolAdapter.LotogolHolder> {

    private List<ParametroPartida> listaPartidas;
    private LotogolListener listener;

    public LotogolAdapter(List<ParametroPartida> listaPartidas, LotogolListener listener) {
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
        if(position != 0){
            holder.partidaRow.getRowPartidaLotogol().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_claro);
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    class LotogolHolder extends RecyclerView.ViewHolder {

        private PartidaViewLotogol partidaRow;

        LotogolHolder(View itemView) {
            super(itemView);
            partidaRow = (PartidaViewLotogol) itemView;
        }
    }
}
