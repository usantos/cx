package br.gov.caixa.loterias.apostas.controllers;


import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Collections;
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
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheMinhasApostasAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_minhas_apostas_teimosinha);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));

        pegaExtras();
        setaViews();
        aplicaCores();
        setupToolbar();
//        setaMetodos();

        preencheTipoApostaLinha();
        preencheRodape();
        //preencheCentro();

        //if(ConfiguracaoAppSingleton.getInstance().isMicroServico() && resultadoConcurso != null){
        //    apresentaBySituacao(aposta.getSituacao());
        //} else {
        servicoResultadoModalidadeConcursos();
        //}
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
//                premioPago = false;
//                if (comprovanteApostaDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)){
//                    apostaMatriz = comprovanteApostaDTO.getAposta();
//                    apostaMatriz.setNumerosSelecionados(comprovanteApostaDTO.getAposta().getMatrizNumerosSelecionados());
//                }
//                valorPremio = null;
//            } else {
//                premioPago = Boolean.TRUE;
//                if (detalhesPremioDTO != null) {
//                    aposta = detalhesPremioDTO.getAposta();
//                    if (detalhesPremioDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)){
//                        apostaMatriz = detalhesPremioDTO.getAposta();
//                        apostaMatriz.setNumerosSelecionados(detalhesPremioDTO.getAposta().getMatrizNumerosSelecionados());
//                    }
//                    valorPremio = detalhesPremioDTO.getPremio().getValorLiquido();
//                } else {
//                    aposta = detalhesHistoricoPremioDTO.getAposta();
//                    if (detalhesHistoricoPremioDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)){
//                        apostaMatriz = detalhesHistoricoPremioDTO.getAposta();
//                        apostaMatriz.setNumerosSelecionados(detalhesHistoricoPremioDTO.getAposta().getMatrizNumerosSelecionados());
//                    }
//                    valorPremio = detalhesHistoricoPremioDTO.getPremioDTO().getValorLiquido();
//                }
//            }
//
//            if (Objects.equals(aposta.getSituacao().getValor(), SituacaoResultadoBilheteEnum.EFETIVADA.getValor())) {
//                if(!ConfiguracaoAppSingleton.getInstance().isMicroServico() || aposta.getSituacao().getDescricao().contains("Aposta não conferida")) {
//                    isBuscandoSituacaoSilce = true;
//                    ConferirResultadoHelper.conferirResultadoHandler = this;
//                    ConferirResultadoHelper.conferirResultado(aposta.getId(), this);
//                }
//            } else {
//                inibirTeim();
//            }
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

    private void preencheCabecalho() {
        statusApostaTxt.setText(aposta.getSituacao().getDescricao());

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
            tv_val_premio.setText(ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));
            clResgatePremio.setVisibility(View.VISIBLE);
        } else {
            tv_val_premio_tit.setVisibility(TextView.GONE);
            tv_val_premio.setVisibility(TextView.GONE);
            clResgatePremio.setVisibility(View.GONE);
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
                        //List<List<Double>> listListSelecionado = (List<List<Double>>) aposta.getNumerosSelecionados();

                        items.add(new ItemMinhasApostasDTO(resultadoConcursoDTO.getConcurso().getModalidade(), resultadoConcursoDTO.getConcurso().getNumero().toString(),
                                situacao,
                                StringUtils.formatToStringListFlexPrimeiro(resultado),
                                StringUtils.formatToStringListFlexPrimeiro(aposta.getNumerosSelecionados()),
                                valorPremio));
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
        } //else {
                //Concurso nao apurado
                //items.add(new ItemMinhasApostasDTO(ModalidadeEnum.LOTOFACIL,
                //                Arrays.asList("01","02","03","04","05","06","07","08")));
        //}

        RecyclerView recyclerView = findViewById(R.id.rv_accordion_teimosinha);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetalheMinhasApostasAdapter adapter = new DetalheMinhasApostasAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }
}