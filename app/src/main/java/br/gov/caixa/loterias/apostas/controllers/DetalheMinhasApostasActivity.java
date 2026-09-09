package br.gov.caixa.loterias.apostas.controllers;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ItemMinhasApostasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UF;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.DetalhesMinhasApostasButtonHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.AccessibleRecyclerAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheMinhasApostasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PartidasLotecaNovaAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class DetalheMinhasApostasActivity extends AppCompatActivity {
    public static final String ARG_COMPROVANTE = "ARG_COMPROVANTE";
    public static final String ARG_DETALHES_PREMIO = "ARG_DETALHES_PREMIO";

    private ComprovanteApostaDTO comprovanteApostaDTO;
    private DetalhesPremioDTO detalhesPremioDTO;

    private List<ResultadoConcursoDTO> listResultadoConcursoDTO;

    private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

    private ConstraintLayout clScroll, clResgatePremio, clResgatePremio2, comprovanteLayout, clInformacoesBolao, cl_loteca_top, cl_loteca_bottom;
    private LinearLayout llLegendaButton;

    private RecyclerView rvFieldsBolao;

    private ImageView ivResgatePremio, ivResgatePremio2;

    private ImageView validadePremioImg, ivImageDownload, iv_lupa_legenda;

    private View divider, vLinhaLoteca, vLinhaLoteca2, vLinhaLoteca3;

    private TextView concursoLabel, concursoTxt, quantidadeApostadasTxt, dataAposta, dataApostaTxt, horaApostaTxt;
    private TextView containerConcurso;
    private TextView clAccessibilityLeft, clAccessibilityRight;
    private TextView statusApostaTxt, tv_val_premio, tv_val_premio_tit, tvStatusCentralizado;
    private TextView ResgatarPremioTxt, ResgatarPremioTxt2;

    private TextView validadePremioText, validadePremioLabel, gerarConprovanteTxt;

    private TextView tvLabelMinhasApostas, tvLabelLegenda, tvLabelLotecaAcertos, tvLotecaAcertos, tvLabelLotecaPremio, tvLotecaPremio;


    private TipoApostaLinhaView tipoApostaLinhaView;

    private EstiloModalidadeMKP estiloMKP;

    //Botoes
    DetalhesMinhasApostasButtonHelper detalhesMinhasApostasButtonHelper;
    ConstraintLayout clbottons, clLotecaAcertos, clLotecaPremio;
    LinearLayout llAdicFav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_minhas_apostas_todas);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));

        pegaExtras();
        setaViews();
        aplicaCores();
        setupToolbar();
        desativaComponentes();

        if (isConcursoNaoApurado(aposta)) {
            populaTela();
        } else{
            servicoResultadoModalidadeConcursos();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlertDialogUtils.dismiss();
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            comprovanteApostaDTO = (ComprovanteApostaDTO) bundle.getSerializable(ARG_COMPROVANTE);
            detalhesPremioDTO = (DetalhesPremioDTO) bundle.getSerializable(ARG_DETALHES_PREMIO);
        }

        if (comprovanteApostaDTO != null) {
            aposta = comprovanteApostaDTO.getAposta();
        } else {
            if (detalhesPremioDTO != null) {
                aposta = detalhesPremioDTO.getAposta();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void populaTela() {
        ativaComponentes();

        detalhesMinhasApostasButtonHelper = new DetalhesMinhasApostasButtonHelper(DetalheMinhasApostasActivity.this);
        detalhesMinhasApostasButtonHelper.setupButtonListeners(isBolao() || isLoteca());
        detalhesMinhasApostasButtonHelper.setupDTO(comprovanteApostaDTO, detalhesPremioDTO, aposta);

        preencheCabecalho();
        preencheTipoApostaLinha();
        preencheRodape();


        if (isLoteca()) {
            preencheCentroLoteca();
        } else if (isBolao()) {
            preencheCentroBolao();
        } else if (isTeimosinha(aposta)) {
            preencheCentroTeimosinha();
        } else {
            preencheCentroApSimples();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isEspecial(aposta.getTipoConcurso().getValor()) && estiloMKP.getImagemEspecialSimples() > 0) {
            toolbar.setBackgroundResource(estiloMKP.getImagemEspecialSimples());
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, estiloMKP.getCorClara()));
        }

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.seta_esquerda);
            upArrow.mutate().setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setHomeActionContentDescription(R.string.txt_voltar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setTextSize(18);
        toolbarTitle.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
        toolbarTitle.setText(ViewUtils.textFuturaAndFuturaBold(this, "_"+ViewUtils.getNomeModalidadePorAposta(aposta).toLowerCase()+"_"));
        ViewCompat.setAccessibilityHeading(toolbarTitle, true);

        ImageButton btnDuvidas = findViewById(R.id.ib_duvidas);
        btnDuvidas.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
        btnDuvidas.setOnClickListener(view -> {
            abrirTermosUso();
        });
        btnDuvidas.setContentDescription(getString(R.string.txt_duvidas));
    }

    protected void abrirTermosUso() {
        Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
        startActivity(intent);
    }

    private void setaViews(){
        clScroll = findViewById(R.id.cl_scroll_content);

        concursoLabel = findViewById(R.id.concursoLabel);
        containerConcurso = findViewById(R.id.cl_concursos);
        concursoTxt = findViewById(R.id.concursoTxt);
        quantidadeApostadasTxt = findViewById(R.id.quantidadeApostadasTxt);

        clAccessibilityLeft = findViewById(R.id.cl_accessibility_left);
        clAccessibilityRight = findViewById(R.id.cl_accessibility_right);
        dataAposta = findViewById(R.id.dataAposta);
        dataApostaTxt = findViewById(R.id.dataApostaTxt);
        horaApostaTxt = findViewById(R.id.horaApostaTxt);
        divider = findViewById(R.id.divider);

        statusApostaTxt = findViewById(R.id.statusApostaTextView);
        tv_val_premio_tit = findViewById(R.id.tv_val_premio_tit);
        tvStatusCentralizado = findViewById(R.id.tvStatusCentralizado);
        tv_val_premio = findViewById(R.id.tv_val_premio);

        tipoApostaLinhaView = findViewById(R.id.tipo_aposta_linha);

        clInformacoesBolao = findViewById(R.id.clInformacoesBolao);
        rvFieldsBolao = findViewById(R.id.rv_fields_bolao);

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

        //Loteca
        cl_loteca_top = findViewById(R.id.cl_loteca_top);
        cl_loteca_bottom = findViewById(R.id.cl_loteca_bottom);
        tvLabelMinhasApostas = findViewById(R.id.tv_label_minhas_apostas);
        tvLabelLegenda = findViewById(R.id.tv_label_legenda);
        iv_lupa_legenda = findViewById(R.id.iv_lupa_legenda);
        llLegendaButton = findViewById(R.id.ll_legenda_button);
        vLinhaLoteca = findViewById(R.id.v_linha_loteca);
        vLinhaLoteca2 = findViewById(R.id.v_linha_loteca2);
        clLotecaAcertos = findViewById(R.id.cl_loteca_acertos);
        tvLabelLotecaAcertos = findViewById(R.id.tv_label_loteca_acertos);
        tvLotecaAcertos = findViewById(R.id.tv_loteca_acertos);
        clLotecaPremio = findViewById(R.id.cl_loteca_premio);
        tvLabelLotecaPremio = findViewById(R.id.tv_label_loteca_premio);
        tvLotecaPremio = findViewById(R.id.tv_loteca_premio);
        vLinhaLoteca3 = findViewById(R.id.v_linha_loteca3);

        setAccessibilityAnchor();

        //Botoes
        clbottons = findViewById(R.id.v_bottom);
        llAdicFav = findViewById(R.id.ll_adic_fav);
    }

    private void setAccessibilityAnchor() {
        ViewCompat.setAccessibilityDelegate(clResgatePremio, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                info.setClassName(android.widget.Button.class.getName());
                info.setClickable(true);
                info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            }
        });

        ViewCompat.setAccessibilityDelegate(clResgatePremio2, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                info.setClassName(android.widget.Button.class.getName());
                info.setClickable(true);
                info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            }
        });

        ViewCompat.setAccessibilityDelegate(comprovanteLayout, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                info.setClassName(android.widget.Button.class.getName());
                info.setClickable(true);
                info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            }
        });
    }

    private void desativaComponentes() {
        concursoLabel.setVisibility(View.GONE);
        concursoTxt.setVisibility(View.GONE);
        quantidadeApostadasTxt.setVisibility(View.GONE);
        dataAposta.setVisibility(View.GONE);
        dataApostaTxt.setVisibility(View.GONE);
        horaApostaTxt.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);

        statusApostaTxt.setVisibility(View.GONE);
        tv_val_premio_tit.setVisibility(View.GONE);
        tv_val_premio.setVisibility(View.GONE);

        tipoApostaLinhaView.setVisibility(View.GONE);

        clResgatePremio.setVisibility(View.GONE);
        ivResgatePremio.setVisibility(View.GONE);
        ResgatarPremioTxt.setVisibility(View.GONE);

        clResgatePremio2.setVisibility(View.GONE);
        ivResgatePremio2.setVisibility(View.GONE);
        ResgatarPremioTxt2.setVisibility(View.GONE);

        comprovanteLayout.setVisibility(View.GONE);
        ivImageDownload.setVisibility(View.GONE);
        gerarConprovanteTxt.setVisibility(View.GONE);

        validadePremioText.setVisibility(View.GONE);
        validadePremioLabel.setVisibility(View.GONE);
        validadePremioImg.setVisibility(View.GONE);

        //Botoes
        clbottons.setVisibility(View.GONE);
        llAdicFav.setVisibility(View.GONE);
    }
    private void ativaComponentes() {
        concursoLabel.setVisibility(View.VISIBLE);
        concursoTxt.setVisibility(View.VISIBLE);
        quantidadeApostadasTxt.setVisibility(View.VISIBLE);
        dataAposta.setVisibility(View.VISIBLE);
        dataApostaTxt.setVisibility(View.VISIBLE);
        horaApostaTxt.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        statusApostaTxt.setVisibility(View.VISIBLE);
        tv_val_premio_tit.setVisibility(View.VISIBLE);
        tv_val_premio.setVisibility(View.VISIBLE);

        tipoApostaLinhaView.setVisibility(View.VISIBLE);

        clResgatePremio.setVisibility(View.VISIBLE);
        ivResgatePremio.setVisibility(View.VISIBLE);
        ResgatarPremioTxt.setVisibility(View.VISIBLE);

        clResgatePremio2.setVisibility(View.VISIBLE);
        ivResgatePremio2.setVisibility(View.VISIBLE);
        ResgatarPremioTxt2.setVisibility(View.VISIBLE);

        comprovanteLayout.setVisibility(View.VISIBLE);
        ivImageDownload.setVisibility(View.VISIBLE);
        gerarConprovanteTxt.setVisibility(View.VISIBLE);

        validadePremioText.setVisibility(View.VISIBLE);
        validadePremioLabel.setVisibility(View.VISIBLE);
        validadePremioImg.setVisibility(View.VISIBLE);

        //Botoes
        clbottons.setVisibility(View.VISIBLE);
        llAdicFav.setVisibility(View.VISIBLE);
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
        tvStatusCentralizado.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
        tv_val_premio.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));

        if (aposta.getModalidade().equals(ModalidadeEnum.SUPER_7) || aposta.getModalidade().equals(ModalidadeEnum.DIA_DE_SORTE)) {
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

        if (isLoteca()) {
            tvLabelMinhasApostas.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            tvLabelLegenda.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            iv_lupa_legenda.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            vLinhaLoteca.setBackgroundResource(estiloMKP.getCorFonteFundoEscuro());
            vLinhaLoteca2.setBackgroundResource(estiloMKP.getCorFonteFundoEscuro());
            tvLabelLotecaAcertos.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            tvLotecaAcertos.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            tvLabelLotecaPremio.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            tvLotecaPremio.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoEscuro()));
            vLinhaLoteca3.setBackgroundResource(estiloMKP.getCorFonteFundoEscuro());
        }
    }

    private void preencheCabecalho() {
        if (aposta.getQuantidadeTeimosinhas() == 0) {
            concursoLabel.setText(getString(R.string.label_concurso));
        }

        concursoTxt.setVisibility(View.VISIBLE);
        concursoTxt.setText(aposta.getConcursoInicial().toString());

        concursoLabel.setContentDescription(concursoLabel.getText() + getString(R.string.txt_space) + aposta.getConcursoInicial().toString());
        containerConcurso.setContentDescription(concursoLabel.getContentDescription());

        if (aposta.getQuantidadeTeimosinhas() == 0) {
            quantidadeApostadasTxt.setVisibility(View.GONE);
        } else {
            String accessibilityContent = getString(R.string.concursos) + aposta.getConcursoInicial().toString() + " até " +
                    (aposta.getConcursoInicial()+aposta.getQuantidadeTeimosinhas()-1);

            concursoTxt.setText(aposta.getConcursoInicial().toString() + " - " +
                    (aposta.getConcursoInicial()+aposta.getQuantidadeTeimosinhas()-1));

            quantidadeApostadasTxt.setVisibility(View.VISIBLE);
            if (aposta.getQuantidadeTeimosinhas() == 1) {
                quantidadeApostadasTxt.setText("("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_aposta)+")");
                accessibilityContent = accessibilityContent + "("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_aposta)+")";
            } else {
                quantidadeApostadasTxt.setText("("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_apostas)+")");
                accessibilityContent = accessibilityContent + "("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_apostas)+")";
            }

            concursoLabel.setContentDescription(accessibilityContent);
            containerConcurso.setContentDescription(accessibilityContent);
        }

        if (isBolao()) {
            dataApostaTxt.setText(aposta.getReservaCotaBolao().getDataRegistroBolao());
            horaApostaTxt.setText(aposta.getReservaCotaBolao().getHoraRegistroBolao());
            String betDateAccessibilityContent = getString(R.string.contest_date) + aposta.getReservaCotaBolao().getDataRegistroBolao() + " " + aposta.getReservaCotaBolao().getHoraRegistroBolao();
            clAccessibilityLeft.setContentDescription(betDateAccessibilityContent);
            clInformacoesBolao.setVisibility(View.VISIBLE);

            List<Pair<String, String>> labelValue = new ArrayList<>();
            String labelLoterica = "Nome da Lotérica: ";
            String valorLoterica = aposta.getReservaCotaBolao().getNumeroLoterica().getNome();
            labelValue.add(new Pair<>(labelLoterica, valorLoterica));
            String uf = StringUtils.capitalizerNovo(aposta.getReservaCotaBolao().getNumeroLoterica().getNomeMunicipio().trim()) + " - " + UF.siglaFromId(aposta.getReservaCotaBolao().getNumeroLoterica().getIdUF().intValue());
            labelValue.add(new Pair<>("Cidade/UF: ", uf));
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            labelValue.add(new Pair<>("Valor da cota: ", nf.format(aposta.getReservaCotaBolao().getVrCotaReservada())));
            labelValue.add(new Pair<>("Tarifa de Serviço: ", nf.format(aposta.getReservaCotaBolao().getVrTarifaServico())));
            labelValue.add(new Pair<>("Cota: ", aposta.getReservaCotaBolao().getNumeroCotaReservada() + "/" + aposta.getReservaCotaBolao().getQtdCotaTotalBolao()));

            setupFieldsBolao(labelValue);

        } else {
            String betDateAccessibilityContent = getString(R.string.contest_date) + aposta.getDataEfetivacao() + " " + StringUtils.transformToHorahMinuto(aposta.getHoraEfetivacao());
            dataAposta.setContentDescription(betDateAccessibilityContent);
            clAccessibilityLeft.setContentDescription(betDateAccessibilityContent);
            dataApostaTxt.setText(aposta.getDataEfetivacao());
            //horaApostaTxt.setVisibility(View.VISIBLE);
            horaApostaTxt.setText(aposta.getHoraEfetivacao());
        }

        if (isApostaPremiada(aposta) && detalhesPremioDTO != null && detalhesPremioDTO.getPremio() != null && detalhesPremioDTO.getPremio().getValorLiquido() != null){
            statusApostaTxt.setVisibility(View.VISIBLE);
            statusApostaTxt.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
            tv_val_premio_tit.setVisibility(TextView.VISIBLE);
            tv_val_premio.setVisibility(TextView.VISIBLE);
            tv_val_premio.setText(ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));

            statusApostaTxt.setContentDescription(statusApostaTxt.getText() + " " + tv_val_premio_tit.getText() + " " + tv_val_premio.getText());
            clAccessibilityRight.setContentDescription(statusApostaTxt.getContentDescription());

            clResgatePremio.setVisibility(View.VISIBLE);
            validadePremioText.setVisibility(View.VISIBLE);
            validadePremioLabel.setVisibility(View.VISIBLE);
            validadePremioImg.setVisibility(View.VISIBLE);
        } else {
            statusApostaTxt.setVisibility(View.GONE);
            tv_val_premio_tit.setVisibility(TextView.GONE);
            tv_val_premio.setVisibility(TextView.GONE);
            clResgatePremio.setVisibility(View.GONE);
            validadePremioText.setVisibility(View.GONE);
            validadePremioLabel.setVisibility(View.GONE);
            validadePremioImg.setVisibility(View.GONE);
            tvStatusCentralizado.setVisibility(TextView.VISIBLE);
            if (isConcursoNaoApurado(aposta)) {
                tvStatusCentralizado.setText(SituacaoAposta.EnumSituacaoAposta.CONCURSO_NAO_APURADO.getDescricao().replace(" não", "\nnão"));
            }
            else if (isApostaNaoPremiadaOuConcursoFuturo(aposta)) {
                tvStatusCentralizado.setText(SituacaoAposta.EnumSituacaoAposta.NAO_PREMIADA.getDescricao().replace(" não", "\nnão"));
            }
            else {
                tvStatusCentralizado.setText(ApostaUtils.descricaoCorrigida(aposta.getSituacao()));
            }
            clAccessibilityRight.setContentDescription(tvStatusCentralizado.getText());
        }
    }

    private void setupFieldsBolao(List<Pair<String, String>> list) {
        AccessibleRecyclerAdapter adapter = new AccessibleRecyclerAdapter(list, estiloMKP.getCorFonteFundoEscuro());
        rvFieldsBolao.setAdapter(adapter);
        rvFieldsBolao.setLayoutManager(new LinearLayoutManager(this));
        rvFieldsBolao.setVisibility(View.VISIBLE);
    }

    private void preencheRodape() {
        comprovanteLayout.setVisibility(View.VISIBLE);
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
            tipoApostaLinhaView.setupView(listTipoApostaLinha, estiloMKP.getCorClara(), estiloMKP.getCorFonteFundoClaro(), estiloMKP.getCorFonteFundoClaro(), TipoApostaLinhaView.TriangleDirection.DOWN, true);
        }
    }

    private boolean isLoteca() {
        return aposta.getModalidade() == ModalidadeEnum.LOTECA;
    }

    private boolean isBolao() {
        return aposta.getIndicadorCotaBolao();
    }

    private boolean isTeimosinha(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getQuantidadeTeimosinhas() > 0;
    }

    private boolean isApostaPremiada(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA;
    }
    private boolean isConcursoNaoApurado(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.CONCURSO_NAO_APURADO ||
               aposta.getSituacao().getValor() == SituacaoAposta.EFETIVADA;
    }
    private boolean isApostaNaoPremiadaOuConcursoFuturo(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.NAO_PREMIADA ||
               aposta.getSituacao().getValor() == SituacaoAposta.NAO_PREMIADA_CONCORRENDO_A_CONCURSO_FUTURO;
    }
    private boolean isEspecial(String tipoConcurso){
        return tipoConcurso.equalsIgnoreCase("2");
    }
    private boolean chamaServicoApostaDetalhePremio(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA ||
                aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO;
    }

    private void servicoResultadoModalidadeConcursos() {
        AlertDialogUtils.show(DetalheMinhasApostasActivity.this);

        String txtModalidade = ApostaDTO.lowerCaseFromString(aposta.getModalidade());
        Integer concursoInicial = aposta.getConcursoInicial();
        Integer concursoFinal = aposta.getConcursoInicial();
        if (isTeimosinha(aposta)) {
            concursoFinal = Integer.valueOf((aposta.getConcursoInicial() + aposta.getQuantidadeTeimosinhas() - 1));
        }

        ApostaSilceBO.getInstance().getResultadoModalidadeConcurso(txtModalidade, concursoInicial, concursoFinal, new RequestListener<ResultadoConcursosDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursosDTOResponse response) {
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalheMinhasApostasActivity.this);
                }
                if(response != null){
                    listResultadoConcursoDTO = response.getPayload();
                    if (detalhesPremioDTO == null && chamaServicoApostaDetalhePremio(aposta)) {
                        servicoApostaDetalhePremio(aposta.getId());
                    } else {
                        populaTela();
                        AlertDialogUtils.dismiss();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                apresentaErro(error);
            }
        });
    }

    private void servicoApostaDetalhePremio(Long apostaId) {
        ApostaSilceBO.getInstance().getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                if (result != null && result.getPayload() != null) {
                    detalhesPremioDTO = result.getPayload();
                }
                populaTela();
                AlertDialogUtils.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                apresentaErro(error);
            }
        });
    }

    private void apresentaErro(VolleyError error) {
        boolean emApuracao = error.networkResponse != null && error.networkResponse.statusCode == 404 && error.networkResponse.data != null;
        if (emApuracao) {
            try {
                String codigo = new JSONObject(new String(error.networkResponse.data)).getString("codigo");
                emApuracao = codigo.equals("001005");
            } catch (JSONException e) {
                emApuracao = new String(error.networkResponse.data).matches("(?s).*\"codigo\"\\s*:\\s*\"001005\".*");
            }
        }
        String mensagem = emApuracao ? this.getString(R.string.erro_minhas_apostas_nao_apurado)
                                     : this.getString(R.string.erro_minhas_apostas_teimosinha);
        ViewUtils.alertFecharCustom(this, mensagem, this::finish);
    }

    private void preencheCentroTeimosinha() {
        List<ItemMinhasApostasDTO> items = new ArrayList<>();

        if (listResultadoConcursoDTO != null && listResultadoConcursoDTO.size() > 0) {
            for (ResultadoConcursoDTO resultadoConcursoDTO : listResultadoConcursoDTO) {

                String situacao = getString(R.string.label_concurso_nao_premiado);
                BigDecimal valorPremio = null;
                if (detalhesPremioDTO != null) {
                    for (ConcursoPremiadoDTO concursoPremiado : detalhesPremioDTO.getPremio().getConcursos()) {
                        if (concursoPremiado.getConcurso().getNumero().equals(resultadoConcursoDTO.getConcurso().getNumero())) {
                            situacao = concursoPremiado.getSituacao();
                            if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO &&
                                concursoPremiado.getSituacao().equalsIgnoreCase("Premiado")) {
                                situacao = "Prêmio pago";
                            }
                            valorPremio = concursoPremiado.getValorLiquido();
                            break;
                        }
                    }
                }

                ArrayList<Number> resultado = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio();

                ItemMinhasApostasDTO item = new ItemMinhasApostasDTO();
                item.setModalidade(aposta.getModalidade());
                item.setConcurso(resultadoConcursoDTO.getConcurso().getNumero().toString());
                item.setSituacao(situacao);
                item.setValorPremio(valorPremio);

                //switch (resultadoConcursoDTO.getConcurso().getModalidade()) {
                switch (aposta.getModalidade()) {
                    case TIMEMANIA:
                        item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                        item.setTimeMesSorteado(resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getNome() + "/" + resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getUf());
                        item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                        item.setSeuTimeMes(aposta.getTimeDoCoracao().getNome()+"/"+aposta.getTimeDoCoracao().getUf());
                        items.add(item);
                        break;

                    case DIA_DE_SORTE:
                        item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                        item.setTimeMesSorteado(resultadoConcursoDTO.getPremiacaoMesDeSorte().getMesDeSorte().getNome());
                        item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                        item.setSeuTimeMes(aposta.getMesDeSorte().getNome());
                        items.add(item);
                        break;

                    case DUPLA_SENA:
                        item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                        ArrayList<Number> resultado2 = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosSegundoSorteio();
                        item.setNumerosSorteados2(StringUtils.formatToStringList(resultado2));
                        item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                        item.setSeusNumeros2(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                        items.add(item);
                        break;

                    case SUPER_7:
                        //item.setNumerosSorteados(StringUtils.formatToStringListFlexPrimeiro(resultado));
                        item.setNumerosSorteados(StringUtils.formatToStringListFlexS7(resultado));
                        //item.setSeusNumeros(StringUtils.formatToStringListFlexPrimeiro(aposta.getNumerosSelecionados()));
                        item.setSeusNumeros(StringUtils.formatToStringListFlexS7(aposta.getNumerosSelecionados()));
                        items.add(item);
                        break;

                    case MAIS_MILIONARIA:
                        item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                        ArrayList<Number> resultadoTrevos = (ArrayList) resultadoConcursoDTO.getTrevosSorteadosPrimeiroSorteio();
                        item.setTrevosSorteados(StringUtils.formatToStringList1Digito(resultadoTrevos));
                        item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                        item.setSeusTrevos(StringUtils.formatToStringList1Digito(aposta.getTrevosSelecionados()));
                        items.add(item);
                        break;

                    case LOTOFACIL:
                    case QUINA:
                    case LOTOMANIA:
                    case MEGA_SENA:
                    default:
                        item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                        item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                        items.add(item);
                        break;
                }
            }
        } else {
            //Aposta não apurada
            ItemMinhasApostasDTO item = new ItemMinhasApostasDTO();
            item.setModalidade(aposta.getModalidade());
            item.setTemAcordeon(false);

            switch (aposta.getModalidade()) {
                case TIMEMANIA:
                    item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                    item.setSeuTimeMes(aposta.getTimeDoCoracao().getNome()+"/"+aposta.getTimeDoCoracao().getUf());
                    break;
                case DIA_DE_SORTE:
                    item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                    item.setSeuTimeMes(aposta.getMesDeSorte().getNome());
                    break;
                case DUPLA_SENA:
                    item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                    item.setSeusNumeros2(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                    break;
                case SUPER_7:
                    //item.setSeusNumeros(StringUtils.formatToStringListFlexPrimeiro(aposta.getNumerosSelecionados()));
                    item.setSeusNumeros(StringUtils.formatToStringListFlexS7(aposta.getNumerosSelecionados()));
                    break;
                case MAIS_MILIONARIA:
                    item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                    item.setSeusTrevos(StringUtils.formatToStringList1Digito(aposta.getTrevosSelecionados()));
                    break;
                default:
                    item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                    break;
            }

            items.add(item);
        }

        RecyclerView recyclerView = findViewById(R.id.rv_centro_detalhe_minhas_apostas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetalheMinhasApostasAdapter adapter = new DetalheMinhasApostasAdapter(this, items);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private void preencheCentroBolao() {

        List<ItemMinhasApostasDTO> items = new ArrayList<>();

        List<ApostaBolaoDTO> listApostaBolaoDTO = aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao();

        if (listApostaBolaoDTO != null && listApostaBolaoDTO.size() > 0) {
            for (ApostaBolaoDTO apostaBolaoDTO : listApostaBolaoDTO) {

                ResultadoConcursoDTO resultadoConcursoDTO = null;
                ArrayList<Number> listResultados = null;
                if (listResultadoConcursoDTO != null && listResultadoConcursoDTO.size() > 0) {
                    resultadoConcursoDTO = listResultadoConcursoDTO.get(0);
                    listResultados = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio();
                }

                ItemMinhasApostasDTO item = new ItemMinhasApostasDTO();
                item.setModalidade(aposta.getModalidade());
                item.setBolao(true);
                item.setSurpresinha(apostaBolaoDTO.isSurpresinha());
                if (aposta.getModalidade() != ModalidadeEnum.SUPER_7) {
                    item.setSeusNumeros(StringUtils.formatToStringList(apostaBolaoDTO.getDezenas()));
                }
                if (resultadoConcursoDTO != null) {
                    if (aposta.getModalidade() != ModalidadeEnum.SUPER_7) {
                        item.setNumerosSorteados(StringUtils.formatToStringList(listResultados));
                    }
                }

                switch (aposta.getModalidade()) {
                    case TIMEMANIA:
                        item.setSeuTimeMes(apostaBolaoDTO.getTimeCoracao().getNome() + "/" + apostaBolaoDTO.getTimeCoracao().getUf());
                        if (resultadoConcursoDTO != null) {
                            item.setTimeMesSorteado(resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getNome() + "/" + resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getUf());
                        }
                        break;

                    case DIA_DE_SORTE:
                        item.setSeuTimeMes(apostaBolaoDTO.getMesSorte().getNome());
                        if (resultadoConcursoDTO != null) {
                            item.setTimeMesSorteado(resultadoConcursoDTO.getPremiacaoMesDeSorte().getMesDeSorte().getNome());
                        }
                        break;

                    case DUPLA_SENA:
                        item.setSeusNumeros2(StringUtils.formatToStringList(apostaBolaoDTO.getDezenas()));
                        if (resultadoConcursoDTO != null) {
                            ArrayList<Number> listResultados2 = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosSegundoSorteio();
                            item.setNumerosSorteados2(StringUtils.formatToStringList(listResultados2));
                        }
                        break;

                    case SUPER_7:
                        //item.setSeusNumeros(StringUtils.formatToStringListFlexPrimeiro(apostaBolaoDTO.getDezenas()));
                        item.setSeusNumeros(StringUtils.formatToStringListFlexS7(apostaBolaoDTO.getDezenas()));
                        if (resultadoConcursoDTO != null) {
                            //item.setNumerosSorteados(StringUtils.formatToStringListFlexPrimeiro(listResultados));
                            item.setNumerosSorteados(StringUtils.formatToStringListFlexS7(listResultados));
                        }
                        break;

                    case MAIS_MILIONARIA:
                        item.setSeusTrevos(StringUtils.formatToStringList1Digito(apostaBolaoDTO.getTrevos()));
                        if (resultadoConcursoDTO != null) {
                            ArrayList<Number> resultadoTrevos = (ArrayList) resultadoConcursoDTO.getTrevosSorteadosPrimeiroSorteio();
                            item.setTrevosSorteados(StringUtils.formatToStringList1Digito(resultadoTrevos));
                        }
                        break;

                    case LOTOFACIL:
                    case QUINA:
                    case LOTOMANIA:
                    case MEGA_SENA:
                    default:
                        break;
                }
                items.add(item);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.rv_centro_detalhe_minhas_apostas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetalheMinhasApostasAdapter adapter = new DetalheMinhasApostasAdapter(this, items);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private void preencheCentroApSimples() {
        List<ItemMinhasApostasDTO> items = new ArrayList<>();

        ResultadoConcursoDTO resultadoConcursoDTO = null;
        ArrayList<Number> resultado = null;
        BigDecimal valorPremio = null;
        if (listResultadoConcursoDTO != null && listResultadoConcursoDTO.size() > 0) {
            resultadoConcursoDTO = listResultadoConcursoDTO.get(0);
            resultado = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio();

            if (detalhesPremioDTO != null) {
                for (ConcursoPremiadoDTO concursoPremiado : detalhesPremioDTO.getPremio().getConcursos()) {
                    if (concursoPremiado.getConcurso().getNumero().equals(resultadoConcursoDTO.getConcurso().getNumero())) {
                        valorPremio = concursoPremiado.getValorLiquido();
                        break;
                    }
                }
            }
        }

        ItemMinhasApostasDTO item = new ItemMinhasApostasDTO();
        item.setModalidade(aposta.getModalidade());
        item.setConcurso(aposta.getConcursoInicial().toString());
        item.setTemAcordeon(false);

        switch (aposta.getModalidade()) {
            case TIMEMANIA:
                item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                item.setSeuTimeMes(aposta.getTimeDoCoracao().getNome()+"/"+aposta.getTimeDoCoracao().getUf());
                if (resultadoConcursoDTO != null) {
                    item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                    item.setTimeMesSorteado(resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getNome() + "/" + resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getUf());
                    item.setValorPremio(valorPremio);
                }
                items.add(item);
                break;

            case DIA_DE_SORTE:
                item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                item.setSeuTimeMes(aposta.getMesDeSorte().getNome());
                if (resultadoConcursoDTO != null) {
                    item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                    item.setTimeMesSorteado(resultadoConcursoDTO.getPremiacaoMesDeSorte().getMesDeSorte().getNome());
                    item.setValorPremio(valorPremio);
                }
                items.add(item);
                break;

            case DUPLA_SENA:
                item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                item.setSeusNumeros2(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                if (resultadoConcursoDTO != null) {
                    item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                    ArrayList<Number> resultado2 = (ArrayList) resultadoConcursoDTO.getNumerosSorteadosSegundoSorteio();
                    item.setNumerosSorteados2(StringUtils.formatToStringList(resultado2));
                    item.setValorPremio(valorPremio);
                }
                items.add(item);
                break;

            case SUPER_7:
                //item.setSeusNumeros(StringUtils.formatToStringListFlexPrimeiro(aposta.getNumerosSelecionados()));
                item.setSeusNumeros(StringUtils.formatToStringListFlexS7(aposta.getNumerosSelecionados()));
                if (resultadoConcursoDTO != null) {
                    //item.setNumerosSorteados(StringUtils.formatToStringListFlexPrimeiro(resultado));
                    item.setNumerosSorteados(StringUtils.formatToStringListFlexS7(resultado));
                    item.setValorPremio(valorPremio);
                }
                items.add(item);
                break;

            case MAIS_MILIONARIA:
                item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                item.setSeusTrevos(StringUtils.formatToStringList1Digito(aposta.getTrevosSelecionados()));
                if (resultadoConcursoDTO != null) {
                    item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                    ArrayList<Number> resultadoTrevos = (ArrayList) resultadoConcursoDTO.getTrevosSorteadosPrimeiroSorteio();
                    item.setTrevosSorteados(StringUtils.formatToStringList1Digito(resultadoTrevos));
                    item.setValorPremio(valorPremio);
                }
                items.add(item);
                break;

            case LOTOFACIL:
            case QUINA:
            case LOTOMANIA:
            case MEGA_SENA:
            default:
                item.setSeusNumeros(StringUtils.formatToStringList(aposta.getNumerosSelecionados()));
                if (resultadoConcursoDTO != null) {
                    item.setNumerosSorteados(StringUtils.formatToStringList(resultado));
                    item.setValorPremio(valorPremio);
                }
                items.add(item);
                break;
        }

        RecyclerView recyclerView = findViewById(R.id.rv_centro_detalhe_minhas_apostas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetalheMinhasApostasAdapter adapter = new DetalheMinhasApostasAdapter(this, items);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }


    private void preencheCentroLoteca() {
        //Top
        cl_loteca_top.setVisibility(View.VISIBLE);
        llLegendaButton.setOnClickListener(v-> DialogUtils.dialogLegendaLoteca(this));

        RecyclerView recyclerView = findViewById(R.id.rv_centro_detalhe_minhas_apostas);
        if (isBolao()) {
            recyclerView.setAdapter(new PartidasLotecaNovaAdapter(getPartidasBolao(), this,
                    new ConfiguracaoLoteca(estiloMKP.getCorFonteFundoEscuro(), estiloMKP.getCorEscura()), getResultadoConcurso()) {
            });
        } else {
            recyclerView.setAdapter(new PartidasLotecaNovaAdapter(getPartidas(), this,
                    new ConfiguracaoLoteca(estiloMKP.getCorFonteFundoEscuro(), estiloMKP.getCorEscura()), getResultadoConcurso()) {
            });
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(Boolean.FALSE);

        //Bottom
        if (getResultadoConcurso() != null) {
            cl_loteca_bottom.setVisibility(View.VISIBLE);
            if (isBolao()) {
                tvLotecaAcertos.setText(calculaAcertos(getResultadoConcurso(), getPartidasBolao())) ;
            } else {
                tvLotecaAcertos.setText(calculaAcertos(getResultadoConcurso(), getPartidas()));
            }
            clLotecaAcertos.setContentDescription(getString(R.string.label_acertos_dois_pontos)+","+tvLotecaAcertos.getText().toString());


            if (chamaServicoApostaDetalhePremio(aposta)) {
                clLotecaPremio.setVisibility(View.VISIBLE);
                //tvLabelLotecaPremio.setVisibility(View.VISIBLE);
                //tvLotecaPremio.setVisibility(View.VISIBLE);

                BigDecimal valorPremio = BigDecimal.ZERO;
                if (detalhesPremioDTO != null && detalhesPremioDTO.getPremio() != null && detalhesPremioDTO.getPremio().getValorLiquido() != null) {
                    valorPremio = detalhesPremioDTO.getPremio().getValorLiquido();
                }
                tvLotecaPremio.setText(ViewUtils.getMoedaFormat(valorPremio));
                clLotecaPremio.setContentDescription(getString(R.string.label_premio_dois_pontos)+","+tvLotecaPremio.getText().toString());
            }
        }
    }

    private List<PartidaLotecaDTO> getPartidasBolao() {
        if (!aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().isEmpty()){
            if (aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(0).getPartidasLoteca() != null){
                return aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(0).getPartidasLoteca();
            }
        }
        return new ArrayList<>();
    }

    private List<PartidaLotecaDTO> getPartidas() {
        return aposta.getPartidasLoteca();
    }

    private ResultadoConcursoDTO getResultadoConcurso(){
        if (listResultadoConcursoDTO != null && !listResultadoConcursoDTO.isEmpty()) {
            return this.listResultadoConcursoDTO.get(0);
        }
        return null;
    }

    private String calculaAcertos(ResultadoConcursoDTO resultado, List<PartidaLotecaDTO> partidas) {
        Integer acertos = 0;
        if (resultado != null && resultado.getPartidasLoteca() != null) {
            for (int i = 0; i < resultado.getPartidasLoteca().toArray().length; i++) {
                PartidaLotecaDTO res = (PartidaLotecaDTO) resultado.getPartidasLoteca().get(i);
                if ((res.getEquipe1().getVitoria() && partidas.get(i).getEquipe1().getVitoria()) ||
                    (res.getEquipe2().getVitoria() && partidas.get(i).getEquipe2().getVitoria()) ||
                    (res.getEmpate() && partidas.get(i).getEmpate())) {
                    acertos++;
                }
            }
        }
        String acertosString = "nenhum";
        if (acertos > 0) {
            if (acertos == 1) {
                acertosString = acertos.toString() + " " + getString(R.string.palpite);
            } else {
                acertosString = acertos.toString() + " " + getString(R.string.palpite) + "s";
            }
        }
        return acertosString;
    }

}