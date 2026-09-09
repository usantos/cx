package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.DescricaoLoteca;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class PartidaLotecaNovaHolder extends LoteriasHolder<PartidaLotecaDTO> {

	private ConstraintLayout cl_partida;
	private TextView labelJogo, labelVencedor, nomeEquipe1, nomeEquipe2, nomeEmpate;
	private View checkEquipe1, checkEquipe2, checkEmpate;
	private Context context;
	private ConfiguracaoLoteca configuracaoLoteca;
	private ResultadoConcursoDTO resultado;
	private View vLinhaDivisoria;

	//private Boolean isEspecial;

	public PartidaLotecaNovaHolder(View itemView, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso) {
		super(itemView);
		cl_partida = itemView.findViewById(R.id.cl_partida);
		labelJogo = itemView.findViewById(R.id.label_jogo);
		labelVencedor = itemView.findViewById(R.id.label_vencedor);
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
		//this.isEspecial = isEspecial;
	}

	@Override
	public void bind(PartidaLotecaDTO partida, int position) {

//		if (isEspecial != null && isEspecial) {
//			nomeEquipe1.setText(Utils.getEquipeNome(partida.getEquipe1().getParametroEquipe()));
//		} else {
			nomeEquipe1.setText(Utils.getEquipeComUf(partida.getEquipe1().getParametroEquipe()));
//		}

//		if (isEspecial != null && isEspecial) {
//			nomeEquipe2.setText(Utils.getEquipeNome(partida.getEquipe2().getParametroEquipe()));
//		} else {
			nomeEquipe2.setText(Utils.getEquipeComUf(partida.getEquipe2().getParametroEquipe()));
//		}

		if (configuracaoLoteca != null) {
			labelJogo.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
			labelVencedor.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
			vLinhaDivisoria.setBackgroundColor(context.getColor(configuracaoLoteca.getCorTexto()));
			//checkEquipe1.setButtonTintList(ColorStateList.valueOf(context.getColor(configuracaoLoteca.getCorTexto())));
			//checkEquipe2.setButtonTintList(ColorStateList.valueOf(context.getColor(configuracaoLoteca.getCorTexto())));
			//checkEmpate.setBauttonTintList(ColorStateList.valueOf(context.getColor(configuracaoLoteca.getCorTexto())));
			nomeEquipe1.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
			nomeEquipe2.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
			nomeEmpate.setTextColor(context.getColor(configuracaoLoteca.getCorTexto()));
		}

		String jogo = "Jogo " + String.format("%02d", position + 1) + DescricaoLoteca.descricaoPartida(partida);
		String descricaoAcessibilidade = jogo;

		boolean acerto1 = false;
		boolean acerto2 = false;
		boolean acertoEmpate = false;

		//Resultado (se existir)
		if (resultado != null && resultado.getPartidasLoteca() != null) {
			PartidaLotecaDTO res = (PartidaLotecaDTO) resultado.getPartidasLoteca().get(position);

			acerto1 = res.getEquipe1() != null && Boolean.TRUE.equals(res.getEquipe1().getVitoria());
			acerto2 = res.getEquipe2() != null && Boolean.TRUE.equals(res.getEquipe2().getVitoria());
			acertoEmpate = Boolean.TRUE.equals(res.getEmpate());

			jogo += ":";

			labelVencedor.setVisibility(View.VISIBLE);
			if (acerto1) {
				labelVencedor.setText(nomeEquipe1.getText().toString());
				descricaoAcessibilidade += ", resultado " + nomeEquipe1.getText().toString();
			} else if (acerto2) {
				labelVencedor.setText(nomeEquipe2.getText().toString());
				descricaoAcessibilidade += ", resultado " + nomeEquipe2.getText().toString();
			} else {
				labelVencedor.setText(nomeEmpate.getText().toString());
				descricaoAcessibilidade += ", resultado " + nomeEmpate.getText().toString();
			}
		}

		labelJogo.setText(jogo);
		//labelJogo.setContentDescription(descricaoAcessibilidade);
		//labelJogo.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
		cl_partida.setContentDescription(descricaoAcessibilidade);
		//cl_partida.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

        boolean sel1 = partida.getEquipe1().getVitoria();
		boolean sel2 = partida.getEquipe2().getVitoria();
		boolean selEmpate = partida.getEmpate();

		aplicaLegenda(nomeEquipe1, sel1, sel1 && acerto1);
		aplicaLegenda(nomeEquipe2, sel2, sel2 && acerto2);
		aplicaLegenda(nomeEmpate, selEmpate, selEmpate && acertoEmpate);

		aplicaChecks(partida);

		//Ultima linha
		if (position == 13) {
			vLinhaDivisoria.setVisibility(View.GONE);
		}
	}

	private void aplicaChecks(PartidaLotecaDTO partida) {
		//checkEquipe1.setChecked(partida.getEquipe1().getVitoria());
		//checkEmpate.setChecked(partida.getEmpate());
		//checkEquipe2.setChecked(partida.getEquipe2().getVitoria());

		if (partida.getEquipe1().getVitoria()) {
			checkEquipe1.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_radio_checked));
			checkEquipe1.setContentDescription("Selecionado");
		} else {
			checkEquipe1.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_radio_unchecked));
			checkEquipe1.setContentDescription("Não Selecionado");
		}
		checkEquipe1.getBackground().setTint(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));

		if (partida.getEmpate()) {
			checkEmpate.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_radio_checked));
			checkEmpate.setContentDescription("Selecionado");
		} else {
			checkEmpate.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_radio_unchecked));
			checkEmpate.setContentDescription("Não Selecionado");
		}
		checkEmpate.getBackground().setTint(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));

		if (partida.getEquipe2().getVitoria()) {
			checkEquipe2.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_radio_checked));
			checkEquipe2.setContentDescription("Selecionado");
		} else {
			checkEquipe2.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_radio_unchecked));
			checkEquipe2.setContentDescription("Não Selecionado");
		}
		checkEquipe2.getBackground().setTint(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
	}

	private void aplicaLegenda(TextView tv, boolean selecionado, boolean acertou) {

		String textoOriginal = tv.getText().toString();
		String descricaoAcessibilidade = textoOriginal;

		if (acertou) { 									//Acertou palpite
			tv.setBackground(ContextCompat.getDrawable(context, R.drawable.background_roudend_less_branco));
			tv.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTextoSelecionado()));
			tv.setText(ViewUtils.textCaixaSTDBold(context, "_"+tv.getText().toString()+"_"));
			tv.setPadding(16, 8, 16, 8);
			descricaoAcessibilidade = textoOriginal + ", Você acertou o palpite";
		} else if (selecionado && resultado == null) { 	//Escolhido e NÃO apurado
			tv.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rouded_border_white));
			tv.setTextColor(ContextCompat.getColor(context,  configuracaoLoteca.getCorTexto()));
			tv.setText(ViewUtils.textCaixaSTDBold(context, "_"+tv.getText().toString()+"_"));
			tv.setPadding(16, 8, 16, 8);
			descricaoAcessibilidade = textoOriginal + ", Palpite escolhido e não apurado";
		} else if (selecionado) { 						//Escolhido e apurado (mas não acertou)
			tv.setBackground(null);
			tv.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
			tv.setText(ViewUtils.textCaixaSTDBold(context, "_"+tv.getText().toString()+"_"));
			tv.setPadding(0, 0, 0, 0);
			descricaoAcessibilidade = textoOriginal + ", Você não acertou o palpite";
		} else { 										//Não escolhido
			tv.setBackground(null);
			tv.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
			tv.setPadding(0, 0, 0, 0);
			descricaoAcessibilidade = textoOriginal; // normal
		}

		tv.setContentDescription(descricaoAcessibilidade);
	}

}
