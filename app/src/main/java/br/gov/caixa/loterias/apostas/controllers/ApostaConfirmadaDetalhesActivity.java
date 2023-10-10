package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
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
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhes;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhes_;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaFragment_;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;
import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

/**
 * Created by pmotta on 22/03/2018.
 * Class ApostaConfirmadaDetalhesActivity
 */

@EActivity(R.layout.activity_aposta_confirmada_detalhes)
@OptionsMenu(R.menu.menu_apostas_confirmadas)
public class ApostaConfirmadaDetalhesActivity extends AppCompatActivity implements ConferirResultadoHandler {

    private ApostaFavoritaModel model;

    @Bean
    protected PdfUtils pdfUtils;

    @Bean
    protected ApostaBO apostaBO;

    @Extra
    protected ComprovanteApostaDTO comprovanteApostaDTO;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    @Extra
    protected ResultadoConcursoDTO resultadoConcurso;

    @Extra
    protected DetalhesHistoricoPremioDTO detalhesHistoricoPremioDTO;

    @Extra
    protected DTOEnumLong situacaoAposta;

    @ViewById
    protected FrameLayout fragmentContainer;

    @ViewById
    protected FrameLayout linearLayoutBottom;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected TextView validadePremioText, comprovanteText, editarTxt;

    @ViewById
    protected RelativeLayout layoutnomeApostaLayout, favoritadoLayout, favoriteBtn, comprovanteLayout, layoutTextoSalveAposta;

    @ViewById
    protected LinearLayout  ll_adic_fav;

    @ViewById
    protected EditText nomeApostaEditTxt;

