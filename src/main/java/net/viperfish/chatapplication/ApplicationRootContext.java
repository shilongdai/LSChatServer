package net.viperfish.chatapplication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.handlers.LoginHandler;
import net.viperfish.chatapplication.handlers.MessagingHandler;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@EnableAsync(proxyTargetClass = true, order = 1)
@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.LOWEST_PRECEDENCE)
@EnableJpaRepositories(basePackages = {
    "net.viperfish.chatapplication.core"}, entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "jpaTransactionManager")
@ComponentScan(basePackages = "net.viperfish.chatapplication")
public class ApplicationRootContext implements AsyncConfigurer {

    private Logger log = LogManager.getLogger();
    @Autowired
    private UserDatabase userDatabase;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        int level = 2;
        log.info("Creating thread pool with " + level + " threads");
        ThreadPoolTaskScheduler exec = new ThreadPoolTaskScheduler();
        exec.setPoolSize(level);
        exec.setThreadNamePrefix("transaction");
        exec.setAwaitTerminationSeconds(60);
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor executor) -> {
            String errorBuilder = "Task Rejected";
            log.error(errorBuilder);
        });
        return exec;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() throws ClassNotFoundException {
        LocalValidatorFactoryBean result = new LocalValidatorFactoryBean();
        result.setProviderClass(Class.forName("org.hibernate.validator.HibernateValidator"));
        return result;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() throws ClassNotFoundException {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(this.localValidatorFactoryBean());
        return processor;
    }

    @Override
    public Executor getAsyncExecutor() {
        Executor exec = this.taskScheduler();
        log.info(exec + " ready for use");
        return exec;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            String errorBuilder = "Async execution error on method:" + method.toString() + " with parameters:" + Arrays.toString(params);
            log.error(errorBuilder);
        };
    }

    @Bean
    public DataSource chatDataSource() {
        BasicDataSource datasource = new BasicDataSource();
        datasource.setUsername("chatapplication");
        datasource.setPassword("chatapplication");
        datasource.setDriverClassName("org.h2.Driver");
        datasource.setUrl("jdbc:h2:./users");
        datasource.setMaxIdle(3);
        datasource.setMaxWaitMillis(5000);
        datasource.setRemoveAbandonedOnBorrow(true);
        datasource.setRemoveAbandonedOnBorrow(true);
        datasource.setRemoveAbandonedTimeout(20);
        datasource.setLogAbandoned(true);
        datasource.setValidationQuery("select 1");
        datasource.setMinEvictableIdleTimeMillis(3600000);
        datasource.setTimeBetweenEvictionRunsMillis(1800000);
        datasource.setNumTestsPerEvictionRun(10);
        datasource.setTestOnBorrow(true);
        datasource.setTestOnReturn(false);
        datasource.addConnectionProperty("useUnicode", "yes");
        datasource.addConnectionProperty("characterEncoding", "utf8");
        return datasource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        Map<String, Object> properties = new Hashtable<>();
        properties.put("javax.persistence.schema-generation.database.action", "none");
        properties.put("hibernate.connection.characterEncoding", "utf8");
        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.connection.charSet", "utf8");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(adapter);
        factory.setDataSource(this.chatDataSource());
        factory.setPackagesToScan("net.viperfish.chatapplication");
        factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        factory.setValidationMode(ValidationMode.NONE);
        factory.setJpaPropertyMap(properties);
        return factory;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager(this.entityManagerFactoryBean().getObject());
    }

    @Bean
    public FileConfiguration configuration() throws IOException, ConfigurationException {
        Path configFile = Paths.get("config");
        if (!configFile.toFile().exists()) {
            Files.createFile(configFile);
        }
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.setFileName(configFile.toString());
        config.setAutoSave(true);
        config.load();
        return config;
    }
    
    @Bean
    public UserRegister userRegister() {
        return new UserRegister();
    }
    

    @Bean
    public ChatApplication chatApplication() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ChatApplication application = new ChatApplication();
        application.setSocketMapper(this.userRegister());
        application.addHandler(LSRequest.LS_LOGIN, new LoginHandler(userDatabase, this.userRegister(), this.serverKey().getPrivate()));
        application.addHandler(LSRequest.LS_MESSAGE, new MessagingHandler());
        return application;
    }

    @Bean
    public KeyPair serverKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Files.readAllBytes(Paths.get("pubkey.pub"));
        byte[] privateKeyBytes = Files.readAllBytes(Paths.get("private.key"));
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey priv = KeyFactory.getInstance("EC").generatePrivate(privateSpec);
        
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = KeyFactory.getInstance("EC").generatePublic(publicSpec);
        
        KeyPair result = new KeyPair(publicKey, priv);
        return result;
    }
    
}
