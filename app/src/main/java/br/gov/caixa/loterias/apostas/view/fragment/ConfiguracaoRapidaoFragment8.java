package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfiguracaoRapidaoActivity;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumerosRapidaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TipoApostaMultiplaSelecaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class ConfiguracaoRapidaoFragment8 extends Fragment implements View.OnClickListener {
	private View view = null;
	private RapidaoDTO rapidaoConfig;
	private TextView tvValorMinimo, tvValorMaximo, tvPremioMinimo;
	private ImageButton ibEditApostaMin,ibEditApostaMax,ibEditPremioMin,
			ibEditModalidades,ibEditNumerosObrigatorios,ibEditNumerosProibidos;//ibEditApostasFavoritas;
	private BigDecimal valorMinApostaCarrinho, valorMaxApostaCarrinho, valorPremioMinimo;
	private ExpandableHeightGridView gridObrigatorios, gridProibidos;
	private RecyclerView gridModalidades;


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

		gridModalidades = view.findViewById(R.id.ehgv_modalidades);
		gridObrigatorios = view.findViewById(R.id.ehgv_numeros_obrigatorios);
		gridProibidos = view.findViewById(R.id.ehgv_numeros_proibidos);
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
		populaArraysTipoAposta(array, rapidaoConfig.getModalidades());

		gridModalidades.setLayoutManager(new GridLayoutManager(getContext(), 2));
		gridModalidades.setAdapter(new TipoApostaMultiplaSelecaoAdapter(array, null));

		NumerosRapidaoAdapter numerosObrigatoriosAdapter = new NumerosRapidaoAdapter(getActivity(), rapidaoConfig.getPrognosticosObrigatorios(), true);
		gridObrigatorios.setAdapter(numerosObrigatoriosAdapter);
		gridObrigatorios.setExpanded(true);

		NumerosRapidaoAdapter numerosProibidosAdapter = new NumerosRapidaoAdapter(getActivity(), rapidaoConfig.getPrognosticosProibidos(), false);
		gridProibidos.setAdapter(numerosProibidosAdapter);
		gridProibidos.setExpanded(true);
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
		}
	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));

			TipoAposta tipoAposta = null;
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
				case MAIS_MILIONALIA:
					tipoAposta = new TipoAposta("+Milionária", estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), true, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
					break;
				default:
					tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), true, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
					break;
			}
			if (tipoAposta != null) {
				tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale("pt", "BR")));
				array.add(tipoAposta);
			}
		}
	}

}
