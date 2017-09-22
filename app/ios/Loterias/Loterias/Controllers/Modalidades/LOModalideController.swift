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

    @IBOutlet var resultadoView : UIView!
    
    @IBOutlet var corpoView : UIView!
    @IBOutlet var topoCorpo : UIImageView!
    
    @IBOutlet var aposteView : UIView!
    @IBOutlet var apostaLabel : UILabel!
    
    @IBOutlet var valorMinimoCompra : UILabel!

    @IBOutlet var badgeMinhasCompras : UILabel!
    @IBOutlet var badgeCarrinhoDeApostas : UILabel!
    
    @IBOutlet var iconeCalendario : UIImageView!
    @IBOutlet var iconeRelogio : UIImageView!
    
    @IBOutlet var gradientView : UIView!
    @IBOutlet weak var valorAcumulado: UILabel!

    public var tipoModalidade : LOModalidadeEnum?
    private var estiloModalidade : EstiloModalidade!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.gradientView.layoutIfNeeded()
        self.gradientView.layer.masksToBounds = true
        
        print(self.gradientView)
        
        let gradient : CAGradientLayer = CAGradientLayer()
        gradient.frame = self.gradientView.bounds
        gradient.locations = [0.0, 1.0]
        gradient.colors = [self.UIColorFromHex(0xffffff, alpha: 1.0).cgColor, self.UIColorFromHex(0xd4dfe2, alpha: 1.0).cgColor]
        
        self.gradientView.layer.addSublayer(gradient)
        
        montarTelaBaseadoNoTipoDeModalidade()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func montarTelaBaseadoNoTipoDeModalidade () {
        
        if let tipoModalidade = self.tipoModalidade {
            switch tipoModalidade {
                case .Duplasena:
                    self.estiloModalidade = EstiloModalidade(corClara: 0xa92551, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
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
        self.iconeRelogio.image = self.estiloModalidade.imagemRelogio
        self.iconeCalendario.image = self.estiloModalidade.imagemCalendario
        self.resultadoView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corEscura, alpha: 1.0)
        self.aposteView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        self.topoCorpo.image = self.estiloModalidade.imagemTopo
        
        popularCampos()
    }
    
    func popularCampos () {
        let attStringR$ = NSAttributedString.init(string: "R$", attributes: [NSFontAttributeName: UIFont.init(name: "FuturaStd-Medium", size: 12)!, NSForegroundColorAttributeName:self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)])
        
        //Remover mock de valor
        let attStringValor = NSAttributedString.init(string: "132.000.000,00", attributes: [NSFontAttributeName: UIFont.init(name: "FuturaStd-Bold", size: 20)!, NSForegroundColorAttributeName:self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)])

        let combination = NSMutableAttributedString()
        combination.append(attStringR$)
        combination.append(attStringValor)
        
        self.valorAcumulado.attributedText = combination
    }

    
    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination.isKind(of: LOSimularApostasController.self)  {
            let destination = segue.destination as! LOSimularApostasController
            destination.estiloModalidade = self.estiloModalidade
        }
    }
}
