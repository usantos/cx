//
//  LOPopupItensCell.swift
//  Loterias
//
//  Created by Jenkins on 01/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOPopupItensCell: UITableViewCell {

    @IBOutlet var titulo : UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        if selected {
            self.titulo.textColor = self.UIColorFromHex(0x54bbab, alpha: 1.0)
            self.titulo.font = UIFont(name: "Arial-Bold", size: 16.0)
            self.backgroundColor = self.UIColorFromHex(0xeff5f6, alpha: 1.0)
        } else {
            self.titulo.textColor = self.UIColorFromHex(0x4e596b, alpha: 1.0)
            self.titulo.font = UIFont(name: "Arial-Regular", size: 16.0)
            self.backgroundColor = UIColor.white
        }
        // Configure the view for the selected state
    }
}
