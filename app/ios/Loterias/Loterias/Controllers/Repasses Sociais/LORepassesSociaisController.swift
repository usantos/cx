//
//  LORepassesSociaisController.swift
//  Loterias
//
//  Created by Jenkins on 21/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LORepassesSociaisController: UITableViewController {
    let gradient : CAGradientLayer = CAGradientLayer()
    @IBOutlet var titleCustomView : UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.estimatedRowHeight = 150
        
        gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        gradient.name = "gradientView"
        
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.isNavigationBarHidden = false
        
        self.navigationItem.titleView = self.titleCustomView
        
        self.navigationController?.navigationBar.setBackgroundImage(self.image(fromLayer: gradient), for: .default)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        if self.isMovingFromParentViewController {
            self.navigationController?.navigationBar.shadowImage = UIImage()
            self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell : UITableViewCell?
        if indexPath.row == 0 {
            cell = tableView.dequeueReusableCell(withIdentifier: "cabecalhoCell", for: indexPath)
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "rodapeCell", for: indexPath)

        }

        return cell!
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
}
