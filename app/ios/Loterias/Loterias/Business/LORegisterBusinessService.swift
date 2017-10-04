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
    
    public func getUfs (parameters: Parameters, completionHandler: @escaping(_ error: String?, _ listaCidade : LOWrapperCidade?) -> ()) {
        let urlTest = URL(string: getBaseUrl()! + "ufs".toBase64())
        
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest!, method: .get ,headers :header).responseString { response in
            debugPrint(response)
            var listaCidades : LOWrapperCidade?
            
            let treat = self.treatStatusCode(statusCode: (response.response?.statusCode)!)
            if treat.status {
                if let json = response.result.value {
                    do {
                        listaCidades = try JSONDecoder().decode(LOWrapperCidade.self, from: json.data(using: .utf8)!)
                        completionHandler(nil, listaCidades)
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
