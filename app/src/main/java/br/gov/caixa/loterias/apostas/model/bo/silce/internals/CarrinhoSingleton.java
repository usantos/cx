package br.gov.caixa.loterias.apostas.model.bo.silce.internals;
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
        this.carrinho.atualizaValorTotal();
    }
}
