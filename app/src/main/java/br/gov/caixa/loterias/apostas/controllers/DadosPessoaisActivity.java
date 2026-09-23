package br.gov.caixa.loterias.apostas.controllers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect;
import br.gov.caixa.loterias.apostas.event.DadosPessoaisEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.state.DadosPessoaisState;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButton;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;
import br.gov.caixa.loterias.apostas.viewModel.DadosPessoaisViewModel;

/**
 * View da tela de dados pessoais.

 * Os listeners apenas lancam eventos pelo lambda onEvent. A tela observa
 * um unico LiveData (uiState) e, ao consumir um comando, devolve o evento
 * ...Consumido para a ViewModel limpar o slot.
 */
public class DadosPessoaisActivity extends LoteriasBaseAppActivity {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");

    private TextInputLayout editTextLimiteLayout;
    private TextInputLayout editTextCEPLayout;
    private EditText editTextNomeContent;
    private EditText editTextUFContent;
    private EditText editTextMunicipioContent;
    private EditText editTextEmailContent;
    private EditText editTextLimiteContent;
    private MaterialTextView limiteTitulo;
    private MaterialTextView txtValidaCep;
    private MaterialTextView validaLimiteMinContent;
    private MaterialTextView validaLimiteMaxContent;
    private MaterialTextView labelCep;
    private MaskedEditText editTextCpfContent;
    private br.gov.caixa.loterias.apostas.view.components.MaskedEditText editTextCepContent;
    private SegmentedButtonGroup segmentedButtonSexo;
    private Button updateDadosPessoaisButton;
    private Button naoSeiCepButton;
    private Button updateDadosPessoaisLimiteButton;
    private CheckBox notificacaoSwitch;
    private LinearLayout containerNotificacoes;

    private DadosPessoaisViewModel viewModel;

    /** Lambda unico de saida da View para a ViewModel. */
    private DadosPessoaisEvent.Dispatcher onEvent;

