//
//  LONumeroApostaCell.swift
//  Loterias
//
//  Created by Jenkins on 13/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LONumeroApostaCell: UICollectionViewCell {
    
    @IBOutlet var numero : UILabel! {
        didSet {
            numero.layer.borderWidth = 1
            numero.layer.borderColor = UIColor.black.cgColor
            numero.layer.cornerRadius = 22.5
        }
    }
}
