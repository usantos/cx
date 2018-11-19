package caixa.gov.br.loterias.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;

import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationException;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.RetornoListaCarrinho;
import caixa.gov.br.loterias.model.bo.AcessoBO;
import caixa.gov.br.loterias.model.bo.AcessoBO_;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;

import caixa.gov.br.loterias.model.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CadastrarActivity_;
import caixa.gov.br.loterias.view.PrincipalActivity;
import caixa.gov.br.loterias.view.PrincipalActivity_;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class LoginFragment extends Fragment {


    Button buttonLogin;

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
                KeycloakBO_.getInstance_(getActivity()).limparSessao(getActivity());

                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
                startActivity(new Intent(getActivity(), PrincipalActivity_.class));
                getActivity().finish();
            }
        });

        buttonLogin  = (Button) v.findViewById(R.id.buttonFacaLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
                connectKeycloak();
            }
        });

        KeycloakBO keycloakBO =  KeycloakBO_.getInstance_(getActivity());
        if (DadosUsuarioBO.checarFecharIntroducao(getActivity()) && DadosUsuarioBO.checarUsuarioLogado(getActivity())){
            connectKeycloak();
        }

        return v;
    }

    private void connectKeycloak() {
        if(AppUtils.isNetworkAvailable(getActivity())){
            AcessoBO.PRIMEIRO_LOGIN = true;
            AcessoBO_.getInstance_(getContext()).iniciarLoginSSO();
        }else{
            buttonLogin.setEnabled( true );
            Log.e("caixa", "Erro ao conexar com internet LoginFragment.connectKeycloak()");
            ViewUtils.alert(getActivity(), getString(R.string.seminternet));
        }
    }

    private void loginSucess() {
        startActivity(new Intent(getActivity(), CadastrarActivity_.class));
        getActivity().finish();
    }




}
