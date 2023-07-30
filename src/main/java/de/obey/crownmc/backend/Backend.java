package de.obey.crownmc.backend;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 20:43

*/

import com.zaxxer.hikari.HikariDataSource;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public final class Backend {

    @Getter
    private final HikariDataSource hikariDataSource = new HikariDataSource();

    public Backend(final ServerConfig serverConfig) {

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

        final String jdbc = "jdbc:mysql://";

        if(password.length() > 0)
            hikariDataSource.setPassword(password);

        hikariDataSource.setUsername(username);
        hikariDataSource.setJdbcUrl(jdbc + host + "/" + database + "?autoReconnect=true");
        hikariDataSource.setMaximumPoolSize(20);
        hikariDataSource.setMinimumIdle(100);

        messageUtil.log("Connecting to §f-> §e§o" + username + "@" + host + "/" +database + " pw='" + password + "'");

        try {
            hikariDataSource.getConnection();
            messageUtil.log("Mysql conncted ...");

            createTables();

        } catch (final SQLException exception) {
            serverConfig.setWhitelist(true);
            messageUtil.log("MYSQL IS NOT SETUP, MAINTANANCE-MODE HAS BEEN ACTIVATED ...");
        }
    }

    private void createTables() {
        execute("CREATE TABLE IF NOT EXISTS users(id bigint, uuid text, name text, money bigint, crowns bigint, kills bigint, deaths bigint, bounty bigint, level bigint, xp bigint, killstreak bigint, killstreakrecord bigint, elopoints bigint, votes bigint, playtime bigint, destroyedBlocks bigint, destroyedEventBlocks bigint)");
        execute("CREATE TABLE IF NOT EXISTS clans(name text, leader text, trophies int, kills int, deaths int, xp int, level int);");
        System.out.println("CREATED TABLES");
    }

    public void execute(final String command) {
        try (final Connection connection = hikariDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(command)){

            preparedStatement.execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ResultSet getResultSet(String command) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {
            connection = hikariDataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            closeAfter(connection, statement, resultSet);

            return resultSet;
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getString(String table, String what, String type, String typeValue) {

        try {
            final ResultSet resultSet = getResultSet("SELECT * FROM " + table + " WHERE " + type + "= '" + typeValue + "'");

            if(resultSet == null)
                return "null";

            if (resultSet.next())
                return resultSet.getString(what);

        } catch (final SQLException ignored) {}

        return "null";
    }

    public Long getLong(String table, String what, String type, String typeValue) {
        try (final ResultSet resultSet = getResultSet("SELECT * FROM " + table + " WHERE " + type + "= '" + typeValue + "'");){

            if(resultSet == null)
                return -1L;

            if (resultSet.next())
                return resultSet.getLong(what);

        } catch (final SQLException ignored) {}

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
