	//
//  LOLoginController.swift
//  Loterias
//
//  Created by Jenkins on 31/08/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects
import Alamofire
import AeroGearOAuth2
import AeroGearHttp
    
class LOLoginController: UIViewController, UITextFieldDelegate, UINavigationControllerDelegate{

    @IBOutlet var usuario : HoshiTextField!
    @IBOutlet var senha : HoshiTextField!
    @IBOutlet var btnContinuar : UIButton!
    @IBOutlet var btnCadastrar : UIButton!
    var arrayTextFields : Array<HoshiTextField>!
    
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.arrayTextFields = [self.usuario, self.senha]
        
        self.navigationController?.navigationBar.barTintColor = UIColor(red: 0.0/255.0, green: 118.0/255.0, blue: 183.0/255.0, alpha: 1.0)
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        self.navigationController?.navigationBar.shadowImage = UIImage()
        
        self.addRightIconOnNavigation(image: #imageLiteral(resourceName: "icon-loteria"))
        self.configuraTextfields()
    }
    
    func configuraTextfields () {
        self.usuario.borderInactiveColor = UIColor(red: 0.0/255.0, green: 179.0/255.0, blue: 171.0/255.0, alpha: 1)
        self.usuario.borderActiveColor = UIColor(red: 0.0/255.0, green: 179.0/255.0, blue: 171.0/255.0, alpha: 1)
        self.usuario.delegate = self
        
        self.senha.borderInactiveColor = UIColor(red: 0.0/255.0, green: 179.0/255.0, blue: 171.0/255.0, alpha: 1)
        self.senha.borderActiveColor = UIColor(red: 0.0/255.0, green: 179.0/255.0, blue: 171.0/255.0, alpha: 1)
        self.senha.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func dismissKeyboard() {
        self.view.endEditing(true)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        if self.validaCamposDeTexto(textField) {
            self.btnContinuar.isEnabled = true
            self.btnContinuar.backgroundColor = UIColor(red: 0.0/255.0, green: 179.0/255.0, blue: 171.0/255.0, alpha: 1)
            self.btnContinuar.setTitleColor(UIColor.white, for: .normal)
            
            self.btnCadastrar.isEnabled = false
            self.btnCadastrar.backgroundColor = UIColor(red: 0.0/255.0, green: 135.0/255.0, blue: 191.0/255.0, alpha: 1)
            self.btnCadastrar.setTitleColor(UIColor(red: 0.0/255.0, green: 93.0/255.0, blue: 170.0/255.0, alpha: 1), for: .normal)
        } else {
            self.btnContinuar.isEnabled = false
            self.btnContinuar.backgroundColor = UIColor(red: 0.0/255.0, green: 135.0/255.0, blue: 191.0/255.0, alpha: 1)
            self.btnContinuar.setTitleColor(UIColor(red: 0.0/255.0, green: 93.0/255.0, blue: 170.0/255.0, alpha: 1), for: .normal)
            
            self.btnCadastrar.isEnabled = true
            self.btnCadastrar.backgroundColor = UIColor(red: 255.0/255.0, green: 145.0/255.0, blue: 33.0/255.0, alpha: 1)
            self.btnCadastrar.setTitleColor(UIColor.white, for: .normal)
        }
    }
    
    func validaCamposDeTexto(_ textField: UITextField) -> Bool {
        for textfieldLocal in self.arrayTextFields {
            if (textfieldLocal.text?.characters.count)! == 0 {
                textfieldLocal.rightView = nil
                return false
            } else {
                textfieldLocal.addRightImg(image: #imageLiteral(resourceName: "icon-input-ok"), rect: CGRect(x: 0, y: 0, width: 12, height: 10))
            }
        }
        
        return true
    }
    
    @IBAction func logar () {
        self.login2()
    }
    
    func login () {
        let http = Http()

        let keycloakConfig = Config(base: SERVER_URL,
                                    authzEndpoint: AUTH_ENDPOINT,
                                    redirectURL: AUTH_REDIRECT_URL,
                                    accessTokenEndpoint: ACCESS_TOKEN_ENDPOINT,
                                    clientId: AUTH_CLIENT_ID,
                                    refreshTokenEndpoint: REFRESH_TOKEN_ENDPOINT,
                                    revokeTokenEndpoint: "",
                                    isOpenIDConnect: true,
                                    userInfoEndpoint: "",
                                    scopes: ["offline"],
                                    clientSecret: CLIENT_SECRET,
                                    accountId: AUTHZ_ACCOOUNT_ID,
                                    isWebView: false)
        
        let gdModule = KeycloakOAuth2Module(config: keycloakConfig, session: UntrustedMemoryOAuth2Session(accountId: "ACCOUNT_FOR_CLIENTID_\(keycloakConfig.clientId)"))
        //let gdModule = AccountManager.addKeycloakAccount(keycloakConfig)
        http.authzModule = gdModule
        
//        gdModule.login { (teste, claim, error) in
//        
//        }
    }
    
    func login2 () {
        let http = Http()
        
        let keycloakHost = "https://logindes.caixa.gov.br"
        let keycloakConfig = KeycloakConfig(
            clientId: "cli-mob-mlo",
            host: keycloakHost,
            realm: "r_inter_auth_basic")
        // If you want to use embedded web view uncomment
        keycloakConfig.isWebView = true
        
        // Workaround issue on Keychain https://forums.developer.apple.com/message/23323
//        let gdModule = KeycloakOAuth2Module(config: keycloakConfig, session: UntrustedMemoryOAuth2Session(accountId: "ACCOUNT_FOR_CLIENTID_\(keycloakConfig.clientId)"))
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
