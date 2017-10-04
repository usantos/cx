//
//  LOAdicionarCartaoController.swift
//  Loterias
//
//  Created by Jenkins on 22/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects
import InputMask

class LOAdicionarCartaoController: UITableViewController, UICollectionViewDelegate, UICollectionViewDataSource, MaskedTextFieldDelegateListener {
    
    var maskedDelegate: MaskedTextFieldDelegate!
    
    @IBOutlet weak var textoExplicativoCabecalho: UILabel!
    @IBOutlet weak var collectionBandeiras : UICollectionView! {
        didSet {
            collectionBandeiras.delegate = self
            collectionBandeiras.dataSource = self
        }
    }
    @IBOutlet weak var numeroCartao: HoshiTextField! {
        didSet {
            numeroCartao.delegate = self
        }
    }
    @IBOutlet weak var nomeCartao: HoshiTextField!
    @IBOutlet weak var cvv: HoshiTextField!
    @IBOutlet weak var mesValidade: HoshiTextField!
    @IBOutlet weak var anoValidade: HoshiTextField!
    
    var salvarCartao: Bool = false
    
    @IBOutlet weak var totalCompra: UILabel!
    @IBOutlet var titleViewCustom : UIView!
    
    let gradient : CAGradientLayer = CAGradientLayer()
    var arrayCells = Array<LOBandeiraCell>()
    override func viewDidLoad() {
        super.viewDidLoad()
        
        maskedDelegate = MaskedTextFieldDelegate(format: "[0000] [0000] [0000] [0000]")
        maskedDelegate.listener = self
        
        numeroCartao.delegate = maskedDelegate
        
        self.navigationItem.titleView = titleViewCustom
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillDisappear(_ animated: Bool) {

    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 8
    }

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 15
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "bandeiraCell", for: indexPath) as! LOBandeiraCell
        
        //cell.setSelected(isSelected: cell.isSelecionado)
        
        if !arrayCells.contains(cell) {
            arrayCells.append(cell)
        }
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath) as? LOBandeiraCell
        
        if (cell?.isSelecionado)! {
            cell?.isSelecionado = false
        } else {
            cell?.isSelecionado = true
        }
        
        cell?.setSelected(isSelected: (cell?.isSelecionado)!)
        
        for cellLocal in arrayCells {
            if cell != cellLocal {
                cellLocal.setSelected(isSelected: false)
                cellLocal.isSelecionado = false
            }
        }
    }
    
    @IBAction func trocaCheckSalvarCartao (sender: UIButton) {
        if !salvarCartao {
            sender.setImage(#imageLiteral(resourceName: "icon-check"), for: .normal)
            salvarCartao = true
        } else {
            sender.setImage(#imageLiteral(resourceName: "icon-uncheck"), for: .normal)
            salvarCartao = false
        }
    }
    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

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

}
