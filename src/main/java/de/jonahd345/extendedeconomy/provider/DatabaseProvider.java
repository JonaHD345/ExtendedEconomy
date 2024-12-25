package de.jonahd345.extendedeconomy.provider;

import java.sql.*;

public class DatabaseProvider {
    private String host;

    private String port;

    private String user;

    private String password;

    private String database;

    private Connection connection;

    public DatabaseProvider(String host, String port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.connect();
    }

    public void connect() {
        if (!isConnected()) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database +
                        "?autoReconnect=true", this.user, this.password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (this.connection == null)
            return;
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void update(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ResultSet getResult(String qry) {
        if (isConnected()) {
            try {
                return this.connection.createStatement().executeQuery(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void checkDatabase() {
        ResultSet rs = this.getResult("SELECT * FROM extendedeconomy_coins");
        if (!this.isConnected() || rs == null) {
            this.connect();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isConnected() {
        return this.getConnection() != null;
    }
}
