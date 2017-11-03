//
//  LOCabecalhoRepassesCell.swift
//  Loterias
//
//  Created by Jenkins on 22/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOCabecalhoRepassesCell: UITableViewCell, UIPickerViewDelegate, UIPickerViewDataSource {

    @IBOutlet var pickerView : UIPickerView!
    
    var numbers = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
    var repassometroObj : LORepassometro?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        pickerView.delegate = self
        pickerView.dataSource = self
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func configuraPickerview () {
        
    }

    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return 10
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 8
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return numbers[row]
    }
    
    /*
    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        
        let pickerLabel = UILabel()
        
        if pickerView.selectedRow(inComponent: component) == row {
            pickerLabel.attributedText = NSAttributedString(string: "teste", attributes: [NSFontAttributeName:UIFont(name: "AvenirNext-Bold", size: 26.0)!, NSForegroundColorAttributeName: UIColor.yellow])
        } else {
            pickerLabel.attributedText = NSAttributedString(string: "teste1", attributes: [NSFontAttributeName:UIFont(name: "Avenir Next", size: 22.0)!, NSForegroundColorAttributeName: UIColor.white])
        }
        
        pickerLabel.textAlignment = .left
        return pickerLabel
    }
     */
    
    func pickerView(_ pickerView: UIPickerView, attributedTitleForRow row: Int, forComponent component: Int) -> NSAttributedString? {
        let str = numbers[row]
        return NSAttributedString(string: str, attributes: [NSAttributedStringKey.foregroundColor:UIColor.white])
    }
}
