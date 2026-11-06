package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.view.custom.PartidaViewLoteca;

/**
 * Created by cedesbr450 on 15/03/18.
 */

public class LotecaComprasAdapter extends RecyclerView.Adapter<LotecaComprasAdapter.LotecaHolder> {

    private List<PartidaLotecaDTO> listaPartidas;

    public LotecaComprasAdapter(List<PartidaLotecaDTO> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    @Override
    public LotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PartidaViewLoteca view = PartidaViewLoteca.build(parent.getContext());
        return new LotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(LotecaHolder holder, int position) {
        holder.partidaRow.setLayout(listaPartidas.get(position), position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    class LotecaHolder extends RecyclerView.ViewHolder {

        private PartidaViewLoteca partidaRow;

        LotecaHolder(View itemView) {
            super(itemView);
            partidaRow = (PartidaViewLoteca) itemView;
        }
    }
}
