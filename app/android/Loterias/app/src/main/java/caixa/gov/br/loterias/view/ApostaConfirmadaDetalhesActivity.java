package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ComprovanteApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConcursoDTO;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.DialogUtils;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.PdfUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.helper.AdicionarApostaCarrinhoHelper;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaNumeroDetalhesPremioPago_;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaNumeroDetalhes_;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaPartidaFragment_;

/**
 * Created by pmotta on 22/03/2018.
 * Class ApostaConfirmadaDetalhesActivity
 */

@EActivity(R.layout.activity_aposta_confirmada_detalhes)
@OptionsMenu(R.menu.menu_apostas_confirmadas)
public class ApostaConfirmadaDetalhesActivity extends AppCompatActivity {

    @Bean
    protected DadosCorporativosBO dadosCorporativosBO;

    @Bean
    protected PdfUtils pdfUtils;

    @Extra
    protected ComprovanteApostaDTO comprovanteApostaDTO;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    @Extra
    protected ResultadoConcursoDTO resultadoConcursoDTO;

    @ViewById
    protected FrameLayout fragmentContainer;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected TextView validadePremioText, comprovanteText, editarTxt;

    @ViewById
    protected ImageView validadePremioImage, comprovanteImage, comprovanteImpressaoImage;

    @ViewById
    protected RelativeLayout layoutnomeApostaLayout, favoritadoLayout, favoriteBtn;

    @ViewById
    protected EditText nomeApostaEditTxt;

    private EstiloModalidade estilo;
    private IdentificaoDeUmaApostaDas8Modalidades aposta;
    private boolean premioPago;
    private ApostaFavoritaDTO apostaFavoritaDTO;
    private SessaoUsuario sessaoUsuario;
    private ParametroSimulacao parametroSimulacao;

    @Click
    protected void carrinhoApostasBtn() {
        CarrinhoActivity_.intent(this).start();
    }

    @Click
    protected void favoriteBtn() {
        layoutnomeApostaLayout.setVisibility(View.VISIBLE);
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

            dadosCorporativosBO.validarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse result) {
                    dadosCorporativosBO.salvarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse result) {
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
        pdfUtils.createComprovantePdf(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (comprovanteApostaDTO != null) {
            premioPago = Boolean.FALSE;
            aposta = comprovanteApostaDTO.getAposta();
        } else {
            premioPago = Boolean.TRUE;
            aposta = detalhesPremioDTO.getAposta();
        }

        estilo = new EstiloModalidade(aposta.getModalidade());
        setTheme(estilo.getThemeId());
        setTitle(AppUtils.capitalizeFirstChar(getString(estilo.getStringId())));
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

        sessaoUsuario = SessaoUsuario.getInstance();
        parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());
        if (estilo != null) {
            ViewUtils.setColorDrawable(this, validadePremioImage.getDrawable(), estilo.getIconValidadeComprovanteColor());
            ViewUtils.setColorDrawable(this, comprovanteImage.getDrawable(), estilo.getIconValidadeComprovanteColor());
            ViewUtils.setColorDrawable(this, comprovanteImpressaoImage.getDrawable(), estilo.getIconImpressoraColor());
        }
        if (premioPago) {
            comprovanteText.setText(detalhesPremioDTO.getNsbi());
        } else {
            comprovanteText.setText(comprovanteApostaDTO.getNsbi());
        }

        if (CollectionUtils.isNotEmpty(aposta.getNumerosSelecionados())) {
            favoriteBtn.setVisibility(View.VISIBLE);
        }

        Fragment fragment;

        switch (aposta.getModalidade()) {
            case LOTOGOL:
                disableButtonEditar();
                fragment = ApostaConfirmadaPartidaFragment_.builder().aposta(aposta).loteca(Boolean.FALSE).build();
                break;
            case LOTECA:
                disableButtonEditar();
                fragment = ApostaConfirmadaPartidaFragment_.builder().aposta(aposta).loteca(Boolean.TRUE).build();
                break;
            default:
                if (premioPago) {
                    fragment = ApostaConfirmadaNumeroDetalhesPremioPago_.builder().aposta(aposta).resultadoConcursoDTO(resultadoConcursoDTO).build();
                } else {
                    fragment = ApostaConfirmadaNumeroDetalhes_.builder().aposta(aposta).build();
                }
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
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

    private void onError(VolleyError error) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, this);
        ViewUtils.alert(this, msgError);
    }
}
