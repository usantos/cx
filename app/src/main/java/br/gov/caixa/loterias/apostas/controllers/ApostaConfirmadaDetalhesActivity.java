package br.gov.caixa.loterias.apostas.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.model.ApostaFavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaBolaoDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaDetalhesFragment;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;
import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

/**
 * Created by pmotta on 22/03/2018.
 * Class ApostaConfirmadaDetalhesActivity
 */

public class ApostaConfirmadaDetalhesActivity extends AppCompatActivity implements ConferirResultadoHandler {
    public static final String ARG_COMPROVANTE = "ARG_COMPROVANTE";
    public static final String ARG_DETALHES_PREMIO = "ARG_DETALHES_PREMIO";
    public static final String ARG_RESULTADO_CONCURSO = "ARG_RESULTADO_CONCURSO";
    public static final String ARG_DETALHES_HISTORICO = "ARG_DETALHES_HISTORICO";
    public static final String ARG_SITUACAO = "ARG_SITUACAO";

    private ApostaFavoritaModel model;
    private ApostaBO apostaBO;

    private ComprovanteApostaDTO comprovanteApostaDTO;
    private DetalhesPremioDTO detalhesPremioDTO;
    private ResultadoConcursoDTO resultadoConcurso;
    private DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO;
    private DTOEnumLong situacaoAposta;

    private FrameLayout fragmentContainer, linearLayoutBottom;

    private Toolbar toolbar;

    private TextView validadePremioLabel, validadePremioText, editarTxt;

    private RelativeLayout layoutnomeApostaLayout, favoritadoLayout,
            favoriteBtn, layoutTextoSalveAposta, editarApostaBtn,
            carrinhoApostasBtn, adicionarCarrinhoBtn;
    private ImageView salvarApostaImg, validadePremioImg;

    private LinearLayout  ll_adic_fav;
    private ConstraintLayout comprovanteLayout, informacaoApostaLayout, containerConteudo;

    private EditText nomeApostaEditTxt;
    private View id_view_divisao, linhaSeparadora;

    private EstiloModalidade estilo;
    private EstiloModalidadeMKP estiloMKP;
    private IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;
    private IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> apostaMatriz;

    private boolean premioPago;
    private ApostaFavoritaDTO apostaFavoritaDTO;
    private ParametroSimulacao parametroSimulacao;
    private ComprovanteApostaDTO comprovanteParaPdf;
    private boolean isHistorico;
    private int RESGATOU_MP = 2000;

    private BigDecimal valorPremio;
    private ComprovanteApostaDTOResponse comprovanteApostaDTOResponse;
    private DetalhesPremioDTOResponse detalhesPremioDTOResponse;
    private DetalhesHistoricoPremioDTOResponse detalhesHistoricoPremioDTOResponse;
    private ApostaConfirmadaPartidaDetalhesFragment partidaFragment;
    private ApostaConfirmadaNumeroDetalhesFragment numerosFragment;
    private ApostaConfirmadaBolaoDetalhesFragment numerosBolaoFragment;

