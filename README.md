# ♟️ Chess Game – Phase 1

## 📘 Project Overview
This project is the first phase of a chess program implemented in Java for our OOP course.  
Phase 1 focuses on building the **skeleton structure** of the application using object-oriented design principles.  

The project is organized into packages:  
- `board` → Board and Position classes  
- `game` → Game and Player classes  
- `pieces` → Piece (abstract) and subclasses (partner’s section)  
- `utils` → Main driver and TestMain (testing harness)  

---

## ✅ Phase 1 Deliverables
- **Board.java** – 8×8 grid, displays board, supports moving pieces.  
- **Game.java** – manages the game flow (start, play, switch turns, end).  
- **Player.java** – represents a chess player with name and color.  
- **Position.java** – represents a square on the board (row/col).  
- **Main.java** – entry point for running the game.  
- **TestMain.java** – (optional) testing driver for development.  

---

## 🛠 How to Compile & Run

### From IntelliJ IDEA
1. Open the project in IntelliJ.  
2. Run `Main.java` from the `utils` package.  

### From Command Line
In the project root:  
```bash
# Compile everything
javac -d out -sourcepath src src/utils/Main.java

# Run the program
java -cp out utils.Main
```

---

## 📖 Documentation
Javadoc HTML has been generated and is available in the `YEs/` folder.  
Open `YEs/index.html` in a web browser to browse the class documentation.

---index.html available on Request---

---

## 🚀 Next Steps (Future Phases)
- Implement the `Piece` subclasses (`Pawn`, `Rook`, `Knight`, etc.).  
- Add legal move validation.  
- Implement check, checkmate, and stalemate detection.  
- Expand game loop for full chess play.  

---

### 👥 Authors
- **You** – Board, Game, Player, Position, Main  
- **Eric Nepogodin** – Piece classes and move rules  
