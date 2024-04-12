package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.utils.DataUtil;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosBolaoRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class ApostaConfirmadaBolaoDetalhesFragment extends ApostaConfirmadaDetalhesFragment {
	private static final String ARG_APOSTA = "ARG_APOSTA";

    private ConstraintLayout cl_premio_pago, clResgatePremio;

	private IdentificaoDeUmaApostaDas8Modalidades aposta;

	private View view;
    private TextView concursoTxt, cotaTxt, dataApostaTxt, statusApostaTxt, tv_val_premio, tv_val_premio_tit;
	private TextView tvNumerosSorteados, tvNumerosSorteados2;
	private TextView tvDuplaSena1, tvDuplaSena2;
	private RecyclerView listViewSorteados, listViewSorteados2, listViewJogos, listViewJogos2;
	private ImageView imgTriangulo;
	private EstiloModalidadeMKP estiloMKP;

    public ApostaConfirmadaBolaoDetalhesFragment() {}

	public static ApostaConfirmadaBolaoDetalhesFragment newInstance(IdentificaoDeUmaApostaDas8Modalidades aposta,
                                                                    ResultadoConcursoDTO resultado,
                                                                    ComprovanteApostaDTO comprovante,
                                                                    BigDecimal valorPremio) {
		ApostaConfirmadaBolaoDetalhesFragment fragment = new ApostaConfirmadaBolaoDetalhesFragment();
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
				estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_aposta_confirmada_bolao_detalhes, container, false);
		setaViews();

		clResgatePremio.setOnClickListener(onResgateClickListenr());

		preencheDadosAposta();

		return view;
	}

	private void preencheDadosAposta() {
		EstiloModalidadeMKP estiloModalidadeMKP = new EstiloModalidadeMKP(aposta.getModalidade());
		imgTriangulo.setImageResource(estiloModalidadeMKP.getImagemTrianguloOutline());

		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		cotaTxt.setText(aposta.getReservaCotaBolao().getNumeroCotaReservada() + "/" + aposta.getReservaCotaBolao().getQtdCotaTotalBolao());
		dataApostaTxt.setText(getDataBolao(aposta));
		statusApostaTxt.setText(aposta.getSituacao().getDescricao());

		if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || isApostaPremiada(aposta)) {
			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
			if (getValorDoPremio() != null) {
				tv_val_premio.setText(ViewUtils.getMoedaFormat(getValorDoPremio()));
			} else {
				buscaDetalhePremio(aposta.getId(), onBuscaDetalhePremioListener());
			}
			if (isApostaPremiada(aposta)){
				statusApostaTxt.setText(aposta.getSituacao().getDescricao());
				clResgatePremio.setVisibility(View.VISIBLE);
				cl_premio_pago.setVisibility(View.GONE);
				statusApostaTxt.setVisibility(View.VISIBLE);
			} else {
				cl_premio_pago.setVisibility(View.VISIBLE);
				clResgatePremio.setVisibility(View.GONE);
				statusApostaTxt.setVisibility(View.GONE);
			}
		} else {
			statusApostaTxt.setText(aposta.getSituacao().getDescricao());
			clResgatePremio.setVisibility(View.GONE);
		}

		initList();
	}

    private String getDataBolao(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
        String dataString = DataUtil.formatStringData(DataUtil.DD_MM_YYYY, aposta.getReservaCotaBolao().getDataHoraReserva());
        String horaString = DataUtil.formatStringData(DataUtil.HH_MM_SS, aposta.getReservaCotaBolao().getDataHoraReserva());

        return dataString + "\n" + horaString;
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
		boolean temResultadoSegundoSorteio = temResultadoSegundoSorteio();
		List<Integer> listSorteados2 = new ArrayList<>();
		List<Integer> listSorteados = new ArrayList<>();
		if (temResultadoSorteio()) {
			if (temResultadoSegundoSorteio) {
				tvNumerosSorteados.setText(getString(R.string.numeros_sorteados) + "\n\n" +
										   getString(R.string.primeiro_sorteiro));
				tvDuplaSena1.setVisibility(View.VISIBLE);
				tvDuplaSena1.setText(getString(R.string.seus_jogos) + "\n\n" +
						getString(R.string.primeiro_sorteiro));
			}

			listSorteados = getResultadoConcurso().getListaSorteadosPrimeiroSorteio();
			List dezenas = getDezenas(listSorteados);
			ListaDezenaRecyclerView numerosAdapter = new ListaDezenaRecyclerView(dezenas, listSorteados
					, getDezenaConfig(), null);
			listViewSorteados.setAdapter(numerosAdapter);
			listViewSorteados.setLayoutManager(new GridLayoutManager(getContext(), 6));

			if (temResultadoSegundoSorteio) {
				tvDuplaSena2.setVisibility(View.VISIBLE);
				tvDuplaSena2.setText(getString(R.string.segundo_sorteiro));

				tvNumerosSorteados2.setVisibility(View.VISIBLE);
				listViewSorteados2.setVisibility(View.VISIBLE);

				listSorteados2 = getResultadoConcurso().getListaSorteadosSegundoSorteio();
				List dezenas2 = getDezenas(listSorteados2);
				ListaDezenaRecyclerView numerosAdapter2 = new ListaDezenaRecyclerView(dezenas2, listSorteados2
						, getDezenaConfig(), null);
				listViewSorteados2.setAdapter(numerosAdapter2);
				listViewSorteados2.setLayoutManager(new GridLayoutManager(getContext(), 6));
			}
		} else {
			((TextView)view.findViewById(R.id.id_seus_numeros)).setText("");
		}

		JogosBolaoRecyclerView adpterJogos = new JogosBolaoRecyclerView(R.color.branco,
				aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
				listSorteados, getDezenaConfig());
		listViewJogos.setAdapter(adpterJogos);
		listViewJogos.setLayoutManager(new LinearLayoutManager(getContext()));

		if (temResultadoSegundoSorteio) {
			listViewJogos2.setVisibility(View.VISIBLE);
			JogosBolaoRecyclerView adpterJogos2 = new JogosBolaoRecyclerView(R.color.branco,
					aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
					listSorteados2, getDezenaConfig());
			listViewJogos2.setAdapter(adpterJogos2);
			listViewJogos2.setLayoutManager(new LinearLayoutManager(getContext()));
		}

		if (isSituacaoConcursoNaoApurado() || isApostaPequena()){
			view.findViewById(R.id.id_view_base).setVisibility(View.VISIBLE);
		}
	}

	private boolean isApostaPequena() {
		List<ApostaBolaoDTO> list = aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao();
		return list.size() == 1 || (list.size() < 4 && list.get(0).getDezenas().size() <= 6);
	}

	private boolean isSituacaoConcursoNaoApurado() {
		return aposta.getSituacao().getValor() == 101;
	}

	private boolean temResultadoSorteio() {
		return getResultadoConcurso() != null && getResultadoConcurso().getListaSorteadosPrimeiroSorteio() != null
				&& !getResultadoConcurso().getListaSorteadosPrimeiroSorteio().isEmpty();
	}

	private boolean temResultadoSegundoSorteio() {
		return getResultadoConcurso() != null && getResultadoConcurso().getListaSorteadosSegundoSorteio() != null
				&& !getResultadoConcurso().getListaSorteadosSegundoSorteio().isEmpty();
	}

	private DezenaConfig getDezenaConfig() {
		//ShapeConfig shapeConfig = new ShapeConfig(R.color.branco, R.color.mega_verde_escuro_mkp);
		ShapeConfig shapeConfig = new ShapeConfig(R.color.branco, estiloMKP.getCorEscura());

		return new DezenaConfig(false, R.color.branco,
				R.layout.item_dezena_detalhe,
				shapeConfig,false);
	}

	private List<Dezena> getDezenas(List<Integer> listaInteiros) {
		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < listaInteiros.size(); i++) {
			if (listaInteiros.get(i) == 100 || listaInteiros.get(i) == 0){
				dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE, "00"));
			} else {
				dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE, "" + listaInteiros.get(i)));
			}

		}
		return dezenas;
	}

	private View.OnClickListener onResgateClickListenr() {
		return v -> {
			irParaResgate(aposta.getId());
		};
	}

	private void setaViews() {
        imgTriangulo = view.findViewById(R.id.id_tag_bolao);
        concursoTxt = view.findViewById(R.id.concursoTxt);
		cotaTxt = view.findViewById(R.id.cotaTxt);
        dataApostaTxt = view.findViewById(R.id.dataApostaTxt);
        statusApostaTxt = view.findViewById(R.id.statusApostaTextView);
        tv_val_premio = view.findViewById(R.id.tv_val_premio);
        tv_val_premio_tit = view.findViewById(R.id.tv_val_premio_tit);

		tvDuplaSena1 = view.findViewById(R.id.id_dupla_sena1);
		tvNumerosSorteados = view.findViewById(R.id.id_seus_numeros);
		tvDuplaSena2 = view.findViewById(R.id.id_dupla_sena2);
		tvNumerosSorteados2 = view.findViewById(R.id.id_seus_numeros2);

		listViewSorteados = view.findViewById(R.id.id_lista_sorteados);
		listViewSorteados2 = view.findViewById(R.id.id_lista_sorteados2);
		listViewJogos = view.findViewById(R.id.id_lista_jogos);
		listViewJogos2 = view.findViewById(R.id.id_lista_jogos2);

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