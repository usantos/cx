//
//  LORodapeCell.swift
//  Loterias
//
//  Created by Jenkins on 20/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LORodapeCell: UITableViewCell {

    @IBOutlet weak var fazerNovaAposta : UIButton! {
        didSet {
            fazerNovaAposta.layer.borderWidth = 1
            fazerNovaAposta.layer.borderColor = self.UIColorFromHex(0x4BBDAC, alpha: 1.0).cgColor
        }
    }
    
    @IBOutlet var avancarParaFormaPagamento : UIButton! {
        didSet {
            avancarParaFormaPagamento.titleLabel?.numberOfLines = 0
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
