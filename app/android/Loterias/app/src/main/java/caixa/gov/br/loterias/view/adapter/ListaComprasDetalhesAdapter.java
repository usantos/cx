package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.EstiloModalidadeAposta;

/**
 * Created by cedesbr450 on 30/01/18.
 */


public class ListaComprasDetalhesAdapter extends BaseAdapter {

    private final Context mContext;
    List<ApostaDTO> listaAposta;
    private final CompraDTO compraDTO;
    private final int ordemCompra;
    // 1
    public ListaComprasDetalhesAdapter(Context context, List<ApostaDTO> listaAposta, CompraDTO compraDTO, int ordemCompra) {
        this.mContext = context;
        this.listaAposta = listaAposta;
        this.compraDTO = compraDTO;
        this.ordemCompra = ordemCompra;
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
            if (listaAposta.get(idLista).getIndicadorSurpresinha().getValor() == 2){
                int contador = 1;
                for (int i =0; i < listaAposta.get(idLista).getQuantidadeNumeros(); i++){
                    if (contador == 5){
                        numeroStr = numeroStr + "XX\n";
                        contador = 0;
                    }else if (i == listaAposta.get(idLista).getQuantidadeNumeros() -1){
                        numeroStr = numeroStr + "XX";
                    }else{
                        numeroStr = numeroStr + "XX - ";
                    }
                    contador ++;
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

            TextView seusNumerosListaComprasTextView = (TextView) convertView.findViewById(R.id.SeusNumerosListaComprasTextView);
            seusNumerosListaComprasTextView.setText(numeroStr);

            TextView statusResultadoApostaListaComprasTextView = (TextView) convertView.findViewById(R.id.statusResultadoApostaListaComprasTextView);
            statusResultadoApostaListaComprasTextView.setText(listaAposta.get(idLista).getSituacao().getDescricao());

            EstiloModalidadeAposta estilo = new EstiloModalidadeAposta(listaAposta.get(idLista).getModalidade());

            LinearLayout topListItemComprasLinearLayout = (LinearLayout) convertView.findViewById(R.id.topListItemComprasLinearLayout);
            topListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorClara()));

            LinearLayout bottomListItemComprasLinearLayout = (LinearLayout) convertView.findViewById(R.id.bottomListItemComprasLinearLayout);
            bottomListItemComprasLinearLayout.setBackgroundColor(mContext.getResources().getColor(estilo.getCorEscura()));


        }else{
            convertView = layoutInflater.inflate(R.layout.list_group_compras, null);
            ImageView setaIndicador = (ImageView) convertView.findViewById(R.id.setaComprasGroup);
            setaIndicador.setVisibility(View.INVISIBLE);

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
