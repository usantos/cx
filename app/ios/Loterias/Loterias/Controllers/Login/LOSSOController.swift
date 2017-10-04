//
//  LOSSOController.swift
//  Loterias
//
//  Created by Jenkins on 02/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import AeroGearOAuth2
import AeroGearHttp

class LOSSOController: UIViewController {

    private var SERVER_URL : String = "https://logindes.caixa.gov.br"
    private var AUTH_URL : String = "https://logindes.caixa.gov.br/auth"
    
    private var AUTH_ENDPOINT : String = "/realms/r_inter_auth_basic/protocol/openid-connect/auth"
    private var ACCESS_TOKEN_ENDPOINT : String = "/realms/r_inter_auth_basic/protocol/openid-connect/token"
    private var REFRESH_TOKEN_ENDPOINT : String = "/realms/r_inter_auth_basic/protocol/openid-connect/token"
    private var AUTHZ_ACCOOUNT_ID : String = "keycloak-token"
    private var AUTH_CLIENT_ID : String = "cli-mob-mlo"
    private var AUTH_REDIRECT_URL : String = "http://localhost"
    private var MODULE_NAME : String = ""
    private var CLIENT_SECRET : String = "b54d171d-03de-41f3-b1b1-64e98672cbb8"
    
    @IBOutlet weak var aposteAgora: UIButton! {
        didSet {
            aposteAgora.layer.borderWidth = 1
            aposteAgora.layer.borderColor = UIColor.white.cgColor
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func login2 () {
        let http = Http()
        
        let keycloakHost = "https://logindes.caixa.gov.br"
        let keycloakConfig = KeycloakConfig(
            clientId: "cli-mob-mlo",
            host: keycloakHost,
            realm: "r_inter_auth_basic")

        keycloakConfig.isWebView = true
        
        let gdModule = AccountManager.addKeycloakAccount(config: keycloakConfig)
        http.authzModule = gdModule
        
        gdModule.requestAccess { (teste, error) in
            
        }
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
