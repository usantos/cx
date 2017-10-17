//
//  LOModalidadesBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 10/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOModalidadesBusinessService: NSObject {
    
    public func getModalidades (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ listaModalidade : [LOModalidade]?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "modalidades".toBase64())
    
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .get , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            let statusCode = response.response != nil ? response.response?.statusCode : 1009
            
            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value {
                    do {
                        let jsonData = try JSONSerialization.data(withJSONObject: json, options: .prettyPrinted)
                        var dictWithData = try JSONDecoder().decode([String: [LOModalidade]].self, from: jsonData)
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
