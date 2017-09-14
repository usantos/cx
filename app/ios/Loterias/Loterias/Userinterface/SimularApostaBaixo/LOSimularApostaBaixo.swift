//
//  LOSimularApostaBaixo.swift
//  Loterias
//
//  Created by Jenkins on 14/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOSimularApostaBaixo: UIView {
    @IBOutlet var topButton : UIButton!
    @IBOutlet var nameView : UIView!
    @IBOutlet var limparAposta : UIButton!
    @IBOutlet var adicionarAoCarrinho : UIButton!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        inicializa()
    }
    
    private func inicializa() {
        
    }
}
