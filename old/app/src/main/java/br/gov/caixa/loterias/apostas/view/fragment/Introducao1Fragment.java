package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.controllers.IntroducaoActivity;
import br.gov.caixa.loterias.apostas.controllers.RestritoDezoitoAnosActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Introducao1Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Introducao1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Introducao1Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    private TextView textView1;

    private OnFragmentInteractionListener mListener;

    public Introducao1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Introducao1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Introducao1Fragment newInstance(int param1, String param2) {
        Introducao1Fragment fragment = new Introducao1Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_introducao1, container, false);

        textView1 = v.findViewById(R.id.textView1Fragment1);
        textView1.setText(ViewUtils.textCaixaSTDBold(getContext(), "_Uma nova experiência_"));
        textView1.setTextSize(20);
        textView1.setHint("Título");

        DialogUtils.dialogSimNao(
                getActivity(),
                getActivity().getString(R.string.MA003),
                new OnDialogDoisBotoesListener() {
                    @Override
                    public void PositiveButton(DialogInterface dialog, int which) {
                        //Apenas fecha a modal, sem realizar nenhuma ação
                    }

                    @Override
                    public void NegativeButton(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), RestritoDezoitoAnosActivity.class));
                        getActivity().finish();
                    }
                }
        );

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
