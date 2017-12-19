package caixa.gov.br.loterias.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.data.UsuarioLogadoSingleton;
import caixa.gov.br.loterias.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CadastrarActivity_;
import caixa.gov.br.loterias.view.CadastrarPasso2Activity_;
import caixa.gov.br.loterias.view.CarrinhoActivity;
import caixa.gov.br.loterias.view.FormaPagamentoActivity;
import caixa.gov.br.loterias.view.PrincipalActivity;
import caixa.gov.br.loterias.view.RepassesSociaisActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button buttonAposte = (Button) v.findViewById(R.id.buttonAposteAgora);
        buttonAposte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mock login
//                startActivity(new Intent(getActivity(), CarrinhoActivity.class));
//                getActivity().finish();

                final View loadViewProgress = LoadingViewLoterias.init(getActivity());

                DadosUsuarioBO dadosUsuarioBO = new DadosUsuarioBO(getActivity());
//                dadosUsuarioBO.autenticar("vinicius.ferraz@gmail.com", "Vaibombar2", new SilceClient.Listener<UsuarioLogadoResponse>() {

                dadosUsuarioBO.autenticar("symoneresio@yahoo.com.br", "Vaibombar10", new SilceClient.Listener<UsuarioLogadoResponse>() {
                    @Override
                    public void onResponse(UsuarioLogadoResponse response) {
                        LoadingViewLoterias.stop(loadViewProgress);

                        UsuarioLogadoSingleton user = UsuarioLogadoSingleton.getInstance();
                        user.token = response.getPayload().getToken();

                        startActivity(new Intent(getActivity(), PrincipalActivity.class));
                    }
                }, new SilceClient.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingViewLoterias.stop(loadViewProgress);

                        ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                    }
                });
            }
        });

        Button buttonLogin = (Button) v.findViewById(R.id.buttonFacaLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final View loadViewProgress = LoadingViewLoterias.init(getActivity());

                DadosUsuarioBO dadosUsuarioBO = new DadosUsuarioBO(getActivity());
                dadosUsuarioBO.autenticar("symoneresio@yahoo.com.br", "Vaibombar10", new SilceClient.Listener<UsuarioLogadoResponse>() {
                    @Override
                    public void onResponse(UsuarioLogadoResponse response) {
                        LoadingViewLoterias.stop(loadViewProgress);
                        loginSucess();
                    }
                }, new SilceClient.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingViewLoterias.stop(loadViewProgress);
                        ViewUtils.alert(getActivity(), MensagensNetwork.setMensagem(error, getActivity()));
                    }
                });
//                connectKeycloak();
            }
        });


        return v;
    }


    private void connectKeycloak() {
        if (!KeycloakBO.isConnected()) {

            KeycloakBO.connect(getActivity(), new org.jboss.aerogear.android.core.Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            loginSucess();
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    }
            );

        } else {
            loginSucess();
        }
    }

    private void loginSucess() {
        startActivity(new Intent(getActivity(), CadastrarActivity_.class));
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
