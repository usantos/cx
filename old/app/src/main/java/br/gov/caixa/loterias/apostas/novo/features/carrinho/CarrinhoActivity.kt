package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.controllers.AppIndisponivelActivity
import br.gov.caixa.loterias.apostas.controllers.CadastrarActivity
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel
import br.gov.caixa.loterias.apostas.model.sp.LoginSP
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper
import br.gov.caixa.loterias.apostas.utils.AppCenterManager
import br.gov.caixa.loterias.apostas.utils.DateUtils
import br.gov.caixa.loterias.apostas.utils.DialogUtils
import br.gov.caixa.loterias.apostas.utils.IntentUtil
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils
import br.gov.caixa.loterias.apostas.utils.MessagingUtils
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum
import br.gov.caixa.loterias.apostas.utils.PermissaoUtil
import br.gov.caixa.loterias.apostas.utils.RedirectUtils
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil
import br.gov.caixa.loterias.apostas.utils.SnackbarUtils
import br.gov.caixa.loterias.apostas.utils.ViewUtils
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper
import br.gov.caixa.loterias.apostas.view.activity.DetalhesBolaoActivity
import br.gov.caixa.loterias.apostas.view.activity.DetalhesComboActivity
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener
import com.android.volley.VolleyError
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.Timer
import java.util.TimerTask

