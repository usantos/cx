package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalheDuvidaActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaView;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaView_;

/**
 * Created by joafilho on 15/02/2018.
 * Class adapter ItemDuvidaAdapter
 */

public class ItemDuvidaAdapter extends RecyclerView.Adapter<ItemDuvidaAdapter.ItemDuvidaViewHolder> {

    private List<ListSecaoDTO> itensDuvida;

    public ItemDuvidaAdapter(List<ListSecaoDTO> itensDuvida) {
        this.itensDuvida = itensDuvida;
    }

    @Override
    public ItemDuvidaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //ItemDuvidaView view = ItemDuvidaView_.build(parent.getContext());
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duvida, null);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duvida, parent, false);

        //RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //view.setLayoutParams(lp);
        return new ItemDuvidaViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ItemDuvidaViewHolder holder, int position) {
//        holder.itemDuvidaView.setLayout(itensDuvida.get(position));
        if (position != 0) {
            //holder.itemDuvidaView.getItemDuvida().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
            holder.itemDuvida.setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
        }
        ListSecaoDTO secaoDTO = itensDuvida.get(position);
        holder.textItemDuvida.setTag(secaoDTO.getId());
        holder.textItemDuvida.setText(secaoDTO.getNome());
    }

    @Override
    public int getItemCount() {
        return itensDuvida.size();
    }

    class ItemDuvidaViewHolder extends RecyclerView.ViewHolder  { //implements View.OnClickListener

        //private ItemDuvidaView itemDuvidaView;
        private Context context;

        private TextView textItemDuvida;
        private ImageView imageItemDuvida;
        private LinearLayout itemDuvida;

        public ItemDuvidaViewHolder(View view, Context context) {
            super(view);
            //this.itemDuvidaView = (ItemDuvidaView) itemView;
            this.context = context;

            this.textItemDuvida = view.findViewById(R.id.textItemDuvida);
            this.imageItemDuvida = view.findViewById(R.id.imageItemDuvida);
            this.itemDuvida = view.findViewById(R.id.itemDuvida);

            if (this.itemDuvida!= null) {
                this.itemDuvida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int adapterPosition = getAbsoluteAdapterPosition();
                        if (adapterPosition == RecyclerView.NO_POSITION) return;;

                        Intent intent = IntentUtil.getIntentOrigemDestino((Activity) context, DetalheDuvidaActivity.class);
                        intent.putExtra(DetalheDuvidaActivity.SECAO_DTO_EXTRA, ((Serializable) itensDuvida.get(adapterPosition)));
                        ((Activity) context).startActivity(intent);
                    }
                });
            }
        }
    }
}
