//
//  LOModalideController.swift
//  Loterias
//
//  Created by Jenkins on 12/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import AnimatedCollectionViewLayout
import SideMenu

struct EstiloModalidade {
    let corClara : UInt
    let corEscura : UInt
    let imagemTopo : UIImage
    let imagemCalendario : UIImage
    let imagemRelogio : UIImage
    let tipoModalidade: LOModalidadeEnum
}

class LOModalideController: UIViewController, UISideMenuNavigationControllerDelegate, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    private var arrayModalidades = [LOModalidadeEnum]()
    var currentPage : Int = 0
    public var estiloModalidade : EstiloModalidade!
    public var tipoModalidade : LOModalidadeEnum?
    @IBOutlet weak var valorMinimoCompra : UILabel!
    @IBOutlet weak var badgeMinhasCompras : UILabel!
    @IBOutlet weak var badgeCarrinhoDeApostas : UILabel!
    @IBOutlet weak var aposteView : UIView!
    @IBOutlet var gradientView : UIView!
    @IBOutlet weak var blankViewBottom : UIView!
    var blackView : UIView!

    @IBOutlet weak var collectionView : UICollectionView! {
        didSet {
            collectionView.delegate = self
            collectionView.dataSource = self
            
            let layout = AnimatedCollectionViewLayout()
            
            if DeviceType.IS_IPHONE_6 {
                layout.animator = LinearCardAttributesAnimator(minAlpha: 0.8, itemSpacing: 0.35, scaleRate: 0.75)
            } else if DeviceType.IS_IPHONE_6P {
                layout.animator = LinearCardAttributesAnimator(minAlpha: 0.8, itemSpacing: 0.32, scaleRate: 0.78)
            } else {
                layout.animator = LinearCardAttributesAnimator(minAlpha: 0.8, itemSpacing: 0.4, scaleRate: 0.7)
            }
            layout.scrollDirection = .horizontal

            collectionView.collectionViewLayout = layout
            collectionView.showsHorizontalScrollIndicator = false
        }
    }
    
    //Trevo
    struct ConfigAntigaTrevo {
        var yCollection: CGFloat!
        var alphaBlankView: CGFloat!
        var aposteColor: UIColor!
        var aposteTituloColor : UIColor!
    }
    
    public var trevoBarButton : UIBarButtonItem!
    var trevoAberto = false
    var configAntigaTrevo : ConfigAntigaTrevo!
    
    @IBOutlet weak var rapidaoView : UIView!
    @IBOutlet weak var lerBilhetesView : UIView!
    @IBOutlet weak var favoritosView : UIView!
    @IBOutlet weak var repassesSociaisView : UIView!

    @IBOutlet var aposteButon : UIButton! {
        didSet {
            aposteButon.titleLabel?.numberOfLines = 0
        }
    }

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
    }
    
    func chamaWebservice () {
//        let loadingView = self.addLoadingView(alpha: 1.0)
//        LOModalidadesBusinessService().getModalidades(parameters: [:]) { (error, modalidades) in
//            loadingView.removeFromSuperview()
//            if error != nil {
//                self.alertSimple(title: "Atenção", message: error!)
        
                self.arrayModalidades = [LOModalidadeEnum.Megasena, LOModalidadeEnum.Duplasena, LOModalidadeEnum.Lotofacil, LOModalidadeEnum.Quina]
                self.collectionView.reloadData()
//            } else {
//                for modalidade : LOModalidade in modalidades! {
//                    self.arrayModalidades.append(LOModalidadeEnum(rawValue: modalidade.descricao)!)
//                }
//                self.collectionView.reloadData()
//            }
//        }
    }
    
    func selecionaEstiloModalidade () -> EstiloModalidade {
        var estiloModalidade : EstiloModalidade!
        
        if let tipoModalidade = self.tipoModalidade {
            switch tipoModalidade {
            case .Duplasena:
                estiloModalidade = EstiloModalidade(corClara: 0xa62a52, corEscura: 0x7d1a3f , imagemTopo: #imageLiteral(resourceName: "titulo-duplasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-duplasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-duplasena"), tipoModalidade: self.tipoModalidade!)
                break
            case .Megasena:
                estiloModalidade = EstiloModalidade(corClara: 0x00AB6B, corEscura: 0x009b66 , imagemTopo: #imageLiteral(resourceName: "titulo_megasena"), imagemCalendario: #imageLiteral(resourceName: "icon-data-megasena"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-megasena"), tipoModalidade: self.tipoModalidade!)
                break
            case .Lotomania:
                estiloModalidade = EstiloModalidade(corClara: 0xff9a2f, corEscura: 0xdd8628 , imagemTopo: #imageLiteral(resourceName: "titulo_lotomania"), imagemCalendario: #imageLiteral(resourceName: "icon-data-lotomania"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-lotomania"), tipoModalidade: self.tipoModalidade!)
                break
            case .Lotogol:
                estiloModalidade = EstiloModalidade(corClara: 0x00b7e3, corEscura: 0x009fc5 , imagemTopo: #imageLiteral(resourceName: "titulo_lotogol"), imagemCalendario: #imageLiteral(resourceName: "icon-data-lotogol"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-lotogol"), tipoModalidade: self.tipoModalidade!)
                break
            case .Lotofacil:
                estiloModalidade = EstiloModalidade(corClara: 0xbf3c93, corEscura: 0x9b2b7a , imagemTopo: #imageLiteral(resourceName: "titulo_lotofacil"), imagemCalendario: #imageLiteral(resourceName: "icon-data-lotofacil"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-lotofacil"), tipoModalidade: self.tipoModalidade!)
                break
            case .Loteca:
                estiloModalidade = EstiloModalidade(corClara: 0xf30025, corEscura: 0xd30220 , imagemTopo: #imageLiteral(resourceName: "titulo_loteca"), imagemCalendario: #imageLiteral(resourceName: "icon-data-loteca"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-loteca"), tipoModalidade: self.tipoModalidade!)
                break
            case .Federal:
                estiloModalidade = EstiloModalidade(corClara: 0x005fa1, corEscura: 0x00528c , imagemTopo: #imageLiteral(resourceName: "titulo_federal"), imagemCalendario: #imageLiteral(resourceName: "icon-data-federal"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-federal"), tipoModalidade: self.tipoModalidade!)
                break
            case .Quina:
                estiloModalidade = EstiloModalidade(corClara: 0x642573, corEscura: 0x642475 , imagemTopo: #imageLiteral(resourceName: "titulo_quina"), imagemCalendario: #imageLiteral(resourceName: "icon-data-quina"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-quina"), tipoModalidade: self.tipoModalidade!)
                break
            case .Timemania:
                estiloModalidade = EstiloModalidade(corClara: 0xa8cd4a, corEscura: 0x92b240 , imagemTopo: #imageLiteral(resourceName: "titulo_timemania"), imagemCalendario: #imageLiteral(resourceName: "icon-data-timemania"), imagemRelogio: #imageLiteral(resourceName: "icon-horario-timemania"), tipoModalidade: self.tipoModalidade!)
                break
            }
        }
        self.estiloModalidade = estiloModalidade
        return estiloModalidade
    }
    
    func estiloNavigationController () {
        let imgRendering = #imageLiteral(resourceName: "icon-menu").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: imgRendering, style: .plain, target: self, action: #selector(apresentaMenu))
        
        let imgRendering3 = #imageLiteral(resourceName: "icon-menu-right-blue").withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        
        let button =  UIButton(type: .custom)
        button.setImage(#imageLiteral(resourceName: "icon-trevo"), for: .normal)
        button.addTarget(self, action: #selector(LOModalideController.apresentaTrevo), for: .touchUpInside)
        button.frame = CGRect(x: 0, y: 0, width: 44, height: 44)
        
        trevoBarButton = UIBarButtonItem(customView: button)
        
        self.navigationItem.rightBarButtonItems = [UIBarButtonItem(image: imgRendering3, style: .plain, target: self, action: #selector(apresentaAjuda)) , trevoBarButton]
        
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
            self.aposteView.alpha = 1.0
        })
    }
    
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
        
        if indexPath.row == 0 {
            estiloAposteBotao()
        }
        
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
        currentPage = Int(ceil(x/w))
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
        
        if currentPage < self.arrayModalidades.count {
            UIView.animate(withDuration: 0.3, animations: {
                self.aposteView.alpha = 0.7
            })
        }
    }
    
    @objc func apresentaMenu () {
        self.performSegue(withIdentifier: "apresentaMenu", sender: self)
    }
    
    @objc func apresentaTrevo () {
        self.trevoBarButton.isEnabled = false

        if !trevoAberto {
            configAntigaTrevo = ConfigAntigaTrevo(yCollection: self.collectionView.frame.origin.y, alphaBlankView: self.blankViewBottom.alpha, aposteColor: self.aposteView.backgroundColor, aposteTituloColor: self.aposteButon.titleLabel?.textColor)
            self.collectionView.isScrollEnabled = false
            trevoAberto = true
            
            UIView.animate(withDuration: 0.5, animations: {
                self.collectionView.frame.origin.y = self.view.frame.size.height - 204
                self.blankViewBottom.alpha = 0.8
                self.aposteView.backgroundColor = .white
                self.aposteButon.titleLabel?.textColor = UIColor.lightGray

                self.trevoBarButton.customView?.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
                
                self.view.layoutIfNeeded()
            }, completion: { (success) in
                if success {
                    self.adicionaOpcoesTrevo()
                }
            })
        
        } else {
            self.collectionView.isScrollEnabled = true
            trevoAberto = false
            self.removeOpcoesTrevo()
        }
    }
    
    func adicionaOpcoesTrevo () {
        UIView.transition(with: self.rapidaoView, duration: 0.2, options: .transitionCrossDissolve, animations: {
            self.rapidaoView.isHidden = false
        }) { _ in
            UIView.transition(with: self.lerBilhetesView, duration: 0.2, options: .transitionCrossDissolve, animations: {
                self.lerBilhetesView.isHidden = false
            }) { _ in
                UIView.transition(with: self.favoritosView, duration: 0.2, options: .transitionCrossDissolve, animations: {
                    self.favoritosView.isHidden = false
                }) { _ in
                    UIView.transition(with: self.repassesSociaisView, duration: 0.2, options: .transitionCrossDissolve, animations: {
                        self.repassesSociaisView.isHidden = false
                    }) { _ in
                        self.trevoBarButton.isEnabled = true
                    }
                }
            }
        }
    }
    
    func removeOpcoesTrevo () {
        UIView.transition(with: self.repassesSociaisView, duration: 0.2, options: .transitionCrossDissolve, animations: {
            self.repassesSociaisView.isHidden = true
        }) { _ in
            UIView.transition(with: self.favoritosView, duration: 0.2, options: .transitionCrossDissolve, animations: {
                self.favoritosView.isHidden = true
            }) { _ in
                UIView.transition(with: self.lerBilhetesView, duration: 0.2, options: .transitionCrossDissolve, animations: {
                    self.lerBilhetesView.isHidden = true
                }) { _ in
                    UIView.transition(with: self.rapidaoView, duration: 0.2, options: .transitionCrossDissolve, animations: {
                        self.rapidaoView.isHidden = true
                    }) { _ in
                        UIView.animate(withDuration: 0.5, animations: {
                            self.collectionView.frame.origin.y = self.configAntigaTrevo.yCollection
                            self.blankViewBottom.alpha = self.configAntigaTrevo.alphaBlankView
                            self.aposteView.backgroundColor = self.configAntigaTrevo.aposteColor
                            self.aposteButon.titleLabel?.textColor = self.configAntigaTrevo.aposteTituloColor
                            self.trevoBarButton.customView?.transform = CGAffineTransform(rotationAngle: -(.pi * 2))
                            
                            self.view.layoutIfNeeded()
                        }, completion: { (_) in
                            self.trevoBarButton.isEnabled = true
                        })
                    }
                }
            }
        }
    }
    
    @objc func apresentaAjuda () {
        self.performSegue(withIdentifier: "apresentaAjuda", sender: self)
    }
    
    @IBAction func mandarParaApostas () {
        self.tipoModalidade = self.arrayModalidades[currentPage]
        self.estiloModalidade = selecionaEstiloModalidade()
        self.performSegue(withIdentifier: "mandaParaApostas", sender: self)
    }
    
    func sideMenuWillAppear(menu: UISideMenuNavigationController, animated: Bool) {
        blackView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: self.view.frame.size.height + 64))
        blackView.backgroundColor = self.UIColorFromHex(0x4e596b, alpha: 0.9)
        self.navigationController?.navigationBar.addSubview(blackView)
    }
    
    func sideMenuDidAppear(menu: UISideMenuNavigationController, animated: Bool) {
        print("SideMenu Appeared! (animated: \(animated))")
    }
    
    func sideMenuWillDisappear(menu: UISideMenuNavigationController, animated: Bool) {
        blackView.removeFromSuperview()
    }
    
    func sideMenuDidDisappear(menu: UISideMenuNavigationController, animated: Bool) {
        print("SideMenu Disappeared! (animated: \(animated))")
    }
}