    private boolean bloqueiaListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_pessoais);

        viewModel = new ViewModelProvider(this).get(DadosPessoaisViewModel.class);
        onEvent = event -> viewModel.onEvent(event);

        setaViews();
        configurarInsets();
        setaMetodos();
        observarViewModel();

        onEvent.dispatch(new DadosPessoaisEvent.TelaIniciada());
    }

    @Override
    protected boolean baseConsumeInsets() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        onEvent.dispatch(new DadosPessoaisEvent.TelaRetomada());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onEvent.dispatch(new DadosPessoaisEvent.VoltarClicado());
        return true;
    }

    private void configurarInsets() {
        View footer = findViewById(R.id.footerDadosPessoais);
        View scroll = findViewById(R.id.scrollViewDadosPessoais);
        ViewCompat.setOnApplyWindowInsetsListener(scroll, (v, insets) -> {
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int imeOnly = ime.bottom - sys.bottom;
            int needed = Math.max(0, imeOnly - footer.getHeight());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), needed);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setaViews() {
        editTextCpfContent = findViewById(R.id.editTextCpfContent);
        editTextNomeContent = findViewById(R.id.editTextNomeContent);
        editTextCepContent = findViewById(R.id.editTextCepContent);
        editTextCEPLayout = findViewById(R.id.editTextCEP);
        editTextUFContent = findViewById(R.id.editTextUFContent);
        editTextMunicipioContent = findViewById(R.id.editTextMunicipioContent);
        editTextEmailContent = findViewById(R.id.editTextEmailContent);
        editTextLimiteContent = findViewById(R.id.editTextLimiteContent);
        editTextLimiteLayout = findViewById(R.id.editTextLimite);
        limiteTitulo = findViewById(R.id.limite_titulo);
        labelCep = findViewById(R.id.labelCep);
        txtValidaCep = findViewById(R.id.txt_valida_cep);
        validaLimiteMinContent = findViewById(R.id.txt_valida_limite_min);
        validaLimiteMaxContent = findViewById(R.id.txt_valida_limite_max);
        segmentedButtonSexo = findViewById(R.id.segmentedButtonSexo);
        updateDadosPessoaisButton = findViewById(R.id.updateDadosPesoaisButton);
        naoSeiCepButton = findViewById(R.id.naoSeiCep);
        updateDadosPessoaisLimiteButton = findViewById(R.id.updateDadosPessoaisLimiteButton);
        notificacaoSwitch = findViewById(R.id.notificacao_switch);
        containerNotificacoes = findViewById(R.id.containerNotificacoes);

        configurarToolbar();
        atualizarDescricaoNotificacoes(notificacaoSwitch.isChecked());
    }

    private void configurarToolbar() {
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setHint(getString(R.string.titulo));
        textTitulo.setText(getString(R.string.title_dados_pessoais));

        ImageButton customButton = findViewById(R.id.customButton);
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v ->
                onEvent.dispatch(new DadosPessoaisEvent.VoltarClicado())
        );
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
    }

    // ------------------------------------------------------------------
    // Listeners: apenas lancam eventos
    // ------------------------------------------------------------------

    private void setaMetodos() {
        editTextMunicipioContent.setClickable(false);
        editTextUFContent.setClickable(false);

        updateDadosPessoaisButton.setOnClickListener(view ->
                onEvent.dispatch(new DadosPessoaisEvent.AtualizarClicado())
        );

        updateDadosPessoaisLimiteButton.setOnClickListener(view ->
                onEvent.dispatch(new DadosPessoaisEvent.RecalcularLimiteClicado())
        );

        naoSeiCepButton.setOnClickListener(view ->
                onEvent.dispatch(new DadosPessoaisEvent.NaoSeiCepClicado())
        );

        segmentedButtonSexo.setOnPositionChangedListener(position -> {
            atualizarIconesSexo(position);
            if (!bloqueiaListeners) {
                onEvent.dispatch(new DadosPessoaisEvent.SexoSelecionado(position));
            }
        });

        notificacaoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            atualizarDescricaoNotificacoes(isChecked);
            if (!bloqueiaListeners) {
                onEvent.dispatch(new DadosPessoaisEvent.NotificacaoAlterada(isChecked));
            }
        });

        containerNotificacoes.setOnClickListener(view -> notificacaoSwitch.toggle());

        editTextCepContent.addTextChangedListener(criarTextWatcherCep());
        editTextLimiteContent.addTextChangedListener(criarTextWatcherLimite());
    }

    private TextWatcher criarTextWatcherCep() {
        return new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (bloqueiaListeners) {
                    return;
                }
                onEvent.dispatch(new DadosPessoaisEvent.CepAlterado(editable.toString()));
            }
        };
    }

    private TextWatcher criarTextWatcherLimite() {
        return new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (bloqueiaListeners) {
                    return;
                }
                onEvent.dispatch(new DadosPessoaisEvent.LimiteAlterado(editable.toString()));
            }
        };
    }

    // ------------------------------------------------------------------
    // Observer unico
    // ------------------------------------------------------------------

    private void observarViewModel() {
        viewModel.getUiState().observe(this, this::renderizar);
    }

    private void renderizar(DadosPessoaisState state) {
        if (state == null) {
            return;
        }
        exibirCarregamento(state.isCarregando());

        if (state.getApostador() != null) {
            renderizarApostador(state);
        }
        renderizarLocalizacao(state);
        renderizarLimite(state);

        tratarComandoRede(state.getComandoRede());
        tratarComandoDialog(state.getComandoDialog());
        tratarComandoAnalytics(state.getComandoAnalytics());
        tratarComandoNavegacao(state.getComandoNavegacao());
    }

    private void renderizarApostador(DadosPessoaisState state) {
        ApostadorDTO apostador = state.getApostador();
        bloqueiaListeners = true;
        try {
            editTextCpfContent.setMaskedText(apostador.getCpf());
            editTextNomeContent.setText(apostador.getNome());
            editTextEmailContent.setText(apostador.getEmail());
            setTextSeDiferente(editTextCepContent, apostador.getCep());
            notificacaoSwitch.setChecked(state.isAceitaNoticias());
            segmentedButtonSexo.setPosition(state.getPosicaoSexo());
            atualizarIconesSexo(state.getPosicaoSexo());
        } finally {
            bloqueiaListeners = false;
        }
    }

    private void renderizarLocalizacao(DadosPessoaisState state) {
        editTextUFContent.setText(state.getUf() == null ? "" : state.getUf());
        editTextMunicipioContent.setText(state.getMunicipio() == null ? "" : state.getMunicipio());

        if (state.getErroCep() != null) {
            labelCep.setTextColor(getColor(R.color.vermelho_erro));
            editTextCEPLayout.setBoxStrokeColor(getColor(R.color.vermelho_erro));
            txtValidaCep.setVisibility(VISIBLE);
            txtValidaCep.setText(getString(R.string.cep_invalido));
            editTextUFContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            editTextMunicipioContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }
        labelCep.setTextColor(getColor(R.color.cinza));
        editTextCEPLayout.setBoxStrokeColor(getColor(R.color.cinza_item_desabilitado));
        editTextCEPLayout.setHelperTextEnabled(false);
        txtValidaCep.setVisibility(GONE);

        if (state.isCepValido()) {
            addLeftImg(editTextUFContent);
            addLeftImg(editTextMunicipioContent);
        }
    }

    private void renderizarLimite(DadosPessoaisState state) {
        ApostadorDTO apostador = state.getApostador();
        if (apostador == null) {
            return;
        }
        String valorLimiteMin = formatarMoeda(apostador.getLimiteMinimoDiario());
        String valorLimiteMax = formatarMoeda(apostador.getLimiteDiarioAutorizado());

        validaLimiteMinContent.setText(getString(R.string.restringe_limite_ao_minimo, valorLimiteMin));
        validaLimiteMaxContent.setText(getString(R.string.restringe_limite_ao_maximo, valorLimiteMax));
        updateDadosPessoaisLimiteButton.setText(getString(R.string.dados_limite_compra, valorLimiteMax));

        validaLimiteMinContent.setVisibility(state.isLimiteMenorQueMinimo() ? VISIBLE : GONE);
        validaLimiteMaxContent.setVisibility(state.isLimiteMaiorQueMaximo() ? VISIBLE : GONE);

        bloqueiaListeners = true;
        try {
            setTextSeDiferente(editTextLimiteContent, formatarMoeda(state.getLimiteDigitado()));
        } finally {
            bloqueiaListeners = false;
        }

        if (state.isLimiteValido()) {
            limiteTitulo.setTextColor(getColor(R.color.cinza));
            editTextLimiteLayout.setBoxStrokeColor(getColor(R.color.cinza_item_desabilitado));
            editTextLimiteLayout.setHelperTextEnabled(true);
            editTextLimiteLayout.setHelperText(valorLimiteMin + " até " + valorLimiteMax);
        } else {
            limiteTitulo.setTextColor(getColor(R.color.vermelho_erro));
            editTextLimiteLayout.setBoxStrokeColor(getColor(R.color.vermelho_erro));
            editTextLimiteLayout.setHelperTextEnabled(false);
        }
    }

    // ------------------------------------------------------------------
    // Consumo dos comandos
    // ------------------------------------------------------------------

    private void tratarComandoRede(DadosPessoaisEffect comando) {
        if (comando == null) {
            return;
        }
        if (comando instanceof DadosPessoaisEffect.ErroDeRede erroDeRede) {
            AlertDialogUtils.dismiss();
            RedirectNetwork.checkRedirect(erroDeRede.getError(), this);
        } else if (comando instanceof DadosPessoaisEffect.Redirecionar redirecionar) {
            RedirectNetwork.checkRedirectSucesso(redirecionar.getRedirect(), this);
        }
        onEvent.dispatch(new DadosPessoaisEvent.ComandoRedeConsumido());
    }

    private void tratarComandoDialog(DadosPessoaisEffect comando) {
        if (comando == null) {
            return;
        }
        if (comando instanceof DadosPessoaisEffect.CamposObrigatoriosPendentes camposPendentes) {
            mostrarCamposPendentes(camposPendentes);
        } else if (comando instanceof DadosPessoaisEffect.MostrarAvisoNotificacao avisoNotificacao) {
            mostrarAvisoNotificacao(avisoNotificacao.isAtivado());
        } else if (comando instanceof DadosPessoaisEffect.ConfirmarSaidaParaCorreios) {
            confirmarSaidaParaCorreios();
        }
        onEvent.dispatch(new DadosPessoaisEvent.ComandoDialogConsumido());
    }

    private void tratarComandoAnalytics(DadosPessoaisEffect comando) {
        if (!(comando instanceof DadosPessoaisEffect.RegistrarEvento registrarEvento)) {
            return;
        }
        registrarEvento(registrarEvento);
        onEvent.dispatch(new DadosPessoaisEvent.ComandoAnalyticsConsumido());
    }

    private void tratarComandoNavegacao(DadosPessoaisEffect comando) {
        if (comando == null) {
            return;
        }
        onEvent.dispatch(new DadosPessoaisEvent.ComandoNavegacaoConsumido());

        if (comando instanceof DadosPessoaisEffect.CadastroAtualizado) {
            abrirConfirmacao();
        } else if (comando instanceof DadosPessoaisEffect.AbrirSiteCorreios) {
            Utils.abreUrl(this, R.string.Url_Correios);
        } else if (comando instanceof DadosPessoaisEffect.FecharTela) {
            finish();
        }
    }

    private void abrirConfirmacao() {
        DadosPessoaisState state = viewModel.getUiState().getValue();
        if (state != null && state.getLimiteDigitado() != null) {
            SessaoUsuario.getInstance()
                    .getParametrosSimulacao()
                    .setValorLimiteDiario(state.getLimiteDigitado());
        }
        startActivity(new Intent(this, DadosPessoaisConfirmacaoActivity.class));
        finish();
    }

    private void mostrarCamposPendentes(DadosPessoaisEffect.CamposObrigatoriosPendentes comando) {
        StringBuilder campos = new StringBuilder();
        for (DadosPessoaisEffect.CampoObrigatorio campo : comando.getCampos()) {
            campos.append(getString(R.string.barra_n)).append(nomeDoCampo(campo));
        }
        DialogUtils.dialogEntendi(
                this,
                getString(R.string.MA002) + getString(R.string.espaco_em_branco) + campos
        );
    }

    private String nomeDoCampo(DadosPessoaisEffect.CampoObrigatorio campo) {
        return switch (campo) {
            case UF -> getString(R.string.uf);
            case MUNICIPIO -> getString(R.string.municipio);
            default -> getString(R.string.CEP);
        };
    }

    private void mostrarAvisoNotificacao(boolean ativado) {
        String mensagem = ativado
                ? "Ao ativar esta opção, você receberá notificações importantes de loterias se"
                + " estiverem habilitadas no seu aparelho. A funcionalidade pode ser ativada ou"
                + " desativada a qualquer momento. Para salvar, clique em atualizar."
                : "Ao desativar esta opção, você não receberá mais notificações de loterias."
                + " Para salvar, clique em atualizar.";
        DialogUtils.dialogEntendi(this, mensagem);
    }

    private void confirmarSaidaParaCorreios() {
        DialogUtils.dialogTituloConfirmar(
                this,
                getString(R.string.nao_sei_cep),
                getString(R.string.voce_sera_redirecionado_para_o_site_dos_correios),
                (dialog, which) -> onEvent.dispatch(
                        new DadosPessoaisEvent.RedirecionamentoCorreiosConfirmado()
                )
        );
    }

    private void registrarEvento(DadosPessoaisEffect.RegistrarEvento comando) {
        switch (comando.getTipo()) {
            case ENTROU_EDITAR_CADASTRO:
                AppCenterManager.registraEvento(getString(R.string.evento_entrou_editar_cadastro));
                break;
            case EDICAO_CADASTRO_SUCESSO:
                AppCenterManager.registraEvento(getString(R.string.evento_edicao_cadastro_sucesso));
                break;
            case ENTROU_RECALCULAR_LIMITE:
            default:
                AppCenterManager.registraEvento("ENTROU_RECALCULAR_LIMITE_AUTORIZADO");
                break;
        }
    }

    // ------------------------------------------------------------------
    // Helpers de View
    // ------------------------------------------------------------------

    private void exibirCarregamento(boolean carregando) {
        if (carregando) {
            AlertDialogUtils.show(this);
        } else {
            AlertDialogUtils.dismiss();
        }
    }

    private void atualizarIconesSexo(int position) {
        SegmentedButton masculino = findViewById(R.id.btMasculino);
        SegmentedButton feminino = findViewById(R.id.btFeminino);
        boolean isMasculino = position == 0;
        masculino.setDrawable(isMasculino ? R.drawable.ic_check_circle : R.drawable.ic_circle_outline);
        feminino.setDrawable(isMasculino ? R.drawable.ic_circle_outline : R.drawable.ic_check_circle);
    }

    private void atualizarDescricaoNotificacoes(boolean isChecked) {
        String descricao = isChecked ? "Ativado: Interruptor" : "Desativado: Interruptor";
        String textoFinal = getString(R.string.label_notificacoes) + ": " + descricao;
        containerNotificacoes.setContentDescription(textoFinal);
        containerNotificacoes.post(() -> {
            AccessibilityManager accessibilityManager =
                    (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (accessibilityManager != null && accessibilityManager.isEnabled()) {
                containerNotificacoes.announceForAccessibility(textoFinal);
            }
        });
    }

    private void setTextSeDiferente(EditText editText, String texto) {
        String valorAtual = editText.getText() == null ? "" : editText.getText().toString();
        String novoValor = texto == null ? "" : texto;
        if (valorAtual.equals(novoValor)) {
            return;
        }
        editText.setText(novoValor);
        if (editText.hasFocus()) {
            editText.setSelection(novoValor.length());
        }
    }

    private String formatarMoeda(BigDecimal valor) {
        BigDecimal seguro = valor == null ? BigDecimal.ZERO : valor;
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(seguro);
    }

    protected void addLeftImg(EditText textField) {
        if (!textField.getText().toString().trim().isEmpty()) {
            textField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            TextViewCompat.setCompoundDrawableTintList(
                    textField,
                    ContextCompat.getColorStateList(
                            textField.getContext(), R.color.cinza_item_desabilitado
                    )
            );
            textField.setHintTextColor(
                    ContextCompat.getColor(textField.getContext(), R.color.cinza_item_desabilitado)
            );
        } else {
            textField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            TextViewCompat.setCompoundDrawableTintList(textField, null);
        }
    }

    /** TextWatcher enxuto para evitar boilerplate nos campos. */
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // sem uso
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // sem uso
        }
    }
}
