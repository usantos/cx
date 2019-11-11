package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.controllers.ListaRapidaoActivity;
import br.gov.caixa.loterias.apostar.interno.controllers.RapidaoConfigActivity_;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class TutorialRapidaoFragment extends Fragment {

    private static final String NUM_TELA = "NUM_TELA";

    private TextView tvRapidaoResolve;
    private TextView tvNumerosProibidos;
    private TextView tvLimites;
    private TextView tvQuerAdicionar;
    private Button btnConfigureAgora;
    private Button btnGerarRapidao;

    private int mParam1;

    private OnFragmentInteractionListener mListener;

    public TutorialRapidaoFragment() {
    }


    public static TutorialRapidaoFragment newInstance(int param1) {
        TutorialRapidaoFragment fragment = new TutorialRapidaoFragment();
        Bundle args = new Bundle();
        args.putInt(NUM_TELA, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(NUM_TELA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        switch (mParam1) {
            case 0:
                return inflater.inflate(R.layout.fragment_tutorial_rapidao_1, container, false);
            case 1:
                view = inflater.inflate(R.layout.fragment_tutorial_rapidao_2, container, false);
                configuraTela2(view);
                return view;
            case 2:
                view = inflater.inflate(R.layout.fragment_tutorial_rapidao_3, container, false);
                configuraTela3(view);
                return view;
            default:
                return null;

        }

    }

    private void configuraTela2(View view){

        tvRapidaoResolve = view.findViewById(R.id.tv_rapidao_resolve);
        tvRapidaoResolve.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_rapidao_resolve)));

    }

    private void configuraTela3(View view){

        tvLimites = view.findViewById(R.id.tv_limites);
        tvNumerosProibidos = view.findViewById(R.id.tv_numeros_proibidos);
        btnConfigureAgora = view.findViewById(R.id.btn_configure_agora);
        btnGerarRapidao = view.findViewById(R.id.btn_gerar_rapidao);
        tvQuerAdicionar = view.findViewById(R.id.tv_quer_adicionar);

        tvLimites.setText(TextUtils.concat(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_limites_1)), ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_limites_2))));
        tvNumerosProibidos.setText(ViewUtils.fromHtml(getResources().getString(R.string.label_numeros_proibidos)));
        btnConfigureAgora.setText(ViewUtils.textFuturaAndFuturaBold(getContext(),getResources().getString(R.string.botao_configure_agora)));
        btnGerarRapidao.setText(ViewUtils.textFuturaAndFuturaBold(getContext(),getResources().getString(R.string.botao_gerar_rapidao)));
        tvQuerAdicionar.setText(ViewUtils.textFuturaAndFuturaBold(getContext(),getResources().getString(R.string.label_quer_adicionar)));

        btnGerarRapidao.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ListaRapidaoActivity.class));
        });
        btnConfigureAgora.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RapidaoConfigActivity_.class));

        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
