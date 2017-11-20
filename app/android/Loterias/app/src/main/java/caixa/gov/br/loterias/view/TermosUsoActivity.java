package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import caixa.gov.br.loterias.R;

public class TermosUsoActivity extends AppCompatActivity {

    private Button termosUsoButton;
    private Button politicaButton;
    private Button duvidasButton;
    private TextView textoConteudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_uso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        termosUsoButton = findViewById(R.id.buttonTermos);
        duvidasButton = findViewById(R.id.buttonDuvidas);
        politicaButton = findViewById(R.id.buttonPolitica);
        textoConteudo = findViewById(R.id.textViewConteudoSobre);
        listenerTermosUso();
        listenerDuvidas();
        listenerPolitica();

    }

    private void listenerTermosUso(){
        termosUsoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                termosUsoButton.setBackgroundResource(R.color.verdeazul);
                termosUsoButton.setTextColor(getResources().getColor(R.color.branco));
                duvidasButton.setBackgroundResource(R.color.cinzaclaro);
                duvidasButton.setTextColor(getResources().getColor(R.color.verdeazul));
                politicaButton.setBackgroundResource(R.color.cinzaclaro);
                politicaButton.setTextColor(getResources().getColor(R.color.verdeazul));

                textoConteudo.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.\n" +
                        "\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                        "\n" +
                        "Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet.\n" +
                        "\n" +
                        "Proin gravida dolor sit amet lacus accumsan et viverra justo commodo.\n" +
                        "\n" +
                        "Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.\n" +
                        "\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.\n" +
                        "\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet.\n" +
                        "\n" +
                        "Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.");
            }
        });
    }

    private void listenerPolitica(){
        politicaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                termosUsoButton.setBackgroundResource(R.color.cinzaclaro);
                termosUsoButton.setTextColor(getResources().getColor(R.color.verdeazul));
                duvidasButton.setBackgroundResource(R.color.cinzaclaro);
                duvidasButton.setTextColor(getResources().getColor(R.color.verdeazul));
                politicaButton.setBackgroundResource(R.color.verdeazul);
                politicaButton.setTextColor(getResources().getColor(R.color.branco));

                textoConteudo.setText("Política");

            }
        });
    }

    private void listenerDuvidas(){
        duvidasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                termosUsoButton.setBackgroundResource(R.color.cinzaclaro);
                termosUsoButton.setTextColor(getResources().getColor(R.color.verdeazul));
                duvidasButton.setBackgroundResource(R.color.verdeazul);
                duvidasButton.setTextColor(getResources().getColor(R.color.branco));
                politicaButton.setBackgroundResource(R.color.cinzaclaro);
                politicaButton.setTextColor(getResources().getColor(R.color.verdeazul));

                textoConteudo.setText("Dúvidas");

            }
        });
    }
}
