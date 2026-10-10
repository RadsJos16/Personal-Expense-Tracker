# Personal Expense Tracker

**Java Programming Micro-Project**  
Student: Radha Joshi | CSE Division A | Roll No. 54  
MIT World Peace University

## Overview
A Java Swing desktop application to record personal expenses and monitor a monthly budget.

## Features
- Add, view, search, and delete expenses
- Record date, category, description, and amount
- Set a monthly budget and calculate remaining budget
- Summary of total, highest, lowest, average, and number of expenses
- Store expenses and budget locally so they remain after restarting

## Technologies
Java, Java Swing, OOP, ArrayList, file handling, VS Code.

## Files
- `Expense.java` — expense data class
- `ExpenseGUI.java` — main GUI and application logic
- `expenses.txt` — created automatically for expense records
- `budget.txt` — created automatically for the budget

## Run the project
Install a JDK, open this folder in VS Code or a terminal, and run:

```bash
javac Expense.java ExpenseGUI.java
java ExpenseGUI
```

This is a local desktop app; it does not use a database or cloud storage.
