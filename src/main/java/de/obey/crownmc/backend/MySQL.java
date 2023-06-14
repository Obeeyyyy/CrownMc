package de.obey.crownmc.backend;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 20:43

*/

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class MySQL {

    @Getter
    private HikariDataSource hikariDataSource;

    public MySQL(final ServerConfig serverConfig) {

        final String host = serverConfig.getHost(),
                username = serverConfig.getUsername(),
                database = serverConfig.getDatabase(),
                password = serverConfig.getPassword();

        final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        if (host.equalsIgnoreCase("change")) {
            serverConfig.setWhitelist(true);
            messageUtil.log("MYSQL IS NOT SETUP, MAINTANANCE-MODE HAS BEEN ACTIVATED ...");
            return;
        }

        final HikariConfig hikariConfig = new HikariConfig();
        final String jdbc = "jdbc:mysql://";

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setJdbcUrl(jdbc + host + "/" + database + "?autoReconnect=true");
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(100);

        hikariDataSource = new HikariDataSource(hikariConfig);

        try {
            hikariDataSource.getConnection();
            messageUtil.log("Mysql conncted ...");

            createTables();

        } catch (final SQLException e) {
            serverConfig.setWhitelist(true);
            messageUtil.log("MYSQL IS NOT SETUP, MAINTANANCE-MODE HAS BEEN ACTIVATED ...");
        }
    }

    private void createTables() {
        execute("CREATE TABLE IF NOT EXISTS users(id int, uuid text, money bigint, kills int, deaths int, bounty bigint, level int, xp int, killstreak int, killstreakrecord int, elopoints int, votes int, playtime bigint, destroyedBlocks BIGINT, destroyedEventBlocks BIGINT)");
        System.out.println("CREATED TABLE");
    }

    public void execute(final String command) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = hikariDataSource.getConnection();
            preparedStatement = connection.prepareStatement(command);

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ResultSet getResultSet(String command) {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = hikariDataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            closeAfter(connection, statement, resultSet);

            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }


    public String getString(String table, String what, String type, String typeValue) {

        ResultSet resultSet = null;

        try {
            resultSet = getResultSet("SELECT * FROM " + table + " WHERE " + type + "= '" + typeValue + "'");

            if (resultSet.next())
                return resultSet.getString(what);

        } catch (SQLException ignored) {
        }

        return "null";
    }

    public Long getLong(String table, String what, String type, String typeValue) {

        ResultSet resultSet = null;

        try {
            resultSet = getResultSet("SELECT * FROM " + table + " WHERE " + type + "= '" + typeValue + "'");

            if (resultSet.next())
                return resultSet.getLong(what);

        } catch (SQLException ignored) {
        }

        return -1L;
    }

    private void closeAfter(final Connection connection, final Statement statement, final ResultSet resultSet) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(CrownMain.getInstance(), 5);
    }

}
