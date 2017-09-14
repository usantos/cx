//
//  LOOptionsCell.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

enum LOOpcoesDeBuscaEnum {
    case Localizacao
    case Cep
    case CodigoLoterica
}

class LOOptionsCell: UITableViewCell {

    @IBOutlet var localizacao : UIButton!
    @IBOutlet var cep : UIButton!
    @IBOutlet var codigoLoterica : UIButton!
    var opcaoSelecionada : LOOpcoesDeBuscaEnum?
    weak var delegate : LOCadastroPassoDoisDelegate?

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func ativarLocalizacao () {
        selecionarOpcao(button: localizacao)
        deselecionarOpcao(button: cep)
        deselecionarOpcao(button: codigoLoterica)
        opcaoSelecionada = LOOpcoesDeBuscaEnum.Localizacao
        delegate?.trocaTipoDaBusca(opcao: opcaoSelecionada!)
    }
    
    @IBAction func ativarCep () {
        selecionarOpcao(button: cep)
        deselecionarOpcao(button: localizacao)
        deselecionarOpcao(button: codigoLoterica)
        opcaoSelecionada = LOOpcoesDeBuscaEnum.Cep
        delegate?.trocaTipoDaBusca(opcao: opcaoSelecionada!)
    }
    
    @IBAction func ativarCodigo () {
        selecionarOpcao(button: codigoLoterica)
        deselecionarOpcao(button: cep)
        deselecionarOpcao(button: localizacao)
        opcaoSelecionada = LOOpcoesDeBuscaEnum.CodigoLoterica
        delegate?.trocaTipoDaBusca(opcao: opcaoSelecionada!)
    }

    func deselecionarOpcao (button : UIButton) {
        button.backgroundColor = UIColor(red: 239.0/255.0, green: 245.0/255.0, blue: 246.0/255.0, alpha: 1.0)
        button.setTitleColor(UIColor(red: 53.0/255.0, green: 168.0/255.0, blue: 152.0/255.0, alpha: 1.0), for: .normal)
    }
    
    func selecionarOpcao (button : UIButton) {
        button.backgroundColor = UIColor(red: 53.0/255.0, green: 168.0/255.0, blue: 152.0/255.0, alpha: 1.0)
        button.setTitleColor(UIColor.white, for: .normal)
    }
}
