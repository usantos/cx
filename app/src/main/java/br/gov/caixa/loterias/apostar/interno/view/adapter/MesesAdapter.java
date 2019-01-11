package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostar.interno.view.custom.ItemRetanguloTextView;
import br.gov.caixa.loterias.apostar.interno.view.custom.ItemRetanguloTextView_;

public class MesesAdapter extends BaseAdapter {

    private List<ParametroMesDeSorte> meses;

    public MesesAdapter(List<ParametroMesDeSorte> meses) {
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
        convertView = ItemRetanguloTextView_.build(parent.getContext());
        ((ItemRetanguloTextView) convertView).setLayout(meses.get(position));
        return convertView;
    }
}
