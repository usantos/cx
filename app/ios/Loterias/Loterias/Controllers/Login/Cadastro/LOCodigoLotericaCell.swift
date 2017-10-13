//
//  LOCodigoLotericaCell.swift
//  Loterias
//
//  Created by Jenkins on 13/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

class LOCodigoLotericaCell: UITableViewCell {

    @IBOutlet var codigoText : HoshiTextField! {
        didSet {
            let button = UIButton(type: .custom)
            button.setImage(#imageLiteral(resourceName: "icon-duvida"), for: .normal)
            button.imageEdgeInsets = UIEdgeInsetsMake(0, -16, 0, 0)
            button.frame = CGRect(x: CGFloat(codigoText.frame.size.width - 25), y: CGFloat(5), width: CGFloat(25), height: CGFloat(25))
            button.addTarget(self, action: #selector(self.refresh), for: .touchUpInside)
            codigoText.rightView = button
            codigoText.rightViewMode = .always
        }
    }
    
    @IBAction func refresh(_ sender: Any) {
        print("apresentar ajuda")
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
