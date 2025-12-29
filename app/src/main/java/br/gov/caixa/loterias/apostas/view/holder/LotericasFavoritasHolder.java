package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnItemLotericaFavoritaListener;

public class LotericasFavoritasHolder extends LoteriasHolder<LotericaFavoritaDTO> implements View.OnClickListener {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView nomeLoterica;
	private TextView codLoterica;
	private TextView cidade;
	private View linhaNumero;

    ImageView excluirLixeira;

    TextView verBoloes;

	private OnItemLotericaFavoritaListener listener;


	public LotericasFavoritasHolder(Activity activity, View itemView, OnItemLotericaFavoritaListener listener) {
		super(itemView);
		this.activity = activity;
		nomeLoterica 	= itemView.findViewById(R.id.nome_loterica);
		codLoterica 	= itemView.findViewById(R.id.cod_loterica);
		cidade 		= itemView.findViewById(R.id.cidade_loterica);
		linhaNumero		= itemView.findViewById(R.id.linha_numero);
        excluirLixeira = itemView.findViewById(R.id.ic_lixeira);
        verBoloes = itemView.findViewById(R.id.ver_bolao);

		this.listener = listener;

        verBoloes.setOnClickListener(this);
        excluirLixeira.setOnClickListener(this);
	}

	@Override
	public void bind(LotericaFavoritaDTO loterica, int position) {

		codLoterica.setText(String.valueOf(loterica.getCodigo()));
		if (loterica.getNomeFantasia().length() > 30){
			nomeLoterica.setText(loterica.getNomeFantasia().substring(0,20).concat("..."));
		} else {
			nomeLoterica.setText(loterica.getNomeFantasia());
		}
		String cidadeUf = loterica.getNomeMunicipio() + " - " + loterica.getSiglaUf();
		if (cidadeUf.length() > 30) {
			cidade.setText(cidadeUf.substring(0,20).concat("..."));
		} else {
			cidade.setText(cidadeUf);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
            case R.id.ic_lixeira: {
                String excluirFavorita = "Deseja excluir xxx da sua lista de favoritas?";

                final int pos = getAbsoluteAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    break;
                }

                final String nome = nomeLoterica.getText().toString();
                final String mensagem = excluirFavorita.replace("xxx", nome);

                DialogInterface.OnClickListener positive = (dialogInterface, which) -> {
                    listener.excluir(this, pos);
                    dialogInterface.dismiss();
                };

                DialogInterface.OnClickListener negative = (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                };

                AlertDialog dialog = ViewUtils.dialogoNew(
                        activity,
                        "Atenção",
                        mensagem,
                        positive,
                        negative,
                        R.string.cancelar,
                        R.string.confirmar
                );

                dialog.show();

                break;
            }

            case R.id.ver_bolao:
				listener.verMais(this, getAbsoluteAdapterPosition());
				break;
		}
	}

}
