//
//  LOMunicipio.swift
//  Loterias
//
//  Created by Jenkins on 11/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import Foundation

class LOMunicipio: Codable {
    var id : LOMunicipioId
    var nome : String
    var uf : LOUnidadeFederativa
}

struct LOMunicipioId: Codable {
    var idUF : Int
    var numero : Int
    var digitoVerificador : Int
}


