//
//  LOTermoBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 11/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOTermoBusinessService: NSObject {
    public func getTermo (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ termo : String?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "termos-de-uso/termo-vigente".toBase64())
        
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
    
    
    public func registraAceiteTermo (parameters: Parameters, completionHandler: @escaping(_ error: Bool?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "/termos-de-uso/aceitar-termo-vigente".toBase64())
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .post , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            debugPrint(response)
            let statusCode = response.response != nil ? response.response?.statusCode : 1009

            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value as? [String:[String:Any]] {
                    completionHandler(true)
                }
            } else {
                completionHandler(false)
            }
        }
    }
}
