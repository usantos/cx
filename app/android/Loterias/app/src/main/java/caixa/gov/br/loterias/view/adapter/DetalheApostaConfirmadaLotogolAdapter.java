package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotogolView;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotogolView_;
import caixa.gov.br.loterias.view.custom.PartidasLotogolApostasConfirmadasView;
import caixa.gov.br.loterias.view.custom.PartidasLotogolApostasConfirmadasView_;

/**
 * Created by joafilho on 26/01/2018.
 * Class DetalheApostaConfirmadaLotogolAdapter
 */

public class DetalheApostaConfirmadaLotogolAdapter extends RecyclerView.Adapter<DetalheApostaConfirmadaLotogolAdapter.ApostaLotogolHolder> {

        private List<PartidaLotogolDTO> partidasLotogol;

    public DetalheApostaConfirmadaLotogolAdapter(List<PartidaLotogolDTO> partidasLotogol) {
        this.partidasLotogol = partidasLotogol;
    }

    @Override
    public ApostaLotogolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PartidasLotogolApostasConfirmadasView view = PartidasLotogolApostasConfirmadasView_.build(parent.getContext());

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ApostaLotogolHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotogolHolder holder, int position) {
        holder.partidasLotogolApostasConfirmadasView.setLayout(partidasLotogol.get(position));
//        if(position != 0){
//            holder.partidasLotogolApostasConfirmadasView.setBackgroundResource(R.drawable.drawer_linha_divisao_branco_azul_top);
//        }
    }

    @Override
    public int getItemCount() {
        return partidasLotogol.size();
    }

    public class ApostaLotogolHolder extends RecyclerView.ViewHolder {

        private PartidasLotogolApostasConfirmadasView partidasLotogolApostasConfirmadasView;

        public ApostaLotogolHolder(View itemView) {
            super(itemView);

            partidasLotogolApostasConfirmadasView = (PartidasLotogolApostasConfirmadasView) itemView;
        }
    }
}
