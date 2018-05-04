package caixa.gov.br.loterias.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.Dezena;
import caixa.gov.br.loterias.model.bo.silce.dto.EquipeDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroValorApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.DialogUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.helper.AdicionarApostaCarrinhoHelper;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.adapter.LotecaAdapter;
import caixa.gov.br.loterias.view.adapter.LotogolAdapter;
import caixa.gov.br.loterias.view.adapter.TimeCoracaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.listener.CartelaFragmentListener;
import caixa.gov.br.loterias.view.listener.LotogolListener;

public class CartelaFragment extends Fragment implements OnClickListener, LotecaAdapter.LocateAdapterListener, LotogolListener {

    private List<Dezena> dezenas = new ArrayList<>();
    private List<Integer> dezenasSelecionadas = new ArrayList<>();
    private List<Integer> dezenasApostaEspelho = new ArrayList<>();
    private List<ParametroEquipe> listaEquipefiltro;
    private HashSet<ParametroPartida> partidasSelecionadas;

    private CartelaFragmentListener cartelaFragmentListener;

    private ParametroEquipe equipeSelecionada = null;
    private ParametroJogoDTO parametroJogo;
    private ModalidadeEnum tipoJogo;
    private TimeCoracaoAdapter timeCoracaoAdapter;

    private SimularApostaActivity parentActivity;
    private AppCompatCheckBox selcioneOutrosNumeros;
    private View view;
    private TextView infoEscolha, infoPaginacao;
    private EditText informeNomeTime;
    private RelativeLayout opcaoOutrosNumerosLayout;
    private LinearLayout selecioneTimeCoracaoLayout;
    private ExpandableHeightGridView mGridTimeCoracao, listaDezenas;
    private ExpandableHeightRecyclerView recyclerViewPartida;

    private boolean isTipoLoteca, isTipoTimemania, isEcolhaTimeBtnAtivado, isTelaSelecaoTimeAtivado, isTipoLotogol, isTipoLotomania, isEscolhaTimeCoracaoSurpresinha;

    public BigDecimal valorTotalAposta;
    public int typeGameColorLight, typeGameColorDark, numeroConcursoAtual, qtdCartelasCarrinho = 0;
    public Button botaoFinalizar, botaoLimparAposta;
    public TextView valorAposta;
    public SimularDezenasAdapter simularDezenasAdapter;
    public String dataSorteioAtual;

    public CartelaFragment() {
        // Required empty public constructor
    }

    public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
        BigDecimal valorEmReais = BigDecimal.ZERO;

