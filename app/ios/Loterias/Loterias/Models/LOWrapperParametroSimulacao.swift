//
//  LOWrapperParametroSimulacao.swift
//  Loterias
//
//  Created by Jenkins on 03/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

struct LOWrapperParametroSimulacao: Codable {
    var valorLimiteDiario : Double?
    var valorMinimoCarrinho : Double?
    var quantidadeMaximaApostasCarrinho : Int?
    var parametros : [LOParametroSimulacao]?
}
