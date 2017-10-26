//
//  LOCadastroController.swift
//  Loterias
//
//  Created by Jenkins on 31/08/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects
import InputMask

class LOCadastroController: UITableViewController, UITextFieldDelegate, MaskedTextFieldDelegateListener, LOPopupDelegate {
    
    var maskedDelegateCpf: MaskedTextFieldDelegate!
    var maskedDelegateCep: MaskedTextFieldDelegate!
    var maskedDelegateDtNascimento: MaskedTextFieldDelegate!

    @IBOutlet weak var btnMasculino : UIButton!
    @IBOutlet weak var btnFeminino : UIButton!
    var isMasculino = true
    
    @IBOutlet weak var cpf: HoshiTextField! {
        didSet {
            cpf.delegate = self
        }
    }
    @IBOutlet weak var nome: HoshiTextField! {
        didSet {
            nome.delegate = self
        }
    }
    
    @IBOutlet weak var nomeDaMae: HoshiTextField! {
        didSet {
            nomeDaMae.delegate = self
        }
    }
    
    @IBOutlet weak var cep: HoshiTextField! {
        didSet {
            cep.delegate = self
        }
    }
    
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
    
    @IBOutlet weak var dataNascimento: HoshiTextField! {
        didSet {
            dataNascimento.delegate = self
        }
    }
    
    @IBOutlet weak var email: HoshiTextField! {
        didSet {
            email.delegate = self
        }
    }
    let gradient : CAGradientLayer = CAGradientLayer()
    var arrayTextFields : Array<HoshiTextField>!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.gradient.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 64)
        self.gradient.startPoint = CGPoint(x: 0.0, y: 0.5)
        self.gradient.endPoint = CGPoint(x: 1.0, y: 0.5)
        self.gradient.colors = [self.UIColorFromHex(0x54bbab, alpha: 1.0).cgColor, self.UIColorFromHex(0x005ca9, alpha: 1.0).cgColor]
        self.gradient.name = "gradientView"
        
        self.navigationController?.navigationBar.tintColor = .white
        self.navigationController?.isNavigationBarHidden = false
        
        self.navigationController?.navigationBar.setBackgroundImage(self.image(fromLayer: self.gradient), for: .default)
        
        maskedDelegateCpf = MaskedTextFieldDelegate(format: "[000].[000].[000]-[00]")
        maskedDelegateCpf.listener = self
        
        maskedDelegateCep = MaskedTextFieldDelegate(format: "[00000]-[000]")
        maskedDelegateCep.listener = self
        
        maskedDelegateDtNascimento = MaskedTextFieldDelegate(format: "[00]/[00]/[0000]")
        maskedDelegateDtNascimento.listener = self
        
        cep.delegate = maskedDelegateCep
        cpf.delegate = maskedDelegateCpf
        dataNascimento.delegate = maskedDelegateDtNascimento
        
        self.addRightIconOnNavigation(image: #imageLiteral(resourceName: "icon-loteria"))
        
        self.arrayTextFields = [cpf, nome, nomeDaMae, cep, dataNascimento, email]
        
        self.navigationController?.navigationBar.backIndicatorImage = #imageLiteral(resourceName: "ic-back-button")
        self.navigationController?.navigationBar.backIndicatorTransitionMaskImage = #imageLiteral(resourceName: "ic-back-button")
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        if self.isMovingFromParentViewController {
            self.navigationController?.navigationBar.shadowImage = UIImage()
            self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        validaCamposDeTexto(textField)
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if textField == nomeDaMae || textField == email {
            guard let text = textField.text else { return true }
            let newLength = text.characters.count + string.characters.count - range.length
            return newLength <= 80
        }
        return true
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        switch textField {
            
        case self.uf:
            let loadingView = self.addLoadingView(alpha: 0.8)
            LORegisterBusinessService().getUfs(parameters: [:], completionHandler: { (error, listaCidades) in
                loadingView.removeFromSuperview()
                if error != nil {
                    self.alertSimple(title: "Atenção", message: error!)
                } else {
                    let storyboard = UIStoryboard(name: "Login", bundle: nil)
                    let controller = storyboard.instantiateViewController(withIdentifier: "LOPopupSelecionarOpcoesController") as? LOPopupSelecionarOpcoesController
                    var arrayUfs = Array<String>()
                    for objCidade in listaCidades! {
                        arrayUfs.append(objCidade.sigla!)
                    }
                    controller?.arrayDados = arrayUfs
                    controller?.tituloPopup = "UF"
                    controller?.modalPresentationStyle = .overFullScreen
                    controller?.delegate = self
                    controller?.senderText = textField
                    
                    self.present(controller!, animated: true, completion: nil)
                }
            })
            return false
        case self.municipio:
            let storyboard = UIStoryboard(name: "Login", bundle: nil)
            let controller = storyboard.instantiateViewController(withIdentifier: "LOPopupSelecionarOpcoesController") as? LOPopupSelecionarOpcoesController
            var arrayUfs = Array<String>()
            
            //RETIRAR MOCK
            arrayUfs.append("Brasilia")
            arrayUfs.append("Taguatinga")
            arrayUfs.append("Asa norte")
            
            controller?.arrayDados = arrayUfs
            controller?.tituloPopup = "Município"
            controller?.modalPresentationStyle = .overFullScreen
            controller?.delegate = self
            controller?.senderText = textField
            
            self.present(controller!, animated: true, completion: nil)
            return false
        default:
            break
        }
        return true
    }
    
    func validaCamposDeTexto(_ textField: UITextField) -> Bool {
        for textfieldLocal in self.arrayTextFields {
            if (textfieldLocal.text?.characters.count)! == 0 {
                textfieldLocal.rightView = nil
                return false
            } else {
                textfieldLocal.addRightImg(image: #imageLiteral(resourceName: "icon-input-ok"), rect: CGRect(x: 0, y: 0, width: 12, height: 10))
            }
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
    
    @IBAction func dismiss () {
        self.navigationController?.popViewController(animated: true)
    }
    
    func didSelect(string: String, sender: UITextField) {
        sender.text = string
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
