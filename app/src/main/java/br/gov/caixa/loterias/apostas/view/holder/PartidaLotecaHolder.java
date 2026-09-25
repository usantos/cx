package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;

public class PartidaLotecaHolder extends LoteriasHolder<PartidaLotecaDTO> {

	private TextView nomeEquipe1, nomeEquipe2, nomeEmpate;
	private ImageView checkEquipe1, checkEquipe2, checkEmpate;
	private Context context;
	private ConfiguracaoLoteca configuracaoLoteca;
	private ResultadoConcursoDTO resultado;

	private Boolean isEspecial;

	public PartidaLotecaHolder(View itemView, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso, Boolean isEspecial) {
		super(itemView);
		nomeEquipe1 = itemView.findViewById(R.id.nome_equipe_1);
		nomeEquipe2 = itemView.findViewById(R.id.nome_equipe_2);
		nomeEmpate  = itemView.findViewById(R.id.nome_empate);
		checkEquipe1 = itemView.findViewById(R.id.check_equipe_1);
		checkEquipe2 = itemView.findViewById(R.id.check_equipe_2);
		checkEmpate = itemView.findViewById(R.id.check_empate);
		this.context = context;
		this.configuracaoLoteca = configuracaoLoteca;
		this.resultado = resultadoConcurso;
		this.isEspecial = isEspecial;
	}

	@Override
	public void bind(PartidaLotecaDTO partida, int position) {
		//TODO//
		String legenda = " " + partida.getLegendaAbreviada();
		if (partida.getEquipe1().getParametroEquipe().getIndicadorSelecao() != null && partida.getEquipe1().getParametroEquipe().getIndicadorSelecao()) {
			nomeEquipe1.setText(Utils.getEquipeNome(partida.getEquipe1().getParametroEquipe()) + legenda);
		} else {
			nomeEquipe1.setText(Utils.getEquipeComUf(partida.getEquipe1().getParametroEquipe()) + legenda);
		}

		if (partida.getEquipe2().getParametroEquipe().getIndicadorSelecao() != null && partida.getEquipe2().getParametroEquipe().getIndicadorSelecao()) {
			nomeEquipe2.setText(Utils.getEquipeNome(partida.getEquipe2().getParametroEquipe()) + legenda);
		} else {
			nomeEquipe2.setText(Utils.getEquipeComUf(partida.getEquipe2().getParametroEquipe()) + legenda);
		}

		if (configuracaoLoteca != null) {
			nomeEquipe1.setTextColor(context.getResources().getColor(configuracaoLoteca.getCorTexto()));
			nomeEquipe2.setTextColor(context.getResources().getColor(configuracaoLoteca.getCorTexto()));
			nomeEmpate.setTextColor(context.getResources().getColor(configuracaoLoteca.getCorTexto()));
		}
		aplicaChecks(partida);
		if (resultado != null){
			aplicaResultados(position);
		}
	}

	private void aplicaResultados(int position) {
		if (resultado.getPartidasLoteca() != null && !resultado.getPartidasLoteca().isEmpty()){
			PartidaLotecaDTO partida = (PartidaLotecaDTO) resultado.getPartidasLoteca().get(position);
			if (partida.getEquipe1() != null && partida.getEquipe1().getVitoria() != null && partida.getEquipe1().getVitoria()){
				aplicaCorSelecionado(nomeEquipe1);
			} else if (partida.getEquipe2() != null && partida.getEquipe2().getVitoria() != null && partida.getEquipe2().getVitoria()){
				aplicaCorSelecionado(nomeEquipe2);
			} else if (partida.getEmpate() != null && partida.getEmpate()){
				aplicaCorSelecionado(nomeEmpate);
			}
		}
	}

	private void aplicaCorSelecionado(TextView txt) {
		txt.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTextoSelecionado()));
		txt.setBackgroundColor(ContextCompat.getColor(context, configuracaoLoteca.getBackgroundSelecionado()));
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
