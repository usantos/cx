package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import org.parceler.Parcels;

import java.util.Objects;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.PdfUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostar.interno.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostar.interno.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ApostasConfirmadasAdapter;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ApostaConfirmadaNumeroDetalhesPremioPago_;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ApostaConfirmadaNumeroDetalhes_;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ApostaConfirmadaPartidaFragment_;

import static br.gov.caixa.loterias.apostar.interno.view.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostar.interno.view.CarrinhoActivity.LER_CARRINHO_LOCAL;

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
    protected ResultadoConcursoDTO resultadoConcursoDTO;

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
    protected EditText nomeApostaEditTxt;

    private EstiloModalidade estilo;
    private IdentificaoDeUmaApostaDas8Modalidades aposta;
    private boolean premioPago;
    private ApostaFavoritaDTO apostaFavoritaDTO;
    private ParametroSimulacao parametroSimulacao;
    private ComprovanteApostaDTO comprovanteParaPdf;
    private boolean isHistorico;

    @Click
    protected void carrinhoApostasBtn() {
        DialogUtils.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                DialogUtils.dismiss();
                //CarrinhoActivity_.intent(ApostaConfirmadaDetalhesActivity.this).carrinho(response.getPayload()).start();
                Bundle args = new Bundle();
                args.putParcelable(CARRINHO, Parcels.wrap(response.getPayload()));
                startActivity(new Intent(ApostaConfirmadaDetalhesActivity.this, CarrinhoActivity.class).putExtras(args));
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ApostaConfirmadaDetalhesActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    startActivity(new Intent(ApostaConfirmadaDetalhesActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true));
                    //CarrinhoActivity_.intent(ApostaConfirmadaDetalhesActivity.this).lerCarrinhoLocal(true).start();
                } else {
                    RedirectNetwork.checkRedirect( error, ApostaConfirmadaDetalhesActivity.this );
                }
            }
        });
    }

    @Click
    protected void favoriteBtn() {
        layoutnomeApostaLayout.setVisibility(View.VISIBLE);
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
                    .extra("tipoAposta", aposta.getModalidade())
                    .extra("modalidade", (new Gson()).toJson(parametroSimulacao.getParametroJogo()))
                    .start();
        }
    }

    @Click
    protected void adicionarCarrinhoBtn() {
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

            apostaFavoritaDTO.setNome(nomeApostaEditTxt.getText().toString());
            apostaFavoritaDTO.setNumerosSelecionados(aposta.getNumerosSelecionados());
            apostaFavoritaDTO.setTimeDoCoracao(aposta.getTimeDoCoracao());
            apostaFavoritaDTO.setMesDeSorte(aposta.getMesDeSorte());

            dadosCorporativosBO.validarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse result) {
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

                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(error);
                }
            });
        } else {
            ViewUtils.alert(this, getString(R.string.msg_favor_inserir_nome_aposta));
        }
    }

    @Click
    protected void comprovanteLayout() {
        DialogUtils.show(this);

        if (premioPago && isHistorico) {
            apostaBO.getApostaHistoricoComprovante(aposta.getId(), new RequestListener<DetalhesHistoricoPremioDTOResponse>() {
                @Override
                public void onResponse(DetalhesHistoricoPremioDTOResponse result) {
                    DialogUtils.dismiss();
                    pdfUtils.createComprovanteHistoricoPremioPdf(result.getPayload(), ApostaConfirmadaDetalhesActivity.this);
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
                    pdfUtils.createComprovantePremioPdf(result.getPayload(), ApostaConfirmadaDetalhesActivity.this);
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

                    comprovanteParaPdf = result.getPayload();
                    pdfUtils.createComprovanteApostaPdf(comprovanteParaPdf, ApostaConfirmadaDetalhesActivity.this);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(error);
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseManager.registraEvento(this ,"ENTROU_GERENCIAR_APOSTAS_DETALHE");

        if (comprovanteApostaDTO != null) {
            premioPago = false;
            isHistorico = false;
            aposta = comprovanteApostaDTO.getAposta();
        } else {
            premioPago = Boolean.TRUE;
            if (detalhesPremioDTO != null) {
                aposta = detalhesPremioDTO.getAposta();
                isHistorico = false;
            } else {
                aposta = detalhesHistoricoPremioDTO.getAposta();
                isHistorico = true;
            }
        }

        if (Objects.equals(aposta.getSituacao().getValor(), SituacaoResultadoBilheteEnum.EFETIVADA.getValor())) {
            ConferirResultadoHelper.conferirResultadoHandler = this;
            ConferirResultadoHelper.conferirResultado(aposta.getId(), this);
        }

        estilo = new EstiloModalidade(aposta.getModalidade());
        setTheme(estilo.getThemeId());
        setTitle(ViewUtils.textFuturaAndSizeTitle(this, AppUtils.capitalizeFirstChar(getString(estilo.getStringId()))));
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

        chooseFragmentToPresent();
    }

    private void chooseFragmentToPresent() {
        Fragment fragment = null;

        switch (aposta.getModalidade()) {
            case LOTOGOL:
                disableButtonEditar();
                fragment = ApostaConfirmadaPartidaFragment_.builder().aposta(aposta).loteca(Boolean.FALSE).build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                break;
            case LOTECA:
                disableButtonEditar();
                fragment = ApostaConfirmadaPartidaFragment_.builder().aposta(aposta).loteca(Boolean.TRUE).build();

                linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorEscura()));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                break;
            default:
                if (premioPago) {
                    //ADICIONAR CHAMADA DE RESULTADO
                    if (detalhesHistoricoPremioDTO != null) {
                        getResultadoModalidadeConcurso(aposta);
                    } else if (detalhesPremioDTO != null) {
                        getResultadoModalidadeConcurso(aposta);
                    }
                } else {
                    fragment = ApostaConfirmadaNumeroDetalhes_.builder().aposta(aposta).build();

                    linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorEscura()));
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .commit();
                }
                break;
        }
    }

    private void getResultadoModalidadeConcurso
            (IdentificaoDeUmaApostaDas8Modalidades apostaDTO) {
        apostaBO.getResultadoModalidade(ApostaDTO.lowerCaseFromString(apostaDTO.getModalidade()), apostaDTO.getConcursoAlvo().toString(),
                new RequestListener<ResultadoConcursoDTOResponse>() {
                    @Override
                    public void onResponse(ResultadoConcursoDTOResponse result) {
                        try {
                            DialogUtils.dismiss();
                            Fragment fragment;

                            resultadoConcursoDTO = result.getPayload();

                            fragment = ApostaConfirmadaNumeroDetalhesPremioPago_.builder().aposta(aposta).resultadoConcursoDTO(resultadoConcursoDTO).build();

                            linearLayoutBottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), estilo.getCorEscura()));
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .commit();
                        }catch (Exception e){
                            ViewUtils.alert(getApplicationContext(), getResources().getString(R.string.erro_500));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onApostasConfirmadasError(error);
                    }
                });
    }

    private void onApostasConfirmadasError(VolleyError error) {
        if (this != null) {
            DialogUtils.dismiss();
            RedirectNetwork.checkRedirect(error, this);
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

    private static void getApostaDetalhePremio(Long apostaId, final Context contextParam, ComprovanteApostaDTO comprovante) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(contextParam);
        apostaBO.getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                DialogUtils.dismiss();
                ResultadoApostaConfirmadaActivity_.intent(contextParam).detalhesPremioDTO(result.getPayload()).comprovante(comprovante).start();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                onError(error, contextParam);
            }
        });
    }

    private static void onError(VolleyError error, Context contextParam) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, contextParam);
        ViewUtils.alert(contextParam, msgError);
    }

    private void onError(VolleyError error) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, this);
        ViewUtils.alert(this, msgError);
    }

    @Override
    public void handle(DTOEnumLong situacao) {
        aposta.setSituacao(situacao);

        chooseFragmentToPresent();

        if (Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA.getValor(), situacao.getValor())||
                Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA_AINDA_CONCORRENDO.getValor(), situacao.getValor())) {
            getApostaDetalhePremio(aposta.getId(), this, comprovanteApostaDTO);
        }
    }

    @Override
    public void handle(ApostasConfirmadasAdapter.ViewHolder holder, DTOEnumLong situacao) { }

    @Override
    public void handleError(ApostasConfirmadasAdapter.ViewHolder holder) {

    }
}