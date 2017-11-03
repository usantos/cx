//
//  LORetornoLotericas.swift
//  Loterias
//
//  Created by Jenkins on 01/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

struct LORetornoLotericas: Codable {
    var nomeFantasia : String?
    var nome : String?
    var logradouro : String?
    var bairro : String?
    var nomeMunicipio : String?
    var nomeUF : String?
    var id : Int?
    var codigo : String?
    var idUF : Int?
    var idMunicipio : Int?
    var dvMunicipio : Int?
    var situacao : LOSituacaoLoterica?
    var polo : Int?
    var dv : Int?
    var numeroFormatado : String?
}
