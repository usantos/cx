package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.ComprasDezenasView;
import br.gov.caixa.loterias.apostar.interno.view.custom.ComprasDezenasView_;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;

/**
 * Created by cedesbr450 on 30/01/18.
 */


public class ListaComprasDetalhesAdapter extends RecyclerView.Adapter<ListaComprasDetalhesAdapter.ListaCompraHolder> {

    private final Context mContext;
    private final Activity activity;
    private List<ApostaDTO> listaAposta;

    public ListaComprasDetalhesAdapter(Activity activity, Context context, List<ApostaDTO> listaAposta) {
        this.mContext = context;
        this.activity = activity;
        this.listaAposta = listaAposta;
    }

    @Override
    public ListaCompraHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(mContext).inflate(R.layout.list_item_compras, null);
        layoutInflater.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ListaCompraHolder(layoutInflater);
    }

    @Override
    public void onBindViewHolder(ListaCompraHolder holder, int position) {

        EstiloModalidade estilo = new EstiloModalidade(listaAposta.get(position).getModalidade());
        mContext.setTheme(estilo.getThemeId());
        int colorText = mContext.getResources().getColor(estilo.getMenuItemColor());

        holder.tipoPremioListaComprasTextView.setText(ViewUtils.textFuturaAndFuturaBold(mContext,
                mContext.getString(R.string.texto_futura_bold, ApostaDTO.fromStringModalidade(listaAposta.get(position).getModalidade()))));
        holder.tipoPremioListaComprasTextView.setTextColor(colorText);

        holder.dataConcursoListaComprasTextView.setText(listaAposta.get(position).getDataEfetivacao());
        holder.dataConcursoListaComprasTextView.setTextColor(colorText);

        holder.concursoListaComprasTextView.setText(ViewUtils.textFuturaAndFuturaBold(mContext, mContext.getString(R.string.label_aposta_concurso_bold, listaAposta.get(position).getConcursoAlvo())));
        holder.concursoListaComprasTextView.setTextColor(colorText);

        holder.statusResultadoApostaListaComprasTextView.setText(listaAposta.get(position).getSituacao().getDescricao());
        holder.statusResultadoApostaListaComprasTextView.setTextColor(colorText);

        holder.topListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorClara()));
        holder.bottomListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorEscura()));
        holder.seusNumerosItemListaComprasTextView.setTextColor(colorText);

        View child = null;
        switch (listaAposta.get(position).getModalidade()) {
            case LOTECA:
                child = LayoutInflater.from(mContext).inflate(R.layout.item_compras_loteca_layout, null);
                holder.seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
                ExpandableHeightRecyclerView lotecaRecyclerViewItemCompra = child.findViewById(R.id.lotecaRecyclerViewItemCompra);
                LotecaComprasAdapter lotecaAdapter = new LotecaComprasAdapter(listaAposta.get(position).getPartidasLoteca());
                lotecaRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(activity));
                lotecaRecyclerViewItemCompra.setAdapter(lotecaAdapter);
                lotecaRecyclerViewItemCompra.setExpanded(true);
                break;
            case LOTOGOL:
                child = LayoutInflater.from(mContext).inflate(R.layout.item_compras_lotogol_layout, null);
                holder.seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
                ExpandableHeightRecyclerView lotogolRecyclerViewItemCompra = child.findViewById(R.id.lotecaRecyclerViewItemCompra);
                DetalhesApostaLotogolDetalhesComprasAdapter lotogolAdapter = new DetalhesApostaLotogolDetalhesComprasAdapter(listaAposta.get(position).getPartidasLotogol());
                lotogolRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(activity));
                lotogolRecyclerViewItemCompra.setAdapter(lotogolAdapter);
                lotogolRecyclerViewItemCompra.setExpanded(true);
                break;
            default:
                holder.seusNumerosItemListaComprasTextView.setText(R.string.seus_numeros);
                child = ComprasDezenasView_.build(mContext);
                ((ComprasDezenasView)child).setLayout(listaAposta.get(position));
                break;
        }

        holder.itemComprasContentModalideLinearLayout.addView(child);
    }

    @Override
    public int getItemCount() {
        return listaAposta.size();
    }

    class ListaCompraHolder extends RecyclerView.ViewHolder {

        private TextView tipoPremioListaComprasTextView;
        private TextView dataConcursoListaComprasTextView;
        private TextView concursoListaComprasTextView;
        private TextView statusResultadoApostaListaComprasTextView;
        private LinearLayout topListItemComprasLinearLayout;
        private LinearLayout bottomListItemComprasLinearLayout;
        private TextView seusNumerosItemListaComprasTextView;
        private LinearLayout itemComprasContentModalideLinearLayout;

        ListaCompraHolder(View itemView) {
            super(itemView);
            tipoPremioListaComprasTextView = itemView.findViewById(R.id.TipoPremioListaComprasTextView);
            dataConcursoListaComprasTextView = itemView.findViewById(R.id.dataConcursoListaComprasTextView);
            concursoListaComprasTextView = itemView.findViewById(R.id.concursoListaComprasTextView);
            statusResultadoApostaListaComprasTextView = itemView.findViewById(R.id.statusResultadoApostaListaComprasTextView);
            topListItemComprasLinearLayout = itemView.findViewById(R.id.topListItemComprasLinearLayout);
            bottomListItemComprasLinearLayout = itemView.findViewById(R.id.bottomListItemComprasLinearLayout);
            seusNumerosItemListaComprasTextView = itemView.findViewById(R.id.seusNumerosItemListaComprasTextView);
            itemComprasContentModalideLinearLayout = itemView.findViewById(R.id.itemComprasContentModalideLinearLayout);
        }
    }
}
