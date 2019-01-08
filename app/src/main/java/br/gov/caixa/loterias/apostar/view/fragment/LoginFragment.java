package br.gov.caixa.loterias.apostar.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bean.RetornoListaCarrinho;
import br.gov.caixa.loterias.apostar.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostar.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostar.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostar.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.utils.AppUtils;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.CadastrarActivity_;
import br.gov.caixa.loterias.apostar.view.PrincipalActivity_;
import br.gov.caixa.loterias.apostar.view.custom.LoadingViewLoterias;

public class LoginFragment extends Fragment {

    private AlertDialog loadViewProgress;

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
        if ((DadosUsuarioBO.checarFecharIntroducao(getActivity()) && DadosUsuarioBO.checarUsuarioLogado(getActivity())) && LoginSP.isPrimeiraInicializacao()){
            LoginSP.primeiraInicializacao(false);
            connectKeycloak();
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LoginSP.isLoginRealizado()) {
            verificarCadastroWebservice();
        }
    }


    private void verificarCadastroWebservice() {
        loadViewProgress = LoadingViewLoterias.show(getContext());
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(getContext());
        final RetornoListaCarrinho retornoListaCarrinho = new RetornoListaCarrinho();
        DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
        List<ApostaDTO> listApostas = new ArrayList<>();
        retornoListaCarrinho.setApostas(listApostas);
        dadosUsuarioBO.postVerificarCadastro(retornoListaCarrinho, new RequestListener<UsuarioLogadoResponse>() {
            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() == null) {
                    PrincipalActivity_.intent(getActivity()).start();
                    getActivity().finish();
                } else {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, getActivity());
            }
        });
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
