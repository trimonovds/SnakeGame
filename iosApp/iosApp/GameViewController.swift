//
//  GameViewController.swift
//  iosApp
//
//  Created by Дмитрий Тримонов on 21.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import UIKit
import shared

class GameViewController: UIViewController, GameView, UICollectionViewDelegateFlowLayout, UICollectionViewDataSource {
    
    var delegate: GameViewDelegate? = nil
    
    func render(state: GameState) {
        switch state {
        case let gameOver as GameState.GameOver:
            gameCellsView.alpha = 0.0
            gameControllerView.alpha = 0.0
            gameOverView.alpha = 1.0
        case let playing as GameState.Playing:
            gameCellsView.alpha = 1.0
            gameControllerView.alpha = 1.0
            gameOverView.alpha = 0.0
            ds = playing.cells
            gameCellsView.reloadData()
        default:
            fatalError()
        }
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return ds.count
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return ds[section].count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: GameViewController.ri, for: indexPath) as! GameUICollectionViewCell
        let data = ds[indexPath.section][indexPath.item]
        cell.update(with: data)
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let dim = minViewDimension / CGFloat(GameViewController.cellsInRow)
        return CGSize(width: dim, height: dim)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        
        gameOverView = GameOverView()
        gameOverView.onRetry = { [weak self] in
            self?.presenter.onDidTapRestart()
        }
        view.addSubview(gameOverView)
        gameOverView.translatesAutoresizingMaskIntoConstraints = false
        
        layout = UICollectionViewFlowLayout()
        layout.minimumLineSpacing = 0
        layout.minimumInteritemSpacing = 0
        layout.sectionInset = .zero
        gameCellsView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        view.addSubview(gameCellsView)
        gameCellsView.translatesAutoresizingMaskIntoConstraints = false
        gameCellsView.backgroundColor = .clear
        gameCellsView.register(GameUICollectionViewCell.self, forCellWithReuseIdentifier: GameViewController.ri)
        gameCellsView.dataSource = self
        gameCellsView.delegate = self
        
        gameControllerView = GameControllerView()
        gameControllerView.onTap = { [weak self] btn in
            self?.presenter.onDidTapButton(button: btn)
        }
        view.addSubview(gameControllerView)
        gameControllerView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            gameOverView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            gameOverView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),
            gameOverView.leftAnchor.constraint(equalTo: view.leftAnchor),
            gameOverView.rightAnchor.constraint(equalTo: view.rightAnchor),
            gameCellsView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            gameCellsView.leftAnchor.constraint(equalTo: view.leftAnchor),
            gameCellsView.rightAnchor.constraint(equalTo: view.rightAnchor),
            gameCellsView.heightAnchor.constraint(equalToConstant: minViewDimension),
            gameControllerView.topAnchor.constraint(equalTo: gameCellsView.bottomAnchor, constant: 16),
            gameControllerView.heightAnchor.constraint(equalToConstant: 100),
            gameControllerView.centerXAnchor.constraint(equalTo: view.centerXAnchor)
        ])
        
        presenter.onAttach(view: self)
    }
    
    deinit {
        presenter.onDettach()
    }
    
    private var minViewDimension: CGFloat {
        return min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)
    }
    
    private static let ri = "Cell"
    private static let cellsInRow: Int32 = 20
    private let presenter = GamePresenter(cellsInRow: GameViewController.cellsInRow)
    private var ds: [[GameCell]] = []
    private var gameOverView: GameOverView!
    private var layout: UICollectionViewFlowLayout!
    private var gameCellsView: UICollectionView!
    private var gameControllerView: GameControllerView!
}

class GameControllerView: UIView {
    
