package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import caixa.gov.br.loterias.model.bo.silce.dto.MesDTOResponse;
import caixa.gov.br.loterias.view.custom.ItemRetanguloTextView;
import caixa.gov.br.loterias.view.custom.ItemRetanguloTextView_;

public class MesesAdapter extends BaseAdapter {

    private List<MesDTOResponse> meses;

    public MesesAdapter(List<MesDTOResponse> meses) {
        this.meses = meses;
    }

    @Override
    public int getCount() {
        return this.meses.size();
    }

    @Override
    public Object getItem(int position) {
        return this.meses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = ItemRetanguloTextView_.build(parent.getContext());
        }

        ((ItemRetanguloTextView)convertView).setLayout(meses.get(position));
        return convertView;
    }
}
