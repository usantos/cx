package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;

/**
 * Created by cedesbr450 on 30/01/18.
 */


public class ListaComprasDetalhesAdapter extends BaseAdapter {

    private final Context mContext;
    List<ApostaDTO> listaAposta;
    private final CompraDTO compraDTO;

    // 1
    public ListaComprasDetalhesAdapter(Context context, List<ApostaDTO> listaAposta, CompraDTO compraDTO) {
        this.mContext = context;
        this.listaAposta = listaAposta;
        this.compraDTO = compraDTO;
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

            TextView TipoPremioListaComprasTextView = (TextView) convertView.findViewById(R.id.TipoPremioListaComprasTextView);
            TipoPremioListaComprasTextView.setText(ApostaDTO.fromStringModalidade(listaAposta.get(position-1).getModalidade()));

            TextView dataConcursoListaComprasTextView = (TextView) convertView.findViewById(R.id.dataConcursoListaComprasTextView);

            TextView cocursoListaComprasTextView = (TextView) convertView.findViewById(R.id.cocursoListaComprasTextView);

            TextView SeusNumerosListaComprasTextView = (TextView) convertView.findViewById(R.id.SeusNumerosListaComprasTextView);

            TextView statusResultadoApostaListaComprasTextView = (TextView) convertView.findViewById(R.id.statusResultadoApostaListaComprasTextView);



        }else{
            convertView = layoutInflater.inflate(R.layout.list_group_compras, null);
            ImageView setaIndicador = (ImageView) convertView.findViewById(R.id.setaComprasGroup);
            setaIndicador.setVisibility(View.INVISIBLE);

            TextView dataCompra = (TextView) convertView.findViewById(R.id.dataCompra);
            dataCompra.setText(compraDTO.getDataEfetivacaoDebito());
            TextView valorCompra = (TextView) convertView.findViewById(R.id.valorCompra);
            valorCompra.setText(caixa.gov.br.loterias.utils.ViewUtils.getMoedaFormat(compraDTO.getValorTotal()));
        }



        return convertView;
    }
}
