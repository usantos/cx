package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfiguracaoRapidaoActivity;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumerosRapidaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.TiposApostasAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;


public class ConfiguracaoRapidaoFragment8 extends Fragment implements View.OnClickListener {

	private View view = null;
	private RapidaoDTO rapidaoConfig;
	private TextView tvValorMinimo, tvValorMaximo, tvPremioMinimo;
	private ImageButton ibEditApostaMin,ibEditApostaMax,ibEditPremioMin,
			ibEditModalidades,ibEditNumerosObrigatorios,ibEditNumerosProibidos;//ibEditApostasFavoritas;
	private BigDecimal valorMinApostaCarrinho, valorMaxApostaCarrinho, valorPremioMinimo;
	private ExpandableHeightGridView gridModalidades, gridObrigatorios, gridProibidos;
	//private RecyclerView rvApostasFavoritas;


	public static ConfiguracaoRapidaoFragment8 newInstance() {
		ConfiguracaoRapidaoFragment8 fragment = new ConfiguracaoRapidaoFragment8();
		return fragment;
	}

	public ConfiguracaoRapidaoFragment8() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment8, container, false);
			setaViews(view);
			setaClicks();
		}
		setaMetodos();
		return view;
	}

	private void setaClicks(){
		ibEditApostaMin.setOnClickListener(this);
		ibEditApostaMax.setOnClickListener(this);
		ibEditPremioMin.setOnClickListener(this);
		ibEditModalidades.setOnClickListener(this);
		ibEditNumerosObrigatorios.setOnClickListener(this);
		ibEditNumerosProibidos.setOnClickListener(this);
		//ibEditApostasFavoritas.setOnClickListener(this);
	}

	private void setaViews (View view) {

		tvValorMinimo = view.findViewById(R.id.tv_val_aposta_min);
		tvValorMaximo = view.findViewById(R.id.tv_val_aposta_max);
		tvPremioMinimo = view.findViewById(R.id.tv_val_premio_min);

		ibEditApostaMin = view.findViewById(R.id.ib_editar_aposta_min);
		ibEditApostaMax = view.findViewById(R.id.ib_editar_aposta_max);
		ibEditPremioMin = view.findViewById(R.id.ib_editar_premio_min);
		ibEditModalidades = view.findViewById(R.id.ib_editar_modalidades);
		ibEditNumerosObrigatorios = view.findViewById(R.id.ib_editar_numeros_obrigatorios);
		ibEditNumerosProibidos = view.findViewById(R.id.ib_editar_numeros_proibidos);
		//ibEditApostasFavoritas = view.findViewById(R.id.ib_editar_apostas_favoritas);


		gridModalidades = view.findViewById(R.id.ehgv_modalidades);
		gridObrigatorios = view.findViewById(R.id.ehgv_numeros_obrigatorios);
		gridProibidos = view.findViewById(R.id.ehgv_numeros_proibidos);
		//rvApostasFavoritas = view.findViewById(R.id.rv_apostas_favoritas);

	}

	public void setaMetodos(){

		rapidaoConfig = RapidaoConfigSingleton.getInstance().getRapidaoConfig();

		valorMinApostaCarrinho = rapidaoConfig.getValorMinimo();
		valorMaxApostaCarrinho = rapidaoConfig.getValorMaximo();
		valorPremioMinimo = rapidaoConfig.getValorMinimoPremioPrincipal();

		tvValorMinimo.setText(ViewUtils.getMoedaFormat(valorMinApostaCarrinho));
		tvValorMaximo.setText(ViewUtils.getMoedaFormat(valorMaxApostaCarrinho));
		if(valorPremioMinimo.equals(new BigDecimal(0))){
			tvPremioMinimo.setText("Qualquer valor");
		} else {
			tvPremioMinimo.setText(ViewUtils.getMoedaFormat(valorPremioMinimo));
		}


		ArrayList<TipoAposta> array = new ArrayList<>();

		populaArraysTipoAposta(array,rapidaoConfig.getModalidades());

		TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(getActivity(), array);
		gridModalidades.setAdapter(tiposApostasAdapter);
		gridModalidades.setExpanded(true);

		NumerosRapidaoAdapter numerosObrigatoriosAdapter = new NumerosRapidaoAdapter(getActivity(), rapidaoConfig.getPrognosticosObrigatorios(), true);
		gridObrigatorios.setAdapter(numerosObrigatoriosAdapter);
		gridObrigatorios.setExpanded(true);

		NumerosRapidaoAdapter numerosProibidosAdapter = new NumerosRapidaoAdapter(getActivity(), rapidaoConfig.getPrognosticosProibidos(), false);
		gridProibidos.setAdapter(numerosProibidosAdapter);
		gridProibidos.setExpanded(true);


		/*FavoritasRapidaoRecyclerAdapter apostaFavoritaAdapter = new FavoritasRapidaoRecyclerAdapter(getActivity(),
																									getActivity(),
																									rapidaoConfig.getApostasFavoritas(),
																									null,
																									true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		rvApostasFavoritas.setHasFixedSize(true);
		rvApostasFavoritas.setAdapter(apostaFavoritaAdapter);
		rvApostasFavoritas.setLayoutManager(layoutManager);*/


	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case  R.id.ib_editar_premio_min:
				ConfiguracaoRapidaoActivity.scrollToFragment(ConfiguracaoRapidaoActivity.FRAG_PREMIO_MIN);
				break;
			case  R.id.ib_editar_aposta_max:
			case  R.id.ib_editar_aposta_min:
				ConfiguracaoRapidaoActivity.scrollToFragment(ConfiguracaoRapidaoActivity.FRAG_VALOR_MIN_MAX);
				break;
			case R.id.ib_editar_modalidades:
				ConfiguracaoRapidaoActivity.scrollToFragment(ConfiguracaoRapidaoActivity.FRAG_MODALIDADES);
				break;
			case R.id.ib_editar_numeros_obrigatorios:
				ConfiguracaoRapidaoActivity.scrollToFragment(ConfiguracaoRapidaoActivity.FRAG_NUM_OBRG);
				break;
			case R.id.ib_editar_numeros_proibidos:
				ConfiguracaoRapidaoActivity.scrollToFragment(ConfiguracaoRapidaoActivity.FRAG_NUM_PROIB);
				break;
			/*case R.id.ib_editar_apostas_favoritas:
				ConfiguracaoRapidaoActivity.scrollToFragment(ConfiguracaoRapidaoActivity.FRAG_APOSTAS_FAV);
				break;*/
		}
	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			TipoAposta tipoAposta = null;
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
				case QUINA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_5, R.color.quinaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case LOTOFACIL:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_6, R.color.lotofacilclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case LOTOMANIA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_4, R.color.lotomaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case MEGA_SENA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_megasena, R.color.megasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case TIMEMANIA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.timemaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case DUPLA_SENA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_9, R.color.duplasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case DIA_DE_SORTE:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_2, R.color.diasorteclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case SUPER_SETE:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.superseteclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case MAIS_MILIONALIA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_mais_milionaria_nao_selecionado, R.color.mais_milionaria_clara, false, item.getValor(), item.getDescricaoEspecial());
					break;
				default:
					break;
			}
			if (tipoAposta != null) {
				tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale("pt", "BR")));
				array.add(tipoAposta);
			}
		}
	}

}
