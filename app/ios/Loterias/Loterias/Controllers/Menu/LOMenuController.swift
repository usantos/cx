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

        /*
        
        let menuRightNavigationController = UISideMenuNavigationController(rootViewController: YourViewController)
        // UISideMenuNavigationController is a subclass of UINavigationController, so do any additional configuration
        // of it here like setting its viewControllers. If you're using storyboards, you'll want to do something like:
        // let menuRightNavigationController = storyboard!.instantiateViewController(withIdentifier: "RightMenuNavigationController") as! UISideMenuNavigationController
        SideMenuManager.menuRightNavigationController = menuRightNavigationController
        */
//        let controller  = self.storyboard?.instantiateViewController(withIdentifier: "leftMenu") as! LOLeftSideMenuControllerController
//
//        let menuLeftNavigationController = UISideMenuNavigationController(rootViewController: controller)
        //menuLeftNavigationController.menuWidth = max(round(min((self.view.frame.size.width), (self.view.frame.size.height)) * 0.85), 240)

        //menuLeftNavigationController.
        // UISideMenuNavigationController is a subclass of UINavigationController, so do any additional configuration
        // of it here like setting its viewControllers. If you're using storyboards, you'll want to do something like:
         //let menuLeftNavigationController = storyboard!.instantiateViewController(withIdentifier: "LeftMenuNavigationController") as! UISideMenuNavigationController
        self.menuWidth = max(round(min((self.view.frame.size.width), (self.view.frame.size.height)) * 0.85), 280)
        
        SideMenuManager.menuLeftNavigationController = self
        // Enable gestures. The left and/or right menus must be set up above for these to work.
        // Note that these continue to work on the Navigation Controller independent of the view controller it displays!
        //SideMenuManager.menuAddPanGestureToPresent(toView: self.navigationController!.navigationBar)
        //SideMenuManager.menuAddScreenEdgePanGesturesToPresent(toView: self.navigationController!.view)

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
