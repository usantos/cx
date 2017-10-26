//
//  LOAjudaController.swift
//  Loterias
//
//  Created by Jenkins on 02/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOAjudaController: UIViewController {
    let gradient : CAGradientLayer = CAGradientLayer()

    @IBOutlet weak var termosDeUso: UIButton! {
        didSet {
            termosDeUso.titleLabel?.numberOfLines = 0
            termosDeUso.titleLabel?.textAlignment = .center
        }
    }
    @IBOutlet weak var politicaPrivacidade: UIButton! {
        didSet {
            politicaPrivacidade.titleLabel?.numberOfLines = 0
        }
    }
    
    @IBOutlet weak var textView : UITextView!
    
    @IBOutlet weak var duvidas: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.isNavigationBarHidden = true
        
        self.gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        self.gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        self.gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        self.gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        self.view.layer.insertSublayer(self.gradient, at: 0)            
        
        chamaWebService()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func backButtonAction () {
        self.navigationController?.popViewController(animated: true)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    
    func chamaWebService () {
        let loadingView = self.addLoadingView(alpha: 1.0)
        LOTermoBusinessService().getTermo(parameters: [:]) { (error, textoTermo) in
            loadingView.removeFromSuperview()
            if error != nil {
                self.alertSimple(title: "Atenção", message: error!)
            } else {
                self.textView.text = textoTermo
            }
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
