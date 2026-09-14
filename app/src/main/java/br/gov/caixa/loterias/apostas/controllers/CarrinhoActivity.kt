package br.gov.caixa.loterias.apostas.controllers

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
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
import br.gov.caixa.loterias.apostas.utils.FragmentUtils
import br.gov.caixa.loterias.apostas.utils.IntentUtil
import br.gov.caixa.loterias.apostas.utils.LocalizacaoUtils
import br.gov.caixa.loterias.apostas.utils.MessagingUtils
import br.gov.caixa.loterias.apostas.utils.PermissaoUtil
import br.gov.caixa.loterias.apostas.utils.RedirectUtils
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil
import br.gov.caixa.loterias.apostas.utils.ViewUtils
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrinhoApostasListViewAdapter
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener
import com.android.volley.VolleyError
import java.math.BigDecimal
import java.util.Timer
import java.util.TimerTask

class CarrinhoActivity : LoteriasBaseAppActivity() {
    var offSet: Int = 0
    private lateinit var toolbar: Toolbar
    private lateinit var dialogFavoritar: Dialog
    private lateinit var rvListaCarrinho: RecyclerView
    private lateinit var infoValorMinimo: TextView
    protected lateinit var vLoading: View
    private lateinit var etCarrinho: EditText
    private lateinit var btnAvancaPagamento: Button
    private lateinit var btnNovaAposta: Button
    private lateinit var btnLimpar: ImageButton
    private lateinit var btnFavoritar: ImageButton
    private var carrinho: CarrinhoDTO? = null
    private var lerCarrinhoLocal = false
    private var ocorrendoValidacao = false
    private var onResume = false
    private var bloqScroll = false
    private val listApostas = mutableListOf<IdentificaoDeUmaApostaDas8Modalidades<*>>()
    private lateinit var svCarrinho: ScrollView
    private lateinit var textCotasBolao: TextView
    private lateinit var textApostasIndividuais: TextView
    private lateinit var textComboApostas: TextView
    private lateinit var viewDivBottomBolao: View
    private lateinit var viewDivBottom: View
    private lateinit var viewDivBottomCombo: View

    private lateinit var timer: Timer
    private val handler = Handler()

    private lateinit var origem: String

    private lateinit var model: CarrinhoModel

