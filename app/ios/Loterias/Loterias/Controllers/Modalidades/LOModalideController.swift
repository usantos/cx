//
//  LOModalideController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

struct EstiloModalidade {
    let hexCorAposte : UInt
    let hexCorResultado : UInt
    let imagemTopo : UIImage
    let imagemCalendario : UIImage
    let imagemRelogio : UIImage
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
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x7d1a3f, hexCorResultado: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"))
                    break
                case .Megasena:
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x60a96b, hexCorResultado: 0x508e5e , imagemTopo: #imageLiteral(resourceName: "titulo_megasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-megasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-megasena"))
                    break
                case .Lotomania:
                    //
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x7d1a3f, hexCorResultado: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"))

                    break
                case .Lotogol:
                    //
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x7d1a3f, hexCorResultado: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"))

                    break
                case .Lotofacil:
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0xbd3e96, hexCorResultado: 0x9b2b7a , imagemTopo: #imageLiteral(resourceName: "titulo_lotofacil"), imagemCalendario: #imageLiteral(resourceName: "icon-data-lotofacil"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-lotofacil"))
                    break
                case .Loteca:
                    //
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x7d1a3f, hexCorResultado: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"))

                    break
                case .Federal:
                    //
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x7d1a3f, hexCorResultado: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"))
                    break
                case .Quina:
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x642475, hexCorResultado: 0x642475 , imagemTopo: #imageLiteral(resourceName: "titulo_quina"), imagemCalendario: #imageLiteral(resourceName: "icon-data-quina"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-quina"))
                    break
                case .Timemania:
                    //
                    self.estiloModalidade = EstiloModalidade(hexCorAposte: 0x7d1a3f, hexCorResultado: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"))
                    break
            }
        }
        
        aplicarEstilo()
    }
    
    func aplicarEstilo () {
        self.iconeRelogio.image = self.estiloModalidade.imagemRelogio
        self.iconeCalendario.image = self.estiloModalidade.imagemCalendario
        self.resultadoView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.hexCorResultado, alpha: 1.0)
        self.aposteView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.hexCorAposte, alpha: 1.0)
        self.topoCorpo.image = self.estiloModalidade.imagemTopo
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
