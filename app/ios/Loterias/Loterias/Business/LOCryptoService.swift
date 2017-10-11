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
            //let aes = try AES(key: key, iv: iv) // aes128

            var encryptor = try AES(key: key, iv: iv).makeEncryptor()

            var ciphertext = Array<UInt8>()
            //ciphertext += try encryptor.update(withBytes: Array("AES-CBC".utf8))
            ciphertext += try encryptor.update(withBytes: Array("{\"idUF\" : 17}".utf8))

            ciphertext += try encryptor.finish()

            let cipherFinal = ciphertext.toHexString() + "!#!#!" + key.toHexString() + "!#!#!" + iv.toHexString()
            
            print(cipherFinal.toBase64())
        } catch {
            print(error)
        }
    }
    
    func crypfyURL () {
        do {
             let urlTest = URL(string: "http://10.130.9.22:81/silce-servico-rest/rest/v1/" + "ufs".toBase64())
            let urlTestMun = URL(string: "https://simlo.des.caixa/silce-servico-rest/rest/v1/" + "municipios".toBase64())

            let key = try PKCS5.PBKDF2(password: password, salt: salt, iterations: 1, variant: .sha256).calculate()
            
            let aes = try AES(key: key, iv: iv) // aes128
            
            var teste = Array<String.UTF8View>()
            teste.append("AES-CBC".utf8)
            teste.append("".utf8)
            
            var ciphertext = try aes.encrypt(Array("AES-CBC".utf8))
            
            //let encrypted = try Blowfish(key: key.toHexString(), iv: iv.toHexString(), blockMode: .CBC, padding: .pkcs5).encrypt(Array("idUF==23".utf8))

            //print(ciphertext.toHexString())
            
            
            //let cipherFinal = encrypted.description + "!#!#!" + key.toHexString() + "!#!#!" + iv.toHexString()
            
            //print(cipherFinal)
            //print(cipherFinal.toBase64())
            getUfs(urlTest: urlTest!)
        } catch {
            print(error)
        }
    }
    
    func cripfyUrlWithQueryParam () {
        var urlTestMun : URL?
        do {
            let key = try PKCS5.PBKDF2(password: password, salt: salt, iterations: 1, variant: .sha256).calculate()
            
            let aes = try AES(key: key, iv: iv) // aes128
            
            let queryParam = "idUF==17"
            
            var encryptor = try AES(key: key, iv: iv).makeEncryptor()
            var ciphertext = Array<UInt8>()
            
            ciphertext += try encryptor.update(withBytes: Array("AES-CBC".utf8))
            ciphertext += try encryptor.update(withBytes: key)
            ciphertext += try encryptor.update(withBytes: Array(queryParam.utf8))

            ciphertext += try encryptor.finish()

            let cipherFinal = ciphertext.toHexString() + "!#!#!" + key.toHexString() + "!#!#!" + iv.toHexString()

            let urlBs64 = "https://simlo.des.caixa/silce-servico-rest/rest/v1/" + "municipios".toBase64() + "?q=" + cipherFinal
            
            urlTestMun = URL(string: urlBs64 )
        } catch {
            
        }
        getUfs(urlTest: URL(string: "https://simlo.des.caixa/")!)
    }
    
    private static var Manager: Alamofire.SessionManager = {
        
        // Create the server trust policies
        let serverTrustPolicies: [String: ServerTrustPolicy] = [
            
            "simlo.des.caixa:443": .disableEvaluation
        ]
        
        // Create custom manager
        let configuration = URLSessionConfiguration.default
        configuration.httpAdditionalHeaders = Alamofire.SessionManager.defaultHTTPHeaders
        let manager = Alamofire.SessionManager(
            configuration: URLSessionConfiguration.default,
            serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
        )
        
        return manager
    }()
    
    
    
    /**
     Calls the Login Web Service to authenticate the user
     */
    public func getUfs(urlTest: URL){
        
        // Handle Authentication challenge
        
        let delegate: Alamofire.SessionDelegate = LOCryptoService.Manager.delegate
        delegate.sessionDidReceiveChallenge = { session, challenge in
            var disposition: URLSession.AuthChallengeDisposition = .performDefaultHandling
            var credential: URLCredential?
            if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust {
                disposition = URLSession.AuthChallengeDisposition.useCredential
                credential = URLCredential(trust: challenge.protectionSpace.serverTrust!)
            } else {
                if challenge.previousFailureCount > 0 {
                    disposition = .cancelAuthenticationChallenge
                } else {
                    credential = LOCryptoService.Manager.session.configuration.urlCredentialStorage?.defaultCredential(for: challenge.protectionSpace)
                    if credential != nil {
                        disposition = .useCredential
                    }
                }
            }
            return (disposition, credential)
        }
        
        //Web service Request
        let header: HTTPHeaders = ["Accept": "application/json"]
        Alamofire.request(urlTest, method: .get ,headers :header).responseString { response in
            debugPrint(response)
            var listaCidades : [LOUnidadeFederativa]?
            
            if let json = response.result.value {
                do {                    
                    //listaCidades = try JSONDecoder().decode(LOCidade.self, from: json.data(using: .utf8)!)
                } catch {
                    print(error.localizedDescription)
                }
            }
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
