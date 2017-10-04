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
    
class LOLoginController: UIViewController, UITextFieldDelegate, UINavigationControllerDelegate{

    @IBOutlet var usuario : HoshiTextField!
    @IBOutlet var senha : HoshiTextField!
    @IBOutlet var btnContinuar : UIButton!
    @IBOutlet var btnCadastrar : UIButton!
    var arrayTextFields : Array<HoshiTextField>!
    
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
        LOLoginBusinessService().login { (success) in
            
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
