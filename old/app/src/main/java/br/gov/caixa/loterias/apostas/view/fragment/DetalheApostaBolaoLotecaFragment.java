package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.ConcursoUtils;
import br.gov.caixa.loterias.apostas.utils.DataUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PartidasLotecaRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class DetalheApostaBolaoLotecaFragment extends ApostaConfirmadaDetalhesFragment {
	private static final String ARG_APOSTA = "ARG_APOSTA";
	private IdentificaoDeUmaApostaDas8Modalidades aposta;
	private View view;
	private TextView concursoTxt, cotaTxt, dataHoraTextView,
			statusApostaTextView, tv_val_premio, tv_val_premio_tit;
	private ConstraintLayout cl_premio_pago, clResgatePremio;
	private ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;
	private TipoApostaLinhaView tipoApostaLinhaView;

	public DetalheApostaBolaoLotecaFragment() {}

	public static DetalheApostaBolaoLotecaFragment newInstance(IdentificaoDeUmaApostaDas8Modalidades aposta,
															   ResultadoConcursoDTO resultado,
															   ComprovanteApostaDTO comprovante,
															   BigDecimal valorPremio) {
		DetalheApostaBolaoLotecaFragment fragment = new DetalheApostaBolaoLotecaFragment();
		Bundle               args     = fragment.getBundle(resultado, comprovante, valorPremio);
		args.putSerializable(ARG_APOSTA, new Gson().toJson(aposta));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			String apostaJson = getArguments().getString(ARG_APOSTA);
			if (apostaJson != null && !apostaJson.isEmpty()){
				aposta = new Gson().fromJson(apostaJson, IdentificaoDeUmaApostaDas8Modalidades.class);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_detalhe_aposta_bolao_loteca, container, false);
		setaViews();

		clResgatePremio.setOnClickListener(onResgateClickListenr());

		preencheDadosAposta();

		return view;
	}

	private void preencheDadosAposta() {
		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		cotaTxt.setText(aposta.getReservaCotaBolao().getNumeroCotaReservada() + "/" + aposta.getReservaCotaBolao().getQtdCotaTotalBolao());
		dataHoraTextView.setText(getDataBolao(aposta));

		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || isApostaPremiada(aposta)) {

			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
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

		preencheTipoApostaLinha();
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

	private void preencheTipoApostaLinha() {
		List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

		if (aposta.getTroca()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
		}
		if (aposta.getIndicadorCotaBolao()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
		}
		if (aposta.getCombo()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
		}
		if (aposta.getEspelho()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
		}
		if (aposta.getSurpresinha()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
		}
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
		}

		if (listTipoApostaLinha.size() > 0) {
			tipoApostaLinhaView.setVisibility(View.VISIBLE);
			tipoApostaLinhaView.setupView(listTipoApostaLinha, R.color.loteca_claro_mkp, R.color.branco, R.color.branco, TipoApostaLinhaView.TriangleDirection.DOWN);
		}
	}

	private void initList() {
		listaPartidasApostadasExpandebleRecyclerView.setAdapter(new PartidasLotecaRecyclerViewAdapter(getPartidas(), getContext(), getConfigLoteca(), getResultadoConcurso()));
		listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
		listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
	}

	@NonNull
	private static ConfiguracaoLoteca getConfigLoteca() {
		return new ConfiguracaoLoteca(R.color.branco, R.color.loteca_escuro_mkp, R.color.branco);
	}

	private List<PartidaLotecaDTO> getPartidas() {
		if (!aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().isEmpty()){
			if (aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(0).getPartidasLoteca() != null){
				return aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(0).getPartidasLoteca();
			}
		}
		return new ArrayList<>();
	}

	private View.OnClickListener onResgateClickListenr() {
		return v -> {
			irParaResgate(aposta.getId());
		};
	}

	private String getDataBolao(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
		String dataString = DataUtil.formatStringData(DataUtil.DD_MM_YYYY, aposta.getReservaCotaBolao().getDataHoraReserva());
		String horaString = DataUtil.formatStringData(DataUtil.HH_MM_SS, aposta.getReservaCotaBolao().getDataHoraReserva());

		return dataString + "\n" + horaString;
	}

	private void setaViews() {
		concursoTxt = view.findViewById(R.id.concursoTxt);
		cotaTxt = view.findViewById(R.id.cotaTxt);
		dataHoraTextView = view.findViewById(R.id.dataApostaTxt);
		statusApostaTextView = view.findViewById(R.id.statusApostaTextView);
		tipoApostaLinhaView = view.findViewById(R.id.tipo_aposta_linha);
		listaPartidasApostadasExpandebleRecyclerView = view.findViewById(R.id.listaPartidasApostadasExpandebleRecyclerView);
		tv_val_premio = view.findViewById(R.id.tv_val_premio);
		tv_val_premio_tit = view.findViewById(R.id.tv_val_premio_tit);
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