import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

// ============================================================
// 🧁 MODEL CLASSES
// ============================================================

class BakeryItem {
    private String name;
    private double price;
    private int quantity;

    public BakeryItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

// ---------- Singleton Database ----------
class BakeryDatabase {
    private static BakeryDatabase instance;
    private java.util.List<BakeryItem> regularItems;
    private java.util.List<BakeryItem> specialItems;

    private BakeryDatabase() {
        regularItems = new ArrayList<>(Arrays.asList(
                new BakeryItem("Bread", 2.5, 50),
                new BakeryItem("Cake", 15.0, 20),
                new BakeryItem("Cookies", 5.0, 30),
                new BakeryItem("Croissant", 3.0, 25),
                new BakeryItem("Cupcake", 4.0, 40),
                new BakeryItem("Donut", 2.0, 35),
                new BakeryItem("Muffin", 3.5, 28),
                new BakeryItem("Bagel", 2.5, 20),
                new BakeryItem("Brownie", 4.5, 18),
                new BakeryItem("Puff Pastry", 3.0, 22)
        ));

        specialItems = new ArrayList<>(Arrays.asList(
                new BakeryItem("Red Velvet Cake", 25.0, 10),
                new BakeryItem("Cheese Pastry", 20.0, 8),
                new BakeryItem("Chocolate Lava Cake", 30.0, 6),
                new BakeryItem("Fruit Tart", 22.0, 12),
                new BakeryItem("Strawberry Cheesecake", 28.0, 9),
                new BakeryItem("Macarons Box", 35.0, 5),
                new BakeryItem("Tiramisu", 27.0, 7),
                new BakeryItem("Blueberry Danish", 18.0, 10),
                new BakeryItem("Caramel Eclair", 24.0, 6),
                new BakeryItem("Premium Chocolate Cake", 40.0, 4)
        ));
    }

    public static BakeryDatabase getInstance() {
        if (instance == null)
            instance = new BakeryDatabase();
        return instance;
    }

    public java.util.List<BakeryItem> getRegularItems() { return regularItems; }
    public java.util.List<BakeryItem> getSpecialItems() { return specialItems; }
}

// ============================================================
// 🧮 STRATEGY PATTERN
// ============================================================

interface BillingStrategy {
    double calculate(double price, int qty);
}
class RegularBillingStrategy implements BillingStrategy {
    public double calculate(double price, int qty) { return price * qty; }
}

// ============================================================
// 🧭 CONTROLLER
// ============================================================

class BakeryController {
    private BakeryDatabase db = BakeryDatabase.getInstance();

    public java.util.List<BakeryItem> getRegularItems() { return db.getRegularItems(); }
    public java.util.List<BakeryItem> getSpecialItems() { return db.getSpecialItems(); }

    public void updateStock(String itemName, int newQty) {
        for (BakeryItem i : db.getRegularItems())
            if (i.getName().equalsIgnoreCase(itemName)) i.setQuantity(newQty);

        for (BakeryItem i : db.getSpecialItems())
            if (i.getName().equalsIgnoreCase(itemName)) i.setQuantity(newQty);
    }

    public double calculateTotal(java.util.List<BakeryItem> items, BillingStrategy strategy) {
        double total = 0;
        for (BakeryItem i : items) total += strategy.calculate(i.getPrice(), i.getQuantity());
        return total;
    }
}

// ============================================================
// ⚙️ COMMAND PATTERN
// ============================================================

interface Command { void execute(); }

// ============================================================
// 🎨 MAIN VIEW
// ============================================================

class BakeryView extends JFrame {
    private BakeryController controller;
    private JTable regularTable, specialTable;
    private DefaultTableModel regModel, specialModel;
    private JLabel totalLabel;

    public BakeryView(BakeryController controller) {
        this.controller = controller;
        setTitle("Sweet Delights Bakery Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createHeading(), BorderLayout.NORTH);
        add(createTables(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        loadItems();
        setVisible(true);
    }

    private JLabel createHeading() {
        JLabel lbl = new JLabel("Sweet Delights Bakery", JLabel.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 36));
        lbl.setForeground(new Color(120, 0, 80));
        return lbl;
    }

    private JPanel createTables() {
        JPanel panel = new JPanel(new GridLayout(4,1,10,10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Item Name","Unit Price ($)","Available Qty","Purchase Qty"};

        regModel = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){ return c==3;} };
        specialModel = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){ return c==3;} };

        regularTable = new JTable(regModel);
        specialTable = new JTable(specialModel);

        styleTable(regularTable);
        styleTable(specialTable);

        JComboBox<Integer> qtyBox = new JComboBox<>();
        for(int i=0;i<=10;i++) qtyBox.addItem(i);

        regularTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(qtyBox));
        specialTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(qtyBox));

        panel.add(createTitle("Regular Items"));
        panel.add(new JScrollPane(regularTable));
        panel.add(createTitle("Special Items"));
        panel.add(new JScrollPane(specialTable));

