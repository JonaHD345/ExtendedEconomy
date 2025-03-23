package de.jonahd345.extendedeconomy.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.jonahd345.extendedeconomy.config.Config;
import lombok.Getter;

import java.sql.*;
import java.util.logging.Logger;

/**
 * This class provides methods to manage the database connection with HikariCP and execute queries.
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
    @Getter
    private HikariDataSource dataSource;
    private Logger logger;
    private String instanceName;

    /**
     * Constructor to initialize the database connection parameters and connect to the database.
     * @param host the database host
     * @param port the database port
     * @param user the database user
     * @param password the database password
     * @param database the database name
     */
    public DatabaseProvider(String host, String port, String user, String password, String database, Logger logger, String instanceName) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.logger = logger;
        this.instanceName = instanceName;
        connect();
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
            HikariConfig config = new HikariConfig();

            if (Config.MYSQL.getValueAsBoolean()) {
                config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC");
                config.setUsername(user);
                config.setPassword(password);
                config.setPoolName(instanceName);
            } else {
                config.setDriverClassName("org.sqlite.JDBC");
                config.setJdbcUrl("jdbc:sqlite:" + this.file);
            }

            config.setMinimumIdle(1);
            config.setMaximumPoolSize(10);
            config.setMaxLifetime(2700000);
            config.setConnectionTimeout(5000);

            try {
                dataSource = new HikariDataSource(config);
                connection = dataSource.getConnection();
            } catch (SQLException e) {
                logger.severe("Failed to connect to database: " + e.getMessage());
            }
        }
    }

    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                logger.severe("Failed to disconnect from database: " + e.getMessage());
            }
        }
    }

    /**
     * Executes an update query (INSERT, UPDATE, DELETE).
     * @param query the SQL query to execute
     */
    public void update(String query) {
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.severe("Failed execute update on database: " + e.getMessage());
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
                logger.severe("Failed execute query on database: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Checks if the specified table exists in the database.
     * @param tableName the name of the table to check
     * @return true if the table exists, false otherwise
     */
    public boolean isTablePresent(String tableName) {
        try (ResultSet resultSet = this.getResult("SELECT COUNT(*) AS total FROM information_schema.tables WHERE table_schema = '" + database + "' AND table_name = '" + tableName + "';")) {
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt("total") == 1;
            }
        } catch (SQLException e) {
            logger.severe("Error checking if table exists: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the connection to the database is established.
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return this.getConnection() != null;
    }
}
