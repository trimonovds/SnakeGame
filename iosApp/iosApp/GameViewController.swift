//
//  GameViewController.swift
//  iosApp
//
//  Created by Дмитрий Тримонов on 21.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import UIKit
import shared

class GameViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        engine.run()
    }
    
    private let engine = GameEngine(settings: GameSettings(fieldSize: shared.Size(width: 6, height: 6)))
}
