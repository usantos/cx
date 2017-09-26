//
//  LOTrevoMenuController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOTrevoMenuController: UIViewController {

    @IBOutlet weak var trevo: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    
    @IBOutlet weak var resultadoAnterior : UIButton!
    @IBOutlet weak var aposte : UIButton!
    @IBOutlet weak var trevoBaixo : UIButton!
    
    var estiloModalidade : EstiloModalidade!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.scrollView.layoutIfNeeded()
        self.scrollView.layer.masksToBounds = true
        
        
        let gradient : CAGradientLayer = CAGradientLayer()
        gradient.frame = self.scrollView.bounds
        gradient.locations = [0.0, 1.0]
        gradient.colors = [self.UIColorFromHex(0xffffff, alpha: 1.0).cgColor, self.UIColorFromHex(0xd4dfe2, alpha: 1.0).cgColor]
        
        self.scrollView.layer.insertSublayer(gradient, at: 0)
        
        aplicaEstilos()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = true
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func dismissView () {
        let transition = CATransition()
        transition.duration = 0.5
        transition.type = kCATransitionPush
        transition.subtype = kCATransitionFromTop
        view.window!.layer.add(transition, forKey: kCATransition)
        self.dismiss(animated: true, completion: nil)
    }
    
    func aplicaEstilos () {
        resultadoAnterior.backgroundColor = self.UIColorFromHex(estiloModalidade.corEscura, alpha: 1.0)
        aposte.backgroundColor = self.UIColorFromHex(estiloModalidade.corEscura, alpha: 1.0)
        trevoBaixo.backgroundColor = self.UIColorFromHex(estiloModalidade.corClara, alpha: 1.0)
        
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        let fixoAposte = NSAttributedString.init(string: "Aposte", attributes: [NSFontAttributeName: UIFont.init(name: "FuturaStd-Bold", size: 15)!, NSForegroundColorAttributeName:UIColor.white, NSParagraphStyleAttributeName: paragraph])
        
        //Remover mock de valor
        let textoDinamico = NSAttributedString.init(string: String(format:"\npor R$%.2f", 3.5), attributes: [NSFontAttributeName: UIFont.init(name: "FuturaStd-Medium", size: 15)!, NSForegroundColorAttributeName:UIColor.white, NSParagraphStyleAttributeName: paragraph])
        
        let combination = NSMutableAttributedString()
        combination.append(fixoAposte)
        combination.append(textoDinamico)
        
        self.aposte.titleLabel?.numberOfLines = 0
        self.aposte.setAttributedTitle(combination, for: .normal)
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
