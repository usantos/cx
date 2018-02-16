package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;

/**
 * Created by cedesbr450 on 16/02/18.
 */


public final class FavoritasRecyclerViewAdapter extends RecyclerView.Adapter<FavoritasRecyclerViewAdapter.ViewHolder> {


    private final Context context;
    private final List<ApostaFavoritaDTO> listaApostaFavorita;

    public FavoritasRecyclerViewAdapter(final Context context, final List<ApostaFavoritaDTO> listaApostaFavorita) {
        this.context = context;
        this.listaApostaFavorita = listaApostaFavorita;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_favoritas, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ApostaFavoritaDTO aposta = listaApostaFavorita.get(position);


        String numeroStr = "";
        for (Integer num : aposta.getNumerosSelecionados()) {
            if (num == (aposta.getNumerosSelecionados().get(aposta.getNumerosSelecionados().size() - 1))) {
                numeroStr += String.format("%02d", num);
            } else {
                numeroStr += String.format("%02d - ", num);
            }
        }
        holder.numerosApostaFavorita.setText(numeroStr);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onIntercept(View v, MotionEvent event) {

                return true;
            }
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaApostaFavorita.size();
    }

    public List<ApostaFavoritaDTO> getCompras() {
        return Collections.unmodifiableList(listaApostaFavorita);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relativeLayoutContentFavoritas;
        protected RelativeLayout relativeLayoutContentAlterarFavoritas;
        protected TextView tituloApostaFavorira;
        protected TextView numerosApostaFavorita;

        public ViewHolder(final View itemView) {
            super(itemView);
            relativeLayoutContentFavoritas = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutContentFavoritas);
            relativeLayoutContentAlterarFavoritas = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutContentAlterarFavoritas);
            tituloApostaFavorira = (TextView) itemView.findViewById(R.id.tituloApostaFavorira);
            numerosApostaFavorita = (TextView) itemView.findViewById(R.id.numerosApostaFavorita);
        }
    }
}
