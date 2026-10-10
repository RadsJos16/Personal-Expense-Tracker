public class Expense {
    private final int id;
    private final String date, category, description;
    private final double amount;

    public Expense(int id, String date, String category, String description, double amount) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }

    public String toFileLine() {
        return id + "\t" + clean(date) + "\t" + clean(category) + "\t" + clean(description) + "\t" + amount;
    }

    private String clean(String value) {
        return value.replace("\t", " ").replace("\n", " ").replace("\r", " ");
    }

    public static Expense fromFileLine(String line) {
        String[] parts = line.split("\t", 5);
        if (parts.length != 5) return null;
        try {
            return new Expense(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                    Double.parseDouble(parts[4]));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
