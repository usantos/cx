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
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;


public class ApostaFavoritaAdapter extends BaseAdapter {

    private final Context mContext;
    private List<ApostaFavoritaDTO> favoritosSalvos;

    // 1
    public ApostaFavoritaAdapter(Context context, List<ApostaFavoritaDTO> favoritosSalvos) {
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


        final TextView tituloApostaFavorira = convertView.findViewById( R.id.tituloApostaFavorira );
        tituloApostaFavorira.setText( aposta.getModalidade().getDescricao() );
        EstiloModalidade estilo = new EstiloModalidade(ModalidadeEnum.toString(favoritosSalvos.get(position).getModalidade().getDescricao().toLowerCase(new Locale("pt", "BR"))));
        tituloApostaFavorira.setText(favoritosSalvos.get(position).getModalidade().getDescricao());
        tituloApostaFavorira.setTextColor(parent.getResources().getColor(estilo.getCorClara()));

        final TextView numerosApostaFavorita = convertView.findViewById( R.id.numerosApostaFavorita );
        StringBuilder numeroStr = new StringBuilder();
        for (Integer num : aposta.getNumerosSelecionados()) {
            if (num.equals(aposta.getNumerosSelecionados().get( aposta.getNumerosSelecionados().size() - 1 ))) {
                numeroStr.append(String.format("%02d", num));
            } else {
                numeroStr.append(String.format("%02d - ", num));
            }
        }

        if (ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()) == ModalidadeEnum.TIMEMANIA) {
            numeroStr.append("\nTime do coração: ").append(aposta.getTimeDoCoracao().getNome());
        }

        if (ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()) == ModalidadeEnum.DIA_DE_SORTE) {
            numeroStr.append("\nMês de sorte: ").append(aposta.getMesDeSorte().getNome());
        }

        numerosApostaFavorita.setText(numeroStr.toString());

        final CheckBox checkbox = convertView.findViewById( R.id.checkboxApostaFavorita );
        checkbox.setVisibility(View.GONE);

        return convertView;
    }
}