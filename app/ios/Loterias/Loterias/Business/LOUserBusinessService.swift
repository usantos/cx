//
//  LOUserBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 31/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOUserBusinessService: NSObject {
    public func alterarSenha (senhaAtual: String ,novaSenha: String, completionHandler: @escaping(_ error: String?, _ termo : String?) -> ()) {
        
        let obj = LOAlterarSenha(senhaAtual: senhaAtual, novaSenha: novaSenha)
        var url : URL?
        
        let jsonEncoder = JSONEncoder()
        do {
            let jsonData = try jsonEncoder.encode(obj)
            let jsonString = String(data: jsonData, encoding: .utf8)
            url = LOCrypto().cripfyBody(endpoint: "usuarios/alterar-senha", params: Array(jsonString!.utf8))
        }
        catch {
        }
        
        let header: HTTPHeaders = ["Content-Type": "application/json; charset=utf-8"]
        Alamofire.request(url!, method: .post , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            let statusCode = response.response != nil ? response.response?.statusCode : 1009
            
            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value as? [String:Any] {
                    //completionHandler(nil, json["payload"]!["conteudo"] as? String)
                }
            } else {
                completionHandler(treat.message, nil)
            }
        }
    }
    
    public func autenticar (email: String ,senha: String, completionHandler: @escaping(_ error: String?, _ termo : String?) -> ()) {
        
        let obj = LOAutenticarEntrada(email: email, senha: senha)
        var url : URL?
        
        let jsonEncoder = JSONEncoder()
        do {
            let jsonData = try jsonEncoder.encode(obj)
            let jsonString = String(data: jsonData, encoding: .utf8)
            url = LOCrypto().cripfyBody(endpoint: "usuarios/autenticar", params: Array(jsonString!.utf8))
        }
        catch {
        }
        
        let header: HTTPHeaders = ["Content-Type": "application/json; charset=utf-8"]
        Alamofire.request(url!, method: .post , encoding: JSONEncoding.prettyPrinted ,headers :header).responseJSON { response in
            let statusCode = response.response != nil ? response.response?.statusCode : 1009
            
            let treat = self.treatStatusCode(statusCode: statusCode!)
            if treat.status {
                if let json = response.result.value as? [String:Any] {
                    //completionHandler(nil, json["payload"]!["conteudo"] as? String)
                }
            } else {
                completionHandler(treat.message, nil)
            }
        }
    }
}
