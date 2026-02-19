package br.gov.caixa.loterias.apostas.controllers;


import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ItemMinhasApostasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogFragmentUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MensagemUtils;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheMinhasApostasAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;
import br.gov.caixa.loterias.apostas.view.listener.OnModalidadesAbertasListener;

public class DetalheMinhasApostasTeimosinhaActivity extends AppCompatActivity {
    public static final String ARG_COMPROVANTE = "ARG_COMPROVANTE";
//    public static final String ARG_DETALHES_PREMIO = "ARG_DETALHES_PREMIO";
//    public static final String ARG_RESULTADO_CONCURSO = "ARG_RESULTADO_CONCURSO";
//    public static final String ARG_DETALHES_HISTORICO = "ARG_DETALHES_HISTORICO";
//    public static final String ARG_SITUACAO = "ARG_SITUACAO";

    private ComprovanteApostaDTO comprovanteApostaDTO;
    private DetalhesPremioDTO detalhesPremioDTO;

    private List<ResultadoConcursoDTO> listResultadoConcursoDTO;

    private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

    private Toolbar toolbar;

    private ConstraintLayout clScroll, clResgatePremio, clResgatePremio2, comprovanteLayout;
    private ImageView ivResgatePremio, ivResgatePremio2;
    private ImageView validadePremioImg, ivImageDownload;

    private View divider;

    private TextView concursoLabel, concursoTxt, quantidadeApostadasTxt, dataAposta, dataApostaTxt, horaApostaTxt;
    private TextView statusApostaTxt, tv_val_premio, tv_val_premio_tit;
    private TextView ResgatarPremioTxt, ResgatarPremioTxt2;

    private TextView validadePremioText, validadePremioLabel, gerarConprovanteTxt;

    private TipoApostaLinhaView tipoApostaLinhaView;

    private EstiloModalidadeMKP estiloMKP;

    //Botoes
    private RelativeLayout carrinhoApostasBtn, adicionarCarrinhoBtn, editarApostaBtn;
    private Uri fileApostaUri, filePremioUri;
    private ParametroSimulacao parametroSimulacao, parametroSimulacaoTrocado;
    private SessaoUsuario sessaoUsuario;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_minhas_apostas_teimosinha);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));

        pegaExtras();
        setaViews();
        aplicaCores();
        setupToolbar();
        setaMetodos();

        sessaoUsuario = SessaoUsuario.getInstance();
        parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());
        parametroSimulacaoTrocado = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());

        preencheTipoApostaLinha();
        preencheRodape();
        //preencheCentro();

        servicoResultadoModalidadeConcursos();
    }
    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            comprovanteApostaDTO = (ComprovanteApostaDTO) bundle.getSerializable(ARG_COMPROVANTE); //Não premiada / premio pago ??
