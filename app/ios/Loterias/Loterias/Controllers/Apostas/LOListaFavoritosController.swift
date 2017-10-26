//
//  LOListaFavoritosController.swift
//  Loterias
//
//  Created by Jenkins on 24/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOListaFavoritosController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet var navBar : UIView!
    @IBOutlet var tableView : UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.navigationController?.isNavigationBarHidden = true
        
        let gradientNav = CAGradientLayer()
        gradientNav.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: navBar.frame.height)
        gradientNav.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradientNav.endPoint = CGPoint(x: 1.0, y: 0.5)
        gradientNav.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        gradientNav.name = "gradientView"
        
        navBar.layer.insertSublayer(gradientNav, at: 0)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    
    @IBAction func dismiss () {
        self.navigationController?.popViewController(animated: true)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 0{
            let cell = tableView.dequeueReusableCell(withIdentifier: "topoCell", for: indexPath)
            
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "itemCell", for: indexPath)
            
            return cell
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
