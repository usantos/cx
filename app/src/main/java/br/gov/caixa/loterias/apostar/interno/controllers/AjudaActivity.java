package br.gov.caixa.loterias.apostar.interno.controllers;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;

public class AjudaActivity extends AppCompatActivity {

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
                corEscura = R.color.megasenaescuro;
                backGroundToolBar = R.color.megasenaclaro;
                toolBarTrapezio = R.drawable.background_trapeze_mega_sena_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case LOTOFACIL:
                titulo = R.string.label_lotofacil;
                corEscura = R.color.lotofacilescuro;
                backGroundToolBar = R.color.lotofacilclaro;
                toolBarTrapezio = R.drawable.background_trapeze_lotofacil_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case QUINA:
                titulo = R.string.label_quina;
                corEscura = R.color.quinaescuro;
                backGroundToolBar = R.color.quinaclaro;
                toolBarTrapezio = R.drawable.background_trapeze_quina_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case DUPLA_SENA:
                titulo = R.string.label_dupla_sena;
                corEscura = R.color.duplasenaescuro;
                backGroundToolBar = R.color.duplasenaclaro;
                toolBarTrapezio = R.drawable.background_trapeze_dupla_sena_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case LOTOMANIA:
                titulo = R.string.label_lotomania;
                corEscura = R.color.lotomaniaescuro;
                backGroundToolBar = R.color.lotomaniaEscuroToolBar;
                toolBarTrapezio = R.drawable.background_trapeze_lotomania_tollbar;
                iconFechar = R.drawable.btn_fechar;
                break;
            case TIMEMANIA:
                titulo = R.string.label_timemania;
                corEscura = R.color.timemaniaescuro;
                backGroundToolBar = R.color.timemaniaClaroToolBar;
                toolBarTrapezio = R.drawable.background_trapeze_timemania_tollbar;
                iconFechar = R.drawable.fechar_verde;
                break;
            case LOTECA:
                titulo = R.string.label_loteca;
                corEscura = R.color.lotecaescuro;
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
                corEscura = R.color.megasenaescuro;
                backGroundToolBar = R.color.megasenaclaro;
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
        textConcurso.setText("Nº 1456");
        textAbertura.setText("10/04 | 18h00");
        textProximoSorteio.setText("09/12");
        textEncerramento.setText("11/04 | 15h50");
        titleTextScroll.setText(getString(R.string.label_regras_jogo));
        textScroll.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit.\n\nAenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet.\n\nProin gravida dolor sit amet lacus accumsan et viverra justo commodo.\n\nProin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet.\n\nProin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.");
    }
}
