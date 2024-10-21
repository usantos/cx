package br.gov.caixa.loterias.apostas.model.bo.silce.internals;
import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;

public class CarrinhoSingleton {

    private CarrinhoDTO carrinho;
    private static CarrinhoSingleton carrinhoInstance;

    private CarrinhoSingleton(){ }

    public static CarrinhoSingleton getInstance(){
        if (carrinhoInstance == null){
            carrinhoInstance = new CarrinhoSingleton();
        }
        return carrinhoInstance;
    }

    public  CarrinhoDTO getCarrinho(){
        return carrinho;
    }

    public void setCarrinho(CarrinhoDTO carrinho){
        this.carrinho = carrinho;
        if (carrinho != null){
            this.carrinho.atualizaValorTotal();
        }
    }

    public void zerarCarrinho(){
        this.carrinho = new CarrinhoDTO();
        this.carrinho.setValorTotal(new BigDecimal(0));
    }
}
