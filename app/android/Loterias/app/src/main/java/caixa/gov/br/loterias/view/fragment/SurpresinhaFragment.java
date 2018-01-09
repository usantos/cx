package caixa.gov.br.loterias.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
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
import caixa.gov.br.loterias.bo.silce.dto.IncluirSurpresinhaDTO;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.view.CadastrarActivity;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ViewUtils;

import static java.lang.String.format;

public class SurpresinhaFragment extends Fragment implements OnClickListener {

    int typeGameColorLight;
    int typeGameColorDark;

    SurpresinhaGridViewAdapter simularDezenasAdapter;

    Button botaoMenosSurpresinha;
    Button botaoEscolhaOutrotime;
    Button botaoMaisSurpresinha;
    Button botaoAdicionarSurpresinhaAoCarrinho;

    TextView qtdSurpresinhasTextView;
    TextView valorAposta;

    public ParametroJogoDTO parametroJogo;
    private SimularApostaActivity parentActivity;

    private SurpresinhaFragmentListener surpresinhaFragmentListener;
    private ParametroEquipe equipeSelecionada = null;

    //mocks
    int numeroConcursoAtual;
    String dataSorteioAtual;

    int qtdSurpresinhas = 1;

    public SurpresinhaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configuraBotoes(getView());
        configuraListaDezenas(getView());

        if (equipeSelecionada != null) {
            adicionarSurpresinhaNoCarrinho();
        }

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

        return inflater.inflate(R.layout.fragment_surpresinha, container, false);
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
        valorAposta = getView().findViewById(R.id.valorApostaSurpresinha);
        if (parametroJogo != null) {
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        }

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

        botaoAdicionarSurpresinhaAoCarrinho = fragmentView.findViewById(R.id.btnAdicionarSurpresinhaAoCarrinho);
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(Color.WHITE);
        botaoAdicionarSurpresinhaAoCarrinho.setOnClickListener(this);
        botaoAdicionarSurpresinhaAoCarrinho.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.adicionarAoCarrinho)));

        Button carrinhoImageView = fragmentView.findViewById(R.id.botaoCarrinho);
        carrinhoImageView.setOnClickListener(this);

        TextView textInfoCarrinho = fragmentView.findViewById(R.id.textInfoCarrinho);
        textInfoCarrinho.setOnClickListener(this);
    }

    private void configuraListaDezenas(View fragmentView) {
        ExpandableHeightGridView listaDezenas = fragmentView.findViewById(R.id.listaDezenasSurpresinha);

        listaDezenas.setExpanded(true);

        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha());
        listaDezenas.setAdapter(simularDezenasAdapter);
    }

    private ArrayList<String> montaListaDezenasSurpresinha() {
        ArrayList<String> arrayDezenas = new ArrayList<>();
        for (int i = 0; i < parentActivity.qtdDezenasPossiveisSelecionado; i++) {
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
                valorAposta.setText(ViewUtils.getMoedaFormat(valorEmReais));
            } else {
                valorEmReais = valorEmReais.multiply(new BigDecimal(qtdTeimosinhas)).multiply(new BigDecimal(qtdSurpresinhas));
                valorAposta.setText(ViewUtils.getMoedaFormat(valorEmReais));
            }
        } else {
            if (qtdTeimosinhas == 0) {
                valorAposta.setText(ViewUtils.getMoedaFormat(valorEmReais));
            } else {
                valorEmReais = valorEmReais.multiply(new BigDecimal(qtdTeimosinhas));
                valorAposta.setText(ViewUtils.getMoedaFormat(valorEmReais));
            }
        }
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

        apostaBO.adicionarSurpresinhaNoCarrinho(aposta, new SilceClient.Listener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);

                surpresinhaFragmentListener.adicionarApostaCarrinho();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
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

            case R.id.btnAdicionarSurpresinhaAoCarrinho: {
                adicionarSurpresinhaNoCarrinho();
            }
            break;

            case R.id.botaoCarrinho:
            case R.id.textInfoCarrinho: {
                surpresinhaFragmentListener.abreCarrinhoApostas();

            }
        }

        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));
    }

    public void setEquipeSelecionada(ParametroEquipe equipeSelecionada) {
        this.equipeSelecionada = equipeSelecionada;
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

            int sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getActivity().getResources().getDisplayMetrics());

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
