//
//  LOSimularSurpresinha.swift
//  Loterias
//
//  Created by Jenkins on 15/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

protocol LOSurpresinhaDelegate {
    func willDisappearSurpresinha()
}

class LOSimularSurpresinha: UIView, UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource, LOSurpresinhaDelegate {

    public var estiloModalidade : EstiloModalidade!{
        didSet {
            self.viewAdicionarCarrinho.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        }
    }
    
    @IBOutlet var collectionViewNumerosMascarados : UICollectionView! {
        didSet {
            collectionViewNumerosMascarados.delegate = self
            collectionViewNumerosMascarados.dataSource = self
        }
    }
    
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
        let teste = Bundle.main.loadNibNamed("LOSimularSurpresinha", owner: self, options: nil)?.first as! UIView
        self.addSubview(teste)
        
        let nib = UINib(nibName: "LONumeroApostaCell", bundle: nil)
        self.collectionViewNumerosMascarados.register(nib , forCellWithReuseIdentifier: "numeroCell")
        
        self.collectionViewNumerosMascarados.addObserver(self, forKeyPath: "contentSize" , options: NSKeyValueObservingOptions.old, context: nil)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 20
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
 
        return collectionView.dequeueReusableCell(withReuseIdentifier: "numeroCell", for: indexPath)
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        resizeCollectionViewSize()
    }

    func resizeCollectionViewSize() {
        self.verticalLayoutConstraint.constant = self.collectionViewNumerosMascarados.collectionViewLayout.collectionViewContentSize.height
        
        self.layoutIfNeeded()
    }
    
    func willDisappearSurpresinha() {
        self.collectionViewNumerosMascarados.removeObserver(self, forKeyPath: "contentSize")
    }

}
