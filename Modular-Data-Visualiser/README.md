**Modular Data Visualizer** is a lightweight Java Swing application for exploring tabular data (CSV), drawing Bar / Line / Pie / Box plots, and automatically recommending an appropriate chart type. It supports auto-aggregation (binning) for large X axes, CSV import, and PNG export.

> Built with Java + Gradle. Simple, modular chart components make it easy to extend with new chart types and analysis modules.

---
## made by
Aniket Gawande -124B1F024
Atharv Shinde  -124B1F029
Ashutosh More  -124B1F036

## Features

- Load CSV files and select X (labels) and Y (numeric values) columns
- Bar, Line, Pie and Box plots
- Auto-aggregation (binning) when X has too many items
- Chart recommender that suggests the best chart type + explanation
- Export chart area as PNG
- Clean, modular codebase (ChartFactory, ChartPanel, DataReader, analyzers)

---

## Screenshots

<img width="400" height="300" alt="image" src="https://github.com/user-attachments/assets/5560a62d-d12e-43cd-b073-c4cd6e5a91dd" />
<img width="400" height="300" alt="image" src="https://github.com/user-attachments/assets/ce0fdf15-8898-4240-9331-e9b00d427572" />


---

## Quickstart — Prerequisites

- Java JDK 17 or newer (17 LTS recommended). Works with newer JDKs (OpenJDK 17/21/25) but if you use JDK >17, ensure your IDE project language level matches.
- Git (to clone)
- Gradle wrapper is included in the repo — no global Gradle install required.

> On Windows, run Gradle commands with `gradlew.bat`; on macOS/Linux use `./gradlew`.

---

## Run in your IDE (recommended)

1. Clone the repo:
   git clone https://github.com/aniket-gawande/Modular-Data-Visualiser.git
   cd Modular-Data-Visualiser

2. Open the project in IntelliJ IDEA (or Eclipse):

   * **IntelliJ:** `File` → `Open` → select the project folder.
   * Let IntelliJ import the Gradle project and resolve dependencies.

3. Set project SDK to Java 17 (or the JDK you have installed).

4. Run the main class:

   * `com.example.visualizer.AppMain` (or `com.example.visualizer.ui.MainWindow` if AppMain delegates to UI).
   * Use the green ▶ run button in IntelliJ.

---

## Run from command line (Gradle)

**Build & run (Gradle wrapper)**

Unix / macOS:

```bash
./gradlew clean build
./gradlew run
```

Windows (PowerShell or CMD):

```powershell
.\gradlew.bat clean build
.\gradlew.bat run
```

> If the `application` plugin is configured, `./gradlew run` will launch the app. If not, proceed to the manual run instructions below.

---

## Build a runnable JAR (CLI)

1. Build:

```bash
./gradlew clean build
```

2. Find the JAR in `build/libs/`. If the build produces a fat/shadow JAR, it will contain all dependencies and a `Main-Class` manifest. If not, use the classpath command below.

3. Run (if JAR has `Main-Class`):

```bash
java -jar build/libs/modular-data-visualizer-<version>.jar
```

4. If no runnable JAR is produced, run from classes:

```bash
# runs the main class directly using compiled classes and resources
java -cp "build/classes/java/main;build/resources/main;build/libs/*" com.example.visualizer.AppMain
```

(Use `:` instead of `;` as the classpath separator on macOS/Linux.)

---

## CSV Format & Example

* First row should be column headers (names).
* Each subsequent row is a record.
* Choose one column for X (labels — categorical or short text) and one column for Y (numeric values).
* Example (`sample-data.csv`):

```csv
Category,Value
A,10
B,25
C,7
D,13
```

* For time/continuous X you can use ISO date strings (the UI currently treats X as labels; numeric/time axis support can be added later).

---

## Usage Notes

* If your X axis has thousands of unique labels, enable **Auto-aggregate** to bin the X-values. Set `Bins` to the number of aggregated buckets you want.
* Use short labels for X axis to avoid overlap.
* Exported PNG reflects the visible chart panel size — use the application window to control export resolution.

---

## Troubleshooting

**`java.lang.StackOverflowError` on startup (Nimbus / Look-and-Feel)**

* Some JVM + LAF combos trigger recursion. Workarounds:

  * Run with a simpler LAF: uncomment or add `UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())` early in `main`.
  * Use JDK 17 if you see UI stack issues with newer JDKs.

**Gradle `run` fails with `Main class not found`**

* Verify `application` plugin or check `mainClass` configuration in `build.gradle`.
* Or run the class directly using `java -cp` with compiled classes, see *Build a runnable JAR* section.

**Git push rejected: non-fast-forward**

* You may encounter `failed to push some refs` if your local branch is behind remote. Run:

  ```bash
  git fetch origin
  git merge origin/main --allow-unrelated-histories
  git push origin main
  ```

  (If you're unsure, check the repo's commit graph and resolve conflicts before pushing.)

---

## Project structure (high level)

```
src/
  main/
    java/
      com.example.visualizer/
        AppMain.java
        ui/
          MainWindow.java
          ChartPanel.java
        charts/
          BarChart.java
          LineChart.java
          PieChart.java
          BoxPlot.java
        data/
          DataReader.java
          DataSet.java
          DataSeries.java
        analysis/
          ChartRecommender.java
          BinAggregator.java
resources/
  sample-data.csv
build.gradle
gradlew, gradlew.bat, settings.gradle
```

---

## Contributing

* Open issues for bugs or feature requests.
* Send PRs with clear commit messages and a short description of the change.
* Keep UI changes modular — add new charts via `ChartFactory` and `Chart` implementations.

---

## License & Contact
```
MIT License
Author: Aniket Gawande (Aniket, Atharv, Ashutosh)
```
