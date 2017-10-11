//
//  LOSimularApostasController.swift
//  Loterias
//
//  Created by Jenkins on 13/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

protocol LOSimularApostaDelegate {
    func adicionarNoCarrinho()
}

class LOSimularApostasController: UIViewController, LOSimularApostaDelegate {
    
    //Custom views
    @IBOutlet var viewCustom : LOSimularApostaNumeros!
    @IBOutlet var surpresinhaViewCustom : LOSimularSurpresinha!
    
    //Elementos da barra superior
    @IBOutlet weak var barraSuperior : UIView!
    @IBOutlet weak var selecionaNumeros: UIButton!
    @IBOutlet weak var surpresinha: UIButton!
    @IBOutlet weak var escolheQntNumero : UIButton!
    @IBOutlet weak var escolheQntConcursos : UIButton!
    @IBOutlet weak var meuNavigationItem : LOCustomNavbarSimular!

    var delegateSurpresa: LOSurpresinhaDelegate?
    var delegateSimularNumeros : LOSimularNumerosDelegate?
    public var estiloModalidade : EstiloModalidade!
    
    var apresentaNumeros : Bool = true

    // ---------------

    override func viewWillAppear(_ animated: Bool) {
        delegateSurpresa = self.surpresinhaViewCustom
        delegateSimularNumeros = self.viewCustom
        
        delegateSurpresa?.willAppearSurpresinha()
        delegateSimularNumeros?.willAppearSimularNumeros()
        
        self.viewCustom.delegateSimular = self
        
        aplicaEstilos()
        
        //LOCryptoService().crypfyURL()
        
        LOCryptoTest().cripfyFinal()
        
        LORegisterBusinessService().getUfs(parameters: [:]) { (error, listaCidade) in
            
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        delegateSurpresa?.willDisappearSurpresinha()
        delegateSimularNumeros?.willDisappearSimularNumeros()
        
        self.navigationController?.navigationBar.barTintColor = .white
        UINavigationBar.appearance().tintColor = .blue

    }
    
    @IBAction func apresentaNumeros(_ sender: Any) {
        self.surpresinha.setImage(#imageLiteral(resourceName: "icon-surpresinha"), for: .normal)
        self.selecionaNumeros.setImage(#imageLiteral(resourceName: "icon-seleciona"), for: .normal)
        self.viewCustom.isHidden = false
        self.apresentaNumeros = true
    }
    
    @IBAction func apresentaSurpresinha(_ sender: Any) {
        self.surpresinha.setImage(#imageLiteral(resourceName: "icon-surpresinha2"), for: .normal)
        self.selecionaNumeros.setImage(#imageLiteral(resourceName: "icon-seleciona2"), for: .normal)
        self.viewCustom.isHidden = true
        self.apresentaNumeros = false
    }
    
    func aplicaEstilos () {
        self.barraSuperior.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corEscura, alpha: 1.0)
        self.navigationController?.navigationBar.barTintColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
       
        self.viewCustom.estiloModalidade = self.estiloModalidade
        self.surpresinhaViewCustom.estiloModalidade = self.estiloModalidade
        
        self.navigationController?.navigationBar.tintColor = .white
        self.meuNavigationItem.navTitle.text = self.estiloModalidade.tipoModalidade.rawValue
    }
    
    @IBAction func apresentaPopup (_ sender: UIButton) {
        self.performSegue(withIdentifier: "mandarParaPopup", sender: self)
    }
    
    func adicionarNoCarrinho() {
        self.performSegue(withIdentifier: "mandaParaAdicaoAposta", sender: self)
    }
    
    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination.isKind(of: LOConfirmacaoApostaController.self)  {
            let destination = segue.destination as! LOConfirmacaoApostaController
            destination.estiloModalidade = self.estiloModalidade
        }

    }
}
