import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;

public class ExpenseGUI extends JFrame {
    private final ArrayList<Expense> expenses = new ArrayList<>();
    private final File expenseFile = new File("expenses.txt");
    private final File budgetFile = new File("budget.txt");
    private double budget = 0;
    private int nextId = 1;
    private final Color purple = new Color(91, 75, 138);
    private JLabel totalLabel, budgetLabel, remainingLabel, countLabel;
    private DefaultTableModel model;
    private JTable table;

    public ExpenseGUI() {
        setTitle("Personal Expense Tracker");
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 520));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadExpenses();
        loadBudget();
        buildUI();
        refreshDashboard();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 247, 251));

        JPanel side = new JPanel();
        side.setBackground(purple);
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(BorderFactory.createEmptyBorder(24, 14, 20, 14));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("<html><center>PERSONAL<br>EXPENSE TRACKER</center></html>");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 18));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(brand);
        side.add(Box.createVerticalStrut(32));
        sideButton(side, "Dashboard", this::refreshDashboard);
        sideButton(side, "Add Expense", this::addExpense);
        sideButton(side, "Search Expenses", this::searchExpenses);
        sideButton(side, "Set Monthly Budget", this::setBudget);
        sideButton(side, "Expense Summary", this::showSummary);
        sideButton(side, "Delete Selected", this::deleteSelected);
        side.add(Box.createVerticalGlue());
        sideButton(side, "Exit", () -> { saveExpenses(); saveBudget(); dispose(); });
        add(side, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(14, 16));
        main.setBackground(new Color(248, 247, 251));
        main.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel headings = new JPanel();
        headings.setOpaque(false);
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Expense Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        JLabel subtitle = new JLabel("Track your spending and monthly budget");
        subtitle.setForeground(Color.GRAY);
        headings.add(title);
        headings.add(Box.createVerticalStrut(5));
        headings.add(subtitle);
        header.add(headings, BorderLayout.WEST);

        JButton add = new JButton("+ Add Expense");
        style(add, purple, Color.WHITE);
        add.addActionListener(e -> addExpense());
        header.add(add, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 15));
        center.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        totalLabel = new JLabel("₹0.00");
        budgetLabel = new JLabel("₹0.00");
        remainingLabel = new JLabel("₹0.00");
        countLabel = new JLabel("0");
        cards.add(card("TOTAL SPENT", totalLabel, new Color(232, 224, 250)));
        cards.add(card("MONTHLY BUDGET", budgetLabel, new Color(225, 239, 233)));
        cards.add(card("BUDGET LEFT THIS MONTH", remainingLabel, new Color(255, 239, 214)));
        cards.add(card("EXPENSE COUNT", countLabel, new Color(225, 235, 250)));
        center.add(cards, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 227, 237)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        JLabel recent = new JLabel("Recent Expenses");
        recent.setFont(new Font("SansSerif", Font.BOLD, 18));
        tablePanel.add(recent, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Description", "Amount"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(27);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton search = new JButton("Search");
        JButton delete = new JButton("Delete Selected");
        style(search, new Color(242, 238, 250), purple);
        style(delete, new Color(250, 231, 233), new Color(160, 48, 60));
        search.addActionListener(e -> searchExpenses());
        delete.addActionListener(e -> deleteSelected());
        actions.add(search);
        actions.add(delete);
        tablePanel.add(actions, BorderLayout.SOUTH);

        center.add(tablePanel, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private JPanel card(String title, JLabel value, Color color) {
        JPanel p = new JPanel();
        p.setBackground(color);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 10));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        value.setFont(new Font("SansSerif", Font.BOLD, 19));
        p.add(label);
        p.add(Box.createVerticalStrut(12));
        p.add(value);
        return p;
    }

    private void sideButton(JPanel panel, String text, Runnable action) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(185, 40));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(105, 89, 151));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        b.addActionListener(e -> action.run());
        panel.add(b);
        panel.add(Box.createVerticalStrut(8));
    }

    private void style(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 13, 9, 13));
    }

    private void addExpense() {
        JTextField date = new JTextField(LocalDate.now().toString());
        JComboBox<String> category = new JComboBox<>(new String[]{
                "Food", "Transport", "Shopping", "Bills", "Education", "Health", "Entertainment", "Other"});
        JTextField description = new JTextField();
        JTextField amount = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(date);
        panel.add(new JLabel("Category:"));
        panel.add(category);
        panel.add(new JLabel("Description:"));
        panel.add(description);
        panel.add(new JLabel("Amount (₹):"));
        panel.add(amount);

        if (JOptionPane.showConfirmDialog(this, panel, "Add Expense",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            LocalDate.parse(date.getText().trim());
            double value = Double.parseDouble(amount.getText().trim());
            if (value <= 0 || description.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a description and amount greater than zero.");
                return;
            }
            expenses.add(new Expense(nextId++, date.getText().trim(),
                    (String) category.getSelectedItem(), description.getText().trim(), value));
            saveExpenses();
            refreshDashboard();
            JOptionPane.showMessageDialog(this, "Expense added successfully.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Use the date format YYYY-MM-DD.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount.");
        }
    }

    private void searchExpenses() {
        String query = JOptionPane.showInputDialog(this, "Search by category or description:");
        if (query == null || query.trim().isEmpty()) return;
        query = query.trim().toLowerCase();
        model.setRowCount(0);
        for (Expense x : expenses) {
            if (x.getCategory().toLowerCase().contains(query)
                    || x.getDescription().toLowerCase().contains(query)) addRow(x);
        }
        if (model.getRowCount() == 0) JOptionPane.showMessageDialog(this, "No matching expenses found.");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an expense from the table first.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Delete expense ID " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        expenses.removeIf(x -> x.getId() == id);
        saveExpenses();
        refreshDashboard();
    }

    private void setBudget() {
        String input = JOptionPane.showInputDialog(this, "Enter monthly budget (₹):", budget);
        if (input == null) return;
        try {
            double value = Double.parseDouble(input.trim());
            if (value < 0) throw new NumberFormatException();
            budget = value;
            saveBudget();
            refreshDashboard();
            JOptionPane.showMessageDialog(this, "Monthly budget saved.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid budget amount.");
        }
    }

    private void showSummary() {
        double total = totalSpent();
        double highest = expenses.stream().mapToDouble(Expense::getAmount).max().orElse(0);
        double lowest = expenses.stream().mapToDouble(Expense::getAmount).min().orElse(0);
        double average = expenses.isEmpty() ? 0 : total / expenses.size();
        JOptionPane.showMessageDialog(this,
                "Number of expenses: " + expenses.size()
                + "\nTotal spent: " + money(total)
                + "\nHighest expense: " + money(highest)
                + "\nLowest expense: " + money(lowest)
                + "\nAverage expense: " + money(average)
                + "\nMonthly budget: " + money(budget)
                + "\nBudget remaining this month: " + money(budget - currentMonthTotal()),
                "Expense Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshDashboard() {
        if (model == null) return;
        model.setRowCount(0);
        expenses.stream().sorted(Comparator.comparing(Expense::getDate).reversed()
                .thenComparing(Comparator.comparingInt(Expense::getId).reversed())).forEach(this::addRow);
        totalLabel.setText(money(totalSpent()));
        budgetLabel.setText(money(budget));
        remainingLabel.setText(money(budget - currentMonthTotal()));
        countLabel.setText(String.valueOf(expenses.size()));
    }

    private void addRow(Expense x) {
        model.addRow(new Object[]{x.getId(), x.getDate(), x.getCategory(),
                x.getDescription(), money(x.getAmount())});
    }

    private double totalSpent() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    private double currentMonthTotal() {
        YearMonth current = YearMonth.now();
        return expenses.stream().filter(x -> {
            try { return YearMonth.from(LocalDate.parse(x.getDate())).equals(current); }
            catch (DateTimeParseException ex) { return false; }
        }).mapToDouble(Expense::getAmount).sum();
    }

    private String money(double value) {
        return String.format("\u20B9%.2f", value);
    }

    private void loadExpenses() {
        if (!expenseFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(expenseFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Expense x = Expense.fromFileLine(line);
                if (x != null) {
                    expenses.add(x);
                    nextId = Math.max(nextId, x.getId() + 1);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load expenses: " + ex.getMessage());
        }
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(expenseFile))) {
            for (Expense x : expenses) writer.println(x.toFileLine());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save expenses: " + ex.getMessage());
        }
    }

    private void loadBudget() {
        if (!budgetFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(budgetFile))) {
            String line = reader.readLine();
            if (line != null) budget = Double.parseDouble(line.trim());
        } catch (IOException | NumberFormatException ex) {
            budget = 0;
        }
    }

    private void saveBudget() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(budgetFile))) {
            writer.println(budget);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save budget: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new ExpenseGUI().setVisible(true);
        });
    }
}
