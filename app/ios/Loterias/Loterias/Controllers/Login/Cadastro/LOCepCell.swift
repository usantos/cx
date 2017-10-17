//
//  LOCepCell.swift
//  Loterias
//
//  Created by Jenkins on 16/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import InputMask
import TextFieldEffects

class LOCepCell: UITableViewCell, UITextFieldDelegate, MaskedTextFieldDelegateListener {
    var maskedDelegateCep: MaskedTextFieldDelegate!
    
    @IBOutlet weak var cep: HoshiTextField! {
        didSet {
            cep.delegate = self
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        
        maskedDelegateCep = MaskedTextFieldDelegate(format: "[00000]-[000]")
        maskedDelegateCep.listener = self
        
        cep.delegate = maskedDelegateCep
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        validaCamposDeTexto(textField)
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
