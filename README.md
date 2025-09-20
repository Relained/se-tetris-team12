# Tetris with SRS (Super Rotation System)

A modern implementation of Tetris featuring th### Visual Features

- **3D Block Rendering**: Highlighted blocks with depth effect
- **Smooth Animations**: Fluid piece movement and rotation
- **Color-coded Pieces**: Each tetromino type has distinct colors
- **Grid Overlay**: Clear playing field boundaries
- **Responsive Layout**: Scalable UI elements
- **Next Piece Preview**: Centered preview with 3D rendering
- **Real-time Buffer Display**: Always-updated upcoming piece Rotation System (SRS), built with Java and JavaFX.

## 🎮 Features

### Core Gameplay
- **Super Rotation System (SRS)**: Industry-standard rotation with wall kicks
- **Complete Game Flow**: Start menu, gameplay, and game over screens
- **Modern UI**: Clean, responsive interface with real-time score display
- **Enhanced Hard Drop**: Instant piece placement for competitive play
- **Next Piece Preview**: See upcoming piece with no generation delay
- **Zero-Delay Spawning**: Instant piece generation from buffer system

### SRS Implementation
- **4 Rotation States**: SPAWN (0°), RIGHT (90°), REVERSE (180°), LEFT (270°)
- **Wall Kick Tables**: Separate kick data for I-piece and JLSTZ pieces
- **O-piece Optimization**: No unnecessary rotation for square pieces
- **Standard Spawn Positions**: Correct spawn locations according to SRS guidelines

### Game Mechanics
- **Progressive Difficulty**: Speed increases with level progression
- **Line Clear Scoring**: 
  - Single: 100 × level
  - Double: 300 × level  
  - Triple: 500 × level
  - Tetris (4 lines): 800 × level
- **Level System**: Advance every 10 lines cleared
- **Real-time Stats**: Score, level, and lines cleared display
- **Next Piece Buffer**: Always-ready piece generation with preview
- **Competitive Timing**: Zero-delay piece spawning for professional play

## 🎯 Controls

| Key | Action |
|-----|--------|
| `←` `→` | Move left/right |
| `↓` | Soft drop |
| `↑` or `X` | Rotate clockwise |
| `Z` | Rotate counterclockwise |
| `Space` | Hard drop (instant placement) |
| `Enter` | Start game / Restart |
| `Escape` | Return to menu (from game over) |

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- JavaFX runtime

### Running the Game
```bash
# Clone the repository
git clone https://github.com/Relained/se-tetris-team12.git
cd se-tetris-team12

# Build and run
./gradlew run
```

### Building
```bash
./gradlew build
```

### Testing Implementation
```bash
# Test SRS system
./gradlew build && java -cp build/classes/java/main org.example.SRSTest

# Test next piece buffer system
./gradlew build && java -cp build/classes/java/main org.example.NextPieceBufferTest
```

## 🏗️ Architecture

### Key Components
- **`App.java`**: JavaFX application with UI management and game loop
- **`TetrisGame.java`**: Core game logic and state management with piece buffer
- **`Tetromino.java`**: Piece representation with SRS integration
- **`SRSSystem.java`**: Super Rotation System implementation
- **`SRSTest.java`**: Comprehensive SRS validation tests
- **`NextPieceBufferTest.java`**: Buffer system and instant spawn validation

### Game States
- **MENU**: Welcome screen with game start
- **PLAYING**: Active gameplay with real-time updates
- **GAME_OVER**: Final score display and restart options

## 🎨 Visual Features

- **3D Block Rendering**: Highlighted blocks with depth effect
- **Smooth Animations**: Fluid piece movement and rotation
- **Color-coded Pieces**: Each tetromino type has distinct colors
- **Grid Overlay**: Clear playing field boundaries
- **Responsive Layout**: Scalable UI elements

## 🧪 SRS Validation

The implementation includes comprehensive tests verifying:
- ✅ Rotation state transitions
- ✅ Wall kick table accuracy
- ✅ Piece-specific kick data
- ✅ Integration with game logic

## 📈 Scoring System

| Action | Points |
|--------|--------|
| Single Line | 100 × Level |
| Double Lines | 300 × Level |
| Triple Lines | 500 × Level |
| Tetris (4 Lines) | 800 × Level |

## 🔧 Technical Details

- **Language**: Java 17+
- **UI Framework**: JavaFX
- **Build System**: Gradle
- **Architecture**: MVC pattern with clear separation of concerns
- **Standards Compliance**: Full SRS specification implementation

## 🎯 What Makes This Special

1. **Authentic SRS**: Implements the exact rotation system used in modern Tetris games
2. **Competitive-Ready**: Instant hard drop and precise controls
3. **Educational**: Well-documented code demonstrating game development patterns
4. **Extensible**: Clean architecture for easy feature additions

## 🏆 Team

- **Team 12** - Software Engineering Course Implementation

---

*Experience Tetris the way it was meant to be played - with perfect rotations and competitive precision!* 🎮
25 소공 12팀 테트리스 레포지토리입니다.
