//
//  LOLoginBusinessService.swift
//  Loterias
//
//  Created by Jenkins on 01/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import Alamofire
import AeroGearOAuth2
import AeroGearHttp

class LOLoginBusinessService: NSObject {
    
    private var SERVER_URL : String = "https://logindes.caixa.gov.br"
    private var AUTH_URL : String = "https://logindes.caixa.gov.br/auth"
    private var REALM : String = "r_inter_auth_basic"
    private var AUTH_ENDPOINT : String = "/realms/r_inter_auth_basic/protocol/openid-connect/auth"
    private var ACCESS_TOKEN_ENDPOINT : String = "/realms/r_inter_auth_basic/protocol/openid-connect/token"
    private var REFRESH_TOKEN_ENDPOINT : String = "/realms/r_inter_auth_basic/protocol/openid-connect/token"
    private var AUTHZ_ACCOOUNT_ID : String = "keycloak-token"
    private var AUTH_CLIENT_ID : String = "cli-mob-mlo"
    private var AUTH_REDIRECT_URL : String = "http://localhost"
    private var MODULE_NAME : String = ""
    private var CLIENT_SECRET : String = "b54d171d-03de-41f3-b1b1-64e98672cbb8"
    
    public func login (completionHandler: @escaping(_ success: Bool) -> ()) {
        let keycloakConfig = KeycloakConfig(
            clientId: AUTH_CLIENT_ID,
            host: SERVER_URL,
            realm: REALM)
        
        keycloakConfig.isWebView = true
        
        let gdModule = AccountManager.addKeycloakAccount(config: keycloakConfig)
        Http().authzModule = gdModule
        
        gdModule.requestAccess { (teste, error) in
            
        }
    }
}
