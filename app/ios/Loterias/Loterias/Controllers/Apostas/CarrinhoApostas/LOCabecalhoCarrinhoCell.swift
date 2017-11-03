//
//  LOCabecalhoCarrinhoCell.swift
//  Loterias
//
//  Created by Jenkins on 27/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOCabecalhoCarrinhoCell: UITableViewCell {
    @IBOutlet var textoDinamicoTopo : UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        var combination = NSMutableAttributedString()

        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .left
        let fixoAposte = NSAttributedString.init(string: "Lembre-se que o valor mínimo do total de compras nesse aplicativo é ", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 15)!, NSAttributedStringKey.foregroundColor:UIColorFromHex(0x48586C, alpha: 1.0), NSAttributedStringKey.paragraphStyle: paragraph])
        
        //Remover mock de valor
        let textoDinamico = NSAttributedString.init(string: String(format:"R$%.2f", 24.00), attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 15)!, NSAttributedStringKey.foregroundColor:UIColorFromHex(0x48586C, alpha: 1.0), NSAttributedStringKey.paragraphStyle: paragraph])
        
        combination = NSMutableAttributedString()
        combination.append(fixoAposte)
        combination.append(textoDinamico)
        
        textoDinamicoTopo.attributedText = combination
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
