//
//  LOCarrinhoApostasController.swift
//  Loterias
//
//  Created by Jenkins on 20/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOCarrinhoApostasController: UIViewController, LOItemCellDelegate, UITableViewDelegate, UITableViewDataSource {

    var cellsArray : Array<UITableViewCell>?
    let gradient : CAGradientLayer = CAGradientLayer()
    @IBOutlet var titleViewCustom : UIView!
    @IBOutlet var navBarCustom : UIView!
    @IBOutlet var tableView : UITableView!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.estimatedRowHeight = 150
        
        populaArrayDeCelulas()
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
        return cellsArray != nil ? (cellsArray?.count)! : 0
    }

    @IBAction func backButtonAction () {
        self.navigationController?.popViewController(animated: true)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return cellsArray![indexPath.row]
    }
    
    func populaArrayDeCelulas () {
        cellsArray = Array<UITableViewCell>()
        
        cellsArray?.append(self.tableView.dequeueReusableCell(withIdentifier: "cabecalhoCell")!)
        
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "itemCell")! as! LOItemCell
        cell.numeros.text = "1 2 3 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24"
        cell.delegate = self
        
        cellsArray?.append(cell)
        cellsArray?.append(self.tableView.dequeueReusableCell(withIdentifier: "itemCell")!)
        cellsArray?.append(self.tableView.dequeueReusableCell(withIdentifier: "itemCell")!)
        cellsArray?.append(self.tableView.dequeueReusableCell(withIdentifier: "rodapeCell")!)

        self.tableView.reloadData()
    }
    
    func swipeableTableViewCellDidRecognizeSwipe(cell : LOItemCell) {
        
    }
    func swipeableTableViewCellDidTapLeftButton(cell : LOItemCell) {
        
    }
    func swipeableTableViewCellDidTapRightButton(cell : LOItemCell) {
        
    }
    
    @IBAction func fazerNovaAposta () {
        for vc in (self.navigationController?.viewControllers ?? []) {
            if vc is LOSimularApostasController {
                self.navigationController?.navigationBar.shadowImage = UIImage()
                self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
                _ = self.navigationController?.popToViewController(vc, animated: true)
                break
            }
        }
    }
    
    @IBAction func voltaParaCarrinhoDeApostas(segue:UIStoryboardSegue) { }

    // MARK: - Navigation

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
