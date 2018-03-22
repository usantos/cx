package caixa.gov.br.loterias.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.CarrinhoApostasListViewAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_carrinho)
public class CarrinhoActivity extends SettingToolbarActivity {

    private CarrinhoDTO carrinho;
    private AlertDialog alertDialog;

    @ViewById
    protected ExpandableHeightRecyclerView apostasListaRecycleView;

    @ViewById
    protected TextView valorTotalCarrinho, infoValorMinimo;

    @ViewById
    protected Button botaoAvancarPagamento, botaoFazerNovaAposta;

    @ViewById
    protected LinearLayout limparCarrinhoLinearLayout;

    @AfterViews
    public void init() {
        ViewUtils.setMoedaFormatHtml(BigDecimal.ZERO, valorTotalCarrinho);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_carrinho)));
        callWebservice();
    }

    public void lerCarrinhoLocal(Boolean animacao) {
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        carrinho = new CarrinhoDTO();
        carrinho.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        atualizaLayout(animacao);
    }

    public void atualizaLayout(boolean animacao) {
        montarLayout();
        if (carrinho.getApostas() != null) {
            configuraRecyclerViewCarrinhoAposta(animacao);
            setValorTotalCarrinho();
        }
        botaoAvancarPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));

    }

    public void setCarrinho(CarrinhoDTO carrinho) {
        this.carrinho = carrinho;
    }

    @Click(R.id.botaoAvancarPagamento)
    protected void clickBotaoAvancarPagamento() {
        Intent activity = new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class);
        startActivity(activity);
    }

    @Click(R.id.botaoFazerNovaAposta)
    protected void clickBotaoFazerNovaAposta() {
        setResult(2);
        finish();
    }

    @Click(R.id.limparCarrinhoLinearLayout)
    protected void clickLimparCarrinhoLinearLayout() {
        ViewUtils.alertTitleButtonPositiveListener(this, R.string.label_empty, getString(R.string.MA011), R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog = LoadingViewLoterias.show(CarrinhoActivity.this);
                ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);
                apostaBO.limparCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse result) {
                        alertDialog.dismiss();
                        limparCarrinhoAtualizarTela();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertDialog.dismiss();
                        if (MensagensNetwork.isUnauthorizedError(error)) {
                            deleteCarrinhoLocal();
                        } else {
                            String msgError = MensagensNetwork.setMensagem(error, CarrinhoActivity.this);
                            ViewUtils.alert(CarrinhoActivity.this, msgError);
                        }
                    }
                });
            }
        });

    }

    private void deleteCarrinhoLocal() {
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        crud.deleteAll();
        limparCarrinhoAtualizarTela();
    }

    private void limparCarrinhoAtualizarTela() {
        carrinho.getApostas().removeAll(carrinho.getApostas());
        atualizaLayout(Boolean.FALSE);
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimo, getResources().getString(R.string.label_lembrar_valor_minimo).replace("_", "<br>") + " ");
    }

    private void callWebservice() {

        alertDialog = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                alertDialog.dismiss();
                carrinho = response.getPayload();
                atualizaLayout(Boolean.TRUE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    lerCarrinhoLocal(Boolean.TRUE);
                } else {
                    ViewUtils.alert(CarrinhoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
                }
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(boolean animacao) {
        List<IdentificaoDeUmaApostaDas8Modalidades> listIdentificaoDeUmaApostaDas8Modalidades = new ArrayList<>();
        for (IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades : carrinho.getApostas()) {
            if (identificaoDeUmaApostaDas8Modalidades.getModalidade() != null) {
                listIdentificaoDeUmaApostaDas8Modalidades.add(identificaoDeUmaApostaDas8Modalidades);
            }
        }
        if (carrinho.getApostas().size() > 0) {
            limparCarrinhoLinearLayout.setVisibility(View.VISIBLE);
        } else {
            limparCarrinhoLinearLayout.setVisibility(View.GONE);
        }
        apostasListaRecycleView.setLayoutManager(new LinearLayoutManager(this));
        apostasListaRecycleView.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listIdentificaoDeUmaApostaDas8Modalidades, this, animacao));
        apostasListaRecycleView.setExpanded(Boolean.TRUE);

    }

    private void setValorTotalCarrinho() {
        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        for (IdentificaoDeUmaApostaDas8Modalidades aposta : carrinho.getApostas()) {
            valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());
        }
        ViewUtils.setMoedaFormatHtml(valorTotalCarrinho, this.valorTotalCarrinho);
    }
}
