package br.gov.caixa.loterias.core.network.mock.fixture

internal object MockBettingFixtures {
    fun modalities() = """[
        {"valor":2,"descricao":"Mega-Sena","descricaoEspecial":"Mega da Virada","descricaoGestor":"MEGA_SENA"},
        {"valor":3,"descricao":"Quina","descricaoEspecial":"Quina de São João","descricaoGestor":"QUINA"},
        {"valor":7,"descricao":"Super Sete","descricaoGestor":"SUPER_7"},
        {"valor":8,"descricao":"Lotofácil","descricaoEspecial":"Lotofácil da Independência","descricaoGestor":"LOTOFACIL"},
        {"valor":11,"descricao":"Dia de Sorte","descricaoGestor":"DIA_DE_SORTE"},
        {"valor":14,"descricao":"Lotogol","descricaoGestor":"LOTOGOL"},
        {"valor":16,"descricao":"Lotomania","descricaoGestor":"LOTOMANIA"},
        {"valor":19,"descricao":"Loteca","descricaoGestor":"LOTECA"},
        {"valor":20,"descricao":"Timemania","descricaoGestor":"TIMEMANIA"}
    ]"""

    fun bettingParameters() = """{"valorLimiteDiario":500,"valorMinimoCarrinho":30,
        "quantidadeMaximaApostasCarrinho":50,"modalidadesDisponiveisCotas":[{
        "modalidade":"MEGA_SENA","modalidadeDetalhada":{"valor":2,"descricao":"Mega-Sena"},
        "tipoConcurso":"NORMAL"}],"parametros":[{"parametroJogo":{"concurso":{
        "modalidade":"MEGA_SENA","modalidadeDetalhada":{"valor":2,"descricao":"Mega-Sena"},
        "tipoConcurso":"NORMAL","numero":2900,"dataHoraSorteio":"10/09/2026 20:00:00",
        "dataFechamento":"10/09/2099 19:00:00","estimativa":35000000,"aberto":true},
        "prognosticoMaximo":60,"quantidadeMinima":6,"quantidadeMaxima":20,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,3,4],"valoresAposta":[{"valor":5,"numeroPrognosticos":6,"numeroTrevos":0}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"TIMEMANIA","modalidadeDetalhada":{"valor":20,"descricao":"Timemania"},
        "tipoConcurso":"NORMAL","numero":2300,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "prognosticoMaximo":80,"quantidadeMinima":10,"quantidadeMaxima":10,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,4],"valoresAposta":[{"valor":3.50,"numeroPrognosticos":10}],"equipes":[
        {"numero":101,"nome":"Time Azul","uf":"DF","descricaoCurta":"AZU"},
        {"numero":102,"nome":"Time Verde","uf":"SP","descricaoCurta":"VRD"},
        {"numero":103,"nome":"Time Branco","uf":"RJ","descricaoCurta":"BRA"}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"LOTECA","modalidadeDetalhada":{"valor":19,"descricao":"Loteca"},
        "tipoConcurso":"NORMAL","numero":1200,"dataHoraSorteio":"10/09/2026 16:00:00","aberto":true},
        "prognosticoMaximo":0,"quantidadeMinima":0,"quantidadeMaxima":0,"quantidadeSurpresinhas":0,
        "teimosinhas":[0],"valoresAposta":[{"valor":3.00,"quantidadeDuplos":0,"quantidadeTriplos":0},
        {"valor":6.00,"quantidadeDuplos":1,"quantidadeTriplos":0}],"partidas":[
        {"numero":1,"equipe1":{"numero":101,"nome":"Time Azul","uf":"DF"},"equipe2":{"numero":102,"nome":"Time Verde","uf":"SP"}},
        {"numero":2,"equipe1":{"numero":103,"nome":"Time Branco","uf":"RJ"},"equipe2":{"numero":101,"nome":"Time Azul","uf":"DF"}}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"LOTOGOL","modalidadeDetalhada":{"valor":14,"descricao":"Lotogol"},
        "tipoConcurso":"NORMAL","numero":1100,"dataHoraSorteio":"10/09/2026 16:00:00","aberto":true},
        "prognosticoMaximo":0,"quantidadeMinima":0,"quantidadeMaxima":0,"quantidadeSurpresinhas":0,
        "teimosinhas":[0],"valoresAposta":[{"valor":2.00,"numeroPrognosticos":0}],"partidas":[
        {"numero":1,"equipe1":{"numero":101,"nome":"Time Azul","uf":"DF"},"equipe2":{"numero":102,"nome":"Time Verde","uf":"SP"}},
        {"numero":2,"equipe1":{"numero":103,"nome":"Time Branco","uf":"RJ"},"equipe2":{"numero":101,"nome":"Time Azul","uf":"DF"}}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"SUPER_7","modalidadeDetalhada":{"valor":7,"descricao":"Super Sete"},
        "tipoConcurso":"NORMAL","numero":750,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "prognosticoMaximo":9,"quantidadeMinima":7,"quantidadeMaxima":21,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,3],"valoresAposta":[{"valor":2.50,"numeroPrognosticos":7},
        {"valor":5.00,"numeroPrognosticos":8},{"valor":1250.00,"numeroPrognosticos":15},
        {"valor":2500000.00,"numeroPrognosticos":21}],"concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"DIA_DE_SORTE","modalidadeDetalhada":{"valor":11,"descricao":"Dia de Sorte"},
        "tipoConcurso":"NORMAL","numero":1100,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "prognosticoMaximo":31,"quantidadeMinima":7,"quantidadeMaxima":15,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,3],"valoresAposta":[{"valor":2.50,"numeroPrognosticos":7}],"meses":[
        {"numero":1,"nome":"Janeiro","abreviacao":"JAN"},{"numero":2,"nome":"Fevereiro","abreviacao":"FEV"},
        {"numero":3,"nome":"Março","abreviacao":"MAR"},{"numero":4,"nome":"Abril","abreviacao":"ABR"},
        {"numero":5,"nome":"Maio","abreviacao":"MAI"},{"numero":6,"nome":"Junho","abreviacao":"JUN"},
        {"numero":7,"nome":"Julho","abreviacao":"JUL"},{"numero":8,"nome":"Agosto","abreviacao":"AGO"},
        {"numero":9,"nome":"Setembro","abreviacao":"SET"},{"numero":10,"nome":"Outubro","abreviacao":"OUT"},
        {"numero":11,"nome":"Novembro","abreviacao":"NOV"},{"numero":12,"nome":"Dezembro","abreviacao":"DEZ"}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"LOTOMANIA","modalidadeDetalhada":{"valor":16,"descricao":"Lotomania"},
        "tipoConcurso":"NORMAL","numero":2800,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "aceitaEspelho":true,"prognosticoMaximo":100,"quantidadeMinima":50,"quantidadeMaxima":50,
        "quantidadeSurpresinhas":15,"teimosinhas":[2,3],"valoresAposta":[{"valor":3.00,"numeroPrognosticos":50}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}}]}"""

