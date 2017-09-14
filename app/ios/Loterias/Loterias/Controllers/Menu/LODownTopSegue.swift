//
//  LODownTopSegue.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LODownTopSegue: UIStoryboardSegue {
    let duration: TimeInterval = 1
    let delay: TimeInterval = 0
    let animationOptions: UIViewAnimationOptions = [.curveEaseInOut]
    
    override func perform() {
        let sourceView = source.view
        let destinationView = destination.view
        
        let screenHeight = UIScreen.main.bounds.size.height
        destinationView?.transform = CGAffineTransform(translationX: 0, y: screenHeight)
        
        UIApplication.shared.keyWindow?.insertSubview(destinationView!, aboveSubview: sourceView!)
        
        UIView.animate(withDuration: duration, delay: delay, options: animationOptions, animations: {
            destinationView?.transform = .identity
        }) { (_) in
            self.source.dismiss(animated: true, completion: nil)
        }
    }
}
