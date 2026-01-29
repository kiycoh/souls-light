# Soul's Light

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![LibGDX](https://img.shields.io/badge/LibGDX-1.12.1-red?style=for-the-badge)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

> **A top-down 2D Action RPG built with the LibGDX framework.**

**Soul's Light** is a pixel-art action game featuring dynamic combat, exploration, and RPG elements. Built on the robust MVC architecture.

---

## 🎮 Key Features

- **Action-Packed Combat**: Real-time combat system with attacks, hit detection, and enemy AI.
- **MVC Architecture**: Strict separation of concerns (Model-View-Controller) for maintainable code.
- **Physics Engine**: Integrated **Box2D** physics for realistic collision and movement.
- **Item System**: Inventory management with interactive items.
- **Dynamic Lighting**: (Planned/In-progress) Atmospheric lighting effects using Box2DLights.
- **Custom UI**: Integrated HUD and menus using Scene2D.

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- **Java Development Kit (JDK) 21**: Ensure you have JDK 21 installed.
  ```bash
  java -version
  ```

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/souls-light.git
    cd souls-light
    ```

2.  **Build the project**
    Use the included Gradle wrapper to build the project.
    ```bash
    # Linux / macOS
    ./gradlew build

    # Windows
    gradlew build
    ```

### Running the Game

To launch the desktop version of the game:

```bash
# Linux / macOS
./gradlew lwjgl3:run

# Windows
gradlew lwjgl3:run
```

---

## 🏗️ Project Structure

The project follows the standard LibGDX structure:

- **`core/`**: Contains all the game logic, shared across platforms.
  - `model/`: Game entities, physics, and logic (e.g., `GameModel`, `Player`).
  - `view/`: Rendering logic, screens, and UI (e.g., `GameScreen`, `GameHUD`).
  - `controller/`: Input handling and game loop management (e.g., `GameController`).
- **`lwjgl3/`**: The Desktop launcher backend using LWJGL 3.
- **`assets/`**: Images, sounds, maps, and configuration files.

---

## 🕹️ Controls

| Action | Key / Input | Controller |
|--------|-------------|------------|
| **Move** | `W`, `A`, `S`, `D` | `Left Stick` |
| **Attack** | `Space` | `A` |
| **Special Attack** | `O` | `Y` |
| **Interact** | `E` | To be implemented |
| **Use Inventory** | `1`, `2`, `3`| To be implemented |
| **Save** | `F5` | To be implemented |
| **Load saved game** | `F6` | To be implemented |
| **Pause** | `ESC` | To be implemented |
| **Open Debug Menu** | `F1` | To be implemented |
| **Activate/Deactivate Debug Mode** | `0` | To be implemented |

---

## 🤝 Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

<p align="center">
  Built with ❤️ using <a href="https://libgdx.com/">LibGDX</a>
</p>
