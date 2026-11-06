package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import java.util.List;
import java.util.concurrent.Executor;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.BiometriaLogin;
import br.gov.caixa.loterias.apostas.model.bo.DadosBiometriaBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnRefreshTokenListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoApostadorEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.model.LoginSSOModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AmbienteEnum;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;


public class LoginFragment extends Fragment {

    private static final String USUARIO_KEY = "USUARIO_KEY";

    Button buttonLogin;
    TextView txtVersao;

    ConstraintLayout biometriaLinearLayout;
    Switch biometriaSwitch;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Boolean temBiometria;
    Boolean temMatriculaBiometria = false;
    BiometriaDTO biometriaDTO;
    AlertDialog alertDialogBio;
    Spinner ambienteSpinner;
    private boolean dialogBiometriaVisivel = false;


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
        if (!(BuildConfig.FLAVOR.equals("prd"))) {
            //Ambiente Selecao
            ambienteSpinner = v.findViewById(R.id.spin_ambiente);
            ambienteSpinner.setVisibility(View.VISIBLE);
            AmbienteEnum[] ambienteEnums = AmbienteEnum.values();
            ArrayAdapter<AmbienteEnum> adapter = new ArrayAdapter<AmbienteEnum>(getContext(),
                    R.layout.spinner_item_ambiente, ambienteEnums);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ambienteSpinner.setAdapter(adapter);
            //Carrega selecao salva no SharedPreferences
            String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO_ESTEIRA.name());
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

        biometriaLinearLayout.setOnClickListener(view -> biometriaSwitch.toggle());

        Button buttonAposte = v.findViewById(R.id.buttonAposteAgora);
        buttonAposte.setOnClickListener(v1 -> {
            AppCenterManager.registraEvento("ENTROU_OPCAO_DEGUSTAR");
            KeycloakBO.getInstance().limparSessao(getActivity());
            if(DadosUsuarioBO.temUsuarioBD() &&
                    (!DadosUsuarioBO.obterNome().isEmpty() ||
                            !DadosUsuarioBO.obterCpf().isEmpty())){
                new LoginSSOModel(getActivity()).sair(PrincipalActivity.class, () -> {});
            } else {
                startActivity(new Intent(getContext(), PrincipalActivity.class));
                getActivity().finish();
            }

        });

        buttonLogin  = v.findViewById(R.id.buttonFacaLogin);
        txtVersao = v.findViewById(R.id.textVersao);
        txtVersao.setText("v. "+ BuildConfig.VERSION_NAME);

        biometriaDTO = DadosBiometriaBO.obterBiometria();
        if (temBiometria && biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
            biometriaSwitch.setChecked(true);
            setBotoesParaToken();
        } else {
            setBotoesParaSSO();
        }

        buttonLogin.setOnClickListener(v12 -> {
            AnalyticsHelper.getInstance().logInteraction(
                    AnalyticsHelper.EventCategoryParams.CTA,
                    AnalyticsHelper.EventActionParams.CLICK,
                    AnalyticsHelper.EventLabelParams.ENTRY,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.ENTRY,
                    AnalyticsHelper.Tela.LOGIN
            );

            AnalyticsHelper.getInstance().logFlowStart(
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.Tela.LOGIN.nome,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.Tela.LOGIN.nome,
                    AnalyticsHelper.Tela.LOGIN
            );

            biometriaDTO = DadosBiometriaBO.obterBiometria();

            if (temBiometria && BiometriaBO.getInstance().getBiometriaHabilitada() &&
                    biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
                biometricPrompt.authenticate(promptInfo);
            } else {
                AppCenterManager.registraEvento("ENTROU_LOGIN");
                connectKeycloak();
            }
        });

