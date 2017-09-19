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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.isNavigationBarHidden = true
        
        self.scrollView.layoutIfNeeded()
        self.scrollView.layer.masksToBounds = true
        
        
        let gradient : CAGradientLayer = CAGradientLayer()
        gradient.frame = self.scrollView.bounds
        gradient.locations = [0.0, 1.0]
        gradient.colors = [self.UIColorFromHex(0xffffff, alpha: 1.0).cgColor, self.UIColorFromHex(0xd4dfe2, alpha: 1.0).cgColor]
        
        self.scrollView.layer.insertSublayer(gradient, at: 0)
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
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
