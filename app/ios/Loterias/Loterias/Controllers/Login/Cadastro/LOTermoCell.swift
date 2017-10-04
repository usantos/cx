//
//  LOTermoCell.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOTermoCell: UITableViewCell {

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBOutlet var termosDeUso : UIButton! {
        didSet {
            guard let text = self.termosDeUso.titleLabel?.text else { return }
            let textRange = NSMakeRange(0, text.characters.count)
            let attributedText = NSMutableAttributedString(string: text)
            attributedText.addAttribute(NSAttributedStringKey.underlineStyle , value: NSUnderlineStyle.styleSingle.rawValue, range: textRange)
            
            self.termosDeUso.setAttributedTitle(attributedText, for: .normal)
        }
    }
    
    @IBOutlet var politicasDePrivacidade : UIButton! {
        didSet {
            guard let text = self.politicasDePrivacidade.titleLabel?.text else { return }
            let textRange = NSMakeRange(0, text.characters.count)
            let attributedText = NSMutableAttributedString(string: text)
            attributedText.addAttribute(NSAttributedStringKey.underlineStyle , value: NSUnderlineStyle.styleSingle.rawValue, range: textRange)
            
            self.politicasDePrivacidade.setAttributedTitle(attributedText, for: .normal)
        }
    }
}
