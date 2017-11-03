//
//  LOParametroJogo.swift
//  Loterias
//
//  Created by Jenkins on 03/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

struct LOParametroJogo: Codable {
    var concurso : LOConcurso?
    var situacaoConcursoCanal : String?
    var probabilidadeDeGanhar : Int?
    var aceitaEspelho : Bool?
    var prognosticoMaximo : Int?
    var quantidadeMinima : Int?
    var quantidadeMaxima : Int?
    var quantidadeSurpresinhas : Int?
    var teimosinhas : [Int]?
    var valoresAposta : [LOParametroValorAposta]?
    var equipes : [LOParametroEquipe]?
    var partidas : [LOParametroPartida]?
    var valorApostaMinima : Double
    var concursoDisponivelSimulacao : Bool?
    var concursoCanalAberto : Bool?
}
