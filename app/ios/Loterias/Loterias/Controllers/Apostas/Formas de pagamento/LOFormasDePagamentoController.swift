//
//  LOFormasDePagamentoController.swift
//  Loterias
//
//  Created by Jenkins on 22/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

protocol LOFormasDePagamentoDelegate {
    func excluirFormaDePagamento(_ sender : UITableViewCell)
}

class LOFormasDePagamentoController: UIViewController, LOFormasDePagamentoDelegate, UITableViewDataSource, UITableViewDelegate {
    
    let gradient : CAGradientLayer = CAGradientLayer()
    @IBOutlet var titleViewCustom : UIView!
    var arrayCells = Array<UITableViewCell>()
    var listaFormaPagamento = Array<Int>()
    var arrayFormaPagamentoCells = Array<LOFormaDePagamentoCell>()
    
    @IBOutlet var navBarCustom : UIView!
    @IBOutlet var tableView : UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.estimatedRowHeight = 150
        
        self.navigationItem.titleView = titleViewCustom
        
        listaFormaPagamento.append(1)
        listaFormaPagamento.append(2)

        
        populateArrayCells()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.titleView = self.titleViewCustom
        
        self.navigationController?.isNavigationBarHidden = true
        
        self.gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        self.gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        self.gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        self.gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        
        self.navBarCustom.layer.insertSublayer(self.gradient, at: 0)
    }
    
    @IBAction func backButtonAction () {
        self.navigationController?.popViewController(animated: true)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Table view data source

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayCells.count
    }
    
    @IBAction func finalizarAposta () {
        self.performSegue(withIdentifier: "finalizarAposta", sender: self)
    }
    
    func populateArrayCells () {
        let cabecalhoCell = tableView.dequeueReusableCell(withIdentifier: "cabecalhoCell")
        arrayCells.append(cabecalhoCell!)
        
        for formaPagamento in listaFormaPagamento {
            let itemCell = tableView.dequeueReusableCell(withIdentifier: "itemCell") as? LOFormaDePagamentoCell
            itemCell?.bandeiraCartao.image = #imageLiteral(resourceName: "mastercard")
            itemCell?.tituloCartao.text = "Mastercard"
            itemCell?.numero.text = aplicaMascaraNumeroCartao(numero: "4444444444441234")
            itemCell?.delegate = self
            
            arrayCells.append(itemCell!)
            arrayFormaPagamentoCells.append(itemCell!)
        }
        
        let rodapeCell = tableView.dequeueReusableCell(withIdentifier: "rodapeCell")
        arrayCells.append(rodapeCell!)
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if !(tableView.cellForRow(at: indexPath) == self.arrayCells.first || tableView.cellForRow(at: indexPath) == self.arrayCells.last) {
            let cell = tableView.cellForRow(at: indexPath) as? LOFormaDePagamentoCell
            
            if (cell?.isSelecionado)! {
                cell?.isSelecionado = false
            } else {
                cell?.isSelecionado = true
            }
            
            cell?.setSelected(isSelected: (cell?.isSelecionado)!)
            
            for cellLocal in arrayFormaPagamentoCells {
                if cell != cellLocal {
                    cellLocal.setSelected(isSelected: false)
                    cellLocal.isSelecionado = false
                }
            }
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return arrayCells[indexPath.row]
    }
    
    func aplicaMascaraNumeroCartao (numero: String) -> String {
        var numeroComMask = "**** **** **** "
        
        let indexEndOfText = numero.index(numero.startIndex, offsetBy: 12)
        let substring = numero[indexEndOfText...]
        numeroComMask.append(substring.description)
        
        return numeroComMask
    }
    
    func excluirFormaDePagamento(_ sender: UITableViewCell) {
        self.alertConfirm(title: "Atenção", message: "Deseja realmente excluir essa forma de pagamento?", titleCancelAction: "Não!", titleOkAction: "Sim") {
            
            let indexPath = self.tableView.indexPath(for: sender)!
            
            self.arrayCells.remove(at: indexPath.row)
            self.arrayFormaPagamentoCells.remove(at: indexPath.row - 1)

            self.tableView.deleteRows(at: [indexPath], with: .automatic)
        }
    }
    
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        if tableView.cellForRow(at: indexPath) == self.arrayCells.first || tableView.cellForRow(at: indexPath) == self.arrayCells.last {
            return false
        }
        return true
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            arrayCells.remove(at: indexPath.row)
            arrayFormaPagamentoCells.remove(at: indexPath.row - 1)
            self.tableView.deleteRows(at: [indexPath], with: .automatic)
        }
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
