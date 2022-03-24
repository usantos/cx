package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DezenaLotogol;

/**
 * Created by joafilho on 09/01/2018.
 */

public class NumerosLotogolAdapter extends BaseAdapter {

    private final Context context;
    private final List<DezenaLotogol> placarList;

    public NumerosLotogolAdapter(Context mContext, List<DezenaLotogol> placarList) {
        this.context = mContext;
        this.placarList = placarList;
    }

    @Override
    public int getCount() {
        return placarList.size();
    }

    @Override
    public Object getItem(int i) {
        return placarList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.linearlayout_numeros_lotogol, null);
        }

        view.findViewById(R.id.linearLayoutNumerosPlacar).setBackgroundResource(placarList.get(i).getBackGround());
        view.setTag(placarList.get(i).getValor());
        return view;
    }
}
