//
//  LOApostasBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 31/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOApostasBusinessService: NSObject {
    
    func joinerNumbers (numbers: Array<Int>) -> String {
        var string = ""
        for number in numbers {
            if number == numbers.last {
                string += String(number)
            } else {
                string += String(number) + ","
            }
        }
        return string
    }
    
    public func completaJogo (quantidadeTotalNumeros: Int ,numerosMarcados: Array<Int>, menorPrognostico: Int, maiorPrognostico: Int, completionHandler: @escaping(_ status: Bool, _ msgError : String?) -> ()) {
        
        var qparams : [String:String] = Dictionary()
        qparams["quantidadeTotalNumeros"] = String(quantidadeTotalNumeros)
        qparams["numerosMarcados"] = joinerNumbers(numbers: numerosMarcados)
        qparams["menorPrognostico"] = String(menorPrognostico)
        qparams["maiorPrognostico"] = String(maiorPrognostico)

        let paramsUInt8 = makeQueryParamsEncrypt(params: qparams)
        
        let url = LOCrypto().cripfyBody(endpoint: "apostas/completar-jogo", params: paramsUInt8)
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(url!, method: .get , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            let statusCode = response.response != nil ? response.response?.statusCode : 1009
            
            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                completionHandler(true, nil)
            } else {
                completionHandler(false, treat.message)
            }
        }
    }
}
