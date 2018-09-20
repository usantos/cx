package caixa.gov.br.loterias.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.IncluirSurpresinhaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.helper.AdicionarApostaCarrinhoHelper;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

@EFragment(R.layout.fragment_surpresinha)
public class SurpresinhaFragment extends Fragment implements OnClickListener {

    private TextView valorAposta;
    private SimularApostaActivity parentActivity;
    private SurpresinhaFragmentListener surpresinhaFragmentListener;
    private ParametroEquipe equipeSelecionada = null;
    private boolean isTipoTimeMania, isTipoLotomania, isDiaDeSorte;
    private ParametroJogoDTO parametroJogo;
    private int typeGameColorLight, qtdSurpresinhas = 1;
    private SurpresinhaGridViewAdapter simularDezenasAdapter;
    private BigDecimal valorTotalAposta;

    @ViewById
    protected ImageView escolhaItemImageView;

    @ViewById
    protected TextView infoItemSurpresaTextView, qtdSurpresinhasSelecionadas;

    @ViewById
    protected Button escolherOutroItemBtn, btnMenosSurpresinha, btnMaisSurpresinha;

    @ViewById
    protected RelativeLayout escolhaItemLayout;

    @ViewById
    protected ExpandableHeightGridView listaDezenasSurpresinha;

    @ViewById
    protected LinearLayout layoutEspelhoSurpresinha;

    @ViewById
    protected AppCompatCheckBox selcioneEspelhoSurpresinha;

    public SurpresinhaFragment() {
        // Required empty public constructor
    }

    @AfterViews
    protected void init() {
        parentActivity = ((SimularApostaActivity) getActivity());
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            equipeSelecionada = new Gson().fromJson(bundle.getString("equipeSelecionada"), ParametroEquipe.class);
        }

        identificarTipoJogo();
        configuraListaDezenas();
        atualizaDezenaAdapter();
        configuraBotoes();

        if (isTipoLotomania) {
            layoutEspelhoSurpresinha.setVisibility(View.VISIBLE);
            selcioneEspelhoSurpresinha = layoutEspelhoSurpresinha.findViewById(R.id.selcioneEspelhoSurpresinha);
        }

        if (equipeSelecionada != null) {
            adicionarSurpresinhaNoCarrinho();
        }

        if (isTipoTimeMania) {
            escolhaItemLayout.setVisibility(View.VISIBLE);
        }

