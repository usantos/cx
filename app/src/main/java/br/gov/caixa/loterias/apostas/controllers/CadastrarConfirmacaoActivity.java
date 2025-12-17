package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class CadastrarConfirmacaoActivity extends LoteriasBaseAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cadastrar_confirmacao );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        setTitle( ViewUtils.textFuturaAndFuturaBoldTitle( this, getTitle().toString() ) );

        TextView tituloConfirmacaoCadastroTextView = findViewById( R.id.tituloConfirmacaoCadastroTextView );
        TextView subtituloConfirmaCadastroTextView = findViewById( R.id.subtituloConfirmaCadastroTextView );
        tituloConfirmacaoCadastroTextView.setText( ViewUtils.textFuturaAndFuturaBold( this, getResources().getString(R.string.sucesso_cadastro) ) );
        subtituloConfirmaCadastroTextView.setText( getResources().getString(R.string.aguarde_redirecionamento) );

        if (temCarrinhoLocal()){
            verificaCadastro();
        } else {
            nextActivity();
        }
    }

    private void verificaCadastro() {
        DBLoteriasCrud crud = new DBLoteriasCrud(this);
        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
        ServicoFactoryUtil.getDadosUsuarioService().postVerificarCadastro(listApostas, new RequestListener<UsuarioLogadoResponse>() {

            @Override
            public void onResponse(UsuarioLogadoResponse response) {
                if (response.getRedirect() == null) {
                    SessaoUsuario.getInstance().setValorMaximoAposta(response.getPayload().getLimiteDiario());
                    if(response.getPayload().getLimiteDiario() != null){
                        SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(response.getPayload().getLimiteDiario());
                    }
                    SessaoUsuario.getInstance().setResponderAutoavaliacao(response.getPayload().getResponderAutoavaliacao());
                    SessaoUsuario.getInstance().setSuspensaoTemporariaApostador(response.getPayload().getSuspensaoTemporariaApostador());
                    SessaoUsuario.getInstance().setSituacaoApostador(response.getPayload().getSituacaoApostador());

                    crud.deleteAll();
                    CarrinhoSingleton.getInstance().setCarrinho(response.getPayload().getCarrinho());
                    UltimaNotificacaoSingleton.getInstance().setUltimaNoficacao(response.getPayload().getUltimaNotificacao());
                    UltimaNotificacaoSingleton.getInstance().setPagamentoNaoIdentificado(response.getPayload().getPagamentoNaoIdentificado());

                    nextActivity();
                } else {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), CadastrarConfirmacaoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivity();
            }
        });
    }

    private void nextActivity() {
        Utils.delay(3, () -> {
            Intent intent = IntentUtil.getIntentOrigemDestino(CadastrarConfirmacaoActivity.this, PrincipalActivity.class);
            startActivity(intent);

            CadastrarConfirmacaoActivity.this.finish();
        });
    }

    private boolean temCarrinhoLocal() {
        DBLoteriasCrud crud = new DBLoteriasCrud(this);
        List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
        return !listApostas.isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dados_pessoais, menu);

        return true;
    }
}
