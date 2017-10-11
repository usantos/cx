//
//  LOFinalizarCell.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOFinalizarCell: UITableViewCell {

    var delegate : LOCadastroPassoDoisDelegate!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func finalizarCadastro () {
        delegate.finalizarCadastro()
    }

}
