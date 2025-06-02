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

     private ServerMethodsNuvem(){}
}
