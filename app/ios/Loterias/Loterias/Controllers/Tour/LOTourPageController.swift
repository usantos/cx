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
            //return orderedViewControllers.first
            // Uncommment the line below, remove the line above if you don't want the page control to loop.
            return nil
        }
        
        guard orderedViewControllersCount > nextIndex else {
            return nil
        }
        
        return orderedViewControllers[nextIndex]
    }

    func configurePageControl() {
        // The total number of pages that are available is based on how many available colors we have.
        pageControl = UIPageControl(frame: CGRect(x: 0,y: UIScreen.main.bounds.maxY - 50,width: UIScreen.main.bounds.width,height: 50))
        self.pageControl.numberOfPages = orderedViewControllers.count
        self.pageControl.currentPage = 0
        self.pageControl.tintColor = UIColor.clear
        self.pageControl.pageIndicatorTintColor = UIColor.white
        self.pageControl.currentPageIndicatorTintColor = UIColor.black
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
