package caixa.gov.br.loterias.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.IncluirSurpresinhaDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ViewUtils;

import static java.lang.String.format;

public class SurpresinhaFragment extends Fragment implements OnClickListener {

    int typeGameColorLight, typeGameColorDark, numeroConcursoAtual;
    int qtdSurpresinhas = 1;

    SurpresinhaGridViewAdapter simularDezenasAdapter;

    Button botaoMenosSurpresinha, botaoEscolhaOutrotime, botaoMaisSurpresinha, botaoAdicionarSurpresinhaAoCarrinho;

    private View divisaoLinhaDois;

    private TextView infoTimeSurpresa;

    private ImageView brasaoTimeSurpresa;
    TextView qtdSurpresinhasTextView, valorAposta;

    public ParametroJogoDTO parametroJogo;
    private SimularApostaActivity parentActivity;

    private SurpresinhaFragmentListener surpresinhaFragmentListener;
    private ParametroEquipe equipeSelecionada = null;
    private ModalidadeEnum tipoJogo;
    private boolean isTipoTimeMania, isTipoLotomania;
    private AppCompatCheckBox selcioneEspelhoSurpresinha;

    String dataSorteioAtual;
    public BigDecimal valorTotalAposta;

    public SurpresinhaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View fragmentView = getView();

        if (isTipoLotomania) {
            LinearLayout layoutEspelhoSurpresinha = fragmentView.findViewById(R.id.layoutEspelhoSurpresinha);
            layoutEspelhoSurpresinha.setVisibility(View.VISIBLE);
            layoutEspelhoSurpresinha.setOnClickListener(this);

            selcioneEspelhoSurpresinha = layoutEspelhoSurpresinha.findViewById(R.id.selcioneEspelhoSurpresinha);
        }
        configuraBotoes(fragmentView);
        configuraListaDezenas(fragmentView);
        configurarComponentesTimeMania();

        if (equipeSelecionada != null) {
            adicionarSurpresinhaNoCarrinho();
        }

        if (!isTipoTimeMania) {
            divisaoLinhaDois.setVisibility(View.GONE);
            brasaoTimeSurpresa.setVisibility(View.GONE);
            infoTimeSurpresa.setVisibility(View.GONE);
            botaoEscolhaOutrotime.setVisibility(View.GONE);
        }

    }

    private void configurarComponentesTimeMania() {
        divisaoLinhaDois = getView().findViewById(R.id.divisaoLinhaDois);
        brasaoTimeSurpresa = getView().findViewById(R.id.brasaoTimeSurpresa);
        infoTimeSurpresa = getView().findViewById(R.id.infoTimeSurpresa);
    }

    public void surpresinhaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle != null) {
            //mocks
            numeroConcursoAtual = dataBundle.getInt("numeroConcursoAtual");
            dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = ((SimularApostaActivity) getActivity());
        parametroJogo = parentActivity.getParametroSimulacao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_surpresinha, container, false);