    //fragments
    private lateinit var somadorCarrinhoFragment: SomadorCarrinhoFragment? = null
    private lateinit var titleToolbar: TextView
    private lateinit var customButton: ImageButton

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)
        model = CarrinhoModel(this@CarrinhoActivity)
        carrinho = CarrinhoSingleton.getInstance().carrinho
        pegaExtras()
        timerExcluiCotasExpiradas()
        setaViews()
        setaMetodos()
    }

    protected override fun onResume() {
        super.onResume()
        desabilitaBoloes()
        desabilitaApostas()
        desabilitaCombos()
        limparCarrinhoInicial()
        atualizaParametros()
    }

    private fun limparCarrinhoInicial() {
        offSet = 0
        listApostas.clear()
        rvListaCarrinho.adapter?.notifyDataSetChanged()
    }

    private fun postResume() {
        if (DadosUsuarioBO.checarUsuarioLogado(this)) {
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice()
        }
    }

    private fun lerCarrinho() {
        if (getIntent().getComponent()?.getClassName() == CarrinhoActivity::class.java.getName()) {
            lerCarrinhoLocal = false
            carrinho = null
        }
        if (lerCarrinhoLocal) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_carrinho_offline))
            lerCarrinhoLocal()
        } else {
            atualizaLayout()
        }
        handleBtnFavoritar()
    }

    fun lerCarrinhoLocal() {
        carrinho = model.lerCarrinhoLocal()
        atualizaLayout()
    }

    private fun handleBtnFavoritar() {
        if (!DadosUsuarioBO.checarUsuarioLogado(this)) {
            btnFavoritar.visibility = View.GONE
        } else {
            if (podeHabilitarFavoritar()) {
                btnFavoritar.visibility = View.VISIBLE
            }
        }
    }

    private fun podeHabilitarFavoritar(): Boolean {
        val carrinho = carrinho

        return !carrinho.getApostas().isNullOrEmpty() && carrinho.getBoloes()
            .isNullOrEmpty() && carrinho.getCombos().isNullOrEmpty()
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
        origem = requireNotNull(bundle.getString(ORIGEM)) {
            "Extra ORIGEM não informada"
        }
    }

    private fun setaViews() {
        viewDivBottom = findViewById(R.id.v_div_bottom)
        rvListaCarrinho = findViewById(R.id.listaCarrinho)

        infoValorMinimo = findViewById(R.id.tv_info_valor_minimo)
        btnAvancaPagamento = findViewById(R.id.btn_avancar_pagamento)
        btnNovaAposta = findViewById(R.id.btn_nova_aposta)
        toolbar = findViewById(R.id.toolbar)
        btnLimpar = findViewById(R.id.ib_excluir)
        btnFavoritar = findViewById(R.id.ib_favoritar)
        vLoading = findViewById(R.id.pb_loading)
        svCarrinho = findViewById(R.id.sv_carrinho)
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

    private fun configurarBack() {
        onBackPressedDispatcher().addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    setResult(ResultadoNavegacaoAposta.RESULT_RESET)
                    finish()
                }
            })

        customButton.setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setaMetodos() {
        btnAvancaPagamento.setOnClickListener { v: View? -> onClickAvancaPagamento() }
        btnNovaAposta.setOnClickListener { v: View? -> clickBotaoFazerNovaAposta() }
        btnLimpar.setOnClickListener { v: View? -> clickLimparCarrinhoLinearLayout() }
        btnFavoritar.setOnClickListener { v: View? -> onClickFavoritar() }

        val layoutManager = LinearLayoutManager(this)
        rvListaCarrinho.setLayoutManager(layoutManager)
        rvListaCarrinho.setAdapter(
            CarrinhoApostasListViewAdapter(
                this,
                listApostas as java.util.ArrayList<IdentificaoDeUmaApostaDas8Modalidades<*>?>?,
                this,
                true,
                model,
                false
            )
        )

        svCarrinho.viewTreeObserver?.addOnScrollChangedListener {
            val scrollY = svCarrinho.scrollY
            val view = svCarrinho.getChildAt(svCarrinho.size - 1)
            val diff = (view.bottom - (svCarrinho.height + scrollY))
            if (diff == FIM_SCROLL && !bloqScroll) {
                vLoading.visibility = View.VISIBLE
                bloqScroll = true
                Handler().postDelayed({
                    atualizaLayout()
                }, 1)
            }
        }
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
                vLoading.visibility = View.VISIBLE
                model.buscaCarrinhoSilce(object : OnSilceListener<CarrinhoDTO?> {
                    override fun success(payload: CarrinhoDTO?) {
                        vLoading.visibility = View.GONE
                        AlertDialogUtils.dismiss()
                        setCarrinho(payload)
                        if (payload?.getMsgCotaExcluida() != null && payload.getMsgCotaExcluida()) {
                            notificationCotasExcluidas()
                        }
                        trataPermissao()
                    }

                    override fun error(error: VolleyError?) {
                        vLoading.visibility = View.GONE
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
        val apostas = carrinho?.getApostas()?.size ?: 0
        val apostasIndividuais = carrinho?.getApostasIndividuais()?.size ?: 0
        val boloes = carrinho?.getBoloes()?.size ?: 0
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
        requestCode: Int, permissions: Array<String?>?, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions?, grantResults)
        if (requestCode == LocalizacaoUtils.PERMISSAO_LOCALIZACAO_COD) {
            if (grantResults.size > 0 && grantResults[0] >= 0) {
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
        try {
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
                if (DadosUsuarioBO.checarUsuarioLogado(this)) {
                    validarCarrinho()
                } else {
                    AlertDialogExperimenteLogarSingleton.show(this, true, null)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun deletaAposta(
        position: Int, aposta: IdentificaoDeUmaApostaDas8Modalidades<*>?, isBolao: Boolean
    ) {
        if (isBolao) {
            listBoloes?.removeAt(position)
            rvListaCarrinho.adapter?.notifyItemRemoved(position)
            setValorTotalCarrinho()
            configuraBotoesSuperiores()
            if (listBoloes.isEmpty()) {
                desabilitaBoloes()
            }
            return
        }
        if (apagouLotomaniaGerarEspelho(listApostas[position])) {
            listApostas.removeAt(position)
            rvListaCarrinho.adapter?.notifyDataSetChanged()
        } else {
            listApostas.removeAt(position)
            rvListaCarrinho.adapter?.notifyItemRemoved(position)
        }
        offSet--
        setValorTotalCarrinho()
        configuraBotoesSuperiores()
        if (listApostas.isEmpty()) {
            desabilitaApostas()
        }
    }

    private fun apagouLotomaniaGerarEspelho(aposta: IdentificaoDeUmaApostaDas8Modalidades<*>): Boolean {
        return aposta.getModalidade() == ModalidadeEnum.LOTOMANIA && aposta.gerarApostaEspelho
    }

    fun atualizaLayout() {
        try {
            if (temBolaoCarrinho()) {
                configuraRecyclerViewCarrinhoBoloes()

                fragmentSomadorCarrinho()
                habilitaBoloes()
            }
            if (temApostasIndividuaisCarrinho()) {
//                configuraRecyclerViewCarrinhoAposta(offSet)
                fragmentSomadorCarrinho()
                habilitaApostas()
            }
            if (temCombosCarrinho()) {
                configuraRecyclerViewCarrinhoCombos()
                fragmentSomadorCarrinho()
                habilitaCombos()
            }
            configuraBotoesSuperiores()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        montarLayout()
    }

    private fun callWebservice() {
        AlertDialogUtils.show(this@CarrinhoActivity)
        vLoading.visibility = View.VISIBLE
        model.buscaCarrinhoSilce(onCarrinhoListener())
    }

    private fun onCarrinhoListener(): OnSilceListener<CarrinhoDTO?> {
        return object : OnSilceListener<CarrinhoDTO?> {
            override fun success(payload: CarrinhoDTO?) {
                vLoading.visibility = View.GONE
                AlertDialogUtils.dismiss()

                setCarrinho(payload)
                atualizaLayout()

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
                vLoading.visibility = View.GONE
                AlertDialogUtils.dismiss()
            }
        }
    }

    private fun fragmentSomadorCarrinho() {
        if (somadorCarrinhoFragment == null) {
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(
                supportFragmentManager,
                somadorCarrinhoFragment,
                R.id.id_fg_somador_carrinho,
                "TELA CARRINHO"
            )
        } else {
            setValorTotalCarrinho()
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
                    this@CarrinhoActivity,
                    MensagensNetwork.getErrorMessage(error),
                    object : OnDialogBotaoListener {
                        override fun onButtonClick(dialog: DialogInterface?, which: Int) {
                            favoritarCarrinho(nome)
                        }
                    })
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
                    this@CarrinhoActivity, getString(R.string.carrinho_fav_lbl_msg),

                    object : OnDialogBotaoListener {
                        override fun onButtonClick(dialog: DialogInterface?, which: Int) {
                            dialogFavoritar.dismiss()
                        }
                    })
            }

            override fun error(error: VolleyError?) {
                AlertDialogUtils.dismiss()
            }
        }
    }

    protected fun clickBotaoFazerNovaAposta() {
        if (origem.equals(
                DetalhesComprasActivity.DETALHES_COMPRA_ACTIVITY, ignoreCase = true
            )
        ) {
            //PrincipalActivity_.intent(CarrinhoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
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
        DialogUtils.dialogSim(
            this@CarrinhoActivity, getString(R.string.MA011),

            OnDialogBotaoListener { dialog: DialogInterface?, which: Int ->
                AlertDialogUtils.show(this@CarrinhoActivity)
                model.limparCarrinho(onLimparCarrinhoListener())
            })
    }

    private fun onLimparCarrinhoListener(): OnSilceListener<*> {
        return object : OnSilceListener<Any?> {
            override fun success(payload: Any?) {
                AlertDialogUtils.dismiss()
                limparCarrinhoAtualizarTela()
                limparCarrinhosFavoritos()
                setValorTotalCarrinho()
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
        setValorTotalCarrinho()
    }

    private fun limparCarrinhoAtualizarTela() {
        offSet = 0

        carrinho?.let {
            it.getApostas()?.removeAll(it.getApostas())
            it.getApostasIndividuais().removeAll(it.getApostasIndividuais())
            if (temBolaoCarrinho()) {
                it.getBoloes().removeAll(it.getBoloes())
            }
            if (temCombosCarrinho()) {
                it.getCombos().removeAll(it.getCombos())
            }
            it.setValorTotal(BigDecimal.ZERO)
            CarrinhoSingleton.getInstance().setCarrinho(carrinho)
        }

        listApostas.removeAll(listApostas)
        rvListaCarrinho.adapter?.notifyDataSetChanged()
        desabilitaApostas()
        desabilitaBoloes()
        desabilitaCombos()
        atualizaLayout()
        configuraBotoesSuperiores()
    }

    private fun temBolaoCarrinho() =
        !carrinho?.getBoloes().isNullOrEmpty()

    private fun temApostasIndividuaisCarrinho() = !carrinho?.getApostasIndividuais().isNullOrEmpty()

    private fun temCombosCarrinho() = !carrinho?.getCombos().isNullOrEmpty()

    private fun montarLayout() {
        val sessaoUsuario = SessaoUsuario.getInstance()
        val valorCarrinho = sessaoUsuario.valorMinimimoAposta ?: BigDecimal.ZERO
        if (valorCarrinho != null && valorCarrinho > BigDecimal.ZERO) {
            ViewUtils.setMoedaFormatHtml(
                valorCarrinho,
                infoValorMinimo,
                getResources().getString(R.string.label_lembrar_valor_minimo).replace(
                    getResources().getString(R.string.underline),
                    getResources().getString(R.string.quebra_html)
                ) + getResources().getString(R.string.espaco_em_branco)
            )
            infoValorMinimo.visibility = View.VISIBLE
        } else {
            infoValorMinimo.visibility = View.GONE
        }
        btnAvancaPagamento.text = ViewUtils.textCaixaSTDBold(
            this, getString(R.string.avancarFormaPagamento)
        )
    }

    fun configuraRecyclerViewCarrinhoBoloes() {
        if (listBoloes != null && listBoloes.size == 0) {
            if (temBolaoCarrinho()) {
                listBoloes.addAll(carrinho?.getBoloes())
            }
        }
        vLoading.visibility = View.GONE
    }

    fun configuraRecyclerViewCarrinhoCombos() {
        if (listCombos != null && listCombos.size == 0) {
            if (temCombosCarrinho()) {
                listCombos.addAll(carrinho?.getCombos())
            }
        }
        vLoading.visibility = View.GONE
    }

//    fun configuraRecyclerViewCarrinhoAposta(offSet: Int) {
//        var cont: Int
//        var maximoLista = 0
//
//        val apostas: MutableList<IdentificaoDeUmaApostaDas8Modalidades<*>> =
//            ArrayList()
//        if (temApostasIndividuaisCarrinho()) {
//            if (offSet > carrinho?.getApostasIndividuais().size) {
//                vLoading.visibility = View.GONE
//                return
//            }
//            maximoLista = pegaMaximoLista()
//            cont = offSet
//            while (cont < maximoLista) {
//                val aposta: IdentificaoDeUmaApostaDas8Modalidades<*> =
//                    (if (carrinho.getApostasIndividuais()
//                            .get(cont) == null
//                    ) null else carrinho.getApostasIndividuais()[cont])?
//                if (aposta.getModalidade() != null) {
//                    apostas.add(aposta)
//                }
//                cont++
//            }
//        }
//
//        if (apostas != null) {
//            listApostas?.addAll(apostas)
//        }
//
//        if (listApostas != null) {
//            rvListaCarrinho?.getAdapter()?.notifyItemRangeInserted(offSet, maximoLista)
//            vLoading?.setVisibility(View.GONE)
//
//            this.offSet += PAGE_SIZE
//        }
//        bloqScroll = false
//    }

    private fun desabilitaBoloes() {
        textCotasBolao.visibility = View.GONE
        viewDivBottomBolao.visibility = View.GONE
    }

    private fun habilitaBoloes() {
        if (temBolaoCarrinho()) {
            textCotasBolao.visibility = View.VISIBLE
            viewDivBottomBolao.visibility = View.VISIBLE
        }
    }

    private fun desabilitaApostas() {
        textApostasIndividuais.visibility = View.GONE
        viewDivBottom.visibility = View.GONE
        rvListaCarrinho.visibility = View.GONE
    }

    private fun habilitaApostas() {
        if (!carrinho?.getApostasIndividuais().isNullOrEmpty()) {
            textApostasIndividuais.visibility = View.VISIBLE
            viewDivBottom.visibility = View.VISIBLE
            rvListaCarrinho.visibility = View.VISIBLE
        }
    }

    private fun desabilitaCombos() {
        textComboApostas.visibility = View.GONE
        viewDivBottomCombo.visibility = View.GONE
    }

    private fun habilitaCombos() {
        if (temCombosCarrinho()) {
            textComboApostas.visibility = View.VISIBLE
            viewDivBottomCombo.visibility = View.VISIBLE
        }
    }

    private fun configuraBotoesSuperiores() {
        val possuiItens = !carrinho?.getApostas().isNullOrEmpty() || !carrinho?.getBoloes()
            .isNullOrEmpty() || !carrinho?.getCombos().isNullOrEmpty()

        btnLimpar.visibility = if (possuiItens) View.VISIBLE else View.GONE

        btnFavoritar.visibility =
            if (possuiItens && DadosUsuarioBO.checarUsuarioLogado(this) && podeHabilitarFavoritar()) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun setValorTotalCarrinho() {
        carrinho?.setValorTotal(carrinho?.getValorTotal())
        if (somadorCarrinhoFragment == null) {
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(
                supportFragmentManager,
                somadorCarrinhoFragment,
                R.id.id_fg_somador_carrinho,
                "TELA CARRINHO"
            )
        }
        somadorCarrinhoFragment?.atualizaValorTotal(carrinho)
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

    private fun onParametroSimulacaoListener(): OnSilceListener<ParametrosSimulacao?> {
        return object : OnSilceListener<ParametrosSimulacao?> {
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
                handler.post(Runnable {
                    //Tarefa
                    if (temCotasExpiradas()) {
                        onResume()
                    }
                })
            }
        }
        timer.schedule(task, 0, 60000)
        //1 minuto
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun temCotasExpiradas(): Boolean {
        val carrinhoDTO = CarrinhoSingleton.getInstance().getCarrinho()
        if (carrinhoDTO == null || carrinhoDTO.getBoloes() == null || carrinhoDTO.getBoloes()
                .isEmpty()
        ) {
            return false
        }

        val listBolao = carrinhoDTO.getBoloes()

        for (bolao in listBolao) {
            val dataHoraExpiracao = bolao.reservaCotaBolao.dataHoraExpiracaoReserva
            if (dataHoraExpiracao != null || isNotEmpty()) {
                val tempo_restante_milesec = DateUtils.diffMillisSecondsTimerZone(dataHoraExpiracao)
                if (tempo_restante_milesec <= 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun notificationCotasExcluidas() {
        //Toast.makeText(this, getString(R.string.cotas_expiradas_excluidas_carrinho),
        //        Toast.LENGTH_SHORT).show();
        MessagingUtils.criaNotificacaoNova(
            this, "", this.getString(R.string.cotas_expiradas_excluidas_carrinho)
        )
    }

    companion object {
        const val ORIGEM: String = "ORIGEM"

        @JvmField
        var CARRINHO: String = "CARRINHO"

        @JvmField
        var LER_CARRINHO_LOCAL: String = "LER_CARRINHO_LOCAL"
        var FIM_SCROLL: Int = 0
        var PAGE_SIZE: Int = 10
    }
}