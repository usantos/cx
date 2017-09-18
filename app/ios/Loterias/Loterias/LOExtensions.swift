//
//  LOExtensions.swift
//  Loterias
//
//  Created by Jenkins on 31/08/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

extension UITextField {
    
    func addRightImg(image: UIImage, rect: CGRect)
    {
        let imgView = UIImageView(frame: rect)
        imgView.image = image
        
        self.rightViewMode = .always
        self.rightView = imgView
    }
}

extension NSObject {
    func UIColorFromHex(_ hex: UInt, alpha: Float) -> UIColor {
        return UIColor(
            red: CGFloat((hex & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((hex & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(hex & 0x0000FF) / 255.0,
            alpha: CGFloat(alpha)
        )
    }
}

extension UIViewController {
    public func addLoadingView (alpha : CGFloat) -> UIView {
        
        let overlayView = UIView()
        let activityIndicator = UIActivityIndicatorView()

        overlayView.frame = UIScreen.main.bounds
        overlayView.center = CGPoint(x: UIScreen.main.bounds.width / 2.0, y: UIScreen.main.bounds.height / 2.0)
        overlayView.backgroundColor = UIColor.white
        overlayView.alpha = alpha
        overlayView.clipsToBounds = true
        
        activityIndicator.frame = CGRect(x: 0, y: 0, width: 40, height: 40)
        activityIndicator.activityIndicatorViewStyle = .gray
        activityIndicator.center = CGPoint(x: UIScreen.main.bounds.width / 2, y: UIScreen.main.bounds.height / 2)
        
        overlayView.addSubview(activityIndicator)
        
        activityIndicator.startAnimating()
        
        self.view.window?.addSubview(overlayView)
        
        return overlayView
    }
    
    public func addRightIconOnNavigation (image: UIImage) {
        let imgRendering = image.withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image:imgRendering , style: UIBarButtonItemStyle.plain, target: nil, action: nil)
    }
    
    public func addLeftIconOnNavigation (image: UIImage) {
        let imgRendering = image.withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image:imgRendering , style: UIBarButtonItemStyle.plain, target: nil, action: nil)
    }
}

extension CGFloat {
    static func - (left: CGFloat, right: Double) -> CGFloat {
        return left - CGFloat(right);
    }
    
    static func - (left: CGFloat, right: Int) -> CGFloat {
        return left - CGFloat(right);
    }
    
    static func + (left: CGFloat, right: Double) -> CGFloat {
        return left + CGFloat(right);
    }
    
    static func + (left: CGFloat, right: Int) -> CGFloat {
        return left + CGFloat(right);
    }
}
