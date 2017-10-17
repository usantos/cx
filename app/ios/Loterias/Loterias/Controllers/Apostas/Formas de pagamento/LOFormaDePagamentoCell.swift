//
//  LOFormaDePagamentoCell.swift
//  Loterias
//
//  Created by Jenkins on 03/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOFormaDePagamentoCell: UITableViewCell {

    @IBOutlet weak var bandeiraCartao : UIImageView!
    @IBOutlet weak var check : UIImageView!
    @IBOutlet weak var tituloCartao : UILabel!
    @IBOutlet weak var numero : UILabel!
    var isSelecionado : Bool = false
    
    var delegate : LOFormasDePagamentoDelegate?

    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func setSelected (isSelected: Bool) {
        if isSelected {
            self.contentView.backgroundColor = self.UIColorFromHex(0xeff5f6, alpha: 1.0)
            self.check.isHidden = false
        } else {
            self.contentView.backgroundColor = .white
            self.check.isHidden = true
        }
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func excluirFormaDePagamento () {
        self.delegate?.excluirFormaDePagamento(self)
    }
}
