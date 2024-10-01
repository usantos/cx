package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DialogFragmentUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolheModalidadeDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnModalidadesAbertasListener;

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

    public List<ApostaFavoritaDTO> getCompras() {
        return Collections.unmodifiableList(listaApostaFavorita);
    }

    //endregion
}