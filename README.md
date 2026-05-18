# BLINK MATCH MEMORY
Blink Match Memory is a fast-paced card-matching puzzle game built in Java using a Graphical User Interface (GUI). It puts a high-stakes twist on the classic memory game by adding time limits and an unpredictable weather system that scrambles your board!

I. Core Features
1. Time Pressure: A countdown timer forces you to think and act fast before it's game over.
2. Dynamic Weather Disruptions:
  - Sunny Weather: Normal, uninterrupted memory matching.
  - Stormy Weather: Triggers a sudden visual shift and instantly shuffles all remaining face-down cards to destroy your memory map.
3. Audio & High Scores: Embedded sound effects for game actions and a system to save/load your highest scores.
II. Controls
- Left Click: Flip/select cards and interact with menu buttons (Start, Help, Restart, Exit).
III. Technical Architecture
This project is built using a clean Model-View-Controller (MVC) framework:
1. Model: Handles data for individual “Card” elements, “Player” scores, “GameState” tracker, and an abstract “Weather” system (“Sunny” vs. “Stormy”).
2. View: Manages the visual interface across modular windows (“StartPanel”, “GamePanel”, “HelpPanel”, “GameOverPanel”) extending a reusable “BasePanel”.
3. Controller: The “GamePanel” acts as the main hub managing game flow, matching algorithms, and score calculations.
IV. Multi-Threading Implementation
Java Timers/Threads are used to run processes concurrently without freezing the main game interface:
1. Manages the independent countdown timer.
2. Handles the brief delay before flipping mismatched cards back down.
3. Controls the randomized background intervals for the Storm shuffle engine.
V. How to Run
cd BlinkMatchMemory &
javac -d bin -sourcepath src src/blinkmatch/Main.java &
java -cp bin blinkmatch.Main
