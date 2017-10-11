//
//  LOModalidesPageController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOModalidesPageController: UIPageViewController, UIPageViewControllerDelegate, UIPageViewControllerDataSource {
    
    private var arrayModalidades = [LOModalidadeEnum]()
    
    var selectedController : LOModalideController?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.dataSource = self
        self.delegate = self
        
        let imgRendering = #imageLiteral(resourceName: "icon-menu").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: imgRendering, style: .plain, target: self, action: #selector(apresentaMenu))
        
        let imgRendering2 = #imageLiteral(resourceName: "icon-trevo").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        let imgRendering3 = #imageLiteral(resourceName: "icon-menu-right-blue").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        
        self.navigationItem.rightBarButtonItems = [UIBarButtonItem(image: imgRendering3, style: .plain, target: self, action: #selector(apresentaMenu)) , UIBarButtonItem(image: imgRendering2, style: .plain, target: self, action: #selector(apresentaTrevo))]
        
        self.chamaWebservice()
    }
    
    func chamaWebservice () {
        LOModalidadesBusinessService().getModalidades(parameters: [:]) { (error, modalidades) in
            for modalidade : LOModalidade in modalidades! {
                self.arrayModalidades.append(LOModalidadeEnum(rawValue: modalidade.descricao)!)
            }
            
            if let firstViewController = self.orderedViewControllers.first {
                self.setViewControllers([firstViewController],
                                   direction: .forward,
                                   animated: true,
                                   completion: nil)
            }
        }
    }
    
    @objc func apresentaMenu () {
        self.performSegue(withIdentifier: "apresentaMenu", sender: self)
    }
    
    @objc func apresentaTrevo () {
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
        
        for i in 0 ..< self.arrayModalidades.count {
            array.append(self.newController(viewController: "modalidadeScreen", indexArray:i))
        }
        
        self.selectedController = array[0] as? LOModalideController
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
    
    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool){
        selectedController = pageViewController.viewControllers![0] as? LOModalideController
    }

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "apresentaTrevo" {
            let controller = (segue.destination as! UINavigationController).topViewController as? LOTrevoMenuController
            controller?.estiloModalidade = selectedController?.estiloModalidade
        }
    }
}
