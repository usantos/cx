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

    override func viewDidLoad() {
        super.viewDidLoad()

    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.navigationBar.barTintColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)
        self.view.backgroundColor = self.UIColorFromHex(self.estiloModalidade.corClara, alpha: 1.0)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
