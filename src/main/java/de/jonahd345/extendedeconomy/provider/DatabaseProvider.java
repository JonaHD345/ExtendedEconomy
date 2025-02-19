package de.jonahd345.extendedeconomy.provider;

import de.jonahd345.extendedeconomy.config.Config;
import lombok.Getter;

import java.sql.*;

/**
 * This class provides methods to manage the database connection and execute queries.
 */
public class DatabaseProvider {
    private String host;
    private String port;
    private String user;
    private String password;
    private String database;
    private String file;
    @Getter
    private Connection connection;

    /**
     * Constructor to initialize the database connection parameters and connect to the database.
     * @param host the database host
     * @param port the database port
     * @param user the database user
     * @param password the database password
     * @param database the database name
     */
    public DatabaseProvider(String host, String port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.connect();
    }

    public DatabaseProvider(String file) {
        this.file = file;
        this.connect();
    }

    /**
     * Establishes a connection to the database.
     */
    public void connect() {
        if (!isConnected()) {
            try {
                if (Config.MYSQL.getValueAsBoolean()) {
                    this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database +
                            "?autoReconnect=true", this.user, this.password);
                } else {
                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disconnects from the database.
     */
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

    /**
     * Executes an update query (INSERT, UPDATE, DELETE).
     * @param query the SQL query to execute
     */
    public void update(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Executes a SELECT query and returns the result set.
     * @param qry the SQL query to execute
     * @return the result set of the query
     */
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

    /**
     * Checks the database connection and reconnects if necessary.
     */
    public void checkDatabase() {
        ResultSet rs = this.getResult("SELECT * FROM extendedeconomy_coins");
        if (!this.isConnected() || rs == null) {
            this.connect();
        }
    }

    /**
     * Checks if the connection to the database is established.
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return this.getConnection() != null;
    }
}
