package br.gov.caixa.loterias.apostas.model.bo;

class ServerMethodsNuvem {
     static final String PARAM_SIMULACAO_NUVEM_PATH = "/publico/parametros-simulacao";
     static final String EQUIPES_ESPORTIVAS_LISTA_EQUIPES_NUVEM_PATH = "/publico/equipes-esportivas/listaEquipes";
     static final String MODALIDADES_NUVEM_PATH = "/publico/modalidades";

     static final String USUARIOS_VERIFICAR_CADASTRO_NUVEM_PATH = "/logado/usuarios/verificar-cadastro";

     static final String APOSTAS_FAVORITAS_LISTA_UNICA_NUVEM_PATH = "/logado/apostas-favoritas/lista-unica";
     static final String VALIDAR_FAVORITA_NUVEM_PATH = "/logado/apostas-favoritas/validar";
     static final String APOSTAS_FAVORITAS_NUVEM_PATH = "/logado/apostas-favoritas/";
     static final String INCLUIR_FAVORITA_NUVEM_PATH = "/logado/apostas-favoritas";

     static final String CARRINHO_FAVORITO_BUSCAR_CARRINHOS_NUVEM = "/logado/carrinhos-favoritos";
     static final String CARRINHO_FAVORITO_VALIDAR_NOME_NUVEM = "/logado/carrinhos-favoritos/validar";
     static final String CARRINHO_FAVORITO_DELETAR_CARRINHO_NUVEM = "/logado/carrinhos-favoritos/{id}";
     static final String CARRINHO_FAVORITO_BUSCA_APOSTAS_NUVEM = "/logado/carrinhos-favoritos/{idCarrinho}/apostas";
     static final String CARRINHO_FAVORITO_DELETAR_APOSTA_NUVEM = "/logado/carrinhos-favoritos/{id}/apostas/{idAposta}";

     //static final String CARRINHO_FAVORITO_TRANSFORMAR = "/carrinhos/incluir-carrinho-favorito/{idCarrinhoFavorito}";
     //     static final String CARRINHO_FAVORITO_SALVAR_NUVEM = "/logado/carrinhos/salvar-carrinho-favorito";
     private ServerMethodsNuvem(){}
}
