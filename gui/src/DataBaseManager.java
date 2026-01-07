import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

public class DataBaseManager
{
    private Connection connection;
    private static final String url = "jdbc:postgresql://localhost:5432/postgres?currentSchema=public";
    private static final String user = "postgres";
    private String pass = System.getenv("DB_PASSWORD");

    public DataBaseManager()
    {
        try
        {
            connectToDatabase();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void connectToDatabase()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection established!");

        } catch (ClassNotFoundException e)
        {
            System.err.println("Driver PostgreSQL not found!");
            e.printStackTrace();
        } catch (SQLException e)
        {
            System.err.println("Error connecting to database!");
            e.printStackTrace();
        }
    }

    public boolean isConnected()
    {
        return connection != null;
    }

    public String[] get3aColumnNames()
    {
        return new String[]{"Id comanda", "Id client", "Id curier", "Data", "Total", "Status", "Metoda PLata", "Adresa Livrare"};
    }

    public String[][] get3aData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "SELECT id_com, id_client, id_curier, data_comanda, total, status, metoda_plata, adresa_livrare " +
                "FROM comanda c " +
                "WHERE c.metoda_plata = 'cash' " +
                "ORDER BY total DESC";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = {
                        String.valueOf(rs.getInt("id_com")),
                        String.valueOf(rs.getInt("id_client")),
                        rs.getString("id_curier") != null ? String.valueOf(rs.getInt("id_curier")) : "N/A",
                        rs.getString("data_comanda"),
                        String.format("%.2f RON", rs.getDouble("total")),
                        rs.getString("status") != null ? rs.getString("status") : "-",
                        rs.getString("metoda_plata") != null ? rs.getString("metoda_plata") : "-",
                        rs.getString("adresa_livrare")
                };
                dataList.add(row);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[][] get3bData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "select id_com, id_client, id_curier, data_comanda, total, status, metoda_plata, adresa_livrare " +
                "from comanda c " +
                "where c.status = 'in curs de livrare'" +
                "order by total;";
        try
        {
            if (connection == null || connection.isClosed())
            {
                System.err.println("Error connecting to database!");
                return dataList.toArray(new String[0][]);
            }
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = {
                        String.valueOf(rs.getInt("id_com")),
                        rs.getString("id_client"),
                        rs.getString("id_curier") != null ? rs.getString("id_curier") : "N/A",
                        rs.getString("data_comanda"),
                        String.format("%.2f RON", rs.getDouble("total")),
                        rs.getString("status") != null ? rs.getString("status") : "-",
                        rs.getString("metoda_plata") != null ? rs.getString("metoda_plata") : "-",
                        rs.getString("adresa_livrare")
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[] get4aColumnNames()
    {
        return new String[]{"Id comanda", "Id client", "Id curier", "Data", "Total"};
    }

    public String[][] get4aData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = " select c.id_com , c.id_client , c.id_curier , c.data_comanda , c.total " +
                "from comanda c left join curier c2 on c.id_curier = c2.id_curier " +
                "where c.status = 'livrata';";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = {
                        String.valueOf(rs.getInt("id_com")),
                        String.valueOf(rs.getInt("id_client")),
                        rs.getString("id_curier") != null ? String.valueOf(rs.getInt("id_curier")) : "N/A",
                        rs.getString("data_comanda"),
                        String.format("%.2f RON", rs.getDouble("total")),
                };
                dataList.add(row);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[] get4bColumnNames()
    {
        return new String[]{"Id produs", "Denumire", "Categorie"};
    }

    public String[][] get4bData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "select p1.id_prod, p1.denumire, p1.categorie " +
        " from produs p1 "+
        " join produs p2 on p1.denumire = p2.denumire "+
        " where p1.id_prod <> p2.id_prod "+
        " and p1.categorie <> p2.categorie "+
        " order by p1.denumire;";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = {
                        String.valueOf(rs.getInt("id_prod")),
                        rs.getString("denumire"),
                        rs.getString("categorie"),
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain products!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[] get5aColumnNames()
    {
        return new String[]{"Id client"};
    }

    public String[][] get5aData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "select distinct c.id_client \n" +
                "from client c\n" +
                "where exists (\n" +
                "\tselect * \n" +
                "\tfrom comanda c2 \n" +
                "\twhere c.id_client = c2.id_client\n" +
                ");";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getInt("id_client"))
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain clients!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[] get5bColumnNames()
    {
        return new String[]{"Denumire", "Id produs"};
    }

    public String[][] get5bData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "select p.denumire, p.id_prod \n" +
                "from produs p\n" +
                "where not exists (\n" +
                "\tselect * \n" +
                "\tfrom comanda_item ci \n" +
                "\twhere ci.id_prod = p.id_prod \n" +
                ");";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getString("denumire")),
                        rs.getString("id_prod"),
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[] get6aColumnNames()
    {
        return new String[]{"Client", "Total comenzi 2025"};
    }

    public String[][] get6aData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "select c.nume, sum(c2.total) as total_comenzi_2025\n" +
                "from client c join comanda c2 on c.id_client = c2.id_client \n" +
                "group by c.nume;";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getString("nume")),
                        rs.getString("total_comenzi_2025"),
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[] get6bColumnNames()
    {
        return new String[]{"Curier", "Suma curier 2025"};
    }