    private boolean isBuscandoSituacaoSilce = false;
    private boolean criouFragmentDetalhe = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aposta_confirmada_detalhes);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));

        model = new ApostaFavoritaModel(ApostaConfirmadaDetalhesActivity.this);
        apostaBO = ApostaBO_.getInstance_(this);

        pegaExtras();
        setaViews();
        aplicaCorFontes();
        setupToolbar();
        setaMetodos();

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());

        if(ConfiguracaoAppSingleton.getInstance().isMicroServico() && resultadoConcurso != null){
            apresentaBySituacao(aposta.getSituacao());
        } else {
            getResultadoModalidade(ApostaDTO.lowerCaseFromString(aposta.getModalidade()),aposta.getConcursoInicial().toString());
        }
    }

    private void aplicaCorFontes() {
        if (isApostaBolao(aposta) && aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
            validadePremioLabel.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
            validadePremioText.setTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
            validadePremioImg.setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
            linhaSeparadora.setBackgroundColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CollectionUtils.isEmpty(aposta.getNumerosSelecionados())) {
            favoriteBtn.setVisibility(View.GONE);
        }
        if(aposta.getModalidade() == ModalidadeEnum.LOTECA){
            ll_adic_fav.setVisibility(View.GONE);
            id_view_divisao.setVisibility(View.GONE);
        } else if (aposta.getIndicadorCotaBolao()){
            editarApostaBtn.setVisibility(View.GONE);
            ll_adic_fav.setVisibility(View.GONE);
        } else {
            id_view_divisao.setVisibility(View.VISIBLE);
        }
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            comprovanteApostaDTO = (ComprovanteApostaDTO) bundle.getSerializable(ARG_COMPROVANTE);
            detalhesPremioDTO = (DetalhesPremioDTO) bundle.getSerializable(ARG_DETALHES_PREMIO);
            resultadoConcurso = (ResultadoConcursoDTO) bundle.getSerializable(ARG_RESULTADO_CONCURSO);
            detalhesHistoricoPremioDTO = (DetalhesHistoricoPremioDTO) bundle.getSerializable(ARG_DETALHES_HISTORICO);
            situacaoAposta = (DTOEnumLong) bundle.getSerializable(ARG_SITUACAO);
        }

        if (comprovanteApostaDTO != null) {
            premioPago = false;
            isHistorico = false;
            aposta = comprovanteApostaDTO.getAposta();
            if (comprovanteApostaDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)){
                apostaMatriz = comprovanteApostaDTO.getAposta();
                apostaMatriz.setNumerosSelecionados(comprovanteApostaDTO.getAposta().getMatrizNumerosSelecionados());
            }
            valorPremio = null;
        } else {
            premioPago = Boolean.TRUE;
            if (detalhesPremioDTO != null) {
                aposta = detalhesPremioDTO.getAposta();
                if (detalhesPremioDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)){
                    apostaMatriz = detalhesPremioDTO.getAposta();
                    apostaMatriz.setNumerosSelecionados(detalhesPremioDTO.getAposta().getMatrizNumerosSelecionados());
                }
                valorPremio = detalhesPremioDTO.getPremio().getValorLiquido();
                isHistorico = false;
            } else {
                aposta = detalhesHistoricoPremioDTO.getAposta();
                if (detalhesHistoricoPremioDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)){
                    apostaMatriz = detalhesHistoricoPremioDTO.getAposta();
                    apostaMatriz.setNumerosSelecionados(detalhesHistoricoPremioDTO.getAposta().getMatrizNumerosSelecionados());
                }
                valorPremio = detalhesHistoricoPremioDTO.getPremioDTO().getValorLiquido();
                isHistorico = true;
            }
        }

        if (Objects.equals(aposta.getSituacao().getValor(), SituacaoResultadoBilheteEnum.EFETIVADA.getValor())) {
            if(!ConfiguracaoAppSingleton.getInstance().isMicroServico() || aposta.getSituacao().getDescricao().contains("Aposta não conferida")) {
                isBuscandoSituacaoSilce = true;
                ConferirResultadoHelper.conferirResultadoHandler = this;
                ConferirResultadoHelper.conferirResultado(aposta.getId(), this);
            }
        } else {
            inibirTeim();
        }

        chamadasParaComprovante();
    }

    private void setaViews(){
        //estilo = new EstiloModalidade(aposta.getModalidade());
        estilo = new EstiloModalidade(aposta); //Colocar EstiloModalidadeMKP
        estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade()); //Colocar EstiloModalidadeMKP
        if(!aposta.getIndicadorCotaBolao()){
            setTheme(estilo.getThemeId());
        }

        fragmentContainer       = findViewById(R.id.fragmentContainer);
        linearLayoutBottom      = findViewById(R.id.linearLayoutBottom);
        toolbar                 = findViewById(R.id.toolbar);
        validadePremioText      = findViewById(R.id.validadePremioText);
        validadePremioLabel     = findViewById(R.id.validadePremioLabel);
        validadePremioImg       = findViewById(R.id.validadePremioImage);
        editarTxt               = findViewById(R.id.editarTxt);
        layoutnomeApostaLayout  = findViewById(R.id.layoutnomeApostaLayout);
        favoritadoLayout        = findViewById(R.id.favoritadoLayout);
        favoriteBtn             = findViewById(R.id.favoriteBtn);
        layoutTextoSalveAposta  = findViewById(R.id.layoutTextoSalveAposta);
        editarApostaBtn         = findViewById(R.id.editarApostaBtn);
        ll_adic_fav             = findViewById(R.id.ll_adic_fav);
        informacaoApostaLayout  = findViewById(R.id.informacaoApostaLayout);
        containerConteudo       = findViewById(R.id.container_conteudo);
        nomeApostaEditTxt       = findViewById(R.id.nomeApostaEditTxt);
        id_view_divisao         = findViewById(R.id.id_view_divisao);
        comprovanteLayout       = findViewById(R.id.comprovanteLayout);
        carrinhoApostasBtn      = findViewById(R.id.carrinhoApostasBtn);
        adicionarCarrinhoBtn    = findViewById(R.id.adicionarCarrinhoBtn);
        salvarApostaImg         = findViewById(R.id.salvarApostaImg);
        linhaSeparadora         = findViewById(R.id.linhaSeparadora);
    }

    private void setaMetodos(){
        carrinhoApostasBtn.setOnClickListener(v -> carrinhoApostasBtn());
        favoriteBtn.setOnClickListener(v-> favoriteBtn());
        favoritadoLayout.setOnClickListener(v -> favoritadoLayout());
        layoutTextoSalveAposta.setOnClickListener(v -> layoutTextoSalveAposta());
        editarApostaBtn.setOnClickListener(v -> editarApostaBtn());
        adicionarCarrinhoBtn.setOnClickListener(v -> adicionarCarrinhoBtn());
        comprovanteLayout.setOnClickListener(v -> comprovanteLayout());
        salvarApostaImg.setOnClickListener(v -> salvarApostaImg());
    }

    private void chooseFragmentToPresent() {
        if (!isBuscandoSituacaoSilce && !criouFragmentDetalhe){
            switch (aposta.getModalidade()) {
                case LOTOGOL:
                    disableButtonEditar();
                    partidaFragment = FragmentUtils.startApostaConfirmadaPartidaDetalhes(getSupportFragmentManager(),
                            R.id.fragmentContainer, aposta, null, comprovanteApostaDTO, valorPremio, false);
                    break;
                case LOTECA:
                    disableButtonEditar();
                    if (aposta.getIndicadorCotaBolao()){
                        FragmentUtils.startApostaConfirmadaPartidaDetalhesBolao(getSupportFragmentManager(), R.id.fragmentContainer, aposta, resultadoConcurso, comprovanteApostaDTO, valorPremio);
                    } else {
                        partidaFragment = FragmentUtils.startApostaConfirmadaPartidaDetalhes(getSupportFragmentManager(),
                                R.id.fragmentContainer, aposta, resultadoConcurso, comprovanteApostaDTO, valorPremio, true);
                    }
                    break;
                default:
                    if (aposta.getIndicadorCotaBolao()){
                        numerosBolaoFragment = FragmentUtils.startApostaConfirmadaBolaoDetalhes(getSupportFragmentManager(),
                                R.id.fragmentContainer, aposta, resultadoConcurso, comprovanteApostaDTO, valorPremio);
                    } else {
                        numerosFragment = FragmentUtils.startApostaConfirmadaDetalhes(getSupportFragmentManager(),
                                R.id.fragmentContainer, aposta, resultadoConcurso, comprovanteApostaDTO, valorPremio);
                    }
            }
            criouFragmentDetalhe = true;
        }

        linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, getCorClara()));
        informacaoApostaLayout.setBackgroundColor(ContextCompat.getColor(this, getCorEscura()));
        if (aposta.getIndicadorCotaBolao()){
            containerConteudo.setBackgroundColor(ContextCompat.getColor(this, getCorEscura()));
        } else {
            fragmentContainer.setBackgroundColor(ContextCompat.getColor(this, getCorClara()));
        }
    }

    private int getCorEscura() {
        if (isApostaBolao(aposta)) {
            if (estiloMKP != null) {
                return estiloMKP.getCorEscura();
            }
            estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());
            return estiloMKP.getCorEscura();
        } else {
            if (estilo != null) {
                return estilo.getCorEscura();
            }
            estilo = new EstiloModalidade(aposta);
            return estilo.getCorEscura();
        }
    }

    private int getCorClara() {
        if (isApostaBolao(aposta)) {
            if (estiloMKP != null) {
                return estiloMKP.getCorClara();
            }
            estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());
            return estiloMKP.getCorClara();
        } else {
            if (estilo != null) {
                return estilo.getCorClara();
            }
            estilo = new EstiloModalidade(aposta);
            return estilo.getCorClara();
        }
    }

    private void carrinhoApostasBtn() {
        AlertDialogUtils.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ApostaConfirmadaDetalhesActivity.this);
                }
                Bundle args = new Bundle();
                args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                startActivity(new Intent(ApostaConfirmadaDetalhesActivity.this, CarrinhoActivity.class).putExtras(args));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    startActivity(new Intent(ApostaConfirmadaDetalhesActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true));
                } else {
                    RedirectNetwork.checkRedirect( error, ApostaConfirmadaDetalhesActivity.this);
                }
            }
        });
    }

    private void favoriteBtn() {
        if (layoutnomeApostaLayout.getVisibility() == View.VISIBLE){
            layoutnomeApostaLayout.setVisibility(View.GONE);
        } else {
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        }
        if(favoritadoLayout.getVisibility() == View.VISIBLE){
            favoritadoLayout.setVisibility(View.INVISIBLE);
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        }
        favoriteBtn.setVisibility(View.VISIBLE);
    }

    private void favoritadoLayout() {
        if(favoritadoLayout.getVisibility() == View.VISIBLE){
            favoritadoLayout.setVisibility(View.INVISIBLE);
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.VISIBLE);
        }
    }

    private void layoutTextoSalveAposta() {
        layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        layoutTextoSalveAposta.setClickable(false);
    }

    private void editarApostaBtn() {
        if (CollectionUtils.isNotEmpty(aposta.getNumerosSelecionados())) {
            SimularApostaActivity_.intent(this).aposta(aposta)
                                  .extra(getResources().getString(R.string.extra_tipo_aposta), aposta.getModalidade())
                                  .extra(getResources().getString(R.string.extra_modalidade), (new Gson()).toJson(parametroSimulacao.getParametroJogo()))
                                  .start();
        }
    }

    private void adicionarCarrinhoBtn() {
        if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(ApostaConfirmadaDetalhesActivity.this, 1)) {
            ModalidadeEnum modalidade = (aposta!= null && aposta.getModalidade()!= null) ? aposta.getModalidade(): null;
            if (modalidade != null && !modalidade.equals(ModalidadeEnum.LOTECA)){
                if (!modalidade.equals(ModalidadeEnum.SUPER_7)) {
                    aposta.setNumerosSelecionados(aposta.getListaNumerosSelecionados());
                } else {
                    AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(this, apostaMatriz);
                    return;
                }
            }

            AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(this, aposta);
        }
    }

    private void salvarApostaImg() {
        if (StringUtils.isNotEmpty(nomeApostaEditTxt.getText().toString())) {
            AlertDialogUtils.show(this);

            ParametroJogoDTO parametroJogo;
            apostaFavoritaDTO = new ApostaFavoritaDTO();
            if (parametroSimulacao != null && parametroSimulacao.getParametroJogo() != null) {

                parametroJogo = parametroSimulacao.getParametroJogo();
                DTOEnumInteger modalidade = new DTOEnumInteger();
                modalidade.setDescricao(parametroJogo.getConcurso().getModalidadeDetalhada().getDescricao());
                modalidade.setValor(parametroJogo.getConcurso().getModalidadeDetalhada().getValor());
                apostaFavoritaDTO.setModalidade(modalidade);
            }
            if (nomeApostaEditTxt != null && nomeApostaEditTxt.getText() != null && nomeApostaEditTxt.getText().toString() != null){
                if(nomeApostaEditTxt.getText().toString().length() > 25){
                    apostaFavoritaDTO.setNome(nomeApostaEditTxt.getText().toString().substring(0, 25));
                } else {
                    apostaFavoritaDTO.setNome(nomeApostaEditTxt.getText().toString());
                }
            } else {
                apostaFavoritaDTO.setNome("");
            }
            if(aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                apostaFavoritaDTO.setParametroTrevo(new ParametroTrevo());

                apostaFavoritaDTO.getParametroTrevo().setTrevosSelecionados(aposta.getTrevosSelecionados());
            }
            if(aposta.getModalidade() == ModalidadeEnum.SUPER_7){
                apostaFavoritaDTO.setNumerosSelecionados(aposta.getMatrizNumerosSelecionados());
            } else {
                apostaFavoritaDTO.setNumerosSelecionados(aposta.getListaNumerosSelecionados());
            }
            apostaFavoritaDTO.setTimeDoCoracao(aposta.getTimeDoCoracao());
            apostaFavoritaDTO.setMesDeSorte(aposta.getMesDeSorte());


            model.validaAposta(apostaFavoritaDTO, onValidaApostaListener());

        } else {
            ViewUtils.alert(this, getString(R.string.msg_favor_inserir_nome_aposta));
        }
    }

    private OnSilceListener<NetworkResponse> onValidaApostaListener() {
        return new OnSilceListener<NetworkResponse>() {
            @Override
            public void success(NetworkResponse payload) {
                salvarAposta();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                if(isErroNegocial(error)){
                    ViewUtils.alertTitleButtonYesOrNotListener(
                            ApostaConfirmadaDetalhesActivity.this,
                            MensagensNetwork.getErrorMessage(error),
                            (dialogInterface, i) ->
                                    salvarAposta());
                } else {
                    RedirectNetwork.checkRedirect(error, ApostaConfirmadaDetalhesActivity.this);
                };
            }
        };
    }

    private void salvarAposta(){
        model.salvaAposta(apostaFavoritaDTO, new OnSilceListener<NetworkResponse>() {
            @Override
            public void success(NetworkResponse payload) {
                AlertDialogUtils.dismiss();
                favoritadoLayout.setVisibility(View.VISIBLE);
                layoutnomeApostaLayout.setVisibility(View.GONE);
                favoriteBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void comprovanteLayout() {
        if (premioPago && isHistorico) {
            if (detalhesHistoricoPremioDTOResponse != null && detalhesHistoricoPremioDTOResponse.getPayload() != null){
                PdfUtils.createComprovanteHistoricoPremioPdf(detalhesHistoricoPremioDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
            } else {
                buscaDetalheComprovanteHistoricoAposta(true);
            }
        } else if (premioPago) {
            if(detalhesPremioDTOResponse != null && detalhesPremioDTOResponse.getPayload() != null){
                PdfUtils.createComprovantePremioPagoPdf(detalhesPremioDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
            } else if (detalhesPremioDTO != null){
                PdfUtils.createComprovantePremioPagoPdf(detalhesPremioDTO, ApostaConfirmadaDetalhesActivity.this);
            } else {
                buscarDetalheComprovantePremioAposta(true);
            }
        } else {
            if (comprovanteParaPdf != null){
                PdfUtils.createComprovanteApostaPdf(comprovanteParaPdf, ApostaConfirmadaDetalhesActivity.this);
            } else if (comprovanteApostaDTOResponse != null && comprovanteApostaDTOResponse.getPayload() != null){
                PdfUtils.createComprovanteApostaPdf(comprovanteApostaDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
            } else if (comprovanteApostaDTO != null){
                PdfUtils.createComprovanteApostaPdf(comprovanteApostaDTO, ApostaConfirmadaDetalhesActivity.this);
            } else {
                buscaDetalhesComprovanteAposta(true);
            }
        }
    }

    private void chamadasParaComprovante(){
        if(!AlertDialogUtils.isShow()) {
            AlertDialogUtils.show(this);
        }
        if (premioPago && isHistorico) {
            buscaDetalheComprovanteHistoricoAposta(false);
        } else if (premioPago) {
            buscarDetalheComprovantePremioAposta(false);
        } else {
            buscaDetalhesComprovanteAposta(false);
        }
    }

    private void buscaDetalheComprovanteHistoricoAposta(boolean geraPDF) {
        apostaBO.getApostaHistoricoComprovante(aposta.getId(), new RequestListener<DetalhesHistoricoPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesHistoricoPremioDTOResponse result) {
                AlertDialogUtils.dismiss();
                detalhesHistoricoPremioDTOResponse = result;
                if (geraPDF){
                    comprovanteLayout();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private void buscarDetalheComprovantePremioAposta(boolean geraPDF) {
        apostaBO.getApostaComprovantePremio(aposta.getId(), new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                AlertDialogUtils.dismiss();
                detalhesPremioDTOResponse = result;
                detalhesPremioDTO = result.getPayload();
                if (geraPDF){
                    comprovanteLayout();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private void buscaDetalhesComprovanteAposta(boolean geraPDF) {
        apostaBO.getApostaDetalheComprovante(aposta.getId(), new RequestListener<ComprovanteApostaDTOResponse>() {
            @Override
            public void onResponse(ComprovanteApostaDTOResponse result) {
                AlertDialogUtils.dismiss();
                comprovanteApostaDTOResponse = result;
                comprovanteParaPdf = result.getPayload();
                comprovanteApostaDTO = result.getPayload();
                if (geraPDF){
                    comprovanteLayout();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apostas_confirmadas, menu);

        MenuItem menuDuvida = menu.findItem(R.id.action_duvida);
        Drawable drawable = menuDuvida.getIcon();
        if (drawable != null) {
            if (aposta != null && aposta.getIndicadorCotaBolao()){
                ViewUtils.setColorDrawable(this, drawable, estiloMKP.getCorFonteFundoClaro());
            } else {
                ViewUtils.setColorDrawable(this, drawable, estilo.getMenuItemColor());
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_duvida){
            Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getResultadoModalidade(String txtmodalidade, String concurso) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(this);
        apostaBO.getResultadoModalidade(txtmodalidade,String.valueOf(concurso), new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), ApostaConfirmadaDetalhesActivity.this);
                }
                if(response != null){
                    resultadoConcurso = response.getPayload();
                }
                apresentaBySituacao(aposta.getSituacao());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                //** Estava duplicando ** apresentaBySituacao(aposta.getSituacao());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESGATOU_MP && resultCode == RESULT_OK) {
            finish();
        }
        if (requestCode == RESGATOU_MP && resultCode == RESULT_FIRST_USER) {
            inibirTeim();
        }
    }

    private void inibirTeim() {
        if (aposta.getQuantidadeTeimosinhas() != null && aposta.getQuantidadeTeimosinhas() > 0 && !aposta.getSituacao().getDescricao().contains("não apurado")) {
            ViewUtils.alertTitleButton(this,
                                       R.string.label_atencao ,
                                       getResources().getString(R.string.inibir_teimosinha),
                                       getResources().getString(R.string.title_entendi));
        }
    }

    private void disableButtonEditar() {
        editarTxt.setTextColor(getResources().getColor(R.color.cinza_button));
    }

    private void setupToolbar() {
        if (isApostaBolao(aposta)) {
            if (isEspecial(aposta.getTipoConcurso().getValor())) {
                toolbar.setBackgroundResource(estiloMKP.getImagemEspecialDetalhes());
            } else {
                toolbar.setBackgroundColor(ContextCompat.getColor(this, getCorClara()));
            }
            toolbar.setTitleTextColor(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, getCorClara()));
        }
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
            if (isApostaBolao(aposta)){
                Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.seta_esquerda);
                upArrow.mutate().setColorFilter(ContextCompat.getColor(this, estiloMKP.getCorFonteFundoClaro()), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
        setTitle(ViewUtils.getNomeModalidadePorAposta(aposta).toLowerCase());
    }

    private void getApostaDetalhePremio(Long apostaId, final Context contextParam, ComprovanteApostaDTO comprovante) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                AlertDialogUtils.dismiss();

                if (result.getPayload().getPremio().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(result.getPayload().getPremio().getValorLiquido()) == 0)) {
                    if (numerosFragment != null){
                        numerosFragment.atualizaValorPremio(result.getPayload().getPremio().getValorLiquido());
                    } else if(partidaFragment != null){
                        partidaFragment.atualizaValorPremio(result.getPayload().getPremio().getValorLiquido());
                    }
                }
                if (numerosFragment != null){
                    numerosFragment.setDetalhesPremio(result.getPayload());
                }
                //ResultadoApostaConfirmadaActivity_.intent(contextParam).detalhesPremioDTO(result.getPayload()).comprovante(comprovante).startForResult(RESGATOU_MP);
                Intent intent = IntentUtil.getIntentOrigemDestino((Activity) contextParam, ResultadoApostaConfirmadaActivity.class);
                intent.putExtra(ResultadoApostaConfirmadaActivity.DETALHES_PREMIO_DTO_EXTRA, ((Serializable) result.getPayload()));
                intent.putExtra(ResultadoApostaConfirmadaActivity.COMPROVANTE_EXTRA, ((Serializable) comprovante));
                ((Activity) contextParam).startActivityForResult(intent, RESGATOU_MP);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private void onError(VolleyError error) {
        AlertDialogUtils.dismiss();
        RedirectNetwork.checkRedirect(error,ApostaConfirmadaDetalhesActivity.this);
    }

    private void apresentaBySituacao(DTOEnumLong situacao) {
        aposta.setSituacao(situacao);

        chooseFragmentToPresent();

        if (Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA.getValor(), situacao.getValor())||
                Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA_AINDA_CONCORRENDO.getValor(), situacao.getValor())) {
            getApostaDetalhePremio(aposta.getId(), this, comprovanteApostaDTO);
        }
        if(!situacao.getDescricao().equalsIgnoreCase("Premiada")){
            inibirTeim();
        }
    }

    private boolean isApostaBolao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getIndicadorCotaBolao() != null && aposta.getIndicadorCotaBolao();
    }

    private boolean isEspecial(String tipoConcurso){
        return tipoConcurso.equalsIgnoreCase("2");
    }

    @Override
    public void handle(DTOEnumLong situacao) {
        isBuscandoSituacaoSilce = false;
        apresentaBySituacao(situacao);
    }

    @Override
    public void handle(int position, ApostaConfirmadaHolder holder, DTOEnumLong situacao) {
        isBuscandoSituacaoSilce = false;
    }

    @Override
    public void handleError(ApostaConfirmadaHolder holder, VolleyError error) {
        isBuscandoSituacaoSilce = false;
        RedirectNetwork.checkRedirect(error, ApostaConfirmadaDetalhesActivity.this);
    }
}