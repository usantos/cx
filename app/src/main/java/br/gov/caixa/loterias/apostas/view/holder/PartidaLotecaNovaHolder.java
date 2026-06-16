package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;

public class PartidaLotecaNovaHolder extends LoteriasHolder<PartidaLotecaDTO> {

	private TextView labelJogo, nomeEquipe1, nomeEquipe2, nomeEmpate;
	private RadioButton checkEquipe1, checkEquipe2, checkEmpate;
	private Context context;
	private ConfiguracaoLoteca configuracaoLoteca;
	private ResultadoConcursoDTO resultado;
	private View vLinhaDivisoria;

	private Boolean isEspecial;

	public PartidaLotecaNovaHolder(View itemView, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso, Boolean isEspecial) {
		super(itemView);
		labelJogo = itemView.findViewById(R.id.label_jogo);
		vLinhaDivisoria = itemView.findViewById(R.id.v_linha_divisoria_loteca);
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
		//String nomeequipe1 = Utils.getEquipeComUf(partida.getEquipe1().getParametroEquipe()) + " " + partida.getLegendaAbreviada();
		//String nomeequipe2 = Utils.getEquipeComUf(partida.getEquipe2().getParametroEquipe()) + " " + partida.getLegendaAbreviada();

		labelJogo.setText("Jogo " + String.format("%02d", position + 1));

		//if (partida.getEquipe1().getIndicadorSelecao() == null || partida.getEquipe1().getIndicadorSelecao()) {
		if (isEspecial != null && isEspecial) {
			nomeEquipe1.setText(Utils.getEquipeNome(partida.getEquipe1().getParametroEquipe()));
		} else {
			nomeEquipe1.setText(Utils.getEquipeComUf(partida.getEquipe1().getParametroEquipe()));
		}

		//if (partida.getEquipe2().getIndicadorSelecao() == null || partida.getEquipe2().getIndicadorSelecao()) {
		if (isEspecial != null && isEspecial) {
			nomeEquipe2.setText(Utils.getEquipeNome(partida.getEquipe2().getParametroEquipe()));
		} else {
			nomeEquipe2.setText(Utils.getEquipeComUf(partida.getEquipe2().getParametroEquipe()));
		}

		if (configuracaoLoteca != null) {
			labelJogo.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
			vLinhaDivisoria.setBackgroundColor(context.getColor(configuracaoLoteca.getCorTexto()));
			checkEquipe1.setButtonTintList(ColorStateList.valueOf(context.getColor(configuracaoLoteca.getCorTexto())));
			checkEquipe2.setButtonTintList(ColorStateList.valueOf(context.getColor(configuracaoLoteca.getCorTexto())));
			checkEmpate.setButtonTintList(ColorStateList.valueOf(context.getColor(configuracaoLoteca.getCorTexto())));
			nomeEquipe1.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
			nomeEquipe2.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
			nomeEmpate.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
		}

		aplicaChecks(partida);

		boolean sel1 = partida.getEquipe1().getVitoria();
		boolean sel2 = partida.getEquipe2().getVitoria();
		boolean selEmpate = partida.getEmpate();

		// resultado (se existir)
		boolean acerto1 = false;
		boolean acerto2 = false;
		boolean acertoEmpate = false;

		if (resultado != null && resultado.getPartidasLoteca() != null) {
			PartidaLotecaDTO res = (PartidaLotecaDTO) resultado.getPartidasLoteca().get(position);

			acerto1 = res.getEquipe1() != null && Boolean.TRUE.equals(res.getEquipe1().getVitoria());
			acerto2 = res.getEquipe2() != null && Boolean.TRUE.equals(res.getEquipe2().getVitoria());
			acertoEmpate = Boolean.TRUE.equals(res.getEmpate());
		}

		aplicaLegenda(nomeEquipe1, sel1, sel1 && acerto1);
		aplicaLegenda(nomeEquipe2, sel2, sel2 && acerto2);
		aplicaLegenda(nomeEmpate, selEmpate, selEmpate && acertoEmpate);
	}

	private void aplicaChecks(PartidaLotecaDTO partida) {
		checkEquipe1.setChecked(partida.getEquipe1().getVitoria());
		checkEmpate.setChecked(partida.getEmpate());
		checkEquipe2.setChecked(partida.getEquipe2().getVitoria());
	}

	private void aplicaLegenda(TextView txt, boolean selecionado, boolean acertou) {

		if (acertou) { 									//Acertou palpite
			txt.setBackground(ContextCompat.getDrawable(context, R.drawable.background_roudend_less_branco));
			txt.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTextoSelecionado()));
			txt.setPadding(16, 8, 16, 8);
		} else if (selecionado && resultado == null) { 	//Escolhido e NÃO apurado
			txt.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rouded_border_white));
			txt.setTextColor(ContextCompat.getColor(context, android.R.color.white));
			txt.setPadding(16, 8, 16, 8);
		} else if (selecionado) { 						//Escolhido e apurado (mas não acertou)
			txt.setBackground(null);
			txt.setTextColor(ContextCompat.getColor(context, android.R.color.white));
			txt.setPadding(0, 0, 0, 0);
		} else { 										//Não escolhido
			txt.setBackground(null);
			txt.setTextColor(ContextCompat.getColor(context, android.R.color.white));
			txt.setPadding(0, 0, 0, 0);
		}
	}

}
