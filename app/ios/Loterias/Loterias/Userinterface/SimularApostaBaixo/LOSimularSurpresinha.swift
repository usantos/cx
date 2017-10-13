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
    func willAppearSurpresinha()
}

class LOSimularSurpresinha: UIView, UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource, LOSurpresinhaDelegate {

    var delegateSimular : LOSimularApostaDelegate!
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
    
    @IBOutlet var adicionarAoCarrinho : UIButton! {
        didSet {
            adicionarAoCarrinho.titleLabel?.numberOfLines = 0
        }
    }
    
    @IBOutlet var scrollView : UIScrollView!
    @IBOutlet var verticalLayoutConstraint : NSLayoutConstraint!
    @IBOutlet weak var viewAdicionarCarrinho: UIView!
    @IBOutlet weak var numerosSurpresinhas : UITextField!
    @IBOutlet weak var subtrai : UIButton!
    @IBOutlet weak var soma : UIButton!
    var timer: Timer!

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
    }
    
    override func awakeFromNib() {
        let surpesinha = Bundle.main.loadNibNamed("LOSimularSurpresinha", owner: self, options: nil)?.first as! UIView
        self.addSubview(surpesinha)
        
        self.collectionViewNumerosMascarados.register(UINib(nibName: "LONumeroApostaCell", bundle: nil) , forCellWithReuseIdentifier: "numeroCell")
        
        soma.addTarget(self, action: #selector(buttonDownSum), for: .touchDown)
        soma.addTarget(self, action: #selector(buttonUpSum), for: [.touchUpInside, .touchUpOutside])
        
        subtrai.addTarget(self, action: #selector(buttonDownSub), for: .touchDown)
        subtrai.addTarget(self, action: #selector(buttonUpSub), for: [.touchUpInside, .touchUpOutside])
        
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 10
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
        self.verticalLayoutConstraint.constant = self.collectionViewNumerosMascarados.collectionViewLayout.collectionViewContentSize.height
        
        //Calculando tamanho da collectionview caso venha com menos elementos do que a tela suporta.
        //Fixando então a constant para apresentação otimizada.
        
        if DeviceType.IS_IPHONE_6P {
            if self.verticalLayoutConstraint.constant < 124 {
                self.verticalLayoutConstraint.constant = 124
            } else {
                self.verticalLayoutConstraint.constant = self.collectionViewNumerosMascarados.collectionViewLayout.collectionViewContentSize.height
                
                self.scrollView.contentSize.height = self.scrollView.contentSize.height + 64
            }
        } else if DeviceType.IS_IPHONE_6 {
            if self.verticalLayoutConstraint.constant < 53 {
                self.verticalLayoutConstraint.constant = 53
            } else {
                self.verticalLayoutConstraint.constant = self.collectionViewNumerosMascarados.collectionViewLayout.collectionViewContentSize.height
                
                self.scrollView.contentSize.height = self.scrollView.contentSize.height + 64
            }
        } else if DeviceType.IS_IPHONE_5 {
            self.verticalLayoutConstraint.constant = self.collectionViewNumerosMascarados.collectionViewLayout.collectionViewContentSize.height
            
            self.scrollView.contentSize.height = self.scrollView.contentSize.height + 64
        }
        self.scrollView.contentSize.width = UIScreen.main.bounds.width

        self.layoutIfNeeded()
    }
    
    func willDisappearSurpresinha() {
        self.collectionViewNumerosMascarados.removeObserver(self, forKeyPath: "contentSize")
    }
    
    func willAppearSurpresinha() {
        self.collectionViewNumerosMascarados.addObserver(self, forKeyPath: "contentSize" , options: NSKeyValueObservingOptions.old, context: nil)
    }
    
    // MARK: Soma / Subtrai Surpresinha
    @objc func somaUm () {
        if numerosSurpresinhas.text == "" {
            numerosSurpresinhas.text = "1"
        } else {
            numerosSurpresinhas.text = String(Int(numerosSurpresinhas.text!)! + 1)
        }
    }

    @objc func subtraiUm () {
        if numerosSurpresinhas.text != "" {
            if Int(numerosSurpresinhas.text!)! > 1 {
                numerosSurpresinhas.text = String(Int(numerosSurpresinhas.text!)! - 1)
            }
        }
    }
    
    @objc func buttonDownSum() {
        somaUm()
        timer = Timer.scheduledTimer(timeInterval: 0.2, target: self, selector: #selector(LOSimularSurpresinha.somaUm), userInfo: nil, repeats: true)
    }
    
    @objc func buttonUpSum() {
        timer.invalidate()
    }
    
    @objc func buttonDownSub() {
        subtraiUm()
        timer = Timer.scheduledTimer(timeInterval: 0.2, target: self, selector: #selector(LOSimularSurpresinha.subtraiUm), userInfo: nil, repeats: true)
    }
    
    @objc func buttonUpSub() {
        timer.invalidate()
    }
    
    @IBAction func adicionarApostaNoCarrinho() {
        self.delegateSimular.adicionarNoCarrinho()
    }
    
    @IBAction func enviarParaCarrinhoDeApostas () {
        self.delegateSimular.enviarParaTelaCarrinho()
    }
}
