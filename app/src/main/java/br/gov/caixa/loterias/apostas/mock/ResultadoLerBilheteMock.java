package br.gov.caixa.loterias.apostas.mock;

public class ResultadoLerBilheteMock {


    public static String retornaJson(){
        StringBuilder s = new StringBuilder();

        s.append("{ ").append("\n");
        s.append("  versao : 1.0.0," ).append("\n");
        s.append("  payload : {" ).append("\n");
        s.append("    situacao : {" ).append("\n");
        s.append("      valor : 100," ).append("\n");
        s.append("      descricao : \"Premiada\"" ).append("\n");
        s.append("    }," ).append("\n");
        s.append("    locaisParaRecebimento : [ {" ).append("\n");
        s.append("      id : 9660," ).append("\n");
        s.append("      descricao : \"Unidade Lotérica\"," ).append("\n");
        s.append("      canal : \"SISPL\"" ).append("\n");
        s.append("    }, {" ).append("\n");
        s.append("      id : 9770," ).append("\n");
        s.append("      descricao : \"Agência\"," ).append("\n");
        s.append("      canal : \"SIGEL\"" ).append("\n");
        s.append("    } ]," ).append("\n");
        s.append("    premio : {" ).append("\n");
        s.append("      concursos : [ {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5036," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        valorBruto : 1195.08," ).append("\n");
        s.append("        valorLiquido : 1195.08," ).append("\n");
        s.append("        valorIRRF : 0.00," ).append("\n");
        s.append("        situacao : \"Premiado\"," ).append("\n");
        s.append("        faixas : [ {" ).append("\n");
        s.append("          numero : 3," ).append("\n");
        s.append("          sorteio : 1," ).append("\n");
        s.append("          nome : \"QUADRA\"," ).append("\n");
        s.append("          valorLiquido : 1195.08" ).append("\n");
        s.append("        } ]" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5037," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        valorBruto : 1058.94," ).append("\n");
        s.append("        valorLiquido : 1058.94," ).append("\n");
        s.append("        valorIRRF : 0.00," ).append("\n");
        s.append("        situacao : \"Premiado\"," ).append("\n");
        s.append("        faixas : [ {" ).append("\n");
        s.append("          numero : 3," ).append("\n");
        s.append("          sorteio : 1," ).append("\n");
        s.append("          nome : \"QUADRA\"," ).append("\n");
        s.append("          valorLiquido : 1058.94" ).append("\n");
        s.append("        } ]" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5038," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        valorBruto : 440.34," ).append("\n");
        s.append("        valorLiquido : 440.34," ).append("\n");
        s.append("        valorIRRF : 0.00," ).append("\n");
        s.append("        situacao : \"Premiado\"," ).append("\n");
        s.append("        faixas : [ {" ).append("\n");
        s.append("          numero : 3," ).append("\n");
        s.append("          sorteio : 1," ).append("\n");
        s.append("          nome : \"QUADRA\"," ).append("\n");
        s.append("          valorLiquido : 440.34" ).append("\n");
        s.append("        } ]" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5039," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        valorBruto : 394.20," ).append("\n");
        s.append("        valorLiquido : 394.20," ).append("\n");
        s.append("        valorIRRF : 0.00," ).append("\n");
        s.append("        situacao : \"Premiado\"," ).append("\n");
        s.append("        faixas : [ {" ).append("\n");
        s.append("          numero : 3," ).append("\n");
        s.append("          sorteio : 1," ).append("\n");
        s.append("          nome : \"QUADRA\"," ).append("\n");
        s.append("          valorLiquido : 394.20" ).append("\n");
        s.append("        } ]" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5040," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        valorBruto : 227.04," ).append("\n");
        s.append("        valorLiquido : 227.04," ).append("\n");
        s.append("        valorIRRF : 0.00," ).append("\n");
        s.append("        situacao : \"Premiado\"," ).append("\n");
        s.append("        faixas : [ {" ).append("\n");
        s.append("          numero : 3," ).append("\n");
        s.append("          sorteio : 1," ).append("\n");
        s.append("          nome : \"QUADRA\"," ).append("\n");
        s.append("          valorLiquido : 227.04" ).append("\n");
        s.append("        } ]" ).append("\n");
        s.append("      } ]," ).append("\n");
        s.append("      concursosNaoPremiado : [ {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5029," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5030," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5031," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5032," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5033," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5034," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5035," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Premiado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5041," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Em Apuração\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5042," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Apurado\"" ).append("\n");
        s.append("      }, {" ).append("\n");
        s.append("        concurso : {" ).append("\n");
        s.append("          numero : 5043," ).append("\n");
        s.append("          aberto : false," ).append("\n");
        s.append("          naoInicializado : false" ).append("\n");
        s.append("        }," ).append("\n");
        s.append("        situacao : \"Não Apurado\"" ).append("\n");
        s.append("      } ]," ).append("\n");
        s.append("      valorTotalPremio : 9999," ).append("\n");
        s.append("      localPagamento : \"Indisponível\"" ).append("\n");
        s.append("    }," ).append("\n");
        s.append("    modalidade : {" ).append("\n");
        s.append("      valor : 2," ).append("\n");
        s.append("      descricao : \"Mega-Sena\"," ).append("\n");
        s.append("      descricaoEspecial : \"Mega da Virada\"" ).append("\n");
        s.append("    }" ).append("\n");
        s.append("  }" ).append("\n");
        s.append(" } ");

        return s.toString();
    }

    public static final String MOCK = "{\n" +
            "  \"versao\" : \"1.0.0\",\n" +
            "  \"payload\" : {\n" +
            "    \"situacao\" : {\n" +
            "      \"valor\" : 100,\n" +
            "      \"descricao\" : \"Premiada\"\n" +
            "    },\n" +
            "    \"locaisParaRecebimento\" : [ {\n" +
            "      \"id\" : 9660,\n" +
            "      \"descricao\" : \"Unidade Lotérica\",\n" +
            "      \"canal\" : \"SISPL\"\n" +
            "    }, {\n" +
            "      \"id\" : 9770,\n" +
            "      \"descricao\" : \"Agência\",\n" +
            "      \"canal\" : \"SIGEL\"\n" +
            "    } ],\n" +
            "    \"premio\" : {\n" +
            "       \"valorBruto\" : 1195.08, " +
            "       \"valorLiquido\" : 1195.08, " +
            "      \"concursos\" : [ {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5036,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"valorBruto\" : 1195.08,\n" +
            "        \"valorLiquido\" : 1195.08,\n" +
            "        \"valorIRRF\" : 0.00,\n" +
            "        \"situacao\" : \"Premiado\",\n" +
            "        \"faixas\" : [ {\n" +
            "          \"numero\" : 3,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 3,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 4,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 4,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 5,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 5,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 5,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 5,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        }, " +
            "        {\n" +
            "          \"numero\" : 6,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1195.08\n" +
            "        } " +
            " ]\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5037,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"valorBruto\" : 1058.94,\n" +
            "        \"valorLiquido\" : 1058.94,\n" +
            "        \"valorIRRF\" : 0.00,\n" +
            "        \"situacao\" : \"Premiado\",\n" +
            "        \"faixas\" : [ {\n" +
            "          \"numero\" : 3,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 1058.94\n" +
            "        } ]\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5038,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"valorBruto\" : 440.34,\n" +
            "        \"valorLiquido\" : 440.34,\n" +
            "        \"valorIRRF\" : 0.00,\n" +
            "        \"situacao\" : \"Premiado\",\n" +
            "        \"faixas\" : [ {\n" +
            "          \"numero\" : 3,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 440.34\n" +
            "        } ]\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5039,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"valorBruto\" : 394.20,\n" +
            "        \"valorLiquido\" : 394.20,\n" +
            "        \"valorIRRF\" : 0.00,\n" +
            "        \"situacao\" : \"Premiado\",\n" +
            "        \"faixas\" : [ {\n" +
            "          \"numero\" : 3,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 394.20\n" +
            "        } ]\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5040,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"valorBruto\" : 227.04,\n" +
            "        \"valorLiquido\" : 227.04,\n" +
            "        \"valorIRRF\" : 0.00,\n" +
            "        \"situacao\" : \"Premiado\",\n" +
            "        \"faixas\" : [ {\n" +
            "          \"numero\" : 3,\n" +
            "          \"sorteio\" : 1,\n" +
            "          \"nome\" : \"QUADRA\",\n" +
            "          \"valorLiquido\" : 227.04\n" +
            "        } ]\n" +
            "      } ],\n" +
            "      \"concursosNaoPremiado\" : [ {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5029,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5030,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5031,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5032,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5033,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5034,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5035,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Premiado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5041,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Em Apuração\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5042,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Apurado\"\n" +
            "      }, {\n" +
            "        \"concurso\" : {\n" +
            "          \"numero\" : 5043,\n" +
            "          \"aberto\" : false,\n" +
            "          \"naoInicializado\" : false\n" +
            "        },\n" +
            "        \"situacao\" : \"Não Apurado\"\n" +
            "      } ],\n" +
            "\t  \"valorTotalPremio\" : 9999.55, \n" +
            "      \"localPagamento\" : \"Indisponível\"\n" +
            "    },\n" +
            "    \"modalidade\" : {\n" +
            "      \"valor\" : 2,\n" +
            "      \"descricao\" : \"Mega-Sena\",\n" +
            "      \"descricaoEspecial\" : \"Mega da Virada\"\n" +
            "    }\n" +
            "  }\n" +
            "}";


}
