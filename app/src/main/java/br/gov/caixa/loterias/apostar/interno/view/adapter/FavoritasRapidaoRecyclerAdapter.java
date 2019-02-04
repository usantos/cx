package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;

public final class FavoritasRapidaoRecyclerAdapter extends RecyclerView.Adapter<FavoritasRapidaoRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<ApostaFavoritaDTO> listaApostaFavorita;
    private FavoritasRapidaoRecyclerAdapter.Favorito favorito;

    public FavoritasRapidaoRecyclerAdapter(final Activity activity, final Context context, final List<ApostaFavoritaDTO> listaApostaFavorita) {
        this.context = context;
        this.listaApostaFavorita = listaApostaFavorita;
        this.activity = activity;
    }

    @Override
    public FavoritasRapidaoRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_aposta_favorita, parent, false);
        final FavoritasRapidaoRecyclerAdapter.ViewHolder viewHolder = new FavoritasRapidaoRecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritasRapidaoRecyclerAdapter.ViewHolder holder, int position) {
        EstiloModalidade estilo = new EstiloModalidade(ModalidadeEnum.toString(listaApostaFavorita.get(position).getModalidade().getDescricao().toLowerCase()));
        holder.modalidades.setText(listaApostaFavorita.get(position).getModalidade().getDescricao());
        holder.modalidades.setTextColor(context.getResources().getColor(estilo.getCorClara()));

        String numeroStr = "";
        for (Integer num : listaApostaFavorita.get(position).getNumerosSelecionados()) {
            if (num.equals(listaApostaFavorita.get(position).getNumerosSelecionados().get(listaApostaFavorita.get(position).getNumerosSelecionados().size() - 1))) {
                numeroStr += String.format("%02d", num);
            } else {
                numeroStr += String.format("%02d - ", num);
            }
        }
        holder.numerosEDetalhes.setText(numeroStr);

        final ApostaFavoritaDTO aposta = listaApostaFavorita.get( position );

        holder.checkBox.setOnCheckedChangeListener( (CompoundButton compoundButton, boolean b) ->  {
                if(favorito != null) {
                    if (b) {
                        favorito.onIncluso(aposta);
                    }else{
                        favorito.onRemover(aposta);
                    }
                }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView modalidades;
        protected TextView numerosEDetalhes;
        protected CheckBox checkBox;

        public ViewHolder(final View itemView) {
            super(itemView);
            modalidades = (TextView) itemView.findViewById(R.id.tituloApostaFavorira);
            numerosEDetalhes = (TextView) itemView.findViewById(R.id.numerosApostaFavorita);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxApostaFavorita);
        }
    }

        @Override
    public int getItemCount() {
        return listaApostaFavorita.size();
    }

    public void setOnFavorito(FavoritasRapidaoRecyclerAdapter.Favorito favorito){
        this.favorito = favorito;
    }

    public interface Favorito{

        void onRemover(ApostaFavoritaDTO aposta);

        void onIncluso(ApostaFavoritaDTO aposta);

    }
}
