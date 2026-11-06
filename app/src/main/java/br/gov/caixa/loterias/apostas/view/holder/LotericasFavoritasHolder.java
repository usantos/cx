package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.NomeLotericaFormatter;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemLotericaFavoritaListener;

public class LotericasFavoritasHolder extends LoteriasHolder<LotericaFavoritaDTO> implements View.OnClickListener {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView nomeLoterica;
	private TextView cidade;
	private View linhaNumero;

    ImageView excluirLixeira;

    TextView verBoloes;

	private OnItemLotericaFavoritaListener listener;


	public LotericasFavoritasHolder(Activity activity, View itemView, OnItemLotericaFavoritaListener listener) {
		super(itemView);
		this.activity = activity;
		nomeLoterica 	= itemView.findViewById(R.id.nome_loterica);
		cidade 		= itemView.findViewById(R.id.cidade_loterica);
		linhaNumero		= itemView.findViewById(R.id.linha_numero);
        excluirLixeira = itemView.findViewById(R.id.ic_lixeira);
        verBoloes = itemView.findViewById(R.id.ver_bolao);

		this.listener = listener;

        verBoloes.setOnClickListener(this);
        excluirLixeira.setOnClickListener(this);
		ViewCompat.setAccessibilityHeading(nomeLoterica,true);
	}

	@Override
	public void bind(LotericaFavoritaDTO loterica, int position) {

		nomeLoterica.setText(NomeLotericaFormatter.formatar(
				StringUtils.capitalizerNovo(loterica.getNomeFantasia()), 27));
		String nomeMunicipio = StringUtils.capitalizerNovo(loterica.getNomeMunicipio());
		String cidadeUf;
		if(nomeMunicipio.length() > 30){
			cidadeUf = nomeMunicipio.substring(0,20).concat("...") + " - " + loterica.getSiglaUf();
			cidade.setText(cidadeUf);
		}
		else {
			cidadeUf = nomeMunicipio + " - " + loterica.getSiglaUf();
			cidade.setText(cidadeUf);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
            case R.id.ic_lixeira: {
                String excluirFavorita = "Deseja excluir xxx da sua lista de favoritas?";

                final int pos = getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    break;
                }

                final String nome = nomeLoterica.getText().toString().replace('\n', ' ');
                final String mensagem = excluirFavorita.replace("xxx", nome);

				DialogUtils.dialogConfirmar(
						activity,
						mensagem,
						new OnDialogBotaoListener() {
							@Override
							public void onButtonClick(DialogInterface dialog, int which) {
								listener.excluir(LotericasFavoritasHolder.this, pos);
							}
						}
				);

                break;
            }

            case R.id.ver_bolao:
				listener.verBoloes(this, getBindingAdapterPosition());
				break;
		}
	}

}
