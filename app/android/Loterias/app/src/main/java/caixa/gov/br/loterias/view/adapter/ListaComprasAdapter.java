package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.CompraDTO;
import caixa.gov.br.loterias.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.model.TipoAposta;
import caixa.gov.br.loterias.utils.ViewUtils;

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
        valorCompra.setText(caixa.gov.br.loterias.utils.ViewUtils.getMoedaFormat(compraDTO.get(position).getValorTotal()));

        return convertView;
    }
}
