package com.js.rest.api;


import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.implementations.cache.gcp.memcache.CcpGcpMemCache;
import com.ccp.implementations.db.bulk.elasticsearch.CcpElasticSerchDbBulk;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.email.sendgrid.CcpSendGridEmailSender;
import com.ccp.implementations.file.bucket.gcp.CcpGcpFileBucket;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.instant.messenger.telegram.CcpTelegramInstantMessenger;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.implementations.main.authentication.gcp.oauth.CcpGcpMainAuthentication;
import com.ccp.implementations.mensageria.sender.gcp.pubsub.CcpGcpPubSubMensageriaSender;
import com.ccp.implementations.password.mindrot.CcpMindrotPasswordHandler;
import com.ccp.local.testings.implementations.CcpLocalInstances;
import com.ccp.local.testings.implementations.cache.CcpLocalCacheInstances;
import com.ccp.rest.api.spring.exceptions.handler.CcpRestApiExceptionHandlerSpring;
import com.ccp.rest.api.spring.response.CcpRestApiSessionValuesRemoverSpring;
import com.ccp.rest.api.spring.openapi.CcpMissingPathParamsCustomizer;
import com.ccp.rest.api.spring.servlet.filters.CcpPutSessionValuesAndExecuteTaskFilter;
import com.ccp.rest.api.spring.servlet.filters.CcpValidEmailFilter;
import com.ccp.rest.api.utils.CcpRestApiUtils;
import com.jn.business.session.JnBusinessValidateSession;
import com.jn.entities.JnEntityJobsnowError;
import com.js.rest.api.endpoints.JsRestApiContactUs;
import com.js.rest.api.endpoints.JsRestApiGrouper;
import com.js.rest.api.endpoints.JsRestApiLogin;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Entry point of the REST API of the js cost center (login, port 8082). Wires the dependencies (local implementations
 * when {@code localEnvironment} is set, GCP and Elasticsearch otherwise), records unhandled errors in
 * {@code jn_jobsnow_error} and registers the servlet filters: e-mail syntax and session values on {@code /login/*} and
 * {@code /contact-us/*} (which has no session, since 2026-10-23), session validation on {@code /grouper/*}.
 * Until 2026-10-17 the login was served by the API of the jn cost center (port 8080).
 */
@EnableWebMvc
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@ComponentScan(basePackageClasses = {
		JsRestApiLogin.class,
		JsRestApiGrouper.class,
		JsRestApiContactUs.class,
		CcpRestApiExceptionHandlerSpring.class,
		CcpRestApiSessionValuesRemoverSpring.class,
})
@SpringBootApplication
public class JsRestApiSpringStarter {
	
	/**
	 * Wires the dependencies and starts Spring.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		CcpGsonJsonHandler ccpGsonJsonHandler = CcpGsonJsonHandler.INSTANCE;
		CcpDependencyInjection.loadAllDependencies(
				ccpGsonJsonHandler
				);
		
		 
		boolean localEnvironment = CcpRestApiUtils.isLocalEnvironment();	
		CcpTelegramInstantMessenger ccpTelegramInstantMessenger = CcpTelegramInstantMessenger.INSTANCE;
		CcpMindrotPasswordHandler ccpMindrotPasswordHandler = CcpMindrotPasswordHandler.INSTANCE;
		CcpElasticSearchDbRequest ccpElasticSearchDbRequest = CcpElasticSearchDbRequest.INSTANCE;
		CcpGcpMainAuthentication ccpGcpMainAuthentication = CcpGcpMainAuthentication.INSTANCE;
		CcpElasticSerchDbBulk ccpElasticSerchDbBulk = CcpElasticSerchDbBulk.INSTANCE;
		CcpElasticSearchCrud ccpElasticSearchCrud = CcpElasticSearchCrud.INSTANCE;
		CcpApacheMimeHttp ccpApacheMimeHttp = CcpApacheMimeHttp.INSTANCE;
		// the queries (e.g. the purge of a versionable record's history) need it
		CcpElasticSearchQueryExecutor ccpElasticSearchQueryExecutor = CcpElasticSearchQueryExecutor.INSTANCE;

		CcpDependencyInjection.loadAllDependencies
		(
				localEnvironment ? CcpLocalInstances.asyncMensageriaListener : CcpGcpPubSubMensageriaSender.INSTANCE,
				localEnvironment ? CcpLocalInstances.email : CcpSendGridEmailSender.INSTANCE,
				localEnvironment ? CcpLocalInstances.bucket : CcpGcpFileBucket.INSTANCE,
				localEnvironment ? CcpLocalCacheInstances.map : CcpGcpMemCache.INSTANCE,
				// local: writes to c:\logs\telegram instead of sending a real message to the support chat
				localEnvironment ? CcpLocalInstances.instantMessenger : ccpTelegramInstantMessenger,
				ccpMindrotPasswordHandler,
				ccpElasticSearchDbRequest,
				ccpGcpMainAuthentication,
				ccpElasticSerchDbBulk, 
				ccpElasticSearchCrud,
				ccpElasticSearchQueryExecutor,
				ccpApacheMimeHttp
		);

		CcpRestApiExceptionHandlerSpring.genericExceptionHandler = CcpEntityOperationType.save.getOperationCallback(JnEntityJobsnowError.ENTITY);

		SpringApplication.run(JsRestApiSpringStarter.class, args);
	}

	/**
	 * Adds to the OpenAPI document the path variables that an operation uses in its path but does not declare.
	 * @return the customizer
	 */
	@Bean
	public GlobalOpenApiCustomizer missingPathParamsCustomizer() {
		return CcpMissingPathParamsCustomizer.INSTANCE;
	}

	/**
	 * Describes the API in the OpenAPI document.
	 * @return the description
	 */
	@Bean
	public OpenAPI jsOpenAPI() {
		OpenAPI openAPI = new OpenAPI();
		Info info2 = new Info();
		Info title = info2
						.title("JobsNow Authentication API");
						Info description = title
						.description("REST API for authentication: login, password management, token and session control.");
						Info version = description
						.version("1.0");
						OpenAPI info = openAPI
						.info(version);
						return info;
	}

	/**
	 * Serves the static resources of the Swagger UI.
	 * @return the configurer
	 */
	@Bean
	public WebMvcConfigurer swaggerResourceHandler() {
		var webMvcConfigurer = new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				ResourceHandlerRegistration addResourceHandler = registry.addResourceHandler("/webjars/**");
				addResourceHandler
						.addResourceLocations("classpath:/META-INF/resources/webjars/");
						ResourceHandlerRegistration addResourceHandler2 = registry.addResourceHandler("/swagger-ui/**");
						addResourceHandler2
						.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
			}
		};
		return webMvcConfigurer;
	}

	/**
	 * Rejects an invalid e-mail in the path of {@code /login/*}, {@code /grouper/*} and {@code /contact-us/*}.
	 * @return the filter registration
	 */
	@Bean
	public FilterRegistrationBean<CcpValidEmailFilter> emailFilter() {
		FilterRegistrationBean<CcpValidEmailFilter> filterRegistration = new FilterRegistrationBean<>();
		CcpValidEmailFilter emailSyntaxFilter = CcpValidEmailFilter.getEmailSyntaxFilter("login/", "grouper/", "contact-us/");
		filterRegistration.setFilter(emailSyntaxFilter);
		filterRegistration.addUrlPatterns("/login/*", "/grouper/*", "/contact-us/*");
		return filterRegistration;
	}

	/**
	 * Puts the session values (user agent, ip, e-mail) in the body of {@code /login/*} and of {@code /contact-us/*}, which
	 * has no session: the login screen may be the one with the bug.
	 * @return the filter registration
	 */
	@Bean
	public FilterRegistrationBean<CcpPutSessionValuesAndExecuteTaskFilter> putSessionValuesFilter() {
		FilterRegistrationBean<CcpPutSessionValuesAndExecuteTaskFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(CcpPutSessionValuesAndExecuteTaskFilter.TASKLESS);
		filterRegistration.addUrlPatterns("/login/*", "/contact-us/*");
		return filterRegistration;
	}

	/**
	 * Validates the session ({@code JnBusinessValidateSession}, which runs the validator seeded by the login) on
	 * {@code /grouper/*}, and puts the session values in the body.
	 * @return the filter registration
	 */
	@Bean
	public FilterRegistrationBean<CcpPutSessionValuesAndExecuteTaskFilter> validateSessionFilter() {
		FilterRegistrationBean<CcpPutSessionValuesAndExecuteTaskFilter> filterRegistration = new FilterRegistrationBean<>();
		CcpPutSessionValuesAndExecuteTaskFilter filter = new CcpPutSessionValuesAndExecuteTaskFilter(JnBusinessValidateSession.INSTANCE);
		filterRegistration.setFilter(filter);
		filterRegistration.addUrlPatterns("/grouper/*");
		return filterRegistration;
	}
}
