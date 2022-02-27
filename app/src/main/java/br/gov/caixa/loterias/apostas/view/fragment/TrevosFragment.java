package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.OnTrevosListener;

@EFragment(R.layout.fragment_trevos)
public class TrevosFragment extends Fragment implements OnItemClickListener<DezenaHolder> {

    @ViewById
    protected TextView escolhaNumerosText;
    @ViewById
    protected RecyclerView listaTrevos;

    @FragmentArg
    protected ParametroJogoDTO parametro;
    @FragmentArg
    protected ArrayList<Integer> dezenasSelecionadas;
    @FragmentArg
    protected int idBtnQtdTrevos;

    private OnTrevosListener listener;
    private List<ParametroValorApostaDTO> valoresApostas;
    private String textoBotaoTrevosSelecionado;
    private ParametroValorApostaDTO valorApostaSelecionado;
    private List<Dezena> dezenaList;
    private List<Integer> trevoSelecionadoList = new ArrayList<>();

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

        String                 tituloSemBold      = getString(R.string.voce_escolheu_x_numeros)
                .replace("x", String.valueOf(dezenasSelecionadas.size()));
        SpannableStringBuilder titulo = ViewUtils.textFuturaAndFuturaBold(getActivity(), tituloSemBold);

        escolhaNumerosText.setText(titulo);
        escolhaNumerosText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        configIncialButtons();

        valoresApostas = parametro.getValoresTrevoByNumero(dezenasSelecionadas.size());

        ListaDezenaRecyclerView listaDezenaRecyclerView = new ListaDezenaRecyclerView(getListaDezenas(), trevoSelecionadoList, R.color.mais_milionaria_escura, this);
        listaTrevos.setAdapter(listaDezenaRecyclerView);
        RecyclerView.LayoutManager layot = new GridLayoutManager(getContext(), 3);
        listaTrevos.setLayoutManager(layot);

    }

    private List<Dezena> getListaDezenas() {
        if (dezenaList == null || dezenaList.size() != parametro.getTrevos().getQtdPrognostico()){
            dezenaList = new ArrayList<>();
            for (int numeo = 1; numeo <= parametro.getTrevos().getQtdPrognostico(); numeo++){
                Dezena dezena = new Dezena();
                dezena.setValue(String.valueOf(numeo));
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
            Toast.makeText(getContext(), "Falta implementar", Toast.LENGTH_LONG).show();
        };
    }

    private View.OnClickListener onFinalizarClickListener() {
        return v -> {
            Toast.makeText(getContext(), "Falta implementar", Toast.LENGTH_LONG).show();
        };
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
                listener.valorAposta(valorApostaSelecionado);
            }

            @Override
            public void cancelar() {

            }
        };
    }

    private List<String> getListStringvaloresTrevos(List<ParametroValorApostaDTO> listaValores){
        List<String> valores = new ArrayList<>();
        for (ParametroValorApostaDTO valorAposta : listaValores){
            valores.add(valorAposta.getNumeroTrevos() + " " + getString(R.string.trevos_por) + " " + valorAposta.getValor());
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
    public void click(DezenaHolder holder, int position) {
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
    }
}
