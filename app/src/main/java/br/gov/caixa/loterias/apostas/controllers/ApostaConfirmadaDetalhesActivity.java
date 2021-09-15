package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.ApostasConfirmadasAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaNumeroDetalhes_;
import br.gov.caixa.loterias.apostas.view.fragment.ApostaConfirmadaPartidaFragment_;

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

    @Bean
    protected DadosCorporativosBO dadosCorporativosBO;

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
    protected ImageView validadePremioImage, comprovanteImage, comprovanteImpressaoImage;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_gerenciar_apostas_detalhes));
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
            ConferirResultadoHelper.conferirResultadoHandler = this;
            ConferirResultadoHelper.conferirResultado(aposta.getId(), this);
        } else {
            inibirTeim();
        }

        chamadasParaComprovante();
    }

    @AfterViews
    protected void init() {
        setupToolbar();

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());
        if (estilo != null) {
            ViewUtils.setColorDrawable(this, validadePremioImage.getDrawable(), estilo.getIconValidadeComprovanteColor());
            ViewUtils.setColorDrawable(this, comprovanteImage.getDrawable(), estilo.getIconValidadeComprovanteColor());
            ViewUtils.setColorDrawable(this, comprovanteImpressaoImage.getDrawable(), estilo.getIconImpressoraColor());
        }
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
        Fragment fragment = null;

        switch (aposta.getModalidade()) {
            case LOTOGOL:
                disableButtonEditar();
                fragment = ApostaConfirmadaPartidaFragment_.builder().valorPremio(valorPremio).aposta(aposta).loteca(Boolean.FALSE).build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragmentContainer, fragment)
                                           .commit();
                break;
            case LOTECA:
                disableButtonEditar();
                fragment = ApostaConfirmadaPartidaFragment_.builder().valorPremio(valorPremio).aposta(aposta).resultadoConcurso(resultadoConcurso).loteca(Boolean.TRUE).build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragmentContainer, fragment)
                                           .commit();
                break;
            default:
                fragment = ApostaConfirmadaNumeroDetalhes_.builder().valorPremio(valorPremio).aposta(aposta).resultadoConcurso(resultadoConcurso).build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragmentContainer, fragment)
                                           .commit();
                break;
        }
    }

    @Click
    protected void carrinhoApostasBtn() {
        DialogUtils.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                DialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ApostaConfirmadaDetalhesActivity.this);
                }
                Bundle args = new Bundle();
                args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                startActivity(new Intent(ApostaConfirmadaDetalhesActivity.this, CarrinhoActivity.class).putExtras(args));

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
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
            DialogUtils.show(this);

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
            if(aposta.getModalidade() == ModalidadeEnum.SUPER_7){
                apostaFavoritaDTO.setNumerosSelecionados(aposta.getMatrizNumerosSelecionados());

            } else {
                apostaFavoritaDTO.setNumerosSelecionados(aposta.getListaNumerosSelecionados());
            }
            apostaFavoritaDTO.setTimeDoCoracao(aposta.getTimeDoCoracao());
            apostaFavoritaDTO.setMesDeSorte(aposta.getMesDeSorte());

            dadosCorporativosBO.validarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse result) {
                    salvarAposta();
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogUtils.dismiss();
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
            });

        } else {
            ViewUtils.alert(this, getString(R.string.msg_favor_inserir_nome_aposta));
        }
    }

    private void salvarAposta(){
        dadosCorporativosBO.salvarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse result) {
                DialogUtils.dismiss();
                favoritadoLayout.setVisibility(View.VISIBLE);
                layoutnomeApostaLayout.setVisibility(View.GONE);
                favoriteBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    @Click
    protected void comprovanteLayout() {
        if (premioPago && isHistorico) {
            pdfUtils.createComprovanteHistoricoPremioPdf(detalhesHistoricoPremioDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
        } else if (premioPago) {
            pdfUtils.createComprovantePremioPdf(detalhesPremioDTOResponse.getPayload(), ApostaConfirmadaDetalhesActivity.this);
        } else {
            pdfUtils.createComprovanteApostaPdf(comprovanteParaPdf, ApostaConfirmadaDetalhesActivity.this);
        }
    }
    private void chamadasParaComprovante(){
        if(!DialogUtils.isShow()) {
            DialogUtils.show(this);
        }
        if (premioPago && isHistorico) {
            apostaBO.getApostaHistoricoComprovante(aposta.getId(), new RequestListener<DetalhesHistoricoPremioDTOResponse>() {
                @Override
                public void onResponse(DetalhesHistoricoPremioDTOResponse result) {
                    DialogUtils.dismiss();
                    detalhesHistoricoPremioDTOResponse = result;
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(error);
                }
            });
        } else if (premioPago) {
            apostaBO.getApostaComprovantePremio(aposta.getId(), new RequestListener<DetalhesPremioDTOResponse>() {
                @Override
                public void onResponse(DetalhesPremioDTOResponse result) {
                    DialogUtils.dismiss();
                    detalhesPremioDTOResponse = result;
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(error);
                }
            });
        } else {
            apostaBO.getApostaDetalheComprovante(aposta.getId(), new RequestListener<ComprovanteApostaDTOResponse>() {
                @Override
                public void onResponse(ComprovanteApostaDTOResponse result) {
                    DialogUtils.dismiss();
                    comprovanteApostaDTOResponse = result;
                    comprovanteParaPdf = result.getPayload();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(error);
                }
            });
        }
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
            DialogUtils.dismiss();
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
                DialogUtils.dismiss();

                ResultadoApostaConfirmadaActivity_.intent(contextParam).detalhesPremioDTO(result.getPayload()).comprovante(comprovante).startForResult(RESGATOU_MP);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private void onError(VolleyError error) {
        DialogUtils.dismiss();
        RedirectNetwork.checkRedirect(error,ApostaConfirmadaDetalhesActivity.this);
    }

    @Override
    public void handle(DTOEnumLong situacao) {
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
    public void handle(ApostasConfirmadasAdapter.ViewHolder holder, DTOEnumLong situacao) { }

    @Override
    public void handleError(ApostasConfirmadasAdapter.ViewHolder holder, VolleyError error) {
        RedirectNetwork.checkRedirect(error, ApostaConfirmadaDetalhesActivity.this);
    }
}