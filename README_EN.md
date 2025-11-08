# Employee Production Performance Prediction System

A Java application that predicts employee production performance using Fuzzy Logic.

## 📋 Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Fuzzy Logic Model](#fuzzy-logic-model)
- [Development](#development)
- [License](#license)

## ✨ Features

- **Fuzzy Logic Based Prediction**: Predicts production performance based on employee experience, age, and gender
- **Visualization**: Displays membership functions and fuzzy logic rules graphically
- **Flexible Model**: Easily editable rule set via FCL (Fuzzy Control Language) file

## 🔧 Requirements

- **Java**: JDK 8 or higher
- **jFuzzyLogic Library**: Available in the project as `lib/jFuzzyLogic.jar`
- **IDE**: Eclipse (recommended) or any Java IDE

## 📦 Installation

1. Clone the project:
```bash
git clone <repository-url>
cd BulanikOdev
```

2. Open the project in Eclipse:
   - File → Import → Existing Projects into Workspace
   - Select the project folder
   - Complete the import process

3. Ensure jFuzzyLogic.jar is in the classpath:
   - Right-click project → Properties → Java Build Path → Libraries
   - Verify that `lib/jFuzzyLogic.jar` is added

## 🚀 Usage

1. Run the project (execute the `main` method in `Main.java`)

2. The program will ask for the following information:
   - **Experience (years)**: Employee's work experience (e.g., 5, 10, 15)
   - **Gender**: 0 = Female, 1 = Male
   - **Age (years)**: Employee's age (e.g., 25, 40, 55)

3. Based on your input, the program will:
   - Display membership functions graphically
   - Calculate the estimated number of parts produced

### Example Usage

```
Experience (years): 10
Gender (0 = Female, 1 = Male): 1
Age (years): 35
```

## 📁 Project Structure

```
BulanikOdev/
├── src/
│   └── pkt/
│       ├── Main.java              # Main application class
│       ├── CalisanUretim.java    # Fuzzy logic model class
│       └── CalisanUretim.fcl     # Fuzzy logic rules (FCL)
├── lib/
│   └── jFuzzyLogic.jar           # jFuzzyLogic library
├── bin/                           # Compiled class files
├── .gitignore                     # Git ignore file
├── .gitattributes                 # Git attributes file
└── README_EN.md                   # This file
```

## 🧠 Fuzzy Logic Model

### Input Variables

#### 1. Experience (years)
- **Low**: 0-5 years
- **Medium**: 5-15 years
- **High**: 10+ years

#### 2. Gender
- **Female**: 0
- **Male**: 1

#### 3. Age (years)
- **Young**: 18-35 years
- **Medium**: 30-50 years
- **Old**: 45-60 years

### Output Variable

#### Number of Parts
- **Low**: 0-100 parts
- **Medium**: 50-350 parts
- **High**: 300-500+ parts

### Rule Set

The model contains 18 fuzzy logic rules. The rules determine production performance based on combinations of experience, age, and gender.

**Example Rules:**
- High experience + Young age → High production
- Low experience + Old age → Low production
- Medium experience + Medium age + Male → High production

For detailed rules, see the `src/pkt/CalisanUretim.fcl` file.

### Defuzzification

The output value is calculated using the **COG (Center of Gravity)** method.

## 💻 Development

### Editing the Model

To modify fuzzy logic rules or membership functions, edit the `src/pkt/CalisanUretim.fcl` file.

### Viewing Results

You can print results to the console by uncommenting line 22 in `Main.java`:

```java
System.out.println(model);
```

## 📄 License

This project is developed for educational purposes.

## 👥 Contributors

- Project developer

## 📝 Notes

- The jFuzzyLogic library is available in the project as `lib/jFuzzyLogic.jar`
- The FCL file is written according to the IEC 61131-7 standard
- The jFuzzyLogic JFuzzyChart class is used for graphical display

