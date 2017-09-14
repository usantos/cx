//
//  LOLocalizacaoCell.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

class LOLocalizacaoCell: UITableViewCell, UITextFieldDelegate {
    
    @IBOutlet weak var uf: HoshiTextField! {
        didSet {
            self.uf.delegate = self
            self.uf.addRightImg(image: #imageLiteral(resourceName: "icon-combo"), rect: CGRect(x: 0, y: 0, width: 12, height: 8))
        }
    }
    
    @IBOutlet weak var cidade: HoshiTextField! {
        didSet {
            self.cidade.delegate = self
            self.cidade.addRightImg(image: #imageLiteral(resourceName: "icon-combo"), rect: CGRect(x: 0, y: 0, width: 12, height: 8))
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

}
