package caixa.gov.br.loterias.model.bo;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import caixa.gov.br.loterias.model.bo.silce.internals.SilceConnection;

 class ServerMethods {
     static final String COMPLETA_JOGO_PATH = "/apostas/completar-jogo";
     static final String VALIDAR_FAVORITA_PATH = "/apostas-favoritas/validar";
     static final String INCLUIR_FAVORITA_PATH = "/apostas-favoritas";

     static final String INCLUIR_APOSTA_CARRINHO_PATH = "/carrinhos/incluir-aposta";
     static final String INCLUIR_SURPRESINHA_CARRINHO_PATH = "/carrinhos/incluir-surpresinha";
     static final String BUSCA_CARRINHO_PATH = "/carrinhos";
     static final String DELETE_APOSTA_CARRINHO_PATH = "carrinhos/apostas/";
     static final String LIMPAR_CARRINHO_PATH = "/carrinhos/limpar";

     static final String INCLUIR_APOSTAS_CARRINHO_PATH = "/carrinhos/incluir-apostas";

     static final String UFS_PATH = "/ufs";
     static final String TERMO_PATH = "/termos-de-uso/termo-vigente";
     static final String TERMO_VALIDA_ACEITO_PATH = "/termos-de-uso/valida-termo-aceito";
     static final String TERMO_ACEITAR_PATH = "/termos-de-uso/aceitar-termo-vigente";
     static final String MUNICIPIOS_PATH = "/municipios";
     static final String REPASSOMETRO_PATH = "/repassometro";
     static final String PARAM_SIMULACAO_PATH = "/parametros-simulacao";
     static final String BAIRROS_PATH = "/bairros";

     static final String LOGIN_PATH = "/usuarios/autenticar";
     static final String LIST_CARTOES_PATH = "/usuarios/cartoes";
     static final String SAIR_PATH = "/usuarios/sair";
     static final String GRAVAR_USUARIO_PATH = "/usuarios";

     static final String LOTERICAS_PATH = "/lotericas";
     static final String LOTERICAS_HABILITADAS_PATH = "/lotericas/buscas-habilitadas";
     static final String LOTERICAS_CODIGO_COMPLETO_PATH = "/lotericas/codigo-completo";
     static final String GERAR_APOSTAS_RAPIDAO_PATH = "/rapidoes/gerar-apostas";
     static final String RAPIDOES_PATH = "/rapidoes";
     static final String MODALIDADES_PATH = "/modalidades";

     static final String APOSTAS_FAVORITAS_PATH = "/apostas-favoritas/";
     static final String APOSTAS_FAVORITAS_LISTA_UNICA_PATH = "/apostas-favoritas/lista-unica";

     static final String APOSTAS_MES_ANO_PATH = "/apostas/meses";
     static final String APOSTAS_CONFIRMADAS_PATH = "/apostas";
     static final String APOSTAS_CONFIRMADAS_DETALHES_PATH = "/apostas/{id}/detalhes-premio";
     static final String APOSTAS_CONFIRMADAS_COMPROVANTE_PATH = "/apostas/{id}/comprovante";
     static final String APOSTAS_CONFERIR_PATH = "/apostas/{id}/conferir";
     static final String APOSTAS_GERAR_CODIGO_RESGATE_PATH = "/apostas/{id}/app/gerar-codigo-resgate-loterica";
     static final String APOSTAS_HISTORICO_MES_ANO_PATH = "/historico-apostas/meses";
     static final String APOSTAS_CONFIRMADAS_HISTORICO_PATH = "/historico-apostas";

     static final String EQUIPES_ESPORTIVAS_ESCUDO_PATH = "/equipes-esportivas/{id}/escudo";
     static final String BILHETES_CONFERIR_BILHETE_PATH = "/bilhetes/conferir-bilhete";

     static final String COMPRAS_PATH = "/compras";
     static final String COMPRAS_MESES_PATH = "/compras/meses";
     static final String DETALHES_COMPRAS_PATH = "/compras/{id}/apostas";

     static final String DUVIDAS_SECOES_PATH = "/duvidas/secoes";
     static final String DUVIDAS_PATH = "/duvidas";

     static final String EQUIPES_ESPORTIVAS_POR_MODALIDADE_EQUIPE_PATH = "/equipes-esportivas/{idEquipe}/{idModalidade}/escudo";


     static final String RESULTADOS_MODALIDADE_CONCURSO_PATH = "/resultados/{modalidade}/{concurso}";

     static final String CARRINHOS_REGISTRAR_APOSTAS_PATH = "/carrinhos/registrar-apostas";

     static final String USUARIOS_RECUPERAR_DADOS_PATH = "/usuarios/recuperar-dados";


     static final String USUARIOS_PATH = "/usuarios";

     private ServerMethods(){}
}
