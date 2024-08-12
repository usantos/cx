package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.view.holder.FavoritaHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;

/**
 * Created by cedesbr450 on 16/02/18.
 */
public final class FavoritasRecyclerViewAdapter extends RecyclerView.Adapter<FavoritaHolder> {
    //region Variables
    private final Context context;
    private final FavoritasActivity activity;
    private final List<ApostaFavoritaDTO> listaApostaFavorita;
    private ApostaFavoritaListener listener;
    //endregion

    //region Constructors
    public FavoritasRecyclerViewAdapter(final FavoritasActivity activity, final Context context, final List<ApostaFavoritaDTO> listaApostaFavorita, ApostaFavoritaListener listener) {
        this.context = context;
        this.listaApostaFavorita = listaApostaFavorita;
        this.activity = activity;
        this.listener = listener;
    }
    //endregion

    //region Overrides
    @Override
    public FavoritaHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_favoritas, parent, false);
        return new FavoritaHolder(view, activity, listener, activity.getSupportFragmentManager());
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritaHolder holder, int position) {
        ApostaFavoritaDTO aposta = listaApostaFavorita.get(position);
        holder.bind(aposta, position);
    }
    @Override
    public int getItemCount() {
        return listaApostaFavorita.size();
    }
    //endregion
}