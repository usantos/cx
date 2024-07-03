package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;

public class PartidaLotecaHolder extends LoteriasHolder<PartidaLotecaDTO> {

	private TextView nomeEquipe1, nomeEquipe2, nomeEmpate;
	private ImageView checkEquipe1, checkEquipe2, checkEmpate;
	private Context context;
	private ConfiguracaoLoteca configuracaoLoteca;

	public PartidaLotecaHolder(View itemView, Context context, ConfiguracaoLoteca configuracaoLoteca) {
		super(itemView);
		nomeEquipe1 = itemView.findViewById(R.id.nome_equipe_1);
		nomeEquipe2 = itemView.findViewById(R.id.nome_equipe_2);
		nomeEmpate  = itemView.findViewById(R.id.nome_empate);
		checkEquipe1 = itemView.findViewById(R.id.check_equipe_1);
		checkEquipe2 = itemView.findViewById(R.id.check_equipe_2);
		checkEmpate = itemView.findViewById(R.id.check_empate);
		this.context = context;
		this.configuracaoLoteca = configuracaoLoteca;
	}

	@Override
	public void bind(PartidaLotecaDTO partida, int position) {
		nomeEquipe1.setText(Utils.getEquipeComUf(partida.getEquipe1().getParametroEquipe()) + " " + partida.getLegendaAbreviada());
		nomeEquipe2.setText(Utils.getEquipeComUf(partida.getEquipe2().getParametroEquipe()) + " " + partida.getLegendaAbreviada());
		if (configuracaoLoteca != null) {
			nomeEquipe1.setTextColor(context.getResources().getColor(configuracaoLoteca.getCorTexto()));
			nomeEquipe2.setTextColor(context.getResources().getColor(configuracaoLoteca.getCorTexto()));
			nomeEmpate.setTextColor(context.getResources().getColor(configuracaoLoteca.getCorTexto()));
		}
		aplicaChecks(partida);
	}

	private void aplicaChecks(PartidaLotecaDTO partida) {
		aplicaCheck(checkEquipe1, partida.getEquipe1().getVitoria());
		aplicaCheck(checkEmpate, partida.getEmpate());
		aplicaCheck(checkEquipe2, partida.getEquipe2().getVitoria());
	}

	private void aplicaCheck(ImageView imageView, boolean isCheck) {
		if (isCheck){
			imageView.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_radio_checked, null));
			if (configuracaoLoteca != null) {
				imageView.setImageDrawable(VectorUtils.getShape(R.drawable.ic_radio_checked, configuracaoLoteca.getCorTexto()));
			}
		} else {
			imageView.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_radio_unchecked, null));
			if (configuracaoLoteca != null) {
				imageView.setImageDrawable(VectorUtils.getShape(R.drawable.ic_radio_unchecked, configuracaoLoteca.getCorTexto()));
			}
		}
	}

}
