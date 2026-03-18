package br.gov.caixa.loterias.apostas.controllers;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ItemMinhasApostasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.DetalhesMinhasApostasButtonHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheMinhasApostasAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;

public class DetalheMinhasApostasTeimosinhaActivity extends AppCompatActivity {
    public static final String ARG_COMPROVANTE = "ARG_COMPROVANTE";
    public static final String ARG_DETALHES_PREMIO = "ARG_DETALHES_PREMIO";

    private ComprovanteApostaDTO comprovanteApostaDTO;
    private DetalhesPremioDTO detalhesPremioDTO;

    private List<ResultadoConcursoDTO> listResultadoConcursoDTO;

    private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

    private ConstraintLayout clScroll, clResgatePremio, clResgatePremio2, comprovanteLayout;
    private ImageView ivResgatePremio, ivResgatePremio2;
    private ImageView validadePremioImg, ivImageDownload;

    private View divider;

    private TextView concursoLabel, concursoTxt, quantidadeApostadasTxt, dataAposta, dataApostaTxt, horaApostaTxt;
    private TextView statusApostaTxt, tv_val_premio, tv_val_premio_tit, tvStatusCentralizado;
    private TextView ResgatarPremioTxt, ResgatarPremioTxt2;

    private TextView validadePremioText, validadePremioLabel, gerarConprovanteTxt;

    private TipoApostaLinhaView tipoApostaLinhaView;

    private EstiloModalidadeMKP estiloMKP;

