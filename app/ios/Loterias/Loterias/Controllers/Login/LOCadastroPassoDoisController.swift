//
//  LOCadastroPassoDoisController.swift
//  Loterias
//
//  Created by Jenkins on 01/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

protocol LOCadastroPassoDoisDelegate: class {
    func trocaTipoDaBusca(opcao : LOOpcoesDeBuscaEnum)
    func finalizarCadastro()
}

class LOCadastroPassoDoisController: UITableViewController, LOCadastroPassoDoisDelegate {
    
    var arrayDeCelulas = Array<UITableViewCell>()
    let gradient : CAGradientLayer = CAGradientLayer()

    override func viewDidLoad() {
        super.viewDidLoad()

        self.configurarTelaParaBuscarPorLocalizacao()
        
        self.tableView.estimatedRowHeight = 140
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.tableFooterView = UITableViewHeaderFooterView()
        
        self.addRightIconOnNavigation(image: #imageLiteral(resourceName: "icon-loteria"))
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        self.gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        self.gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        self.gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        self.gradient.name = "gradientView"
        
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.isNavigationBarHidden = false
        
        self.navigationController?.navigationBar.setBackgroundImage(self.image(fromLayer: self.gradient), for: .default)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if arrayDeCelulas.count > 0 {
            return arrayDeCelulas.count
        }
        return 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return arrayDeCelulas[indexPath.row]
    }

    func configurarTelaParaBuscarPorLocalizacao () {
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "topoCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "cabecalhoCell")!)
        
        let opcoesCell : LOOptionsCell = tableView.dequeueReusableCell(withIdentifier: "opcoesCell")! as! LOOptionsCell
        opcoesCell.delegate = self
        
        self.arrayDeCelulas.append(opcoesCell)
        arrayDeCelulas.removeSubrange(3..<arrayDeCelulas.count)
        
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "locCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "bairroCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "termoCell")!)
        
        let finalizarCell : LOFinalizarCell = tableView.dequeueReusableCell(withIdentifier: "finalizarCell")! as! LOFinalizarCell
        finalizarCell.delegate = self
        self.arrayDeCelulas.append(finalizarCell)
        
        self.tableView.reloadData()
    }
    
    func configurarTelaParaBuscarPorCodigoLoterica () {
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "topoCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "cabecalhoCell")!)
        
        let opcoesCell : LOOptionsCell = tableView.dequeueReusableCell(withIdentifier: "opcoesCell")! as! LOOptionsCell
        opcoesCell.delegate = self
        
        self.arrayDeCelulas.append(opcoesCell)
        arrayDeCelulas.removeSubrange(3..<arrayDeCelulas.count)

        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "codigoCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "termoCell")!)
        
        let finalizarCell : LOFinalizarCell = tableView.dequeueReusableCell(withIdentifier: "finalizarCell")! as! LOFinalizarCell
        finalizarCell.delegate = self
        self.arrayDeCelulas.append(finalizarCell)

        self.tableView.reloadData()
    }
    
    func configurarTelaParaBuscarPorCep () {
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "topoCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "cabecalhoCell")!)
        
        let opcoesCell : LOOptionsCell = tableView.dequeueReusableCell(withIdentifier: "opcoesCell")! as! LOOptionsCell
        opcoesCell.delegate = self
        
        self.arrayDeCelulas.append(opcoesCell)
        arrayDeCelulas.removeSubrange(3..<arrayDeCelulas.count)
        
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "cepCell")!)
        
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "lotericaCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "lotericaCell")!)
        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "lotericaCell")!)

        self.arrayDeCelulas.append(tableView.dequeueReusableCell(withIdentifier: "termoCell")!)
        
        let finalizarCell : LOFinalizarCell = tableView.dequeueReusableCell(withIdentifier: "finalizarCell")! as! LOFinalizarCell
        finalizarCell.delegate = self
        self.arrayDeCelulas.append(finalizarCell)
        
        self.tableView.reloadData()
    }

    func trocaTipoDaBusca(opcao: LOOpcoesDeBuscaEnum) {
        switch opcao {
        case .Localizacao:
            configurarTelaParaBuscarPorLocalizacao()
            break
        case .Cep:
            configurarTelaParaBuscarPorCep()
            break
        case .CodigoLoterica:
            configurarTelaParaBuscarPorCodigoLoterica()
            break
        }
    }
    
    func finalizarCadastro() {
        self.performSegue(withIdentifier: "finalizarCadastro", sender: self)

//        if termoSelecionado() {
//            LOTermoBusinessService().registraAceiteTermo(parameters: [:], completionHandler: { (sucesso) in
//                if sucesso! {
//                    self.performSegue(withIdentifier: "finalizarCadastro", sender: self)
//                } else {
//                    self.alertSimple(title: "Ops!", message: "")
//                }
//            })
//        } else {
//            self.alertSimple(title: "Atenção", message: "Para continuar é necessário o aceite do termo.")
//        }
    }
    
    func termoSelecionado() -> Bool {
        for cell in arrayDeCelulas {
            if cell.reuseIdentifier == "termoCell" {
                return ((cell as? LOTermoCell)?.selecionado)!
            }
        }
        return false
    }
    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
