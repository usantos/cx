//
//  LOModalidesPageController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOModalidesPageController: UIPageViewController, UIPageViewControllerDelegate, UIPageViewControllerDataSource {
    
    private var arrayModalidades = [LOModalidadeEnum.Megasena, LOModalidadeEnum.Duplasena,
                                    LOModalidadeEnum.Federal, LOModalidadeEnum.Loteca,
                                    LOModalidadeEnum.Lotofacil, LOModalidadeEnum.Lotogol,
                                    LOModalidadeEnum.Lotomania,LOModalidadeEnum.Quina,
                                    LOModalidadeEnum.Timemania]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.dataSource = self
        self.delegate = self
        
        if let firstViewController = orderedViewControllers.first {
            setViewControllers([firstViewController],
                               direction: .forward,
                               animated: true,
                               completion: nil)
        }
        
        
        let imgRendering = #imageLiteral(resourceName: "icon-menu").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: imgRendering, style: .plain, target: self, action: #selector(apresentaMenu))
        
        let imgRendering2 = #imageLiteral(resourceName: "icon-trevo").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: imgRendering2, style: .plain, target: self, action: #selector(apresentaTrevo))
    }
    
    func apresentaMenu () {
        self.performSegue(withIdentifier: "apresentaMenu", sender: self)
    }
    
    func apresentaTrevo () {
        self.performSegue(withIdentifier: "apresentaTrevo", sender: self)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    func newController(viewController: String, indexArray: Int) -> UIViewController {
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: viewController) as! LOModalideController
        viewController.tipoModalidade = arrayModalidades[indexArray]
        
        return viewController
    }
    
    lazy var orderedViewControllers: [UIViewController] = {
        var array = Array<UIViewController>()
        
        //Fixo pelo número de modalidades de apostas.
        for i in 0 ..< self.arrayModalidades.count {
            array.append(self.newController(viewController: "modalidadeScreen", indexArray:i))
        }
        
        return array
    }()
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        guard let viewControllerIndex = orderedViewControllers.index(of: viewController) else {
            return nil
        }
        
        let previousIndex = viewControllerIndex - 1
        
        // User is on the first view controller and swiped left to loop to
        // the last view controller.
        guard previousIndex >= 0 else {
            return orderedViewControllers.last
            // Uncommment the line below, remove the line above if you don't want the page control to loop.
            // return nil
        }
        
        guard orderedViewControllers.count > previousIndex else {
            return nil
        }
        
        return orderedViewControllers[previousIndex]
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
        guard let viewControllerIndex = orderedViewControllers.index(of: viewController) else {
            return nil
        }
        
        let nextIndex = viewControllerIndex + 1
        let orderedViewControllersCount = orderedViewControllers.count
        
        // User is on the last view controller and swiped right to loop to
        // the first view controller.
        guard orderedViewControllersCount != nextIndex else {
            return orderedViewControllers.first
            // Uncommment the line below, remove the line above if you don't want the page control to loop.
            //return nil
        }
        
        guard orderedViewControllersCount > nextIndex else {
            return nil
        }
        
        return orderedViewControllers[nextIndex]
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