    public String[][] get6bData()
    {
        List<String[]> dataList = new ArrayList<>();

        String query = "select c2.nume, sum(c.total) as suma_curier\n" +
                "from comanda c join curier c2 on c.id_curier = c2.id_curier\n" +
                "group by c.total, c2.nume\n" +
                "order by c.total desc;";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getString("nume")),
                        rs.getString("suma_curier"),
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }

        return dataList.toArray(new String[0][]);
    }

    public String[][] getClientiData()
    {
        List<String[]> dataList = new ArrayList<>();
        String query = "select c.id_client, c.nume, c.telefon, c.email" + " from client c;";
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getInt("id_client")),
                        rs.getString("nume"),
                        rs.getString("telefon"),
                        rs.getString("email")
                };
                dataList.add(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }
        return dataList.toArray(new String[0][]);
    }

    public String[] getClientiColumns()
    {
        return new String[]{"ID", "Nume", "Nr de telefon", "email"};
    }

    public String[][] getProdusData()
    {
        List<String[]> dataList = new ArrayList<>();
        String query = "select id_prod, denumire, categorie, pret, disponibil from produs;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getInt("id_prod")),
                        rs.getString("denumire"),
                        rs.getString("categorie"),
                        String.format("%.2f", rs.getDouble("pret")),
                        rs.getBoolean("disponibil") ? "Da" : "Nu"
                };
                dataList.add(row);
            }
        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }
        return dataList.toArray(new String[0][]);
    }

    public String[] getProdusColumns()
    {
        return new String[]{"ID", "Denumire", "Categorie", "Pret (RON)", "Disponibil"};
    }

    public String[][] getComenziData()
    {
        List<String[]> dataList = new ArrayList<>();
        String query = "SELECT id_com, data_comanda, total, status, metoda_plata, adresa_livrare FROM comanda;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getInt("id_com")),
                        rs.getString("data_comanda"),
                        String.format("%.2f RON", rs.getDouble("total")),
                        rs.getString("status"),
                        rs.getString("metoda_plata"),
                        rs.getString("adresa_livrare")
                };
                dataList.add(row);
            }
        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }
        return dataList.toArray(new String[0][]);
    }

    public String[] getComenziColumns()
    {
        return new String[]{"ID", "Data", "Total", "Status", "Plata", "Adresa Livrare"};
    }

    public String[][] getCurieriData()
    {
        List<String[]> dataList = new ArrayList<>();
        String query = "SELECT id_curier, nume, telefon, tura, activ FROM curier;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {
                String[] row = new String[]{
                        String.valueOf(rs.getInt("id_curier")),
                        rs.getString("nume"),
                        rs.getString("telefon"),
                        rs.getString("tura"),
                        rs.getBoolean("activ") ? "Activ" : "Inactiv"
                };
                dataList.add(row);
            }
        } catch (SQLException e)
        {
            System.err.println("Error: Could not obtain data!");
            e.printStackTrace();
        }
        return dataList.toArray(new String[0][]);
    }

    public String[] getCurieriColumns()
    {
        return new String[]{"ID", "Nume", "Telefon", "Tura", "Status"};
    }

    public int getTotalVandutCategorie(String categorie)
    {
        int total = 0;
        String query = "Select get_total_vandut_categorie(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query))
        {
            pstmt.setString(1, categorie);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                total = rs.getInt(1);
            }
        } catch (SQLException e)
        {
                e.printStackTrace();
        }
        return total;
    }
}