        return panel;
    }

    private JLabel createTitle(String text) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(100, 0, 60));
        return lbl;
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tbl.setRowHeight(28);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    private JPanel createButtons() {
        JPanel panel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Grand Total: $0.00", JLabel.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton billBtn = btn("Generate Bill");
        billBtn.addActionListener(e -> new BillCommand(controller,this).execute());

        JButton orderBtn = btn("Place Order");
        orderBtn.addActionListener(e -> new OrderCommand(this).execute());

        JButton stockBtn = btn("Update Stock");
        stockBtn.addActionListener(e -> new StockCommand(controller,this).execute());

        JButton exitBtn = btn("Exit");
        exitBtn.addActionListener(e -> System.exit(0));

        JPanel p = new JPanel(new FlowLayout());
        p.add(billBtn); p.add(orderBtn); p.add(stockBtn); p.add(exitBtn);

        panel.add(totalLabel,BorderLayout.NORTH);
        panel.add(p,BorderLayout.CENTER);
        return panel;
    }

    private JButton btn(String t){
        JButton b=new JButton(t);
        b.setFont(new Font("Segoe UI",Font.BOLD,16));
        b.setBackground(new Color(150,70,160));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(10,20,10,20));
        return b;
    }

    public void loadItems() {
        regModel.setRowCount(0);
        specialModel.setRowCount(0);

        for(BakeryItem i: controller.getRegularItems())
            regModel.addRow(new Object[]{i.getName(),i.getPrice(),i.getQuantity(),0});
        for(BakeryItem i: controller.getSpecialItems())
            specialModel.addRow(new Object[]{i.getName(),i.getPrice(),i.getQuantity(),0});
    }

    public java.util.List<BakeryItem> getPurchasedItems() {
        java.util.List<BakeryItem> list = new ArrayList<>();
        add(regModel,list); add(specialModel,list); return list;
    }

    private void add(DefaultTableModel m,java.util.List<BakeryItem> l){
        for(int i=0;i<m.getRowCount();i++){
            int q=Integer.parseInt(m.getValueAt(i,3).toString());
            if(q>0) l.add(new BakeryItem(m.getValueAt(i,0).toString(),
                    Double.parseDouble(m.getValueAt(i,1).toString()),q));
        }
    }

    public void showBill(String cust,double total){
        totalLabel.setText("Grand Total: $"+String.format("%.2f",total));
        JOptionPane.showMessageDialog(this,"Bill for "+cust+"\nTotal: $"+total,"Bill",JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessage(String m){ JOptionPane.showMessageDialog(this,m); }
}

// ============================================================
// ⚙️ COMMAND CLASSES
// ============================================================

class BillCommand implements Command {
    BakeryController c; BakeryView v;
    BillCommand(BakeryController c,BakeryView v){this.c=c; this.v=v;}

    public void execute(){
        String n=JOptionPane.showInputDialog("Enter Customer Name:");
        if(n==null||n.isEmpty()) return;
        double total=c.calculateTotal(v.getPurchasedItems(), new RegularBillingStrategy());
        v.showBill(n,total);
    }
}

class OrderCommand implements Command {
    BakeryView v;
    OrderCommand(BakeryView v){this.v=v;}

    public void execute(){
        JTextField n=new JTextField(); JTextField a=new JTextField(); JTextField p=new JTextField();
        JPanel panel=new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Customer Name:")); panel.add(n);
        panel.add(new JLabel("Address:")); panel.add(a);
        panel.add(new JLabel("Phone:")); panel.add(p);

        if(JOptionPane.showConfirmDialog(v,panel,"Place Order",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
            v.showMessage("Order placed successfully for "+n.getText());
    }
}

class StockCommand implements Command {
    BakeryController c; BakeryView v;
    StockCommand(BakeryController c,BakeryView v){this.c=c; this.v=v;}

    public void execute(){
        JComboBox<String> cmb=new JComboBox<>();
        for(BakeryItem i:c.getRegularItems()) cmb.addItem(i.getName());
        for(BakeryItem i:c.getSpecialItems()) cmb.addItem(i.getName());

        JTextField q=new JTextField();
        JPanel p=new JPanel(new GridLayout(0,1));
        p.add(new JLabel("Select Item:")); p.add(cmb);
        p.add(new JLabel("New Quantity:")); p.add(q);

        if(JOptionPane.showConfirmDialog(v,p,"Update Stock",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            try{
                c.updateStock(cmb.getSelectedItem().toString(),Integer.parseInt(q.getText()));
                v.loadItems(); v.showMessage("Updated successfully!");
            }catch(Exception e){ v.showMessage("Invalid Value!"); }
        }
    }
}

class WelcomePage extends JFrame {

    public WelcomePage() {
        setTitle("Welcome | Sweet Delights Bakery");
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(210,170,220),
                        0, getHeight(), new Color(110,40,120));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Sweet Delights Bakery");
        title.setFont(new Font("Serif",Font.BOLD,38));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Freshness in Every Bite!");
        tagline.setFont(new Font("Segoe UI",Font.PLAIN,22));
        tagline.setForeground(Color.WHITE);

        JButton start = new JButton("Continue");
        start.setFont(new Font("Segoe UI",Font.BOLD,20));
        start.setBackground(Color.WHITE);
        start.setForeground(new Color(120,0,80));
        start.setBorder(new EmptyBorder(10,20,10,20));
        start.setCursor(new Cursor(Cursor.HAND_CURSOR));

        start.addActionListener(e -> { dispose(); new BakeryView(new BakeryController()); });

        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(15,0,15,0);
        c.gridy=0; bg.add(title,c);
        c.gridy=1; bg.add(tagline,c);
        c.gridy=2; bg.add(start,c);

        add(bg);
        setVisible(true);
    }
}

public class BakeryManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomePage());
    }
}
