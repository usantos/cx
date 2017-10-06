//
//  LOLotericaCell.swift
//  Loterias
//
//  Created by Jenkins on 06/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOLotericaCell: UITableViewCell {

    @IBOutlet var nomeLoterica : UILabel!
    @IBOutlet var codigoLoterica : UILabel!

    var isSelecionado = false
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func trocaRadioButton (sender: UIButton) {
        if !isSelecionado {
            sender.setImage(#imageLiteral(resourceName: "icon-radio-check"), for: .normal)
            isSelecionado = true
        } else {
            sender.setImage(#imageLiteral(resourceName: "icon-radio-uncheck"), for: .normal)
            isSelecionado = false
        }
    }
}
