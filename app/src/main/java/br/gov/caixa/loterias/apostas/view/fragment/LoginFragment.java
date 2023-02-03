package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import java.util.List;
import java.util.concurrent.Executor;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CadastrarActivity_;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.controllers.TokenActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaLogin;
import br.gov.caixa.loterias.apostas.model.bo.DadosBiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;


public class LoginFragment extends Fragment {

    Button buttonLogin;
    TextView txtVersao;

    LinearLayout biometriaLinearLayout;
    Switch biometriaSwitch;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Boolean temBiometria;
    Boolean biometriaHabilitada = false;
    BiometriaDTO biometriaDTO;
    AlertDialog alertDialogBio;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        biometriaLinearLayout = v.findViewById(R.id.biometriaLinearLayout);
        biometriaSwitch = v.findViewById(R.id.biometriaSwitch);

        setUpBiometria();

        biometriaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    biometriaHabilitada = true;
                } else {
                    DialogUtils.showDialogNaoSim(getActivity(),
                            getString(R.string.desativar_biometria), getString(R.string.desativar_biometria_mensagem),
                            new OnDialogListenerLeftRight() {
                                @Override
                                public void left() {
                                    biometriaHabilitada = true;
                                    if (temBiometria && biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
                                        biometriaSwitch.setChecked(true);
                                        buttonLogin.setText(R.string.entrar);
                                    }
                                }
                                @Override
                                public void right() {
                                    DadosBiometriaBO.limparRegistros(); //Apaga RefreshToken
                                    biometriaHabilitada = false;
                                    buttonLogin.setText(R.string.label_faca_login_cadastre);
                                }
                            });
                }
            }
        });

        Button buttonAposte = v.findViewById(R.id.buttonAposteAgora);
        buttonAposte.setOnClickListener(v1 -> {
            AppCenterManager.registraEvento("ENTROU_OPCAO_DEGUSTAR");
            KeycloakBO_.getInstance_(getActivity()).limparSessao(getActivity());

            DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
            startActivity(new Intent(getContext(), PrincipalActivity_.class));
            getActivity().finish();
        });

        buttonLogin  = v.findViewById(R.id.buttonFacaLogin);
        txtVersao = v.findViewById(R.id.textVersao);
        txtVersao.setText("v. "+ BuildConfig.VERSION_NAME);

        biometriaDTO = DadosBiometriaBO.obterBiometria();
        if (temBiometria && biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
            biometriaSwitch.setChecked(true);
            buttonLogin.setText(R.string.entrar);
        } else {
            buttonLogin.setText(R.string.label_faca_login_cadastre);
        }

        buttonLogin.setOnClickListener(v12 -> {

            biometriaDTO = DadosBiometriaBO.obterBiometria();

            if (temBiometria && biometriaHabilitada &&
                    biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
                biometricPrompt.authenticate(promptInfo);
            } else {
                AppCenterManager.registraEvento("ENTROU_LOGIN");
                DadosUsuarioBO.updateFecharIntroducao(getActivity(), true);
                connectKeycloak();
            }
        });

        if ((DadosUsuarioBO.checarFecharIntroducao(getActivity()) && DadosUsuarioBO.checarUsuarioLogado(getActivity())) && LoginSP.isPrimeiraInicializacao()){
            LoginSP.primeiraInicializacao(false);
            //Bloqueado para NAO entrar direto no SSO
            //connectKeycloak();
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LoginSP.isLoginRealizado()) {
            if(SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() == null){
                verificarCadastroWebservice();
            }else {
                PrincipalActivity_.intent(getActivity()).start();
                getActivity().finish();
            }

        }
    }


    private void verificarCadastroWebservice() {
        AlertDialogUtils.show(getContext());
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(getContext());
        DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
        dadosUsuarioBO.postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() == null) {
                    SessaoUsuario.getInstance().setValorMaximoAposta(response.getPayload().getLimiteDiario());
                    if(response.getPayload().getLimiteDiario() != null){
                        SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                    }
                    crud.deleteAll();
                    CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());
                    UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(response.getPayload().getUltimaNotificacao());

                    PrincipalActivity_.intent(getActivity()).start();
                    getActivity().finish();
                } else {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
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


    private void setUpBiometria() {

        BiometricManager biometricManager = BiometricManager.from(getActivity());
        //BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                temBiometria = true;
                biometriaLinearLayout.setVisibility(View.VISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                temBiometria = false;
                biometriaLinearLayout.setVisibility(View.GONE);
                return;
        }

        Executor executor = ContextCompat.getMainExecutor(getActivity());

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //Chama SSO
                AppCenterManager.registraEvento("ENTROU_LOGIN");
                connectKeycloak();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                //Toast.makeText(getActivity(), "*** Biometria Sucesso ***", Toast.LENGTH_SHORT).show();

                BiometriaLogin biometriaLogin = new BiometriaLogin();
                biometriaLogin.loginBiometria(getActivity(), onRefreshTokenListener());
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Loterias")
                //.setSubtitle("SubTitulo")
                .setDescription("Desbloqueie seu celular")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();
    }


    private <T> OnRefreshTokenListener onRefreshTokenListener() {
        return new OnRefreshTokenListener() {
            @Override
            public void successRefresh() {
                //Toast.makeText(getActivity(), "** SUCESSO REFRESH **", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getContext(), PrincipalActivity_.class));
                getActivity().finish();
            }

            @Override
            public void errorRefresh(VolleyError volleyError) {
                //Toast.makeText(getActivity(), "** Erro REFRESH **"+ volleyError, Toast.LENGTH_SHORT).show();
                //Chama SSO
                AppCenterManager.registraEvento("ENTROU_LOGIN");
                connectKeycloak();
            }
        };
    }

}
