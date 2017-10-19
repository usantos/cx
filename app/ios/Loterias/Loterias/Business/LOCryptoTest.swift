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

class LOCryptoTest: NSObject {
    let salt: Array<UInt8> = Array("nacllcan".utf8)
    let password: Array<UInt8> = Array("s33krit".utf8)
//    let iv: Array<UInt8> = AES.randomIV(AES.blockSize)
//
//    func cripfyFinal () {
//        do {
//            print("Salt: ", salt)
//            print("Iv: ", iv)
//            
//            let key = try PKCS5.PBKDF2(password: password, salt: salt, iterations: 1, variant: .sha256).calculate()
//            let key3 = try PKCS5.PBKDF2(password: password, salt: salt).calculate()
//            let key2 = try PKCS5.PBKDF1(password: password, salt: salt, variant: PKCS5.PBKDF1.Variant.sha1, iterations: 1, keyLength: 16)
//            print("Key: ", key)
//            print("Key: ", key2)
//            print("Key3: ",key3)
//
//            //let aes = try AES(key: key, iv: iv) // aes128
//            
//            let queryParam = "quantidadeTotalNumeros==6&&numerosMarcados==15,18,19,48,52,58&&menorPrognostico==1&&maiorPrognostico==60&&"
//            
//            var encryptor = try AES(key: key, iv: iv , blockMode: .CBC, padding: .pkcs5).makeEncryptor()
//            print("Encryptor: ",encryptor)
//            var ciphertext = Array<UInt8>()
//            
//            ciphertext += try encryptor.update(withBytes: Array(queryParam.utf8))
//            
//            ciphertext += try encryptor.finish()
//            print("Cipher: ",ciphertext)
//            
//            let cipherFinal = ciphertext.toBase64()! + "!#!#!" + key.toBase64()! + "!#!#!" + iv.toBase64()!
//            //
//            let urlBs64 = "https://simlo.des.caixa/silce-servico-rest/rest/v1/" + "apostas/completar-jogo".toBase64() + "?q=" + cipherFinal
//            print(urlBs64)
//            //urlTestMun = URL(string: urlBs64 )
//    
//        } catch {
//            
//        }
//
//    }
//    
//    func cripfyUrlWithQueryParam () {
//        var urlTestMun : URL?
//        do {
//            //print("Salt: ", salt)
//            //print("Iv: ", iv)
//            
//            let data = ("YbEzQCORJsub6TqXt4n9Dg==").data(using: String.Encoding.utf8)
//            let base64 = data!.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0))
//            var arrayTest = [UInt8]()
//            
//            for c in base64.characters {
//                print(c)
//                arrayTest.append(Array(String(c).utf8)[0])
//            }
//            
//            let arraySalt: [UInt8] = Array("YbEzQCORJsub6TqXt4n9Dg==".utf8)
//            let arrayIv: [UInt8] = Array("8hQLtNV44Y1bIeONmlr+Dg==".utf8)
//            print("Salt: ", arraySalt)
//            print("Iv: ", arrayIv)
//            //let key = try PKCS5.PBKDF2(password: password, salt: arraySalt, iterations: 1, variant: .sha256).calculate()
//            
//            //print("Key: ", key.description)
//            
//            //let aes = try AES(key: key, iv: iv) // aes128
//            
//            let queryParam = "idUF==17"
//            
//            var encryptor = try AES(key: Array("QEm06ZAThg1E314zziahsg==".utf8), iv: arrayIv , blockMode: .CBC, padding: .pkcs5).makeEncryptor()
//            print("Encryptor: ",encryptor)
//            var ciphertext = Array<UInt8>()
//            
//            ciphertext += try encryptor.update(withBytes: Array(queryParam.utf8))
//            
//            ciphertext += try encryptor.finish()
//            print("Cipher: ",ciphertext)
////            let cipherFinal = ciphertext.toHexString() + "!#!#!" + key.toHexString() + "!#!#!" + iv.toHexString()
////
////            let urlBs64 = "https://simlo.des.caixa/silce-servico-rest/rest/v1/" + "municipios".toBase64() + "?q=" + cipherFinal
//            
//            //urlTestMun = URL(string: urlBs64 )
//        } catch {
//            
//        }
//        getUfs(urlTest: URL(string: "https://simlo.des.caixa/")!)
//    }
//    
//    
//    private static var Manager: Alamofire.SessionManager = {
//        
//        // Create the server trust policies
//        let serverTrustPolicies: [String: ServerTrustPolicy] = [
//            
//            "simlo.des.caixa:443": .disableEvaluation
//        ]
//        
//        // Create custom manager
//        let configuration = URLSessionConfiguration.default
//        configuration.httpAdditionalHeaders = Alamofire.SessionManager.defaultHTTPHeaders
//        let manager = Alamofire.SessionManager(
//            configuration: URLSessionConfiguration.default,
//            serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
//        )
//        
//        return manager
//    }()
//    
//    
//    
//    /**
//     Calls the Login Web Service to authenticate the user
//     */
//    public func getUfs(urlTest: URL){
//        
//        // Handle Authentication challenge
//        
//        let delegate: Alamofire.SessionDelegate = LOCryptoTest.Manager.delegate
//        delegate.sessionDidReceiveChallenge = { session, challenge in
//            var disposition: URLSession.AuthChallengeDisposition = .performDefaultHandling
//            var credential: URLCredential?
//            if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust {
//                disposition = URLSession.AuthChallengeDisposition.useCredential
//                credential = URLCredential(trust: challenge.protectionSpace.serverTrust!)
//            } else {
//                if challenge.previousFailureCount > 0 {
//                    disposition = .cancelAuthenticationChallenge
//                } else {
//                    credential = LOCryptoTest.Manager.session.configuration.urlCredentialStorage?.defaultCredential(for: challenge.protectionSpace)
//                    if credential != nil {
//                        disposition = .useCredential
//                    }
//                }
//            }
//            return (disposition, credential)
//        }
//        
//        //Web service Request
//        let header: HTTPHeaders = ["Accept": "application/json"]
//        LOCryptoTest.Manager.request(urlTest, method: .get, parameters: [:], encoding: URLEncoding(destination: .queryString) ,headers :header).responseString { response in
//            debugPrint(response)
//            //var listaCidades : LOWrapperCidade?
//            
//            if let json = response.result.value {
//                do {
//                    //listaCidades = try JSONDecoder().decode(LOWrapperCidade.self, from: json.data(using: .utf8)!)
//                } catch {
//                    print(error.localizedDescription)
//                }
//            }
//        }
//    }
//    
//    //    var cipher = forge.cipher.createCipher('AES-CBC', key);
//    //    cipher.start({iv: iv});
//    //    cipher.update(forge.util.createBuffer(jsonString));
//    //    cipher.finish();
//    //    enc2 = cipher.output;
//    //
//    //    request.data = enc2.data + '!#!#!' + key + '!#!#!' + iv;
//    //    request.data = btoa(request.data);
    
}

