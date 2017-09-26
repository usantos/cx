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
        
        self.navigationItem.titleView = titleCustomView
    }
    
    override func viewWillAppear(_ animated: Bool) {
        UIGraphicsBeginImageContext((UIApplication.shared.statusBarView?.frame.size)!);
        UIImage(named: "020")?.draw(in: (UIApplication.shared.statusBarView?.bounds)!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        UIApplication.shared.statusBarView?.backgroundColor = UIColor(patternImage: image!)
        
        UIGraphicsBeginImageContext(CGSize(width: 375, height: 64));
        UIImage(named: "020")?.draw(in: CGRect(x: 0, y: 4, width: 375, height: 64))
        let image2 = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        UINavigationBar.appearance().setBackgroundImage(image2, for: .default)
        
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.isNavigationBarHidden = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.statusBarView?.backgroundColor = .clear
        UINavigationBar.appearance().shadowImage = UIImage()
        UINavigationBar.appearance().setBackgroundImage(UIImage(), for: .default)
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
