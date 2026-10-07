package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;
import java.util.ArrayList;
import android.widget.ImageView;
import br.gov.caixa.loterias.apostas.utils.ModalidadeOrdering;
import br.gov.caixa.loterias.apostas.utils.ModalidadePreferences;
import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.custom.ModalidadeSorteioResultadoView;
import br.gov.caixa.loterias.apostas.view.holder.HomeViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnHomeListener;

/**
 * Created by brfernan on 26/10/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> implements OnHomeListener {

    private List<Modalidade> data;
    private List<Modalidade> backendOrder;
    private final ModalidadePreferences preferences;
    private Runnable onOrderingChanged;
    private Context context;
    private Activity activity;
    private static Button aposta;

    public HomeAdapter(Activity activity, Context context, List<Modalidade> data, View aposta) {
        this.data = data;
        this.backendOrder = new ArrayList<>(data);
        this.preferences = new ModalidadePreferences(context);
        reorder();
        this.context = context;
        this.activity = activity;
        this.aposta = (Button) aposta;
    }


    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_modalidade, parent, false);

        return new HomeViewHolder(v, this, activity);
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder holder, final int position) {
        Modalidade modalidade = data.get(position);
        holder.itemView.setTag(modalidade);
        holder.bind(modalidade, position);
        bindFavorite(holder.itemView.findViewById(R.id.favoriteModalidade), modalidade);
    }

    public int retornaPosicaoPorModalidade(ModalidadeEnum modalidade) {
        for (int i = 0; i < data.size(); i++) {
            if (data != null && data.get(i).getTipoModalidade().equals(modalidade)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setModalidades(List<Modalidade> data) {
        this.data = data;
        this.backendOrder = new ArrayList<>(data);
        refreshOrdering(true);
    }

    private boolean reorder() {
        List<Modalidade> ordered = ModalidadeOrdering.sorted(backendOrder,
                item -> item.getConcurso() != null && item.getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL,
                item -> preferences.isFavorite(item.getTipoModalidade()),
                item -> preferences.usage(item.getTipoModalidade()),
                BuildConfig.MODALIDADE_REORDER_MIN_USAGE);
        boolean changed = !data.equals(ordered);
        if (changed) {
            data.clear();
            data.addAll(ordered);
        }
        return changed;
    }

    public void setOnOrderingChanged(Runnable listener) {
        onOrderingChanged = listener;
    }

    public void refreshOrdering() {
        // Favorites may change in the management screen without changing card order.
        refreshOrdering(true);
    }

    private void refreshOrdering(boolean favoriteChanged) {
        if (reorder() || favoriteChanged) {
            notifyDataSetChanged();
            if (onOrderingChanged != null) onOrderingChanged.run();
        }
    }

    private void bindFavorite(ImageView heart, Modalidade modalidade) {
        boolean favorite = preferences.isFavorite(modalidade.getTipoModalidade());
        heart.setImageResource(favorite ? R.drawable.ic_orange_heart_filled : R.drawable.ic_orange_heart);
        heart.setContentDescription(context.getString(favorite ? R.string.modalidade_remover_favorito : R.string.modalidade_favoritar, modalidade.getNome()));
        heart.setOnClickListener(view -> {
            boolean currentFavorite = preferences.isFavorite(modalidade.getTipoModalidade());
            if (!currentFavorite && preferences.isFavoriteLimitReached()) {
                DialogUtils.dialogEntendi(activity, context.getString(R.string.modalidades_favoritas_limite));
                return;
            }
            DialogUtils.dialogConfirmar(activity,
                    context.getString(currentFavorite ? R.string.msg_excluir_favorita
                            : R.string.msg_incluir_favorita, modalidade.getNome()),
                    (dialog, which) -> {
                        if (!preferences.setFavorite(modalidade.getTipoModalidade(), !currentFavorite)) {
                            DialogUtils.dialogEntendi(activity, context.getString(R.string.modalidades_favoritas_limite));
                            return;
                        }
                        refreshOrdering(true);
                    });
        });
    }

    @Override
    public void callWebserviceResultadoConcurso(final RelativeLayout relativeLayoutTopResultados,
                                                 final LinearLayout linearLayoutResultadosApostas,
                                                 final String modalidade,
                                                 final int concurso,
                                                 final int position,
                                                 final RelativeLayout setaEsquerdaAcaoRelativeLayout,
                                                 final RelativeLayout setaDireitaAcaoRelativeLayout,
                                                 final boolean animarView, final HomeViewHolder holder
                                                ) {

        final Modalidade requested = data.get(position);
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(context);
        ApostaSilceBO.getInstance().getResultadoModalidade(modalidade, new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
                }

                requested.setResultadoConcursoDTO(response.getPayload());
                if (holder.itemView.getTag() != requested) return;
                if (animarView) {
                    holder.animacaoAbrirResultados(context,
                                            relativeLayoutTopResultados,
                                            linearLayoutResultadosApostas,
                                            data.indexOf(requested), setaEsquerdaAcaoRelativeLayout,
                                            setaDireitaAcaoRelativeLayout);
                }

                setLayoutResultado(linearLayoutResultadosApostas,
                                   data.indexOf(requested));

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    @Override
    public void setLayoutResultado(LinearLayout linearLayoutResultadosApostas, int position) {
        linearLayoutResultadosApostas.removeAllViews();
        ModalidadeSorteioResultadoView view = ModalidadeSorteioResultadoView.build(context);
        view.setLayout(data.get(position));
        linearLayoutResultadosApostas.addView(view);
    }
}
