package br.gov.caixa.loterias.apostar.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.CompraDTO;

/**
 * Created by cedesbr450 on 30/01/18.
 */

public class ListaComprasAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<CompraDTO> compraDTO;

    // 1
    public ListaComprasAdapter(Context context, List<CompraDTO> compraDTO) {
        this.mContext = context;
        this.compraDTO = compraDTO;
    }

    // 2
    @Override
    public int getCount() {
        return compraDTO.size();
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
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.list_group_compras, null);
        }

        TextView dataCompra = (TextView) convertView.findViewById(R.id.dataCompra);
        dataCompra.setText(compraDTO.get(position).getDataEfetivacaoDebito());
        TextView valorCompra = (TextView) convertView.findViewById(R.id.valorCompra);
        valorCompra.setText(br.gov.caixa.loterias.apostar.utils.ViewUtils.getMoedaFormat(compraDTO.get(position).getValorTotal()));

        return convertView;
    }
}
