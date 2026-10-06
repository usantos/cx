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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.BotoesFiltroApostas;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ExpandableListDataFiltro;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.Tupla;
import br.gov.caixa.loterias.apostas.view.holder.ModalidadeHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

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
	private List<DTOEnumLong> opcoesDeTipoApostas;
	private List<DTOEnumLong> opcoesDeTipoConcursos;
	private HashMap<String, List<String>> expandableListDetail;
	private NumberPicker npSituacao;
    private NumberPicker npOrdenarPor;
    private NumberPicker npTipoAposta;
    private NumberPicker npTipoConcurso;
    private RecyclerView gridModalidades;
	private EditText etNumConcurso;
	private List<TipoAposta> tiposAposta = new ArrayList<>();
	private List<ModalidadeDTO> modalidades = new ArrayList<>();
	private List<TipoAposta> tiposApostasSalvas = new ArrayList<>();
	private Activity activity;
	private SelecaoModalidadesRecyclerAdapter adapterModalidades;
	private View viewModalidades;
	private View viewSituacao;
	private View viewConcurso;
    private View viewOrdenarPor;
    private View viewTipoAposta;
    private View viewTipoConcurso;

    private int numeroSituacaoSelecionada;
    private int numeroSituacaoPadrao;
    private int numeroOrdenarPorSelecionada;
    private int numeroOrdenarPorPadrao;
    private int numeroTipoApostaSelecionada;
    private int numeroTipoApostaPadrao;
    private int numeroTipoConcursoPadrao;
    private int numeroTipoConcursoSelecionado;
	private ConfigConsultaDTO configConsulta;
	private BotoesFiltroApostas botoesFiltroApostas;

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
		preConfiguraTipoAposta();
		preConfiguraTipoConcurso();
	}

	public FiltroApostasAdapter(Context context,
								List<ModalidadeDTO> modalidades,
								Activity activity,
								Boolean isUnicaSelecao,
								ConfigConsultaDTO configConsulta,
								BotoesFiltroApostas botoesFiltroApostas) {
		this.context = context;
		this.isUnicaSelecao = isUnicaSelecao;
		this.setModalidades(modalidades);
		this.setActivity(activity);
		this.configConsulta = configConsulta;
		this.botoesFiltroApostas = botoesFiltroApostas;

		preConfiguraLista();
		configuraAdapterModalidades();
		preConfiguraSituacoes();
		preConfiguraOrdenacoes();
		preConfiguraTipoAposta();
		preConfiguraTipoConcurso();
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
							npTipoAposta.setMaxValue(opcoesDeTipoApostas.size());
							npTipoAposta.setValue(numeroTipoApostaSelecionada);
							npTipoAposta.setDisplayedValues(extrairDescricoes(opcoesDeTipoApostas));
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
							npTipoConcurso.setMinValue(0);
							npTipoConcurso.setMaxValue(opcoesDeTipoConcursos.size()-1);
							//npTipoConcurso.setValue(1);
							npTipoConcurso.setValue(numeroTipoConcursoSelecionado);
							npTipoConcurso.setDisplayedValues(extrairDescricoes(opcoesDeTipoConcursos));
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
                            npOrdenarPor.setMinValue(2);
                            npOrdenarPor.setMaxValue(opcoesDeOrdenacao.size()+1);
                            npOrdenarPor.setValue(numeroOrdenarPorSelecionada);
							npOrdenarPor.setDisplayedValues(extrairDescricoes(opcoesDeOrdenacao));
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
							gridModalidades.setLayoutManager(new GridLayoutManager(context, 2));
							gridModalidades.setAdapter(getAdapterModalidades());
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
							npSituacao.setDisplayedValues(extrairDescricoes(situacoes));
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
			setAdapterModalidades(new SelecaoModalidadesRecyclerAdapter(getTiposAposta(), isUnicaSelecao, onItemClickListener()));
		}
	}

	private OnItemClickListener<ModalidadeHolder> onItemClickListener() {
		return (holder, position) -> {
			if (tiposAposta.get(position).getSelect()) {
				tiposAposta.get(position).setSelect(false);
				holder.atualizaNaoSelecionado(tiposAposta.get(position));
			} else {
				if (isUnicaSelecao) {
					adapterModalidades.limpaSelecaoModalidades();
				}
				tiposAposta.get(position).setSelect(true);
				holder.atualizaSelecionado(tiposAposta.get(position));

			}
		};
	}

	public Boolean isAddBadge(){
		if (getAdapterModalidades() != null) {
			if(getAdapterModalidades().getModalidadesSelecionadas().size() > 0){
				return true;
			}
		}
		if (getNpSituacao() != null) {
			npSituacao.setValue(numeroSituacaoSelecionada);
			if(numeroSituacaoPadrao != numeroSituacaoSelecionada) {
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
			npOrdenarPor.setValue(numeroOrdenarPorSelecionada);
			if(numeroOrdenarPorPadrao != numeroOrdenarPorSelecionada) {
				return true;
			}
		}
		if (getNpTipoAposta() != null) {
			npTipoAposta.setValue(numeroTipoApostaSelecionada);
			if(numeroTipoApostaPadrao != numeroTipoApostaSelecionada) {
				return true;
			}
		}
		if (getNpTipoConcurso() != null) {
			npTipoConcurso.setValue(numeroTipoConcursoSelecionado);
			if(numeroTipoConcursoPadrao != numeroTipoConcursoSelecionado) {
				return true;
			}
		}

		if(getBotoesFiltroApostas() != null) {
            return botoesFiltroApostas.getCombo() == 3
                    || botoesFiltroApostas.getSurpresinha() == 3
                    || botoesFiltroApostas.getTeimosinha() == 3;
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
			String situacaoSelecionada = situacoes.get(situacao-1).getDescricao();
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
			numeroSituacaoSelecionada = configConsulta.getSituacaoPadrao().getValor().intValue();
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
			numeroOrdenarPorSelecionada = numeroTipoConcursoPadrao;
			getNpOrdenarPor().setValue(numeroTipoConcursoPadrao);
		}
		if(getNpTipoAposta() != null) {
			numeroTipoApostaSelecionada = numeroTipoApostaPadrao;
			getNpTipoAposta().setValue(numeroTipoApostaPadrao);
		}
		if(getNpTipoConcurso() != null) {
			numeroTipoConcursoSelecionado = numeroTipoConcursoPadrao;
			getNpTipoConcurso().setValue(numeroTipoConcursoPadrao);
		}

		if(getBotoesFiltroApostas() != null) {
			getBotoesFiltroApostas().setCombo(0);
			getBotoesFiltroApostas().setSurpresinha(0);
			getBotoesFiltroApostas().setTeimosinha(0);
		}

	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			TipoAposta tipoAposta = null;
			EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
				case MAIS_MILIONALIA:
					tipoAposta = new TipoAposta("+Milionária", estilo.getTrevoFundoClaro(),  estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzanaoselecionado);
					break;
				case TIMEMANIA:
				case DIA_DE_SORTE:
				case SUPER_SETE:
					tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzanaoselecionado);
					tipoAposta.setTextSelectColor(estilo.getCorLetraLista());
					break;
				default:
					tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzanaoselecionado);
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
		return numeroSituacaoSelecionada;
	}

	public RecyclerView getGridModalidades() {
		return gridModalidades;
	}

	public void setGridModalidades(RecyclerView gridModalidades) {
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

	public void setBotoesFiltroApostas(BotoesFiltroApostas botoesFiltroApostas) {
		this.botoesFiltroApostas = botoesFiltroApostas;
	}

	public BotoesFiltroApostas getBotoesFiltroApostas() {
		return botoesFiltroApostas;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public SelecaoModalidadesRecyclerAdapter getAdapterModalidades() {
		return adapterModalidades;
	}

	public void setAdapterModalidades(SelecaoModalidadesRecyclerAdapter adapterModalidades) {
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
		numeroSituacaoPadrao = configConsulta.getSituacaoPadrao().getValor().intValue();
		numeroSituacaoSelecionada = numeroSituacaoPadrao;
	}

	private void preConfiguraOrdenacoes() {
		opcoesDeOrdenacao = configConsulta.getOrdenacoes();
		numeroOrdenarPorPadrao = configConsulta.getOrdenacaoPadrao().getValor().intValue();
		numeroOrdenarPorSelecionada = numeroOrdenarPorPadrao;
	}

	private void preConfiguraTipoAposta() {
		opcoesDeTipoApostas = configConsulta.getListaTipoAposta();
		numeroTipoApostaPadrao = opcoesDeTipoApostas.get(0).getValor().intValue();
		numeroTipoApostaSelecionada = numeroTipoApostaPadrao;
	}

	private void preConfiguraTipoConcurso() {
		opcoesDeTipoConcursos = configConsulta.getListaTipoConcurso();
		numeroTipoConcursoPadrao = opcoesDeTipoConcursos.get(0).getValor().intValue();
		numeroTipoConcursoSelecionado = numeroTipoConcursoPadrao;
	}

	public static String[] extrairDescricoes(List<DTOEnumLong> opcoes) {
		String[] descricoes = new String[opcoes.size()];
		for (int i = 0; i < opcoes.size(); i++) {
			descricoes[i] = opcoes.get(i).getDescricao();
		}
		return descricoes;
	}

}

