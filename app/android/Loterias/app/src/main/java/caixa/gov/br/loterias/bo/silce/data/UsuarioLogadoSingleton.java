package caixa.gov.br.loterias.bo.silce.data;

public final class UsuarioLogadoSingleton {
    public String nome;
    public String email;
    public String token;
    public int tempoExpirarSegundos;
    public CarrinhoDTO carrinho;

    private static UsuarioLogadoSingleton userInstance;

    private UsuarioLogadoSingleton(){}

    public static UsuarioLogadoSingleton getInstance(){
        if (userInstance == null){
            userInstance = new UsuarioLogadoSingleton();
        }

        return userInstance;
    }
}
