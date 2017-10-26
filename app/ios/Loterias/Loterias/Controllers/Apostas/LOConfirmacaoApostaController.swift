//
//  LOConfirmacaoApostaController.swift
//  Loterias
//
//  Created by Jenkins on 19/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOConfirmacaoApostaController: UIViewController {

    public var estiloModalidade : EstiloModalidade!
    
    @IBOutlet var customNavBar : LOCustomNavbarSimular!
    @IBOutlet var viewAdicionarAoCarrinho : UIView!
    @IBOutlet var fazerNovaAposta : UIButton! {
        didSet {
            fazerNovaAposta.layer.borderWidth = 1
            fazerNovaAposta.layer.borderColor = UIColor.white.cgColor
        }
    }
    @IBOutlet var botaoAdicionarAoCarrinho : UIButton! {
        didSet {
            botaoAdicionarAoCarrinho.titleLabel?.numberOfLines = 0
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        self.customNavBar.navTitle.text = estiloModalidade.tipoModalidade.rawValue
        colocaEstiloNoBotaoAdicionarAoCarinho()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        customNavBar.botaoDireita.setImage(#imageLiteral(resourceName: "icon-duvida-branco"), for: .normal)
        customNavBar.botaoDireita.addTarget(self, action: #selector(duvidaModalidade), for: .touchUpInside)
        
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.navigationBar.barTintColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        self.view.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func colocaEstiloNoBotaoAdicionarAoCarinho (){
        
        //remover mock
        if 12 > 10 {
            viewAdicionarAoCarrinho.backgroundColor = self.UIColorFromHex(0x48586c, alpha: 1.0)
        } else  {
            viewAdicionarAoCarrinho.backgroundColor = self.UIColorFromHex(0xeff5f6, alpha: 1.0)
        }
    }
    
    @IBAction func adicionarAoCarrinhoDeApostas(_ sender: Any) {
        
    }
    
    @IBAction func dismiss () {
        self.navigationController?.popViewController(animated: true)
    }
    
    @objc func duvidaModalidade () {
        self.performSegue(withIdentifier: "apresentaDetalheModalidade", sender: self)
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination.isKind(of: LODetalhamentoModalidadeController.self) {
            let destination = segue.destination as! LODetalhamentoModalidadeController
            destination.estiloModalidade = self.estiloModalidade
        }
    }
    

}
