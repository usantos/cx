package caixa.gov.br.loterias.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.custom.ComprasDezenasView;
import caixa.gov.br.loterias.view.custom.ComprasDezenasView_;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;

/**
 * Created by cedesbr450 on 30/01/18.
 */


public class ListaComprasDetalhesAdapter extends BaseAdapter {

    private final Context mContext;
    private final Activity activity;
    List<ApostaDTO> listaAposta;
    private final CompraDTO compraDTO;
    private final int ordemCompra;

    // 1
    public ListaComprasDetalhesAdapter(Activity activity, Context context, List<ApostaDTO> listaAposta, CompraDTO compraDTO, int ordemCompra) {
        this.mContext = context;
        this.activity = activity;
        this.listaAposta = listaAposta;
        this.compraDTO = compraDTO;
        this.ordemCompra = ordemCompra;
    }

    // 2
    @Override
    public int getCount() {
        return listaAposta.size() + 1;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (position > 0) {
            convertView = layoutInflater.inflate(R.layout.list_item_compras, null);

            int idLista = position - 1;

            TextView TipoPremioListaComprasTextView = convertView.findViewById(R.id.TipoPremioListaComprasTextView);
            TipoPremioListaComprasTextView.setText(ApostaDTO.fromStringModalidade(listaAposta.get(idLista).getModalidade()));

            TextView dataConcursoListaComprasTextView = convertView.findViewById(R.id.dataConcursoListaComprasTextView);
            dataConcursoListaComprasTextView.setText(listaAposta.get(idLista).getDataEfetivacao());

            TextView concursoListaComprasTextView = convertView.findViewById(R.id.concursoListaComprasTextView);
            concursoListaComprasTextView.setText(ViewUtils.textFuturaAndFuturaBold(mContext, mContext.getString(R.string.label_aposta_concurso_bold, listaAposta.get(idLista).getConcursoAlvo())));


            String numeroStr = "";
            if (listaAposta.get(idLista).getIndicadorSurpresinha().getValor() == 2) {
                int contador = 1;
                for (int i = 0; i < listaAposta.get(idLista).getQuantidadeNumeros(); i++) {
                    if (contador == 5) {
                        numeroStr = numeroStr + "XX\n";
                        contador = 0;
                    } else if (i == listaAposta.get(idLista).getQuantidadeNumeros() - 1) {
                        numeroStr = numeroStr + "XX";
                    } else {
                        numeroStr = numeroStr + "XX - ";
                    }
                    contador++;
                }

            } else {
                for (Integer num : listaAposta.get(idLista).getNumerosSelecionados()) {
                    if (num == (listaAposta.get(idLista).getNumerosSelecionados().get(listaAposta.get(idLista).getNumerosSelecionados().size() - 1))) {
                        numeroStr += String.format("%02d", num);
                    } else {
                        numeroStr += String.format("%02d - ", num);
                    }
                }
            }


            TextView statusResultadoApostaListaComprasTextView = convertView.findViewById(R.id.statusResultadoApostaListaComprasTextView);
            statusResultadoApostaListaComprasTextView.setText(listaAposta.get(idLista).getSituacao().getDescricao());

            EstiloModalidade estilo = new EstiloModalidade(listaAposta.get(idLista).getModalidade());

            LinearLayout topListItemComprasLinearLayout = convertView.findViewById(R.id.topListItemComprasLinearLayout);
            topListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorClara()));

            LinearLayout bottomListItemComprasLinearLayout = convertView.findViewById(R.id.bottomListItemComprasLinearLayout);
            bottomListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorEscura()));

            TextView seusNumerosItemListaComprasTextView = convertView.findViewById(R.id.seusNumerosItemListaComprasTextView);

            LinearLayout itemComprasContentModalideLinearLayout = convertView.findViewById(R.id.itemComprasContentModalideLinearLayout);
            View child = null;
            switch (listaAposta.get(idLista).getModalidade()) {
                case TIMEMANIA:
                    child = layoutInflater.inflate(R.layout.item_compras_timemania_layout, null);
                    statusResultadoApostaListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.verdemediotransparente));
                    TipoPremioListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                    dataConcursoListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                    concursoListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                    seusNumerosItemListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                    TextView seusNumerosListaComprasTextView1 = convertView.findViewById(R.id.SeusNumerosListaComprasTextView);
                    seusNumerosListaComprasTextView1.setText(numeroStr);
                    break;
                case LOTECA:
                    child = layoutInflater.inflate(R.layout.item_compras_loteca_layout, null);
                    seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
                    ExpandableHeightRecyclerView lotecaRecyclerViewItemCompra = child.findViewById(R.id.lotecaRecyclerViewItemCompra);
                    LotecaComprasAdapter lotecaAdapter = new LotecaComprasAdapter(listaAposta.get(idLista).getPartidasLoteca());
                    lotecaRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(activity));
                    lotecaRecyclerViewItemCompra.setAdapter(lotecaAdapter);
                    lotecaRecyclerViewItemCompra.setExpanded(true);
                    break;
                case LOTOGOL:
                    child = layoutInflater.inflate(R.layout.item_compras_lotogol_layout, null);
                    seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
                    ExpandableHeightRecyclerView lotogolRecyclerViewItemCompra = child.findViewById(R.id.lotecaRecyclerViewItemCompra);
                    DetalhesApostaLotogolDetalhesComprasAdapter lotogolAdapter = new DetalhesApostaLotogolDetalhesComprasAdapter(listaAposta.get(idLista).getPartidasLotogol());
                    lotogolRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(activity));
                    lotogolRecyclerViewItemCompra.setAdapter(lotogolAdapter);
                    lotogolRecyclerViewItemCompra.setExpanded(true);
                    break;
                case DIA_DE_SORTE:
                    child = ComprasDezenasView_.build(mContext);
                    mContext.setTheme(estilo.getThemeId());
                    ((ComprasDezenasView) child).setLayout(listaAposta.get(idLista));
                    break;
                default:
                    child = layoutInflater.inflate(R.layout.item_compras_generico_layout, null);
                    TextView seusNumerosListaComprasTextView2 = convertView.findViewById(R.id.SeusNumerosListaComprasTextView);
                    seusNumerosListaComprasTextView2.setText(numeroStr);
                    break;
            }
            itemComprasContentModalideLinearLayout.addView(child);

        } else {
            convertView = layoutInflater.inflate(R.layout.list_group_compras, null);
            FrameLayout setaIndicador = convertView.findViewById(R.id.setaComprasGroup);
            setaIndicador.setVisibility(View.INVISIBLE);

            TextView dataCompra = convertView.findViewById(R.id.dataCompra);
            dataCompra.setText(compraDTO.getDataEfetivacaoDebito());
            TextView valorCompra = convertView.findViewById(R.id.valorCompra);
            valorCompra.setText(caixa.gov.br.loterias.utils.ViewUtils.getMoedaFormat(compraDTO.getValorTotal()));

            TextView itemCompraOrdemTextView = convertView.findViewById(R.id.itemCompraOrdemTextView);
            itemCompraOrdemTextView.setText(mContext.getString(R.string.label_compra_valor, ordemCompra));
        }


        return convertView;
    }
}
