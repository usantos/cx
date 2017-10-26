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
    var arrayTableCell = Array<UITableViewCell>()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.estimatedRowHeight = 150
        
        maskedDelegate = MaskedTextFieldDelegate(format: "[0000] [0000] [0000] [0000]")
        maskedDelegate.listener = self
        
        numeroCartao.delegate = maskedDelegate
    
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.titleView = self.titleViewCustom
        
        self.gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        self.gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        self.gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        self.gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        
        self.navigationController?.navigationBar.tintColor = .white
        
        self.navigationController?.navigationBar.setBackgroundImage(self.image(fromLayer: self.gradient), for: .default)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
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
            
        if !arrayCells.contains(cell) {
            arrayCells.append(cell)
        }
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath) as? LOBandeiraCell
        
        cell?.setSelected(isSelected: (cell?.isSelected)!)
        
        for cellLocal in arrayCells {
            if cell != cellLocal {
                cellLocal.setSelected(isSelected: false)
                cellLocal.isSelected = false
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
