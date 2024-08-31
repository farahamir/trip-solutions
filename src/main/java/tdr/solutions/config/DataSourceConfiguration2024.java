package tdr.solutions.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Configuration class for setting up the data source, entity manager factory,
 * and transaction manager specifically for the year 2024.
 *
 * <p>This class configures a separate data source and JPA setup to manage
 * Trip Detail Records (tdrs) for the year 2024. It utilizes HikariCP for
 * connection pooling and Flyway for database migrations.</p>
 *
 * <p>The configuration enables JPA repositories for the `tdr.emobility.charging.solutions.repository._2024`
 * package, and associates the corresponding entity manager and transaction manager beans
 * with these repositories.</p>
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "tdr.solutions.repository._2024",
        entityManagerFactoryRef = "tdr2024EntityManagerFactory",
        transactionManagerRef = "tdr2024TransactionManager"
)
public class DataSourceConfiguration2024 {

    /** Environment instance used to access application properties. */
    private final Environment environment;

    /**
     * Constructs a new {@code DataSourceConfiguration2024} with the specified environment.
     *
     * @param environment the Spring {@link Environment} to access application properties
     */
    public DataSourceConfiguration2024(Environment environment) {
        this.environment = environment;
    }

    /**
     * Creates and configures {@link DataSourceProperties} for the 2024 tdr data source.
     *
     * <p>These properties are loaded from the `spring.datasource.tdr2024` namespace in the
     * application configuration.</p>
     *
     * @return the configured {@link DataSourceProperties} bean
     */
    @Bean
    @ConfigurationProperties("spring.datasource.tdr2024")
    public DataSourceProperties tdr2024DataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Creates and configures the {@link DataSource} for the 2024 tdr database.
     *
     * <p>This method uses the properties defined in {@link #tdr2024DataSourceProperties()}
     * and builds a {@link HikariDataSource}.</p>
     *
     * @return the configured {@link DataSource} bean
     */
    @Bean
    @ConfigurationProperties("spring.datasource.tdr2024.configuration")
    public DataSource tdr2024DataSource() {
        return tdr2024DataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    /**
     * Configures the entity manager factory for the 2024 tdr data source.
     *
     * <p>This method sets up the entity manager factory to use the 2024 tdr data source and
     * also applies Flyway migrations before the factory is fully built.</p>
     *
     * @param builder the {@link EntityManagerFactoryBuilder} used to create the entity manager factory
     * @return the configured {@link LocalContainerEntityManagerFactoryBean} bean
     */
    @Bean(name = "tdr2024EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tdr2024EntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        Flyway.configure()
                .dataSource(tdr2024DataSource())
                .locations(environment.getProperty("spring.datasource.tdr2024.jpa.location"))
                .load()
                .migrate();
        return builder
                .dataSource(tdr2024DataSource())
                .packages("tdr.solutions.model")
                .build();
    }

    /**
     * Configures the transaction manager for the 2024 tdr entity manager factory.
     *
     * <p>This method creates a {@link JpaTransactionManager} associated with the
     * 2024 tdr entity manager factory to handle transactions for the 2024 tdr database.</p>
     *
     * @param tdr2024EntityManagerFactory the entity manager factory used by the transaction manager
     * @return the configured {@link PlatformTransactionManager} bean
     */
    @Bean
    public PlatformTransactionManager tdr2024TransactionManager(
            final @Qualifier("tdr2024EntityManagerFactory") LocalContainerEntityManagerFactoryBean tdr2024EntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(tdr2024EntityManagerFactory.getObject()));
    }
}
