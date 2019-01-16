package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostar.interno.view.custom.ApostaPartidaLotecaView;
import br.gov.caixa.loterias.apostar.interno.view.custom.ApostaPartidaLotecaView_;

/**
 * Created by joafilho on 25/01/2018.
 * DetalheApostaLotecaAdapter
 */

public class DetalheApostaLotecaAdapter extends RecyclerView.Adapter<DetalheApostaLotecaAdapter.ApostaLotecaHolder> {

    private List<PartidaLotecaDTO> listaPartidas;

    public DetalheApostaLotecaAdapter(List<PartidaLotecaDTO> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    @Override
    public ApostaLotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ApostaPartidaLotecaView view = ApostaPartidaLotecaView_.build(parent.getContext());

        return new ApostaLotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotecaHolder holder, int position) {
        holder.apostaPartidaLotecaView.setLayout(listaPartidas.get(position));
    }

    @Override
    public int getItemCount() {
        return this.listaPartidas.size();
    }

    class ApostaLotecaHolder extends RecyclerView.ViewHolder {

        private ApostaPartidaLotecaView apostaPartidaLotecaView;

        public ApostaLotecaHolder(View itemView) {
            super(itemView);

            this.apostaPartidaLotecaView = (ApostaPartidaLotecaView) itemView;
        }
    }
}
