//
//  LONumeroApostaCell.swift
//  Loterias
//
//  Created by Jenkins on 13/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LONumeroApostaCell: UIView {
    
    @IBOutlet var numero : UILabel! {
        didSet {
            numero.layer.borderWidth = 1
            numero.layer.borderColor = UIColor.black.cgColor
            numero.layer.cornerRadius = 22.5
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        loadViewFromNib()
    }
    
    func loadViewFromNib() {
        let teste = Bundle.main.loadNibNamed("LONumeroApostaCell", owner: self, options: nil)?.first as! UICollectionViewCell
        self.addSubview(teste)
    }
}
