//
//  LOBin.swift
//  Loterias
//
//  Created by Jenkins on 27/10/17.
//  Copyright © 2017 Caixa. All rights reserved.
//

import UIKit

class LOBin: NSObject {
    public func fromUtf8 (data: String) -> [UInt8] {
        return Array(data.utf8)
    }
    
    public func toUtf8 (data : [UInt8]) -> String {
        return String(data: Data(bytes: data, count: data.count), encoding: .utf8)!
    }
}
