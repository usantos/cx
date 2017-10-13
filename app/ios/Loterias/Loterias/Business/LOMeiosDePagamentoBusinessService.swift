//
//  LOMeiosDePagamentoBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 11/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOMeiosDePagamentoBusinessService : NSObject {
    
    public func getMeiosDePagamento (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ listaMeioPagamento : [LOMeioDePagamento]?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "meios-pagamento".toBase64())
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .get , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            debugPrint(response)
            
            let treat = self.treatStatusCode(statusCode: (response.response?.statusCode)!)
            if treat.status {
                if let json = response.result.value {
                    do {
                        let jsonData = try JSONSerialization.data(withJSONObject: json, options: .prettyPrinted)
                        var dictWithData = try JSONDecoder().decode([String: [LOMeioDePagamento]].self, from: jsonData)
                        completionHandler(nil, dictWithData["payload"])
                    } catch {
                        completionHandler(error.localizedDescription, nil)
                    }
                }
            } else {
                completionHandler(treat.message, nil)
            }
        }
    }
}
