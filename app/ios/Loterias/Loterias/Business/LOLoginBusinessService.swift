//
//  LOLoginBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 01/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOLoginBusinessService: NSObject {
    public func login (parameters: Parameters, completionHandler: @escaping(_ error: String) -> ()) {
        
        var url = "https://logindes.caixa.gov.br/auth/realms/r_inter_auth_basic/protocol/openid-connect/"
        
        
        
        Alamofire.request(url, method: .post, parameters: parameters, encoding: JSONEncoding.default).responseString { (response) in
            
            if response.response?.statusCode != 200 {
                
            } else {
                
            }
            completionHandler("")
        }
    }
}
