//
//  LOTopDownSegue.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOTopDownSegue: UIStoryboardSegue {
    let duration: TimeInterval = 0.6
    let delay: TimeInterval = 0
    let animationOptions: UIViewAnimationOptions = [.curveEaseInOut]
    
    override func perform() {
        let sourceView = source.view
        let destinationView = destination.view
                
        let screenHeight = UIScreen.main.bounds.size.height
        destinationView?.transform = CGAffineTransform(translationX: 0, y: -screenHeight)
        
        UIApplication.shared.keyWindow?.insertSubview(destinationView!, aboveSubview: sourceView!)
        
        UIView.animate(withDuration: duration, delay: delay, options: animationOptions, animations: {
            destinationView?.transform = .identity
        }) { (_) in
            self.source.present(self.destination, animated: false, completion: nil)
        }
        
    }
}
