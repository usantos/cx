package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostar.interno.view.custom.PartidaViewLotecaResultado;
import br.gov.caixa.loterias.apostar.interno.view.custom.PartidaViewLotecaResultado_;

/**
 * Created by cedesbr450 on 29/03/18.
 */


public class LotecaResultadoTimesAdapter extends RecyclerView.Adapter<LotecaResultadoTimesAdapter.LotecaHolder> {

    private List<PartidaLotecaDTO> partidalotecaLista;
    private  Context context;
    private ModalidadeEnum modalidade;

    public LotecaResultadoTimesAdapter(Context context, List<PartidaLotecaDTO> partidalotecaLista, ModalidadeEnum modalidade) {
        this.partidalotecaLista = partidalotecaLista;
        this.context = context;
        this.modalidade = modalidade;
    }

    @Override
    public LotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PartidaViewLotecaResultado view = PartidaViewLotecaResultado_.build(parent.getContext());
        return new LotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(LotecaHolder holder, int position) {
        holder.partidaRow.setLayout(partidalotecaLista.get(position));
        holder.partidaRow.getPaginaResultadoLotecaText().setText(position + 1 + context.getResources().getString(R.string.espaco_de_espaco) + partidalotecaLista.size());
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return partidalotecaLista.size();
    }

    public class LotecaHolder extends RecyclerView.ViewHolder {

        private PartidaViewLotecaResultado partidaRow;

        LotecaHolder(View itemView) {
            super(itemView);
            partidaRow = (PartidaViewLotecaResultado) itemView;
        }
    }
}
