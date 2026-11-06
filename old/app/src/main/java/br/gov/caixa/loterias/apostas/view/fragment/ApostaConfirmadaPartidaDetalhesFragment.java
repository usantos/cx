package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaConfirmadaLotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaConfirmadaLotogolAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class ApostaConfirmadaPartidaDetalhesFragment extends ApostaConfirmadaDetalhesFragment {
	private static final String ARG_APOSTA = "ARG_APOSTA";
	private static final String ARG_LOTECA = "ARG_LOTECA";

	private IdentificaoDeUmaApostaDas8Modalidades aposta;
	private boolean isLoteca;

	private View view;

	private EstiloModalidadeMKP estilo;
	private LinearLayout cabecalhoLotecaLinearLayout,  concursoRodapeLinearLayout;

	private TextView concursoCabecalhoTextView, concursoRodaPeTextView, dataHoraTextView,
			statusApostaTextView, tv_val_premio, tv_val_premio_tit, tv_premio_pago_tit;
	private ConstraintLayout cl_premio_pago, clResgatePremio;
	private ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

	public ApostaConfirmadaPartidaDetalhesFragment() {}

	public static ApostaConfirmadaPartidaDetalhesFragment newInstance(IdentificaoDeUmaApostaDas8Modalidades aposta,
                                                                      ResultadoConcursoDTO resultado,
                                                                      ComprovanteApostaDTO comprovante,
                                                                      BigDecimal valorPremio,
																	  boolean isLoteca) {
		ApostaConfirmadaPartidaDetalhesFragment fragment = new ApostaConfirmadaPartidaDetalhesFragment();
		Bundle               args     = fragment.getBundle(resultado, comprovante, valorPremio);
		args.putSerializable(ARG_APOSTA, new Gson().toJson(aposta));
		args.putBoolean(ARG_LOTECA, isLoteca);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			isLoteca = getArguments().getBoolean(ARG_LOTECA);
			String apostaJson = getArguments().getString(ARG_APOSTA);
			if (apostaJson != null && !apostaJson.isEmpty()){
				aposta = new Gson().fromJson(apostaJson, IdentificaoDeUmaApostaDas8Modalidades.class);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_aposta_confirmada_partida, container, false);
		setaViews();

		estilo = new EstiloModalidadeMKP(aposta.getModalidade());

		clResgatePremio.setOnClickListener(onResgateClickListenr());

		preencheDadosAposta();

		return view;
	}

	private void preencheDadosAposta() {
		concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		concursoRodaPeTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
			dataHoraTextView.setText(aposta.getDataEfetivacao());
		} else {
			dataHoraTextView.setText(aposta.getDataEfetivacao() + getResources().getString(R.string.barra_n) + aposta.getHoraEfetivacao());
		}
		statusApostaTextView.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));

		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || isApostaPremiada(aposta)) {

			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
			tv_val_premio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			tv_val_premio_tit.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
			tv_premio_pago_tit.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));

			if (getValorDoPremio() != null) {
				tv_val_premio.setText(ViewUtils.getMoedaFormat(getValorDoPremio()));
			} else {
				buscaDetalhePremio(aposta.getId(), onBuscaDetalhePremioListener());
			}
			if (isApostaPremiada(aposta)){
				statusApostaTextView.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
				clResgatePremio.setVisibility(View.VISIBLE);
				cl_premio_pago.setVisibility(View.GONE);
				statusApostaTextView.setVisibility(View.VISIBLE);
			} else {
				cl_premio_pago.setVisibility(View.VISIBLE);
				clResgatePremio.setVisibility(View.GONE);
				statusApostaTextView.setVisibility(View.GONE);
			}
		} else {
			statusApostaTextView.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
		}

		initList();
	}

	private OnSilceListener<DetalhesPremioDTO> onBuscaDetalhePremioListener() {
		return new OnSilceListener<DetalhesPremioDTO>() {
			@Override
			public void success(DetalhesPremioDTO payload) {
				atualizaValorPremio(payload.getPremio().getValorLiquido());
			}

			@Override
			public void error(VolleyError error) {}
		};
	}

	private void initList() {

		if (isLoteca) {
			cabecalhoLotecaLinearLayout.setVisibility(View.VISIBLE);
			concursoRodapeLinearLayout.setVisibility(View.INVISIBLE);
			listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotecaAdapter(aposta.getPartidasLoteca(), getResultadoConcurso()));
		} else {
			concursoRodapeLinearLayout.setVisibility(View.INVISIBLE);
			listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotogolAdapter(aposta.getPartidasLotogol()));
		}

		listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
		listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
	}

	private View.OnClickListener onResgateClickListenr() {
		return v -> {
			irParaResgate(aposta.getId());
		};
	}

	private void setaViews() {
		cabecalhoLotecaLinearLayout = view.findViewById(R.id.cabecalhoLotecaLinearLayout);
		concursoRodapeLinearLayout = view.findViewById(R.id.concursoRodapeLinearLayout);;
		concursoCabecalhoTextView = view.findViewById(R.id.concursoCabecalhoTextView);
		concursoRodaPeTextView = view.findViewById(R.id.concursoRodaPeTextView);
		dataHoraTextView = view.findViewById(R.id.dataHoraTextView);
		statusApostaTextView = view.findViewById(R.id.statusApostaTextView);
		listaPartidasApostadasExpandebleRecyclerView = view.findViewById(R.id.listaPartidasApostadasExpandebleRecyclerView);
		tv_val_premio = view.findViewById(R.id.tv_val_premio);
		tv_val_premio_tit = view.findViewById(R.id.tv_val_premio_tit);
		tv_premio_pago_tit = view.findViewById(R.id.tv_premio_pago_tit);
		cl_premio_pago = view.findViewById(R.id.cl_premio_pago);
		clResgatePremio = view.findViewById(R.id.clResgatePremio);
	}

	public void atualizaValorPremio(BigDecimal valor){
		if (valor != null){
			setValorDoPremio(valor);
			if (tv_val_premio != null){
				tv_val_premio.setText(ViewUtils.getMoedaFormat(valor));
				tv_val_premio.setVisibility(View.VISIBLE);
			}
			if (tv_val_premio_tit != null){
				tv_val_premio_tit.setVisibility(View.VISIBLE);
			}
		}
	}


}