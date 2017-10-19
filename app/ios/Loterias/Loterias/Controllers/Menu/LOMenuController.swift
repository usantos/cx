//
//  LOMenuController.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import SideMenu

class LOMenuController: UISideMenuNavigationController {

    override func viewDidLoad() {
        super.viewDidLoad()

        self.menuWidth = max(round(min((self.view.frame.size.width), (self.view.frame.size.height)) * 0.85), 280)
        SideMenuManager.default.menuLeftNavigationController = self
        SideMenuManager.default.menuPresentMode = .menuSlideIn
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