        if (numPrognostico != 0) {
            for (ParametroValorApostaDTO parametro : parametroJogo.getValoresAposta()) {
                if (parametro.getNumeroPrognosticos() == numPrognostico) {
                    valorEmReais = parametro.getValor();
                    break;
                }
            }
        } else if (parametroJogo.getValorApostaMinima() != null) {
            valorEmReais = parametroJogo.getValorApostaMinima();
        } else if (parametroJogo.getConcurso().getValorApostaMinima() != null) {
            valorEmReais = parametroJogo.getConcurso().getValorApostaMinima();
        }
        if (qtdTeimosinhas != 0) {
            valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdTeimosinhas).movePointLeft(0));
        }

        if (isTipoLotomania && selcioneOutrosNumeros.isChecked()) {
            valorEmReais = valorEmReais.multiply(Constantes.DOIS_BIG_DECIMAL);
        }

        ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
        valorTotalAposta = valorEmReais;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = ((SimularApostaActivity) getActivity());
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
        }

        if (parametroJogo != null && dezenas.size() == 0 && parametroJogo.getPrognosticoMaximo() != null) {
            for (int i = 1; i <= parametroJogo.getPrognosticoMaximo(); i++) {
                dezenas.add(new Dezena("" + i, Boolean.FALSE));
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int idList;
        if (isTipoLoteca || isTipoLotogol) {
            idList = R.id.recyclerViewPartida;
        } else {
            idList = R.id.listaDezenas;
        }

        if (isTipoTimemania) {
            RelativeLayout infoEscolhaPaginacaoLayout = view.findViewById(R.id.infoEscolhaPaginacaoLayout);
            infoEscolha = infoEscolhaPaginacaoLayout.findViewById(R.id.infoEscolha);
            infoPaginacao = infoEscolhaPaginacaoLayout.findViewById(R.id.infoPaginacao);
            infoEscolhaPaginacaoLayout.setVisibility(View.VISIBLE);
        }

        if (isTipoLotomania) {
            opcaoOutrosNumerosLayout = view.findViewById(R.id.opcaoOutrosNumerosLayout);
            selcioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.selcioneOutrosNumeros);
            TextView infoSelecioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.infoSelecioneOutrosNumeros);
            infoSelecioneOutrosNumeros.setText(ViewUtils.fromHtml(Constantes.INFO_OUTROS_50_NUMEROS));
            onClickOutrosNumerosLayout();
            if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
                setVisibilityCheckedNumerosLayout(View.VISIBLE);
            } else {
                setVisibilityCheckedNumerosLayout(View.GONE);
            }
        }

        configuraBotoes(view);
        // Chama a tela de escolha de time
        if (isEscolhaTimeCoracaoSurpresinha) {
            mostrarCartelaEscolhaTimeCoracao();
        } else {
            isTelaSelecaoTimeAtivado = false;
            limparTimeSelecionado();
        }

        if (isTipoTimemania && !isTelaSelecaoTimeAtivado)

        {
            // Tela de escolha de números
            infoEscolha.setText(ViewUtils.fromHtml(Constantes.ESCOLHA_NUMEROS_HTML));
            infoPaginacao.setText(ViewUtils.fromHtml(Constantes.PAGINACAO_UM_DOIS));
        }
        editeAposta();
        if (!isEscolhaTimeCoracaoSurpresinha)

        {
            configuraListaDezenas(view);
            verificaSeMostraBotaoLimparAposta();
            verificaSeMostraSalvarApostaView();
        }

        ViewUtils.organizaTabLayout(getActivity(), R.id.cartelaFragmentMainLayout, idList, R.id.cartelaFragmentScrollView);
        ((ScrollView) view.findViewById(R.id.cartelaFragmentScrollView)).fullScroll(ScrollView.FOCUS_UP);
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

        if (parentActivity.getIntent() != null) {
            tipoJogo = (ModalidadeEnum) parentActivity.getIntent().getSerializableExtra("tipoAposta");
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
                isTipoTimemania = true;
                break;
            case LOTOMANIA:
                isTipoLotomania = true;
                break;
            case LOTECA:
                isTipoLoteca = true;
                break;
            case LOTOGOL:
                isTipoLotogol = true;
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

        if (recyclerViewPartida != null) {
            recyclerViewPartida.setVisibility(visibilidadeEscolhaLoteca);

        }

        if (listaDezenas != null) {
            listaDezenas.setVisibility(visibilidadeDezanas);
        }

    }


    private void adicionarApostaNoCarrinho() {

        if (isEscolhaTimeCoracaoSurpresinha) {
            cartelaFragmentListener.adicionarTimeSurpresa(equipeSelecionada);
            return;
        }

        orderAscDezenas();
        IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida();
        adicionarApostaCarrinhoRequest(ApostaBO_.getInstance_(getActivity()), getActivity(), aposta);
    }

    private IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida() {

        IdentificaoDeUmaApostaDas8Modalidades aposta = new IdentificaoDeUmaApostaDas8Modalidades();

        aposta.setId(Constantes.ZERO_LONG);
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

        aposta.setTimeDoCoracao(equipeSelecionada);

        aposta.setConcursoInicial(0);
        aposta.setSituacao(null);
        aposta.setTroca(false);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date date = new Date();

        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Date hour = new Date();

        aposta.setDataEfetivacao(dateFormat.format(date));
        aposta.setHoraEfetivacao(hourFormat.format(hour));
        aposta.setVinculoEspelho(Constantes.ZERO_LONG);
        aposta.setQuantidadeNumeros(dezenasSelecionadas.size());
        aposta.setSurpresinha(false);

        this.setApostaLoteca(aposta);
        this.setApostaLotogol(aposta);

        return aposta;
    }

    private void adicionarApostaCarrinhoRequest(final ApostaBO apostaBO, final Activity activity, final IdentificaoDeUmaApostaDas8Modalidades aposta) {
        DialogUtils.show(getActivity());

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                CarrinhoDTO carrinhoDTO = new CarrinhoDTO();
                carrinhoDTO = response.getPayload();
                if (IdentificaoDeUmaApostaDas8Modalidades.checkExisteUmaApostaIgualCarrinho( aposta, carrinhoDTO )){
                    DialogUtils.dismiss();
                    ViewUtils.alertTitleButtonYesOrNotListener(getContext(), getResources().getString(R.string.MA015), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DialogUtils.show(getActivity());
                            postAdicionarApostaCarrinho( apostaBO, aposta );
                        }
                    });
                }else {
                    postAdicionarApostaCarrinho( apostaBO, aposta );
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, getActivity() );
            }
        });

    }

    private void postAdicionarApostaCarrinho(ApostaBO apostaBO, final IdentificaoDeUmaApostaDas8Modalidades aposta){
        apostaBO.adicionarApostaNoCarrinho(aposta, new RequestListener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse response) {
                DialogUtils.dismiss();
                TextView count = parentActivity.getTextViewCarrinhoCartelaCount();

                qtdCartelasCarrinho++;

                count.setText(String.format(Locale.getDefault(), "%d", qtdCartelasCarrinho));
                cartelaFragmentListener.adicionarApostaCarrinho();

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();

                if (MensagensNetwork.isUnauthorizedError(error)) {
                    if (AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(getActivity(), aposta)) {
                        cartelaFragmentListener.adicionarApostaCarrinho();
                    }
                } else {
                    ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                }
            }
        });
    }

    private void adicionarApostasCarrinhoRequest(final ApostaBO apostaBO, Activity activity, final List<IdentificaoDeUmaApostaDas8Modalidades> apostas) {
        DialogUtils.show(getActivity());

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                CarrinhoDTO carrinhoDTO = new CarrinhoDTO();
                carrinhoDTO = response.getPayload();
                if (IdentificaoDeUmaApostaDas8Modalidades.checkExisteUmaApostaIgualCarrinhoEmListaApostas( apostas, carrinhoDTO )){
                    DialogUtils.dismiss();
                    ViewUtils.alertTitleButtonYesOrNotListener(getContext(), getResources().getString(R.string.MA015), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DialogUtils.show(getActivity());
                            postAdicionarApostasCarrinhoRequest( apostaBO, apostas );
                        }
                    });
                }else {
                    postAdicionarApostasCarrinhoRequest( apostaBO, apostas );
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, getActivity() );
            }
        });

    }

    private void postAdicionarApostasCarrinhoRequest(ApostaBO apostaBO, final List<IdentificaoDeUmaApostaDas8Modalidades> apostas){
        apostaBO.adicionarApostasNoCarrinho(apostas, new RequestListener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse response) {
                DialogUtils.dismiss();
                TextView count = parentActivity.getTextViewCarrinhoCartelaCount();

                qtdCartelasCarrinho++;

                count.setText(String.format(Locale.getDefault(), "%d", qtdCartelasCarrinho));
                cartelaFragmentListener.adicionarApostaCarrinho();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    boolean retorno = false;
                    for (IdentificaoDeUmaApostaDas8Modalidades aposta : apostas) {
                        retorno = AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(getActivity(), aposta);
                    }
                    if (retorno) {
                        cartelaFragmentListener.adicionarApostaCarrinho();
                    }
                } else {
                    ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                }
            }
        });
    }

    // METODO DO PROTOCOLO ONCLICK
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.botaoAdicionarCompletarCartela: {
                if (isTipoTimemania) {
                    if (isEcolhaTimeBtnAtivado && dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
                        if (mGridTimeCoracao == null) {
                            mostrarCartelaEscolhaTimeCoracao();
                            timemaniaTimeSelecionadoEdicao();
                        } else {
                            if (equipeSelecionada != null) {
                                if (equipeSelecionada.isSelecionado()) {
                                    adicionarApostaNoCarrinho();
                                } else {
                                    selecionarTimeAleatorio();
                                }
                            } else {
                                selecionarTimeAleatorio();
                            }
                        }
                    } else {
                        adcionarApostaPreencherNumerosAleatorios();
                    }
                } else {
                    adcionarApostaPreencherNumerosAleatorios();
                }
            }
            break;

            case R.id.botaoLimparAposta: {
                if (isTipoLoteca && partidasSelecionadas != null) {
                    limparPartidadasSelecionasLoteca(Boolean.FALSE);
                } else if (isTipoTimemania && isTelaSelecaoTimeAtivado) {
                    limparTimeSelecionado();
                } else if (isTipoLotogol && partidasSelecionadas != null) {
                    limparPartidasSelecionadasLotogol(Boolean.FALSE);
                } else {
                    dezenasSelecionadas.clear();
                    simularDezenasAdapter.notifyDataSetChanged();
                    voltaSalvarApostaParaLayoutInicial();
                }
                limparOpcaoOutrosNumerosLayout();

                verificaSeMostraBotaoLimparAposta();
                atualizaStatusBotaoFinalizar();
            }
            break;

            case R.id.simularApostaLayoutCarrinhoApostas:
                cartelaFragmentListener.abreCarrinhoApostas();
                break;

            case R.id.salvarApostaLayout: {
                RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

                ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                final float scale = parentActivity.getResources().getDisplayMetrics().density;
                relativelayoutParams.height = (int) (140 * scale + 0.5f);

                salvarApostaLayout.setLayoutParams(relativelayoutParams);
                salvarApostaLayout.setOnClickListener(null);

                salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
            }
            break;

            case R.id.btnSalvarAposta: {
                EditText nomeAposta = parentActivity.getEditNomeAposta();

                if (!TextUtils.isEmpty(nomeAposta.getText().toString())) {
                    salvarAposta(nomeAposta.getText().toString());
                } else {
                    ViewUtils.alert(getActivity(), getString(R.string.msg_favor_inserir_nome_aposta));
                }
            }

        }
    }

    private void selecionarTimeAleatorio() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(listaEquipefiltro.size() - 1);
        equipeSelecionada = listaEquipefiltro.get(randomInt);
        equipeSelecionada.setSelecionado(true);
        String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
        informeNomeTime.setText(nomeTime);
        timeCoracaoAdapter.notifyDataSetChanged();
        verificaSeMostraBotaoLimparAposta();
        verificaSeMostraSalvarApostaView();
        atualizarAdicionarCarinhoCompletarCartela();
    }

    private void limparOpcaoOutrosNumerosLayout() {
        if (isTipoLotomania && opcaoOutrosNumerosLayout.getVisibility() == View.VISIBLE) {
            setVisibilityCheckedNumerosLayout(View.GONE);
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        }
    }

    private void setVisibilityCheckedNumerosLayout(int visibility) {
        opcaoOutrosNumerosLayout.setVisibility(visibility);
        selcioneOutrosNumeros.setChecked(Boolean.FALSE);
    }


    private void limparTimeSelecionado() {
        if (equipeSelecionada != null) {
            equipeSelecionada.setSelecionado(false);
            informeNomeTime.setText(StringUtils.EMPTY);
        }

        if (timeCoracaoAdapter != null) {
            timeCoracaoAdapter.notifyDataSetChanged();
        }

        voltaSalvarApostaParaLayoutInicial();
    }

    private void adcionarApostaPreencherNumerosAleatorios() {

        if (isTipoTimemania) {
            adicionarApostaPreencherNumerosTimemania();
        } else if (isTipoLotomania) {
            adicionarApostaPreencherNumerosLotomania();
        } else if (isTipoLoteca) {
            int duplo = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
            int triplo = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

            if (partidasSelecionadas.size() == parametroJogo.getPartidas().size() && (duplo > 0 || triplo > 0)) {
                adicionarApostaNoCarrinho();
            } else {
                //TODO implementar regra de completar placar
            }
        } else if (isTipoLotogol) {
            if (partidasSelecionadas.size() == parametroJogo.getPartidas().size()) {
                adicionarApostaNoCarrinho();
            } else {
                //TODO implementar regra de completar placar
            }
        } else if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
            adicionarApostaNoCarrinho();

        } else {
            preencheNumerosAleatorios();
        }
    }

    private void adicionarApostaPreencherNumerosLotomania() {
        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
            orderAscDezenas();
            //verifica se foi marcada a opção para gerar outros números
            if (selcioneOutrosNumeros.isChecked()) {
                // Gera uma nova aposta com os outros 50 números não selecionados
                selecionarDezenasApostaEspelho();

                List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>(2);
                IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida();
                IdentificaoDeUmaApostaDas8Modalidades apostaEspelho = getApostaPreenchida();

                aposta.setEspelho(true);
                aposta.setValor(aposta.getValor().divide(Constantes.DOIS_BIG_DECIMAL));

                apostaEspelho.setNumerosSelecionados(dezenasApostaEspelho);
                apostaEspelho.setValor(aposta.getValor());

                apostas.add(aposta);
                apostas.add(apostaEspelho);

                adicionarApostasCarrinhoRequest(ApostaBO_.getInstance_(getActivity()), getActivity(), apostas);

            } else {
                adicionarApostaNoCarrinho();
            }

        } else {
            preencheNumerosAleatorios();
            setVisibilityCheckedNumerosLayout(View.VISIBLE);
            onClickOutrosNumerosLayout();

        }
    }

    private void onClickOutrosNumerosLayout() {
        opcaoOutrosNumerosLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selcioneOutrosNumeros.isChecked()) {
                    selcioneOutrosNumeros.setChecked(false);
                } else {
                    selcioneOutrosNumeros.setChecked(true);
                }
                atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            }
        });
    }

    private void adicionarApostaPreencherNumerosTimemania() {
        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado && equipeSelecionada != null) {
            adicionarApostaNoCarrinho();
        } else if (isEscolhaTimeCoracaoSurpresinha) {
            parentActivity.adicionarTimeSurpresa(equipeSelecionada);
        } else {
            preencheNumerosAleatorios();
        }
    }

    private Boolean flagApostaSalva = false;

    private void salvarAposta(String nomeAposta) {
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(getActivity());
        DialogUtils.show(getActivity());

        final ApostaFavoritaDTO aposta = new ApostaFavoritaDTO();

        DTOEnumInteger modalidade = new DTOEnumInteger();
        modalidade.setDescricao(parametroJogo.getConcurso().getModalidadeDetalhada().getDescricao());
        modalidade.setValor(parametroJogo.getConcurso().getModalidadeDetalhada().getValor());
        aposta.setModalidade(modalidade);

        aposta.setNome(nomeAposta);
        aposta.setNumerosSelecionados(dezenasSelecionadas);
        aposta.setId(Constantes.ZERO_LONG);

        if (isTipoTimemania && equipeSelecionada != null) {
            aposta.setTimeDoCoracao(equipeSelecionada);
        }

        dadosCorporativosBO.validarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                dadosCorporativosBO.salvarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        DialogUtils.dismiss();

                        RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

                        salvarApostaLayout.setBackgroundResource(R.color.verdeazul);
                        ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                        final float scale = parentActivity.getResources().getDisplayMetrics().density;
                        relativelayoutParams.height = (int) (64 * scale + 0.5f);

                        salvarApostaLayout.setLayoutParams(relativelayoutParams);

                        TextView tituloSalvarAposta = parentActivity.getTextoSalveEstaAposta();
                        tituloSalvarAposta.setText(R.string.label_aposta_salva_carinha);
                        tituloSalvarAposta.setGravity(Gravity.CENTER);

                        ImageView starSalvarApostaImageView = parentActivity.getIconStarSalvar();
                        starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritado);

                        flagApostaSalva = true;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
                        RedirectNetwork.checkRedirect( error, getActivity() );

                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, getActivity() );
            }
        });
    }

    private void preencheNumerosAleatorios() {
        ApostaBO apostaBO = ApostaBO_.getInstance_(getActivity());
        orderAscDezenas();
        apostaBO.completaJogo(parentActivity.qtdDezenasPossiveisSelecionado, dezenasSelecionadas, 1,
                parametroJogo.getPrognosticoMaximo(), new RequestListener<CompletaJogoRsp>() {
                    @Override
                    public void onResponse(CompletaJogoRsp response) {
                        isEcolhaTimeBtnAtivado = true;
                        dezenasSelecionadas.clear();
                        dezenasSelecionadas.addAll(response.getPayload());
                        simularDezenasAdapter.notifyDataSetChanged();
                        atualizaStatusBotaoFinalizar();
                        verificaSeMostraBotaoLimparAposta();
                        verificaSeMostraSalvarApostaView();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                    }
                });
    }

    private void selecionarDezenasApostaEspelho() {
        for (Dezena dezena : dezenas) {
            if (!dezena.isSelected()) {
                dezenasApostaEspelho.add(Integer.parseInt(dezena.getValue()));
            }
        }
    }

    private void mostrarCartelaEscolhaTimeCoracao() {
        // Estando a aposta completa apresenta a tela de escolha do time
        // Seta a paginação
        infoEscolha.setText(getString(R.string.label_escolha_time_seu_coracao));
        infoPaginacao.setText(ViewUtils.fromHtml(Constantes.PAGINACAO_DOIS_DOIS));

        isEcolhaTimeBtnAtivado = true;
        isTelaSelecaoTimeAtivado = true;
        selecioneTimeCoracaoLayout = view.findViewById(R.id.selecioneTimeCoracao);
        mGridTimeCoracao = view.findViewById(R.id.gridTimes);
        preencherVisibilidaeGridView(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        informeNomeTime = selecioneTimeCoracaoLayout.findViewById(R.id.informe_nome_time);
        onChangeInformeNomeTime();
        configurarSelecaoTimeCoracao();
        atualizaStatusBotaoFinalizar();
        verificaSeMostraBotaoLimparAposta();
        verificaSeMostraSalvarApostaView();

    }

    private void onChangeInformeNomeTime() {
        informeNomeTime.addTextChangedListener(new TextWatcher() {
            private boolean isChanged = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isChanged) {
                    return;
                }

                boolean contemIfem = charSequence.toString().contains("-");
                isChanged = true;

                List<ParametroEquipe> parametroEquipeList = parametroJogo.getEquipes();
                listaEquipefiltro.clear();
                if (StringUtils.isNotEmpty(charSequence.toString()) && !contemIfem) {
                    for (ParametroEquipe parametroEquipe : parametroEquipeList) {
                        if (parametroEquipe.getNome().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                            listaEquipefiltro.add(parametroEquipe);
                        }
                    }
                } else if (contemIfem) {
                    informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                    listaEquipefiltro.addAll(parametroEquipeList);
                } else {
                    listaEquipefiltro.addAll(parametroEquipeList);
                    informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                timeCoracaoAdapter.notifyDataSetChanged();
                isChanged = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void configuraBotoes(View fragmentView) {
        valorAposta = parentActivity.getValorApostaCartela();
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);

        botaoFinalizar = parentActivity.getBotaoAdicionarCompletarCartela();
        botaoFinalizar.setOnClickListener(this);
        botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));

        parentActivity.getSimularApostaLayoutCarrinhoApostas().setOnClickListener(this);

        botaoLimparAposta = parentActivity.getBotaoLimparAposta();
        botaoLimparAposta.setOnClickListener(this);


        if (isTipoLoteca || isTipoLotogol) {
            String textoBotao;
            if (isTipoLoteca) {
                textoBotao = getString(R.string.label_selecionar_times_aleatoriamente);
            } else {
                textoBotao = getString(R.string.label_complete_placar_aleatoriamente);
            }
            botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), textoBotao));
        } else if (!isEscolhaTimeCoracaoSurpresinha) {
            botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(fragmentView.getContext(), getString(R.string.completarCartela)));
            verificaSeMostraBotaoLimparAposta();
        } else {
            botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(fragmentView.getContext(), getString(R.string.completarCartela)));
        }
    }

    public void voltaSalvarApostaParaLayoutInicial() {
        RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

        salvarApostaLayout.setBackgroundResource(R.color.cinzaescuro);
        TextView tituloSalvarAposta = parentActivity.getTextoSalveEstaAposta();

        tituloSalvarAposta.setText(R.string.label_salve_esta_aposta_carinha);

        ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

        final float scale = parentActivity.getResources().getDisplayMetrics().density;
        relativelayoutParams.height = (int) (64 * scale + 0.5f);

        salvarApostaLayout.setLayoutParams(relativelayoutParams);
        salvarApostaLayout.setOnClickListener(null);

        ImageView starSalvarApostaImageView = parentActivity.getIconStarSalvar();
        starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritar);

        salvarApostaLayout.setOnClickListener(this);

        EditText nomeAposta = parentActivity.getEditNomeAposta();
        nomeAposta.setText("");
        salvarApostaLayout.setVisibility(View.GONE);
    }

    private void verificaSeMostraBotaoLimparAposta() {
        if (isTipoLoteca) {
            if (partidasSelecionadas.isEmpty()) {
                botaoLimparAposta.setVisibility(View.GONE);
            } else {
                botaoLimparAposta.setVisibility(View.VISIBLE);
            }
        } else if (isTipoLotogol) {

            int visibility = View.GONE;
            for (ParametroPartida parametroPartida : parametroJogo.getPartidas()) {
                if (StringUtils.isNotEmpty(parametroPartida.getEquipe1().getPlacar()) || StringUtils.isNotEmpty(parametroPartida.getEquipe2().getPlacar())) {
                    visibility = View.VISIBLE;
                    break;
                }
            }
            botaoLimparAposta.setVisibility(visibility);
        } else if (isTipoTimemania) {
            if (isTelaSelecaoTimeAtivado && (equipeSelecionada == null || !equipeSelecionada.isSelecionado())) {
                botaoLimparAposta.setVisibility(View.GONE);
            } else if (!isTelaSelecaoTimeAtivado && dezenasSelecionadas.isEmpty()) {
                botaoLimparAposta.setVisibility(View.GONE);
            } else {
                botaoLimparAposta.setVisibility(View.VISIBLE);
            }
        } else if (dezenasSelecionadas.isEmpty()) {
            botaoLimparAposta.setVisibility(View.GONE);
        } else {
            botaoLimparAposta.setVisibility(View.VISIBLE);
        }
    }

    private void verificaSeMostraSalvarApostaView() {

        if (isTipoLoteca || isTipoLotogol) {
            ocutarSalvarAposta();
        } else if (isTipoTimemania) {
            if (isTelaSelecaoTimeAtivado && (equipeSelecionada != null && equipeSelecionada.isSelecionado())) {
                mostrarSalvarAposta();
            } else {
                ocutarSalvarAposta();
            }
        } else if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
            mostrarSalvarAposta();
        } else {
            ocutarSalvarAposta();
        }
    }

    private void ocutarSalvarAposta() {
        RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

        salvarApostaLayout.setVisibility(View.GONE);
    }

    private void mostrarSalvarAposta() {
        RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

        salvarApostaLayout.setVisibility(View.VISIBLE);
        final float scale = parentActivity.getResources().getDisplayMetrics().density;
        salvarApostaLayout.getLayoutParams().height = (int) (64 * scale + 0.5f);
        salvarApostaLayout.setOnClickListener(this);
    }

    public void atualizaStatusBotaoFinalizar() {

        if (isTipoLoteca) {
            int duplo = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
            int triplo = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

            if (partidasSelecionadas.size() == parametroJogo.getPartidas().size() && (duplo > 0 || triplo > 0)) {
                atualizarBotaoFinalizarAdicionarCarrinho();
            } else {
                botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
                botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_selecionar_times_aleatoriamente)));
            }
        } else if (isTipoLotogol) {
            if (partidasSelecionadas.size() < parametroJogo.getPartidas().size()) {
                botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
                botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_complete_placar_aleatoriamente)));
            } else {
                atualizarBotaoFinalizarAdicionarCarrinho();
            }
        } else if (isTipoTimemania) {
            if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado && isEcolhaTimeBtnAtivado) {
                botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.timemaniaescuro));
                if (mGridTimeCoracao != null) {
                    botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_selecionar_time_aleatoriamente)));
                    botaoFinalizar.setBackgroundColor(getContext().getResources().getColor(R.color.cinza));
                } else {
                    botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_escolha_time_coracao)));
                    botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.timemaniaescuro));
                }
            } else {
                atualizarAdicionarCarinhoCompletarCartela();
            }
        } else {
            atualizarAdicionarCarinhoCompletarCartela();
        }
    }

    private void atualizarAdicionarCarinhoCompletarCartela() {
        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado || isEscolhaTimeCoracaoSurpresinha) {
            atualizarBotaoFinalizarAdicionarCarrinho();
            if (isTipoTimemania) {
                if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
                    botaoFinalizar.setClickable(true);
                    botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.timemaniaescuro));
                } else {
                    botaoFinalizar.setClickable(false);
                    botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.azulclaro));
                    parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
                }
            }

        } else if (dezenasSelecionadas.size() > parentActivity.qtdDezenasPossiveisSelecionado) {
            dezenasSelecionadas.clear();
            simularDezenasAdapter.notifyDataSetChanged();
            atualizarBotaoFinalizarCompletarCartela();

            botaoLimparAposta.setVisibility(View.GONE);
        } else {
            atualizarBotaoFinalizarCompletarCartela();
        }
    }

    private void atualizarBotaoFinalizarCompletarCartela() {
        botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
        botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.completarCartela)));
        parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
    }

    private void atualizarBotaoFinalizarAdicionarCarrinho() {
        botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.adicionarAoCarrinho)));
        if (!isTipoLoteca && !isTipoLotogol) {
            parentActivity.getSalvarApostaLayout().setVisibility(View.VISIBLE);
        }
    }

    private void configuraListaDezenas(View fragmentView) {

        listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        recyclerViewPartida = fragmentView.findViewById(R.id.recyclerViewPartida);

        if (dezenas != null && dezenas.size() > 0) {

            listaDezenas.setExpanded(true);
            simularDezenasAdapter = new SimularDezenasAdapter(getActivity(), dezenas);
            listaDezenas.setAdapter(simularDezenasAdapter);
            listaDezenas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Dezena dezena = dezenas.get(position);
                    Integer valorSelecionado = Integer.valueOf(dezena.getValue());

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
                                ViewUtils.alert(getActivity(), getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
                            }
                        } else {
                            dezenasSelecionadas.add(valorSelecionado);
                        }
                    }

                    if (isTipoTimemania) {
                        isEcolhaTimeBtnAtivado = true;
                    }

                    if (isTipoLotomania) {
                        if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
                            setVisibilityCheckedNumerosLayout(View.VISIBLE);
                        } else {
                            setVisibilityCheckedNumerosLayout(View.GONE);
                        }
                    }


                    verificaSeMostraSalvarApostaView();
                    verificaSeMostraBotaoLimparAposta();
                    atualizaStatusBotaoFinalizar();
                    simularDezenasAdapter.notifyDataSetChanged();
                }
            });
        } else {
            if (isTipoLoteca) {
                configurarTimesLoteca();
                partidasSelecionadas = new HashSet<>();
                fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
            } else if (isTipoLotogol) {
                configurarTimesLotogol();
                partidasSelecionadas = new HashSet<>();
                fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
                fragmentView.findViewById(R.id.layoutTitleLoteca).setPadding(20, 80, 0, 0);
                ((TextView) fragmentView.findViewById(R.id.titleContinue)).setText(R.string.label_quantidade_gols_cada_time);

            }

        }

    }

    private void configurarTimesLoteca() {
        LotecaAdapter lotecaAdapter = new LotecaAdapter(parametroJogo.getPartidas(), this);
        recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPartida.setAdapter(lotecaAdapter);
        recyclerViewPartida.setExpanded(true);
    }

    private void configurarTimesLotogol() {
        LotogolAdapter lotogolAdapter = new LotogolAdapter(parametroJogo.getPartidas(), this);
        recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPartida.setAdapter(lotogolAdapter);
        recyclerViewPartida.setExpanded(true);
    }

    /**
     * Metodo configura a cartela de times do coracao
     */
    private void configurarSelecaoTimeCoracao() {
        listaEquipefiltro = new ArrayList<>(parametroJogo.getEquipes());
        timeCoracaoAdapter = new TimeCoracaoAdapter(getActivity(), listaEquipefiltro);
        mGridTimeCoracao.setAdapter(timeCoracaoAdapter);
        mGridTimeCoracao.setExpanded(true);
        mGridTimeCoracao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewItem, int i, long l) {
                if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
                    equipeSelecionada.setSelecionado(false);
                }
                equipeSelecionada = listaEquipefiltro.get(i);
                equipeSelecionada.setSelecionado(true);
                String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
                informeNomeTime.setText(nomeTime);
                timeCoracaoAdapter.notifyDataSetChanged();
                verificaSeMostraBotaoLimparAposta();
                verificaSeMostraSalvarApostaView();
                atualizarAdicionarCarinhoCompletarCartela();
            }
        });
    }

    @Override
    public void clickEquipeLoteca(ParametroPartida parametroPartida) {
        if (parametroPartida.isSelecionado()) {
            if (parametroPartida.getQtdItensSelecionados() == 1) {
                parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) + 1));
                if (parametroPartida.getQtdItensSelecionadosAnterior() == 2) {
                    parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
                }
            } else if (parametroPartida.getQtdItensSelecionados() == 2) {
                parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) + 1));
                if (parametroPartida.getQtdItensSelecionadosAnterior() == 3) {
                    parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) - 1));
                } else {
                    parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
                }
            } else if (parametroPartida.getQtdItensSelecionados() == 3) {
                parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
                parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) + 1));
            }
            partidasSelecionadas.add(parametroPartida);
        } else {
            parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
            partidasSelecionadas.remove(parametroPartida);
        }
        atualizaValorApostaLoteca();
        atualizaStatusBotaoFinalizar();
        verificaSeMostraBotaoLimparAposta();
    }

    private void limparPartidadasSelecionasLoteca(boolean desativado) {
        for (ParametroPartida partida : partidasSelecionadas) {
            partida.setEmpate(desativado);
            partida.getEquipe1().setSelecionado(desativado);
            partida.getEquipe2().setSelecionado(desativado);
            partida.setSelecionado(desativado);
            partida.setQtdItensSelecionadosAnterior(0);
            partida.setQtdItensSelecionados(0);
            parentActivity.zerarApostasLoteca();
        }
        partidasSelecionadas.clear();
        recyclerViewPartida.setExpanded(true);
        recyclerViewPartida.getAdapter().notifyDataSetChanged();
        verificaSeMostraSalvarApostaView();
        atualizaValorApostaLoteca();
    }

    private void limparPartidasSelecionadasLotogol(boolean desativado) {
        for (ParametroPartida partida : parametroJogo.getPartidas()) {
            partida.setSelecionado(desativado);
            partida.getEquipe1().setPlacar(StringUtils.EMPTY);
            partida.getEquipe2().setPlacar(StringUtils.EMPTY);

        }
        partidasSelecionadas.clear();
        recyclerViewPartida.setExpanded(true);
        recyclerViewPartida.setAdapter(new LotogolAdapter(parametroJogo.getPartidas(), this));
        verificaSeMostraSalvarApostaView();
    }

    @Override
    public void partidaSelecionada() {

    }

    @Override
    public void atualizarParametrosLotogol(ParametroPartida parametroPartida) {
        if (parametroPartida.isSelecionado()) {
            partidasSelecionadas.add(parametroPartida);
        } else {
            partidasSelecionadas.remove(parametroPartida);
        }
        verificaSeMostraBotaoLimparAposta();
        atualizaStatusBotaoFinalizar();
        verificaSeMostraSalvarApostaView();
    }

    public void setEscolhaTimeCoracaoSurpresinha(boolean escolhaTimeCoracaoSurpresinha) {
        isEscolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
    }

    private void orderAscDezenas() {
        Integer menorDezenaAtual = null;
        List<Integer> listaOrdenadaAsc = new ArrayList<>();
        while (dezenasSelecionadas.size() > 0) {
            for (Integer dezena : dezenasSelecionadas) {
                if (menorDezenaAtual == null) {
                    menorDezenaAtual = dezena;
                } else if (menorDezenaAtual > dezena) {
                    menorDezenaAtual = dezena;
                }
            }
            listaOrdenadaAsc.add(menorDezenaAtual);
            dezenasSelecionadas.remove(menorDezenaAtual);
            menorDezenaAtual = null;
        }
        dezenasSelecionadas = listaOrdenadaAsc;
    }

    private void setApostaLoteca(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        if (isTipoLoteca) {
            aposta.setPartidasLoteca(new ArrayList<PartidaLotecaDTO>());
            for (ParametroPartida partida : partidasSelecionadas) {
                PartidaLotecaDTO lotecaDTO = new PartidaLotecaDTO();
                EquipeDTO equipeDTO1 = new EquipeDTO();
                EquipeDTO equipeDTO2 = new EquipeDTO();

                this.setValorEquipe1DTOEquipe2DTO(partida, equipeDTO1, equipeDTO2);

                lotecaDTO.setEquipe1(equipeDTO1);
                lotecaDTO.setEquipe2(equipeDTO2);
                lotecaDTO.setEmpate(partida.isEmpate());

                aposta.getPartidasLoteca().add(lotecaDTO);
            }
        }
    }

    private void setApostaLotogol(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        if (isTipoLotogol) {
            aposta.setPartidasLotogol(new ArrayList<PartidaLotogolDTO>());
            for (ParametroPartida partida : partidasSelecionadas) {
                PartidaLotogolDTO lotogolDTO = new PartidaLotogolDTO();
                EquipeDTO equipeDTO1 = new EquipeDTO();
                EquipeDTO equipeDTO2 = new EquipeDTO();

                this.setValorEquipe1DTOEquipe2DTO(partida, equipeDTO1, equipeDTO2);

                lotogolDTO.setEquipe1(equipeDTO1);
                lotogolDTO.setEquipe2(equipeDTO2);

                aposta.getPartidasLotogol().add(lotogolDTO);
            }
        }
    }

    private void setValorEquipe1DTOEquipe2DTO(ParametroPartida partida, EquipeDTO equipeDTO1, EquipeDTO equipeDTO2) {
        equipeDTO1.setPlacar(partida.getEquipe1().getPlacar());
        equipeDTO1.setVitoria(partida.getEquipe1().isSelecionado());
        equipeDTO1.setParametroEquipe(partida.getEquipe1());
        equipeDTO1.setIndicadorSelecao(partida.getEquipe1().getIndicadorSelecao());
        equipeDTO1.setDescricaoCurta(partida.getEquipe1().getDescricaoCurta());
        equipeDTO1.setDescricaoLonga(partida.getEquipe1().getDescricaoLonga());
        equipeDTO1.setNumero(partida.getEquipe1().getNumero());
        equipeDTO1.setNumeroPais(partida.getEquipe1().getNumeroPais());
        equipeDTO1.setPais(partida.getEquipe1().getPais());
        equipeDTO1.setSiglaPais(partida.getEquipe1().getSiglaPais());
        equipeDTO1.setUf(partida.getEquipe1().getUf());
        equipeDTO1.setNomeClass(partida.getEquipe1().getNomeClass());
        equipeDTO1.setNome(partida.getEquipe1().getNome());

        equipeDTO2.setPlacar(partida.getEquipe2().getPlacar());
        equipeDTO2.setVitoria(partida.getEquipe2().isSelecionado());
        equipeDTO2.setParametroEquipe(partida.getEquipe2());
        equipeDTO2.setIndicadorSelecao(partida.getEquipe2().getIndicadorSelecao());
        equipeDTO2.setDescricaoCurta(partida.getEquipe2().getDescricaoCurta());
        equipeDTO2.setDescricaoLonga(partida.getEquipe2().getDescricaoLonga());
        equipeDTO2.setNumero(partida.getEquipe2().getNumero());
        equipeDTO2.setNumeroPais(partida.getEquipe2().getNumeroPais());
        equipeDTO2.setPais(partida.getEquipe2().getPais());
        equipeDTO2.setSiglaPais(partida.getEquipe2().getSiglaPais());
        equipeDTO2.setUf(partida.getEquipe2().getUf());
        equipeDTO2.setNomeClass(partida.getEquipe2().getNomeClass());
        equipeDTO2.setNome(partida.getEquipe2().getNome());
    }

    private void atualizaValorApostaLoteca() {
        BigDecimal valorEmReais = BigDecimal.ZERO;

        Integer duplos = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
        Integer triplos = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

        if (duplos > 0 || triplos > 0) {
            for (ParametroValorApostaDTO parametroValorApostaDTO : parametroJogo.getValoresAposta()) {
                if (parametroValorApostaDTO.getQuantidadeDuplos().equals(duplos) && parametroValorApostaDTO.getQuantidadeTriplos().equals(triplos)) {
                    valorEmReais = parametroValorApostaDTO.getValor();
                    break;
                }
            }

        } else {
            valorEmReais = parametroJogo.getValorApostaMinima();
        }

        ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
        valorTotalAposta = valorEmReais;
    }

    private void editeAposta() {
        if (parentActivity.getAposta() != null) {
            IdentificaoDeUmaApostaDas8Modalidades aposta = parentActivity.getAposta();
            if (CollectionUtils.isNotEmpty(aposta.getNumerosSelecionados())) {
                dezenasSelecionadas = aposta.getNumerosSelecionados();
                parentActivity.qtdDezenasPossiveisSelecionado = aposta.getQuantidadeNumeros();
                parentActivity.qtdConcursoSelecionado = aposta.getQuantidadeTeimosinhas();
                parentActivity.atualizaTextoBotoesQtdSelecionadas();
                equipeSelecionada = aposta.getTimeDoCoracao();
                if (equipeSelecionada != null) {
                    isEcolhaTimeBtnAtivado = Boolean.TRUE;
                }
                atualizaStatusBotaoFinalizar();
            }
            ViewUtils.setMoedaFormatHtml(aposta.getValor(), valorAposta);
        }
    }

    private void timemaniaTimeSelecionadoEdicao() {
        if (equipeSelecionada != null) {
            equipeSelecionada.setSelecionado(Boolean.TRUE);
            String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
            informeNomeTime.setText(nomeTime);

            for (ParametroEquipe equipe : listaEquipefiltro) {
                if (equipe.getNumero().equals(equipeSelecionada.getNumero())) {
                    equipe.setSelecionado(equipeSelecionada.isSelecionado());
                    equipeSelecionada = equipe;
                    break;
                }
            }
            timeCoracaoAdapter.notifyDataSetChanged();
            verificaSeMostraBotaoLimparAposta();
            verificaSeMostraSalvarApostaView();
            atualizarAdicionarCarinhoCompletarCartela();
        }
    }

    // Adapter do gridview das dezenas
    private class SimularDezenasAdapter extends BaseAdapter {
        private Context context;
        private List<Dezena> rows;

        SimularDezenasAdapter(Context context, List<Dezena> activitData) {
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
            int sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, parentActivity.getResources().getDisplayMetrics());

            if (view == null) {
                textView = new TextView(this.context);

                textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
            } else {
                textView = (TextView) view;
            }

            if (dezenasSelecionadas.contains(Integer.valueOf(rows.get(i).getValue()))) {
                shape.setColor(ContextCompat.getColor(context, typeGameColorLight));
                textView.setTextColor(Color.WHITE);
                rows.get(i).setSelected(Boolean.TRUE);
            } else {
                shape.setColor(Color.WHITE);
                textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));
                rows.get(i).setSelected(Boolean.FALSE);
            }

            shape.setCornerRadius(sizeItens / 2);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            if (dezenas.get(i).getValue().length() == 1) {
                textView.setText(String.format("0%s", dezenas.get(i).getValue()));
            } else {

                textView.setText(dezenas.get(i).getValue());
            }
            textView.setTypeface(ViewUtils.getFontFuturaBold(context));
            textView.setGravity(Gravity.CENTER);


            return textView;
        }
    }
}