package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosGridAdapter;

public class SuperSeteResultadoLayout extends LinearLayout {

	private boolean alreadyInflated = false;

	private RecyclerView rv_head, rv_body;
	private Context context;
	private LinearLayout  item_resultado_content_view;
	private View tracoViewDivisoriaResultado;
	private PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

	public static SuperSeteResultadoLayout build(Context context) {
		SuperSeteResultadoLayout instance = new SuperSeteResultadoLayout(context);
		instance.onFinishInflate();
		return instance;
	}

	@Override
	public void onFinishInflate() {
		if (!alreadyInflated) {
			alreadyInflated = true;
			inflate(getContext(), R.layout.item_resultado_supersete, this);
		}
		super.onFinishInflate();
		init();
	}

	protected void init() {
		this.rv_head = findViewById(R.id.rv_head);
		this.rv_body = findViewById(R.id.rv_body);
		this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
		this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
		this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
	}

	public void setLayout(Modalidade modalidade) {

		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade());
		tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));

		List<List<Integer>> matrizResultado = modalidade.getResultadoConcursoDTO().getMatrizNumerosSorteadosPrimeiroSorteio();
		List<String> matrizResultadosTexto = convertList(matrizResultado);

		ArrayList<String> listCabecalho1a7 = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
		setupGridNumeros(rv_head, Shape.RETANGULO_CAROUSEL, 7, listCabecalho1a7, context.getColor(R.color.super_sete_letra_mkp), context.getColor(R.color.super_sete_claro_mkp), context.getColor(R.color.super_sete_claro_mkp));
		setupGridNumeros(rv_body, Shape.RETANGULO_CAROUSEL, 7, matrizResultadosTexto, context.getColor(estilo.getCorFonteFundoEscuro()), Color.TRANSPARENT, Color.TRANSPARENT);

		this.partidaViewDetalhesResultadoView.setLayout(modalidade);
	}

	private List<String> convertList(List<List<Integer>> matrizResultado) {
		List<String> list = new ArrayList<>();
		list.add(matrizResultado.get(0).get(0).toString());
		list.add(matrizResultado.get(1).get(0).toString());
		list.add(matrizResultado.get(2).get(0).toString());
		list.add(matrizResultado.get(3).get(0).toString());
		list.add(matrizResultado.get(4).get(0).toString());
		list.add(matrizResultado.get(5).get(0).toString());
		list.add(matrizResultado.get(6).get(0).toString());
		return list;
	}

	public void setLayout(List<List<Integer>> matrizResultado) {
		List<String> matrizResultadosTexto = convertList(matrizResultado);
		tracoViewDivisoriaResultado.setVisibility(View.GONE);

		ArrayList<String> listCabecalho1a7 = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));

		setupGridNumeros(rv_head, Shape.RETANGULO_CAROUSEL, 7, listCabecalho1a7, context.getColor(R.color.super_sete_letra_mkp), context.getColor(R.color.super_sete_claro_mkp), context.getColor(R.color.super_sete_claro_mkp));
		setupGridNumeros(rv_body, Shape.RETANGULO_CAROUSEL, 7, matrizResultadosTexto, R.color.branco, Color.TRANSPARENT, Color.TRANSPARENT);
	}

	private void setupGridNumeros(RecyclerView recyclerView, Shape shape, int columns, List<String> numbers, int textColor, int borderColor, int backgroudColor) {
		recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
		recyclerView.setNestedScrollingEnabled(false);
		NumerosGridAdapter adapter = new NumerosGridAdapter(shape, numbers, textColor, borderColor, backgroudColor);
		recyclerView.setAdapter(adapter);
	}


	public SuperSeteResultadoLayout(Context context) {
		super(context);
		this.context = context;
	}

	public SuperSeteResultadoLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public SuperSeteResultadoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
}
