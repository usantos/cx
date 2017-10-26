//
//  LOTourPageController.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

protocol LOTourDelegate {
    func jumpToLogin()
}

class LOTourPageController: UIPageViewController, UIPageViewControllerDelegate, UIPageViewControllerDataSource, LOTourDelegate {

    var pageControl = UIPageControl()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.dataSource = self
        self.delegate = self
        
        configurePageControl()
        if let firstViewController = orderedViewControllers.first {
            setViewControllers([firstViewController],
                               direction: .forward,
                               animated: true,
                               completion: nil)
        }
        
        let img = UIImageView(frame: self.view.frame)
        img.contentMode = .scaleAspectFit
        img.image = #imageLiteral(resourceName: "ic_bg_splash")
        
        self.view.insertSubview(img, at: 0)
        img.sendSubview(toBack: self.view)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    func newController(viewController: String) -> UIViewController {
        let controller = UIStoryboard(name: "Tour", bundle: nil).instantiateViewController(withIdentifier: viewController)
        if controller.isKind(of: LOFirstTourController.self) {
            (controller as? LOFirstTourController)?.delegate = self
        }
        
        return controller
    }
    
    lazy var orderedViewControllers: [UIViewController] = {
        return [self.newController(viewController: "screen1"),
                self.newController(viewController: "screen2"),
                self.newController(viewController: "screen3"),
                self.newController(viewController: "screen4")]
    }()
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        guard let viewControllerIndex = orderedViewControllers.index(of: viewController) else {
            return nil
        }
        
        let previousIndex = viewControllerIndex - 1
        
        guard previousIndex >= 0 else {
            return orderedViewControllers.last
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
        
        guard orderedViewControllersCount != nextIndex else {
            return nil
        }
        
        guard orderedViewControllersCount > nextIndex else {
            return nil
        }
        
        return orderedViewControllers[nextIndex]
    }

    func configurePageControl() {
        pageControl = UIPageControl(frame: CGRect(x: 0,y: UIScreen.main.bounds.maxY - 50,width: UIScreen.main.bounds.width,height: 50))
        self.pageControl.numberOfPages = orderedViewControllers.count
        self.pageControl.currentPage = 0
        
        self.view.addSubview(pageControl)
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        let pageContentViewController = pageViewController.viewControllers![0]
        self.pageControl.currentPage = orderedViewControllers.index(of: pageContentViewController)!
    }
    
    func jumpToLogin() {        
        if let lastViewController = orderedViewControllers.last {
            
            setViewControllers([lastViewController],
                               direction: .forward,
                               animated: true,
                               completion: { (sucess) in
                                self.pageControl.currentPage = self.orderedViewControllers.index(of: self.orderedViewControllers.last!)!
            })
        }
    }
}
