package br.gov.caixa.loterias.apostas.novo.features.dadospessoais

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.controllers.DadosPessoaisConfirmacaoActivity
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils
import br.gov.caixa.loterias.apostas.utils.AppCenterManager
import br.gov.caixa.loterias.apostas.utils.DialogUtils
import br.gov.caixa.loterias.apostas.utils.Utils
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButton
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import com.github.pinball83.maskededittext.MaskedEditText as MaskedEditTextLib
import br.gov.caixa.loterias.apostas.view.components.MaskedEditText as MaskedEditTextApp

open class DadosPessoaisActivity : LoteriasBaseAppActivity() {

    private companion object {
        val LOCALE_BR: Locale = Locale("pt", "BR")
        const val TAMANHO_CEP = 8
        val APENAS_NUMEROS = Regex("\\D")
    }

    private lateinit var editTextLimiteLayout: TextInputLayout
    private lateinit var editTextCEPLayout: TextInputLayout
    private lateinit var editTextNomeContent: EditText
    private lateinit var editTextUFContent: EditText
    private lateinit var editTextMunicipioContent: EditText
    private lateinit var editTextEmailContent: EditText
    private lateinit var editTextLimiteContent: EditText
    private lateinit var limiteTitulo: MaterialTextView
    private lateinit var txtValidaCep: MaterialTextView
    private lateinit var validaLimiteMinContent: MaterialTextView
    private lateinit var validaLimiteMaxContent: MaterialTextView
    private lateinit var labelCep: MaterialTextView
    private lateinit var editTextCpfContent: MaskedEditTextLib
    private lateinit var editTextCepContent: MaskedEditTextApp
    private lateinit var segmentedButtonSexo: SegmentedButtonGroup
    private lateinit var updateDadosPessoaisButton: Button
    private lateinit var naoSeiCepButton: Button
    private lateinit var updateDadosPessoaisLimiteButton: Button
    private lateinit var notificacaoSwitch: CheckBox
    private lateinit var containerNotificacoes: LinearLayout

    private lateinit var viewModel: DadosPessoaisViewModel

    private lateinit var onEvent: DadosPessoaisEvent.Dispatcher

    private var bloqueiaListeners = false

