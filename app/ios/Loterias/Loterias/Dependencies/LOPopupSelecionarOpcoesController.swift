//
//  LOPopupSelecionarOpcoesController.swift
//  Loterias
//
//  Created by Jenkins on 01/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

public protocol LOPopupDelegate: class {
    func didSelect(string : String, sender: UITextField)
}

class LOPopupSelecionarOpcoesController : UIViewController, UITableViewDelegate, UITableViewDataSource {

    var arrayDados = Array<String>()
    var tituloPopup : String!
    var senderText: UITextField!
    
    var delegate : LOPopupDelegate!
    var selectedOption : String!
    
    @IBOutlet weak var tituloDados: UILabel!
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            self.tableView.delegate = self
            self.tableView.dataSource = self
        }
    }
    @IBOutlet weak var gradientView : UIView! {
        didSet {
            self.gradientView.layoutIfNeeded()
            self.gradientView.autoresizesSubviews = true
            self.gradientView.layer.masksToBounds = true
            
            let gradient : CAGradientLayer = CAGradientLayer()
            gradient.frame = self.gradientView.bounds
            gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
            gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
            gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]

            self.gradientView.layer.addSublayer(gradient)
        }
    }
    
    @IBAction func confirmarSelecao(_ sender: UIButton) {
        if selectedOption != nil {
            self.delegate.didSelect(string: selectedOption, sender: senderText)
            self.dismiss(animated: true, completion: nil)
        } else {
            self.alertSimple(title: "Atenção", message: "Selecione uma opção ou clique em cancelar para sair.")
        }
    }
        
    @IBAction func cancelarSelecao(_ sender: UIButton) {
        self.dismiss(animated: true, completion: nil)
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = UIColor(red: 78.0/255.0, green: 89.0/255.0, blue: 107.0/255.0, alpha: 0.9)
        self.tituloDados.text = tituloPopup
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayDados.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell : LOPopupItensCell = tableView.dequeueReusableCell(withIdentifier: "celulaPadrao") as! LOPopupItensCell
        
        cell.titulo.text = arrayDados[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedOption = arrayDados[indexPath.row]
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
