//
//  LOBandeiraCell.swift
//  Loterias
//
//  Created by Jenkins on 22/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOBandeiraCell: UICollectionViewCell {

    @IBOutlet var bandeiraImg : UIImageView!
    var isSelecionado : Bool = false
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func setSelected (isSelected: Bool) {
        if isSelected {
            self.layer.borderWidth = 1
            self.layer.borderColor = self.UIColorFromHex(0x2ABCAC, alpha: 1.0).cgColor
        } else {
            self.layer.borderColor = UIColor.white.cgColor
        }
    }
    
    override func prepareForReuse() {
        setSelected(isSelected: false)
    }

}
