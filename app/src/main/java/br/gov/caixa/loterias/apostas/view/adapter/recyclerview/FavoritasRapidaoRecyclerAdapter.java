package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public final class FavoritasRapidaoRecyclerAdapter extends RecyclerView.Adapter<FavoritasRapidaoRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<ApostaFavoritaDTO> listaApostaFavorita;
    private final List<ApostaFavoritaDTO> listaApostasSelecionadas;
    private FavoritasRapidaoRecyclerAdapter.Favorito favorito;
    boolean isConfigFavoritasList = false;


    public FavoritasRapidaoRecyclerAdapter(final Activity activity, final Context context, final List<ApostaFavoritaDTO> listaApostaFavorita, final List<ApostaFavoritaDTO> listaApostasSelecionadas, boolean isConfigFavoritasList) {
        this.context = context;
        this.listaApostaFavorita = listaApostaFavorita;
        this.listaApostasSelecionadas = listaApostasSelecionadas;
        this.activity = activity;
        this.isConfigFavoritasList = isConfigFavoritasList;
    }

    @Override
    public FavoritasRapidaoRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        if (listaApostaFavorita.size() == 0) {
            return new FavoritasRapidaoRecyclerAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_apostas_favoritas_rapidao_empty, parent, false));
        }

        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_aposta_favorita, parent, false);
        final FavoritasRapidaoRecyclerAdapter.ViewHolder viewHolder = new FavoritasRapidaoRecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritasRapidaoRecyclerAdapter.ViewHolder holder, int position) {
        if (listaApostaFavorita.size() > 0) {
            EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.toString(listaApostaFavorita.get(position).getModalidade().getDescricao().toLowerCase(new Locale(context.getResources().getString(R.string.pt), context.getResources().getString(R.string.br)))));
            holder.modalidades.setText(listaApostaFavorita.get(position).getModalidade().getDescricao());
            holder.modalidades.setTextColor(context.getResources().getColor(estilo.getCorClara()));
            holder.setIsRecyclable(false);

            StringBuilder numeroStr = new StringBuilder();

            List<Integer> numerosSelecionados;
            if  (listaApostaFavorita.get(position).getModalidade().getValor() != 7) {
                numerosSelecionados  = listaApostaFavorita.get(position).getListaNumerosSelecionados();
                for (Integer num : numerosSelecionados) {
                    if (num.equals(numerosSelecionados.get(numerosSelecionados.size() - 1))) {
                        numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d), num));
                    } else {
                        numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d_traco), num));
                    }
                }
            }

            final ApostaFavoritaDTO aposta = listaApostaFavorita.get( position );

            if (ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()) == ModalidadeEnum.TIMEMANIA) {
                numeroStr.append(context.getResources().getString(R.string.barra_n_time_do_coracao_dois_pontos)).append(aposta.getTimeDoCoracao().getNome() + "/" + aposta.getTimeDoCoracao().getUf());
            }

            if (ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()) == ModalidadeEnum.DIA_DE_SORTE) {
                numeroStr.append(context.getResources().getString(R.string.barra_n_mes_de_sorte_dois_pontos)).append(aposta.getMesDeSorte().getNome());
            }

            holder.numerosEDetalhes.setText(numeroStr.toString());

            if (listaApostasSelecionadas != null) {
                if (isSelected(aposta)) {
                    holder.checkBox.setChecked(true);
                }

                holder.checkBox.setOnCheckedChangeListener( (CompoundButton compoundButton, boolean b) ->  {
                    if(favorito != null) {
                        if (b) {
                            favorito.onIncluso(aposta);
                        }else{
                            favorito.onRemover(aposta);
                        }
                    }
                });
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
        }
    }

    private boolean isSelected(ApostaFavoritaDTO aposta) {
        for (ApostaFavoritaDTO apostaSelecionada: listaApostasSelecionadas) {
            if (apostaSelecionada.getId().compareTo(aposta.getId() ) == 0) {
                return  true;
            }
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView modalidades;
        protected TextView numerosEDetalhes;
        protected CheckBox checkBox;

        public ViewHolder(final View itemView) {
            super(itemView);
            modalidades = itemView.findViewById(R.id.tituloApostaFavorira);
            numerosEDetalhes = itemView.findViewById(R.id.numerosApostaFavorita);
            checkBox = itemView.findViewById(R.id.checkboxApostaFavorita);
        }
    }

        @Override
    public int getItemCount() {
        if (!isConfigFavoritasList) {
            return listaApostaFavorita.size() == 0 ? 1 : listaApostaFavorita.size();
        } else {
            return listaApostaFavorita.size();
        }
    }

    public void setOnFavorito(FavoritasRapidaoRecyclerAdapter.Favorito favorito){
        this.favorito = favorito;
    }

    public interface Favorito{

        void onRemover(ApostaFavoritaDTO aposta);

        void onIncluso(ApostaFavoritaDTO aposta);

    }
}
