package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class PartidaLotecaHolder extends LoteriasHolder<PartidaLotecaDTO> {

	private final TextView nomeEquipe1;
    private final TextView nomeEquipe2;
    private final TextView nomeEmpate;
    private final TextView jogo;
	private final ImageView checkEquipe1;
    private final ImageView checkEquipe2;
    private final ImageView checkEmpate;
	private final Context context;
	private final ConfiguracaoLoteca configuracaoLoteca;
	private final ResultadoConcursoDTO resultado;

	private final Boolean isEspecial;

	public PartidaLotecaHolder(View itemView, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso, Boolean isEspecial) {
		super(itemView);
		jogo = itemView.findViewById(R.id.title);
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
		String legenda = " " + partida.getLegendaAbreviada();

		nomeEquipe1.setText(getNomeEquipe(partida.getEquipe1()) + legenda);
		nomeEquipe2.setText(getNomeEquipe(partida.getEquipe2()) + legenda);

		aplicaChecks(partida);
		if (resultado != null){
			aplicaResultados(position);
		}
		jogo.setText(context.getString(R.string.jogo_numero, position + 1));
		aplicaFonte(partida);
		if (configuracaoLoteca != null) {
			isCompras();
		}
	}

	private String getNomeEquipe(EquipeDTO equipe) {
		if (equipe == null) {
			return "";
		}
		ParametroEquipe parametro = equipe.getParametroEquipe();
		if (parametro == null) {
			return equipe.getNome() != null ? equipe.getNome() : "";
		}
		if (Boolean.TRUE.equals(parametro.getIndicadorSelecao())) {
			return Utils.getEquipeNome(parametro);
		}
		return Utils.getEquipeComUf(parametro);
	}
	private boolean isVitoria(EquipeDTO equipe) {
		return equipe != null && Boolean.TRUE.equals(equipe.getVitoria());
	}
	private void isCompras(){
		jogo.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
		nomeEquipe1.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
		nomeEquipe2.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
		nomeEmpate.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTexto()));
	}

	private void aplicaFonte(PartidaLotecaDTO partida) {
		aplicaFonte(nomeEmpate, Boolean.TRUE.equals(partida.getEmpate()));
		aplicaFonte(nomeEquipe1, isVitoria(partida.getEquipe1()));
		aplicaFonte(nomeEquipe2, isVitoria(partida.getEquipe2()));
	}

	private void aplicaFonte(TextView txt, boolean devePintar) {
		if (devePintar) {
			txt.setTypeface(ViewUtils.getFontCaixaStdBold(context));
		}
	}

	private void aplicaResultados(int position) {
		if (resultado.getPartidasLoteca() != null && !resultado.getPartidasLoteca().isEmpty()){
			PartidaLotecaDTO partida = (PartidaLotecaDTO) resultado.getPartidasLoteca().get(position);
			if (isVitoria(partida.getEquipe1())){
				aplicaCorSelecionado(nomeEquipe1);
			} else if (isVitoria(partida.getEquipe2())){
				aplicaCorSelecionado(nomeEquipe2);
			} else if (Boolean.TRUE.equals(partida.getEmpate())){
				aplicaCorSelecionado(nomeEmpate);
			}
		}
	}

	private void aplicaCorSelecionado(TextView txt) {
		txt.setTextColor(ContextCompat.getColor(context, configuracaoLoteca.getCorTextoSelecionado()));
		txt.setBackgroundColor(ContextCompat.getColor(context, configuracaoLoteca.getBackgroundSelecionado()));
	}

	private void aplicaChecks(PartidaLotecaDTO partida) {
		aplicaCheck(checkEquipe1, isVitoria(partida.getEquipe1()));
		aplicaCheck(checkEmpate, Boolean.TRUE.equals(partida.getEmpate()));
		aplicaCheck(checkEquipe2, isVitoria(partida.getEquipe2()));
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