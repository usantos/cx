package br.gov.caixa.loterias.apostar.interno.view.adapter;

/**
 * Created by cedesbr450 on 20/12/17.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.ApostaFavorita;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;


public class ApostaFavoritaAdapter extends BaseAdapter {

    private final Context mContext;
    private List<ApostaFavoritaDTO> favoritosSalvos;

    // 1
    public ApostaFavoritaAdapter(Context context, List<ApostaFavoritaDTO> favoritosConfiguracao) {
        this.mContext = context;
        this.favoritosSalvos = favoritosSalvos;
    }

    // 2
    @Override
    public int getCount() {
        return favoritosSalvos.size();
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
            final LayoutInflater layoutInflater = LayoutInflater.from( mContext );
            convertView = layoutInflater.inflate( R.layout.linearlayout_aposta_favorita, null );
        }

        final ApostaFavoritaDTO aposta = favoritosSalvos.get( position );


        final TextView tituloApostaFavorira = (TextView) convertView.findViewById( R.id.tituloApostaFavorira );
        tituloApostaFavorira.setText( aposta.getModalidade().getDescricao() );

        final TextView numerosApostaFavorita = (TextView) convertView.findViewById( R.id.numerosApostaFavorita );
        String numeroStr = "";
        for (Integer num : aposta.getNumerosSelecionados()) {
            if (num == (aposta.getNumerosSelecionados().get( aposta.getNumerosSelecionados().size() - 1 ))) {
                numeroStr += String.format( "%02d", num );
            } else {
                numeroStr += String.format( "%02d - ", num );
            }
        }
        numerosApostaFavorita.setText( numeroStr );


        return convertView;
    }
}