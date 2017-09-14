//
//  LOSimularApostasController.swift
//  Loterias
//
//  Created by Jenkins on 13/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOSimularApostasController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    @IBOutlet weak var simularApostaBaixo : LOSimularApostaBaixo!
    
    
    
    
    @IBOutlet weak var selecionaNumeros: UIButton!
    @IBOutlet weak var surpresinha: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    
    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            self.collectionView.delegate = self
            self.collectionView.dataSource = self
        }
    }
    
    @IBOutlet weak var collectionViewSurpresinha : UICollectionView! {
        didSet {
            self.collectionViewSurpresinha.delegate = self
            self.collectionViewSurpresinha.dataSource = self
        }
    }
    @IBOutlet weak var imgPassoAPasso: UIImageView!
    @IBOutlet weak var verticalLayoutConstraint : NSLayoutConstraint!
    
    @IBOutlet var viewSurpresinha : UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //self.verticalLayoutConstraint.constant = self.collectionView.contentSize.height
//        print(collectionViewSurpresinha.contentInset)
//        //
//        resizeCollectionViewSize()

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func apresentaNumeros(_ sender: Any) {
        //self.viewSurpresinha.isHidden = true
        self.surpresinha.setImage(#imageLiteral(resourceName: "icon-surpresinha"), for: .normal)
        self.selecionaNumeros.setImage(#imageLiteral(resourceName: "icon-seleciona"), for: .normal)

    }
    
    @IBAction func apresentaSurpresinha(_ sender: Any) {
        //self.viewSurpresinha.isHidden = false
        self.surpresinha.setImage(#imageLiteral(resourceName: "icon-surpresinha2"), for: .normal)
        self.selecionaNumeros.setImage(#imageLiteral(resourceName: "icon-seleciona2"), for: .normal)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 30
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "numeroCell", for: indexPath)
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cel : LONumeroApostaCell = collectionView.dequeueReusableCell(withReuseIdentifier: "numeroCell", for: indexPath) as! LONumeroApostaCell
    
        cel.numero.backgroundColor = .black
        cel.numero.textColor = .white
        
        cel.layoutIfNeeded()
    }
    
    func resizeCollectionViewSize() {
        self.verticalLayoutConstraint.constant = self.collectionViewSurpresinha.collectionViewLayout.collectionViewContentSize.height
        self.scrollView.contentSize.height = self.scrollView.contentSize.height + self.verticalLayoutConstraint.constant
        
        self.view.layoutIfNeeded()
    }
    
//    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
//        
//        //Where elements_count is the count of all your items in that
//        //Collection view...
//        let cellCount = CGFloat(30)
//        
//        //If the cell count is zero, there is no point in calculating anything.
//        if cellCount > 0 {
//            let flowLayout = collectionViewLayout as! UICollectionViewFlowLayout
//            let cellWidth = flowLayout.itemSize.width + flowLayout.minimumInteritemSpacing
//            
//            //20.00 was just extra spacing I wanted to add to my cell.
//            let totalCellWidth = cellWidth*cellCount + 5 * (cellCount - 1)
//            let contentWidth = collectionView.frame.size.width - collectionView.contentInset.left - collectionView.contentInset.right
//            
//            if (totalCellWidth < contentWidth) {
//                //If the number of cells that exists take up less room than the
//                //collection view width... then there is an actual point to centering them.
//                
//                //Calculate the right amount of padding to center the cells.
//                let padding = (contentWidth - totalCellWidth) / 2.0
//                return UIEdgeInsetsMake(0, padding, 0, padding)
//            } else {
//                //Pretty much if the number of cells that exist take up
//                //more room than the actual collectionView width, there is no
//                // point in trying to center them. So we leave the default behavior.
//                return UIEdgeInsetsMake(0, 50, 0, 50)
//            }
//        }
//        
//        return UIEdgeInsets.zero
//    }

    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
