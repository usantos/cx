//
//  LORodapeRepassesCell.swift
//  Loterias
//
//  Created by Jenkins on 22/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LORodapeRepassesCell: UITableViewCell {

    @IBOutlet var aposte : UIButton! {
        didSet {
            aposte.titleLabel?.numberOfLines = 0
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
