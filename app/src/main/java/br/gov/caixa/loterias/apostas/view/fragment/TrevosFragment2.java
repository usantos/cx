package br.gov.caixa.loterias.apostas.view.fragment;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.TrevoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.OnTrevosListener2;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public class TrevosFragment2 extends Fragment implements OnItemClickListener<DezenaHolder> {
    private static final String ARG_APOSTA = "ARG_APOSTA";
    private static final String ARG_PARAMENTRO = "ARG_PARAMENTRO";
    private static final String ARG_SELECIONADOS = "ARG_SELECIONADOS";
    private boolean isAvisou = false;
    private static final int QUANTIDADE_COLUNA_LISTA = 3;
    private RecyclerView listaTrevos;
    private ParametroJogoDTO parametro;
    private ArrayList<Integer> dezenasSelecionadas;
    private IdentificaoDeUmaApostaDas8Modalidades aposta;
    private OnTrevosListener2 listener;
    private List<ParametroValorApostaDTO> valoresApostas;
    private String textoBotaoTrevosSelecionado;
    private ParametroValorApostaDTO valorApostaSelecionado;
    private List<Dezena> dezenaList;
    private List<Integer> trevoSelecionadoList = new ArrayList<>();
    private ListaDezenaRecyclerView adapter;
    private View view;

    private TrevoModel model;
    private SimulaViewModel viewModel;

    private TrevosFragment2() {
    }

    public static TrevosFragment2 newInstance(IdentificaoDeUmaApostaDas8Modalidades aposta,
                                              ParametroJogoDTO parametro,
                                              ArrayList<Integer> dezenasSelecionadas) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_APOSTA, aposta);
        args.putSerializable(ARG_PARAMENTRO, parametro);
        args.putString(ARG_SELECIONADOS, String.valueOf(dezenasSelecionadas));

        TrevosFragment2 frag = new TrevosFragment2();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnTrevosListener2) context;
        } catch (Exception e) {
            Log.e("TREVO", "A classe precisa implementar OnTrevoListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            aposta = (IdentificaoDeUmaApostaDas8Modalidades) getArguments().getSerializable(ARG_APOSTA);
            parametro = (ParametroJogoDTO) getArguments().getSerializable(ARG_PARAMENTRO);
            String json = getArguments().getString(ARG_SELECIONADOS);
            if (json != null && !json.isEmpty()) {
                dezenasSelecionadas = new Gson().fromJson(json, new TypeToken<List<Integer>>() {
                }.getType());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trevos, container, false);
        listaTrevos = view.findViewById(R.id.listaTrevos);
        init();

        return view;
    }

    private void init() {
        viewModel = new ViewModelProvider(
                requireActivity()
        ).get(SimulaViewModel.class);

        textoBotaoTrevosSelecionado = parametro.getTrevos().getQtdMinima() + getString(R.string.espaco_em_branco) + getString(R.string.trevos);

        configButtons();

        valoresApostas = parametro.getValoresTrevoByNumero(dezenasSelecionadas.size());
        valorApostaSelecionado = valoresApostas.get(0);
        viewModel.onEvent(
                new SimulaUiEvent.QuantidadeTrevosAlterada(
                        valorApostaSelecionado
                )
        );
        DezenaConfig dezenaConfig = new DezenaConfig(false, R.color.milionaria_escuro_mkp,
                R.layout.item_dezena_trevo,
                new ShapeConfig(R.drawable.ic_item_trevo,
                        R.drawable.ic_item_trevo_selecionado,
                        R.color.milionaria_escuro_mkp, R.color.milionaria_escuro_mkp),
                true);
        adapter = new ListaDezenaRecyclerView(getListaDezenas(), trevoSelecionadoList, dezenaConfig, this);
        listaTrevos.setAdapter(adapter);
        RecyclerView.LayoutManager layot = new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA);
        listaTrevos.setLayoutManager(layot);

        model = new TrevoModel(getActivity());
        editAposta();
    }

    private void editAposta() {
        if (aposta != null) {
            trevoSelecionadoList = AppUtils.converteListaInteiros(aposta.getTrevosSelecionados());
            adapter.atualizaSelecionados(trevoSelecionadoList);
            viewModel.onEvent(
                    new SimulaUiEvent.TrevosAlterados(
                            trevoSelecionadoList.size(),
                            valorApostaSelecionado.getNumeroTrevos(),
                            valorApostaSelecionado
                    )
            );
        }
    }

    private List<Dezena> getListaDezenas() {
        if (dezenaList == null || dezenaList.size() != parametro.getTrevos().getQtdPrognostico()) {
            dezenaList = new ArrayList<>();
            for (int numero = 1; numero <= parametro.getTrevos().getQtdPrognostico(); numero++) {
                Dezena dezena = new Dezena(String.valueOf(numero), Boolean.FALSE, String.valueOf(numero));
                dezenaList.add(dezena);
            }
        }
        return dezenaList;
    }

    private void configButtons() {
        listener.getButtonQtdTrevos().setOnClickListener(
                onQtdTrevosClickListener()
        );
    }

    private View.OnClickListener onSalverApostaClickListener() {
        return v -> {
            if (!listener.nomeApostaFavorita().isEmpty()) {

                validarApostaFav(getApostaFavoritaDTO());

            } else {
                DialogUtils.dialogEntendi(
                        getActivity(),
                        getString(R.string.msg_favor_inserir_nome_aposta)
                );
            }
        };
    }

    private void validarApostaFav(ApostaFavoritaDTO apostaFavorita) {
        ServicoFactoryUtil.getDadosCorporativoService().validarApostaFavorita(apostaFavorita, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                salvarAposta(apostaFavorita);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                if (isErroNegocial(error)) {
                    DialogUtils.dialogSim(
                            getActivity(),
                            MensagensNetwork.getErrorMessage(error),
                            new OnDialogBotaoListener() {
                                @Override
                                public void onButtonClick(DialogInterface dialog, int which) {
                                    salvarAposta(apostaFavorita);
                                }
                            }
                    );
                } else {
                    RedirectNetwork.checkRedirect(error, getActivity());
                }

            }
        });
    }

    public void onAdicionarRodapeClicado() {
        if (!isApostaTrevosCompleta()) {
            return;
        }

        addApostaCarrinho();

        if (viewModel != null) {
            viewModel.onEvent(
                    new SimulaUiEvent.CarregarCarrinho()
            );
        }
    }

    public void onCompletarRodapeClicado() {
        completarTrevosAleatoriamente();
    }

    public void onLimparRodapeClicado() {
        limparTrevosSelecionados();
    }

    private boolean isApostaTrevosCompleta() {
        if (valorApostaSelecionado == null
                || valorApostaSelecionado.getNumeroTrevos() == null) {
            return false;
        }

        return trevoSelecionadoList != null
                && trevoSelecionadoList.size()
                >= valorApostaSelecionado.getNumeroTrevos();
    }

    private void limparTrevosSelecionados() {
        if (trevoSelecionadoList != null) {
            trevoSelecionadoList.clear();
        }

        if (adapter != null) {
            adapter.atualizaSelecionados(
                    trevoSelecionadoList
            );
        }

        if (viewModel != null && valorApostaSelecionado != null) {
            viewModel.onEvent(
                    new SimulaUiEvent.TrevosAlterados(
                            0,
                            valorApostaSelecionado.getNumeroTrevos(),
                            valorApostaSelecionado
                    )
            );
        }
    }

    private void salvarAposta(ApostaFavoritaDTO aposta) {
        if (!AlertDialogUtils.isShow()) {
            AlertDialogUtils.show(getContext());
        }
        ServicoFactoryUtil.getDadosCorporativoService().salvarApostaFavorita(aposta, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_aposta_favorita_sucesso));

                AlertDialogUtils.dismiss();

                listener.atualizaLayoutSalvarAposta();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, getActivity());

            }
        });
    }

    private ApostaFavoritaDTO getApostaFavoritaDTO() {
        ApostaFavoritaDTO apostaFavorita = new ApostaFavoritaDTO();

        DTOEnumInteger modalidade = new DTOEnumInteger();
        modalidade.setDescricao(parametro.getConcurso().getModalidadeDetalhada().getDescricao());
        modalidade.setValor(parametro.getConcurso().getModalidadeDetalhada().getValor());
        apostaFavorita.setModalidade(modalidade);

        String nomeAposta = listener.nomeApostaFavorita();

        if (nomeAposta != null) {
            if (nomeAposta.length() > 25) {
                apostaFavorita.setNome(nomeAposta.substring(0, 25));
            } else {
                apostaFavorita.setNome(nomeAposta);
            }
        } else {
            apostaFavorita.setNome("");
        }
        if (dezenasSelecionadas == null || dezenasSelecionadas.isEmpty()) {
            dezenasSelecionadas = new ArrayList<>();
            for (Dezena d : dezenaList) {
                dezenasSelecionadas.add(Integer.valueOf(d.getValue()));
            }
            apostaFavorita.setNumerosSelecionados(dezenasSelecionadas);
        } else {
            apostaFavorita.setNumerosSelecionados(dezenasSelecionadas);
        }


        apostaFavorita.setId(Constantes.ZERO_LONG);

        apostaFavorita.setParametroTrevo(new ParametroTrevo());
        apostaFavorita.getParametroTrevo().setTrevosSelecionados(trevoSelecionadoList);


        return apostaFavorita;

    }

    private View.OnClickListener onLimparClickListener() {
        return v -> {
            trevoSelecionadoList.clear();

            adapter.atualizaSelecionados(
                    trevoSelecionadoList
            );

            viewModel.onEvent(
                    new SimulaUiEvent.TrevosAlterados(
                            0,
                            valorApostaSelecionado.getNumeroTrevos(),
                            valorApostaSelecionado
                    )
            );

            adapter.atualizaSelecionados(trevoSelecionadoList);
        };
    }

    private View.OnClickListener onFinalizarClickListener() {
        return v -> {
            if (listener.getButtonFinalizar().getText().toString().contains("Completar")) {
                completarTrevosAleatoriamente();
            } else {
                addApostaCarrinho();
            }
        };
    }

    private void addApostaCarrinho() {
        BigDecimal valorAposta = BigDecimal.ZERO;
        if (listener.getQtdConcursos() > 0) {
            valorAposta = valorApostaSelecionado.getValor().multiply(BigDecimal.valueOf(listener.getQtdConcursos()).movePointLeft(0));
        } else {
            valorAposta = valorApostaSelecionado.getValor();
        }
        List<Integer> numerosSelecionadosOrdenados = ListaUtils.orderAscDezenas((List<Integer>) dezenasSelecionadas);

        trevoSelecionadoList = ListaUtils.orderAscDezenas(trevoSelecionadoList);


        IdentificaoDeUmaApostaDas8Modalidades apostaMaisMilionaria = ApostaUtils.getApostaMaisMilionaria(parametro, numerosSelecionadosOrdenados,
                valorAposta,
                listener.getQtdConcursos(), trevoSelecionadoList);
        model.adidionaApostaNoCarrinho(apostaMaisMilionaria);
    }

    private void completarTrevosAleatoriamente() {
        AlertDialogUtils.show(getContext());
        model.preencheNumerosAleatorios(valorApostaSelecionado.getNumeroTrevos(), trevoSelecionadoList,
                parametro.getTrevos().getQtdMaxima(), new OnSilceListener<List<Integer>>() {
                    @Override
                    public void success(List<Integer> list) {
                        AlertDialogUtils.dismiss();
                        trevoSelecionadoList.clear();
                        trevoSelecionadoList.addAll(list);
                        adapter.atualizaSelecionados(list);
                        viewModel.onEvent(
                                new SimulaUiEvent.TrevosAlterados(
                                        trevoSelecionadoList.size(),
                                        valorApostaSelecionado.getNumeroTrevos(),
                                        valorApostaSelecionado
                                )
                        );
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });
    }

    @NotNull
    private View.OnClickListener onQtdTrevosClickListener() {
        return v -> {
            String subtitulo = getStringFormatada(R.string.para_aposta_com_x_numeros, "x", String.valueOf(dezenasSelecionadas.size()));

            valoresApostas = parametro.getValoresTrevoByNumero(dezenasSelecionadas.size());

            List<String> list = getListStringvaloresTrevos(valoresApostas);

            DialogUtils.showDialogListItens(
                    getContext(),
                    getString(R.string.quant_de_trevos),
                    subtitulo,
                    list,
                    "Confirmar",
                    "Cancelar",
                    onDialogListener()
            );

        };
    }

    private OnDialogListener onDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                valorApostaSelecionado = valoresApostas.get(position);
                textoBotaoTrevosSelecionado = valorApostaSelecionado.getNumeroTrevos() + getString(R.string.espaco_em_branco) + getString(R.string.trevos);
                if ((position) > 0 && (!isAvisou)) {
                    DialogUtils.dialogEntendi(
                            getActivity(),
                            getString(R.string.msg_qtd_max_ultrapassada_milionaria)
                    );

                    isAvisou = true;
                }
            }

            @Override
            public void ok(int position) {
                viewModel.onEvent(
                        new SimulaUiEvent.QuantidadeTrevosAlterada(
                                valorApostaSelecionado
                        )
                );
                int ultimoValor = valoresApostas.size() - 1;
                if (valoresApostas.get(ultimoValor).equals(valorApostaSelecionado)) {
                    trevoSelecionadoList = new ArrayList<>();
                    for (int numero = 1; numero <= parametro.getTrevos().getQtdPrognostico(); numero++) {
                        trevoSelecionadoList.add(numero);
                    }
                    adapter.atualizaSelecionados(trevoSelecionadoList);
                    listaTrevos.setAdapter(adapter);
                }
//                listener.atualizaValorAposta(valorApostaSelecionado);
                viewModel.onEvent(
                        new SimulaUiEvent.ValorApostaAlterado(
                                valorApostaSelecionado.getValor()
                        )
                );
                if (trevoSelecionadoList.size() > valorApostaSelecionado.getNumeroTrevos()) {
//                    listener.getButtonFinalizar().setText(ViewUtils.textCaixaSTDBold(getActivity(), getString(R.string.completar_trevos_aleatoriamente)));
//                    listener.getButtonFinalizar().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cinzaescuro));
                    trevoSelecionadoList = new ArrayList<>();
                    adapter.atualizaSelecionados(trevoSelecionadoList);
                    listaTrevos.setAdapter(adapter);
                }
            }

            @Override
            public void cancelar() {

            }
        };
    }

    private List<String> getListStringvaloresTrevos(List<ParametroValorApostaDTO> listaValores) {
        List<String> valores = new ArrayList<>();
        for (ParametroValorApostaDTO valorAposta : listaValores) {
            valores.add(valorAposta.getNumeroTrevos() + " " + getString(R.string.trevos_por)
                    + " " + ViewUtils.getMoedaFormat(valorAposta.getValor()));
        }

        return valores;
    }

    private String getStringFormatada(int idString, String target, String replace) {
        String s = getString(idString).replace(target, replace);
        SpannableStringBuilder string = ViewUtils.textCaixaSTDBold(getActivity(), s);

        return string.toString();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void itemClick(DezenaHolder holder, int position) {
        Dezena dezena = dezenaList.get(position);
        dezena.setSelected(!dezena.isSelected());
        if (dezena.isSelected()) {
            Integer numero = Integer.valueOf(dezena.getValue());
            if (!trevoSelecionadoList.contains(numero)) {
                trevoSelecionadoList.add(numero);
                if ((trevoSelecionadoList.size() > 2) && (!isAvisou)) {
                    DialogUtils.dialogEntendi(
                            getActivity(),
                            getString(R.string.msg_qtd_max_ultrapassada_milionaria)
                    );
                    isAvisou = true;
                }
            }
        } else {
            Integer numero = Integer.valueOf(dezena.getValue());
            if (trevoSelecionadoList.contains(numero)) {
                trevoSelecionadoList.remove(numero);
            }
        }
        holder.atualizaDezena(dezena);
//        checaQuantidadeTrevos();
//        atualizaStatusBotao();
        viewModel.onEvent(
                new SimulaUiEvent.TrevosAlterados(
                        trevoSelecionadoList.size(),
                        valorApostaSelecionado.getNumeroTrevos(),
                        valorApostaSelecionado
                )
        );
    }


    public ParametroValorApostaDTO getParametroValorApostaSelecionado() {
        return valorApostaSelecionado;
    }
}
