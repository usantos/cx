package br.gov.caixa.loterias.apostar.interno.view.fragment;

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
                tvRapidaoResolve = view.findViewById(R.id.tv_rapidao_resolve);
                tvRapidaoResolve.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_rapidao_resolve)));
                return view;
            case 2:
                view = inflater.inflate(R.layout.fragment_tutorial_rapidao_3, container, false);
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

                return view;
            default:
                return null;

        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
