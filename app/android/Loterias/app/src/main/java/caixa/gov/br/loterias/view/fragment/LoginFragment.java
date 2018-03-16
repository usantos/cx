package caixa.gov.br.loterias.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.silce.data.UsuarioLogadoSingleton;
import caixa.gov.br.loterias.model.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CadastrarActivity_;
import caixa.gov.br.loterias.view.PrincipalActivity;

public class LoginFragment extends Fragment {

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (KeycloakBO.isConnected()){
            connectKeycloak();
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
                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
                UsuarioLogadoSingleton user = UsuarioLogadoSingleton.getInstance();
                user.token = null;
                startActivity(new Intent(getActivity(), PrincipalActivity.class));
                getActivity().finish();
            }
        });

        Button buttonLogin = (Button) v.findViewById(R.id.buttonFacaLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
                connectKeycloak();
            }
        });

        return v;
    }

    private void setUser(UsuarioLogadoResponse response) {
        UsuarioLogadoSingleton user = UsuarioLogadoSingleton.getInstance();
        user.token = response.getPayload().getToken();
    }

    private void connectKeycloak() {
        if(AppUtils.isNetworkAvailable(getActivity())){
//        if (!KeycloakBO.isConnected()) {
            KeycloakBO.connect(getActivity(), new org.jboss.aerogear.android.core.Callback() {
                    @Override
                    public void onSuccess(Object o) {
                        UsuarioLogadoSingleton user = UsuarioLogadoSingleton.getInstance();
                        user.token = o.toString();
                        startActivity(new Intent(getActivity(), PrincipalActivity.class));
                        getActivity().finish();
    //                            loginSucess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ViewUtils.alert(getActivity(), getString(R.string.sistema_indisponivel));
                    }
                }
            );
//        } else {
//            loginSucess();
//        }
        }else{
            ViewUtils.alert(getActivity(), getString(R.string.seminternet));
        }
    }

    private void loginSucess() {
        startActivity(new Intent(getActivity(), CadastrarActivity_.class));
        getActivity().finish();
    }
}
