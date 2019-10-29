package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;

public class ConfiguracaoRapidaoFragment5 extends Fragment {

    NumerosSelecionadosRapidao[] listaNumerosSelecionadosRapidao;
    List<Integer> numerosLista = new ArrayList<>();
    List<Integer> listaNumerosContrariosRapidao = new ArrayList<>();
    Button salvarNumerosApostaRapidaoSelecao;
    Button limparButtonRapidaoSelecionados;
    TextView titleNumerosRapidaoObrigatorios;
    TextView textoSelecaoNumerosRapidao;
    ExpandableHeightGridView gridViewNumerosRapidaoSlc;
    Boolean isTipoNrObrigatorios;

    public ConfiguracaoRapidaoFragment5() { }

    public static ConfiguracaoRapidaoFragment5 newInstance() {
        ConfiguracaoRapidaoFragment5 fragment = new ConfiguracaoRapidaoFragment5();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment5, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
