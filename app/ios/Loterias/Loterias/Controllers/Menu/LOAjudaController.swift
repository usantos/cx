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
            politicaPrivacidade.titleLabel?.numberOfLines = 0
        }
    }
    @IBOutlet weak var politicaPrivacidade: UIButton! {
        didSet {
            politicaPrivacidade.titleLabel?.numberOfLines = 0
        }
    }
    @IBOutlet weak var duvidas: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        gradient.name = "gradientView"
        
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.isNavigationBarHidden = false
                
        self.navigationController?.navigationBar.setBackgroundImage(self.image(fromLayer: gradient), for: .default)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        if self.isMovingFromParentViewController {
            self.navigationController?.navigationBar.shadowImage = UIImage()
            self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
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
