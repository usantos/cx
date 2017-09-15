//
//  LOSimularApostasController.swift
//  Loterias
//
//  Created by Jenkins on 13/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOSimularApostasController: UIViewController {
    
    //Custom views
    @IBOutlet var viewCustom : LOSimularApostaBaixo!
    @IBOutlet var surpresinhaViewCustom : LOSimularSurpresinha!
    
    //Elementos da barra superior
    @IBOutlet weak var barraSuperior : UIView!
    @IBOutlet weak var selecionaNumeros: UIButton!
    @IBOutlet weak var surpresinha: UIButton!
    @IBOutlet weak var escolheQntNumero : UIButton!
    @IBOutlet weak var escolheQntConcursos : UIButton!

    var delegateSurpresa: LOSurpresinhaDelegate?
    var delegateSimularNumeros : LOSimularNumerosDelegate?
    public var estiloModalidade : EstiloModalidade!
    
    var apresentaNumeros : Bool = true

    // ---------------

    override func viewWillAppear(_ animated: Bool) {
        delegateSurpresa = self.surpresinhaViewCustom
        delegateSimularNumeros = self.viewCustom
        
        aplicaEstilos()
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
        
    }
    
    @IBAction func apresentaPopup (_ sender: UIButton) {
        self.performSegue(withIdentifier: "mandarParaPopup", sender: self)
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
