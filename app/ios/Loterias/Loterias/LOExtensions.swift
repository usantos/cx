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
    
    func getBaseUrl() -> String? {
        if let path = Bundle.main.path(forResource: "server-config", ofType: "plist") {
            let dictRoot = NSDictionary(contentsOfFile: path)
            if let dict = dictRoot {
                return (dict["protocol"] as! String) + "://" + (dict["baseUrl"] as! String)
            }
        }
        return nil
    }
    
    func treatStatusCode (statusCode : Int) -> (status: Bool, message: String?) {
        switch statusCode {
        case 200:
            return (true, "")
        case 408:
            return (false, "Esgotou o tempo da requisição, tente novamente.")
        case 500:
            return (false, "Problemas no servidor, entre em contato com a equipe de Tecnologia")
        case 503:
            return (false, "Servidor indisponível, tente novamente mais tarde.")
        default:
            return (false, "")
        }
    }
    
    func image(fromLayer layer: CALayer) -> UIImage {
        UIGraphicsBeginImageContext(layer.frame.size)
        
        layer.render(in: UIGraphicsGetCurrentContext()!)
        
        let outputImage = UIGraphicsGetImageFromCurrentImageContext()
        
        UIGraphicsEndImageContext()
        
        return outputImage!
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
        
        let window  = UIApplication.shared.keyWindow!
        window.addSubview(overlayView)
        
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
    
    func alert(title: String, message: String, completionHandler: @escaping () -> ()) {
        let attributedMessage = NSAttributedString(string: String("\n".appending(message)), attributes: [
            NSAttributedStringKey.font : UIFont(name: "System-Regular", size: 18.0)!
        ])
        
        let attributedTitle = NSAttributedString(string: title, attributes: [
            NSAttributedStringKey.font : UIFont(name: "System-Regular", size: 18.0)!
        ])
        
        let alert = UIAlertController(title: "", message: "",  preferredStyle: .alert)
        alert.setValue(attributedMessage, forKey: "attributedMessage")
        alert.setValue(attributedTitle, forKey: "attributedTitle")
        
        let okAction = UIAlertAction(title: "Ok", style: .default) { (alertAction) in
            completionHandler()
        }
        
        alert.addAction(okAction)
        
        self.present(alert, animated: true, completion: nil)
    }
    
    func alertConfirm (title: String, message: String , titleCancelAction: String , titleOkAction: String , completionHandler: @escaping () -> Void) {
        
        let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        let DestructiveAction = UIAlertAction(title: titleCancelAction, style: UIAlertActionStyle.destructive) { (result : UIAlertAction) -> Void in
        }
        
        let okAction = UIAlertAction(title: titleOkAction, style: UIAlertActionStyle.default) { (result : UIAlertAction) -> Void in
            completionHandler()
        }
        alertController.addAction(DestructiveAction)
        alertController.addAction(okAction)
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    func alertSimple (title: String, message: String) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        let okAction = UIAlertAction(title: "Ok", style: UIAlertActionStyle.default)
        
        alertController.addAction(okAction)
        
        self.present(alertController, animated: true, completion: nil)
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

extension String {
    func fromBase64() -> String? {
        guard let data = Data(base64Encoded: self) else {
            return nil
        }
        
        return String(data: data, encoding: .utf8)
    }
    
    func toBase64() -> String {
        return Data(self.utf8).base64EncodedString()
    }
}

extension UIApplication {
    var statusBarView : UIView? {
        return value(forKey: "statusBar") as? UIView
    }
}


