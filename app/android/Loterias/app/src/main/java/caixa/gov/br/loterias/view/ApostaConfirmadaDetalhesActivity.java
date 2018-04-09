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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ComprovanteApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConcursoDTO;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaNumeroDetalhesPremioPago_;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaPartidaFragment_;
import caixa.gov.br.loterias.view.fragment.ApostaConfirmadaNumeroDetalhes_;

/**
 * Created by pmotta on 22/03/2018.
 * Calss ApostaConfirmadaDetalhesActivity
 */

@EActivity(R.layout.activity_aposta_confirmada_detalhes)
@OptionsMenu(R.menu.menu_apostas_confirmadas)
public class ApostaConfirmadaDetalhesActivity extends AppCompatActivity {

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
    protected TextView validadePremioText, comprovanteText;

    @ViewById
    protected ImageView validadePremioImage, comprovanteImage, comprovanteImpressaoImage;

    private EstiloModalidade estilo;

    private IdentificaoDeUmaApostaDas8Modalidades aposta;

    private boolean premioPago;

    @Click
    protected void carrinhoApostasBtn() {
        //TODO:
    }

    @Click
    protected void favoriteBtn() {
        //TODO:
    }

    @Click
    protected void editarApostaBtn() {
        //TODO:
    }

    @Click
    protected void adicionarCarrinhoBtn() {
        //TODO:
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

        Fragment fragment;

        switch (aposta.getModalidade()) {
            case LOTOGOL:
                fragment = ApostaConfirmadaPartidaFragment_.builder().aposta(aposta).loteca(Boolean.FALSE).build();
                break;
            case LOTECA:
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

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }
    }


}
