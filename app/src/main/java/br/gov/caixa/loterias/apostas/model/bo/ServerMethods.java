package br.gov.caixa.loterias.apostas.model.bo;

class ServerMethods {

     //CARRINHO
     static final String INCLUIR_APOSTA_CARRINHO_PATH = "/carrinhos/incluir-aposta";
     static final String INCLUIR_COMBO_CARRINHO_PATH = "/carrinhos/incluir-combo";
     static final String INCLUIR_SURPRESINHA_CARRINHO_PATH = "/carrinhos/incluir-surpresinha";
     static final String BUSCA_CARRINHO_PATH = "/carrinhos";
     //static final String DELETE_APOSTA_COMBO_CARRINHO_PATH = "/carrinhos/combos/";
     //static final String DELETE_APOSTA_CARRINHO_PATH = "/carrinhos/apostas/";
     static final String DELETE_APOSTA_COMBO_CARRINHO_PATH = "/carrinhos/combos/";
     static final String DELETE_APOSTA_CARRINHO_PATH = "/carrinhos/apostas/";
     static final String LIMPAR_CARRINHO_PATH = "/carrinhos/limpar";
     static final String VALIDAR_CARRINHO = "/carrinhos/validar";
     static final String INCLUIR_APOSTAS_CARRINHO_PATH = "/carrinhos/incluir-apostas";
     static final String CARRINHO_FAVORITO_VALIDAR_NOME = "/carrinhos-favoritos/validar";
     static final String CARRINHO_FAVORITO_SALVAR = "/carrinhos/salvar-carrinho-favorito";
     static final String CARRINHO_FAVORITO_TRANSFORMAR = "/carrinhos/incluir-carrinho-favorito/{idCarrinhoFavorito}";
     static final String CARRINHO_VERIFICA_COMPRA_PROCESSAMENTO = "/carrinhos/verifica-compra-processamento";
     // PIX
     static final String GERAR_CODIGO_PIX = "/carrinhos/gerar-cobranca-pix";
     static final String CARRINHOS_REGISTRAR_APOSTAS_PATH = "/carrinhos/registrar-apostas";
     static final String CARRINHOS_REGISTRAR_APOSTAS_ASYNC_PATH = "/carrinhos/registrar-apostas-async";
     static final String CARRINHOS_INCLUIR_APOSTA_FAVORITA_PATH = "/carrinhos/incluir-aposta-favorita/{idApostaFavorita}/{valorTipoConcurso}/{qtdTeimosinhas}";
     static final String CARRINHOS_INCLUIR_APOSTA_FAVORITA = "/carrinhos/incluir-aposta-favorita";


     //CARRINHO FAVORITOS
     static final String CARRINHO_FAVORITO_BUSCA_APOSTAS = "/carrinhos-favoritos/{idCarrinho}/apostas";
     static final String CARRINHO_FAVORITO_BUSCAR_CARRINHOS = "/carrinhos-favoritos";
     static final String CARRINHO_FAVORITO_DELETAR_CARRINHO = "/carrinhos-favoritos/{id}";
     static final String CARRINHO_FAVORITO_DELETAR_APOSTA = "/carrinhos-favoritos/{id}/apostas/{idAposta}";