        if ((DadosUsuarioBO.checarFecharIntroducao() && DadosUsuarioBO.checarUsuarioLogado(getActivity())) && LoginSP.isPrimeiraInicializacao()){
            LoginSP.primeiraInicializacao(false);
            //Bloqueado para NAO entrar direto no SSO
            //connectKeycloak();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        biometriaSwitch = view.findViewById(R.id.biometriaSwitch);
        biometriaLinearLayout = view.findViewById(R.id.biometriaLinearLayout);

        biometriaLinearLayout.setOnClickListener(v -> biometriaSwitch.toggle());

        atualizarDescricaoBiometria(biometriaLinearLayout, biometriaSwitch.isChecked());

        biometriaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            atualizarDescricaoBiometria(biometriaLinearLayout, isChecked);

            if (isChecked) {
                if(!dialogBiometriaVisivel) {
                    BiometriaBO.getInstance().setBiometriaHabilitada(true);
                    if (!temMatriculaBiometria) {
                        ligaBiometria();
                    }
                }
            } else {
                if(!dialogBiometriaVisivel) {
                    desligaBiometria();
                }
            }
        });

    }

    private void atualizarDescricaoBiometria(View rowBiometria, boolean isChecked) {
        String descricao = isChecked ? "Ativado: Interruptor" : "Desativado: Interruptor";
        String textoFinal = getString(R.string.leitor_digital) + ": " + descricao;

        rowBiometria.setContentDescription(textoFinal);

        rowBiometria.post(() -> {
            AccessibilityManager accessibilityManager =
                    (AccessibilityManager) Aplicacao.application.getSystemService(Context.ACCESSIBILITY_SERVICE);

            if (accessibilityManager != null && accessibilityManager.isEnabled()) {
                rowBiometria.announceForAccessibility(textoFinal);
            }
        });
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
            if ((SessaoUsuario.getInstance().getRedirectEnum() != null
                    && (SessaoUsuario.getInstance().getRedirectEnum() == RedirectEnum.CADASTRAR_APOSTADOR ||
                    SessaoUsuario.getInstance().getRedirectEnum() == RedirectEnum.ACEITAR_TERMO_DE_USO))){
                verificarCadastroWebservice();
            } else if(SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() == null){
                verificarCadastroWebservice();
            } else {
                Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), PrincipalActivity.class);
                getActivity().startActivity(intent);
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
        DBLoteriasCrud crud = new DBLoteriasCrud(getContext());
        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();

        ServicoFactoryUtil.getDadosUsuarioService().postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

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
                    SessaoUsuario.getInstance().setSituacaoApostador(response.getPayload().getSituacaoApostador());

                    crud.deleteAll();
                    CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());
                    UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(response.getPayload().getUltimaNotificacao());
                    UltimaNotificacaoSingleton.getInstance().setPagamentoNaoIdentificado(response.getPayload().getPagamentoNaoIdentificado());

                    Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), PrincipalActivity.class);
                    getActivity().startActivity(intent);

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
            AcessoBO.getInstance().iniciarLoginSSO(getActivity(), null);
        }else{
            buttonLogin.setEnabled( true );
            Log.e("caixa", "Erro ao conexar com internet LoginFragment.connectKeycloak()");
            DialogUtils.dialogEntendi(
                    getActivity(),
                    getString(R.string.seminternet)
            );
        }
    }

    private void setBotoesParaSSO() {
        buttonLogin.setText(R.string.label_faca_login_cadastre);
        biometriaSwitch.setChecked(false);
    }

    private void setBotoesParaToken() {
        buttonLogin.setText(getString(R.string.entrar) + " "
                + StringUtils.firstWord(StringUtils.capitalizer(biometriaDTO.getNome())));
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
        dialogBiometriaVisivel = true;
        DialogUtils.dialogDoisBotoesPersonalizados(
                getActivity(),
                getString(R.string.ativar_biometria),
                "Deseja habilitar a biometria?",
                getString(R.string.habilitar),
                getString(R.string.cancelar),
                new OnDialogDoisBotoesListener() {
                    @Override
                    public void PositiveButton(DialogInterface dialog, int which) {
                        dialogBiometriaVisivel = false;
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

                    @Override
                    public void NegativeButton(DialogInterface dialog, int which) {
                        biometriaDesligada();
                        dialogBiometriaVisivel = false;
                    }
                }
        );

    }

    private void desligaBiometria () {
        dialogBiometriaVisivel = true;
        DialogUtils.dialogTituloSimNao(
                getActivity(),
                getString(R.string.desativar_biometria),
                getString(R.string.desativar_biometria_mensagem),
                new OnDialogDoisBotoesListener() {
                    @Override
                    public void PositiveButton(DialogInterface dialog, int which) {
                        dialogBiometriaVisivel = false;
                        biometriaDesligada();
                    }

                    @Override
                    public void NegativeButton(DialogInterface dialog, int which) {
                        BiometriaBO.getInstance().setBiometriaHabilitada(true);
                        biometriaSwitch.setChecked(true);
                        if (temBiometria && biometriaDTO != null && biometriaDTO.getRefresh().length() > 0 ) {
                            setBotoesParaToken();
                        }
                        dialogBiometriaVisivel = false;
                    }
                }

        );
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
                if (SessaoUsuario.getInstance().getSituacaoApostador() != null &&
                        SessaoUsuario.getInstance().getSituacaoApostador().getEnum().equals(SituacaoApostadorEnum.BLOQUEADO_PARCIAL)) {
                    DialogUtils.dialogEntendiListener(
                            getActivity(),
                            getActivity().getString(R.string.usuario_bloqueio_parcial),

                            new OnDialogBotaoListener() {
                                @Override
                                public void onButtonClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getContext(), PrincipalActivity.class));
                                    getActivity().finish();
                                    return;
                                }
                            }
                    );
                    return;
                }
                startActivity(new Intent(getContext(), PrincipalActivity.class));
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

        DialogUtils.dialogTituloEntendiListener(
                getContext(),
                "Mudança de Ambiente",
                "O aplicativo será encerrado para aplicar as mudanças de ambiente.",

                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        if (getActivity() != null) {
                            getActivity().finishAffinity();
                        }
                        return;
                    }
                }
        );
    }
}
