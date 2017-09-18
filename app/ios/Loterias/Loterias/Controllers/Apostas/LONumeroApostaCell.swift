//
//  LONumeroApostaCell.swift
//  Loterias
//
//  Created by Jenkins on 13/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LONumeroApostaCell: UICollectionViewCell {
    
    var isSelecionado: Bool = false
    
    @IBOutlet var numero : UILabel! {
        didSet {
            numero.layer.borderWidth = 1
            numero.layer.borderColor = UIColor.lightGray.cgColor
            numero.layer.cornerRadius = 22.5
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
    }
}
