package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO_;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaLogin;
import br.gov.caixa.loterias.apostas.model.bo.DadosBiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.model.LoginSSOModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AmbienteEnum;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;


public class LoginFragment extends Fragment {

    private static final String USUARIO_KEY = "USUARIO_KEY";

    Button buttonLogin;
    TextView txtVersao;

    LinearLayout biometriaLinearLayout;
    Switch biometriaSwitch;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Boolean temBiometria;
    Boolean temMatriculaBiometria = false;
    BiometriaDTO biometriaDTO;
    AlertDialog alertDialogBio;
    TextView txtSSO;
    TextView txtTrocaUsuario;
    Spinner ambienteSpinner;

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

        //Selecao ambiente se NAO PRD
        if (!BuildConfig.FLAVOR.equals("prd")) {
            //Ambiente Selecao
            ambienteSpinner = v.findViewById(R.id.spin_ambiente);
            ambienteSpinner.setVisibility(View.VISIBLE);
            AmbienteEnum[] ambienteEnums = AmbienteEnum.values();
            ArrayAdapter<AmbienteEnum> adapter = new ArrayAdapter<AmbienteEnum>(getContext(),
                    R.layout.spinner_item_ambiente, ambienteEnums);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ambienteSpinner.setAdapter(adapter);
            //Carrega selecao salva no SharedPreferences
            String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO.name());
            AmbienteEnum ambienteAtual = AmbienteEnum.valueOf(ambienteSalvo);
            ambienteSpinner.setSelection(adapter.getPosition(ambienteAtual));
            //Listener salvar a selecao
            ambienteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AmbienteEnum selectedAmbiente = (AmbienteEnum) parent.getItemAtPosition(position);
                    SharedPreferencesUtils.setValor("AMBIENTE_SELECIONADO", selectedAmbiente.name());
                    if (!selectedAmbiente.name().equals(ambienteSalvo)) {
                        showAlertAndExit(selectedAmbiente);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        setUpBiometria();

        biometriaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                BiometriaBO.getInstance().setBiometriaHabilitada(true);
                if (!temMatriculaBiometria) {
                    ligaBiometria();
                }
            } else {
                desligaBiometria();
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
        txtSSO = v.findViewById(R.id.textViewSSO);
        txtTrocaUsuario = v.findViewById(R.id.textViewTrocar);
        txtVersao = v.findViewById(R.id.textVersao);
        txtVersao.setText("v. "+ BuildConfig.VERSION_NAME);

        biometriaDTO = DadosBiometriaBO.obterBiometria();
        if (temBiometria && biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
            biometriaSwitch.setChecked(true);
            setBotoesParaToken();
        } else {
            setBotoesParaSSO();
        }

        txtTrocaUsuario.setOnClickListener(v13 -> {
            AppCenterManager.registraEvento("ENTROU_LOGIN");
            AlertDialogUtils.show(getContext());
            BiometriaBO.getInstance().setIsTrocandoUsuario(true);
            new LoginSSOModel(getActivity()).sair(() -> {});
        });

        buttonLogin.setOnClickListener(v12 -> {

            biometriaDTO = DadosBiometriaBO.obterBiometria();

            if (temBiometria && BiometriaBO.getInstance().getBiometriaHabilitada() &&
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

        if (BiometriaBO.getInstance().isTrocandoUsuario()){
            biometriaDesligada();
            AlertDialogUtils.dismiss();
            BiometriaBO.getInstance().setIsTrocandoUsuario(false);
            connectKeycloak();
        } else if(LoginSP.isLoginRealizado()) {
            if(SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() == null){
                verificarCadastroWebservice();
            }else {
                PrincipalActivity_.intent(getActivity()).start();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!temMatriculaBiometria) {
            BiometricManager biometricManager = BiometricManager.from(getActivity());
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    temMatriculaBiometria = true;
                    break;
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
                    SessaoUsuario.getInstance().setResponderAutoavaliacao(response.getPayload().getResponderAutoavaliacao());
                    SessaoUsuario.getInstance().setSuspensaoTemporariaApostador(response.getPayload().getSuspensaoTemporariaApostador());

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

    private void setBotoesParaSSO() {
        buttonLogin.setText(R.string.label_faca_login_cadastre);
        txtSSO.setVisibility(View.VISIBLE);
        txtTrocaUsuario.setVisibility(View.INVISIBLE);
        biometriaSwitch.setChecked(false);
    }

    private void setBotoesParaToken() {
        buttonLogin.setText(getString(R.string.entrar) + " "
                + StringUtils.firstWord(StringUtils.capitalizer(biometriaDTO.getNome())));
        txtSSO.setVisibility(View.INVISIBLE);
        txtTrocaUsuario.setVisibility(View.VISIBLE);
    }

    private void setUpBiometria() {

        BiometricManager biometricManager = BiometricManager.from(getActivity());
        //BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                temBiometria = true;
                temMatriculaBiometria = true;
                biometriaLinearLayout.setVisibility(View.VISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                temBiometria = true;
                temMatriculaBiometria = false;
                biometriaLinearLayout.setVisibility(View.VISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                temBiometria = false;
                temMatriculaBiometria = false;
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
                .setTitle(getString(R.string.biometria_popup_titulo))
                .setDescription(getString(R.string.biometria_popup_descricao))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();
    }

    private void ligaBiometria () {
        DialogUtils.showDialogLeftRight(getActivity(), getString(R.string.ativar_biometria),
                null, getString(R.string.cancelar), getString(R.string.habilitar),
                new OnDialogListenerLeftRight() {
                    @Override
                    public void left() {
                    }

                    @Override
                    public void right() {
                        //Direcina para as configurações da Biometria
                        try {
                            final Intent activity = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                            activity.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                           BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                            startActivity(activity);
                        } catch (Exception e) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    }
                });
    }

    private void desligaBiometria () {
        DialogUtils.showDialogLeftRight(getActivity(),
                getString(R.string.desativar_biometria), getString(R.string.desativar_biometria_mensagem),
                null,null,
                new OnDialogListenerLeftRight() {
                    @Override
                    public void left() {
                        BiometriaBO.getInstance().setBiometriaHabilitada(true);
                        biometriaSwitch.setChecked(true);
                        if (temBiometria && biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
                            setBotoesParaToken();
                        }
                    }
                    @Override
                    public void right() {
                        biometriaDesligada();
                    }
                });
    }

    private void biometriaDesligada() {
        DadosBiometriaBO.limparRegistros(); //Apaga RefreshToken
        BiometriaBO.getInstance().setBiometriaHabilitada(false);
        setBotoesParaSSO();
    }

    private <T> OnRefreshTokenListener onRefreshTokenListener() {
        return new OnRefreshTokenListener() {
            @Override
            public void successRefresh() {
                startActivity(new Intent(getContext(), PrincipalActivity_.class));
                getActivity().finish();
            }

            @Override
            public void errorRefresh(VolleyError volleyError) {
                //Chama SSO
                AppCenterManager.registraEvento("ENTROU_LOGIN");
                connectKeycloak();
            }
        };
    }

    private void showAlertAndExit(AmbienteEnum selectedAmbiete) {
        new AlertDialog.Builder(getContext())
            .setTitle("Mudança de Ambiente")
            .setMessage("O aplicativo será encerrado para aplicar as mudanças de ambeinte.")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() != null) {
                        getActivity().finishAffinity();
                    }
                }
            })
            .show();
    }
}
