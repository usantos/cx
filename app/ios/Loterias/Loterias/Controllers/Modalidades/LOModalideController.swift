//
//  LOModalideController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import AnimatedCollectionViewLayout

struct EstiloModalidade {
    let corClara : UInt
    let corEscura : UInt
    let imagemTopo : UIImage
    let imagemCalendario : UIImage
    let imagemRelogio : UIImage
    let tipoModalidade: LOModalidadeEnum
}

class LOModalideController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    private var arrayModalidades = [LOModalidadeEnum]()

    @IBOutlet weak var collectionView : UICollectionView! {
        didSet {
            collectionView.delegate = self
            collectionView.dataSource = self
            
            let layout = AnimatedCollectionViewLayout()
            
            if DeviceType.IS_IPHONE_6 {
                layout.animator = LinearCardAttributesAnimator(minAlpha: 0.8, itemSpacing: 0.35, scaleRate: 0.75)
            } else if DeviceType.IS_IPHONE_6P {
                layout.animator = LinearCardAttributesAnimator(minAlpha: 0.8, itemSpacing: 0.35, scaleRate: 0.75)
            } else {
                layout.animator = LinearCardAttributesAnimator(minAlpha: 0.8, itemSpacing: 0.4, scaleRate: 0.7)
            }
            layout.scrollDirection = .horizontal

            collectionView.collectionViewLayout = layout
            collectionView.showsHorizontalScrollIndicator = false
        }
    }
    //NEW
    
    @IBOutlet weak var aposteView : UIView!
    @IBOutlet var aposteButon : UIButton! {
        didSet {
            aposteButon.titleLabel?.numberOfLines = 0
        }
    }
    
    @IBOutlet weak var valorMinimoCompra : UILabel!
    @IBOutlet weak var badgeMinhasCompras : UILabel!
    @IBOutlet weak var badgeCarrinhoDeApostas : UILabel!
    @IBOutlet var gradientView : UIView!
    public var estiloModalidade : EstiloModalidade!
    public var tipoModalidade : LOModalidadeEnum?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        estiloNavigationController()
        
        collectionView?.isPagingEnabled = true
        
        DispatchQueue.main.async {
            self.gradientView.layoutIfNeeded()
            self.gradientView.layer.masksToBounds = true
            
            let gradient : CAGradientLayer = CAGradientLayer()
            gradient.frame = self.gradientView.bounds
            gradient.locations = [0.0, 1.0]
            gradient.colors = [self.UIColorFromHex(0xffffff, alpha: 1.0).cgColor, self.UIColorFromHex(0xd4dfe2, alpha: 1.0).cgColor]
            
            self.gradientView.layer.addSublayer(gradient)
        }
        
        chamaWebservice()
        //montarTelaBaseadoNoTipoDeModalidade()
    }
    
    func chamaWebservice () {
        LOModalidadesBusinessService().getModalidades(parameters: [:]) { (error, modalidades) in
            if error != nil {
                //self.alertSimple(title: "Atenção", message: error!)
                
                self.arrayModalidades = [LOModalidadeEnum.Megasena, LOModalidadeEnum.Duplasena, LOModalidadeEnum.Lotofacil, LOModalidadeEnum.Quina]
                self.collectionView.reloadData()
            } else {
                for modalidade : LOModalidade in modalidades! {
                    self.arrayModalidades.append(LOModalidadeEnum(rawValue: modalidade.descricao)!)
                }
                self.collectionView.reloadData()
            }
        }
    }
    
    func selecionaEstiloModalidade () -> EstiloModalidade {
        if let tipoModalidade = self.tipoModalidade {
            switch tipoModalidade {
            case .Duplasena:
                return EstiloModalidade(corClara: 0xa62a52, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
            case .Megasena:
                return EstiloModalidade(corClara: 0x00AB6B, corEscura: 0x009b66 , imagemTopo: #imageLiteral(resourceName: "titulo_megasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-megasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-megasena"), tipoModalidade: self.tipoModalidade!)
            case .Lotomania:
                //
                return EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
                
            case .Lotogol:
                //
                return EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
            case .Lotofacil:
                return EstiloModalidade(corClara: 0xbf3c93, corEscura: 0x9b2b7a , imagemTopo: #imageLiteral(resourceName: "titulo_lotofacil"), imagemCalendario: #imageLiteral(resourceName: "icon-data-lotofacil"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-lotofacil"), tipoModalidade: self.tipoModalidade!)
            case .Loteca:
                //
                return EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
            case .Federal:
                //
                return EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
            case .Quina:
                return EstiloModalidade(corClara: 0x642573, corEscura: 0x642475 , imagemTopo: #imageLiteral(resourceName: "titulo_quina"), imagemCalendario: #imageLiteral(resourceName: "icon-data-quina"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-quina"), tipoModalidade: self.tipoModalidade!)
            case .Timemania:
                //
                return EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
            }
        }
        return EstiloModalidade(corClara: 0x7d1a3f, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func estiloNavigationController () {
        let imgRendering = #imageLiteral(resourceName: "icon-menu").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: imgRendering, style: .plain, target: self, action: #selector(apresentaMenu))
        
        let imgRendering2 = #imageLiteral(resourceName: "icon-trevo").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        let imgRendering3 = #imageLiteral(resourceName: "icon-menu-right-blue").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        
        self.navigationItem.rightBarButtonItems = [UIBarButtonItem(image: imgRendering3, style: .plain, target: self, action: #selector(apresentaMenu)) , UIBarButtonItem(image: imgRendering2, style: .plain, target: self, action: #selector(apresentaTrevo))]
        
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.view.backgroundColor = UIColor.clear
    }
    
    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination.isKind(of: LOSimularApostasController.self)  {
            let destination = segue.destination as! LOSimularApostasController
            destination.estiloModalidade = self.estiloModalidade
        }
    }
    
    func estiloAposteBotao () {
        // Montagem do campo APOSTE
        var combination = NSMutableAttributedString()

        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        let fixoAposte = NSAttributedString.init(string: "Aposte", attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Bold", size: 15)!, NSAttributedStringKey.foregroundColor:UIColor.white, NSAttributedStringKey.paragraphStyle: paragraph])
        
        //Remover mock de valor
        let textoDinamico = NSAttributedString.init(string: String(format:"\npor R$%.2f", 3.5), attributes: [NSAttributedStringKey.font: UIFont.init(name: "FuturaStd-Medium", size: 15)!, NSAttributedStringKey.foregroundColor:UIColor.white, NSAttributedStringKey.paragraphStyle: paragraph])
        
        combination = NSMutableAttributedString()
        combination.append(fixoAposte)
        combination.append(textoDinamico)
        
        self.aposteButon.setAttributedTitle(combination, for: .normal)
        UIView.animate(withDuration: 0.3, animations: { () -> Void in
            self.aposteView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        })
    }
    
    //NEW
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.arrayModalidades.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cellTeste", for: indexPath) as? LOModalidadeCell
        self.tipoModalidade = self.arrayModalidades[indexPath.row]
        cell?.estiloModalidade = selecionaEstiloModalidade()
        cell?.aplicarEstilo()
        return cell!
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: view.bounds.width, height: view.bounds.height + 80)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return .zero
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let x = collectionView.contentOffset.x
        let w = collectionView.bounds.size.width
        let currentPage = Int(ceil(x/w))
        print("Current Page: \(currentPage)")
        
        if currentPage < self.arrayModalidades.count {
            self.tipoModalidade = self.arrayModalidades[currentPage]
            self.estiloModalidade = selecionaEstiloModalidade()
            estiloAposteBotao()
        }
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        let x = collectionView.contentOffset.x
        let w = collectionView.bounds.size.width
        let currentPage = Int(ceil(x/w))
        
        UIView.animate(withDuration: 0.5, animations: { () -> Void in
            self.tipoModalidade = self.arrayModalidades[currentPage + 1]
            self.estiloModalidade = self.selecionaEstiloModalidade()
            
            self.aposteView.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        })
    }
    
    @objc func apresentaMenu () {
        self.performSegue(withIdentifier: "apresentaMenu", sender: self)
    }
    
    @objc func apresentaTrevo () {
        self.performSegue(withIdentifier: "apresentaTrevo", sender: self)
    }
    
    @objc func apresentaAjuda () {
        self.performSegue(withIdentifier: "apresentaAjuda", sender: self)
    }
}