//                detalhesPremioDTO = (DetalhesPremioDTO) bundle.getSerializable(ARG_DETALHES_PREMIO);
//                resultadoConcurso = (ResultadoConcursoDTO) bundle.getSerializable(ARG_RESULTADO_CONCURSO);
//                detalhesHistoricoPremioDTO = (DetalhesHistoricoPremioDTO) bundle.getSerializable(ARG_DETALHES_HISTORICO);
//                situacaoAposta = (DTOEnumLong) bundle.getSerializable(ARG_SITUACAO);
        }

        if (comprovanteApostaDTO != null) {
            aposta = comprovanteApostaDTO.getAposta();
        }
    }

    private void setupToolbar() {
        if (isEspecial(aposta.getTipoConcurso().getValor())) {
            toolbar.setBackgroundResource(estiloMKP.getImagemEspecialSimples());
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, estiloMKP.getCorClara()));
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.seta_esquerda);
            upArrow.mutate().setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, "_"+ViewUtils.getNomeModalidadePorAposta(aposta).toLowerCase()+"_"));
    }

    private void setaViews(){
        toolbar = findViewById(R.id.toolbar);
        clScroll = findViewById(R.id.cl_scroll_content);

        concursoLabel = findViewById(R.id.concursoLabel);
        concursoTxt = findViewById(R.id.concursoTxt);
        quantidadeApostadasTxt = findViewById(R.id.quantidadeApostadasTxt);
        dataAposta = findViewById(R.id.dataAposta);
        dataApostaTxt = findViewById(R.id.dataApostaTxt);
        horaApostaTxt = findViewById(R.id.horaApostaTxt);
        divider = findViewById(R.id.divider);

        statusApostaTxt = findViewById(R.id.statusApostaTextView);
        tv_val_premio_tit = findViewById(R.id.tv_val_premio_tit);
        tv_val_premio = findViewById(R.id.tv_val_premio);

        tipoApostaLinhaView = findViewById(R.id.tipo_aposta_linha);

        clResgatePremio = findViewById(R.id.clResgatePremio);
        ivResgatePremio = findViewById(R.id.ivResgatePremio);
        ResgatarPremioTxt = findViewById(R.id.ResgatarPremioTxt);

        clResgatePremio2 = findViewById(R.id.clResgatePremio2);
        ivResgatePremio2 = findViewById(R.id.ivResgatePremio2);
        ResgatarPremioTxt2 = findViewById(R.id.ResgatarPremioTxt2);

        comprovanteLayout = findViewById(R.id.comprovanteLayout);
        ivImageDownload = findViewById(R.id.ivImageDownload);
        gerarConprovanteTxt = findViewById(R.id.gerarConprovanteTxt);

        validadePremioText = findViewById(R.id.validadePremioText);
        validadePremioLabel = findViewById(R.id.validadePremioLabel);
        validadePremioImg = findViewById(R.id.validadePremioImage);

        carrinhoApostasBtn = findViewById(R.id.carrinhoApostasBtn);
        editarApostaBtn = findViewById(R.id.editarApostaBtn);
        adicionarCarrinhoBtn = findViewById(R.id.adicionarCarrinhoBtn);
    }

    private void aplicaCores() {
        estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());

        clScroll.setBackgroundColor(ContextCompat.getColor(this, estiloMKP.getCorEscura()));

        concursoLabel.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        concursoTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        quantidadeApostadasTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        dataAposta.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        dataApostaTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        horaApostaTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        divider.setBackgroundResource(estiloMKP.getCorFonteFundoEscuro());

        statusApostaTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        tv_val_premio_tit.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        tv_val_premio.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));

        if (aposta.getModalidade().equals(ModalidadeEnum.SUPER_7)) {
            clResgatePremio.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, estiloMKP.getCorLetraLista())));
            ivResgatePremio.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            ResgatarPremioTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));

            clResgatePremio2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, estiloMKP.getCorLetraLista())));
            ivResgatePremio2.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            ResgatarPremioTxt2.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));

            comprovanteLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, estiloMKP.getCorLetraLista())));
            ivImageDownload.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            gerarConprovanteTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        } else {
            clResgatePremio.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro())));
            ivResgatePremio.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorEscura()));
            ResgatarPremioTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorEscura()));

            clResgatePremio2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro())));
            ivResgatePremio2.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorEscura()));
            ResgatarPremioTxt2.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorEscura()));

            comprovanteLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro())));
            ivImageDownload.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorEscura()));
            gerarConprovanteTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorEscura()));
        }

        validadePremioText.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        validadePremioLabel.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));

        validadePremioImg.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
    }

    private void setaMetodos(){
        clResgatePremio.setOnClickListener(v-> startResgateActivity());
        clResgatePremio2.setOnClickListener(v-> startResgateActivity());

        //favoriteBtn.setOnClickListener(v-> favoriteBtn());
        //favoritadoLayout.setOnClickListener(v -> favoritadoLayout());
        //layoutTextoSalveAposta.setOnClickListener(v -> layoutTextoSalveAposta());
        //salvarApostaImg.setOnClickListener(v -> salvarApostaImg());
        comprovanteLayout.setOnClickListener(v -> getComprovantePdf());
        editarApostaBtn.setOnClickListener(v -> editarApostaBtn());
        adicionarCarrinhoBtn.setOnClickListener(v -> adicionarCarrinhoBtn());
        carrinhoApostasBtn.setOnClickListener(v -> carrinhoApostasBtn());
    }

    private void startResgateActivity() {
        Intent intent = IntentUtil.getIntentOrigemDestino(this, ResultadoApostaConfirmadaActivity.class);
        intent.putExtra(ResultadoApostaConfirmadaActivity.DETALHES_PREMIO_DTO_EXTRA, ((Serializable) detalhesPremioDTO));
        intent.putExtra(ResultadoApostaConfirmadaActivity.COMPROVANTE_EXTRA, ((Serializable) comprovanteApostaDTO));
        startActivity(intent);
    }

    private void carrinhoApostasBtn() {
        AlertDialogUtils.show(this);

        ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DetalheMinhasApostasTeimosinhaActivity.this);
                }
                Bundle args = new Bundle();
                args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                startActivity(new Intent(DetalheMinhasApostasTeimosinhaActivity.this, CarrinhoActivity.class).putExtras(args));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    startActivity(new Intent(DetalheMinhasApostasTeimosinhaActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true));
                } else {
                    RedirectNetwork.checkRedirect( error, DetalheMinhasApostasTeimosinhaActivity.this);
                }
            }
        });
    }

    private void adicionarCarrinhoBtn() {
        AdicionarApostaCarrinhoHelper.checagemModadalidadesAbertas(aposta.getModalidade(), onChegagemModalidadesListener(false, "Adicionar ao\n carrinho"));
    }

    private void editarApostaBtn() {
        AdicionarApostaCarrinhoHelper.checagemModadalidadesAbertas(aposta.getModalidade(), onChegagemModalidadesListener(true, "Editar Aposta"));
    }

    private OnModalidadesAbertasListener onChegagemModalidadesListener(boolean isEditing, String btnText){
        return new OnModalidadesAbertasListener() {
            @Override
            public void ambasAbertas(List<ParametroSimulacao> arrayConcursos) {
                DialogFragmentUtils.startEscolhaModalidade(getSupportFragmentManager(), arrayConcursos, aposta.getId(),
                        getResources().getString(R.string.adicionar_carrinho_favoritas_view), btnText,false, false,
                        (tipoConcurso, teimosinha, espelho) -> {
                            IdentificaoDeUmaApostaDas8Modalidades clone = aposta;
                            if (!tipoConcurso.toString().equalsIgnoreCase(aposta.getTipoConcurso().getValor())) {
                                clone = ApostaUtils.converteTipoDeConcurso(aposta);
                            }
                            parametroSimulacaoTrocado = ViewUtils.getParametroSimulacao(sessaoUsuario, clone.getModalidade(), clone.getTipoConcurso().getValor());
                            if (isEditing){
                                goToEditarAposta(clone);
                            } else {
                                adicionaApostaCarrinho(clone);
                            }
                        });
            }

            @Override
            public void ambasFechadas() {
                AlertDialogUtils.dismiss();
                ViewUtils.alertTitulo(DetalheMinhasApostasTeimosinhaActivity.this, R.string.label_atencao,"Modalidade não disponível no momento.");
            }

            @Override
            public void unicaModalidade(List<ParametroSimulacao> arrayConcursos, TipoConcursoEnum tipoConcursoEnum) {
                if (tipoConcursoEnum.getValor().equals(aposta.getTipoConcurso().getValor())){
                    if (isEditing){
                        goToEditarAposta(aposta);
                    } else {
                        adicionaApostaCarrinho(aposta);
                    }
                } else {
                    String msg = MensagemUtils.getMensagemConverteModalidade(aposta.getModalidade(), aposta.getTipoConcurso().getValor());

                    ViewUtils.alertTitleButtonPositiveListener(DetalheMinhasApostasTeimosinhaActivity.this, R.string.label_atencao, msg, R.string.confirmar, (dialog, which) -> {
                        IdentificaoDeUmaApostaDas8Modalidades clone =  ApostaUtils.converteTipoDeConcurso(aposta);
                        parametroSimulacaoTrocado = ViewUtils.getParametroSimulacao(sessaoUsuario, clone.getModalidade(), clone.getTipoConcurso().getValor());
                        if (isEditing){
                            goToEditarAposta(clone);
                        } else {
                            adicionaApostaCarrinho(clone);
                        }
                    });
                }
            }
        };
    }

    private void goToEditarAposta(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta){
        if (CollectionUtils.isNotEmpty(aposta.getNumerosSelecionados())) {
            Intent intent = IntentUtil.getIntentOrigemDestino(this, SimularApostaActivity.class);
            intent.putExtra(SimularApostaActivity.APOSTA_EXTRA, ((Serializable) aposta));
            intent.putExtra("tipoAposta", ((Serializable) aposta.getModalidade()));
            intent.putExtra("modalidade", ((Serializable) (new Gson()).toJson(parametroSimulacaoTrocado.getParametroJogo())));
            intent.putExtra("especial", isEspecial(aposta.getTipoConcurso().getValor()));
            startActivity(intent);
        }
    }

    private void adicionaApostaCarrinho(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {

        IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> apostaMatriz;

        if (comprovanteApostaDTO != null) {
            if (comprovanteApostaDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)) {
                apostaMatriz = comprovanteApostaDTO.getAposta();
                apostaMatriz.setNumerosSelecionados(comprovanteApostaDTO.getAposta().getMatrizNumerosSelecionados());
            }
        }
        ModalidadeEnum modalidade = (aposta != null && aposta.getModalidade()!= null) ? aposta.getModalidade(): null;
        if (modalidade != null && !modalidade.equals(ModalidadeEnum.LOTECA)){
            if (!modalidade.equals(ModalidadeEnum.SUPER_7)) {
                aposta.setNumerosSelecionados(aposta.getListaNumerosSelecionados());
            } else {
                apostaMatriz = comprovanteApostaDTO.getAposta();
                apostaMatriz.setNumerosSelecionados(comprovanteApostaDTO.getAposta().getMatrizNumerosSelecionados());
                AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(this, apostaMatriz);
                return;
            }
        }

        AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(this, aposta);
    }

    private void getComprovantePdf() {
        boolean premioPago = true;
        if (comprovanteApostaDTO != null) {
            premioPago = false;
        }
        if(!premioPago){
            if(fileApostaUri != null){
                PdfUtils.abrirPdf(fileApostaUri, DetalheMinhasApostasTeimosinhaActivity.this);
                fileApostaUri = null;
            } else {
                AlertDialogUtils.show(this);
                try {
                    ApostaSilceBO.getInstance().baixarComprovanteAposta(aposta.getId(), new RequestListener<String>() {
                        @Override
                        public void onResponse(String  response) {
                            AlertDialogUtils.dismiss();
                            fileApostaUri = PdfUtils.baixarPdfAposta(response, DetalheMinhasApostasTeimosinhaActivity.this);
                            PdfUtils.abrirPdf(fileApostaUri, DetalheMinhasApostasTeimosinhaActivity.this);
                            fileApostaUri = null;
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            RedirectNetwork.checkRedirect(error,DetalheMinhasApostasTeimosinhaActivity.this);
                            error.getLocalizedMessage();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("ApostaConfirmadaDetAct", "Não foi possível acessar o serviço para baixar comprovante de aposta");
                }
            }
        }else{
            if(filePremioUri != null){
                PdfUtils.abrirPdf(filePremioUri, DetalheMinhasApostasTeimosinhaActivity.this);
                filePremioUri = null;
            } else {
                AlertDialogUtils.show(this);
                try{
                    ApostaSilceBO.getInstance().baixarComprovantePremio(aposta.getId(), new RequestListener<String>() {
                        @Override
                        public void onResponse(String  response) {
                            AlertDialogUtils.dismiss();
                            filePremioUri = PdfUtils.baixarPdfPremio(response, DetalheMinhasApostasTeimosinhaActivity.this);
                            PdfUtils.abrirPdf(filePremioUri, DetalheMinhasApostasTeimosinhaActivity.this);
                            filePremioUri = null;
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            RedirectNetwork.checkRedirect(error,DetalheMinhasApostasTeimosinhaActivity.this);
                            error.getLocalizedMessage();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("ApostaConfirmadaDetAct", "Não foi possível acessar o serviço para baixar comprovante de prêmio");
                }
            }
        }
    }


    private void preencheCabecalho() {
        concursoTxt.setText(aposta.getConcursoInicial().toString());

        if (aposta.getQuantidadeTeimosinhas() == 0) {
            quantidadeApostadasTxt.setVisibility(View.GONE);
        } else {
            concursoTxt.setText(aposta.getConcursoInicial().toString() + " - " +
                    (aposta.getConcursoInicial()+aposta.getQuantidadeTeimosinhas()-1));

            if (aposta.getQuantidadeTeimosinhas() == 1) {
                quantidadeApostadasTxt.setText("("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_aposta)+")");
            } else {
                quantidadeApostadasTxt.setText("("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_apostas)+")");
            }
        }

        dataApostaTxt.setText(aposta.getDataEfetivacao());
        horaApostaTxt.setText(StringUtils.transformToHorahMinuto(aposta.getHoraEfetivacao()));

        if (isApostaPremiada(aposta) && detalhesPremioDTO != null && detalhesPremioDTO.getPremio() != null && detalhesPremioDTO.getPremio().getValorLiquido() != null){
            statusApostaTxt.setText(aposta.getSituacao().getDescricao());
            tv_val_premio.setText(ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));
            clResgatePremio.setVisibility(View.VISIBLE);
        } else {
            statusApostaTxt.setVisibility(View.GONE);
            tv_val_premio_tit.setVisibility(TextView.GONE);
            tv_val_premio.setVisibility(TextView.GONE);
            clResgatePremio.setVisibility(View.GONE);
            validadePremioText.setVisibility(View.GONE);
            validadePremioLabel.setVisibility(View.GONE);
            validadePremioImg.setVisibility(View.GONE);
            if (isConcursoNaoApurado(aposta)) {
                tv_val_premio_tit.setVisibility(TextView.VISIBLE);
                tv_val_premio_tit.setText(R.string.label_concurso_nao_apurado);
            }
            if (isApostaNaoPremiada(aposta)) {
                tv_val_premio_tit.setVisibility(TextView.VISIBLE);
                tv_val_premio_tit.setText(R.string.label_aposta_nao_premiada);
            }
        }
    }

    private void preencheRodape() {
        if (isApostaPremiada(aposta)){
            clResgatePremio2.setVisibility(View.VISIBLE);
        } else {
            clResgatePremio2.setVisibility(View.GONE);
        }
    }

    private void preencheTipoApostaLinha() {
        List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

        if (aposta.getTroca()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
        }
        if (aposta.getIndicadorCotaBolao()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
        }
        if (aposta.getCombo()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
        }
        if (aposta.getEspelho()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
        }
        if (aposta.getSurpresinha()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
        }
        if (aposta.getQuantidadeTeimosinhas() > 0) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
        }

        if (listTipoApostaLinha.size() > 0) {
            tipoApostaLinhaView.setVisibility(View.VISIBLE);
            tipoApostaLinhaView.setupView(listTipoApostaLinha, estiloMKP.getCorClara(), estiloMKP.getCorFonteFundoClaro(), estiloMKP.getCorFonteFundoClaro(), TipoApostaLinhaView.TriangleDirection.DOWN);
        }
    }

    private boolean isApostaPremiada(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA;
    }
    private boolean isConcursoNaoApurado(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.CONCURSO_NAO_APURADO;
    }
    private boolean isApostaNaoPremiada(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.NAO_PREMIADA;
    }
    private boolean isEspecial(String tipoConcurso){
        return tipoConcurso.equalsIgnoreCase("2");
    }

    private void servicoResultadoModalidadeConcursos() {

        String txtModalidade = ApostaDTO.lowerCaseFromString(aposta.getModalidade());
        Integer concursoInicial = aposta.getConcursoInicial();
        Integer concursoFinal = Integer.valueOf((aposta.getConcursoInicial() + aposta.getQuantidadeTeimosinhas() - 1));

        AlertDialogUtils.show(this);
        ApostaSilceBO.getInstance().getResultadoModalidadeConcurso(txtModalidade, concursoInicial, concursoFinal, new RequestListener<ResultadoConcursosDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursosDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalheMinhasApostasTeimosinhaActivity.this);
                }
                if(response != null){
                    listResultadoConcursoDTO = response.getPayload();
                    servicoApostaDetalhePremio(aposta.getId());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                preencheCabecalho();
                preencheCentro();
            }
        });
    }

    private void servicoApostaDetalhePremio(Long apostaId) {
        ApostaSilceBO.getInstance().getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                AlertDialogUtils.dismiss();
                if (result != null && result.getPayload() != null) {
                    detalhesPremioDTO = result.getPayload();
                }
                preencheCabecalho();
                preencheCentro();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                preencheCabecalho();
                preencheCentro();
            }
        });
    }

    private void preencheCentro() {

        List<ItemMinhasApostasDTO> items = new ArrayList<>();

        if (listResultadoConcursoDTO != null && listResultadoConcursoDTO.size() > 0) {
            for (ResultadoConcursoDTO resultadoConcursoDTO : listResultadoConcursoDTO) {

                String situacao = "";
                BigDecimal valorPremio = null;
                for (ConcursoPremiadoDTO concursoPremiado : detalhesPremioDTO.getPremio().getConcursos()) {
                    if (concursoPremiado.getConcurso().getNumero().equals(resultadoConcursoDTO.getConcurso().getNumero())) {
                        situacao = concursoPremiado.getSituacao();
                        valorPremio = concursoPremiado.getValorLiquido();
                        break;
                    }
                }

                ArrayList<Number> resultado = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio();

                switch (resultadoConcursoDTO.getConcurso().getModalidade()) {
                    case TIMEMANIA:
                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringList(resultado),
                                StringUtils.formatToStringList(aposta.getNumerosSelecionados()),
                                resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getNome()+"/"+resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getUf(),
                                aposta.getTimeDoCoracao().getNome()+"/"+aposta.getTimeDoCoracao().getUf(),
                                valorPremio));
                        break;
                    case DIA_DE_SORTE:
                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringList(resultado),
                                StringUtils.formatToStringList(aposta.getNumerosSelecionados()),
                                resultadoConcursoDTO.getPremiacaoMesDeSorte().getMesDeSorte().getNome(),
                                aposta.getMesDeSorte().getNome(),
                                valorPremio));
                        break;
                    case DUPLA_SENA:
                        ArrayList<Number> resultado2 = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosSegundoSorteio();

                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringList(resultado),
                                StringUtils.formatToStringList(aposta.getNumerosSelecionados()),
                                StringUtils.formatToStringList(resultado2),
                                StringUtils.formatToStringList(aposta.getNumerosSelecionados()),
                                valorPremio));
                        break;
                    case SUPER_7:
                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringListFlexPrimeiro(resultado),
                                StringUtils.formatToStringListFlexPrimeiro(aposta.getNumerosSelecionados()),
                                valorPremio));
                        break;
                    case MAIS_MILIONARIA:
                        ArrayList<Number> resultadoTrevos = (ArrayList) resultadoConcursoDTO.getTrevosSorteadosPrimeiroSorteio();

                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringList(resultado),
                                StringUtils.formatToStringList(aposta.getNumerosSelecionados()),
                                valorPremio,
                                StringUtils.formatToStringList1Digito(resultadoTrevos),
                                StringUtils.formatToStringList1Digito(aposta.getTrevosSelecionados())));
                        break;
                    case LOTOFACIL:
                    case QUINA:
                    case LOTOMANIA:
                    case MEGA_SENA:
                    default:
                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringList(resultado),
                                StringUtils.formatToStringList(aposta.getNumerosSelecionados()),
                                valorPremio));
                        break;
                }
            }
        } else {
            //Concurso nao apurado
            items.add(new ItemMinhasApostasDTO(aposta.getModalidade(),
                    StringUtils.formatToStringList(aposta.getNumerosSelecionados())));
        }


        RecyclerView recyclerView = findViewById(R.id.rv_accordion_teimosinha);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetalheMinhasApostasAdapter adapter = new DetalheMinhasApostasAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }
}