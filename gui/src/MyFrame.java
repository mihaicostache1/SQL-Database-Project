import javafx.scene.control.ComboBox;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
    private JTextField search;
    private JButton searchbtn;

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
        cb.addItem("Total vandut per categorie - UDF");

        search = new JTextField(15);
        search.setFont(TABLE_FONT);
        search.setBackground(Color.WHITE);
        search.setToolTipText("Enter search term");

        searchbtn = new JButton("Filtreaza");
        searchbtn.setFont(TABLE_FONT);
        searchbtn.addActionListener(e ->
        {
            String selectedTable = cb.getSelectedItem().toString();
            String searchText = search.getText().trim();
            if (searchText.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Please enter search term");
                return;
            }
            String[][] data = db.searchInTable(selectedTable, searchText);
            if(data == null)
            {
                JOptionPane.showMessageDialog(this, "Cautarea este disponibila doar pentru tabelele principale!");
                return;
            }
            String[] columns = null;
            switch(selectedTable){
                case "Clients" : columns = db.getClientiColumns(); break;
                case "Products" : columns = db.getProdusColumns(); break;
                case "Couriers" : columns = db.getCurieriColumns(); break;
                case "Orders" : columns =db.getComenziColumns(); break;
            }
            if(data.length>0 && columns!=null)
            {
                showTable(data, columns, "Rezultate filtrare pentru: "+searchText);
            }
            else JOptionPane.showMessageDialog(this, "Nu s-au gasit rezultate pentru "+ searchText+"!");
        });

        go = new JButton("Go");
        go.setFont(TABLE_FONT);
        go.addActionListener(e ->
        {
            String selectedItem = cb.getSelectedItem().toString();
            if (selectedItem.equals("Total vandut per categorie - UDF"))
            {
                rezolvaUDF();
                return;
            }
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

    private void rezolvaUDF()
    {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        JLabel label = new JLabel("Introduce categoria:");
        label.setFont(TABLE_FONT);
        JTextField input = new JTextField();
        input.setFont(TABLE_FONT);
        input.setPreferredSize(new Dimension(400, 50));
        panel.add(label);
        panel.add(input);

        int result = JOptionPane.showConfirmDialog(this, panel, "Calculeaza nr produselor vandute dintr-o categorie", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION)
        {
            String category = input.getText().trim();
            if (!category.equals(""))
            {
                int total = db.getTotalVandutCategorie(category);
                if (total == -1)
                {
                    JLabel errorLabel = new JLabel("Categorie inexistenta!");
                    errorLabel.setFont(TABLE_FONT);
                    errorLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(this, errorLabel, "Error", JOptionPane.ERROR_MESSAGE);
                } else
                {
                    JLabel resultLabel = new JLabel("Produse vandute din categoria " + category + " este: " + total);
                    resultLabel.setFont(TABLE_FONT);
                    JOptionPane.showMessageDialog(this, resultLabel, "Rezultat UDF", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private void layoutComponents()
    {
        JPanel northPanel = createNorthPanel();
        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
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
        buttonPanel.add(Box.createHorizontalStrut(30));
        JLabel l = new JLabel("Filtreaza:");
        l.setFont(TABLE_FONT);
        buttonPanel.add(l);
        buttonPanel.add(search);
        buttonPanel.add(searchbtn);
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
        Object[][] processedData = new Object[data.length][columnNames.length];

        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < columnNames.length; j++)
            {
                String value = data[i][j];
                processedData[i][j] = parseValue(value);
            }
        }
        DefaultTableModel model = new DefaultTableModel(processedData, columnNames)
        {
            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                if (getRowCount() > 0 && getValueAt(0, columnIndex) != null)
                {
                    return getValueAt(0, columnIndex).getClass();
                }
                return Object.class;
            }
        };
        JLabel headerLabel = new JLabel(tableName);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(new Color(50, 50, 50));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTable table = new JTable(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Integer.class, centerRenderer);
        table.setDefaultRenderer(Double.class, centerRenderer);
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

    private Object parseValue(String value)
    {
        if (value == null) return "";
        try
        {
            // Încercăm să vedem dacă e Integer
            return Integer.parseInt(value);
        } catch (NumberFormatException e1)
        {
            try
            {
                // Dacă nu e Integer, încercăm Double
                return Double.parseDouble(value);
            } catch (NumberFormatException e2)
            {
                // Dacă nu e niciunul, rămâne String
                return value;
            }
        }
    }

    private void finalizeFrame()
    {
        this.setSize(WIDTH, HEIGHT);
        this.setMinimumSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}