     //APOSTAS
     static final String COMPLETA_JOGO_PATH = "/apostas/completar-jogo";
     static final String COMPLETA_JOGO_NOVO_PATH = "/apostas/completar-jogo/{modalidade}";
     static final String CONSULTA_DADOS_CHAVE = "/apostas/{id}/consulta-dados-chave";
     static final String CANCELA_RESGATE_PIX = "/apostas/{id}/cancelar-resgate-pix";
     static final String APOSTAS_MES_ANO_PATH = "/apostas/meses";
     static final String APOSTAS_CONFIRMADAS_NOVA_API_PATH = "/consultar";
     static final String APOSTAS_CONFIG_CONSULTA = "/apostas/config-consulta";
     static final String APOSTAS_CONFIRMADAS_PATH = "/apostas";
     static final String APOSTAS_CONFIRMADAS_DETALHES_PATH = "/apostas/{id}/detalhes-premio";
     static final String APOSTAS_CONFIRMADAS_COMPROVANTE_PATH = "/apostas/{id}/comprovante";
     static final String APOSTAS_COMPROVANTE_PREMIO_PATH = "/apostas/{id}/comprovante-premio";
     static final String APOSTAS_CONFERIR_PATH = "/apostas/{id}/conferir";
     static final String APOSTAS_GERAR_CODIGO_RESGATE_PATH = "/apostas/{id}/app/gerar-codigo-resgate-loterica";
     static final String APOSTAS_CONFIRMADAS_GERAR_COMPROVANTE_PATH = "apostas/{id}/comprovante-aposta-app-pdf";
     static final String APOSTAS_CONFIRMADAS_GERAR_COMPROVANTE_PREMIO_PATH = "apostas/{id}/comprovante-premio-app-pdf";
     static final String APOSTAS_RECUPERAR_NOVA_API_PATH = "/{id}/recuperar";
     static final String APOSTAS_HISTORICO_MES_ANO_PATH = "/historico-apostas/meses";
     static final String APOSTAS_CONFIRMADAS_HISTORICO_PATH = "/historico-apostas";
     static final String APOSTAS_CONFIRMADAS_HISTORICO_COMPROVANTE_PREMIO_PATH = "/historico-apostas/{id}/comprovante-premio";
     static final String APOSTAS_RESGATAR_PATH = "/apostas/{idAposta}/resgatar-premio";


     //APOSTAS FAVORITAS
     static final String VALIDAR_FAVORITA_PATH = "/apostas-favoritas/validar";
     static final String INCLUIR_FAVORITA_PATH = "/apostas-favoritas";
     static final String APOSTAS_FAVORITAS_PATH = "/apostas-favoritas/";
     static final String APOSTAS_FAVORITAS_MODALIDADE_PATH = "/apostas-favoritas/modalidade";
     static final String APOSTAS_FAVORITAS_LISTA_UNICA_PATH = "/apostas-favoritas/lista-unica";


     //LOTERICAS
     static final String LOTERICAS_PATH = "/lotericas";
     static final String LOTERICAS_HABILITADAS_PATH = "/lotericas/buscas-habilitadas";
     static final String LOTERICAS_CODIGO_COMPLETO_PATH = "/lotericas/codigo-completo";
     static final String LOTERICAS_NOME_FANTASIA_PATH = "/lotericas/nome-fantasia";
     static final String LOTERICAS_NOME_LOTERICO_PATH = "/lotericas/nome-loterico";


     //RECARGAPAY
	 public static final String CARDS_RECARGAPAY = "/cards";
	 public static final String CARDS_RECARGAPAY_CUSTOMER_CARD_TOKEN = "/cards/{customerCardToken}";


     //COMPRAS
     static final String CARRINHO_FAVORITO_SALVAR_COMPRA = "/compras/{idCompra}/salvar-carrinho-favorito";
     static final String COMPRAS_PATH = "/compras";
     static final String COMPRAS_MESES_PATH = "/compras/meses";
     static final String DETALHES_COMPRAS_PATH = "/compras/{id}/apostas";
     static final String SITUACOES_COMPRA_PATH = "/compras/situacoes-compra";


     //MEIO PAGAMENTO
     static final String MEIOS_PAGAMENTOS_ID = "/meios-pagamento/{id}";
     static final String MEIOS_PAGAMENTOS = "/meios-pagamento";


     //TERMO DE USO
     static final String TERMO_PATH = "/termos-de-uso/termo-vigente";
     static final String TERMO_VALIDA_ACEITO_PATH = "/termos-de-uso/valida-termo-aceito";
     static final String TERMO_ACEITAR_PATH = "/termos-de-uso/aceitar-termo-vigente";


     // DADOS USUARIO
     static final String LOGIN_PATH = "/usuarios/autenticar";
     static final String LIST_CARTOES_PATH = "usuarios/cartoes";
     static final String SAIR_PATH = "/usuarios/sair";
     static final String GRAVAR_USUARIO_PATH = "/usuarios";
     static final String EXCLUIR_CARTAO_PATH = "/usuarios/cartoes/";
     static final String USUARIOS_RECUPERAR_DADOS_PATH = "/usuarios/recuperar-dados";
     static final String USUARIOS_RECALCULAR_LIMITE_AUTORIZADO_PATH = "/usuarios/recalcular-limite-autorizado";
     static final String USUARIOS_PATH = "/usuarios";
     static final String USUARIOS_VERIFICAR_CADASTRO_PATH = "/usuarios/verificar-cadastro";
     static final String AUTOSUSPENSAO_APOSTADOR = "/usuarios/suspender-conta-apostador";
     static final String TOKEN_CADASTRO_MP_PATH = "/usuarios/gerar-token-cadastro-mercado-pago";
     static final String TOKEN_APOSTADOR_RP_PATH = "/usuarios/gerar-token-apostador-recarga-pay";
     static final String VERIFICA_USUARIO_BLOQUEADO = "/usuarios/verificar-apostador-bloqueado";


