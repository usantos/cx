//
//  LORepassesSociaisController.swift
//  Loterias
//
//  Created by Jenkins on 21/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LORepassesSociaisController: UITableViewController {
    let gradientNav : CAGradientLayer = CAGradientLayer()
    let gradientCell : CAGradientLayer = CAGradientLayer()

    @IBOutlet var titleCustomView : UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.estimatedRowHeight = 150
        self.tableView.tableFooterView = UITableViewHeaderFooterView()
        DispatchQueue.main.async {
            self.gradientNav.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
            self.gradientNav.startPoint = CGPoint(x: 0.0, y: 0.5)
            self.gradientNav.endPoint = CGPoint(x: 1.0, y: 0.5)
            self.gradientNav.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
            self.gradientNav.name = "gradientView"
            
            self.navigationController?.navigationBar.tintColor = .white
            self.navigationController?.isNavigationBarHidden = false
            
            self.navigationItem.titleView = self.titleCustomView
            
            self.navigationController?.navigationBar.setBackgroundImage(self.image(fromLayer: self.gradientNav), for: .default)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        DispatchQueue.main.async {
            if self.isMovingFromParentViewController {
                self.navigationController?.navigationBar.shadowImage = UIImage()
                self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
            }
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
            cell?.contentView.layoutIfNeeded()
            
            DispatchQueue.main.async {
                self.gradientCell.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: (cell?.contentView.frame.height)!)
                self.gradientCell.startPoint = CGPoint(x: 0.0, y: 0.5)
                self.gradientCell.endPoint = CGPoint(x: 1.0, y: 0.5)
                self.gradientCell.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
                self.gradientCell.name = "gradientView"
                
                cell?.contentView.layer.insertSublayer(self.gradientCell, at: 0)
            }
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "rodapeCell", for: indexPath)
        }

        return cell!
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
}
