//
//  LOSimularApostaBaixo.swift
//  Loterias
//
//  Created by Jenkins on 14/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

protocol LOSimularNumerosDelegate {
    func willDisappearSimularNumeros()
}

class LOSimularApostaBaixo: UIView, UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource, LOSimularNumerosDelegate {
    
    public var estiloModalidade : EstiloModalidade! {
        didSet {
            self.viewAdicionarCarrinho.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        }
    }
    
    @IBOutlet var collectionViewNumeros : UICollectionView! {
        didSet {
            collectionViewNumeros.delegate = self
            collectionViewNumeros.dataSource = self
        }
    }
    
    @IBOutlet var limpar : UIButton! {
        didSet {
            limpar.layer.borderWidth = 1
            limpar.layer.borderColor = limpar.titleLabel?.textColor.cgColor
        }
    }
    
    @IBOutlet var salvarAposta : UIButton! {
        didSet {
            salvarAposta.titleLabel?.minimumScaleFactor = 0.5
            salvarAposta.titleLabel?.numberOfLines = 0
            salvarAposta.titleLabel?.adjustsFontSizeToFitWidth = true
            if UIScreen.main.bounds.width == 320 {
                salvarAposta.imageEdgeInsets = UIEdgeInsets(top: 0, left: 15, bottom: 0, right: 0)
            }
        }
    }
    
    @IBOutlet var nomeAposta : HoshiTextField!
    @IBOutlet var scrollView : UIScrollView!
    @IBOutlet var verticalLayoutConstraint : NSLayoutConstraint!
    @IBOutlet weak var viewAdicionarCarrinho: UIView!

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    override func awakeFromNib() {
        let teste = Bundle.main.loadNibNamed("LOSimularApostaNumeros", owner: self, options: nil)?.first as! UIView
        self.addSubview(teste)
                
        let nib = UINib(nibName: "LONumeroApostaCell", bundle: nil)
        self.collectionViewNumeros.register(nib , forCellWithReuseIdentifier: "numeroCell")
        self.collectionViewNumeros.addObserver(self, forKeyPath: "contentSize" , options: NSKeyValueObservingOptions.old, context: nil)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 6
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "numeroCell", for: indexPath)
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        resizeCollectionViewSize()
    }
    
    func resizeCollectionViewSize() {
        self.verticalLayoutConstraint.constant = self.collectionViewNumeros.collectionViewLayout.collectionViewContentSize.height
       
        //self.scrollView.translatesAutoresizingMaskIntoConstraints = true
        self.scrollView.contentSize = CGSize(width: self.scrollView.frame.size.width, height: self.scrollView.contentSize.height + 55)
        //self.scrollView.frame = CGRect(x: self.scrollView.frame.origin.x, y: self.scrollView.frame.origin.y, width: self.scrollView.frame.size.width, height: self.scrollView.contentSize.height - 80)
        self.scrollView.layoutIfNeeded()
        self.layoutIfNeeded()
    }
    
    func willDisappearSimularNumeros() {
        self.collectionViewNumeros.removeObserver(self, forKeyPath: "contentSize")
    }

}
