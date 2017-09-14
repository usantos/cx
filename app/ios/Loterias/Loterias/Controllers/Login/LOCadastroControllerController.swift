//
//  LOCadastroControllerController.swift
//  Loterias
//
//  Created by Jenkins on 31/08/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

class LOCadastroControllerController: UITableViewController, UITextFieldDelegate {

    @IBOutlet weak var btnMasculino : UIButton!
    @IBOutlet weak var btnFeminino : UIButton!
    var isMasculino = true
    
    @IBOutlet weak var cpf: HoshiTextField!
    @IBOutlet weak var nome: HoshiTextField!
    @IBOutlet weak var nomeDaMae: HoshiTextField!
    @IBOutlet weak var cep: HoshiTextField!
    
    @IBOutlet weak var uf: HoshiTextField! {
        didSet {
            self.uf.delegate = self
            self.uf.addRightImg(image: #imageLiteral(resourceName: "icon-combo"), rect: CGRect(x: 0, y: 0, width: 12, height: 8))
        }
    }
    @IBOutlet weak var municipio: HoshiTextField! {
        didSet {
            self.municipio.delegate = self
            self.municipio.addRightImg(image: #imageLiteral(resourceName: "icon-combo"), rect: CGRect(x: 0, y: 0, width: 12, height: 8))
        }
    }
    
    @IBOutlet weak var dataNascimento: HoshiTextField!
    @IBOutlet weak var email: HoshiTextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.addRightIconOnNavigation(image: #imageLiteral(resourceName: "icon-loteria"))
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        switch textField {
            
        case self.uf:
            self.performSegue(withIdentifier: "mandarParaPopup", sender: self)
            return false
        case self.municipio:
            self.performSegue(withIdentifier: "mandarParaPopup", sender: self)
            return false
        default:
            break
        }
        return true
    }

    @IBAction func ativaMasculino () {
        self.btnMasculino.backgroundColor = UIColor(red: 53.0/255.0, green: 168.0/255.0, blue: 152.0/255.0, alpha: 1.0)
        self.btnMasculino.setTitleColor(UIColor.white, for: .normal)
        
        self.btnFeminino.backgroundColor = UIColor(red: 239.0/255.0, green: 245.0/255.0, blue: 246.0/255.0, alpha: 1.0)
        self.btnFeminino.setTitleColor(UIColor(red: 53.0/255.0, green: 168.0/255.0, blue: 152.0/255.0, alpha: 1.0), for: .normal)
        self.isMasculino = true
    }
    
    @IBAction func ativaFeminino () {
        self.btnFeminino.backgroundColor = UIColor(red: 53.0/255.0, green: 168.0/255.0, blue: 152.0/255.0, alpha: 1.0)
        self.btnFeminino.setTitleColor(UIColor.white, for: .normal)
        
        self.btnMasculino.backgroundColor = UIColor(red: 239.0/255.0, green: 245.0/255.0, blue: 246.0/255.0, alpha: 1.0)
        self.btnMasculino.setTitleColor(UIColor(red: 53.0/255.0, green: 168.0/255.0, blue: 152.0/255.0, alpha: 1.0), for: .normal)
        self.isMasculino = false
    }
    
    @IBAction func mandarParaOPassoDois(_ sender: UIButton) {
        
    }
    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 12
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

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