        if(isDiaDeSorte){
            initDiaDeSorte();
        }
    }

    public void surpresinhaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle != null) {
            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
        }
    }

    private void identificarTipoJogo() {
        ModalidadeEnum tipoJogo = (ModalidadeEnum) getActivity().getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo) {
            case TIMEMANIA:
                isTipoTimeMania = true;
                break;
            case LOTOMANIA:
                isTipoLotomania = true;
                break;
            case DIA_DE_SORTE:
                isDiaDeSorte = true;
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            surpresinhaFragmentListener = (SurpresinhaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SurpresinhaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        surpresinhaFragmentListener = null;
    }

    // METODOS DE CONFIGURACOES
    private void atualizaDezenaAdapter() {
        simularDezenasAdapter.rows = montaListaDezenasSurpresinha();
        simularDezenasAdapter.notifyDataSetChanged();
    }

    private void configuraBotoes() {
        valorAposta = parentActivity.getValorApostaCartela();
        if (parametroJogo != null) {
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        }

//        parentActivity.getSimularApostaLayoutCarrinhoApostas().setOnClickListener(this);

        btnMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinzaescuro));
        btnMaisSurpresinha.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));

        qtdSurpresinhasSelecionadas.setTextColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));

        Button botaoAdicionarSurpresinhaAoCarrinho = parentActivity.getBotaoAdicionarCompletarCartela();
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(Color.WHITE);
        botaoAdicionarSurpresinhaAoCarrinho.setOnClickListener(this);
        botaoAdicionarSurpresinhaAoCarrinho.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.adicionarAoCarrinho)));

        parentActivity.getBotaoLimparAposta().setVisibility(View.GONE);
        parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
    }

    private void configuraListaDezenas() {
        listaDezenasSurpresinha.setExpanded(true);
        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha());
        listaDezenasSurpresinha.setAdapter(simularDezenasAdapter);
    }

    private ArrayList<String> montaListaDezenasSurpresinha() {
        ArrayList<String> arrayDezenas = new ArrayList<>();
        for (int i = 0; i < Constantes.CINCO_INTEIRO; i++) {
            arrayDezenas.add("XX");
        }

        return arrayDezenas;
    }

    public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
        configuraListaDezenas();

        BigDecimal valorEmReais = BigDecimal.ZERO;

        for (ParametroValorApostaDTO parametro : parametroJogo.getValoresAposta()) {
            if (parametro.getNumeroPrognosticos() == numPrognostico) {
                valorEmReais = parametro.getValor();
            }
        }

        if (qtdSurpresinhas > 0) {
            if (qtdTeimosinhas == 0) {
                valorEmReais = valorEmReais.multiply(new BigDecimal(1.0)).multiply(new BigDecimal(qtdSurpresinhas));
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            } else {
                valorEmReais = valorEmReais.multiply(new BigDecimal(qtdTeimosinhas)).multiply(new BigDecimal(qtdSurpresinhas));
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            }
        } else {
            if (qtdTeimosinhas == 0) {
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            } else {
                valorEmReais = valorEmReais.multiply(new BigDecimal(qtdTeimosinhas));
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            }
        }
        if (isTipoLotomania && selcioneEspelhoSurpresinha.isChecked()) {
            valorEmReais = valorEmReais.multiply(Constantes.DOIS_BIG_DECIMAL);
            ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
        }

        valorTotalAposta = valorEmReais;
    }

    public void adicionarSurpresinhaNoCarrinho() {

        IncluirSurpresinhaDTO aposta = new IncluirSurpresinhaDTO();
        aposta.setModalidade(parametroJogo.getConcurso().getModalidade());
        aposta.setQuantidadeNumeros(parentActivity.qtdDezenasPossiveisSelecionado);
        aposta.setQuantidadeSurpresinhas(qtdSurpresinhas);

        DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

        String valorTipoConcurso = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
        tipoConcurso.setValor(valorTipoConcurso);
        tipoConcurso.setDescricao(parametroJogo.getConcurso().getTipoConcurso().toString());
        aposta.setTipoConcurso(tipoConcurso);

        DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
        indicadorSurpresinha.setValor(2);
        indicadorSurpresinha.setDescricao("");

        aposta.setIndicadorSurpresinha(indicadorSurpresinha);
        aposta.setConcursoAlvo(parametroJogo.getConcurso().getNumero());
        aposta.setQuantidadeTeimosinhas(parentActivity.qtdConcursoSelecionado);
        aposta.setQuantidadeApostas(1);
        aposta.setValor(valorTotalAposta);

        aposta.setTimeDoCoracao(equipeSelecionada);

        if (isTipoTimeMania && aposta.getTimeDoCoracao() == null) {
            Random randomGenerator = new Random();
            aposta.setTimeDoCoracao(parametroJogo.getEquipes().get(randomGenerator.nextInt(parametroJogo.getEquipes().size())));
        }
        if (isTipoLotomania) {
            aposta.setGerarEspelho(selcioneEspelhoSurpresinha.isChecked());
        }
//        if(isDiaDeSorte){
//            if(parentActivity.getMesDeSorteSurpresinha() == null){
//                Random randomGenerator = new Random();
//                List<ParametroMesDeSorte> mesesSorte = new ArrayList<>();
//               for (int contador = 0; contador < aposta.getQuantidadeSurpresinhas(); contador++){
//                   mesesSorte.add(parametroJogo.getMeses().get(randomGenerator.nextInt(parametroJogo.getMeses().size())));
//               }
//                aposta.setMesesDeSorte(mesesSorte);
//            }else{
//                aposta.setMesDeSorte(parentActivity.getMesDeSorteSurpresinha());
//            }
//        }

        AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoSurpresinha(parentActivity, aposta);
        parentActivity.zerarApostas();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botaoAdicionarCompletarCartela: {
                adicionarSurpresinhaNoCarrinho();
            }
            break;
        }
    }

    public void setEquipeSelecionada(ParametroEquipe equipeSelecionada) {
        this.equipeSelecionada = equipeSelecionada;
    }

    public BigDecimal getValorTotalAposta() {
        return valorTotalAposta;
    }

    public ParametroJogoDTO getParametroJogo() {
        return parametroJogo;
    }

    @Click
    protected void escolherOutroItemBtn() {
        if(isTipoTimeMania){
            parentActivity.escolherOutroTimeSurpresinha();
        }else if (isDiaDeSorte){
            parentActivity.mesDeSorteFragment(null, true);
        }

    }

    @Click
    protected void btnMenosSurpresinha() {
        if (qtdSurpresinhas > 1) {
            qtdSurpresinhas--;
            if (qtdSurpresinhas == 1) {
                btnMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinzaescuro));
            }
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
        }
    }

    @Click
    protected void btnMaisSurpresinha() {
        if (qtdSurpresinhas == 1) {
            btnMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        }
        if (qtdSurpresinhas < parametroJogo.getQuantidadeSurpresinhas()) {
            qtdSurpresinhas++;
        }
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
    }

    @Click
    protected void layoutEspelhoSurpresinha() {
        selecionarEspelho();
    }

    private void selecionarEspelho() {
        if (selcioneEspelhoSurpresinha.isChecked()) {
            selcioneEspelhoSurpresinha.setChecked(Boolean.FALSE);
        } else {
            selcioneEspelhoSurpresinha.setChecked(Boolean.TRUE);
        }
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
    }

    private void initDiaDeSorte(){
        escolhaItemImageView.setImageDrawable(getResources().getDrawable(R.drawable.agrupar_1));
        infoItemSurpresaTextView.setText(R.string.label_mes_surpresa);
        infoItemSurpresaTextView.setTextColor(getResources().getColor(R.color.diasortemedio));
        escolherOutroItemBtn.setText(R.string.label_escolher_outro_mes);
        escolherOutroItemBtn.setVisibility(View.GONE);
        escolhaItemLayout.setVisibility(View.VISIBLE);
    }

    // Adapter do gridview das dezenas
    private class SurpresinhaGridViewAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> rows;

        SurpresinhaGridViewAdapter(Context context, ArrayList<String> activitData) {
            this.context = context;
            this.rows = activitData;
        }

        @Override
        public int getCount() {
            return rows.toArray().length;
        }

        @Override
        public Object getItem(int i) {
            return rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView;
            GradientDrawable shape = new GradientDrawable();

            int sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getActivity().getResources().getDisplayMetrics());

            if (view == null) {
                textView = new TextView(this.context);
                textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
            } else {
                textView = (TextView) view;
            }

            shape.setColor(Color.WHITE);
            textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));

            shape.setCornerRadius(sizeItens / 2);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            textView.setText(rows.get(i));
            textView.setGravity(Gravity.CENTER);

            return textView;
        }
    }

    // INTERFACE DO FRAGMENT PARA A ACTIVITY
    public interface SurpresinhaFragmentListener {
        void adicionarApostaCarrinho();

        void abreCarrinhoApostas();

        void escolherOutroTimeSurpresinha();
    }

}
