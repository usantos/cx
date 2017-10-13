//
//  LOFimApostaController.swift
//  Loterias
//
//  Created by Jenkins on 25/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOFimApostaController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let gradient : CAGradientLayer = CAGradientLayer()
        gradient.frame = self.view.bounds
        gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]

        self.view.layer.insertSublayer(gradient, at: 0)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func voltarParaInicio() {
        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        self.navigationController?.navigationBar.barTintColor = .white
        self.navigationController?.popToRootViewController(animated: true)
    }
    
    @IBAction func voltarParaMeuCarrinho () {
        performSegue(withIdentifier: "voltaParaCarrinhoDeApostas", sender: self)
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
