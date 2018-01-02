package caixa.gov.br.loterias.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.adapter.LotecaAdapter;
import caixa.gov.br.loterias.view.adapter.TimeCoracaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class CartelaFragment extends Fragment implements OnClickListener, LotecaAdapter.LocateAdapterListener {

    private ArrayList<String> dezenas = new ArrayList<String>();

    private ArrayList<Integer> dezenasSelecionadas = new ArrayList<>();

    private HashSet<ParametroPartida> partidasSelecionadas;

    int typeGameColorLight;
    int typeGameColorDark;

    Button botaoFinalizar;
    Button botaoLimparAposta;
    TextView valorAposta;

    SimularDezenasAdapter simularDezenasAdapter;

    //mocks
    int numeroConcursoAtual;
    int qtdCartelasCarrinho = 0;
    String dataSorteioAtual;

    private SimularApostaActivity parentActivity;
    private ParametroJogoDTO parametroJogo;
    private CartelaFragmentListener cartelaFragmentListener;
    private ExpandableHeightGridView mGridTimeCoracao;
    private ExpandableHeightGridView listaDezenas;
    private LinearLayout selecioneTimeCoracaoLayout;
    private EditText informeNomeTime;
    private ModalidadeEnum tipoJogo;
    private View view;
    private RecyclerView recyclerViewLoteca;
    private TimeCoracaoAdapter timeCoracaoAdapter;

    public BigDecimal valorTotalAposta;

    private boolean tipoLoteca = false;
    private boolean tipoTimeMania;


    public CartelaFragment() {
        // Required empty public constructor
    }

    public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
        BigDecimal valorEmReais = BigDecimal.ZERO;

        if (numPrognostico != 0) {
            for (ParametroValorApostaDTO parametro : parametroJogo.getValoresAposta()) {
                if (parametro.getNumeroPrognosticos() == numPrognostico) {
                    valorEmReais = parametro.getValor();
                }
            }
        } else {
            valorEmReais = parametroJogo.getValorApostaMinima();
        }
        if (qtdTeimosinhas == 0) {
            valorAposta.setText(ViewUtils.getMoedaFormat(valorEmReais));
        } else {
            valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdTeimosinhas).movePointLeft(0));
            valorAposta.setText(ViewUtils.getMoedaFormat(valorEmReais));
        }

        valorTotalAposta = valorEmReais;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = ((SimularApostaActivity) getActivity());
        parametroJogo = parentActivity.getParametroSimulacao();

        if (dezenas.size() == 0 && parentActivity.getParametroSimulacao().getPrognosticoMaximo() != null) {
            for (int i = 1; i <= parentActivity.getParametroSimulacao().getPrognosticoMaximo(); i++) {
                dezenas.add("" + i);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configuraBotoes(getView());

        ViewUtils.organizaTabLayout(getActivity(), R.id.cartelaFragmentMainLayout, R.id.listaDezenas, R.id.cartelaFragmentScrollView);

    }

    public void cartelaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle != null) {
            numeroConcursoAtual = dataBundle.getInt("numeroConcursoAtual");
            dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cartela, container, false);
        identificarTipoJogo();
        configuraListaDezenas(view);
        verificaSeMostraBotaoLimparAposta(view);
        verificaSeMostraSalvarApostaView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cartelaFragmentListener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartelaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cartelaFragmentListener = null;
    }

    private void identificarTipoJogo() {
        tipoJogo = (ModalidadeEnum) getActivity().getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo) {
            case MEGA_SENA:
                break;
            case QUINA:
                break;
            case LOTOFACIL:
                break;
            case DUPLA_SENA:
                break;
            case TIMEMANIA:
                tipoTimeMania = true;
                break;
            case LOTOMANIA:
                break;
            case LOTECA:
                tipoLoteca = true;
                break;
        }
    }

    private void preencherVisibilidadeGridPorTipo() {
        if (tipoJogo == null) {
            tipoJogo = (ModalidadeEnum) getActivity().getIntent().getSerializableExtra("tipoAposta");
        }

        switch (tipoJogo) {
            case MEGA_SENA:
                break;
            case QUINA:
                break;
            case LOTOFACIL:
                break;
            case DUPLA_SENA:
                break;
            case TIMEMANIA:
                preencherVisibilidaeGridView(View.VISIBLE, View.GONE, View.GONE, View.GONE);
                break;
            case LOTOMANIA:
                break;
            case LOTECA:
                preencherVisibilidaeGridView(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                break;
        }
    }

    private void preencherVisibilidaeGridView(int visibilidadeEscolhaTimeCoracao, int visibilidadeSelecioneTimeCoracao, int visibilidadeEscolhaLoteca,
                                              int visibilidadeDezanas) {
        if (mGridTimeCoracao != null) {
            mGridTimeCoracao.setVisibility(visibilidadeEscolhaTimeCoracao);
        }

        if (selecioneTimeCoracaoLayout != null) {
            selecioneTimeCoracaoLayout.setVisibility(visibilidadeSelecioneTimeCoracao);
        }

        if (recyclerViewLoteca != null) {
            recyclerViewLoteca.setVisibility(visibilidadeEscolhaLoteca);

        }

        if (listaDezenas != null) {
            listaDezenas.setVisibility(visibilidadeDezanas);
        }

    }


    private void adicionarApostaNoCarrinho() {
        final ApostaBO apostaBO = new ApostaBO(getActivity());
        final View loadViewProgress = LoadingViewLoterias.init(getActivity());

        IdentificaoDeUmaApostaDas8Modalidades aposta = new IdentificaoDeUmaApostaDas8Modalidades();
        aposta.setId(new Long(0));
        aposta.setModalidade(parametroJogo.getConcurso().getModalidade());

        DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

        String valorTipoConcurso = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
        tipoConcurso.setValor(valorTipoConcurso);
        tipoConcurso.setDescricao(parametroJogo.getConcurso().getTipoConcurso().toString());
        aposta.setTipoConcurso(tipoConcurso);
        aposta.setPartidasLotogol(null);
        aposta.setPartidasLoteca(null);
        aposta.setNumerosSelecionados(dezenasSelecionadas);

        DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
        indicadorSurpresinha.setValor(1);
        indicadorSurpresinha.setDescricao("");

        aposta.setIndicadorSurpresinha(indicadorSurpresinha);
        aposta.setValor(valorTotalAposta);
        aposta.setConcursoAlvo(parametroJogo.getConcurso().getNumero());
        aposta.setQuantidadeTeimosinhas(parentActivity.qtdConcursoSelecionado);
        aposta.setQuantidadeApostas(1);
        aposta.setEspelho(false);

        aposta.setTimeDoCoracao(null);

        aposta.setConcursoInicial(0);
        aposta.setSituacao(null);
        aposta.setTroca(false);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        Date hour = new Date();

        aposta.setDataEfetivacao(dateFormat.format(date));
        aposta.setHoraEfetivacao(hourFormat.format(hour));
        aposta.setVinculoEspelho(new Long(0));
        aposta.setQuantidadeNumeros(dezenasSelecionadas.size());
        aposta.setSurpresinha(false);

        apostaBO.adicionarApostaNoCarrinho(aposta, new SilceClient.Listener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                TextView count = getView().findViewById(R.id.textViewCarrinhoCartelaCount);

                qtdCartelasCarrinho++;

                count.setText(String.format("%d", qtdCartelasCarrinho));

                cartelaFragmentListener.adicionarApostaCarrinho();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
            }
        });
    }

    // METODO DO PROTOCOLO ONCLICK
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botaoAdicionarCompletarCartela: {
                if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
                    adicionarApostaNoCarrinho();
                } else {
                    preencheNumerosAleatorios();
                }
            }
            break;

            case R.id.botaoLimparAposta: {
                dezenasSelecionadas.clear();
                verificaSeMostraBotaoLimparAposta(getView());
                atualizaStatusBotaoFinalizar();
                simularDezenasAdapter.notifyDataSetChanged();
                voltaSalvarApostaParaLayoutInicial();
            }
            break;

            case R.id.botaoCarrinho:
            case R.id.textInfoCarrinho: {
                cartelaFragmentListener.abreCarrinhoApostas();
            }
            break;

            case R.id.salvarApostaLayout: {
                RelativeLayout salvarApostaLayout = view.findViewById(R.id.salvarApostaLayout);

                ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                final float scale = getContext().getResources().getDisplayMetrics().density;
                relativelayoutParams.height = (int) (140 * scale + 0.5f);

                salvarApostaLayout.setLayoutParams(relativelayoutParams);
                salvarApostaLayout.setOnClickListener(null);

                salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
            }
            break;

            case R.id.btnSalvarAposta: {
                EditText nomeAposta = getView().findViewById(R.id.editNomeAposta);

                if (!TextUtils.isEmpty(nomeAposta.getText().toString())) {
                    salvarAposta(nomeAposta.getText().toString());
                } else {
                    ViewUtils.alert(getActivity(), getString(R.string.msg_favor_inserir_nome_aposta));
                }
            }

        }
    }

    private Boolean flagApostaSalva = false;

    private void salvarAposta(String nomeAposta) {
        final DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(getActivity());
        final View loadViewProgress = LoadingViewLoterias.init(getActivity());

        final ApostaFavoritaDTO aposta = new ApostaFavoritaDTO();

        DTOEnumInteger modalidade = new DTOEnumInteger();
        modalidade.setDescricao(parametroJogo.getConcurso().getModalidadeDetalhada().getDescricao());
        modalidade.setValor(parametroJogo.getConcurso().getModalidadeDetalhada().getValor());
        aposta.setModalidade(modalidade);

        aposta.setNome(nomeAposta);
        aposta.setNumerosSelecionados(dezenasSelecionadas);
        aposta.setId(new Long(0));

        ParametroEquipe timeCoracao = new ParametroEquipe();
        timeCoracao.setDescricaoCurta("");
        timeCoracao.setDescricaoLonga("");
        timeCoracao.setIndicadorSelecao(false);
        timeCoracao.setNome("");
        timeCoracao.setNomeClass("");
        timeCoracao.setNumero(0);
        timeCoracao.setPais("");
        timeCoracao.setNumeroPais(0);
        timeCoracao.setSiglaPais("");
        timeCoracao.setUf("");

        aposta.setTimeDoCoracao(timeCoracao);

        dadosCorporativosBO.validarApostaFavorita(aposta, new SilceClient.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                dadosCorporativosBO.salvarApostaFavorita(aposta, new SilceClient.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        LoadingViewLoterias.stop(loadViewProgress);

                        RelativeLayout salvarApostaLayout = getView().findViewById(R.id.salvarApostaLayout);

                        salvarApostaLayout.setBackgroundResource(R.color.verdeazul);
                        ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                        final float scale = getContext().getResources().getDisplayMetrics().density;
                        relativelayoutParams.height = (int) (64 * scale + 0.5f);

                        salvarApostaLayout.setLayoutParams(relativelayoutParams);

                        TextView tituloSalvarAposta = getView().findViewById(R.id.textoSalveEstaAposta);
                        tituloSalvarAposta.setText("Aposta salva :)");
                        tituloSalvarAposta.setGravity(Gravity.CENTER);

                        ImageView starSalvarApostaImageView = getView().findViewById(R.id.iconStarSalvar);
                        starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritado);

                        flagApostaSalva = true;
                    }
                }, new SilceClient.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingViewLoterias.stop(loadViewProgress);
                        ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                    }
                });
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
            }
        });
    }

    private void preencheNumerosAleatorios() {
        ApostaBO apostaBO = new ApostaBO(getActivity());

        apostaBO.completaJogo(parentActivity.qtdDezenasPossiveisSelecionado, dezenasSelecionadas, 1, parametroJogo.getPrognosticoMaximo(), new SilceClient.Listener<CompletaJogoRsp>() {
            @Override
            public void onResponse(CompletaJogoRsp response) {
                dezenasSelecionadas.clear();
                dezenasSelecionadas.addAll(response.getPayload());
                simularDezenasAdapter.notifyDataSetChanged();
                atualizaStatusBotaoFinalizar();
                if (tipoTimeMania) {
                    mostrarCartelaEscolhaTimeCoracao();
                } else {
                    verificaSeMostraBotaoLimparAposta(getView());
                    verificaSeMostraSalvarApostaView(getView());

                }
            }


        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
            }
        });
    }

    private void mostrarCartelaEscolhaTimeCoracao() {
        // Estando a aposta completa apresenta a tela de escolha do time
        selecioneTimeCoracaoLayout = view.findViewById(R.id.selecioneTimeCoracao);
        mGridTimeCoracao = view.findViewById(R.id.gridTimes);
        preencherVisibilidaeGridView(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        informeNomeTime = selecioneTimeCoracaoLayout.findViewById(R.id.informe_nome_time);
        onChangeInformeNomeTime();
        configurarSelecaoTimeCoracao(mGridTimeCoracao);
    }

    private void onChangeInformeNomeTime() {
        informeNomeTime.addTextChangedListener(new TextWatcher() {
            private boolean isChanged = false;


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isChanged) {
                    return;
                }

                isChanged = true;

                if (charSequence.length() > 3) {

                    List<ParametroEquipe> parametroEquipeList = parametroJogo.getEquipes();
                    List<ParametroEquipe> filter = new ArrayList<>();

                    for (ParametroEquipe parametroEquipe : parametroEquipeList) {
                        if (parametroEquipe.getNome().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                            filter.add(parametroEquipe);
                        }
                    }
                    timeCoracaoAdapter.setTimes(filter);
                    timeCoracaoAdapter.notifyDataSetChanged();
                }

                isChanged = false;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void configuraBotoes(View fragmentView) {
        valorAposta = getView().findViewById(R.id.valorApostaCartela);
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);

        botaoFinalizar = fragmentView.findViewById(R.id.botaoAdicionarCompletarCartela);
        botaoFinalizar.setOnClickListener(this);
        botaoFinalizar.setBackgroundColor(ContextCompat.getColor(fragmentView.getContext(), typeGameColorLight));
        botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(fragmentView.getContext(), getString(R.string.completarCartela)));

        Button carrinhoImageView = fragmentView.findViewById(R.id.botaoCarrinho);
        carrinhoImageView.setOnClickListener(this);

        TextView textInfoCarrinho = fragmentView.findViewById(R.id.textInfoCarrinho);
        textInfoCarrinho.setOnClickListener(this);

        botaoLimparAposta = fragmentView.findViewById(R.id.botaoLimparAposta);
        botaoLimparAposta.setOnClickListener(this);
    }

    public void voltaSalvarApostaParaLayoutInicial() {
        RelativeLayout salvarApostaLayout = getView().findViewById(R.id.salvarApostaLayout);

        salvarApostaLayout.setBackgroundResource(R.color.cinzaescuro);
        TextView tituloSalvarAposta = getView().findViewById(R.id.textoSalveEstaAposta);

        tituloSalvarAposta.setText("Salve esta aposta :)");

        ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

        final float scale = getContext().getResources().getDisplayMetrics().density;
        relativelayoutParams.height = (int) (64 * scale + 0.5f);

        salvarApostaLayout.setLayoutParams(relativelayoutParams);
        salvarApostaLayout.setOnClickListener(null);

        ImageView starSalvarApostaImageView = getView().findViewById(R.id.iconStarSalvar);
        starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritar);

        salvarApostaLayout.setOnClickListener(this);

        EditText nomeAposta = getView().findViewById(R.id.editNomeAposta);
        nomeAposta.setText("");
        getView().findViewById(R.id.salvarApostaLayout).setVisibility(View.GONE);
    }

    private void verificaSeMostraBotaoLimparAposta(View fragmentView) {
        if (dezenasSelecionadas.isEmpty()) {
            fragmentView.findViewById(R.id.botaoLimparAposta).setVisibility(View.GONE);
        } else {
            fragmentView.findViewById(R.id.botaoLimparAposta).setVisibility(View.VISIBLE);
        }
    }

    private void verificaSeMostraSalvarApostaView(View fragmentView) {
        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
            RelativeLayout salvarApostaLayout = fragmentView.findViewById(R.id.salvarApostaLayout);

            salvarApostaLayout.setVisibility(View.VISIBLE);
            final float scale = getContext().getResources().getDisplayMetrics().density;
            salvarApostaLayout.getLayoutParams().height = (int) (64 * scale + 0.5f);
            salvarApostaLayout.setOnClickListener(this);
        } else {
            fragmentView.findViewById(R.id.salvarApostaLayout).setVisibility(View.GONE);
        }
    }

    public void atualizaStatusBotaoFinalizar() {
        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
            botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.adicionarAoCarrinho)));
        } else if (dezenasSelecionadas.size() > parentActivity.qtdDezenasPossiveisSelecionado) {
            //dezenasSelecionadas.clear();
            simularDezenasAdapter.notifyDataSetChanged();
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
            botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.completarCartela)));

            getView().findViewById(R.id.salvarApostaLayout).setVisibility(View.GONE);
            getView().findViewById(R.id.botaoLimparAposta).setVisibility(View.GONE);
        } else {
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
            botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.completarCartela)));
        }
    }

    private void configuraListaDezenas(View fragmentView) {

        listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        recyclerViewLoteca = fragmentView.findViewById(R.id.recyclerViewLoteca);

        if (dezenas != null && dezenas.size() > 0) {

            listaDezenas.setExpanded(true);
            simularDezenasAdapter = new SimularDezenasAdapter(getActivity(), dezenas);
            listaDezenas.setAdapter(simularDezenasAdapter);
            listaDezenas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Integer valorSelecionado = Integer.valueOf(dezenas.get(position));

                    if (dezenasSelecionadas.contains(valorSelecionado)) {
                        dezenasSelecionadas.remove(valorSelecionado);
                    } else {
                        int maxNumerosParaSelecionar = parametroJogo.getQuantidadeMaxima();
                        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
                            // VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS
                            int qtdDezenasPossiveisSelecionadoAtual = parentActivity.qtdDezenasPossiveisSelecionado;

                            parentActivity.qtdDezenasPossiveisSelecionado = cartelaFragmentListener.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);

                            if (qtdDezenasPossiveisSelecionadoAtual < maxNumerosParaSelecionar) {
                                parentActivity.qtdDezenasPossiveisSelecionado += 1;
                                parentActivity.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);
                                atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
                                if (flagApostaSalva) {
                                    voltaSalvarApostaParaLayoutInicial();
                                }
                                dezenasSelecionadas.add(valorSelecionado);
                            } else {
                                ViewUtils.alert(getActivity(), "Número de dezenas máximo selecionadas.");
                            }
                        } else {
                            dezenasSelecionadas.add(valorSelecionado);
                        }
                    }

                    verificaSeMostraSalvarApostaView(getView());
                    verificaSeMostraBotaoLimparAposta(getView());
                    atualizaStatusBotaoFinalizar();
                    simularDezenasAdapter.notifyDataSetChanged();
                }
            });
        } else {
            if (tipoLoteca) {
                configurarTimesLoteca();
                partidasSelecionadas = new HashSet<>();
                fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
            }

        }

    }

    private void configurarTimesLoteca() {
        LotecaAdapter lotecaAdapter = new LotecaAdapter(parametroJogo.getPartidas(), this);
        recyclerViewLoteca.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewLoteca.setAdapter(lotecaAdapter);
    }

    /**
     * Metodo configura a cartela de times do coracao
     *
     * @param gridTimes
     */
    private void configurarSelecaoTimeCoracao(ExpandableHeightGridView gridTimes) {

        timeCoracaoAdapter = new TimeCoracaoAdapter(getActivity(), parametroJogo.getEquipes());
        gridTimes.setAdapter(timeCoracaoAdapter);
        gridTimes.setExpanded(true);
        gridTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.isSelected()) {
                    view.setSelected(Boolean.FALSE);
                } else {
                    view.setSelected(Boolean.TRUE);
                }
            }
        });
    }

    @Override
    public void clickado(ParametroPartida parametroPartida) {
        if(parametroPartida.isSelecionado()){
            if(parametroPartida.getQtdItensSelecionados() == 1){
                parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) + 1));
                if(parametroPartida.getQtdItensSelecionadosAnterior() == 2){
                    parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
                }
            }else if(parametroPartida.getQtdItensSelecionados() == 2){
                parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) + 1));
                if(parametroPartida.getQtdItensSelecionadosAnterior() == 3){
                    parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) - 1));
                }else{
                    parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
                }
            }else if(parametroPartida.getQtdItensSelecionados() == 3){
                parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
                parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) + 1));
            }
            partidasSelecionadas.add(parametroPartida);
        }else{
            parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
            partidasSelecionadas.remove(parametroPartida);
        }
    }

    // Adapter do gridview das dezenas
    private class SimularDezenasAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> rows;

        SimularDezenasAdapter(Context context, ArrayList<String> activitData) {
            this.context = context;
            this.rows = activitData;
        }

        @Override
        public int getCount() {
            return rows.size();
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

            if (dezenasSelecionadas.contains(Integer.valueOf(rows.get(i)))) {
                shape.setColor(ContextCompat.getColor(context, typeGameColorLight));
                textView.setTextColor(Color.WHITE);
            } else {
                shape.setColor(Color.WHITE);
                textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));
            }

            shape.setCornerRadius(sizeItens / 2);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            if (dezenas.get(i).length() == 1) {
                textView.setText("0" + dezenas.get(i));
            } else {

                textView.setText(dezenas.get(i));
            }
            textView.setTypeface(ViewUtils.getFontFuturaBold(context));
            textView.setGravity(Gravity.CENTER);


            return textView;
        }
    }

    // INTERFACE DO FRAGMENT PARA A ACTIVITY
    public interface CartelaFragmentListener {
        int verificaNovoQtdDezenasMax(int qtdDezenasAtual);

        void adicionarApostaCarrinho();

        void abreCarrinhoApostas();
    }
}