    private EstiloModalidade estilo;
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
    private ApostaConfirmadaPartidaFragment partidaFragment;
    private ApostaConfirmadaNumeroDetalhes numerosFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));

        model = new ApostaFavoritaModel(ApostaConfirmadaDetalhesActivity.this);

        setaMetodos();
        setaViews();

    }

    private void setaViews(){
        estilo = new EstiloModalidade(aposta.getModalidade());
        setTheme(estilo.getThemeId());

        setTitle(ViewUtils.getNomeModalidadePorAposta(aposta));
    }

    private void setaMetodos(){
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
                ConferirResultadoHelper.conferirResultadoHandler = this;
                ConferirResultadoHelper.conferirResultado(aposta.getId(), this);
            }
        } else {
            inibirTeim();
        }

        chamadasParaComprovante();
    }

    @AfterViews
    protected void init() {
        if(ConfiguracaoAppSingleton.getInstance().isMicroServico()){
            apresentaBySituacao(aposta.getSituacao());
        }
        setupToolbar();

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());
        if (premioPago) {
            if (detalhesPremioDTO != null) {
                comprovanteText.setText(detalhesPremioDTO.getNsbi());
            } else {
                comprovanteText.setText(getString(R.string.label_nsu_pagamento_nsu, String.valueOf(detalhesHistoricoPremioDTO.getPremioDTO().getNsuPagamento())));
            }
        } else if (StringUtils.isNotEmpty(comprovanteApostaDTO.getNsbi())) {
            comprovanteText.setText(comprovanteApostaDTO.getNsbi());
        } else {
            comprovanteLayout.setVisibility(View.INVISIBLE);
        }

        if (CollectionUtils.isEmpty(aposta.getNumerosSelecionados())) {
            favoriteBtn.setVisibility(View.GONE);
        }

        getResultadoModalidade(ApostaDTO.lowerCaseFromString(aposta.getModalidade()),aposta.getConcursoInicial().toString());
        if(aposta.getModalidade() == ModalidadeEnum.LOTECA){
            ll_adic_fav.setVisibility(View.GONE);
        }

    }

    private void chooseFragmentToPresent() {

        switch (aposta.getModalidade()) {
            case LOTOGOL:
                disableButtonEditar();
                partidaFragment = ApostaConfirmadaPartidaFragment_.builder()
                                                                  .valorPremio(valorPremio)
                                                                  .aposta(aposta).loteca(Boolean.FALSE)
                                                                  .comprovanteApostaDTO(comprovanteApostaDTO)
                                                                  .build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragmentContainer, partidaFragment)
                                           .commit();
                break;
            case LOTECA:
                disableButtonEditar();
                partidaFragment = ApostaConfirmadaPartidaFragment_.builder()
                                                                  .valorPremio(valorPremio)
                                                                  .aposta(aposta)
                                                                  .resultadoConcurso(resultadoConcurso)
                                                                  .loteca(Boolean.TRUE)
                                                                  .comprovanteApostaDTO(comprovanteApostaDTO)
                                                                  .build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragmentContainer, partidaFragment)
                                           .commit();
                break;
            default:
                numerosFragment = ApostaConfirmadaNumeroDetalhes_.builder().valorPremio(valorPremio).aposta(aposta).resultadoConcurso(resultadoConcurso).comprovanteApostaDTO(comprovanteApostaDTO).build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragmentContainer, numerosFragment)
                                           .commitAllowingStateLoss();
                break;
        }
    }

    private int getCorEscura() {
        if (estilo != null){
            return estilo.getCorEscura();
        }
        estilo = new EstiloModalidade(aposta.getModalidade());
        return estilo.getCorEscura();
    }

    @Click
    protected void carrinhoApostasBtn() {
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
                    RedirectNetwork.checkRedirect( error, ApostaConfirmadaDetalhesActivity.this );
                }
            }
        });
    }

    @Click
    protected void favoriteBtn() {
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

    @Click
    protected void favoritadoLayout() {
        if(favoritadoLayout.getVisibility() == View.VISIBLE){
            favoritadoLayout.setVisibility(View.INVISIBLE);
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.VISIBLE);
        }
    }

    @Click
    protected void layoutTextoSalveAposta() {
        layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        layoutTextoSalveAposta.setClickable(false);
    }

    @Click
    protected void editarApostaBtn() {
        if (CollectionUtils.isNotEmpty(aposta.getNumerosSelecionados())) {
            SimularApostaActivity_.intent(this).aposta(aposta)
                                  .extra(getResources().getString(R.string.extra_tipo_aposta), aposta.getModalidade())
                                  .extra(getResources().getString(R.string.extra_modalidade), (new Gson()).toJson(parametroSimulacao.getParametroJogo()))
                                  .start();
        }
    }

    @Click
    protected void adicionarCarrinhoBtn() {
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

    @Click
    protected void salvarApostaImg() {
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

    @Click
    protected void comprovanteLayout() {
        if (premioPago && isHistorico) {
            if (detalhesHistoricoPremioDTOResponse != null && detalhesHistoricoPremioDTOResponse.getPayload() != null){
                pdfUtils.createComprovanteHistoricoPremioPdf(detalhesHistoricoPremioDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
            } else {
                buscaDetalheComprovanteHistoricoAposta(true);
            }
        } else if (premioPago) {
            if(detalhesPremioDTOResponse != null && detalhesPremioDTOResponse.getPayload() != null){
                pdfUtils.createComprovantePremioPdf(detalhesPremioDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
            } else if (detalhesPremioDTO != null){
                pdfUtils.createComprovantePremioPdf(detalhesPremioDTO, ApostaConfirmadaDetalhesActivity.this);
            } else {
                buscarDetalheComprovantePremioAposta(true);
            }
        } else {
            if (comprovanteParaPdf != null){
                pdfUtils.createComprovanteApostaPdf(comprovanteParaPdf, ApostaConfirmadaDetalhesActivity.this);
            } else if (comprovanteApostaDTOResponse != null && comprovanteApostaDTOResponse.getPayload() != null){
                pdfUtils.createComprovanteApostaPdf(comprovanteApostaDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
            } else if (comprovanteApostaDTO != null){
                pdfUtils.createComprovanteApostaPdf(comprovanteApostaDTO, ApostaConfirmadaDetalhesActivity.this);
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

    @OptionsMenuItem(R.id.action_duvida)
    protected void menuDuvida(MenuItem menuDuvida) {
        Drawable drawable = menuDuvida.getIcon();
        if (drawable != null && estilo != null) {
            ViewUtils.setColorDrawable(this, drawable, estilo.getMenuItemColor());
        }
    }

    @OptionsItem(R.id.action_duvida)
    protected void onMenuDuvida() {
        Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
        startActivity(intent);
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
                chooseFragmentToPresent();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                // RedirectNetwork.checkRedirect(error, ApostaConfirmadaDetalhesActivity.this);
                chooseFragmentToPresent();
            }
        });
    }

    private void onApostasConfirmadasError(VolleyError error) {
        if (this != null) {
            AlertDialogUtils.dismiss();
            RedirectNetwork.checkRedirect(error, this);
        }
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
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }
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
                ResultadoApostaConfirmadaActivity_.intent(contextParam).detalhesPremioDTO(result.getPayload()).comprovante(comprovante).startForResult(RESGATOU_MP);
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

    @Override
    public void handle(DTOEnumLong situacao) {
        apresentaBySituacao(situacao);
    }

    @Override
    public void handle(int position, ApostaConfirmadaHolder holder, DTOEnumLong situacao) { }

    @Override
    public void handleError(ApostaConfirmadaHolder holder, VolleyError error) {
        RedirectNetwork.checkRedirect(error, ApostaConfirmadaDetalhesActivity.this);
    }
}