open class CarrinhoActivity : LoteriasBaseAppActivity() {
    private lateinit var carrinhoAdapter: CarrinhoAdapter
    private lateinit var toolbar: AppBarLayout
    private lateinit var dialogFavoritar: Dialog
    private lateinit var rvListaCarrinho: RecyclerView
    private lateinit var etCarrinho: EditText
    private lateinit var btnAvancaPagamento: Button
    private lateinit var btnNovaAposta: Button
    private lateinit var btnLimpar: ImageButton
    private lateinit var btnFavoritar: ImageButton
    private var carrinho: CarrinhoDTO? = null
    private var lerCarrinhoLocal = false
    private var ocorrendoValidacao = false
    private var onResume = false
    private lateinit var timer: Timer
    private val handler = Handler(Looper.getMainLooper())
    private var origem: String = ""
    private lateinit var model: CarrinhoModel
    private lateinit var titleToolbar: TextView
    private lateinit var customButton: ImageButton
    private lateinit var tvValorTotalCarrinho: TextView
    private lateinit var tvQuantidadeCarrinho: TextView
    private lateinit var layoutCarrinhoVazio: View
    private lateinit var contentContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)
        model = CarrinhoModel(this@CarrinhoActivity)
        carrinho = CarrinhoSingleton.getInstance().carrinho
        pegaExtras()
        timerExcluiCotasExpiradas()
        setaViews()
        setupRecyclerView()
        setupListeners()

        callWebservice()
    }

    protected override fun onResume() {
        super.onResume()
        atualizaParametros()
    }

    private fun postResume() {
        if (deveSincronizarComBackend()) {
            lerCarrinhoLocal = false
        }
        if (ocorrendoValidacao && !onResume) {
            onResume = true
            DialogUtils.dialogEntendi(
                this@CarrinhoActivity,
                this@CarrinhoActivity.getResources()
                    .getString(R.string.label_verificar_valor_carrinho)
            )
        }
        lerCarrinho()
    }

    private fun deveSincronizarComBackend(): Boolean = DadosUsuarioBO.checarUsuarioLogado(this)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice()
        }
    }

    private fun atualizarEstadoCarrinho() {
        val possuiItens = carrinho?.possuiItensExibidos() == true

        rvListaCarrinho.visibility = if (possuiItens) View.VISIBLE else View.GONE

        layoutCarrinhoVazio.visibility = if (possuiItens) View.GONE else View.VISIBLE

        btnAvancaPagamento.isEnabled = carrinho?.possuiItensParaPagamento() == true
    }

    private fun lerCarrinho() {
        if (lerCarrinhoLocal) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_carrinho_offline))
            lerCarrinhoLocal()
        }
        atualizarEstadoBotoes()
    }

    private fun lerCarrinhoLocal() {
        setCarrinho(model.lerCarrinhoLocal())
        atualizarCarrinhoNaTela()
    }

    private fun handleBtnFavoritar() {
        btnFavoritar.visibility = if (deveSincronizarComBackend() && podeFavoritarCarrinho()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun handleBtnLimpar() {
        btnLimpar.visibility = if (carrinho?.possuiItensExibidos() == true) View.VISIBLE
        else View.GONE
    }

    private fun atualizarEstadoBotoes() {
        handleBtnFavoritar()
        handleBtnLimpar()
    }

    private fun setaDialogFavoritar() {
        dialogFavoritar = Dialog(this)
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text)

        val tituloDialog = dialogFavoritar.findViewById<TextView>(R.id.tv_titulo)
        tituloDialog.text = "Carrinho Favorito"

        etCarrinho = dialogFavoritar.findViewById<EditText>(R.id.et_carrinho)

        val btnOk = dialogFavoritar.findViewById<Button>(R.id.btn_ok)
        val btnCancelar = dialogFavoritar.findViewById<Button>(R.id.btn_cancelar)

        btnOk.setOnClickListener { v: View? ->
            val text = etCarrinho.text.toString()
            if (!text.isEmpty()) {
                validarCarrinhoFavorito(text)
            }
        }
        dialogFavoritar.setOnDismissListener { dialogInterface: DialogInterface? ->
            etCarrinho.setText("")
        }
        btnCancelar.setOnClickListener { v: View? -> dialogFavoritar.dismiss() }

        dialogFavoritar.window?.let {
            val screenWidth = getResources().displayMetrics.widthPixels
            val dialogWidth =
                (screenWidth * 0.85).toInt() // Define a largura da dialog como 85% da largura da tela

            it.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        }
    }

    private fun pegaExtras() {
        val bundle = intent.extras ?: return
        lerCarrinhoLocal = bundle.getBoolean(LER_CARRINHO_LOCAL)
        origem = bundle.getString(ORIGEM) ?: "Extra ORIGEM não informada"
    }

    private fun setaViews() {
        rvListaCarrinho = findViewById(R.id.listaCarrinho)
        layoutCarrinhoVazio = findViewById(R.id.layoutCarrinhoVazio)
        contentContainer = findViewById(R.id.contentContainer)

        btnAvancaPagamento = findViewById(R.id.btn_avancar_pagamento)
        btnNovaAposta = findViewById(R.id.btn_nova_aposta)
        toolbar = findViewById(R.id.toolbar)
        btnLimpar = findViewById(R.id.ib_excluir)
        btnFavoritar = findViewById(R.id.ib_favoritar)

        tvValorTotalCarrinho = findViewById(R.id.tvValorTotalCarrinho)
        tvQuantidadeCarrinho = findViewById(R.id.tvQuantidadeCarrinho)

        configurarToolbar()
        setaDialogFavoritar()
    }

    private fun configurarToolbar() {
        titleToolbar = findViewById(R.id.textCustom)
        titleToolbar.setText(R.string.carrinho_nde_apostas)
        ViewCompat.setAccessibilityHeading(titleToolbar, true)
        customButton = findViewById(R.id.customButton)
        customButton.contentDescription = getString(R.string.voltar)
        customButton.setFocusable(true)
        customButton.isFocusableInTouchMode = true
        customButton.requestFocus()
        btnLimpar.contentDescription = getString(R.string.excluir)
        btnFavoritar.contentDescription = getString(R.string.favoritar_carrinho)
        configurarBack()
    }

    private fun voltar() {
        setResult(ResultadoNavegacaoAposta.RESULT_RESET)
        finish()
    }

    private fun configurarBack() {
        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    voltar()
                }
            })
        customButton.setOnClickListener {
            voltar()
        }
    }

    private fun setupListeners() {
        btnAvancaPagamento.setOnClickListener { v: View? -> onClickAvancaPagamento() }
        btnNovaAposta.setOnClickListener { v: View? -> clickBotaoFazerNovaAposta() }
        btnLimpar.setOnClickListener { v: View? -> clickLimparCarrinhoLinearLayout() }
        btnFavoritar.setOnClickListener { v: View? -> onClickFavoritar() }
    }

    private fun setupRecyclerView() {
        carrinhoAdapter = CarrinhoAdapter(
            onExcluir = ::confirmarExclusaoAposta,
            onExcluirBolao = ::confirmarExclusaoBolao,
            onExcluirCombo = ::confirmarExclusaoCombo,
            onMaisDetalhes = ::abrirDetalhesBolao,
            onCotaExpirada = ::onCotaExpirada,
            onMaisDetalhesCombo = ::abrirDetalhesCombo
        )

        rvListaCarrinho.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carrinhoAdapter
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(false)
        }
    }

    private fun abrirDetalhesCombo(combo: ComboApostaDTO) {
        val bundle = Bundle().apply {
            putString(
                DetalhesComboActivity.ARG_COMBO_APOSTA_DTO, Gson().toJson(combo)
            )
        }

        val intent = IntentUtil.getIntentOrigemDestino(
            this@CarrinhoActivity, DetalhesComboActivity::class.java, bundle
        )

        startActivity(intent)
    }

    private fun onCotaExpirada(aposta: IdentificaoDeUmaApostaDas8Modalidades<*>) {
        deletaAposta(aposta)
        atualizaParametros()
    }

    private fun atualizarCarrinho() {
        atualizaParametros()
    }

    private fun ComboApostaDTO.calcularValorTotal(): BigDecimal =
        apostas.orEmpty().fold(BigDecimal.ZERO) { total, aposta ->
            total.add(aposta.valor ?: BigDecimal.ZERO)
        }

    private fun detalheCotaBolao(aposta: IdentificaoDeUmaApostaDas8Modalidades<*>) {
        val reserva = aposta.reservaCotaBolao ?: return

        val bundle = Bundle().apply {
            putString(
                DetalhesBolaoActivity.ARG_CODIGO_BOLAO, reserva.codigoBolaoReserva
            )
            putString(
                DetalhesBolaoActivity.ARG_MODALIDADE, Gson().toJson(aposta.modalidade)
            )
            putString(
                DetalhesBolaoActivity.ARG_MODO_VISUALIZACAO,
                Gson().toJson(ModoVisualizacaoBolaoEnum.LEITURA)
            )
            putString(
                DetalhesBolaoActivity.ARG_NUMERO_COTA,
                "${reserva.numeroCotaReservada}/${reserva.qtdCotaTotalBolao}"
            )
        }

        val intent = IntentUtil.getIntentOrigemDestino(
            this, DetalhesBolaoActivity::class.java, bundle
        )

        startActivity(intent)
    }

    private fun abrirDetalhesBolao(aposta: IdentificaoDeUmaApostaDas8Modalidades<*>) {
        detalheCotaBolao(aposta)
    }

    private fun onClickFavoritar() {
        DialogUtils.dialogEntendiListener(
            this@CarrinhoActivity, getString(R.string.loteca_nao_favorita)
        ) { dialog, which -> dialogFavoritar.show() }
    }

    private fun onClickAvancaPagamento() {
        logCart()
        if (LoginSP.isLoginRealizado() && RedirectUtils.temRedirectCadastrarApostador()) {
            startActivity(
                IntentUtil.getIntentOrigemDestino(
                    this@CarrinhoActivity, CadastrarActivity::class.java
                )
            )
            finish()
        } else if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(this, 2)) {
            if (temCotasExpiradas()) {
                AlertDialogUtils.show(this@CarrinhoActivity)
                model.buscaCarrinhoSilce(object : OnSilceListener<CarrinhoDTO?> {

                    override fun success(payload: CarrinhoDTO?) {
                        setCarrinho(payload)
                        atualizarCarrinhoNaTela()

                        if (payload?.getMsgCotaExcluida() == true) {
                            notificationCotasExcluidas()
                        }

                        trataPermissao()
                    }

                    override fun error(error: VolleyError?) {
                        AlertDialogUtils.dismiss()
                        trataPermissao()
                    }
                })
            } else {
                trataPermissao()
            }
        }
    }

    private fun logCart() {
        val total = ViewUtils.getMoedaFormat(carrinho?.getValorTotal())
        val apostas = carrinho?.apostas?.size ?: 0
        val apostasIndividuais = carrinho?.apostasIndividuais?.size ?: 0
        val boloes = carrinho?.boloes?.size ?: 0
        AnalyticsHelper.getInstance().logInteraction(
            AnalyticsHelper.EventCategoryParams.CTA,
            AnalyticsHelper.EventActionParams.CLICK,
            AnalyticsHelper.EventLabelParams.PAYMENT,
            AnalyticsHelper.JourneyParams.APOSTAR,
            AnalyticsHelper.SubJourneyParams.CARRINHO,
            AnalyticsHelper.Tela.BETS_CART,
            total,
            apostas.toString(),
            apostasIndividuais.toString(),
            boloes.toString()
        )
    }

    private fun trataPermissao() {
        LocalizacaoUtils.checaStatusPermissaoAsync(this) { result: Int ->
            if (result == LocalizacaoUtils.LOC_NAO_PERMITIDA || result == LocalizacaoUtils.LOC_NEG_PERMANENTEMENTE || result == LocalizacaoUtils.LOC_DESATIVADA) {
                DialogUtils.dialogSimNao(
                    this@CarrinhoActivity,
                    getString(R.string.posso_consultar_sua_localizacao),
                    object : OnDialogDoisBotoesListener {
                        override fun PositiveButton(dialog: DialogInterface?, which: Int) {
                            if (result == LocalizacaoUtils.LOC_NAO_PERMITIDA) {
                                PermissaoUtil.checkList(
                                    this@CarrinhoActivity,
                                    PermissaoUtil.getListaLocalizacaoPermissoes(),
                                    123
                                )
                                LocalizacaoUtils.incrementContLocation()
                            } else if (result == LocalizacaoUtils.LOC_NEG_PERMANENTEMENTE) {
                                LocalizacaoUtils.intentConfigPermissao(this@CarrinhoActivity)
                            } else if (result == LocalizacaoUtils.LOC_DESATIVADA) {
                                LocalizacaoUtils.intentConfigGps(this@CarrinhoActivity)
                            }
                        }

                        override fun NegativeButton(dialog: DialogInterface?, which: Int) {
                            showAlertLocalizacao()
                        }
                    })
            } else {
                if (result == LocalizacaoUtils.LOC_NO_BRASIL) {
                    onFluxoLocalizacaoNoBrasil()
                } else {
                    showAlertLocalizacao()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String?>, grantResults: IntArray, deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == LocalizacaoUtils.PERMISSAO_LOCALIZACAO_COD) {
            if (grantResults.isNotEmpty() && grantResults[0] >= 0) {
                trataPermissao()
            } else {
                if (LocalizacaoUtils.checaNegadaDefinitivamente(this@CarrinhoActivity)) {
                    LocalizacaoUtils.intentConfigPermissao(this@CarrinhoActivity)
                } else {
                    showAlertLocalizacao()
                }
            }
        }
    }

    private fun showAlertLocalizacao() {
        DialogUtils.dialogEntendiListener(
            this@CarrinhoActivity, getString(R.string.alerta_precisa_estar_no_brasil)
        ) { dialog, which -> //PrincipalActivity_.intent(CarrinhoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
            val intent = IntentUtil.getIntentOrigemDestino(
                this@CarrinhoActivity, PrincipalActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun onFluxoLocalizacaoNoBrasil() {
        if (model.existeCompraMesmoValorEm24horas(carrinho?.getValorTotal())) {
            //modal
            DialogUtils.dialogSimNao(
                this@CarrinhoActivity,
                getString(R.string.compra_existente_24_horas),
                object : OnDialogDoisBotoesListener {
                    override fun PositiveButton(dialog: DialogInterface?, which: Int) {
                        // sem ação, apenas fecha o dialog
                    }

                    override fun NegativeButton(dialog: DialogInterface?, which: Int) {
                        if (DadosUsuarioBO.checarUsuarioLogado(this@CarrinhoActivity)) {
                            validarCarrinho()
                        } else {
                            AlertDialogExperimenteLogarSingleton.show(
                                this@CarrinhoActivity, true, null
                            )
                        }
                    }
                })
        } else {
            if (deveSincronizarComBackend()) {
                validarCarrinho()
            } else {
                AlertDialogExperimenteLogarSingleton.show(this, true, null)
            }
        }
    }

    private fun callWebservice() {
        AlertDialogUtils.show(this@CarrinhoActivity)
        model.buscaCarrinhoSilce(onCarrinhoListener())
    }

    private fun onCarrinhoListener(): OnSilceListener<CarrinhoDTO?> {
        return object : OnSilceListener<CarrinhoDTO?> {
            override fun success(payload: CarrinhoDTO?) {
                AlertDialogUtils.dismiss()

                setCarrinho(payload)
                atualizarCarrinhoNaTela()

                if (payload != null) {
                    AnalyticsHelper.getInstance().logViewCartScreen(
                        AnalyticsHelper.JourneyParams.APOSTAR,
                        AnalyticsHelper.SubJourneyParams.CARRINHO,
                        AnalyticsHelper.Tela.BETS_CART,
                        payload
                    )
                }

                if (payload?.getMsgCotaExcluida() != null && payload.getMsgCotaExcluida()) {
                    notificationCotasExcluidas()
                }
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()
            }
        }
    }

    fun setCarrinho(carrinho: CarrinhoDTO?) {
        this.carrinho = carrinho
        CarrinhoSingleton.getInstance().setCarrinho(this.carrinho)
    }

    private fun validarCarrinho() {
        ocorrendoValidacao = true
        AlertDialogUtils.show(this@CarrinhoActivity)
        model.validarCarrinho(onValidarCarrinhoListener())
    }

    private fun onValidarCarrinhoListener(): OnSilceListener<*> {
        return object : OnSilceListener<Any?> {
            override fun success(payload: Any?) {
                ocorrendoValidacao = false
                AlertDialogUtils.dismiss()
                CarrinhoSingleton.getInstance().setCarrinho(carrinho)
                if (carrinho != null) {
                    AnalyticsHelper.getInstance().logFlowEnd(
                        AnalyticsHelper.JourneyParams.APOSTAR,
                        AnalyticsHelper.JourneyParams.APOSTAR,
                        AnalyticsHelper.SubJourneyParams.CARRINHO,
                        AnalyticsHelper.Tela.BETS_CART,
                        carrinho
                    )
                }
                startActivity(Intent(this@CarrinhoActivity, FormaPagamentoActivity::class.java))
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode != 401) {
                    ocorrendoValidacao = false
                }
            }
        }
    }

    private fun validarCarrinhoFavorito(nome: String?) {
        AlertDialogUtils.show(this@CarrinhoActivity)
        model.validarCarrinhoFavorito(nome, onValidarCarrinhoFavorito(nome))
    }

    private fun onValidarCarrinhoFavorito(nome: String?): OnSilceListener<*> {
        return object : OnSilceListener<Any?> {
            override fun success(payload: Any?) {
                AlertDialogUtils.dismiss()
                favoritarCarrinho(nome)
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()

                DialogUtils.dialogSim(
                    this@CarrinhoActivity, MensagensNetwork.getErrorMessage(error)
                ) { dialog, which -> favoritarCarrinho(nome) }
            }
        }
    }

    private fun favoritarCarrinho(nome: String?) {
        AlertDialogUtils.show(this@CarrinhoActivity)
        model.favoritarCarrinho(nome, onFavoritarCarrinhoListener())
    }

    private fun onFavoritarCarrinhoListener(): OnSilceListener<*> {
        return object : OnSilceListener<Any?> {
            override fun success(payload: Any?) {
                AlertDialogUtils.dismiss()

                DialogUtils.dialogEntendiListener(
                    this@CarrinhoActivity, getString(R.string.carrinho_fav_lbl_msg)

                ) { dialog, which -> dialogFavoritar.dismiss() }
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()
            }
        }
    }

    private fun clickBotaoFazerNovaAposta() {
        if (origem.equals(
                DetalhesComprasActivity.DETALHES_COMPRA_ACTIVITY, ignoreCase = true
            )
        ) {
            val intent = IntentUtil.getIntentOrigemDestino(
                this@CarrinhoActivity, PrincipalActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            setResult(ResultadoNavegacaoAposta.RESULT_RESET)
            finish()
        }
    }

    private fun clickLimparCarrinhoLinearLayout() {
        DialogUtils.dialogDoisBotoesPersonalizados(
            this@CarrinhoActivity,
            getString(R.string.excluir_todas_apostas_titulo),
            getString(R.string.todas_as_apostas),
            getString(R.string.manter_aposta),
            getString(R.string.excluir),
            object : OnDialogDoisBotoesListener {

                override fun PositiveButton(
                    dialog: DialogInterface?, which: Int
                ) {

                }

                override fun NegativeButton(
                    dialog: DialogInterface?, which: Int
                ) {
                    AlertDialogUtils.show(this@CarrinhoActivity)
                    model.limparCarrinho(onLimparCarrinhoListener())
                }
            })
    }

    private fun onLimparCarrinhoListener(): OnSilceListener<*> {
        return object : OnSilceListener<Any?> {
            override fun success(payload: Any?) {
                AlertDialogUtils.dismiss()
                limparCarrinhoAtualizarTela()
                limparCarrinhosFavoritos()
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()
                deleteCarrinhoLocal()
            }
        }
    }

    private fun limparCarrinhosFavoritos() {
        model.limparCarrinhosFavoritosLocal()
    }

    private fun deleteCarrinhoLocal() {
        model.deletaCarrinhoLocal()
        limparCarrinhoAtualizarTela()
    }

    private fun limparCarrinhoAtualizarTela() {
        carrinho?.apply {
            apostas?.clear()
            apostasIndividuais?.clear()
            boloes?.clear()
            combos?.clear()
            valorTotal = BigDecimal.ZERO
        }
        CarrinhoSingleton.getInstance().setCarrinho(carrinho)
        atualizarCarrinhoNaTela()
    }

    private fun atualizaParametros() {
        AlertDialogUtils.show(this)
        if (SessaoUsuarioUtil.precisaAtualizar()) {
            model.buscaParametroSiumulacao(onParametroSimulacaoListener())
        } else {
            postResume()
            AlertDialogUtils.dismiss()
        }
    }

    private fun onParametroSimulacaoListener(): OnSilceListener<ParametrosSimulacao> {
        return object : OnSilceListener<ParametrosSimulacao> {
            override fun success(payload: ParametrosSimulacao) {
                AlertDialogUtils.dismiss()

                val parametros = payload.parametros
                if (parametros == null || parametros.isEmpty()) {
                    val appIndis =
                        Intent(this@CarrinhoActivity, AppIndisponivelActivity::class.java)
                    appIndis.putExtra(getResources().getString(R.string.extra_is_busca_param), true)

                    startActivityForResult(appIndis, 1)
                }

                SessaoUsuarioUtil.atualizaParametrosSingleton(payload)
                postResume()
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()
                postResume()
            }
        }
    }

    fun timerExcluiCotasExpiradas() {
        timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    //Tarefa
                    if (temCotasExpiradas()) {
                        atualizarCarrinho()
                    }
                }
            }
        }
        timer.schedule(task, 0, 60000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun temCotasExpiradas(): Boolean {
        val boloes = CarrinhoSingleton.getInstance().carrinho?.boloes.orEmpty()

        return boloes.any { bolao ->
            bolao.reservaCotaBolao?.dataHoraExpiracaoReserva?.let(DateUtils::diffMillisSecondsTimerZone)
                ?.let { it <= 0 } ?: false
        }
    }

    private fun notificationCotasExcluidas() {
        MessagingUtils.criaNotificacaoNova(
            this, "", this.getString(R.string.cotas_expiradas_excluidas_carrinho)
        )
    }

    private fun deletaCombo(combo: ComboApostaDTO) {
        if (deveSincronizarComBackend()) {
            deletaComboRemoto(combo)
        } else {
            deletaComboLocal(combo)
        }
    }

    private fun deletaComboLocal(combo: ComboApostaDTO) {
        val carrinhoAtual = carrinho ?: return

        val valorCombo = combo.calcularValorTotal()

        val removido = carrinhoAtual.combos?.remove(combo) == true

        if (!removido) {
            return
        }

        carrinhoAtual.valorTotal =
            (carrinhoAtual.valorTotal ?: BigDecimal.ZERO).subtract(valorCombo).max(BigDecimal.ZERO)

        atualizarCarrinhoNaTela()
    }

    private fun deletaComboRemoto(combo: ComboApostaDTO) {
        AlertDialogUtils.show(this)

        model.deletaComboCarrinho(
            combo, object : OnSilceListener<CarrinhoDTO?> {

                override fun success(payload: CarrinhoDTO?) {
                    callWebservice()
                }

                override fun error(error: VolleyError?) {
                    AlertDialogUtils.dismiss()
                }
            })
    }

    private fun atualizarCarrinhoNaTela() {
        carrinhoAdapter.submitList(
            carrinho?.let(CarrinhoMapper::map).orEmpty()
        )

        atualizarResumoCarrinho()
        atualizarEstadoCarrinho()
        atualizarEstadoBotoes()
    }

    private fun podeFavoritarCarrinho(): Boolean {
        val carrinho = carrinho ?: return false

        return carrinho.possuiItensExibidos() && carrinho.boloes.isNullOrEmpty()
    }

    private fun CarrinhoDTO.quantidadeItensExibidos(): Int =
        boloes.orEmpty().size + apostasIndividuais.orEmpty().size + combos.orEmpty().size

    private fun CarrinhoDTO.possuiItensExibidos(): Boolean = quantidadeItensExibidos() > 0

    private fun CarrinhoDTO.possuiItensParaPagamento(): Boolean =
        possuiItensExibidos() && (valorTotal ?: BigDecimal.ZERO) > BigDecimal.ZERO

    private fun atualizarResumoCarrinho() {
        tvValorTotalCarrinho.text = ViewUtils.getMoedaFormat(
            carrinho?.valorTotal ?: BigDecimal.ZERO
        )

        tvQuantidadeCarrinho.text = (carrinho?.quantidadeItensExibidos() ?: 0).toString()
    }

    private fun confirmarExclusaoAposta(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        DialogUtils.dialogDoisBotoesPersonalizados(
            this@CarrinhoActivity,
            getString(R.string.excluir_aposta_titulo),
            getString(R.string.excluir_aposta_mensagem),
            getString(R.string.manter_aposta),
            getString(R.string.excluir),

            object : OnDialogDoisBotoesListener {

                override fun PositiveButton(
                    dialog: DialogInterface?, which: Int
                ) {

                }

                override fun NegativeButton(
                    dialog: DialogInterface?, which: Int
                ) {
                    deletaAposta(aposta)
                }
            })
    }

    private fun confirmarExclusaoBolao(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        DialogUtils.dialogDoisBotoesPersonalizados(
            this@CarrinhoActivity,
            getString(R.string.excluir_bolao_titulo),
            getString(R.string.excluir_bolao_mensagem),
            getString(R.string.manter_bolao),
            getString(R.string.excluir),
            object : OnDialogDoisBotoesListener {

                override fun PositiveButton(
                    dialog: DialogInterface?, which: Int
                ) {

                }

                override fun NegativeButton(
                    dialog: DialogInterface?, which: Int
                ) {
                    deletaAposta(aposta)
                }
            })
    }

    private fun confirmarExclusaoCombo(
        combo: ComboApostaDTO
    ) {
        DialogUtils.dialogDoisBotoesPersonalizados(
            this@CarrinhoActivity,
            getString(R.string.excluir_combo_titulo),
            getString(R.string.excluir_combo_mensagem),
            getString(R.string.manter_combo),
            getString(R.string.excluir),

            object : OnDialogDoisBotoesListener {

                override fun PositiveButton(
                    dialog: DialogInterface?, which: Int
                ) {

                }

                override fun NegativeButton(
                    dialog: DialogInterface?, which: Int
                ) {
                    deletaCombo(combo)
                }
            })
    }

    private fun deletaApostaLocal(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {

        model.deletarApostaLocal(aposta)

        setCarrinho(model.lerCarrinhoLocal())
        atualizarCarrinhoNaTela()

        SnackbarUtils.showSuccess(
            anchorView = contentContainer, message = getString(R.string.aposta_removida_carrinho)
        )
    }

    private fun deletaAposta(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        if (deveSincronizarComBackend()) {
            deletaApostaRemota(aposta)
        } else {
            deletaApostaLocal(aposta)
        }
    }

    private fun deletaApostaRemota(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        AlertDialogUtils.show(this)

        model.deletaApostaCarrinho(
            aposta, object : OnSilceListener<CarrinhoDTO?> {

                override fun success(payload: CarrinhoDTO?) {
                    SnackbarUtils.showSuccess(
                        anchorView = contentContainer,
                        message = getString(R.string.aposta_removida_carrinho)
                    )
                    callWebservice()
                }

                override fun error(error: VolleyError?) {
                    AlertDialogUtils.dismiss()

                    SnackbarUtils.showError(
                        anchorView = contentContainer,
                        message = getString(R.string.aposta_nao_removida_carrinho)
                    )
                }
            })
    }

    companion object {
        const val ORIGEM: String = "ORIGEM"

        @JvmField
        var CARRINHO: String = "CARRINHO"

        @JvmField
        var LER_CARRINHO_LOCAL: String = "LER_CARRINHO_LOCAL"
    }
}