    private var ultimoCepEnviado = ""
    private var cepPreenchido = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dados_pessoais)

        viewModel = ViewModelProvider(this)[DadosPessoaisViewModel::class.java]
        onEvent = DadosPessoaisEvent.Dispatcher { event -> viewModel.onEvent(event) }

        setaViews()
        configurarInsets()
        setaMetodos()
        observarViewModel()
    }

    override fun baseConsumeInsets(): Boolean = false

    override fun onResume() {
        super.onResume()
        onEvent.dispatch(DadosPessoaisEvent.TelaRetomada)
    }

    override fun onSupportNavigateUp(): Boolean {
        onEvent.dispatch(DadosPessoaisEvent.VoltarClicado)
        return true
    }

    private fun configurarInsets() {
        val footer = findViewById<View>(R.id.footerDadosPessoais)
        val scroll = findViewById<View>(R.id.scrollViewDadosPessoais)
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeOnly = ime.bottom - sys.bottom
            val needed = maxOf(0, imeOnly - footer.height)
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, needed)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setaViews() {
        editTextCpfContent = findViewById(R.id.editTextCpfContent)
        editTextNomeContent = findViewById(R.id.editTextNomeContent)
        editTextCepContent = findViewById(R.id.editTextCepContent)
        editTextCEPLayout = findViewById(R.id.editTextCEP)
        editTextUFContent = findViewById(R.id.editTextUFContent)
        editTextMunicipioContent = findViewById(R.id.editTextMunicipioContent)
        editTextEmailContent = findViewById(R.id.editTextEmailContent)
        editTextLimiteContent = findViewById(R.id.editTextLimiteContent)
        editTextLimiteLayout = findViewById(R.id.editTextLimite)
        limiteTitulo = findViewById(R.id.limite_titulo)
        labelCep = findViewById(R.id.labelCep)
        txtValidaCep = findViewById(R.id.txt_valida_cep)
        validaLimiteMinContent = findViewById(R.id.txt_valida_limite_min)
        validaLimiteMaxContent = findViewById(R.id.txt_valida_limite_max)
        segmentedButtonSexo = findViewById(R.id.segmentedButtonSexo)
        updateDadosPessoaisButton = findViewById(R.id.updateDadosPesoaisButton)
        naoSeiCepButton = findViewById(R.id.naoSeiCep)
        updateDadosPessoaisLimiteButton = findViewById(R.id.updateDadosPessoaisLimiteButton)
        notificacaoSwitch = findViewById(R.id.notificacao_switch)
        containerNotificacoes = findViewById(R.id.containerNotificacoes)

        configurarToolbar()
        atualizarDescricaoNotificacoes(notificacaoSwitch.isChecked)
    }

    private fun configurarToolbar() {
        val textTitulo = findViewById<TextView>(R.id.textCustom)
        textTitulo.hint = getString(R.string.titulo)
        textTitulo.text = getString(R.string.title_dados_pessoais)

        val customButton = findViewById<ImageButton>(R.id.customButton)
        customButton.contentDescription = getString(R.string.voltar)
        customButton.setOnClickListener {
            onEvent.dispatch(DadosPessoaisEvent.VoltarClicado)
        }
        customButton.isFocusable = true
        customButton.isFocusableInTouchMode = true
        customButton.requestFocus()
    }

    private fun setaMetodos() {
        editTextMunicipioContent.isClickable = false
        editTextUFContent.isClickable = false

        updateDadosPessoaisButton.setOnClickListener {
            onEvent.dispatch(DadosPessoaisEvent.AtualizarClicado)
        }

        updateDadosPessoaisLimiteButton.setOnClickListener {
            onEvent.dispatch(DadosPessoaisEvent.RecalcularLimiteClicado)
        }

        naoSeiCepButton.setOnClickListener {
            onEvent.dispatch(DadosPessoaisEvent.NaoSeiCepClicado)
        }

        segmentedButtonSexo.setOnPositionChangedListener { position ->
            atualizarIconesSexo(position)
            if (!bloqueiaListeners) {
                onEvent.dispatch(DadosPessoaisEvent.SexoSelecionado(position))
            }
        }

        notificacaoSwitch.setOnCheckedChangeListener { _, isChecked ->
            atualizarDescricaoNotificacoes(isChecked)
            if (!bloqueiaListeners) {
                onEvent.dispatch(DadosPessoaisEvent.NotificacaoAlterada(isChecked))
            }
        }

        containerNotificacoes.setOnClickListener { notificacaoSwitch.toggle() }

        editTextCepContent.addTextChangedListener(criarTextWatcherCep())
        editTextLimiteContent.addTextChangedListener(criarTextWatcherLimite())
    }

    private fun criarTextWatcherCep(): TextWatcher {
        return object : SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                if (bloqueiaListeners) {
                    return
                }
                val digitos = APENAS_NUMEROS.replace(editable?.toString().orEmpty(), "")

                if (digitos.length < TAMANHO_CEP) {
                    ultimoCepEnviado = ""
                    return
                }
                if (digitos == ultimoCepEnviado && !telaEmErroDeCep()) {
                    return
                }
                ultimoCepEnviado = digitos
                onEvent.dispatch(DadosPessoaisEvent.CepAlterado(digitos))
            }
        }
    }

    private fun telaEmErroDeCep(): Boolean {
        return viewModel.uiState.value?.erroCep != null
    }

    private fun criarTextWatcherLimite(): TextWatcher {
        return object : SimpleTextWatcher() {
            override fun afterTextChanged(editable: Editable?) {
                if (bloqueiaListeners) {
                    return
                }
                onEvent.dispatch(
                    DadosPessoaisEvent.LimiteAlterado(editable?.toString().orEmpty())
                )
            }
        }
    }

    private fun observarViewModel() {
        viewModel.uiState.observe(this) { state -> renderizar(state) }
        viewModel.comandos.observe(this) { comandos -> tratarComandos(comandos) }
    }

    private fun renderizar(state: DadosPessoaisState?) {
        if (state == null) {
            return
        }
        exibirCarregamento(state.carregando)

        state.apostador?.let { renderizarApostador(state, it) }
        renderizarLocalizacao(state)
        renderizarLimite(state)

    }

    private fun renderizarApostador(state: DadosPessoaisState, apostador: ApostadorDTO) {
        bloqueiaListeners = true
        try {
            editTextCpfContent.setMaskedText(apostador.cpf)
            editTextNomeContent.setText(apostador.nome)
            editTextEmailContent.setText(apostador.email)
            preencherCepInicial(apostador)
            notificacaoSwitch.isChecked = state.aceitaNoticias
            segmentedButtonSexo.setPosition(state.posicaoSexo)
            atualizarIconesSexo(state.posicaoSexo)
        } finally {
            bloqueiaListeners = false
        }
    }

    private fun preencherCepInicial(apostador: ApostadorDTO) {
        val cep = apostador.cep
        if (cepPreenchido || cep == null) {
            return
        }
        editTextCepContent.setText(cep)
        ultimoCepEnviado = APENAS_NUMEROS.replace(cep, "")
        cepPreenchido = true
    }

    private fun renderizarLocalizacao(state: DadosPessoaisState) {
        editTextUFContent.setText(state.uf.orEmpty())
        editTextMunicipioContent.setText(state.municipio.orEmpty())

        if (state.erroCep != null) {
            labelCep.setTextColor(getColor(R.color.vermelho_erro))
            editTextCEPLayout.boxStrokeColor = getColor(R.color.vermelho_erro)
            txtValidaCep.visibility = View.VISIBLE
            txtValidaCep.text = getString(R.string.cep_invalido)
            editTextUFContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            editTextMunicipioContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            return
        }
        labelCep.setTextColor(getColor(R.color.cinza))
        editTextCEPLayout.boxStrokeColor = getColor(R.color.cinza_item_desabilitado)
        editTextCEPLayout.isHelperTextEnabled = false
        txtValidaCep.visibility = View.GONE

        if (state.cepValido) {
            addLeftImg(editTextUFContent)
            addLeftImg(editTextMunicipioContent)
        }
    }

    private fun renderizarLimite(state: DadosPessoaisState) {
        val apostador = state.apostador ?: return

        val valorLimiteMin = formatarMoeda(apostador.limiteMinimoDiario)
        val valorLimiteMax = formatarMoeda(apostador.limiteDiarioAutorizado)

        validaLimiteMinContent.text = getString(R.string.restringe_limite_ao_minimo, valorLimiteMin)
        validaLimiteMaxContent.text = getString(R.string.restringe_limite_ao_maximo, valorLimiteMax)
        updateDadosPessoaisLimiteButton.text = getString(R.string.dados_limite_compra, valorLimiteMax)

        validaLimiteMinContent.visibility =
            if (state.limiteMenorQueMinimo) View.VISIBLE else View.GONE
        validaLimiteMaxContent.visibility =
            if (state.limiteMaiorQueMaximo) View.VISIBLE else View.GONE

        bloqueiaListeners = true
        try {
            setTextSeDiferente(editTextLimiteContent, formatarMoeda(state.limiteDigitado))
        } finally {
            bloqueiaListeners = false
        }

        if (state.limiteValido) {
            limiteTitulo.setTextColor(getColor(R.color.cinza))
            editTextLimiteLayout.boxStrokeColor = getColor(R.color.cinza_item_desabilitado)
            editTextLimiteLayout.isHelperTextEnabled = true
            editTextLimiteLayout.helperText = "$valorLimiteMin até $valorLimiteMax"
        } else {
            limiteTitulo.setTextColor(getColor(R.color.vermelho_erro))
            editTextLimiteLayout.boxStrokeColor = getColor(R.color.vermelho_erro)
            editTextLimiteLayout.isHelperTextEnabled = false
        }
    }
    private fun tratarComandos(comandos: DadosPessoaisComandos?) {
        if (comandos == null) {
            return
        }
        tratarComandoRede(comandos.rede)
        tratarComandoDialog(comandos.dialog)
        tratarComandoAnalytics(comandos.analytics)
        tratarComandoNavegacao(comandos.navegacao)
    }

    private fun tratarComandoRede(comando: DadosPessoaisEffect?) {
        if (comando == null) {
            return
        }
        when (comando) {
            is DadosPessoaisEffect.ErroDeRede -> {
                AlertDialogUtils.dismiss()
                RedirectNetwork.checkRedirect(comando.error, this)
            }

            is DadosPessoaisEffect.Redirecionar ->
                RedirectNetwork.checkRedirectSucesso(comando.redirect, this)

            else -> Unit
        }
        onEvent.dispatch(DadosPessoaisEvent.ComandoRedeConsumido)
    }

    private fun tratarComandoDialog(comando: DadosPessoaisEffect?) {
        if (comando == null) {
            return
        }
        when (comando) {
            is DadosPessoaisEffect.CamposObrigatoriosPendentes ->
                mostrarCamposPendentes(comando)

            is DadosPessoaisEffect.MostrarAvisoNotificacao ->
                mostrarAvisoNotificacao(comando.ativado)

            is DadosPessoaisEffect.ConfirmarSaidaParaCorreios ->
                confirmarSaidaParaCorreios()

            is DadosPessoaisEffect.FalhaAoCarregarDados ->
                DialogUtils.dialogEntendi(
                    this,
                    getString(R.string.erro_carregar_dados)
                )
            else -> Unit
        }
        onEvent.dispatch(DadosPessoaisEvent.ComandoDialogConsumido)
    }

    private fun tratarComandoAnalytics(comando: DadosPessoaisEffect?) {
        if (comando !is DadosPessoaisEffect.RegistrarEvento) {
            return
        }
        registrarEvento(comando)
        onEvent.dispatch(DadosPessoaisEvent.ComandoAnalyticsConsumido)
    }

    private fun tratarComandoNavegacao(comando: DadosPessoaisEffect?) {
        if (comando == null) {
            return
        }
        onEvent.dispatch(DadosPessoaisEvent.ComandoNavegacaoConsumido)

        when (comando) {
            is DadosPessoaisEffect.CadastroAtualizado -> abrirConfirmacao()
            is DadosPessoaisEffect.AbrirSiteCorreios -> Utils.abreUrl(this, R.string.Url_Correios)
            is DadosPessoaisEffect.FecharTela -> finish()
            else -> Unit
        }
    }

    private fun abrirConfirmacao() {
        viewModel.uiState.value?.limiteDigitado?.let { limite ->
            SessaoUsuario.getInstance().getParametrosSimulacao()?.let { parametros ->
                parametros.valorLimiteDiario = limite
            }
        }
        startActivity(Intent(this, DadosPessoaisConfirmacaoActivity::class.java))
        finish()
    }

    private fun mostrarCamposPendentes(comando: DadosPessoaisEffect.CamposObrigatoriosPendentes) {
        val campos = comando.campos.joinToString("") { campo ->
            getString(R.string.barra_n) + nomeDoCampo(campo)
        }
        DialogUtils.dialogEntendi(
            this,
            getString(R.string.MA002) + getString(R.string.espaco_em_branco) + campos
        )
    }

    private fun nomeDoCampo(campo: DadosPessoaisEffect.CampoObrigatorio): String {
        return when (campo) {
            DadosPessoaisEffect.CampoObrigatorio.UF -> getString(R.string.uf)
            DadosPessoaisEffect.CampoObrigatorio.MUNICIPIO -> getString(R.string.municipio)
            DadosPessoaisEffect.CampoObrigatorio.CEP -> getString(R.string.CEP)
        }
    }

    private fun mostrarAvisoNotificacao(ativado: Boolean) {
        val mensagem = if (ativado) {
            "Ao ativar esta opção, você receberá notificações importantes de loterias se" +
                " estiverem habilitadas no seu aparelho. A funcionalidade pode ser ativada ou" +
                " desativada a qualquer momento. Para salvar, clique em atualizar."
        } else {
            "Ao desativar esta opção, você não receberá mais notificações de loterias." +
                " Para salvar, clique em atualizar."
        }
        DialogUtils.dialogEntendi(this, mensagem)
    }

    private fun confirmarSaidaParaCorreios() {
        DialogUtils.dialogTituloConfirmar(
            this,
            getString(R.string.nao_sei_cep),
            getString(R.string.voce_sera_redirecionado_para_o_site_dos_correios)
        ) { _, _ ->
            onEvent.dispatch(DadosPessoaisEvent.RedirecionamentoCorreiosConfirmado)
        }
    }

    private fun registrarEvento(comando: DadosPessoaisEffect.RegistrarEvento) {
        when (comando.tipo) {
            DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_EDITAR_CADASTRO ->
                AppCenterManager.registraEvento(getString(R.string.evento_entrou_editar_cadastro))

            DadosPessoaisEffect.RegistrarEvento.Tipo.EDICAO_CADASTRO_SUCESSO ->
                AppCenterManager.registraEvento(getString(R.string.evento_edicao_cadastro_sucesso))

            DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_RECALCULAR_LIMITE ->
                AppCenterManager.registraEvento("ENTROU_RECALCULAR_LIMITE_AUTORIZADO")
        }
    }

    private fun exibirCarregamento(carregando: Boolean) {
        if (carregando) {
            AlertDialogUtils.show(this)
        } else {
            AlertDialogUtils.dismiss()
        }
    }

    private fun atualizarIconesSexo(position: Int) {
        val masculino = findViewById<SegmentedButton>(R.id.btMasculino)
        val feminino = findViewById<SegmentedButton>(R.id.btFeminino)
        val isMasculino = position == 0
        masculino.setDrawable(
            if (isMasculino) R.drawable.ic_check_circle else R.drawable.ic_circle_outline
        )
        feminino.setDrawable(
            if (isMasculino) R.drawable.ic_circle_outline else R.drawable.ic_check_circle
        )
    }

    private fun atualizarDescricaoNotificacoes(isChecked: Boolean) {
        val descricao = if (isChecked) "Ativado: Interruptor" else "Desativado: Interruptor"
        val textoFinal = getString(R.string.label_notificacoes) + ": " + descricao
        containerNotificacoes.contentDescription = textoFinal
        containerNotificacoes.post {
            val accessibilityManager =
                getSystemService(ACCESSIBILITY_SERVICE) as? AccessibilityManager
            if (accessibilityManager?.isEnabled == true) {
                containerNotificacoes.announceForAccessibility(textoFinal)
            }
        }
    }

    private fun setTextSeDiferente(editText: EditText, texto: String?) {
        val valorAtual = editText.text?.toString().orEmpty()
        val novoValor = texto.orEmpty()
        if (valorAtual == novoValor) {
            return
        }
        editText.setText(novoValor)
        if (editText.hasFocus()) {
            editText.setSelection(novoValor.length)
        }
    }

    private fun formatarMoeda(valor: BigDecimal?): String {
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor ?: BigDecimal.ZERO)
    }

    protected fun addLeftImg(textField: EditText) {
        if (textField.text.toString().isNotBlank()) {
            textField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
            TextViewCompat.setCompoundDrawableTintList(
                textField,
                ContextCompat.getColorStateList(
                    textField.context, R.color.cinza_item_desabilitado
                )
            )
            textField.setHintTextColor(
                ContextCompat.getColor(textField.context, R.color.cinza_item_desabilitado)
            )
        } else {
            textField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            TextViewCompat.setCompoundDrawableTintList(textField, null)
        }
    }

    private abstract class SimpleTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Intencionalmente vazio
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Intencionalmente vazio
        }
    }
}
