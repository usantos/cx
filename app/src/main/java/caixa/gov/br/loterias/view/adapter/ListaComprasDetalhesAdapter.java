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

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.StringUtils;
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
    private ArrayList<String> tipos;

    public ListaComprasDetalhesAdapter(Activity activity, Context context, List<ApostaDTO> listaAposta, CompraDTO compraDTO, int ordemCompra, ArrayList<String> tipos) {
        this.mContext = context;
        this.activity = activity;
        this.listaAposta = listaAposta;
        this.compraDTO = compraDTO;
        this.ordemCompra = ordemCompra;
        this.tipos = tipos;
    }

    // 2
    @Override
    public int getCount() {
        return listaAposta.size()+1;
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

        if (position > 0){
            convertView = layoutInflater.inflate(R.layout.list_item_compras, null);

            int idLista = position-1;

            TextView TipoPremioListaComprasTextView = (TextView) convertView.findViewById(R.id.TipoPremioListaComprasTextView);
            TipoPremioListaComprasTextView.setText(ApostaDTO.fromStringModalidade(listaAposta.get(idLista).getModalidade()));

            TextView dataConcursoListaComprasTextView = (TextView) convertView.findViewById(R.id.dataConcursoListaComprasTextView);
            dataConcursoListaComprasTextView.setText(listaAposta.get(idLista).getDataEfetivacao());

            TextView concursoListaComprasTextView = (TextView) convertView.findViewById(R.id.concursoListaComprasTextView);
            concursoListaComprasTextView.setText("Conc. "+ listaAposta.get(idLista).getConcursoAlvo());

            String numeroStr = "";
            if (listaAposta.get(idLista).getNumerosSelecionados().size() == 0){
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
            }else{
                for (Integer num : listaAposta.get(idLista).getNumerosSelecionados()) {
                    if (num == (listaAposta.get(idLista).getNumerosSelecionados().get(listaAposta.get(idLista).getNumerosSelecionados().size() - 1))) {
                        numeroStr += String.format("%02d", num);
                    } else {
                        numeroStr += String.format("%02d - ", num);
                    }
                }
            }


            TextView statusResultadoApostaListaComprasTextView = (TextView) convertView.findViewById(R.id.statusResultadoApostaListaComprasTextView);
            statusResultadoApostaListaComprasTextView.setText(listaAposta.get(idLista).getSituacao().getDescricao());

            EstiloModalidade estilo = new EstiloModalidade(listaAposta.get(idLista).getModalidade());

            LinearLayout topListItemComprasLinearLayout = (LinearLayout) convertView.findViewById(R.id.topListItemComprasLinearLayout);
            topListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorClara()));

            LinearLayout bottomListItemComprasLinearLayout = (LinearLayout) convertView.findViewById(R.id.bottomListItemComprasLinearLayout);
            bottomListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorEscura()));

            TextView seusNumerosItemListaComprasTextView = (TextView) convertView.findViewById(R.id.seusNumerosItemListaComprasTextView);

            LinearLayout itemComprasContentModalideLinearLayout = (LinearLayout) convertView.findViewById(R.id.itemComprasContentModalideLinearLayout);
            if (listaAposta.get(idLista).getModalidade() == ModalidadeEnum.TIMEMANIA){
                View child = layoutInflater.inflate(R.layout.item_compras_timemania_layout, null);
                itemComprasContentModalideLinearLayout.addView(child);
                statusResultadoApostaListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.verdemediotransparente));
                TipoPremioListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                dataConcursoListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                concursoListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                seusNumerosItemListaComprasTextView.setTextColor(mContext.getResources().getColor(R.color.megasenamedio));
                TextView seusNumerosListaComprasTextView = (TextView) convertView.findViewById(R.id.SeusNumerosListaComprasTextView);
                seusNumerosListaComprasTextView.setText(numeroStr);

            }else if (listaAposta.get(idLista).getModalidade() == ModalidadeEnum.LOTECA){
                View child = layoutInflater.inflate(R.layout.item_compras_loteca_layout, null);
                seusNumerosItemListaComprasTextView.setText("Sua aposta");
                ExpandableHeightRecyclerView lotecaRecyclerViewItemCompra = (ExpandableHeightRecyclerView) child.findViewById(R.id.lotecaRecyclerViewItemCompra);
                itemComprasContentModalideLinearLayout.addView(child);
                LotecaComprasAdapter lotecaAdapter = new LotecaComprasAdapter(listaAposta.get(idLista).getPartidasLoteca());
                lotecaRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(activity));
                lotecaRecyclerViewItemCompra.setAdapter(lotecaAdapter);
                lotecaRecyclerViewItemCompra.setExpanded(true);

            }else if (listaAposta.get(idLista).getModalidade() == ModalidadeEnum.LOTOGOL){
                View child = layoutInflater.inflate(R.layout.item_compras_lotogol_layout, null);
                seusNumerosItemListaComprasTextView.setText("Sua aposta");
                ExpandableHeightRecyclerView lotogolRecyclerViewItemCompra = (ExpandableHeightRecyclerView) child.findViewById(R.id.lotecaRecyclerViewItemCompra);
                itemComprasContentModalideLinearLayout.addView(child);
                DetalhesApostaLotogolDetalhesComprasAdapter lotogolAdapter = new DetalhesApostaLotogolDetalhesComprasAdapter(listaAposta.get(idLista).getPartidasLotogol());
                lotogolRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(activity));
                lotogolRecyclerViewItemCompra.setAdapter(lotogolAdapter);
                lotogolRecyclerViewItemCompra.setExpanded(true);
            }else{
                View child = layoutInflater.inflate(R.layout.item_compras_generico_layout, null);
                itemComprasContentModalideLinearLayout.addView(child);
                TextView seusNumerosListaComprasTextView = (TextView) convertView.findViewById(R.id.SeusNumerosListaComprasTextView);
                seusNumerosListaComprasTextView.setText(numeroStr);
            }

        }else{
            convertView = layoutInflater.inflate(R.layout.list_group_compras, null);
            FrameLayout setaIndicador = (FrameLayout) convertView.findViewById(R.id.setaComprasGroup);
            setaIndicador.setVisibility(View.INVISIBLE);

            TextView ttvTipoJogo = convertView.findViewById(R.id.ttvTipoJogo);
            StringBuilder modalidade = new StringBuilder();
            for(String tipo : tipos){
                tipo = tipo.replace("_", " ");
                tipo = StringUtils.capitalizer(tipo);
                if(!modalidade.toString().contains(tipo)) {
                    if (!"".equals(modalidade.toString())) {
                        modalidade.append(", ");
                    }
                    modalidade.append(tipo);
                }
            }
            if(!"".equals(modalidade.toString())){
                ttvTipoJogo.setText(modalidade.toString());
            }

            TextView dataCompra = (TextView) convertView.findViewById(R.id.dataCompra);
            dataCompra.setText(compraDTO.getDataEfetivacaoDebito());
            TextView valorCompra = (TextView) convertView.findViewById(R.id.valorCompra);
            valorCompra.setText(caixa.gov.br.loterias.utils.ViewUtils.getMoedaFormat(compraDTO.getValorTotal()));

            TextView itemCompraOrdemTextView = (TextView) convertView.findViewById(R.id.itemCompraOrdemTextView);
            itemCompraOrdemTextView.setText("Compra " + ordemCompra);
        }



        return convertView;
    }
}
