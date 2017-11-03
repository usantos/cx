//
//  LOConcurso.swift
//  Loterias
//
//  Created by Jenkins on 03/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

struct LOConcurso: Codable {
    var modalidade : String?
    var modalidadeDetalhda: LOModalidade?
    var tipoConcurso : String?
    var numero : Int?
    var dataFechamento : String?
    var dataAbertura : String?
    var dataHoraSorteio : String?
    var dataSorteio : String?
    var valorApostaMinima : Double?
    var situacao : String?
    var bloqueado : Bool?
    var dataFinalBloqueio : String?
    var estimativa : Int?
    var acumulado : Int?
    var naoInicializado : Bool?
    var aberto : Bool?
}
