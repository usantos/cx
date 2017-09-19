//
//  LOSimularApostaBaixo.swift
//  Loterias
//
//  Created by Jenkins on 14/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

struct ScreenSize
{
    static let SCREEN_WIDTH = UIScreen.main.bounds.size.width
    static let SCREEN_HEIGHT = UIScreen.main.bounds.size.height
    static let SCREEN_MAX_LENGTH = max(ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT)
    static let SCREEN_MIN_LENGTH = min(ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT)
}

struct DeviceType
{
    static let IS_IPHONE_5 = UIDevice.current.userInterfaceIdiom == .phone && ScreenSize.SCREEN_MAX_LENGTH == 568.0
    static let IS_IPHONE_6 = UIDevice.current.userInterfaceIdiom == .phone && ScreenSize.SCREEN_MAX_LENGTH == 667.0
    static let IS_IPHONE_6P = UIDevice.current.userInterfaceIdiom == .phone && ScreenSize.SCREEN_MAX_LENGTH == 736.0
}

protocol LOSimularNumerosDelegate {
    func willDisappearSimularNumeros()
    func willAppearSimularNumeros()
}

class LOSimularApostaNumeros: UIView, UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource, LOSimularNumerosDelegate {
    
    public var estiloModalidade : EstiloModalidade! {
        didSet {
            self.viewAdicionarCarrinho.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        }
    }
    
    var delegateSimular : LOSimularApostaDelegate!
    
    @IBOutlet private weak var collectionViewNumeros : UICollectionView! {
        didSet {
            collectionViewNumeros.delegate = self
            collectionViewNumeros.dataSource = self
        }
    }
    
    @IBOutlet private weak var limpar : UIButton! {
        didSet {
            limpar.layer.borderWidth = 1
            limpar.layer.borderColor = limpar.titleLabel?.textColor.cgColor
        }
    }
    
    @IBOutlet private weak var salvarAposta : UIButton! {
        didSet {
            salvarAposta.titleLabel?.minimumScaleFactor = 0.5
            salvarAposta.titleLabel?.numberOfLines = 0
            salvarAposta.titleLabel?.adjustsFontSizeToFitWidth = true
            if UIScreen.main.bounds.width == 320 {
                salvarAposta.imageEdgeInsets = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 0)
            }
        }
    }
    
    @IBOutlet private weak var nomeAposta : HoshiTextField!
    @IBOutlet private weak var viewNomeAposta : UIView!
    
    @IBOutlet private weak var scrollView : UIScrollView!
    @IBOutlet private weak var verticalLayoutConstraint : NSLayoutConstraint!
    @IBOutlet private weak var viewAdicionarCarrinho: UIView!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    override func awakeFromNib() {
        let teste = Bundle.main.loadNibNamed("LOSimularApostaNumeros", owner: self, options: nil)?.first as! UIView
        self.addSubview(teste)
                
        self.collectionViewNumeros.register(UINib(nibName: "LONumeroApostaCell", bundle: nil) , forCellWithReuseIdentifier: "numeroCell")
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 20
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "numeroCell", for: indexPath) as! LONumeroApostaCell
        cell.numero.textColor = self.UIColorFromHex(estiloModalidade.corEscura, alpha: 1.0)

        return cell
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        resizeCollectionViewSize()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath) as! LONumeroApostaCell
        
        if cell.isSelecionado {
            cell.numero.layer.borderWidth = 1
            cell.numero.layer.borderColor = UIColor.lightGray.cgColor
            cell.numero.layer.backgroundColor = UIColor.white.cgColor
            cell.numero.textColor = self.UIColorFromHex(estiloModalidade.corEscura, alpha: 1.0)
            
            cell.isSelecionado = false
        } else {
            cell.numero.layer.borderWidth = 1
            cell.numero.layer.borderColor = self.UIColorFromHex(estiloModalidade.corEscura, alpha: 1.0).cgColor
            cell.numero.layer.backgroundColor = self.UIColorFromHex(estiloModalidade.corEscura, alpha: 1.0).cgColor
            cell.numero.textColor = .white
            
            cell.isSelecionado = true
        }
    }
    
    func resizeCollectionViewSize() {
        self.verticalLayoutConstraint.constant = self.collectionViewNumeros.collectionViewLayout.collectionViewContentSize.height
        
        //Calculando tamanho da collectionview caso venha com menos elementos do que a tela suporta.
        //Fixando então a constant para apresentação otimizada.
        
        if DeviceType.IS_IPHONE_6P {
            if self.verticalLayoutConstraint.constant < 439 {
                self.verticalLayoutConstraint.constant = 439
            } else {
                self.verticalLayoutConstraint.constant = self.collectionViewNumeros.collectionViewLayout.collectionViewContentSize.height
                
                self.scrollView.contentSize.height = self.scrollView.contentSize.height + 114
            }
        } else if DeviceType.IS_IPHONE_6 {
            if self.verticalLayoutConstraint.constant < 370 {
                self.verticalLayoutConstraint.constant = 370
            } else {
                self.verticalLayoutConstraint.constant = self.collectionViewNumeros.collectionViewLayout.collectionViewContentSize.height
                
                self.scrollView.contentSize.height = self.scrollView.contentSize.height + 114
            }
        } else if DeviceType.IS_IPHONE_5 {
            if self.verticalLayoutConstraint.constant < 275 {
                self.verticalLayoutConstraint.constant = 275
            } else{
                self.verticalLayoutConstraint.constant = self.collectionViewNumeros.collectionViewLayout.collectionViewContentSize.height
                
                self.scrollView.contentSize.height = self.scrollView.contentSize.height + 114
            }
        }
        
        self.layoutIfNeeded()
    }
    
    func willDisappearSimularNumeros() {
        self.collectionViewNumeros.removeObserver(self, forKeyPath: "contentSize")
    }
    
    func willAppearSimularNumeros() {
        self.collectionViewNumeros.addObserver(self, forKeyPath: "contentSize" , options: NSKeyValueObservingOptions.old, context: nil)
    }
    
    @IBAction func adicionarApostaNoCarrinho() {
        self.delegateSimular.adicionarNoCarrinho()
    }
}
