package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class JogoBolaoHolder extends LoteriasHolder<ApostaBolaoDTO> {

	private final ModalidadeEnum modalidade;
	private final int corTitulo;
	private final List<Integer> dezenasSelecionadas;
	private final DezenaConfig dezenaConfig;
	private final TextView txtNumero, txtIndividualidade, mesTime;
	private final RecyclerView listView, listTrevos;
	private final Context context;
	private final EstiloModalidadeMKP estilo;
	private final DezenaConfig trevoConfig;
	private ListaDezenaRecyclerView trevosAdapter;

	public JogoBolaoHolder(ModalidadeEnum modalidade, int corTitulo, List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, View itemView, Context context, DezenaConfig trevoConfig) {
		super(itemView);
		this.modalidade = modalidade;
		this.estilo = new EstiloModalidadeMKP(modalidade);
		this.corTitulo = corTitulo;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		txtNumero = itemView.findViewById(R.id.id_numero_jogo);
		listView = itemView.findViewById(R.id.id_lista);
		txtIndividualidade = itemView.findViewById(R.id.mes_sorte_label);
		mesTime = itemView.findViewById(R.id.mes_sorte_valor);
		listTrevos = itemView.findViewById(R.id.id_trevos);
		this.context = context;
		this.trevoConfig = trevoConfig;
	}

	@Override
	public void bind(ApostaBolaoDTO aposta, int position) {
		txtNumero.setText("Jogo " + (position + 1));
		txtNumero.setTextColor(ContextCompat.getColor(context, corTitulo));

		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < aposta.getDezenas().size(); i++) {
			Integer numero;
			if(aposta.getDezenas().get(i) instanceof Double){
				numero = ((Double)aposta.getDezenas().get(i)).intValue();
			} else {
				numero = (Integer) aposta.getDezenas().get(i);
			}
			if (numero == 100 || numero == 0){
				dezenas.add(new Dezena("" + numero, Boolean.FALSE, "00" ));
			} else {
				dezenas.add(new Dezena("" + numero, Boolean.FALSE, "" + numero));
			}
		}

		ListaDezenaRecyclerView adapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
				dezenaConfig, onItemClickListener());
		listView.setAdapter(adapter);

		RecyclerView.LayoutManager layot = new GridLayoutManager(context, 6);
		listView.setLayoutManager(layot);

		apresentaDetalhesIndividuaisModalidades(aposta);
	}

	private void apresentaDetalhesIndividuaisModalidades(ApostaBolaoDTO aposta) {
		txtIndividualidade.setTextColor(ContextCompat.getColor(context, corTitulo));
		switch (modalidade){
			case TIMEMANIA:
				String time = "-";
				if (aposta.getTimeCoracao() != null && aposta.getTimeCoracao().getNomeComUf() != null){
					time = aposta.getTimeCoracao().getNomeComUf();
				}
				txtNumero.setTextColor(ContextCompat.getColor(context, estilo.getCorFonteFundoClaro()));
				txtIndividualidade.setTextColor(ContextCompat.getColor(context, estilo.getCorFonteFundoClaro()));
				apresentaDiaSorteTimeMania(context.getString(R.string.label_time_coracao_dois_pontos), time, estilo.getCorFonteFundoClaro());
				break;
			case DIA_DE_SORTE:
				String mes = "-";
				if (aposta.getMesSorte() != null && aposta.getMesSorte().getNome() != null){
					mes = aposta.getMesSorte().getNome();
				}
				apresentaDiaSorteTimeMania(context.getString(R.string.label_mes_sorte_dois_pontos), mes,
						estilo.getCorFonteFundoClaro());
				break;
			case MAIS_MILIONARIA:
				if (aposta.getTrevos() != null && !aposta.getTrevos().isEmpty()){
					txtIndividualidade.setVisibility(View.VISIBLE);
					txtIndividualidade.setTypeface(FonteUtils.getFonte(FontCaixaEnum.REGULAR));
					txtIndividualidade.setText(context.getString(R.string.trevosTitulo));

					List<Dezena> trevos = new ArrayList<>();
					for (int i = 0; i < aposta.getTrevos().size(); i++) {
						trevos.add(new Dezena("" + aposta.getTrevos().get(i), Boolean.FALSE, "" + aposta.getTrevos().get(i)));
					}

					listTrevos.setVisibility(View.VISIBLE);
					trevosAdapter = new ListaDezenaRecyclerView(trevos, new ArrayList<>(),
							getTrevoConfig(), onItemClickListener());
					listTrevos.setAdapter(trevosAdapter);

					RecyclerView.LayoutManager layuot = new GridLayoutManager(context, 6);
					listTrevos.setLayoutManager(layuot);
				}
				break;
		}
	}

	private DezenaConfig getTrevoConfig() {
		if (trevoConfig != null) {
			return this.trevoConfig;
		}

		ShapeConfig shapeConfig = new ShapeConfig(R.drawable.ic_item_trevo_branco,
				R.drawable.ic_item_trevo_selecionado,
				R.color.branco, R.color.milionaria_escuro_mkp);

		return new DezenaConfig(false, R.color.branco,
				R.layout.item_dezena_detalhe,
				shapeConfig, false);
	}

	private void apresentaDiaSorteTimeMania(String descricao, String valor, int cor){
		txtIndividualidade.setVisibility(View.VISIBLE);
		mesTime.setVisibility(View.VISIBLE);
		txtIndividualidade.setText(descricao);
		mesTime.setText(valor);
		mesTime.setTextColor(ContextCompat.getColor(context, cor));
	}

	private OnItemClickListener onItemClickListener() {
		return (holder, position) -> {};
	}

	public void bindResultado(ResultadoConcursoDTO resultado) {
		switch (modalidade){
			case DIA_DE_SORTE:
				if (resultado.getPremiacaoMesDeSorte() != null){
					if (mesTime.getText().toString().contains(resultado.getPremiacaoMesDeSorte().getMesDeSorte().getNome())){
						mesTime.setBackgroundColor(ContextCompat.getColor(context, R.color.branco));
						mesTime.setTextColor(ContextCompat.getColor(context, estilo.getCorFonteFundoClaro()));
					}
				}
				break;
			case TIMEMANIA:
				if (resultado.getPremiacaoTimeDoCoracao() != null){
					if (mesTime.getText().toString().contains(resultado.getPremiacaoTimeDoCoracao().getEquipe().getNome())){
						mesTime.setBackgroundColor(ContextCompat.getColor(context, R.color.branco));
						mesTime.setTextColor(ContextCompat.getColor(context, estilo.getCorFonteFundoClaro()));
					}
				}
				break;
			case MAIS_MILIONARIA:
				if (trevosAdapter != null && resultado.getTrevosSorteadosPrimeiroSorteio() != null ){
					List trevosSorteadosPrimeiroSorteio = resultado.getTrevosSorteadosPrimeiroSorteio();
					Collections.sort(trevosSorteadosPrimeiroSorteio);
					trevosAdapter.atualizaSelecionados(trevosSorteadosPrimeiroSorteio);
				}
				break;
		}
	}
}
