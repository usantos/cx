package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.PartidaLotogolDTO;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotogolView;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotogolView_;

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
        return new ApostaLotogolHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotogolHolder holder, int position) {
        holder.apostaPartidaLotogolView.setLayout(partidasLotogol.get(position));
        if(position != 0){
            holder.apostaPartidaLotogolView.getLayoutRowApostaPartidaLotogol().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_claro);
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
