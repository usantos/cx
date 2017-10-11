//
//  LOUser.swift
//  Loterias
//
//  Created by Jenkins on 11/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import Foundation

struct LOUser: Codable {
    var cpf : String
    var email : String
    var nome : String
    var nomeMae : String
    var dataNascimento : String
    var cep : String
    var aceitaTermosUso : Bool
    var aceitaReceberNoticias : Bool
    var municipioId : LOMunicipioId
    var lotericaId : Int
    var sexo : String
    var mercadoPagoCustomerId : String
}

struct LOForgotPassword : Codable {
    var cpf : String
    var email : String
    var dataNascimento : String
}
