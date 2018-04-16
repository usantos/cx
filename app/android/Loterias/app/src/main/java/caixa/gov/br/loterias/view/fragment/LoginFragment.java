package caixa.gov.br.loterias.view.fragment;

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

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.RetornoListaCarrinho;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CadastrarActivity_;
import caixa.gov.br.loterias.view.PrincipalActivity;
import caixa.gov.br.loterias.view.PrincipalActivity_;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

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
                KeycloakBO_.getInstance_(getActivity()).logout(getActivity());

                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
                startActivity(new Intent(getActivity(), PrincipalActivity_.class));
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
                            verificarCadastroWebservice();
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
    private void verificarCadastroWebservice() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(getActivity());
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(getActivity());
        final RetornoListaCarrinho retornoListaCarrinho = new RetornoListaCarrinho();
        DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
//        carrinhoDTO.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        List<ApostaDTO> listApostas = new ArrayList<>();
        retornoListaCarrinho.setApostas( listApostas );
        dadosUsuarioBO.postVerificarCadastro(retornoListaCarrinho, new RequestListener<UsuarioLogadoResponse>() {
            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() == null){
                    PrincipalActivity_.intent(getActivity()).start();
                    getActivity().finish();
                }else{
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), getActivity() );
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, getActivity() );

            }
        });
    }




}