     //BILHETE
     static final String BILHETES_CONFERIR_BILHETE_PATH = "/bilhetes/conferir-bilhete";
     static final String BILHETES_CONFERIR_BILHETE_NOVO_PATH = "/bilhetes/conferir-bilhete-qrcode";

     //DUVIDA
     static final String DUVIDAS_SECOES_PATH = "/duvidas/secoes";
     static final String DUVIDAS_PATH = "/duvidas";


     //EQUIPES ESPORTIVAS
     static final String EQUIPES_ESPORTIVAS_ESCUDO_PATH = "/equipes-esportivas/{id}/escudo";
     static final String EQUIPES_ESPORTIVAS_POR_MODALIDADE_EQUIPE_PATH = "/equipes-esportivas/{idEquipe}/{idModalidade}/escudo";
     static final String EQUIPES_ESPORTIVAS_LISTA_EQUIPES_PATH = "/equipes-esportivas/listaEquipes";


     //RAPIDAO
     static final String GERAR_APOSTAS_RAPIDAO_PATH = "/rapidoes/gerar-apostas";
     static final String RAPIDOES_PATH = "/rapidoes";


     //RESULTADOS
     static final String RESULTADOS_MODALIDADE_CONCURSO_PATH = "resultados/{modalidade}/{concurso}";
     static final String RESULTADOS_MODALIDADE_UTLIMO = "resultados/{modalidade}";
     static final String RESULTADOS_MODALIDADE_CONCURSO = "/resultados";

     //LOTERICAS FAVORITAS
    static final String LOTERICAS_FAVORITAS = "/boloes/consultar-loterica-favorita";
    static final String DELETE_LOTERICA = "/boloes/excluir-loterica-favorita/{nuCd}";

     //BOLOES
     static final String BOLOES_DISPONIVEIS = "/boloes/recuperar-boloes-disponiveis";
     static final String DETALHE_BOLAO = "/boloes/detalhar-bolao";
     static final String INCLUIR_LOTERICA_FAVORITA = "/boloes/incluir-loterica-favorita";
     static final String EXCLUIR_LOTERICA_FAVORITA = "/boloes/excluir-loterica-favorita/{nuCd}";


     //AUTO AVALIACAO
     static final String AUTOAVALIACAO_PERGUNTAS = "/autoavaliacoes/recuperar-perguntas";
     static final String AUTOAVALIACAO_RESPOSTAS = "/autoavaliacoes/grava-resposta";


     //PARAMETROS
     static final String APOSTAS_VERIFICA_APRESENTA_HISTORICO_PATH = "/parametros/configuraveis-app";
     static final String VALIDA_REPRESA = "/parametros/valida-represa";


     //PUSH
     static final String NOTIFICACAO_REGISTRAR_DISPOSITIVO_PATH = "/push/v3/registros";
     static final String PUSH_NOTIFICACAO_REGISTRAR_DISPOSITIVO_PATH = "/push/registros";


     //DADOS CORPORATIVOS
     static final String MODALIDADES_PATH = "/modalidades";
     static final String LER_NOTIFICACAO = "historico-notificacoes/{idNotificacao}/visualiza";
     static final String APP_VERSAO_ATUALIZACAO_PATH = "/app/valida-versao/{versao-app}";
     static final String UFS_PATH = "/ufs";
     static final String MUNICIPIOS_PATH = "/municipios";
     static final String REPASSOMETRO_PATH = "/repassometro";
     static final String PARAM_SIMULACAO_PATH = "/parametros-simulacao";
     static final String BAIRROS_PATH = "/bairros";

     //COMBOS
     static final String BUSCA_COMBOS_PATH = "/combos";

}
