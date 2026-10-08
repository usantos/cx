package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.OrientacaoPixActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.WebViewActivity;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOIdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepresaResponse;
import br.gov.caixa.loterias.apostas.model.model.ApostaConfirmadaModel;
import br.gov.caixa.loterias.apostas.model.model.DetalhesCompraModel;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.SituacaoCompraUtil;
import br.gov.caixa.loterias.apostas.utils.Tupla;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.FiltroApostasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaComprasDetalhesAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.CopiaColaPixFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogTresBotoesListener;

public class DetalhesComprasActivity extends LoteriasBaseAppActivity implements OnCopiaColaListener {

    public static final String DETALHES_COMPRA_ACTIVITY = "DETALHES_COMPRA_ACTIVITY";

    public static final int AMBOS = 0;
    public static final int NORMAL = 1;
    public static final int ESPECIAL = 2;

    public static Boolean isRunning = false;
    private CompraDTO compraDTO;
    private ArrayList<ApostaDTO> listaAposta = new ArrayList<>();
    private ArrayList<ApostaDTO> listaFiltrada = new ArrayList<>();;
    private ArrayList<String> tiposApostas = new ArrayList<>();
    private Dialog dialogFavoritar, dialogFiltrar;
    private ExpandableListView listViewFiltro;
    private ConstraintLayout listViewCompras;
    private ListaComprasDetalhesAdapter listaComprasDetalhesAdapter;
    private FiltroApostasAdapter filtroAdapter;
    private EditText etCarrinho;
    private ImageButton btnFiltro, btnDuvidas;
    private RecyclerView recyclerDetalhesApostas;
    private Boolean mantemSurpresinhas = true;
    private TextView tvBadge;
    private CopiaColaPixFragment fragCopiaCola;
    int ordemCompra;
    long tempo_acabar_pix;
    private Dialog dialogDevolucaoPix;
    private ImageButton ibFavoritar;
    private String dataHoraServidor;
    private ConfigConsultaDTO configConsulta;
    private List<ModalidadeDTO> modalidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compras);
        setaViews();
        pegaExtras();
        setaMetodos();
    }

    private void setaViews(){
        btnFiltro = findViewById(R.id.ib_filtrar);
        btnDuvidas = findViewById(R.id.ib_duvidas);
        recyclerDetalhesApostas = findViewById(R.id.gridviewDetalhesCompras);
        tvBadge = findViewById(R.id.tv_badge);
        listViewCompras = findViewById(R.id.list_group_compras);

        btnFiltro.setVisibility(View.VISIBLE);
        btnDuvidas.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_detalhes_compras))));
        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_detalhes_compras))));
    }

    private void setaMetodos(){
        btnFiltro.setOnClickListener(view -> onFiltroListener());
        btnDuvidas.setOnClickListener(view -> abrirTermosUso());

        buscaConfigConsulta(false);
        criarCabecalho();
        //verificaPixCopiaCola();//Chamado de dentro do buscaDetalhesCompras pois nele atualiza o compraDTO
        verificaCompraProcessada();
        buscaDetalhesCompras();
        setaDialogFavoritar();
        buscaModalidades(false);
    }

    private void onFiltroListener() {
        if (dialogFiltrar != null && !dialogFiltrar.isShowing()){
            dialogFiltrar.show();
        } else if (modalidades != null && configConsulta != null){
            setaDialogFiltrar();
            dialogFiltrar.show();
        } else {
            carregarFiltro();
        }
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String compraDTOJson = bundle.getString("CompraDTO");
            ordemCompra = bundle.getInt("ordem");
            compraDTO = new Gson().fromJson(compraDTOJson, CompraDTO.class);
            tempo_acabar_pix = bundle.getLong("tempoPix", 0l);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    private void verificaStatusPopUp() {
        if (SituacaoCompraUtil.isCancelada(compraDTO.getSituacao())) {
            if (MeioPagamentoUtil.isPix(compraDTO.getMeioPagamento())){
                showDialogCompraCancelada(getString(R.string.pix_cancelado_descricao));
            } else {
                showDialogCompraCancelada(getString(R.string.pagamento_pix_nao_identificado));
            }
        } else if(SituacaoCompraUtil.isCanceladaPix(compraDTO.getSituacao())){
            showDialogCompraCancelada(getString(R.string.pagamento_pix_nao_identificado));
        } else if (SituacaoCompraUtil.isDebitoNaoAutorizado(compraDTO.getSituacao())) {
            showDialogDebitoNaoAutorizado();
        } else if (SituacaoCompraUtil.isEstornada(compraDTO.getSituacao())) {
            //showDialogEstornada();
            showDialogCompraCancelada(getString(R.string.pagamento_pix_estornado));
//        } else if (SituacaoCompraUtil.isDevocaoPix(compraDTO.getSituacao())) {
//            showDialogDevolucaoPix();
        }else if(SituacaoCompraUtil.isRepresada(compraDTO.getSituacao())){
            verificaHorarioRepresa();
        }
    }

    private void showDialogCompraCancelada() {
        if (!contemBolao()){
            DialogUtils.dialogTresBotoes(
                    DetalhesComprasActivity.this,
                    "Atenção",
                    getResources().getString(R.string.mensagem_compra_cancelada_dialog),
                    "Salvar Carrinho",
                    "Ajuda",
                    "Cancelar",
                    new OnDialogTresBotoesListener() {
                        @Override
                        public void TopButton(DialogInterface dialog, int which) {
                            if (dialogFavoritar != null) {
                                dialogFavoritar.show();
                            } else {
                                setaDialogFavoritar();
                                dialogFavoritar.show();
                            }
                        }

                        @Override
                        public void CenterButton(DialogInterface dialog, int which) {
                            abrirAjuda();
                        }

                        @Override
                        public void BottomButton(DialogInterface dialog, int which) {

                        }
                    }

            );
        }
    }

    private void showDialogCompraCancelada(String descricao) {
        if (!contemBolao()){
            DialogUtils.dialogTresBotoes(
                    DetalhesComprasActivity.this,
                    "Atenção",
                    descricao,
                    "Salvar Carrinho",
                    "Ajuda",
                    "Cancelar",
                    new OnDialogTresBotoesListener() {
                        @Override
                        public void TopButton(DialogInterface dialog, int which) {
                            if (dialogFavoritar != null) {
                                dialogFavoritar.show();
                            } else {
                                setaDialogFavoritar();
                                dialogFavoritar.show();
                            }
                        }

                        @Override
                        public void CenterButton(DialogInterface dialog, int which) {
                            abrirAjuda();
                        }

                        @Override
                        public void BottomButton(DialogInterface dialog, int which) {

                        }
                    }
            );

        }
    }

    private boolean contemBolao() {
        if (listaAposta != null){
            for(ApostaDTO aposta : listaAposta){
                if (aposta.getIndicadorCotaBolao()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void verificaHorarioRepresa(){
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosSilceBO.getInstance().validaRepresa(new RequestListener<RepresaResponse>() {
            @Override
            public void onResponse(RepresaResponse response) {
                loadViewProgress.dismiss();
                if(response.getPayload().getPayload()){
                    DialogUtils.dialogEntendi(DetalhesComprasActivity.this,response.getPayload().getMensagem());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                Log.e("DetalhesComprasAct", "Erro ao consultar o serviço de validar represa");
            }
        });
    }


    private void showDialogEstornada() {
        DialogUtils.dialogTresBotoes(DetalhesComprasActivity.this,
                getString(R.string.popup_pix_nao_autorizado),
                getString(R.string.popup_pix_nao_autorizado_descricao),
                getString(R.string.gerar_novo_codigo),
                getString(R.string.alterar_forma_pagamento),
                getString(R.string.btn_title_voltarAoInicio), onDialogListener());

    }

    private void showDialogDevolucaoPagamento() {
        DialogUtils.dialogDoisBotoesPersonalizados(
                DetalhesComprasActivity.this,
                getString(R.string.popup_pix_estorno_pagamento_devolvido),
                getString(R.string.popup_pix_estorno_nao_possivel),
                getString(R.string.tenta_novamente_dialog),
                getString(R.string.fechar),
                new OnDialogDoisBotoesListener() {
                    @Override
                    public void PositiveButton(DialogInterface dialog, int which) {
                        //PrincipalActivity_.intent(DetalhesComprasActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
                        Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, PrincipalActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void NegativeButton(DialogInterface dialog, int which) {
                        //Sem ação apenas fecha a modal
                    }
                }

        );
    }

    private void showDialogDebitoNaoAutorizado() {
        if (MeioPagamentoUtil.isPix(compraDTO.getMeioPagamento())){
            DialogUtils.dialogTresBotoes(this,
                    getString(R.string.popup_pix_nao_realizado),
                    getString(R.string.popup_pix_nao_realizado_descricao),
                    getString(R.string.gerar_novo_codigo),
                    getString(R.string.alterar_forma_pagamento),
                    getString(R.string.btn_title_voltarAoInicio),
                    onDialogListener());
        } else {
            showDialogCompraCancelada();
        }

    }

    private OnDialogTresBotoesListener onDialogListener() {
        return new OnDialogTresBotoesListener() {
            @Override
            public void TopButton(DialogInterface dialog, int which) {
                //cancelContagem();
                Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, OrientacaoPixActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void CenterButton(DialogInterface dialog, int which) {
                //cancelContagem();
                Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, FormaPagamentoActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void BottomButton(DialogInterface dialog, int which) {
                //cancelContagem();
                //PrincipalActivity_.intent(DetalhesComprasActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
                Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, PrincipalActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        };
    }

    private void tentarNovamente() {
//        if (ApostaUtils.temModalidadeNormaleEspecialAberta(listaAposta)){
//            DialogUtils.showDialogSegmentedControl(DetalhesComprasActivity.this,
//                                                   ApostaUtils.getNomeModalidadeNormal(listaAposta),
//                                                   ApostaUtils.getNomeModalidadeEspecial(listaAposta),
//                                                   onReapostaDialogListener());
//        } else {
            realizaReaposta();
//        }
    }

    private void realizaReaposta() {
        AlertDialogUtils.show(this);
        new DetalhesCompraModel(this).realizaReaposta(listaAposta, onReapostaListener());
    }

    private OnSilceListener<AgrupadorDTOIdentificaoDeUmaApostaDas8Modalidades> onReapostaListener() {
        return new OnSilceListener<>() {
            @Override
            public void success(AgrupadorDTOIdentificaoDeUmaApostaDas8Modalidades payload) {
                AlertDialogUtils.dismiss();
                Intent intent = IntentUtil.getIntentOrigemDestino(DetalhesComprasActivity.this, CarrinhoActivity.class);
                intent.putExtra(CarrinhoActivity.ORIGEM, DETALHES_COMPRA_ACTIVITY);
                startActivity(intent);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }


    private void abrirAjuda() {
        if (compraDTO.getMeioPagamento() != null) {
            Intent activity = new Intent(DetalhesComprasActivity.this, WebViewActivity.class);

            if (MeioPagamentoUtil.isMercadoPago(compraDTO.getMeioPagamento())) {
                activity.putExtra(getString(R.string.bundle_key_url), getString(R.string.url_ajuda_mercado_pago)); //MercadoPago
            }
            if (MeioPagamentoUtil.isRecargaPay(compraDTO.getMeioPagamento())) {
                activity.putExtra("url", getString(R.string.url_ajuda_recarga_pay)); //RecargaPay
            }

            startActivity(activity);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            buscaDetalhesCompras();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void criarCabecalho() {
        //Desabilita componentes
        ImageView imageViewRelogio = findViewById(R.id.imageViewRelogio);
        imageViewRelogio.setVisibility(View.GONE);
        TextView contadorDown = findViewById(R.id.contadorDown);
        contadorDown.setVisibility(View.GONE);
        Button btnMinhasCompras = findViewById(R.id.btnMinhasCompras);
        btnMinhasCompras.setVisibility(View.GONE);
        FrameLayout setaIndicador = findViewById(R.id.setaComprasGroup);
        setaIndicador.setVisibility(View.INVISIBLE);

        TextView dataCompra = findViewById(R.id.dataCompraTextView);
        TextView horacompra = findViewById(R.id.horaCompraTextView);
        if(compraDTO.getDataFinalizacaoCompra() != null){
            dataCompra.setText("Data: " + compraDTO.getDataFinalizacaoCompra());
            horacompra.setText(getString(R.string.hora_dois_pontos) + compraDTO.getHoraFinalizacaoCompra());
        }

        TextView textTotal = findViewById(R.id.txtTotal);
        textTotal.setText(ViewUtils.textCaixaSTDBold(this, this.getString(R.string.label_compra_lista_valor)));

        TextView valorCompra = findViewById(R.id.valorCompra);
        valorCompra.setText(ViewUtils.getMoedaFormat(compraDTO.getValorTotal()));

        TextView situacaoCompraTextView = findViewById(R.id.situacaoCompraTextView);
        situacaoCompraTextView.setText(ViewUtils.textCaixaSTDBold(this, this.getString(R.string.label_compra_lista_situacao_descricao,
                SituacaoCompraUtil.getDescricao(compraDTO))));

        if (SituacaoCompraUtil.isSituacaoPositiva(compraDTO.getSituacao())) {
            situacaoCompraTextView.setTextColor(getResources().getColor(R.color.azul_minhas_compras));
        } else if (SituacaoCompraUtil.isSituacaoNegativa(compraDTO.getSituacao())) {
            situacaoCompraTextView.setTextColor(getResources().getColor(R.color.vermelho_minhas_compras));
        } else if (SituacaoCompraUtil.isSituacaoProcessamento(compraDTO.getSituacao())) {
            situacaoCompraTextView.setTextColor(getResources().getColor(R.color.amarelo_minhas_compras));
        }

        if (MeioPagamentoUtil.isPix(compraDTO.getMeioPagamento()) &&
                SituacaoCompraUtil.isSituacaoPositiva(compraDTO.getSituacao())) {
            TextView meioDePagamento = findViewById(R.id.meioDePagamento);
            meioDePagamento.setText(getString(R.string.id_pagamento)+"\n"+compraDTO.getE2eId());
        }

        ibFavoritar = listViewCompras.findViewById(R.id.ib_favoritar);
        ibFavoritar.setVisibility(View.VISIBLE);
        ibFavoritar.setOnClickListener(v -> {
                    configuraVisibilidadeMantSurpresinha(listaAposta);
                });
    }

    private void verificaPixCopiaCola() {
        if (MeioPagamentoUtil.isPix(compraDTO.getMeioPagamento()) &&
            SituacaoCompraUtil.isSituacaoAguardandoPagamento(compraDTO.getSituacao())) {

            startFragment();
        }
    }

    private void startFragment() {
        GerarPixDTO gerarPixDTO = new GerarPixDTO();
        gerarPixDTO.setPixCopiaECola(compraDTO.getPixCopiaECola());
        gerarPixDTO.setDataHoraExpiracao(compraDTO.getDataHoraExpiracaoPagamento());
        gerarPixDTO.setDetalhesCompras(true);
        fragCopiaCola = FragmentUtils.startCopiaColaPix(gerarPixDTO, dataHoraServidor, getSupportFragmentManager(), R.id.fragmentCopCalaPixDetalhe);
    }

    private void verificaCompraProcessada() {
        if (compraDTO.getMeioPagamento() == null) {
            ConstraintLayout constraintLayout = findViewById(R.id.compraPossesadaLaout);
            constraintLayout.setVisibility(View.GONE);
        } else {
//            AppCompatImageView imageView = findViewById(R.id.compraProcessadaImage);
//            if (MeioPagamentoUtil.isMercadoPago(compraDTO.getMeioPagamento())) {
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.mercadopago));
//            }
//            if (MeioPagamentoUtil.isRecargaPay(compraDTO.getMeioPagamento())) {
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.recargapay));
//            }
//            if (MeioPagamentoUtil.isPix(compraDTO.getMeioPagamento())) {
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pix));
//            }
            TextView txtMeioDePagamento = findViewById(R.id.compraPossesada);
            txtMeioDePagamento.setText(ViewUtils.textCaixaSTDBold(this,
                    getString(R.string.label_compra_lista_meio_de_pagamento,
                            MeioPagamentoUtil.getDescricao(compraDTO.getMeioPagamento()))));
        }
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void buscaDetalhesCompras() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        //ApostaSilceBO.getInstance().getDetalhesCompras(compraDTO.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
        ServicoFactoryUtil.getComprasLegadoService().getDetalhesCompras(compraDTO.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                dataHoraServidor = response.getDataHoraServidor();
                loadViewProgress.dismiss();
                AppCenterManager.registraEvento("ENTROU_COMPRAS_DETALHE");

                //Atualizando pois quando recebeu os parametros nao recebeu o pixCopiaECola
                compraDTO = response.getPayload().getCompra();
                verificaPixCopiaCola();

                listaAposta = (ArrayList<ApostaDTO>) response.getPayload().getApostas();

                tiposApostas = new ArrayList<>();
                for(ApostaDTO aposta : listaAposta){
                    tiposApostas.add(aposta.getModalidade().toString());
                    if (aposta.getIndicadorCotaBolao()) {
                        ibFavoritar.setVisibility(View.GONE);
                    }
                }

                configuraAdapter(listaAposta);
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalhesComprasActivity.this);
                }
                verificaStatusPopUp();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                configuraAdapter(listaAposta);
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

    private void configuraVisibilidadeMantSurpresinha(List<ApostaDTO> apostas){
        dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
        dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        for (ApostaDTO aposta: apostas) {
            if  (   aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA ||
                    aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA_NUMERICA) {
                dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.VISIBLE);
                dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.VISIBLE);
                break;
            }
        }
        dialogFavoritar.show();
    }

    private void setaDialogFavoritar() {
        dialogFavoritar = new Dialog(this);
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);

        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
        tituloDialog.setText("Carrinho Favorito");

        EditText etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);

        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);

        CheckBox ckManterSurpresinha = dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas);
        ckManterSurpresinha.setOnCheckedChangeListener((compoundButton, b) -> mantemSurpresinhas = b);

        btnOk.setOnClickListener(v -> {
            String text  = etCarrinho.getText().toString();
            if(text != null && !text.isEmpty() && compraDTO != null && compraDTO.getId() != null){
                validarCarrinhoFavorito(text,compraDTO.getId());
            }
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            etCarrinho.setText("");
            dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
            dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        });

        if(dialogFavoritar.getWindow() != null){

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.85); // Define a largura da dialog como 85% da largura da tela

            dialogFavoritar.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogFavoritar.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }
    }

    private void setaDialogFiltrar() {
        dialogFiltrar = new Dialog(this);
        dialogFiltrar.setContentView(R.layout.dialog_filtro_compras);
        listViewFiltro = dialogFiltrar.findViewById(R.id.elv_filtros);
        Button btnOk = dialogFiltrar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFiltrar.findViewById(R.id.btn_cancelar);
        ImageButton btnLimpar = dialogFiltrar.findViewById(R.id.ib_filtro_limpar);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogFiltrar.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        btnOk.setOnClickListener(v -> filtrarDados());
        btnCancelar.setOnClickListener(v -> dialogFiltrar.dismiss());
        dialogFiltrar.getWindow().setAttributes(lp);

        //final List<String>                  expandableListTitle;
        //final HashMap<String, List<String>> expandableListDetail;
        listViewFiltro.setGroupIndicator(null);
        //expandableListDetail = ExpandableListDataFiltro.getData();
        //expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        filtroAdapter = new FiltroApostasAdapter(this,
                                                 modalidades,
                                                 DetalhesComprasActivity.this,
                                                 false,
                                                 configConsulta);
        listViewFiltro.setAdapter(filtroAdapter);
        btnLimpar.setOnClickListener(v -> {
            filtroAdapter.limpaFiltros();
            dialogFiltrar.dismiss();
            tvBadge.setText(String.valueOf(0));
            tvBadge.setVisibility(View.GONE);
            configuraAdapter(listaAposta);
        });
    }

    private void configuraAdapter(List<ApostaDTO>  listaAposta) {
        if(listaAposta != null){
            recyclerDetalhesApostas.setLayoutManager(new LinearLayoutManager(this));
            listaComprasDetalhesAdapter = new ListaComprasDetalhesAdapter(this, this, listaAposta);
            recyclerDetalhesApostas.setAdapter(listaComprasDetalhesAdapter);
        }
    }

    private void filtrarDados() {
        if (listaAposta != null) {
            listaFiltrada = (ArrayList<ApostaDTO>) listaAposta.clone();
            Tupla<List<ApostaDTO>,Boolean> tuplaConcurso;
            Tupla<List<ApostaDTO>,Boolean> tuplaModalidade;
            Tupla<List<ApostaDTO>,Boolean> tuplaSituacao;

            Boolean isAddBadge = false;
            if(filtroAdapter != null){
                tuplaConcurso = filtroAdapter.getApostasFiltradasConcurso(listaAposta);
                tuplaSituacao = filtroAdapter.getApostasFiltradasSituacao(listaAposta);
                tuplaModalidade = filtroAdapter.getApostasFiltradasModalidade(listaAposta);

                if(tuplaConcurso.y){
                    isAddBadge = true;
                    listaFiltrada.retainAll(tuplaConcurso.x);
                }
                if(tuplaSituacao.y){
                    isAddBadge = true;
                    listaFiltrada.retainAll(tuplaSituacao.x);
                }
                if(tuplaModalidade.y){
                    isAddBadge = true;
                    listaFiltrada.retainAll(tuplaModalidade.x);
                }
            }

            if(!isAddBadge){
                tvBadge.setVisibility(View.GONE);
                configuraAdapter(listaAposta);
            } else {
                tvBadge.setVisibility(View.VISIBLE);
                configuraAdapter(listaFiltrada);
            }

        }
        dialogFiltrar.dismiss();
    }

    private void validarCarrinhoFavorito(String nome, Long idFavorito){
        AlertDialogUtils.show(DetalhesComprasActivity.this);
        ServicoFactoryUtil.getApostaService().validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                AlertDialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), DetalhesComprasActivity.this);
                }
                favoritarCarrinho(nome,idFavorito);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                DialogUtils.dialogSim(DetalhesComprasActivity.this,
                        MensagensNetwork.getErrorMessage(error),

                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                favoritarCarrinho(nome,idFavorito);
                            }
                        }
                );
            }
        });
    }

    private void favoritarCarrinho(String nome, Long idFavorito) {
        AlertDialogUtils.show(DetalhesComprasActivity.this);
        ServicoFactoryUtil.getApostaService().salvarCarrinhoFavorito(nome,idFavorito.toString(), mantemSurpresinhas, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                AlertDialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), DetalhesComprasActivity.this);
                }
                DialogUtils.dialogEntendiListener(DetalhesComprasActivity.this,
                        getString(R.string.carrinho_fav_lbl_msg),

                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                dialogFavoritar.dismiss();
                                startActivity(new Intent(DetalhesComprasActivity.this, CarrinhosFavoritosActivity.class));
                            }
                        }
                );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

    private void carregarFiltro() {
        if (modalidades == null){
            buscaModalidades(true);
        }
        if (configConsulta == null){
            buscaConfigConsulta(true);
        }
    }

    private void buscaModalidades(boolean showFiltro) {
        AlertDialogUtils.show(DetalhesComprasActivity.this);
        new ModalidadeModel(DetalhesComprasActivity.this)
                .buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
                    @Override
                    public void success(List<ModalidadeDTO> payload) {
                        AlertDialogUtils.dismiss();

                        if(payload != null){
                            modalidades = payload;
                        }

                        if (showFiltro){
                            onFiltroListener();
                        }
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });

    }

    private void buscaConfigConsulta(boolean showFiltro) {
        AlertDialogUtils.show(DetalhesComprasActivity.this);
        new ApostaConfirmadaModel(this).requestConfigConsulta(new OnSilceListener<ConfigConsultaDTO>() {
            @Override
            public void success(ConfigConsultaDTO payload) {
                AlertDialogUtils.dismiss();
                configConsulta = payload;

                if (showFiltro){
                    onFiltroListener();
                }
            }
            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onSuccess() {
    }

    @Override
    public void irParaCompras() {
        Log.d("", "");
    }
}
