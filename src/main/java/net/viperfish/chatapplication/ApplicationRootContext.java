package net.viperfish.chatapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
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

import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.filters.AuthenticationFilter;
import net.viperfish.chatapplication.handlers.AddAssociateHandler;
import net.viperfish.chatapplication.handlers.AssociateLookupHandler;
import net.viperfish.chatapplication.handlers.DeleteAssociateHandler;
import net.viperfish.chatapplication.handlers.GetPublicKeyHandler;
import net.viperfish.chatapplication.handlers.LoginHandler;
import net.viperfish.chatapplication.handlers.MessagingHandler;
import net.viperfish.chatapplication.handlers.SearchUserHandler;

@Configuration
@EnableAsync(proxyTargetClass = true, order = 1)
@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.LOWEST_PRECEDENCE)
@EnableJpaRepositories(basePackages = {
		"net.viperfish.chatapplication.core" }, entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "jpaTransactionManager")
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
			String errorBuilder = "Async execution error on method:" + method.toString() + " with parameters:"
					+ Arrays.toString(params);
			log.error(errorBuilder);
		};
	}

	@Bean
	public DataSource chatDataSource() throws ConfigurationException, IOException {
		BasicDataSource datasource = new BasicDataSource();
		String path = this.configuration().getString(GlobalConfig.DB_PATH);
		if (path == null) {
			path = GlobalConfig.DB_PATH_DEFAULT;
		}
		datasource.setUsername(this.configuration().getString(GlobalConfig.DB_USERNAME));
		datasource.setPassword(this.configuration().getString(GlobalConfig.DB_PASSWORD));
		datasource.setDriverClassName("org.h2.Driver");
		datasource.setUrl("jdbc:h2:" + path);
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
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean()
			throws ConfigurationException, IOException {
		Map<String, Object> properties = new Hashtable<>();
		String indexPath = this.configuration().getString(GlobalConfig.INDEX_PATH);
		if (indexPath == null) {
			indexPath = GlobalConfig.INDEX_PATH_DEFAULT;
		}
		properties.put("javax.persistence.schema-generation.database.action", "none");
		properties.put("hibernate.connection.characterEncoding", "utf8");
		properties.put("hibernate.connection.useUnicode", "true");
		properties.put("hibernate.connection.charSet", "utf8");
		properties.put("hibernate.search.default.directory_provider", "filesystem");
		properties.put("hibernate.search.default.indexBase", indexPath);

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
	public PlatformTransactionManager jpaTransactionManager() throws ConfigurationException, IOException {
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
	public ChatApplication chatApplication()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException,
			FileNotFoundException, CertificateException, UnrecoverableEntryException, ConfigurationException {
		ChatApplication application = new ChatApplication(this.userRegister());
		application.addHandler(LSRequest.LS_LOGIN, new LoginHandler(userDatabase, this.userRegister()));
		application.addHandler(LSRequest.LS_MESSAGE, new MessagingHandler());
		application.addHandler(LSRequest.LS_ASSOCIATE_LOOKUP,
				new AssociateLookupHandler(userDatabase, this.userRegister()));
		application.addHandler(LSRequest.LS_ADD_ASSOCIATE, new AddAssociateHandler(userDatabase));
		application.addHandler(LSRequest.LS_LOOKUP_USER, new SearchUserHandler(userDatabase));
		application.addHandler(LSRequest.LS_DELETE_ASSOCIATE, new DeleteAssociateHandler(userDatabase));
		application.addHandler(LSRequest.LS_LOOKUP_KEY, new GetPublicKeyHandler(userDatabase));
		application.addFilter(new AuthenticationFilter());
		return application;
	}

	@Bean
	public KeyStore serverKeyStore() throws KeyStoreException, FileNotFoundException, IOException,
			NoSuchAlgorithmException, CertificateException, ConfigurationException {
		KeyStore ks = KeyStore.getInstance("JKS");
		String keyStorePath = configuration().getString(GlobalConfig.SERVER_KEYSTORE);
		if (keyStorePath == null) {
			keyStorePath = GlobalConfig.SERVER_KEYSTORE_DEFAULT;
		}
		File storeFile = new File(keyStorePath);
		ks.load(new FileInputStream(storeFile),
				this.configuration().getString(GlobalConfig.SERVER_KEYPASS).toCharArray());
		return ks;
	}

	@Bean
	public KeyPair serverKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException,
			FileNotFoundException, CertificateException, UnrecoverableEntryException, ConfigurationException {
		KeyStore ks = this.serverKeyStore();

		PublicKey publicKey = ks.getCertificate("server").getPublicKey();
		PrivateKey priv = ((KeyStore.PrivateKeyEntry) ks.getEntry("server",
				new KeyStore.PasswordProtection(configuration().getString(GlobalConfig.SERVER_KEYPASS).toCharArray())))
						.getPrivateKey();
		KeyPair result = new KeyPair(publicKey, priv);
		return result;
	}

	@Bean
	public SSLContextConfigurator sslConf() {
		SSLContextConfigurator config = new SSLContextConfigurator();
		String keyStorePath;
		try {
			keyStorePath = configuration().getString(GlobalConfig.SERVER_KEYSTORE);
			if (keyStorePath == null) {
				keyStorePath = GlobalConfig.SERVER_KEYSTORE_DEFAULT;
			}
			config.setKeyStoreFile(keyStorePath);
			config.setKeyStorePass(configuration().getString(GlobalConfig.SERVER_KEYPASS));
			return config;
		} catch (ConfigurationException | IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Bean
	public HttpServer httpServer()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException,
			FileNotFoundException, CertificateException, UnrecoverableEntryException, ConfigurationException {
		HttpServer server = HttpServer.createSimpleServer("./", 8080);
		WebSocketAddOn addon = new WebSocketAddOn();
		server.getListeners().stream().forEach((listen) -> {
			listen.registerAddOn(addon);
			listen.setSecure(true);
			listen.setSSLEngineConfig(
					new SSLEngineConfigurator(this.sslConf()).setClientMode(false).setNeedClientAuth(false));
		});

		String messengerPath = configuration().getString(GlobalConfig.MESSENGER_PATH);
		if (messengerPath == null) {
			messengerPath = GlobalConfig.MESSENGER_PATH_DEFAULT;
		}

		ChatApplication application = this.chatApplication();
		WebSocketEngine.getEngine().register("", messengerPath, application);
		return server;
	}

}
