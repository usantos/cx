//
//  LOMeioDePagamento.swift
//  Loterias
//
//  Created by Jenkins on 11/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import Foundation

struct LOMeioDePagamento : Codable {
    var id : Int
    var nomePagamentoCompra : String
    var nomePagamentoPremio : String
    var publicKey : String
}
