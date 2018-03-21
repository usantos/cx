package caixa.gov.br.loterias.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button buttonAposte = (Button) v.findViewById(R.id.buttonAposteAgora);
        buttonAposte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mock login
                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
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

    private void connectKeycloak() {
        if(AppUtils.isNetworkAvailable(getActivity())){
            KeycloakBO_.getInstance_(getActivity()).login(getActivity(), new KeycloakBO.OnLogin() {
                    @Override
                    public void onSuccess(String token) {
                        if(getActivity() != null){
                            startActivity(new Intent(getActivity(), PrincipalActivity.class));
                            getActivity().finish();
                            //loginSucess();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if(getActivity() != null){
                            ViewUtils.alert(getActivity(), getString(R.string.sistema_indisponivel));
                        }
                    }
                }
            );
        }else{
            ViewUtils.alert(getActivity(), getString(R.string.seminternet));
        }
    }

    private void loginSucess() {
        startActivity(new Intent(getActivity(), CadastrarActivity_.class));
        getActivity().finish();
    }
}
