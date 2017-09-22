//
//  LOCryptoService.swift
//  Loterias
//
//  Created by Jenkins on 20/09/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit
import CryptoSwift
import Alamofire

class LOCryptoService: NSObject {
    let salt: Array<UInt8> = Array("nacllcan".utf8)
    let password: Array<UInt8> = Array("s33krit".utf8)

    let iv: Array<UInt8> = AES.randomIV(AES.blockSize)
    
    func crypfyBody () {
        do {
            let key = try PKCS5.PBKDF2(password: password, salt: salt, iterations: 1, variant: .sha256).calculate()
            
            let aes = try AES(key: key, iv: iv) // aes128
            let ciphertext = try aes.encrypt(Array("AES-CBC".utf8))
            print(ciphertext.toHexString())
            

            let cipherFinal = ciphertext.toHexString() + "!#!#!" + key.toHexString() + "!#!#!" + iv.toHexString()
            
            print(cipherFinal)
            print(cipherFinal.toBase64())
        } catch {
            print(error)
        }
    }
    
    func crypfyURL () {
        do {
            let urlTest = URL(string: "https://simlo.des.caixa/silce-servico-rest/rest/v1/" + "ufs/".toBase64())
            let urlTestMun = URL(string: "https://simlo.des.caixa/silce-servico-rest/rest/v1/" + "municipios/".toBase64())

            let key = try PKCS5.PBKDF2(password: password, salt: salt, iterations: 1, variant: .sha256).calculate()
            
            let aes = try AES(key: key, iv: iv) // aes128
            
            var teste = Array<String.UTF8View>()
            teste.append("AES-CBC".utf8)
            teste.append("".utf8)
            
            var ciphertext = try aes.encrypt(Array("AES-CBC".utf8))
            
            //let encrypted = try Blowfish(key: key.toHexString(), iv: iv.toHexString(), blockMode: .CBC, padding: .pkcs5).encrypt(Array("idUF==23".utf8))

            print(ciphertext.toHexString())
            
            
            //let cipherFinal = encrypted.description + "!#!#!" + key.toHexString() + "!#!#!" + iv.toHexString()
            
            //print(cipherFinal)
            //print(cipherFinal.toBase64())
            
            Alamofire.request(urlTest!, method: .get, parameters: [:], encoding: JSONEncoding.default)
                .responseJSON { response in
                    if response != nil {
                        
                    }
            }
        } catch {
            print(error)
        }
    }

//    var cipher = forge.cipher.createCipher('AES-CBC', key);
//    cipher.start({iv: iv});
//    cipher.update(forge.util.createBuffer(jsonString));
//    cipher.finish();
//    enc2 = cipher.output;
//    
//    request.data = enc2.data + '!#!#!' + key + '!#!#!' + iv;
//    request.data = btoa(request.data);
    
}
