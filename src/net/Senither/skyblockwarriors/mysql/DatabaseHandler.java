package net.Senither.skyblockwarriors.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.entity.Player;

public class DatabaseHandler
{

    private SkyblockWarriors _plugin;
    protected static String connector = "jdbc:mysql://";
    protected static String username, password;

    public DatabaseHandler(SkyblockWarriors plugin)
    {
        _plugin = plugin;

        String host = _plugin.getConfig().getString("mysql.host");
        int port = _plugin.getConfig().getInt("mysql.port");
        String database = _plugin.getConfig().getString("mysql.database");

        connector += host + ":" + port + "/" + database;

        username = _plugin.getConfig().getString("mysql.username");
        password = _plugin.getConfig().getString("mysql.password");

        createTables();
    }

    public Connection openConnection()
    {
        try {
            Connection con = DriverManager.getConnection(connector, username, password);
            return con;
        } catch (SQLException e) {
            return null;
        }
    }

    public Boolean closeConnection(Connection con)
    {
        try {
            con.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void createTables()
    {
        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `tokens` (`id` int(11) NOT NULL AUTO_INCREMENT, `username` varchar(22) NOT NULL, `eco` int(11) NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection(connection);
    }

    public void updatePlayer(String username, int update, char method)
    {
        String rowName = "eco";
        if (method == '+') {
            _plugin.playerEco.put(username, _plugin.playerEco.get(username) + update);
        } else {
            _plugin.playerEco.put(username, _plugin.playerEco.get(username) - update);
            if (_plugin.playerEco.get(username) <= 0) {
                method = ' ';
                update = 0;
                rowName = "";
            }
        }

        String query = "UPDATE `tokens` SET `eco` = (" + rowName + " " + method + update + ") WHERE `username` = '" + username + "'";

        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection(connection);
    }

    public boolean playerPayment(Player p, double amout)
    {
        double playerBalance = _plugin.playerEco.get(p.getName()) + 0.000000001;

        if (playerBalance <= amout) {
            String balance = "" + playerBalance;
            String[] array = balance.split("\\.");

            _plugin.chatManager.sendMessage(p, "&cThis item cost &e" + amout + " tokens &cbut you only have &e" + array[0] + "&c!");
            p.closeInventory();
            return false;
        }

        updatePlayer(p.getName(), (int) amout, '-');
        return true;
    }

    public void giveReward(String username)
    {
        String query = "UPDATE `tokens` SET `eco` = (eco + 10) WHERE `username` = '" + username + "'";

        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection(connection);
    }

    public int getPlayerBalance(String username)
    {
        String query = "SELECT `eco` FROM `tokens` WHERE username = '" + username + "';";

        Connection connection = openConnection();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt("eco");
            } else {
                query = "INSERT INTO `tokens` (username, eco) VALUES('" + username + "', 0)";
                stmt.executeUpdate(query);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
