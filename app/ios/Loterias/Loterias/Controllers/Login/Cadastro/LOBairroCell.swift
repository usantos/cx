//
//  LOBairroCell.swift
//  Loterias
//
//  Created by Jenkins on 16/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

class LOBairroCell: UITableViewCell, UITextFieldDelegate {

    @IBOutlet weak var bairro: HoshiTextField! {
        didSet {
            self.bairro.delegate = self
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        if textField == bairro {
            validaCamposDeTexto(textField)
        }
    }

    func validaCamposDeTexto(_ textField: UITextField) -> Bool {
        if (textField.text?.characters.count)! == 0 {
            textField.rightView = nil
            return false
        } else {
            textField.addRightImg(image: #imageLiteral(resourceName: "icon-input-ok"), rect: CGRect(x: 0, y: 0, width: 12, height: 10))
        }
        
        return true
    }
    
}
