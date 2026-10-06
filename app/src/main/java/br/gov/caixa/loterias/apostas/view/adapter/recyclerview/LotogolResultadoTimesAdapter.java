package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.view.custom.PartidaViewLotogolResultado;

/**
 * Created by cedesbr450 on 30/03/18.
 */


public class LotogolResultadoTimesAdapter extends RecyclerView.Adapter<LotogolResultadoTimesAdapter.LotogolHolder> {

    private List<PartidaLotogolDTO> partidalotogolLista;
    private Context context;
    private ModalidadeEnum modalidade;

    public LotogolResultadoTimesAdapter(Context context, List<PartidaLotogolDTO> partidalotogolLista, ModalidadeEnum modalidade) {
        this.partidalotogolLista = partidalotogolLista;
        this.context = context;
        this.modalidade = modalidade;
    }

    @Override
    public LotogolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PartidaViewLotogolResultado view = PartidaViewLotogolResultado.build(parent.getContext());
        return new LotogolHolder(view);
    }

    @Override
    public void onBindViewHolder(LotogolHolder holder, int position) {
        holder.partidaRow.setLayout(partidalotogolLista.get(position));
        holder.partidaRow.getPaginaResultadoLotogolText().setText(position + 1 +  context.getResources().getString(R.string.espaco_de_espaco) + partidalotogolLista.size());
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return partidalotogolLista.size();
    }

    public class LotogolHolder extends RecyclerView.ViewHolder {

        private PartidaViewLotogolResultado partidaRow;

        LotogolHolder(View itemView) {
            super(itemView);
            partidaRow = (PartidaViewLotogolResultado) itemView;
        }
    }
}