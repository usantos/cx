//
//  LORegisterBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 02/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LORegisterBusinessService: NSObject {
    
    public func getUfs (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ listaCidade : [LOUnidadeFederativa]?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "ufs".toBase64()!)
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .get ,headers :header).responseString { response in
            debugPrint(response)
            let statusCode = response.response != nil ? response.response?.statusCode : 1009

            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value {
                    do {
                        var dictWithData = try JSONDecoder().decode([String: [LOUnidadeFederativa]].self, from: json.data(using: .utf8)!)
                        
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
    
    public func getMunicipios (url: URL , parameters: Parameters, completionHandler: @escaping(_ error: String?, _ listaCidade : [LOMunicipio]?) -> ()) {
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(url, method: .get ,headers :header).responseString { response in
            debugPrint(response)
            
            let treat = self.treatStatusCode(statusCode: (response.response?.statusCode)!)
            if treat.status {
                if let json = response.result.value {
                    do {
                        var dictWithData = try JSONDecoder().decode([String: [LOMunicipio]].self, from: json.data(using: .utf8)!)
                        
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
