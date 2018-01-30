package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.DetalhesPremioDTO;

/**
 * Created by cedesbr450 on 30/01/18.
 */


public class ListaComprasDetalhesAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<DetalhesPremioDTO> detalhesPremioDTO;

    // 1
    public ListaComprasDetalhesAdapter(Context context, List<DetalhesPremioDTO> detalhesPremioDTO) {
        this.mContext = context;
        this.detalhesPremioDTO = detalhesPremioDTO;
    }

    // 2
    @Override
    public int getCount() {
        return detalhesPremioDTO.size();
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
//            if (position == 0){
//                convertView = layoutInflater.inflate(R.layout.list_group_compras, null);
//            }else{
//                convertView = layoutInflater.inflate(R.layout.list_item_compras, null);
//            }
            convertView = layoutInflater.inflate(R.layout.list_item_compras, null);
        }

        return convertView;
    }
}
