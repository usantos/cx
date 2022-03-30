package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.TrevoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.OnTrevosListener;

@EFragment(R.layout.fragment_trevos)
public class TrevosFragment extends Fragment implements OnItemClickListener<DezenaHolder> {

    private static final int QUANTIDADE_COLUNA_LISTA = 3;
    @ViewById
    protected RecyclerView listaTrevos;

    @FragmentArg
    protected ParametroJogoDTO parametro;
    @FragmentArg
    protected ArrayList<Integer> dezenasSelecionadas;

    private OnTrevosListener listener;
    private List<ParametroValorApostaDTO> valoresApostas;
    private String textoBotaoTrevosSelecionado;
    private ParametroValorApostaDTO valorApostaSelecionado;
    private List<Dezena> dezenaList;
    private List<Integer> trevoSelecionadoList = new ArrayList<>();
    private ListaDezenaRecyclerView adappter;

    private TrevoModel model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnTrevosListener) context;
        }catch (Exception e){
            Log.e("TREVO", "A classe precisa implementar OnTrevoListener");
        }
    }

    @AfterViews
    protected void init() {
        textoBotaoTrevosSelecionado = parametro.getTrevos().getQtdMinima() + getString(R.string.espaco_em_branco) +getString(R.string.trevos);
        listener.getButtonQtdTrevos().setText(textoBotaoTrevosSelecionado);

        configIncialButtons();

        valoresApostas = parametro.getValoresTrevoByNumero(dezenasSelecionadas.size());
        valorApostaSelecionado = valoresApostas.get(0);

        DezenaConfig dezenaConfig = new DezenaConfig(false, R.color.mais_milionaria_escura,
                                                     R.drawable.ic_item_trevo, R.drawable.ic_item_trevo_selecionado, true);
        adappter = new ListaDezenaRecyclerView(getListaDezenas(), trevoSelecionadoList, dezenaConfig, this);
        listaTrevos.setAdapter(adappter);
        RecyclerView.LayoutManager layot = new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA);
        listaTrevos.setLayoutManager(layot);

        model = new TrevoModel(getActivity());
    }

    private List<Dezena> getListaDezenas() {
        if (dezenaList == null || dezenaList.size() != parametro.getTrevos().getQtdPrognostico()){
            dezenaList = new ArrayList<>();
            for (int numero = 1; numero <= parametro.getTrevos().getQtdPrognostico(); numero++){
                Dezena dezena = new Dezena();
                dezena.setValue(String.valueOf(numero));
                dezena.setSelected(Boolean.FALSE);
                dezenaList.add(dezena);
            }
        }
        return dezenaList;
    }

    private void configIncialButtons() {
        listener.getButtonQtdTrevos().setOnClickListener(onQtdTrevosClickListener());

        SpannableStringBuilder stringBotao = ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.completar_trevos_aleatoriamente));
        listener.getButtonFinalizar().setText(stringBotao);
        listener.getButtonFinalizar().setBackgroundColor(getResources().getColor(R.color.cinzaescuro));
        listener.getButtonFinalizar().setOnClickListener(onFinalizarClickListener());

        listener.getButtonLimpar().setOnClickListener(onLimparClickListener());
        listener.getButtonLimpar().setVisibility(View.GONE);
    }

    private View.OnClickListener onLimparClickListener() {
        return v -> {
            trevoSelecionadoList.clear();
            adappter.atualizaSelecionados(trevoSelecionadoList);
            listener.getButtonLimpar().setVisibility(View.INVISIBLE);
            atualizaStatusBotao();
        };
    }

    private View.OnClickListener onFinalizarClickListener() {
        return v -> {
            if (listener.getButtonFinalizar().getText().toString().contains("Completar")){
                completarTrevosAleatoriamente();
            }else {
                addApostaCarrinho();
            }
        };
    }

    private void addApostaCarrinho() {
        BigDecimal valorAposta = BigDecimal.ZERO;
        if (listener.getQtdConcursos() > 0){
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
                        adappter.atualizaSelecionados(list);
                        atualizaStatusBotao();
                        checaMostraBotaoLimpar();
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
            String subtitulo = getStringFormatada(R.string.para_aposta_com_x_numeros, "x",String.valueOf(dezenasSelecionadas.size()));

            valoresApostas = parametro.getValoresTrevoByNumero(dezenasSelecionadas.size());

            List<String>                  list = getListStringvaloresTrevos(valoresApostas);

            DialogUtils.showDialogListaEscolha(getContext(), getString(R.string.quant_de_trevos), subtitulo,
                                               list, onDialogListener());

        };
    }

    private OnDialogListener onDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                valorApostaSelecionado = valoresApostas.get(position);
                textoBotaoTrevosSelecionado = valorApostaSelecionado.getNumeroTrevos() + getString(R.string.espaco_em_branco) +getString(R.string.trevos);
            }

            @Override
            public void ok() {
                listener.getButtonQtdTrevos().setText(textoBotaoTrevosSelecionado);
                int ultimoValor = valoresApostas.size() - 1;
                if (valoresApostas.get(ultimoValor).equals(valorApostaSelecionado)){
                    trevoSelecionadoList = new ArrayList<>();
                    for (int numero = 1; numero <= parametro.getTrevos().getQtdPrognostico(); numero++){
                        trevoSelecionadoList.add(numero);
                    }
                    adappter.atualizaSelecionados(trevoSelecionadoList);
                    listaTrevos.setAdapter(adappter);
                }
                listener.atualizaValorAposta(valorApostaSelecionado);
                atualizaStatusBotao();
                checaMostraBotaoLimpar();
                if (trevoSelecionadoList.size() > valorApostaSelecionado.getNumeroTrevos()){
                    listener.getButtonFinalizar().setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.completar_trevos_aleatoriamente)));
                    listener.getButtonFinalizar().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cinzaescuro));
                    trevoSelecionadoList = new ArrayList<>();
                    adappter.atualizaSelecionados(trevoSelecionadoList);
                    listaTrevos.setAdapter(adappter);
                }
            }

            @Override
            public void cancelar() {

            }
        };
    }

    private List<String> getListStringvaloresTrevos(List<ParametroValorApostaDTO> listaValores){
        List<String> valores = new ArrayList<>();
        for (ParametroValorApostaDTO valorAposta : listaValores){
            valores.add(valorAposta.getNumeroTrevos() + " " + getString(R.string.trevos_por)
                                + " " + ViewUtils.getMoedaFormat(valorAposta.getValor()));
        }

        return valores;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private String getStringFormatada(int idString, String target, String replace){
        String s = getString(idString).replace(target, replace);
        SpannableStringBuilder string = ViewUtils.textFuturaAndFuturaBold(getActivity(), s);

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
        if (dezena.isSelected()){
            Integer numero = Integer.valueOf(dezena.getValue());
            if (!trevoSelecionadoList.contains(numero)) {
                trevoSelecionadoList.add(numero);
            }
        }else {
            Integer numero = Integer.valueOf(dezena.getValue());
            if (trevoSelecionadoList.contains(numero)) {
                trevoSelecionadoList.remove(numero);
            }
        }
        holder.atualizaDezena(dezena);
        checaQuantidadeTrevos();
        atualizaStatusBotao();
        checaMostraBotaoLimpar();
    }

    private void checaMostraBotaoLimpar() {
        if (trevoSelecionadoList.size() > 0){
            listener.getButtonLimpar().setVisibility(View.VISIBLE);
        }else {
            listener.getButtonLimpar().setVisibility(View.GONE);
        }
    }

    private void checaQuantidadeTrevos() {
        if (trevoSelecionadoList.size() >= parametro.getTrevos().getQtdMinima()){
            textoBotaoTrevosSelecionado = trevoSelecionadoList.size() + getString(R.string.espaco_em_branco) +getString(R.string.trevos);
            listener.getButtonQtdTrevos().setText(textoBotaoTrevosSelecionado);

            valorApostaSelecionado = parametro.getValorApostaBy(dezenasSelecionadas.size(), trevoSelecionadoList.size());
            listener.atualizaValorAposta(valorApostaSelecionado);
        }
    }

    private void atualizaStatusBotao() {
        if (trevoSelecionadoList.size() >= valorApostaSelecionado.getNumeroTrevos()){
            listener.getButtonFinalizar().setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.adicionarAoCarrinho)));
            listener.getButtonFinalizar().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mais_milionaria_escura));

            listener.getButtonSalvar().setVisibility(View.VISIBLE);
        }else {
            listener.getButtonFinalizar().setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), getString(R.string.completar_trevos_aleatoriamente)));
            listener.getButtonFinalizar().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cinzaescuro));

            listener.getButtonSalvar().setVisibility(View.GONE);
        }
    }
}