    var onTap: ((GameViewDelegateGameViewButton) -> Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        [upButton, leftButton, downButton, rightButton].forEach({
            addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
            $0.backgroundColor = .blue
            $0.setTitleColor(.white, for: .normal)
            $0.titleLabel?.font = UIFont.systemFont(ofSize: 16, weight: .bold)
        })
        
        upButton.addTarget(self, action: #selector(onUpTap), for: .touchUpInside)
        leftButton.addTarget(self, action: #selector(onLeftTap), for: .touchUpInside)
        downButton.addTarget(self, action: #selector(onDownTap), for: .touchUpInside)
        rightButton.addTarget(self, action: #selector(onRightTap), for: .touchUpInside)
        
        upButton.setTitle("Up", for: .normal)
        leftButton.setTitle("Left", for: .normal)
        downButton.setTitle("Down", for: .normal)
        rightButton.setTitle("Right", for: .normal)
        
        NSLayoutConstraint.activate([
            topAnchor.constraint(equalTo: upButton.topAnchor),
            upButton.widthAnchor.constraint(equalTo: heightAnchor, multiplier: 0.5),
            upButton.heightAnchor.constraint(equalTo: heightAnchor, multiplier: 0.5),
            upButton.centerXAnchor.constraint(equalTo: centerXAnchor),
            downButton.topAnchor.constraint(equalTo: upButton.bottomAnchor),
            downButton.widthAnchor.constraint(equalTo: upButton.widthAnchor),
            downButton.heightAnchor.constraint(equalTo: upButton.heightAnchor),
            leftButton.rightAnchor.constraint(equalTo: downButton.leftAnchor),
            leftButton.topAnchor.constraint(equalTo: downButton.topAnchor),
            leftButton.widthAnchor.constraint(equalTo: upButton.widthAnchor),
            leftButton.heightAnchor.constraint(equalTo: upButton.heightAnchor),
            rightButton.leftAnchor.constraint(equalTo: downButton.rightAnchor),
            rightButton.topAnchor.constraint(equalTo: downButton.topAnchor),
            rightButton.widthAnchor.constraint(equalTo: upButton.widthAnchor),
            rightButton.heightAnchor.constraint(equalTo: upButton.heightAnchor),
            bottomAnchor.constraint(equalTo: downButton.bottomAnchor),
            leftAnchor.constraint(equalTo: leftButton.leftAnchor),
            rightAnchor.constraint(equalTo: rightButton.rightAnchor)
        ])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    @objc func onUpTap() {
        onTap?(.top)
    }
    
    @objc func onDownTap() {
        onTap?(.bottom)
    }
    
    @objc func onLeftTap() {
        onTap?(.left)
    }
    
    @objc func onRightTap() {
        onTap?(.right)
    }
    
    private let upButton: UIButton = UIButton(type: .system)
    private let leftButton: UIButton = UIButton(type: .system)
    private let downButton: UIButton = UIButton(type: .system)
    private let rightButton: UIButton = UIButton(type: .system)
}

class GameOverView: UIView {
    
    var onRetry: (() -> Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .white
        addSubview(textLabel)
        addSubview(retryButton)
        textLabel.text = "Game over"
        textLabel.textColor = .black
        retryButton.setTitle("Retry", for: .normal)
        textLabel.translatesAutoresizingMaskIntoConstraints = false
        retryButton.translatesAutoresizingMaskIntoConstraints = false
        retryButton.contentEdgeInsets = UIEdgeInsets(top: 8.0, left: 16.0, bottom: 8.0, right: 16.0)
        retryButton.backgroundColor = .green
        retryButton.setTitleColor(.white, for: .normal)
        retryButton.addTarget(self, action: #selector(onRetryTap), for: .touchUpInside)
        NSLayoutConstraint.activate([
            textLabel.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            textLabel.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            retryButton.topAnchor.constraint(equalTo: textLabel.bottomAnchor, constant: 16),
            retryButton.centerXAnchor.constraint(equalTo: self.centerXAnchor)
        ])
    }
    
    @objc func onRetryTap() {
        onRetry?()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private let textLabel = UILabel()
    private let retryButton = UIButton()
}

class GameUICollectionViewCell: UICollectionViewCell {
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        contentView.layer.borderColor = UIColor.black.cgColor
        contentView.layer.borderWidth = 1
        contentView.layer.cornerRadius = 2
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func update(with data: GameCell) {
        contentView.backgroundColor = data.color
    }
}

extension GameCell {
    var color: UIColor {
        switch self {
        case GameCell.snake: return .black
        case GameCell.empty: return .lightGray
        default: fatalError()
        }
    }
}
