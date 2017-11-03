//
//  LOLocalizacaoCell.swift
//  Loterias
//
//  Created by Jenkins on 11/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import TextFieldEffects

class LOLocalizacaoCell: UITableViewCell, UITextFieldDelegate, LOPopupDelegate {
    
    @IBOutlet weak var uf: HoshiTextField! {
        didSet {
            self.uf.delegate = self
            self.uf.addRightImg(image: #imageLiteral(resourceName: "icon-combo"), rect: CGRect(x: 0, y: 0, width: 12, height: 8))
        }
    }
    
    @IBOutlet weak var cidade: HoshiTextField! {
        didSet {
            self.cidade.delegate = self
            self.cidade.addRightImg(image: #imageLiteral(resourceName: "icon-combo"), rect: CGRect(x: 0, y: 0, width: 12, height: 8))
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    var listaUfs : [LOUnidadeFederativa]?

    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        if textField == uf {
            
            let loadingView = self.viewController()?.addLoadingView(alpha: 0.8)
            LORegisterBusinessService().getUfs(parameters: [:], completionHandler: { (error, listaCidades) in
                loadingView?.removeFromSuperview()
                if error != nil {
                    self.viewController()?.alertSimple(title: "Atenção", message: error!)
                } else {
                    let storyboard = UIStoryboard(name: "Login", bundle: nil)
                    let controller = storyboard.instantiateViewController(withIdentifier: "LOPopupSelecionarOpcoesController") as? LOPopupSelecionarOpcoesController
                    var arrayUfs = Array<String>()
                    for objCidade in listaCidades! {
                        arrayUfs.append(objCidade.sigla!)
                    }
                    
                    self.listaUfs = listaCidades!
                    
                    controller?.arrayDados = arrayUfs
                    controller?.tituloPopup = "UF"
                    controller?.modalPresentationStyle = .overFullScreen
                    controller?.delegate = self
                    controller?.senderText = textField
                    
                    self.viewController()?.present(controller!, animated: true, completion: nil)
                }
            })
            return false
        } else if textField == cidade {
            if uf.text != "" {
                let ufSelecionada = listaUfs?.filter { $0.sigla == self.uf.text }
                let loadingView = self.addLoadingView(alpha: 0.8)
                
                LORegisterBusinessService().getMunicipios(idUf: ufSelecionada![0].id!, completionHandler: { (error, listaMunicipios) in
                    loadingView.removeFromSuperview()
                    if error != nil {
                        self.alertSimple(title: "Atenção", message: error!)
                    } else {
                        let storyboard = UIStoryboard(name: "Login", bundle: nil)
                        let controller = storyboard.instantiateViewController(withIdentifier: "LOPopupSelecionarOpcoesController") as? LOPopupSelecionarOpcoesController
                        var arrayMunicipios = Array<String>()
                        
                        for municipio in listaMunicipios! {
                            arrayMunicipios.append(municipio.nome)
                        }
                        
                        controller?.arrayDados = arrayMunicipios
                        controller?.tituloPopup = "Município"
                        controller?.modalPresentationStyle = .overFullScreen
                        controller?.delegate = self
                        controller?.senderText = textField
                        
                        self.viewController()?.present(controller!, animated: true, completion: nil)
                    }
                })
            } else {
                self.alertSimple(title: "Ops!", message: "Selecione primeiro a UF, assim nossa precisão de municípios será incrível.")
            }
            return false
        }
        return true
    }
    
    func didSelect(string: String, sender: UITextField) {
        sender.text = string
    }
}
