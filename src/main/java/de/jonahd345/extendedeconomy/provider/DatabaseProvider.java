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
    private HikariConfig config;
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
        initConnection();
    }

    public DatabaseProvider(String file) {
        this.file = file;
        initConnection();
    }

    /**
     * Establishes a connection to the database.
     */
    public void initConnection() {
        config = new HikariConfig();

        if (Config.MYSQL.getValueAsBoolean()) {
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
            config.setUsername(user);
            config.setPassword(password);
            config.setPoolName(instanceName);
        } else {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + this.file);
        }

        config.setMinimumIdle(Config.MYSQL.getValueAsBoolean() ? 2 : 1); // Keep a small number of idle connections ready
        config.setMaximumPoolSize(Config.MYSQL.getValueAsBoolean() ? 10 : 1); // Max connections; tune based on DB and app load

        config.setMaxLifetime(1800000); // 30 minutes (shorter than DB's timeout to avoid sudden disconnects)
        config.setConnectionTimeout(30000); // 30 seconds (good default)
        config.setIdleTimeout(600000); // 10 minutes (only if minIdle < maxPoolSize)
        config.setValidationTimeout(5000); // 5 seconds to validate the connection

        config.setConnectionTestQuery("SELECT 1"); // Test query for SQL
        config.setLeakDetectionThreshold(2000); // Log if connection is not returned in 2s (helps detect leaks)

        dataSource = new HikariDataSource(config);
    }

    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        try {
            if (dataSource != null) {
                dataSource.close();
                dataSource = null;
            }
        } catch (Exception e) {
            logger.severe("Failed to disconnect from database: " + e.getMessage());
        }
    }

    /**
     * Executes an update query (INSERT, UPDATE, DELETE).
     * @param query the SQL query to execute
     */
    public void update(String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (Exception e) {
            logger.severe("Failed execute update on database: " + e.getMessage());
        }
    }

    /**
     * Checks if the specified table exists in the database.
     * @param tableName the name of the table to check
     * @return true if the table exists, false otherwise
     */
    public boolean isTablePresent(String tableName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM information_schema.tables WHERE table_schema = '" + database + "' AND table_name = '" + tableName + "';")) {

            if (resultSet.next()) {
                return resultSet.getInt("total") == 1;
            }
        } catch (SQLException e) {
            logger.severe("Error checking if table exists: " + e.getMessage());
        }
        return false;
    }

    /**
     * Provides a connection to the database.
     * @return a {@link Connection} from the HikariDataSource or null if an error occurs
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            return null;
        }
    }
}
