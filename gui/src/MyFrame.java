import javafx.scene.control.ComboBox;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends JFrame
{
    private static final String TITLE = "Proiect GUI Swing - Restaurant";
    private static final int WIDTH = 1720;
    private static final int HEIGHT = 900;
    private static final Font TITLE_FONT = new Font("Calibri", Font.PLAIN, 40);
    private static final Font TABLE_FONT = new Font("Calibri", Font.PLAIN, 30);
    private static final Font HEADER_FONT = new Font("Calibri", Font.PLAIN, 40);
    private static final Color TITLE_COLOR = new Color(100, 200, 100);

    // Componente
    private DataBaseManager db;
    private JPanel centerPanel;
    private JComboBox<String> cb;
    private JButton go;
    private JButton proc;

    public MyFrame()
    {
        db = new DataBaseManager();
        if (!db.isConnected())
        {
            JOptionPane.showMessageDialog(this,
                    "Error connecting to database!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initializeFrame();
        createComponents();
        layoutComponents();
        finalizeFrame();
    }

    private void initializeFrame()
    {
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));
        this.getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void createComponents()
    {
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        proc = new JButton();
        cb = new JComboBox<>();
        cb.setFont(TABLE_FONT);
        cb.addItem("Select query...");
        cb.addItem("Clients");
        cb.addItem("Products");
        cb.addItem("Couriers");
        cb.addItem("Orders");
        cb.addItem("3a");
        cb.addItem("3b");
        cb.addItem("4a");
        cb.addItem("4b");
        cb.addItem("5a");
        cb.addItem("5b");
        cb.addItem("6a");
        cb.addItem("6b");
        go = new JButton("Go");
        go.setFont(TABLE_FONT);
        go.addActionListener(e ->
        {
            String selectedItem = cb.getSelectedItem().toString();
            if (selectedItem != null)
            {
                String[][] data = null;
                String[] columns = null;
                switch (selectedItem)
                {
                    case "3a":
                        data = db.get3aData();
                        columns = db.get3aColumnNames();
                        selectedItem = "Orders paid in cash, descending by total amount";
                        break;
                    case "3b":
                        data = db.get3bData();
                        columns = db.get3aColumnNames();
                        selectedItem = "Orders currently in delivery, ascending by total amount";
                        break;
                    case "4a":
                        data = db.get4aData();
                        columns = db.get4aColumnNames();
                        selectedItem = "Order history";
                        break;
                    case "4b":
                        data = db.get4bData();
                        columns = db.get4bColumnNames();
                        selectedItem = "Products with same name, but different category";
                        break;
                    case "5a":
                        data = db.get5aData();
                        columns = db.get5aColumnNames();
                        selectedItem = "Clients who have placed at least an order";
                        break;
                    case "5b":
                        data = db.get5bData();
                        columns = db.get5bColumnNames();
                        selectedItem = "Products never ordered";
                        break;
                    case "6a":
                        data = db.get6aData();
                        columns = db.get6aColumnNames();
                        selectedItem = "Total value per client in 2025";
                        break;
                    case "6b":
                        data = db.get6bData();
                        columns = db.get6bColumnNames();
                        selectedItem = "Top couriers with total amount delivered in 2025";
                        break;
                    case "Clients":
                        data = db.getClientiData();
                        columns = db.getClientiColumns();
                        selectedItem = "Clients";
                        break;
                    case "Products":
                        data = db.getProdusData();
                        columns = db.getProdusColumns();
                        selectedItem = "Products";
                        break;
                    case "Orders":
                        data = db.getComenziData();
                        columns = db.getComenziColumns();
                        selectedItem = "Orders";
                        break;
                    case "Couriers":
                        data = db.getCurieriData();
                        columns = db.getCurieriColumns();
                        selectedItem = "Couriers";
                        break;
                }
                if (data != null && columns != null)
                {
                    showTable(data, columns, selectedItem);
                } else
                {
                    JOptionPane.showMessageDialog(MyFrame.this,
                            "Coudn't handle data for " + selectedItem + ".",
                            "Error DB", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void layoutComponents()
    {
        JPanel northPanel = createNorthPanel();
        JPanel southPanel = createSouthPanel();
        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }


    private JPanel createNorthPanel()
    {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(Color.WHITE);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = createTitleLabel();
        JPanel buttonPanel = createButtonPanel();

        northPanel.add(titleLabel);
        northPanel.add(Box.createVerticalStrut(15));
        northPanel.add(buttonPanel);

        return northPanel;
    }

    private JPanel createSouthPanel()
    {
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Categorie");
        label.setFont(TABLE_FONT);
        JTextField textField = new JTextField(5);
        textField.setPreferredSize(new Dimension(200, 40));
        textField.setFont(TABLE_FONT);
        JButton btnGo = new JButton("Go");
        btnGo.setFont(TABLE_FONT);

        btnGo.addActionListener(e->{
            String categ = textField.getText().trim();
            if(categ.isEmpty() == false)
            {
                int rez = db.getTotalVandutCategorie(categ);
                JOptionPane.showMessageDialog(this, "Total products sold from "+ categ + " is: "+ rez);
            }
        });
        southPanel.add(label);
        southPanel.add(textField);
        southPanel.add(btnGo);
        JPanel buttons = new JPanel(new GridLayout(3, 3, 10, 10));
        return southPanel;
    }

    private JLabel createTitleLabel()
    {
        JLabel label = new JLabel("Proiect BD Tema 4B ");
        label.setForeground(TITLE_COLOR);
        label.setFont(TITLE_FONT);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cb);
        buttonPanel.add(go);
        return buttonPanel;
    }

    public void showTable(String[][] data, String[] columnNames, String tableName)
    {
        if (data == null || data.length == 0)
        {
            JOptionPane.showMessageDialog(this,
                    "No data to show!",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JLabel headerLabel = new JLabel(tableName);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(new Color(50, 50, 50));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTable table = new JTable(data, columnNames);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        table.setFont(TABLE_FONT);
        table.setRowHeight(60);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(200, 200, 200));

        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));

        centerPanel.removeAll();
        centerPanel.add(headerLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void finalizeFrame()
    {
        this.setSize(WIDTH, HEIGHT);
        this.setMinimumSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}