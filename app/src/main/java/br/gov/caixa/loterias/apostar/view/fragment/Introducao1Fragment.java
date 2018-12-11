package br.gov.caixa.loterias.apostar.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.IntroducaoActivity;
import br.gov.caixa.loterias.apostar.view.RestritoDezoitoAnosActivity;

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

        textView1 = (TextView) v.findViewById(R.id.textView1Fragment1);
//        textView1.setText(ViewUtils.fromHtml("Uma nova <font face='Futura Book'>experiência</font>"));
        textView1.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), "Uma nova _experiência_"));

        Button button = (Button) v.findViewById(R.id.buttonPularIntroducao);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                ((IntroducaoActivity) getActivity()).pularIntroducao();
            }
        });
        ViewUtils.alertVerificarIdadeListener(getActivity(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getActivity(), RestritoDezoitoAnosActivity.class));
                getActivity().finish();
            }
        });



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