//        configuraListaDezenas(view);
//        verificaSeMostraBotaoLimparAposta(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            equipeSelecionada = new Gson().fromJson(bundle.getString("equipeSelecionada"), ParametroEquipe.class);
        }

        identificarTipoJogo();

        return inflater.inflate(R.layout.fragment_surpresinha, container, false);
    }

    private void identificarTipoJogo() {
        tipoJogo = (ModalidadeEnum) getActivity().getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo) {
            case TIMEMANIA:
                isTipoTimeMania = true;
                break;
            case LOTOMANIA:
                isTipoLotomania = true;
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        atualizaDezenaAdapter();
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

    private void configuraBotoes(View fragmentView) {
        valorAposta = parentActivity.getValorApostaCartela();
        if (parametroJogo != null) {
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        }

        parentActivity.getSimularApostaLayoutCarrinhoApostas().setOnClickListener(this);

        botaoEscolhaOutrotime = fragmentView.findViewById(R.id.escolherOutroTimeBtn);
        botaoEscolhaOutrotime.setOnClickListener(this);

        botaoMenosSurpresinha = fragmentView.findViewById(R.id.btnMenosSurpresinha);
        botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
        botaoMenosSurpresinha.setOnClickListener(this);

        botaoMaisSurpresinha = fragmentView.findViewById(R.id.btnMaisSurpresinha);
        botaoMaisSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        botaoMaisSurpresinha.setOnClickListener(this);

        qtdSurpresinhasTextView = fragmentView.findViewById(R.id.qtdSurpresinhasSelecionadas);
        qtdSurpresinhasTextView.setTextColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));

        botaoAdicionarSurpresinhaAoCarrinho = parentActivity.getBotaoAdicionarCompletarCartela();
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(Color.WHITE);
        botaoAdicionarSurpresinhaAoCarrinho.setOnClickListener(this);
        botaoAdicionarSurpresinhaAoCarrinho.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.adicionarAoCarrinho)));

        parentActivity.getBotaoLimparAposta().setVisibility(View.GONE);
        parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
    }

    private void configuraListaDezenas(View fragmentView) {
        ExpandableHeightGridView listaDezenas = fragmentView.findViewById(R.id.listaDezenasSurpresinha);

        listaDezenas.setExpanded(true);

        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha());
        listaDezenas.setAdapter(simularDezenasAdapter);
    }

    private ArrayList<String> montaListaDezenasSurpresinha() {
        ArrayList<String> arrayDezenas = new ArrayList<>();
        for (int i = 0; i < Constantes.CINCO_INTEIRO; i++) {
            arrayDezenas.add("XX");
        }

        return arrayDezenas;
    }

    public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
        configuraListaDezenas(getView());

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

        final ApostaBO apostaBO = new ApostaBO(getActivity());
        final View loadViewProgress = LoadingViewLoterias.init(getActivity());

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

        aposta.setTimeDoCoracao(equipeSelecionada);
        if (isTipoLotomania) {
            aposta.setGerarEspelho(selcioneEspelhoSurpresinha.isChecked());
        }

        final IncluirSurpresinhaDTO apostaSurpresinha = aposta;
        apostaBO.adicionarSurpresinhaNoCarrinho(aposta, new SilceClient.Listener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                inserirSuspresinhaBaseLocal(apostaSurpresinha);
                surpresinhaFragmentListener.adicionarApostaCarrinho();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                if (error.networkResponse == null || error.networkResponse.statusCode == 401) {
                    inserirSuspresinhaBaseLocal(apostaSurpresinha);
                    surpresinhaFragmentListener.adicionarApostaCarrinho();
                } else {
                    ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.escolherOutroTimeBtn:
                parentActivity.escolherOutroTimeSurpresinha();
                break;
            case R.id.btnMenosSurpresinha: {
                if (qtdSurpresinhas > 1) {
                    qtdSurpresinhas--;
                    if (qtdSurpresinhas == 1) {
                        botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
                    }
                    atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
                }
            }
            break;

            case R.id.btnMaisSurpresinha: {
                if (qtdSurpresinhas == 1) {
                    botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
                }
                if (qtdSurpresinhas < parametroJogo.getQuantidadeSurpresinhas()) {
                    qtdSurpresinhas++;
                }
                atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            }
            break;

            case R.id.botaoAdicionarCompletarCartela: {
                adicionarSurpresinhaNoCarrinho();
            }
            break;

            case R.id.simularApostaLayoutCarrinhoApostas:
                surpresinhaFragmentListener.abreCarrinhoApostas();
                break;

            case R.id.layoutEspelhoSurpresinha:
                selecionarEspelho();
                break;
        }

        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));
    }

    public void setEquipeSelecionada(ParametroEquipe equipeSelecionada) {
        this.equipeSelecionada = equipeSelecionada;
    }

    private void selecionarEspelho() {
        if (selcioneEspelhoSurpresinha.isChecked()) {
            selcioneEspelhoSurpresinha.setChecked(Boolean.FALSE);
        } else {
            selcioneEspelhoSurpresinha.setChecked(Boolean.TRUE);
        }
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
    }

    private void inserirSuspresinhaBaseLocal(IncluirSurpresinhaDTO aposta) {
        IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades = new IdentificaoDeUmaApostaDas8Modalidades();
        apostaDas8Modalidades.setModalidade(aposta.getModalidade());
        apostaDas8Modalidades.setQuantidadeNumeros(aposta.getQuantidadeNumeros());
        apostaDas8Modalidades.setQuantidadeSurpresinhas(aposta.getQuantidadeSurpresinhas());
        apostaDas8Modalidades.setTipoConcurso(aposta.getTipoConcurso());
        apostaDas8Modalidades.setIndicadorSurpresinha(aposta.getIndicadorSurpresinha());
        apostaDas8Modalidades.setConcursoAlvo(aposta.getConcursoAlvo());
        apostaDas8Modalidades.setQuantidadeTeimosinhas(aposta.getQuantidadeTeimosinhas());
        apostaDas8Modalidades.setQuantidadeApostas(aposta.getQuantidadeApostas());
        apostaDas8Modalidades.setTimeDoCoracao(aposta.getTimeDoCoracao());
        apostaDas8Modalidades.setEspelho(aposta.getGerarEspelho());
        apostaDas8Modalidades.setValor(valorTotalAposta);

        DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
        crud.insertIdentificaoDeUmaApostaDas8Modalidades(apostaDas8Modalidades);
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
