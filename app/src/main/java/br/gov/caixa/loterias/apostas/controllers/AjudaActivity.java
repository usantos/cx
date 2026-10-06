package br.gov.caixa.loterias.apostas.controllers;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class AjudaActivity extends LoteriasBaseAppActivity {

    private ModalidadeEnum tipoJogo;
    private TextView textConcurso, textAbertura, textProximoSorteio, textEncerramento, titleTextScroll, textScroll;
    private static final String TIPO_APOSTA = "tipoAposta";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tipoJogo();
        init();
        carregarCampos();
    }

    private void init() {
        textConcurso = findViewById(R.id.textConcurso);
        textAbertura = findViewById(R.id.textAbertura);
        textProximoSorteio = findViewById(R.id.textProximoSorteio);
        textEncerramento = findViewById(R.id.textEncerramento);
        titleTextScroll = findViewById(R.id.titleTextScroll);
        textScroll = findViewById(R.id.textScroll);
    }

    private void tipoJogo() {
        tipoJogo = (ModalidadeEnum) getIntent().getSerializableExtra(TIPO_APOSTA);

        int titulo;
        int corEscura;
        int backGroundToolBar;
        int toolBarTrapezio;
        int iconFechar;

        switch (tipoJogo) {
            case MEGA_SENA:
                titulo = R.string.label_mega_sena;
                corEscura = R.color.mega_verde_escuro_mkp;
                backGroundToolBar = R.color.mega_verde_claro_mkp;
                toolBarTrapezio = R.drawable.background_trapeze_mega_sena_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case LOTOFACIL:
                titulo = R.string.label_lotofacil;
                corEscura = R.color.lotomania_escuro_mkp;
                backGroundToolBar = R.color.lotofacil_claro_mkp;
                toolBarTrapezio = R.drawable.background_trapeze_lotofacil_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case QUINA:
                titulo = R.string.label_quina;
                corEscura = R.color.quina_escuro_mkp;
                backGroundToolBar = R.color.quina_claro_mkp;
                toolBarTrapezio = R.drawable.background_trapeze_quina_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case DUPLA_SENA:
                titulo = R.string.label_dupla_sena;
                corEscura = R.color.dupla_sena_escuro_mkp;
                backGroundToolBar = R.color.dupla_sena_claro_mkp;
                toolBarTrapezio = R.drawable.background_trapeze_dupla_sena_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case LOTOMANIA:
                titulo = R.string.label_lotomania;
                corEscura = R.color.lotomania_escuro_mkp;
                backGroundToolBar = R.color.lotomaniaEscuroToolBar;
                toolBarTrapezio = R.drawable.background_trapeze_lotomania_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case TIMEMANIA:
                titulo = R.string.label_timemania;
                corEscura = R.color.timemania_escuro_mkp;
                backGroundToolBar = R.color.timemaniaClaroToolBar;
                toolBarTrapezio = R.drawable.background_trapeze_timemania_tollbar;
                iconFechar = R.drawable.fechar_verde;
                break;
            case LOTECA:
                titulo = R.string.label_loteca;
                corEscura = R.color.loteca_escuro_mkp;
                backGroundToolBar = R.color.lotecaEscuroToolbar;
                toolBarTrapezio = R.drawable.background_trapeze_loteca_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case LOTOGOL:
                titulo = R.string.label_lotogol;
                corEscura = R.color.lotogolescuro;
                backGroundToolBar = R.color.lotogolEscuroToolBar;
                toolBarTrapezio = R.drawable.background_trapeze_lotogol_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            default:
                titulo = R.string.label_ajuda;
                corEscura = R.color.mega_verde_escuro_mkp;
                backGroundToolBar = R.color.mega_verde_claro_mkp;
                toolBarTrapezio = R.drawable.background_trapeze_mega_sena_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
        }

        creatToolBar(titulo, corEscura, backGroundToolBar, toolBarTrapezio, iconFechar);
    }

    private void creatToolBar(int titulo, int corEscura, int backGroundToolBar, int toolBarTrapezio, int iconFechar) {

        setContentView(R.layout.custom_toolbar_ajuda);
        Toolbar tb = findViewById(R.id.toolBarAjuda);

        ((AppBarLayout) tb.getParent()).setBackgroundColor(getResources().getColor(corEscura));
        tb.findViewById(R.id.layoutTrevo).setBackgroundResource(toolBarTrapezio);

        ((TextView) tb.findViewById(R.id.titulo_modalidade)).setText(titulo);

        ImageView fecharImageView = tb.findViewById(R.id.action_fechar);
        fecharImageView.setImageResource(iconFechar);
        fecharImageView.setOnClickListener(v -> onBackPressed());

        setSupportActionBar(tb);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, backGroundToolBar)));
        getSupportActionBar().setTitle("");
    }

    private void carregarCampos() {
        textConcurso.setText(getResources().getString(R.string.placeholder_numero_conc));
        textAbertura.setText(getResources().getString(R.string.placeholder_data_hora_concurso));
        textProximoSorteio.setText(getResources().getString(R.string.placeholder_proximo_sorteio));
        textEncerramento.setText(getResources().getString(R.string.placeholder_data_encerramento));
        titleTextScroll.setText(getResources().getString(R.string.label_regras_jogo));
        textScroll.setText(getResources().getString(R.string.placeholder_lorem_ipsum));
    }
}