    fun sportsTeamShields() = """[
        {"id":1,"modalidades":["TIMEMANIA","LOTECA","LOTOGOL"],"equipe":{"id":101},"imagem":null},
        {"id":2,"modalidades":["TIMEMANIA","LOTECA","LOTOGOL"],"equipe":{"id":102},"imagem":null},
        {"id":3,"modalidades":["TIMEMANIA","LOTECA","LOTOGOL"],"equipe":{"id":103},"imagem":null}
    ]"""

    fun combos() = """[{"tipoCombo":{"codigo":7,"nome":"Combo da Sorte","descricao":"8 apostas","novo":true},
        "modalidadesCombo":[{"modalidade":{"valor":2,"descricao":"Mega-Sena"},"tipoConcurso":"NORMAL",
        "quantidadeApostas":2,"quantidadeTeimosinhas":0,"quantidadeDezenas":6,"numero":2900}],"valor":20}]"""

    fun rapidConfiguration() = """{"valorMinimo":30,"valorMaximo":100,
        "valorMinimoPremioPrincipal":0,"prognosticosObrigatorios":[],"prognosticosProibidos":[],
        "modalidades":[{"valor":2,"descricao":"Mega-Sena","descricaoGestor":"MEGA_SENA"}],
        "apostasFavoritas":[]}"""

    fun favoriteBetPage() = """{"rowCount":2,"lista":[
        {"id":41,"modalidade":{"valor":2,"descricao":"Mega-Sena"},"nome":"Minha Mega",
        "numerosSelecionados":[1,7,13,22,35,49]},
        {"id":42,"modalidade":{"valor":2,"descricao":"Mega-Sena"},"nome":"Aniversários",
        "numerosSelecionados":[3,9,12,18,27,31]}],"listaSize":2}"""

    fun groupedFavoriteBets() = """{"resultado":{"MEGA_SENA":${favoriteBetPage()}},"pageSize":1}"""
}
