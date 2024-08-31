package tdr.solutions.config;

import tdr.solutions.model.TripDetailRecordEntity;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Configuration class for setting up the 2023 data source, entity manager, and transaction manager.
 *
 * <p>This class configures the data source, entity manager factory, and transaction manager
 * for managing Trip Detail Records (TDRs) for the year 2023. It integrates with Flyway
 * for database migrations and uses HikariCP as the connection pool.</p>
 *
 * <p>The configuration is marked as primary, indicating that it should be used by default
 * in cases where multiple data sources are configured.</p>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "tdr.solutions.repository._2023",
        entityManagerFactoryRef = "tdr2023EntityManagerFactory",
        transactionManagerRef= "tdr2023TransactionManager"
)
public class DataSourceConfiguration2023 {

    private final Environment environment;

    /**
     * Constructs a new {@code DataSourceConfiguration2023} with the specified {@link Environment}.
     *
     * @param environment the environment object used to retrieve properties from the application configuration
     */
    public DataSourceConfiguration2023(Environment environment) {
        this.environment = environment;
    }

    /**
     * Creates and configures the {@link DataSourceProperties} bean for the 2023 tdr data source.
     *
     * @return the configured {@link DataSourceProperties} instance
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.tdr2023")
    public DataSourceProperties tdr2023DatasourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Creates and configures the 2023 tdr {@link DataSource} bean using HikariCP as the connection pool.
     *
     * @return the configured {@link DataSource} instance
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.tdr2023.configuration")
    public DataSource tdr2023DataSource() {
        return tdr2023DatasourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    /**
     * Configures the 2023 tdr {@link LocalContainerEntityManagerFactoryBean} bean,
     * initializing Flyway for database migrations and setting the base package
     * for JPA entities.
     *
     * @param builder the {@link EntityManagerFactoryBuilder} used to create the entity manager factory
     * @return the configured {@link LocalContainerEntityManagerFactoryBean} instance
     */
    @Primary
    @Bean(name = "tdr2023EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tdr2023EntityManagerFactory(EntityManagerFactoryBuilder builder) {
        // Flyway migration
        Flyway.configure()
                .dataSource(tdr2023DataSource())
                .locations(environment.getProperty("spring.datasource.tdr2023.jpa.location"))
                .load()
                .migrate();

        return builder
                .dataSource(tdr2023DataSource())
                .packages(TripDetailRecordEntity.class)
                .build();
    }

    /**
     * Configures the 2023 tdr {@link PlatformTransactionManager} bean,
     * setting it up with the specified entity manager factory.
     *
     * @param tdr2023EntityManagerFactory the entity manager factory used to configure the transaction manager
     * @return the configured {@link PlatformTransactionManager} instance
     */
    @Primary
    @Bean
    public PlatformTransactionManager tdr2023TransactionManager(
            final @Qualifier("tdr2023EntityManagerFactory") LocalContainerEntityManagerFactoryBean tdr2023EntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(tdr2023EntityManagerFactory.getObject()));
    }
}
