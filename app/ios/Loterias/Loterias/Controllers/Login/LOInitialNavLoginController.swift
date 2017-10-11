//
//  LOInitialNavLoginController.swift
//  Loterias
//
//  Created by Jenkins on 09/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOInitialNavLoginController: UINavigationController {

    var isFirstTime : Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.navigationBar.barTintColor = UIColor(red: 0.0/255.0, green: 116.0/255.0, blue: 181.0/255.0, alpha: 1.0)
        self.navigationController?.navigationBar.backgroundColor	 = UIColor(red: 0.0/255.0, green: 116.0/255.0, blue: 181.0/255.0, alpha: 1.0)
        
        if isFirstTime {
            self.performSegue(withIdentifier: "sendToRegister", sender: self)
        } else {
            self.performSegue(withIdentifier: "sendToLogin", sender: self)
        }
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
