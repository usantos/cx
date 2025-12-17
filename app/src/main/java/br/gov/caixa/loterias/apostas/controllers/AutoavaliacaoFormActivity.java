package br.gov.caixa.loterias.apostas.controllers;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoPerguntaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoRespostaDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.BottomSheetFragment;

public class AutoavaliacaoFormActivity extends LoteriasBaseAppActivity {
    private List<AutoavaliacaoPerguntaDTO> listPergunta;
    private List<AutoavaliacaoRespostaDTO> listResposta;
    private int indiceTelaAtual = 0;
    private TextView tvAutoPergunta;
    private RadioGroup rgAutoavaliacao;
    private Button btnProximo;
    private Button btnAnterior;
    private Button btnFechar;
    private Button btnFecharTop;
    private Button btnAjuda;
    private BottomSheetFragment bottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoavaliacao_form);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.autoavaliacao_apostador))));

        configurar();
        buscaPerguntas();
    }

    private void configurar() {
        tvAutoPergunta = findViewById(R.id.tvAutoPergunta);
        rgAutoavaliacao = findViewById(R.id.rgAutoavaliacao);
        btnFecharTop = findViewById(R.id.btnAutoFecharTop);

        if (!SessaoUsuario.getInstance().getResponderAutoavaliacao()) {
            btnFecharTop.setVisibility(View.VISIBLE);
            btnFecharTop.setOnClickListener(v -> finish());
        }

        btnProximo = findViewById(R.id.btnAutoProximo);
        btnProximo.setOnClickListener(v -> clickProximo());

        btnAnterior = findViewById(R.id.btnAutoAnterior);
        btnAnterior.setOnClickListener(v -> clickAnterior());

        btnFechar = findViewById(R.id.btnAutoFechar);
        btnFechar.setOnClickListener(v -> onFecharListener());

        btnAjuda = findViewById(R.id.btnAutoAjuda);
        btnAjuda.setOnClickListener(v -> {
            bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        desabilitaAnterior();
        desabilitaProximo();

        rgAutoavaliacao.setOnCheckedChangeListener((group, checkedId) -> habilitaProximo());
    }

    private void onFecharListener() {
        finish();
    }

    private void clickProximo() {
        //Clicou Concluido
        if (listPergunta != null && indiceTelaAtual == (listPergunta.size() -1)) {
            salvaNoListResposta();
            salvarRespostas();
            return;
        }

        if (listPergunta != null && indiceTelaAtual < (listPergunta.size() -1)) {
            salvaNoListResposta();

            rgAutoavaliacao.clearCheck();
            desabilitaProximo();
            habilitaAnterior();

            indiceTelaAtual++;
            tvAutoPergunta.setText(listPergunta.get(indiceTelaAtual).getDescricaoPergunta());

            //Ultima tela
            if (indiceTelaAtual == (listPergunta.size() -1)) {
                alteraBtnToConcluido();
            }
        }
    }


    private void clickAnterior() {
        if (indiceTelaAtual > 0 && listPergunta != null) {
            //Ultima tela
            if (indiceTelaAtual == (listPergunta.size() -1)) {
                alteraBtnToProximo();
            }

            rgAutoavaliacao.clearCheck();
            indiceTelaAtual--;
            tvAutoPergunta.setText(listPergunta.get(indiceTelaAtual).getDescricaoPergunta());

            //Primeira tela
            if (indiceTelaAtual == 0) {
                desabilitaAnterior();
                desabilitaProximo();
            }
        }
    }


    private void salvaNoListResposta() {
        boolean indicadorResposta = false;
        int selectedId = rgAutoavaliacao.getCheckedRadioButtonId();
        if (selectedId == R.id.rbYes) {
            indicadorResposta = true;
        }

        listResposta.set(indiceTelaAtual, new AutoavaliacaoRespostaDTO(
                        listPergunta.get(indiceTelaAtual).getId(),
                        "",
                        indicadorResposta));
    }

    private void desabilitaAnterior() {
        btnAnterior.setEnabled(false);
        btnAnterior.setTextColor(getResources().getColor(R.color.verdeazul_inativo));
        btnAnterior.setBackground(getResources().getDrawable(R.drawable.background_border_verdeazul_disable));
        Drawable icone = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_left);
        icone.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.verdeazul_inativo), PorterDuff.Mode.SRC_IN));
        btnAnterior.setCompoundDrawablesWithIntrinsicBounds(icone,null, null,null);
    }

    private void habilitaAnterior() {
        btnAnterior.setEnabled(true);
        btnAnterior.setTextColor(getResources().getColor(R.color.verdeazul));
        btnAnterior.setBackground(getResources().getDrawable(R.drawable.background_border_verde));
        Drawable icone = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_left);
        icone.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.verdeazul), PorterDuff.Mode.SRC_IN));
        btnAnterior.setCompoundDrawablesWithIntrinsicBounds(icone,null, null,null);
    }


    private void desabilitaProximo() {
        btnProximo.setEnabled(false);
        btnProximo.setTextColor(getResources().getColor(R.color.verdeazul_inativo));
        btnProximo.setBackground(getResources().getDrawable(R.drawable.background_border_verdeazul_disable));
        Drawable icone = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_right);
        icone.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.verdeazul_inativo), PorterDuff.Mode.SRC_IN));
        btnProximo.setCompoundDrawablesWithIntrinsicBounds(null,null, icone,null);
    }

    private void habilitaProximo() {
        btnProximo.setEnabled(true);
        btnProximo.setTextColor(getResources().getColor(R.color.branco));
        btnProximo.setBackground(getResources().getDrawable(R.drawable.button_rounded_verdeazul));
        Drawable icone = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_right);
        icone.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        btnProximo.setCompoundDrawablesWithIntrinsicBounds(null,null, icone,null);
    }

    private void alteraBtnToConcluido() {
        btnProximo.setText(R.string.concluido);
        btnProximo.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_sucesso),null);
    }

    private void alteraBtnToProximo() {
        btnProximo.setText(R.string.proximo);
        Drawable icone = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chevron_right);
        icone.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        btnProximo.setCompoundDrawablesWithIntrinsicBounds(null,null, icone,null);
    }

    private void alteraBtnToFechar() {
        btnAnterior.setVisibility(View.GONE);
        btnFechar.setVisibility(View.VISIBLE);
        btnFecharTop.setVisibility(View.GONE);
    }

    private void alteraBtnToAjuda() {
        btnProximo.setVisibility(View.GONE);
        btnAjuda.setVisibility(View.VISIBLE);
    }

    private Boolean verificaRespostasTudoNao() {
        for (AutoavaliacaoRespostaDTO autoResposta :listResposta) {
            if (autoResposta.getIndicadorResposta()) {
                return false;
            }
        }
        return true;
    }

    private void buscaPerguntas() {
        AlertDialogUtils.show(this);
        ApostaSilceBO.getInstance().getAutoavaliacaoPerguntas(new RequestListener<AutoavaliacaoResponse>() {
            @Override
            public void onResponse(AutoavaliacaoResponse result) {
                AlertDialogUtils.dismiss();
                listPergunta = result.getPayload().getRetornoPerguntaResposta();
                listResposta = new ArrayList<>(Collections.nCopies(listPergunta.size(), (AutoavaliacaoRespostaDTO) null));
                tvAutoPergunta.setText(listPergunta.get(indiceTelaAtual).getDescricaoPergunta());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, AutoavaliacaoFormActivity.this);
            }
        });
    }

    private void salvarRespostas() {
        AlertDialogUtils.show(this);
        ApostaSilceBO.getInstance().postAutoavaliacaoRespostas(listResposta, new RequestListener<AutoavaliacaoResponse>() {
            @Override
            public void onResponse(AutoavaliacaoResponse result) {
                AlertDialogUtils.dismiss();
                SessaoUsuario.getInstance().setResponderAutoavaliacao(false);
                rgAutoavaliacao.setVisibility(View.GONE);
                tvAutoPergunta.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                if (verificaRespostasTudoNao()) {
                    btnProximo.setVisibility(View.GONE);
                    tvAutoPergunta.setText(ViewUtils.textCaixaSTDBold(AutoavaliacaoFormActivity.this, getString(R.string.autoavaliacao_parabens)));
                } else {
                    tvAutoPergunta.setText(ViewUtils.textCaixaSTDBold(AutoavaliacaoFormActivity.this, getString(R.string.autoavaliacao_atencao)));
                    alteraBtnToAjuda();
                }
                alteraBtnToFechar();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, AutoavaliacaoFormActivity.this);
            }
        });
    }
}