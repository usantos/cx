package br.gov.caixa.loterias.apostar.interno.view.adapter;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.SituacoesApostaEnum;
import br.gov.caixa.loterias.apostar.interno.utils.Tupla;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;

public class FiltroApostasAdapter extends BaseExpandableListAdapter {

	public static final int MODALIDADES = 0;
	public static final int SITUACAO = 1;
	public static final int CONCURSO = 2;


	private Context context;
	private List<String> expandableListTitle;
	private LinkedHashMap<Integer,String> situacoes;
	private HashMap<String, List<String>> expandableListDetail;
	private NumberPicker npSituacao;
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
	private int numeroSituacaoSelecionada = SituacoesApostaEnum.TODAS;
	private Boolean isUnicaSelecao = false;

	public FiltroApostasAdapter(Context context, List<String> expandableListTitle,
								HashMap<String, List<String>> expandableListDetail,
								List<ModalidadeDTO>  modalidades, Activity activity,
								Boolean isUnicaSelecao,
								LinkedHashMap<Integer,String> situacoes) {
		this.context = context;
		this.expandableListTitle = expandableListTitle;
		this.expandableListDetail = expandableListDetail;
		this.isUnicaSelecao = isUnicaSelecao;
		this.situacoes = situacoes;
		this.setModalidades(modalidades);
		this.setActivity(activity);
		configuraAdapter();
	}

	@Override
	public View getGroupView(int listPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		String listTitle = (String) getGroup(listPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_group_filtro, null);
		}
		TextView listTitleTextView = convertView
				.findViewById(R.id.titleItemGroupSideMenu);
		listTitleTextView.setTypeface(null, Typeface.BOLD);
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

		LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (listPosition) {
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
							npSituacao.setDisplayedValues(situacoes.values().toArray(new String[situacoes.size()]));
							npSituacao.setOnValueChangedListener(new ListenerSituacao());
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

	private void configuraAdapter() {
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

	public int getContadorBadge(){
		int contBadge = 0;
		if (getAdapterModalidades() != null) {
			if(getAdapterModalidades().getModalidadesSelecionadas().size() > 0){
				contBadge++;
			}
		}
		if (getNpSituacao() != null) {
			if(getNpSituacao().getValue() != 1){
				contBadge++;
			}
		}
		if (getEtNumConcurso() != null) {
			if(getEtNumConcurso().getText() != null &&
					getEtNumConcurso().getText().toString() != null &&
					!getEtNumConcurso().getText().toString().isEmpty()) {
				contBadge++;
			}
		}
		return contBadge;
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
			String situacaoSelecionada = situacoes.get(situacao).toLowerCase();
			if(!situacaoSelecionada.equals(situacoes.get(SituacoesApostaEnum.TODAS).toLowerCase())) {
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
			numeroSituacaoSelecionada = 1;
			getNpSituacao().setValue(SituacoesApostaEnum.TODAS);
		}
		if(getAdapterModalidades()!= null){
			getAdapterModalidades().limpaSelecaoModalidades();
		}
		if(getEtNumConcurso()!= null){
			getEtNumConcurso().setText("");
			getEtNumConcurso().clearFocus();
		}
	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			TipoAposta tipoAposta = null;
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao())) {
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
				case LOTOGOL:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_lotogol, R.color.lotogolclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case LOTECA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_loteca, R.color.lotecaclaro, false, item.getValor(), item.getDescricaoEspecial());
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

	public NumberPicker getNpSituacao() {
		return npSituacao;
	}

	public void setNpSituacao(NumberPicker npSituacao) {
		this.npSituacao = npSituacao;
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

}

