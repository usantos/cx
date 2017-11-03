//
//  LOModalidadeCell.swift
//  Loterias
//
//  Created by Jenkins on 17/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOModalidadeCell: UICollectionViewCell {
    @IBOutlet weak var resultadoView : UIView!
    
    @IBOutlet weak var topoView : UIView!
    @IBOutlet weak var trevoTopoView : UIView!
    @IBOutlet weak var topoTitulo : UILabel!
    
    @IBOutlet weak var iconeCalendario : UIImageView!
    @IBOutlet weak var iconeRelogio : UIImageView!
    
    @IBOutlet weak var valorAcumulado : UILabel!
    @IBOutlet weak var premioEstimadoDoConcurso : UILabel!
    @IBOutlet weak var diaSorteio : UILabel!
    @IBOutlet weak var diaEncerramento : UILabel!
    @IBOutlet weak var acumulouOuGanhadores : UILabel!
    var shape : CAShapeLayer?
    public var estiloModalidade : EstiloModalidade!
    
    override func awakeFromNib() {
   
    }

    func aplicarEstilo () {
        DispatchQueue.main.async {
            self.iconeRelogio.image = self.estiloModalidade.imagemRelogio
            self.iconeCalendario.image = self.estiloModalidade.imagemCalendario
            self.resultadoView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corEscura, alpha: 1.0)
            
                self.shape = CAShapeLayer()
                self.trevoTopoView.layer.insertSublayer(self.shape!, at: 0)
                self.topoView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
                self.topoTitulo.text = self.estiloModalidade.tipoModalidade.rawValue.lowercased()
                
                self.shape!.opacity = 1.0
                self.shape!.fillColor = self.UIColorFromHex(self.estiloModalidade.corEscura, alpha: 1.0).cgColor
                
                let path = UIBezierPath()
                path.move(to: CGPoint(x: 0, y: 0))
                path.addLine(to: CGPoint(x: self.trevoTopoView.frame.width, y: 0))
                path.addLine(to: CGPoint(x: self.trevoTopoView.frame.width - 30, y: self.trevoTopoView.frame.height))
                path.addLine(to: CGPoint(x: 0, y: self.trevoTopoView.frame.height))
                path.close()
                self.shape!.path = path.cgPath
        }
        
        popularCampos()
    }
    
    override func prepareForReuse() {
        self.shape?.removeFromSuperlayer()
    }
    
    func popularCampos () {
        // Montagem do campo VALOR ACUMULADO
        let fixoValor = NSAttributedString.init(string: "R$", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 12)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)])
        
        //Remover mock de valor
        let dinamicoValor = NSAttributedString.init(string: "132.000.000,00", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 20)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)])
        
        var combination = NSMutableAttributedString()
        combination.append(fixoValor)
        combination.append(dinamicoValor)
        
        self.valorAcumulado.attributedText = combination
        // ----------------------------------
        
        // ----------------------------------
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        
        //Montagem do campo PRÊMIO ESTIMADO
        let fixoPremio = NSAttributedString.init(string: "Prêmio estimado do concurso ", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 14)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0), NSAttributedStringKey.paragraphStyle: paragraph])
        
        //Remover mock de valor
        let textoDinamicoPremio = NSAttributedString.init(string: "1843", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 14)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0), NSAttributedStringKey.paragraphStyle: paragraph])
        
        combination = NSMutableAttributedString()
        combination.append(fixoPremio)
        combination.append(textoDinamicoPremio)
        
        self.premioEstimadoDoConcurso.attributedText = combination
        
        //Montagem do campo DIA SORTEIO
        let fixoSorteio = NSAttributedString.init(string: "Sorteio\n", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 12)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0)])
        
        //Remover mock de valor
        let textoDinamicoSorteio = NSAttributedString.init(string: "Sábado, 18 de julho", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 12)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0)])
        
        combination = NSMutableAttributedString()
        combination.append(fixoSorteio)
        combination.append(textoDinamicoSorteio)
        
        self.diaSorteio.attributedText = combination
        // ----------------------------------
        
        //Montagem do campo FIM APOSTAS
        let fixoFimApostas = NSAttributedString.init(string: "Apostas se encerram em\n", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 12)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0)])
        
        //Remover mock de valor
        let textoDinamicoFimApostas = NSAttributedString.init(string: "2 dias e 20 horas", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 12)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0)])
        
        combination = NSMutableAttributedString()
        combination.append(fixoFimApostas)
        combination.append(textoDinamicoFimApostas)
        
        self.diaEncerramento.attributedText = combination
        // ----------------------------------
        
        verificaSeGanhadoresOuAcumulado()
    }
    
    func verificaSeGanhadoresOuAcumulado () {
        //Remover mock quando tiver serviço
        if false {
            acumulouOuGanhadores.layer.borderWidth = 1
            acumulouOuGanhadores.layer.borderColor = self.UIColorFromHex(0xd0e0e3, alpha: 1.0).cgColor
            acumulouOuGanhadores.attributedText = NSAttributedString.init(string: String(format: "%d ganhadores", 3), attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 14)!, NSAttributedStringKey.foregroundColor:self.UIColorFromHex(0x4e596b, alpha: 1.0)])
        }
    }
}
