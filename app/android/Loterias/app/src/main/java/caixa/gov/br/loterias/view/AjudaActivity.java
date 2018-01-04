package caixa.gov.br.loterias.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import caixa.gov.br.loterias.R;

public class AjudaActivity extends AppCompatActivity {

    TextView textConcurso, textAbertura, textProximoSorteio, textEncerramento, titleTextScroll, textScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creatToolBar();
        init();
        carregarCampos();
    }

    private void init() {
        textConcurso = (TextView) findViewById(R.id.textConcurso);
        textAbertura = (TextView) findViewById(R.id.textAbertura);
        textProximoSorteio = (TextView) findViewById(R.id.textProximoSorteio);
        textEncerramento = (TextView) findViewById(R.id.textEncerramento);
        titleTextScroll = (TextView) findViewById(R.id.titleTextScroll);
        textScroll = (TextView) findViewById(R.id.textScroll);
    }

    private void creatToolBar() {
        setContentView(R.layout.custom_toolbar_ajuda);
        Toolbar tb = (Toolbar) findViewById(R.id.toolBarAjuda);
        setSupportActionBar(tb);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.lotecaEscuroTollbar)));
        getSupportActionBar().setTitle(null);
        View view = findViewById(R.id.action_fechar);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
