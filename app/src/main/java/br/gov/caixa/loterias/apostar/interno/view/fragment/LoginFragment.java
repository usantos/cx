package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.controllers.CadastrarActivity_;
import br.gov.caixa.loterias.apostar.interno.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.model.sp.LoginSP;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;


public class LoginFragment extends Fragment {

    private AlertDialog loadViewProgress;

    Button buttonLogin;
    TextView txtVersao;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button buttonAposte = v.findViewById(R.id.buttonAposteAgora);
        buttonAposte.setOnClickListener(v1 -> {
            FirebaseManager.registraEvento(getContext() ,"ENTROU_OPCAO_DEGUSTAR");
            KeycloakBO_.getInstance_(getActivity()).limparSessao(getActivity());

            DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
            startActivity(new Intent(getContext(), PrincipalActivity_.class));
            getActivity().finish();
        });

        buttonLogin  = v.findViewById(R.id.buttonFacaLogin);
        txtVersao = v.findViewById(R.id.textVersao);
        txtVersao.setText("v. "+ BuildConfig.VERSION_NAME);

        buttonLogin.setOnClickListener(v12 -> {
            FirebaseManager.registraEvento(getContext() ,"ENTROU_LOGIN");

            DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
            connectKeycloak();
        });

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
        DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
        dadosUsuarioBO.postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() == null) {
                    SessaoUsuario.getInstance().setValorMaximoAposta(response.getPayload().getLimiteDiario());

                    crud.deleteAll();
                    CarrinhoSingleton.getInstance().setCarrinho(null);
                    CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());

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
        if(AppUtils.isNetworkAvailable(getContext())){
            AcessoBO.PRIMEIRO_LOGIN = true;
            AcessoBO_.getInstance_(getContext()).iniciarLoginSSO(getActivity());
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
