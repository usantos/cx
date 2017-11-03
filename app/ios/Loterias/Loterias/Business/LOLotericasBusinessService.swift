//
//  LOLotericasBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 01/11/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire

class LOLotericasBusinessService: NSObject {
    public func getLotericas (entradaServicoLotericas : LOEntradaLotericas, completionHandler: @escaping(_ status: Bool, _ msgError : String?) -> ()) {
        
        var qparams : [String:String] = Dictionary()
        qparams["idBairro"] = String(entradaServicoLotericas.idBairro!)
        qparams["codigoUF"] = String(entradaServicoLotericas.codigoUF!)
        qparams["numero"] = String(entradaServicoLotericas.numero!) //Identificador do Munic�pio
        qparams["digitoVerificador"] = String(entradaServicoLotericas.digitoVerificador!)
        qparams["idUF"] = String(entradaServicoLotericas.idUF!)
        qparams["cep"] = String(entradaServicoLotericas.cep!)
        qparams["pesquisaInteligente"] = String(entradaServicoLotericas.pesquisaInteligente!)

        let paramsUInt8 = makeQueryParamsEncrypt(params: qparams)
        
        let url = LOCrypto().cripfyBody(endpoint: "lotericas", params: paramsUInt8)
        
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
