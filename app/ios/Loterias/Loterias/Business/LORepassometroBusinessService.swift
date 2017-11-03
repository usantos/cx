//
//  LORepassometroBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 01/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LORepassometroBusinessService: NSObject {
    public func getRepasses (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ repassometroObj : LORepassometro?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "repassometro".toBase64()!)
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .get ,headers :header).responseString { response in
            debugPrint(response)
            let statusCode = response.response != nil ? response.response?.statusCode : 1009
            
            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value {
                    do {
                        var dictWithData = try JSONDecoder().decode([String: LORepassometro].self, from: json.data(using: .utf8)!)
                        
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
