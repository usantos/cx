//
//  LOModalideController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

struct EstiloModalidade {
    let corClara : UInt
    let corEscura : UInt
    let imagemTopo : UIImage
    let imagemCalendario : UIImage
    let imagemRelogio : UIImage
    let tipoModalidade: LOModalidadeEnum
}

class LOModalideController: UIViewController {

    @IBOutlet weak var resultadoView : UIView!
    
    @IBOutlet weak var corpoView : UIView!
    @IBOutlet weak var topoCorpo : UIImageView!
    
    @IBOutlet weak var aposteView : UIView!
    @IBOutlet var aposteButon : UIButton! {
        didSet {
            aposteButon.titleLabel?.numberOfLines = 0
        }
    }
    
    @IBOutlet weak var valorMinimoCompra : UILabel!

    @IBOutlet weak var badgeMinhasCompras : UILabel!
    @IBOutlet weak var badgeCarrinhoDeApostas : UILabel!
    
    @IBOutlet weak var iconeCalendario : UIImageView!
    @IBOutlet weak var iconeRelogio : UIImageView!
    
    @IBOutlet var gradientView : UIView!
    @IBOutlet weak var valorAcumulado : UILabel!
    @IBOutlet weak var premioEstimadoDoConcurso : UILabel!
    @IBOutlet weak var diaSorteio : UILabel!
    @IBOutlet weak var diaEncerramento : UILabel!
    @IBOutlet weak var acumulouOuGanhadores : UILabel!
    
    public var tipoModalidade : LOModalidadeEnum?
    public var estiloModalidade : EstiloModalidade!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        DispatchQueue.main.async {
            self.gradientView.layoutIfNeeded()
            self.gradientView.layer.masksToBounds = true
            
            let gradient : CAGradientLayer = CAGradientLayer()
            gradient.frame = self.gradientView.bounds
            gradient.locations = [0.0, 1.0]
            gradient.colors = [self.UIColorFromHex(0xffffff, alpha: 1.0).cgColor, self.UIColorFromHex(0xd4dfe2, alpha: 1.0).cgColor]
            
            self.gradientView.layer.addSublayer(gradient)
        }
        
        montarTelaBaseadoNoTipoDeModalidade()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func montarTelaBaseadoNoTipoDeModalidade () {
        
        if let tipoModalidade = self.tipoModalidade {
            switch tipoModalidade {
                case .Duplasena:
                    self.estiloModalidade = EstiloModalidade(corClara: 0xa62a52, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
                    break
                case .Megasena:
                    self.estiloModalidade = EstiloModalidade(corClara: 0x00AB6B, corEscura: 0x009b66 , imagemTopo: #imageLiteral(resourceName: "titulo_megasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-megasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-megasena"), tipoModalidade: self.tipoModalidade!)
                    break
                case .Lotomania:
                    //
                    self.estiloModalidade = EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)

                    break
                case .Lotogol:
                    //
                    self.estiloModalidade = EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)

                    break
                case .Lotofacil:
                    self.estiloModalidade = EstiloModalidade(corClara: 0xbf3c93, corEscura: 0x9b2b7a , imagemTopo: #imageLiteral(resourceName: "titulo_lotofacil"), imagemCalendario: #imageLiteral(resourceName: "icon-data-lotofacil"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-lotofacil"), tipoModalidade: self.tipoModalidade!)
                    break
                case .Loteca:
                    //
                    self.estiloModalidade = EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)

                    break
                case .Federal:
                    //
                    self.estiloModalidade = EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
                    break
                case .Quina:
                    self.estiloModalidade = EstiloModalidade(corClara: 0x642573, corEscura: 0x642475 , imagemTopo: #imageLiteral(resourceName: "titulo_quina"), imagemCalendario: #imageLiteral(resourceName: "icon-data-quina"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-quina"), tipoModalidade: self.tipoModalidade!)
                    break
                case .Timemania:
                    //
                    self.estiloModalidade = EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
                    break
            }
        }
        
        aplicarEstilo()
    }
    
    func aplicarEstilo () {
        DispatchQueue.main.async {
            self.iconeRelogio.image = self.estiloModalidade.imagemRelogio
            self.iconeCalendario.image = self.estiloModalidade.imagemCalendario
            self.resultadoView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corEscura, alpha: 1.0)
            self.aposteView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
            self.topoCorpo.image = self.estiloModalidade.imagemTopo
        }
        
        popularCampos()
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
        
        // Montagem do campo APOSTE
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        let fixoAposte = NSAttributedString.init(string: "Aposte", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 15)!, NSAttributedStringKey.foregroundColor:UIColor.white, NSAttributedStringKey.paragraphStyle: paragraph])
        
        //Remover mock de valor
        let textoDinamico = NSAttributedString.init(string: String(format:"\npor R$%.2f", 3.5), attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 15)!, NSAttributedStringKey.foregroundColor:UIColor.white, NSAttributedStringKey.paragraphStyle: paragraph])
     
        combination = NSMutableAttributedString()
        combination.append(fixoAposte)
        combination.append(textoDinamico)
        
        self.aposteButon.setAttributedTitle(combination, for: .normal)
        // ----------------------------------
        
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
    
    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination.isKind(of: LOSimularApostasController.self)  {
            let destination = segue.destination as! LOSimularApostasController
            destination.estiloModalidade = self.estiloModalidade
        }
    }
}
