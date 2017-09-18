//
//  LOCustomNavbarSimular.swift
//  Loterias
//
//  Created by Jenkins on 18/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOCustomNavbarSimular: UIView {

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    override func awakeFromNib() {
        let teste = Bundle.main.loadNibNamed("LOCustomNavbarSimular", owner: self, options: nil)?.first as! UIView
        self.addSubview(teste)
    }
}
