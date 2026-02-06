package br.gov.caixa.loterias.apostas.controllers;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;

public class DetalheMinhasApostasTeimosinhaActivity extends AppCompatActivity {
    public static final String ARG_COMPROVANTE = "ARG_COMPROVANTE";
    public static final String ARG_DETALHES_PREMIO = "ARG_DETALHES_PREMIO";
    public static final String ARG_RESULTADO_CONCURSO = "ARG_RESULTADO_CONCURSO";
    public static final String ARG_DETALHES_HISTORICO = "ARG_DETALHES_HISTORICO";
    //public static final String ARG_SITUACAO = "ARG_SITUACAO";

    private ComprovanteApostaDTO comprovanteApostaDTO;

    private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

    private ConstraintLayout clScroll, clResgatePremio;
    private ImageView ivResgatePremio;

    private View divider;
    private TextView concursoLabel, concursoTxt, quantidadeApostadasTxt, dataAposta, dataApostaTxt, horaApostaTxt;
    private TextView statusApostaTxt, tv_val_premio, tv_val_premio_tit;
    private TextView ResgatarPremioTxt;

    private TipoApostaLinhaView tipoApostaLinhaView;

    private EstiloModalidadeMKP estiloMKP;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_minhas_apostas_teimosinha);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));

        //model = new ApostaFavoritaModel(DetalheMinhasApostasActivity.this);
        //apostaSilceBO = ApostaSilceBO.getInstance();

        pegaExtras();
        setaViews();
        aplicaCores();
//        setupToolbar();
//        setaMetodos();

        preencheCabecalho();
        preencheTipoApostaLinha();
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
        tv_val_premio = findViewById(R.id.tv_val_premio);

        tipoApostaLinhaView = findViewById(R.id.tipo_aposta_linha);

        clResgatePremio = findViewById(R.id.clResgatePremio);
        ivResgatePremio = findViewById(R.id.ivResgatePremio);
        ResgatarPremioTxt = findViewById(R.id.ResgatarPremioTxt);

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

        clResgatePremio.setBackgroundResource(estiloMKP.getCorFonteFundoEscuro());
        ivResgatePremio.setBackgroundResource(estiloMKP.getCorClara());
        ResgatarPremioTxt.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorClara()));

    }

    private void preencheCabecalho() {
        statusApostaTxt.setText(aposta.getSituacao().getDescricao());

        concursoTxt.setText(aposta.getConcursoInicial().toString());

        if (aposta.getQuantidadeTeimosinhas() == 0) {
            quantidadeApostadasTxt.setVisibility(View.GONE);
        } else {
            concursoTxt.setText(aposta.getConcursoInicial().toString() + " - " +
                    (aposta.getConcursoInicial()+aposta.getQuantidadeTeimosinhas()));

            if (aposta.getQuantidadeTeimosinhas() == 1) {
                quantidadeApostadasTxt.setText(aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_aposta));
            } else {
                quantidadeApostadasTxt.setText(aposta.getQuantidadeTeimosinhas() + " " + this.getString(R.string.label_apostas));
            }
        }

        dataApostaTxt.setText(aposta.getDataEfetivacao());
        horaApostaTxt.setText(StringUtils.transformToHorahMinuto(aposta.getHoraEfetivacao()));

        if (isApostaPremiada(aposta)){
            //tv_val_premio.setText(ViewUtils.getMoedaFormat(getValorDoPremio()));
            clResgatePremio.setVisibility(View.VISIBLE);
        } else {
            tv_val_premio_tit.setVisibility(TextView.GONE);
            tv_val_premio.setVisibility(TextView.GONE);
            clResgatePremio.setVisibility(View.GONE);
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
}