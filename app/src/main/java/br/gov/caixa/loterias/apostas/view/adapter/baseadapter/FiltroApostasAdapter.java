package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ExpandableListDataFiltro;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.Tupla;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class FiltroApostasAdapter extends BaseExpandableListAdapter {

	public static final int TIPO_APOSTAS = 0;
	public static final int MODALIDADES = 1;
	public static final int TIPO_CONCURSOS = 5;
	public static final int SITUACAO = 2;
	public static final int ORDENAR_POR = 3;
	public static final int CONCURSO = 4;


	private Context context;
	private List<String> expandableListTitle;
	//private LinkedHashMap<Integer,String> situacoes;
	private List<DTOEnumLong> situacoes;
	//private String[] opcoesDeOrdenacao = new String[]{"Padrão", "Data Crescente", "Data Decrescente"};
	private List<DTOEnumLong> opcoesDeOrdenacao;
	private String[] opcoesDeTipoApostas = new String[]{"Todas", "Individual", "Bolão"};
	private String[] opcoesDeTipoConcursos = new String[]{"Todos", "Normal", "Especial"};
	private HashMap<String, List<String>> expandableListDetail;
	private NumberPicker npSituacao;
    private NumberPicker npOrdenarPor;
    private NumberPicker npTipoAposta;
    private NumberPicker npTipoConcurso;
    private ExpandableHeightGridView gridModalidades;
	private EditText etNumConcurso;
	private List<TipoAposta> tiposAposta = new ArrayList<>();
	private List<ModalidadeDTO> modalidades = new ArrayList<>();
	private List<TipoAposta> tiposApostasSalvas = new ArrayList<>();
	private Activity activity;
	private SelecaoModalidadesAdapter adapterModalidades;
	private View viewModalidades;
	private View viewSituacao;
	private View viewConcurso;
    private View viewOrdenarPor;
    private View viewTipoAposta;
    private View viewTipoConcurso;

    private int numeroSituacaoSelecionada = SituacoesApostaEnum.TODAS;
    private int numeroOrdenarPorSelecionada;
    private int numeroTipoApostaSelecionada = 1;
    private int numeroTipoConcursoSelecionado = 1;
	private ConfigConsultaDTO configConsulta;

    private Boolean isUnicaSelecao = false;

	public FiltroApostasAdapter(Context context,
								List<ModalidadeDTO> modalidades,
								Activity activity,
								Boolean isUnicaSelecao,
								ConfigConsultaDTO configConsulta) {
		this.context = context;
		this.isUnicaSelecao = isUnicaSelecao;
		this.setModalidades(modalidades);
		this.setActivity(activity);
		this.configConsulta = configConsulta;

		preConfiguraLista();
		configuraAdapterModalidades();
		preConfiguraSituacoes();
		preConfiguraOrdenacoes();
	}

	@Override
	public View getGroupView(int listPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		String listTitle = (String) getGroup(listPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_group_filtro, null);
		}
		TextView listTitleTextView = convertView.findViewById(R.id.titleItemGroupSideMenu);
		Typeface fontCaixaBold = FonteUtils.getFonte(FontCaixaEnum.BOLD);
		//listTitleTextView.setTypeface(null, Typeface.BOLD);
		listTitleTextView.setTypeface(fontCaixaBold);
		listTitleTextView.setText(listTitle);

		ImageView imagemMaisOpcoes = convertView.findViewById(R.id.imagemMaisOpcoes);

		listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
		convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawner_linha_divisao_cinza_escuro);

		imagemMaisOpcoes.setSelected(isExpanded);

		return convertView;
	}

	@Override
	public View getChildView(int listPosition, final int expandedListPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
//		if (!isUnicaSelecao) {
//			listPosition += 1;
//			if(listPosition == 3){//OrdenarPor não consta nos filtros em DetalhesComprasActivity. Em vez disso, renderiza Concurso.
//				listPosition = 4;
//			}
//		}

		List<Integer> listaOrdenada = getListaReOrdenada(isUnicaSelecao);
		int posLista = listaOrdenada.get(listPosition);

		LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//switch (listPosition) {
			switch (posLista) {
				case TIPO_APOSTAS:
					if (viewTipoAposta == null) {
						viewTipoAposta = layoutInflater.inflate(R.layout.list_item_ordernar_por_filtro, null);
						if (npTipoAposta == null && viewTipoAposta.findViewById(R.id.np_ordenar_por) != null) {
							setNpTipoAposta(viewTipoAposta.findViewById(R.id.np_ordenar_por));
							npTipoAposta.setMinValue(1);
							npTipoAposta.setMaxValue(opcoesDeTipoApostas.length);
							npTipoAposta.setValue(1);
							npTipoAposta.setDisplayedValues(opcoesDeTipoApostas);
							npTipoAposta.setOnValueChangedListener(new ListenerTipoAposta());
						}
					}
					convertView = viewTipoAposta;
					break;
				case TIPO_CONCURSOS:
					if (viewTipoConcurso == null) {
						viewTipoConcurso = layoutInflater.inflate(R.layout.list_item_ordernar_por_filtro, null);
						if (npTipoConcurso == null && viewTipoConcurso.findViewById(R.id.np_ordenar_por) != null) {
							setNpTipoConcurso(viewTipoConcurso.findViewById(R.id.np_ordenar_por));
							npTipoConcurso.setMinValue(1);
							npTipoConcurso.setMaxValue(opcoesDeTipoConcursos.length);
							npTipoConcurso.setValue(1);
							npTipoConcurso.setDisplayedValues(opcoesDeTipoConcursos);
							npTipoConcurso.setOnValueChangedListener(new ListenerTipoConcurso());
						}
					}
					convertView = viewTipoConcurso;
					break;
                case ORDENAR_POR:
                    if (viewOrdenarPor == null) {
                        viewOrdenarPor = layoutInflater.inflate(R.layout.list_item_ordernar_por_filtro, null);
                        if (npOrdenarPor == null && viewOrdenarPor.findViewById(R.id.np_ordenar_por) != null) {
                            setNpOrdenarPor(viewOrdenarPor.findViewById(R.id.np_ordenar_por));
                            npOrdenarPor.setMinValue(1);
                            npOrdenarPor.setMaxValue(opcoesDeOrdenacao.size());
                            npOrdenarPor.setValue(configConsulta.getOrdenacaoPadrao().getValor().intValue());
                            //npOrdenarPor.setDisplayedValues(opcoesDeOrdenacao);
							npOrdenarPor.setDisplayedValues(opcoesDeOrdenacao.stream()
									.map(DTOEnumLong::getDescricao)
									.toArray(String[]::new));
                            npOrdenarPor.setOnValueChangedListener(new ListenerOrdenarPor());
                        }
                    }
					convertView = viewOrdenarPor;
					break;
				case MODALIDADES:
					if(viewModalidades == null) {
						viewModalidades = layoutInflater.inflate(R.layout.list_item_modalidades, null);
						if (gridModalidades == null && viewModalidades.findViewById(R.id.ehgv_grid_modalidades) != null) {
							setGridModalidades(viewModalidades.findViewById(R.id.ehgv_grid_modalidades));
							gridModalidades.setAdapter(getAdapterModalidades());
							gridModalidades.setExpanded(true);
						}
					}
					convertView = viewModalidades;
					break;
				case SITUACAO:
					if(viewSituacao == null) {
						viewSituacao = layoutInflater.inflate(R.layout.list_item_situacao_aposta, null);
						if (npSituacao == null && viewSituacao.findViewById(R.id.np_situacao_aposta) != null) {
							setNpSituacao(viewSituacao.findViewById(R.id.np_situacao_aposta));
							npSituacao.setMinValue(1);
							npSituacao.setMaxValue(situacoes.size());
							//npSituacao.setDisplayedValues(situacoes.values().toArray(new String[situacoes.size()]));
							npSituacao.setDisplayedValues(situacoes.stream()
											.map(DTOEnumLong::getDescricao)
											.toArray(String[]::new));
							npSituacao.setOnValueChangedListener(new ListenerSituacao());
							npSituacao.setWrapSelectorWheel(false);
						}
					}
					convertView = viewSituacao;
					break;
				case CONCURSO:
					if (viewConcurso == null){
						viewConcurso = layoutInflater.inflate(R.layout.list_item_num_concurso, null);
						if(etNumConcurso == null && viewConcurso.findViewById(R.id.et_num_concurso) != null){
							setEtNumConcurso(viewConcurso.findViewById(R.id.et_num_concurso));
						}
					}
					convertView = viewConcurso;
					break;
		}

		return convertView;
	}

	private class ListenerSituacao implements NumberPicker.OnValueChangeListener {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			 numeroSituacaoSelecionada = newVal;
		}
	}

    private class ListenerOrdenarPor implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            numeroOrdenarPorSelecionada = newVal;
        }
    }

	private class ListenerTipoAposta implements NumberPicker.OnValueChangeListener {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			numeroTipoApostaSelecionada = newVal;
		}
	}
	private class ListenerTipoConcurso implements NumberPicker.OnValueChangeListener {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			numeroTipoConcursoSelecionado = newVal;
		}
	}

	private void configuraAdapterModalidades() {
		populaArraysTipoAposta(getTiposAposta(), getModalidades());
		if (getTiposAposta() != null || getTiposAposta().size() > 0) {
			for (TipoAposta tipoAposta : getTiposAposta()) {
				for (TipoAposta tipoApostaSalva : getTiposApostasSalvas()) {
					if (tipoApostaSalva.getTitulo().equals(tipoAposta.getTitulo())) {
						tipoAposta.setSelect(true);
					}
				}
			}
			setAdapterModalidades(new SelecaoModalidadesAdapter(getActivity(), getTiposAposta(),isUnicaSelecao));
		}
	}

	public Boolean isAddBadge(){
		if (getAdapterModalidades() != null) {
			if(getAdapterModalidades().getModalidadesSelecionadas().size() > 0){
				return true;
			}
		}
		if (getNpSituacao() != null) {
			if(getNpSituacao().getValue() != 1){
				return true;
			}
		}
		if (getEtNumConcurso() != null) {
			if(getEtNumConcurso().getText() != null &&
					getEtNumConcurso().getText().toString() != null &&
					!getEtNumConcurso().getText().toString().isEmpty()) {
				return true;
			}
		}
		if (getNpOrdenarPor() != null) {
			if(getNpOrdenarPor().getValue() != 0) {
				return true;
			}
		}
		if (getNpTipoAposta() != null) {
			if(getNpTipoAposta().getValue() != 0) {
				return true;
			}
		}
		if (getNpTipoConcurso() != null) {
			if(getNpTipoConcurso().getValue() != 0) {
				return true;
			}
		}
		return false;
	}

	//TODO DESACOPLAR E COLOCAR INTERFACES
	public Tupla<List<ApostaDTO>,Boolean>  getApostasFiltradasConcurso(List<ApostaDTO>  listaAposta){
		List<ApostaDTO> listaFiltrada = new ArrayList<>();
		Boolean incrementaContador = false;

		if(getEtNumConcurso() != null &&
				getEtNumConcurso().getText() != null &&
				getEtNumConcurso().getText().toString() != null &&
				!getEtNumConcurso().getText().toString().isEmpty()) {
			int numConcurso = Integer.valueOf(getEtNumConcurso().getText().toString());
			for (ApostaDTO aposta:listaAposta) {
				if(aposta.getConcursoAlvo().equals(new Integer(numConcurso))){
					listaFiltrada.add(aposta);
				}

			}
			incrementaContador = true;
		}
		return new Tupla<>(listaFiltrada,incrementaContador);
	}
	//TODO DESACOPLAR
	public Tupla<List<ApostaDTO>,Boolean> getApostasFiltradasModalidade(List<ApostaDTO>  listaAposta){
		List<ApostaDTO> listaFiltrada = new ArrayList<>();
		Boolean incrementaContador = false;

		if(getAdapterModalidades() != null) {
			List<ModalidadeEnum> modalidades = getAdapterModalidades().getModalidadesEnumSelecionadas();
			if(modalidades.size() > 0){
				for (ApostaDTO aposta:listaAposta) {
					if(modalidades.contains(aposta.getModalidade())){
						listaFiltrada.add(aposta);
					}
				}
				incrementaContador = true;
			}
		}
		return new Tupla<>(listaFiltrada,incrementaContador);
	}

	//TODO DESACOPLAR
	public Tupla<List<ApostaDTO>,Boolean> getApostasFiltradasSituacao(List<ApostaDTO>  listaAposta){
		List<ApostaDTO> listaFiltrada = new ArrayList<>();
		Boolean incrementaContador = false;

		if(getNpSituacao() != null) {
			int situacao = numeroSituacaoSelecionada;
			//String situacaoSelecionada = situacoes.get(situacao).toLowerCase();
			String situacaoSelecionada = situacoes.get(situacao-1).getDescricao();
			//if(!situacaoSelecionada.equals(situacoes.get(SituacoesApostaEnum.TODAS).toLowerCase())) {
			if(!situacaoSelecionada.equals(situacoes.get(situacao-1).getDescricao())) {
				for (ApostaDTO aposta:listaAposta) {
					if(situacaoSelecionada.equals(aposta.getSituacao().getDescricao().toLowerCase())) {
						listaFiltrada.add(aposta);
					}
				}
				incrementaContador = true;
			}
		}
		return new Tupla<>(listaFiltrada,incrementaContador);
	}

	public void limpaFiltros(){
		if(getNpSituacao() != null){
			//numeroSituacaoSelecionada = 1;
			numeroSituacaoSelecionada = configConsulta.getSituacaoPadrao().getValor().intValue();
			//getNpSituacao().setValue(SituacoesApostaEnum.TODAS);
			getNpSituacao().setValue(configConsulta.getSituacaoPadrao().getValor().intValue());
		}
		if(getAdapterModalidades()!= null){
			getAdapterModalidades().limpaSelecaoModalidades();
		}
		if(getEtNumConcurso()!= null){
			getEtNumConcurso().setText(context.getResources().getString(R.string.string_vazia));
			getEtNumConcurso().clearFocus();
		}
		if(getNpOrdenarPor() != null) {
			numeroOrdenarPorSelecionada = configConsulta.getOrdenacaoPadrao().getValor().intValue();
			getNpOrdenarPor().setValue(configConsulta.getOrdenacaoPadrao().getValor().intValue());
		}
		if(getNpTipoAposta() != null) {
			numeroTipoApostaSelecionada = 0;
			getNpTipoAposta().setValue(0);
		}
		if(getNpTipoConcurso() != null) {
			numeroTipoConcursoSelecionado = 0;
			getNpTipoConcurso().setValue(0);
		}
	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			TipoAposta tipoAposta = null;
			EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
				case MAIS_MILIONALIA:
					tipoAposta = new TipoAposta("+Milionária", estilo.getTrevoFundoClaro(),  estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzatextoselecionado);
					break;
				case TIMEMANIA:
				case DIA_DE_SORTE:
				case SUPER_SETE:
					tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzatextoselecionado);
					tipoAposta.setTextSelectColor(estilo.getCorLetraLista());
					break;
				default:
					tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzatextoselecionado);
					break;
			}

			if (tipoAposta != null) {
				tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale("pt", "BR")));
				array.add(tipoAposta);
			}
		}
	}

    public NumberPicker getNpOrdenarPor() {
        return npOrdenarPor;
    }

    public void setNpOrdenarPor(NumberPicker npOrdenarPor) {
        this.npOrdenarPor = npOrdenarPor;
    }

    public NumberPicker getNpSituacao() {
		return npSituacao;
	}

	public NumberPicker getNpTipoAposta() {
		return npTipoAposta;
	}

	public NumberPicker getNpTipoConcurso() {
		return npTipoConcurso;
	}

	public void setNpTipoAposta(NumberPicker npTipoAposta) {
		this.npTipoAposta = npTipoAposta;
	}
	public void setNpTipoConcurso(NumberPicker npTipoConcurso) {
		this.npTipoConcurso = npTipoConcurso;
	}

	public void setNpSituacao(NumberPicker npSituacao) {
		this.npSituacao = npSituacao;
	}


	public int getKeySituacaoSelecionada() {
		//Set<Integer> keySet = situacoes.keySet();
		//Integer[] keyArray 	= keySet.toArray(new Integer[keySet.size()]);
		//return keyArray[numeroSituacaoSelecionada - 1];
		return numeroSituacaoSelecionada;
	}

	public ExpandableHeightGridView getGridModalidades() {
		return gridModalidades;
	}

	public void setGridModalidades(ExpandableHeightGridView gridModalidades) {
		this.gridModalidades = gridModalidades;
	}

	public EditText getEtNumConcurso() {
		return etNumConcurso;
	}

	public void setEtNumConcurso(EditText etNumConcurso) {
		this.etNumConcurso = etNumConcurso;
	}

	public List<TipoAposta> getTiposAposta() {
		return tiposAposta;
	}

	public void setTiposAposta(List<TipoAposta> tiposAposta) {
		this.tiposAposta = tiposAposta;
	}

	public List<ModalidadeDTO> getModalidades() {
		return modalidades;
	}

	public void setModalidades(List<ModalidadeDTO> modalidades) {
		this.modalidades = modalidades;
	}

	public List<TipoAposta> getTiposApostasSalvas() {
		return tiposApostasSalvas;
	}

	public void setTiposApostasSalvas(List<TipoAposta> tiposApostasSalvas) {
		this.tiposApostasSalvas = tiposApostasSalvas;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public SelecaoModalidadesAdapter getAdapterModalidades() {
		return adapterModalidades;
	}

	public void setAdapterModalidades(SelecaoModalidadesAdapter adapterModalidades) {
		this.adapterModalidades = adapterModalidades;
	}


	@Override
	public Object getChild(int listPosition, int expandedListPosition) {
		return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
										.get(expandedListPosition);
	}

	@Override
	public long getChildId(int listPosition, int expandedListPosition) {
		return expandedListPosition;
	}


	@Override
	public int getChildrenCount(int listPosition) {
		return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
										.size();
	}

	@Override
	public Object getGroup(int listPosition) {
		return this.expandableListTitle.get(listPosition);
	}

	@Override
	public int getGroupCount() {
		return this.expandableListTitle.size();
	}

	@Override
	public long getGroupId(int listPosition) {
		return listPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int listPosition, int expandedListPosition) {
		return true;
	}


	private List<Integer> getListaReOrdenada(boolean isUnicaSelecao) {
		if (isUnicaSelecao) {
			return Arrays.asList(
					TIPO_APOSTAS,
					MODALIDADES,
					TIPO_CONCURSOS,
					SITUACAO,
					ORDENAR_POR
			);
		} else {
			return Arrays.asList(
					TIPO_APOSTAS,
					MODALIDADES,
					SITUACAO,
					CONCURSO
			);
		}
	}

	private void preConfiguraLista() {
		if (isUnicaSelecao) {
			expandableListDetail = ExpandableListDataFiltro.getDataSemConcurso();
			expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
		} else {
			expandableListDetail = ExpandableListDataFiltro.getData();
			expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
		}
	}

	private void preConfiguraSituacoes() {
		situacoes = configConsulta.getSituacoes();
		numeroSituacaoSelecionada = configConsulta.getSituacaoPadrao().getValor().intValue();
	}

	private void preConfiguraOrdenacoes() {
		opcoesDeOrdenacao = configConsulta.getOrdenacoes();
		numeroOrdenarPorSelecionada = configConsulta.getOrdenacaoPadrao().getValor().intValue();
	}
}

