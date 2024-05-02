package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class JogoBolaoHolder extends LoteriasHolder<ApostaBolaoDTO> {

	private ModalidadeEnum modalidade;
	private int corTitulo;
	private List<Integer> dezenasSelecionadas;
	private DezenaConfig dezenaConfig;
	private TextView txtNumero, txtIndividualidade;
	private RecyclerView listView, listTrevos;
	private Context context;

	public JogoBolaoHolder(ModalidadeEnum modalidade, int corTitulo, List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, View itemView, Context context) {
		super(itemView);
		this.modalidade = modalidade;
		this.corTitulo = corTitulo;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		txtNumero = itemView.findViewById(R.id.id_numero_jogo);
		listView = itemView.findViewById(R.id.id_lista);
		txtIndividualidade = itemView.findViewById(R.id.id_descricao_individualidade);
		listTrevos = itemView.findViewById(R.id.id_trevos);
		this.context = context;
	}

	@Override
	public void bind(ApostaBolaoDTO aposta, int position) {
		txtNumero.setText("Jogo " + (position + 1));
		txtNumero.setTextColor(ContextCompat.getColor(context, corTitulo));

		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < aposta.getDezenas().size(); i++) {
			if (aposta.getDezenas().get(i) == 100 || aposta.getDezenas().get(i) == 0){
				dezenas.add(new Dezena("" + aposta.getDezenas().get(i), Boolean.FALSE, "00" ));
			} else {
				dezenas.add(new Dezena("" + aposta.getDezenas().get(i), Boolean.FALSE, "" + aposta.getDezenas().get(i)));
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
				apresentaDiaSorteTimeMania(context.getString(R.string.label_time_coracao_dois_pontos), aposta.getTimeCoracao().getNomeComUf());
				break;
			case DIA_DE_SORTE:
				String mes = "-";
				if (aposta.getMesSorte() != null && aposta.getMesSorte().getNome() != null){
					mes = aposta.getMesSorte().getNome();
				}
				apresentaDiaSorteTimeMania(context.getString(R.string.label_mes_sorte_dois_pontos), mes);
				break;
			case MAIS_MILIONARIA:
				txtIndividualidade.setVisibility(View.VISIBLE);
				txtIndividualidade.setTypeface(FonteUtils.getFonte(FontCaixaEnum.REGULAR));
				txtIndividualidade.setText(context.getString(R.string.trevosTitulo));

				List<Dezena> trevos = new ArrayList<>();
				for (int i = 0; i < aposta.getTrevos().size(); i++) {
					trevos.add(new Dezena("" + aposta.getTrevos().get(i), Boolean.FALSE, "" + aposta.getTrevos().get(i)));
				}

				listTrevos.setVisibility(View.VISIBLE);
				ListaDezenaRecyclerView trevosAdapter = new ListaDezenaRecyclerView(trevos, new ArrayList<>(),
						getTrevoConfig(), onItemClickListener());
				listTrevos.setAdapter(trevosAdapter);

				RecyclerView.LayoutManager layuot = new GridLayoutManager(context, 6);
				listTrevos.setLayoutManager(layuot);
				break;
		}
	}

	private DezenaConfig getTrevoConfig() {
		ShapeConfig shapeConfig = new ShapeConfig(R.drawable.ic_item_trevo,
				R.drawable.ic_item_trevo_selecionado,
				R.color.milionaria_escuro_mkp, R.color.milionaria_escuro_mkp);

		return new DezenaConfig(false, R.color.milionaria_escuro_mkp,
				R.layout.item_dezena_detalhe,
				shapeConfig, false);
	}

	private void apresentaDiaSorteTimeMania(String descricao, String valor){
		txtIndividualidade.setVisibility(View.VISIBLE);
		txtIndividualidade.setText(descricao + valor);
	}

	private OnItemClickListener onItemClickListener() {
		return (holder, position) -> {};
	}
}
