package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostar.interno.view.custom.ApostaPartidaLotogolView;
import br.gov.caixa.loterias.apostar.interno.view.custom.ApostaPartidaLotogolView_;

/**
 * Created by joafilho on 26/01/2018.
 * Class DetalheApostaLotogolAdapter
 */

public class DetalheApostaLotogolAdapter extends RecyclerView.Adapter<DetalheApostaLotogolAdapter.ApostaLotogolHolder> {

    private List<PartidaLotogolDTO> partidasLotogol;

    public DetalheApostaLotogolAdapter(List<PartidaLotogolDTO> partidasLotogol) {
        this.partidasLotogol = partidasLotogol;
    }

    @Override
    public ApostaLotogolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApostaPartidaLotogolView view = ApostaPartidaLotogolView_.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ApostaLotogolHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotogolHolder holder, int position) {
        holder.apostaPartidaLotogolView.setLayout(partidasLotogol.get(position));
        if (position == partidasLotogol.size() - 1) {
            holder.apostaPartidaLotogolView.getLinhaDivisao().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return partidasLotogol.size();
    }

    public class ApostaLotogolHolder extends RecyclerView.ViewHolder {

        private ApostaPartidaLotogolView apostaPartidaLotogolView;

        public ApostaLotogolHolder(View itemView) {
            super(itemView);

            apostaPartidaLotogolView = (ApostaPartidaLotogolView) itemView;
        }
    }
}
