//
//  LOCustomNavbarSimular.swift
//  Loterias
//
//  Created by Jenkins on 18/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOCustomNavbarSimular: UIView {

    @IBOutlet weak var navTitle : UILabel!
    @IBOutlet weak var concurso : UILabel!
    @IBOutlet weak var sorteio : UILabel!
    @IBOutlet var botaoDireita : UIButton!

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
