<div align="center">

# 💰 KidBank

### Smart Money for Smart Kids 🚀

A simple and educational JavaFX desktop application that helps children learn money management, saving, and financial responsibility.

![Java](https://img.shields.io/badge/Java-11+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-Desktop-blue)
![JUnit](https://img.shields.io/badge/Testing-JUnit-green)
![Status](https://img.shields.io/badge/Status-Student_Project-purple)

</div>

---

## 📌 About the Project

**KidBank** is a desktop banking application designed for children and parents.  
It allows children to manage virtual money, complete tasks, save for goals, and track their financial activity.

Parents can create tasks and assign rewards, while children can complete tasks and earn money in a safe virtual environment.

This project was developed as part of the **Software Engineering SE-25** course project.

---

## ✨ Key Features

| Feature | Description |
|---|---|
| 🔐 Authentication | Login and registration system |
| 👨‍👩‍👧 Parent & Child Roles | Separate dashboards for parents and children |
| ✅ Task System | Parents create tasks, children complete them |
| 💵 Rewards | Approved tasks automatically add money to child balance |
| 🏦 Wallet | Children can view balance, deposit history, and withdraw money |
| 🎯 Savings Goals | Children can create saving goals and track progress |
| 📊 Statistics | Simple charts and summary cards |
| 🧾 Transaction History | Credits and debits are stored and displayed |
| 🎨 JavaFX UI | Clean desktop user interface |
| 🧪 Unit Testing | Service-layer tests using JUnit |

---

## 🖼 Preview

Place screenshots inside the `docs/` folder.

```bash
docs/login.png
docs/dashboard.png
docs/wallet.png
docs/savings.png
```

Example:

```md
![Login Screen](docs/login.png)
![Dashboard](docs/dashboard.png)
```

---

## 🗂 Project Structure

```bash
KidBank/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── app/
│   │       │   └── Main.java
│   │       ├── models/
│   │       ├── services/
│   │       ├── storage/
│   │       ├── ui/
│   │       └── utils/
│   │
│   └── test/
│       └── java/
│           └── services/
│
├── doc/
├── docs/
├── README.md
└── pom.xml
```

---

## ▶️ How to Run

Clone the repository:

```bash
git clone https://github.com/muhammad-codx/Kidbank.git
cd KidBank
```

Run the main file:

```bash
src/main/java/app/Main.java
```

The application entry point is:

```bash
src/main/java/app/Main.java
```

---

## ⚙️ JavaFX Setup

If JavaFX does not run, add VM options:

```bash
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
```

Example for macOS:

```bash
--module-path /Users/yourname/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```

Example for Windows:

```bash
--module-path "C:\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml
```

---

## 🧪 Testing

Tests are located in:

```bash
src/test/java/services
```

Covered service layers:

| Test Area | Description |
|---|---|
| AuthService | Login, registration, parent-child validation |
| BankService | Deposit, withdraw, balance calculation |
| TaskService | Task creation, completion, approval, rejection |
| SavingsService | Savings goal creation and contribution |

Run tests from your IDE or with Maven:

```bash
mvn test
```

---

## 📂 Documentation

Documentation files are stored in:

```bash
doc/
```

This folder can include:

- Project report
- User manual
- Product backlog
- Team members file
- Screenshots or diagrams

---

## 📋 Requirements

| Requirement | Version |
|---|---|
| Java | 11 or higher |
| JavaFX SDK | Required |
| Maven | Recommended |
| IDE | IntelliJ IDEA or VS Code |

---

## 🛠 Future Improvements

- PostgreSQL or MySQL database integration
- Better dashboard statistics
- Mobile version
- Stronger authentication
- Improved UI/UX design
- Notifications and reminders
- Export transaction history

---

## 👨‍💻 Team Members

| # | Name | Student ID |
|---|---|---|
| 1 | Boymirzayev Akbarshoh | SE16017 |
| 2 | Sharifkulov Jasur | SE16058 |
| 3 | Abdumanov Akromjon | SE16047 |

---

## ⭐ Support

If you like this project, don't forget to leave a ⭐ on the repository!

<div align="center">

**KidBank — Smart Money for Smart Kids 💰**

</div>