    //Botoes
    DetalhesMinhasApostasButtonHelper detalhesMinhasApostasButtonHelper;
    ConstraintLayout clbottons;
    LinearLayout llAdicFav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_minhas_apostas_teimosinha);
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

        detalhesMinhasApostasButtonHelper = new DetalhesMinhasApostasButtonHelper(DetalheMinhasApostasTeimosinhaActivity.this);
        detalhesMinhasApostasButtonHelper.setupButtonListeners();

        preencheCabecalho();
        preencheTipoApostaLinha();
        preencheRodape();
        preencheCentro();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        ImageButton btnDuvidas = findViewById(R.id.ib_duvidas);
        btnDuvidas.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
        btnDuvidas.setOnClickListener(view -> {
            abrirTermosUso();
        });
    }

    protected void abrirTermosUso() {
        Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
        startActivity(intent);
    }

    private void setaViews(){
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
        tvStatusCentralizado = findViewById(R.id.tvStatusCentralizado);
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

        //Botoes
        clbottons = findViewById(R.id.v_bottom);
        llAdicFav = findViewById(R.id.ll_adic_fav);
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
    }

    private void preencheCabecalho() {
        concursoTxt.setVisibility(View.VISIBLE);
        concursoTxt.setText(aposta.getConcursoInicial().toString());

        if (aposta.getQuantidadeTeimosinhas() == 0) {
            quantidadeApostadasTxt.setVisibility(View.GONE);
        } else {
            concursoTxt.setText(aposta.getConcursoInicial().toString() + " - " +
                    (aposta.getConcursoInicial()+aposta.getQuantidadeTeimosinhas()-1));

            quantidadeApostadasTxt.setVisibility(View.VISIBLE);
            if (aposta.getQuantidadeTeimosinhas() == 1) {
                quantidadeApostadasTxt.setText("("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_aposta)+")");
            } else {
                quantidadeApostadasTxt.setText("("+aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_apostas)+")");
            }
        }

        //dataApostaTxt.setVisibility(View.VISIBLE);
        dataApostaTxt.setText(aposta.getDataEfetivacao());
        //horaApostaTxt.setVisibility(View.VISIBLE);
        horaApostaTxt.setText(StringUtils.transformToHorahMinuto(aposta.getHoraEfetivacao()));

        if (isApostaPremiada(aposta) && detalhesPremioDTO != null && detalhesPremioDTO.getPremio() != null && detalhesPremioDTO.getPremio().getValorLiquido() != null){
            statusApostaTxt.setVisibility(View.VISIBLE);
            statusApostaTxt.setText(aposta.getSituacao().getDescricao());
            tv_val_premio_tit.setVisibility(TextView.VISIBLE);
            tv_val_premio.setVisibility(TextView.VISIBLE);
            tv_val_premio.setText(ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));
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
            if (isConcursoNaoApurado(aposta)) {
                tvStatusCentralizado.setVisibility(TextView.VISIBLE);
                tvStatusCentralizado.setText(R.string.label_concurso_nao_apurado);
            }
            if (isApostaNaoPremiadaOuConcursoFuturo(aposta)) {
                tvStatusCentralizado.setVisibility(TextView.VISIBLE);
                tvStatusCentralizado.setText(R.string.label_aposta_nao_premiada);
            }
            if (isApostaPremioPago(aposta)) {
                tvStatusCentralizado.setVisibility(TextView.VISIBLE);
                tvStatusCentralizado.setText(R.string.label_aposta_premio_pago);
            }
        }
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
            tipoApostaLinhaView.setupView(listTipoApostaLinha, estiloMKP.getCorClara(), estiloMKP.getCorFonteFundoClaro(), estiloMKP.getCorFonteFundoClaro(), TipoApostaLinhaView.TriangleDirection.DOWN);
        }
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
    private boolean isApostaPremioPago(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO;
    }
    private boolean isEspecial(String tipoConcurso){
        return tipoConcurso.equalsIgnoreCase("2");
    }
    private boolean chamaServicoApostaDetalhePremio(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA ||
                aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO;
    }

    private void servicoResultadoModalidadeConcursos() {
        AlertDialogUtils.show(DetalheMinhasApostasTeimosinhaActivity.this);

        String txtModalidade = ApostaDTO.lowerCaseFromString(aposta.getModalidade());
        Integer concursoInicial = aposta.getConcursoInicial();
        Integer concursoFinal = Integer.valueOf((aposta.getConcursoInicial() + aposta.getQuantidadeTeimosinhas() - 1));

        ApostaSilceBO.getInstance().getResultadoModalidadeConcurso(txtModalidade, concursoInicial, concursoFinal, new RequestListener<ResultadoConcursosDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursosDTOResponse response) {
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalheMinhasApostasTeimosinhaActivity.this);
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
                apresentaErro();
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
                apresentaErro();
            }
        });
    }

    private void apresentaErro() {

        ViewUtils.alertFecharCustom(this, this.getString(R.string.erro_minas_apostas_teimosinha),
                () -> { this.finish(); }
        );
    }

    private void preencheCentro() {
        detalhesMinhasApostasButtonHelper.setupDTO(comprovanteApostaDTO, detalhesPremioDTO, aposta);

        List<ItemMinhasApostasDTO> items = new ArrayList<>();

        if (listResultadoConcursoDTO != null && listResultadoConcursoDTO.size() > 0) {
            for (ResultadoConcursoDTO resultadoConcursoDTO : listResultadoConcursoDTO) {

                String situacao = getString(R.string.label_concurso_nao_premiado);
                BigDecimal valorPremio = null;
                if (detalhesPremioDTO != null) {
                    for (ConcursoPremiadoDTO concursoPremiado : detalhesPremioDTO.getPremio().getConcursos()) {
                        if (concursoPremiado.getConcurso().getNumero().equals(resultadoConcursoDTO.getConcurso().getNumero())) {
                            situacao = concursoPremiado.getSituacao();
                            valorPremio = concursoPremiado.getValorLiquido();
                            break;
                        }
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
            if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
                items.add(new ItemMinhasApostasDTO(aposta.getModalidade(),
                        StringUtils.formatToStringListFlexPrimeiro(aposta.getNumerosSelecionados())));
            } else {
                items.add(new ItemMinhasApostasDTO(aposta.getModalidade(),
                        StringUtils.formatToStringList(aposta.getNumerosSelecionados())));
            }
        }


        RecyclerView recyclerView = findViewById(R.id.rv_accordion_teimosinha);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetalheMinhasApostasAdapter adapter = new DetalheMinhasApostasAdapter(this, items);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }
}