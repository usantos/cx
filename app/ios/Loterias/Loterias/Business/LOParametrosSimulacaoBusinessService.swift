//
//  LOParametrosSimulacaoBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 03/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOParametrosSimulacaoBusinessService: NSObject {
    
    public func getParametrosSimulacao (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ termo : String?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "parametros-simulacao".toBase64()!)
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .get , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            let statusCode = response.response != nil ? response.response?.statusCode : 1009
            
            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value as? [String:[String:Any]] {
                    completionHandler(nil, json["payload"]!["conteudo"] as? String)
                }
            } else {
                completionHandler(treat.message, nil)
            }
        }
    }
}
