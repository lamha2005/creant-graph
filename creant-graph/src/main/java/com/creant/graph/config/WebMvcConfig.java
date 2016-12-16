package com.creant.graph.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.creant.graph.dao.EmployeeDAO;
import com.creant.graph.service.AccountService;
import com.creant.graph.service.IAccountService;

/**
 * @author lamhm
 *
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:/application.properties")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("WEB-INF/pages/");
		resolver.setSuffix(".html");
		return resolver;
	}

	@Bean
	public SimpleMappingExceptionResolver exceptionResolver() {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();

		Properties exceptionMappings = new Properties();
		exceptionMappings.put("java.lang.Exception", "error/error");
		exceptionMappings.put("java.lang.RuntimeException", "error/error");
		exceptionMappings.put("com.me.spring.exception.CustomGenericException", "error/error");

		exceptionResolver.setExceptionMappings(exceptionMappings);
		return exceptionResolver;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		return new JettyEmbeddedServletContainerFactory();
	}

	@Bean
	public RmiServiceExporter exportService(@Autowired AccountService employeeService) {
		System.setProperty("java.rmi.server.hostname", "112.78.15.60");
		RmiServiceExporter exporter = new RmiServiceExporter();
		exporter.setServiceName("AccountService");
		exporter.setService(employeeService);
		exporter.setServiceInterface(IAccountService.class);
		exporter.setRegistryPort(9010);
		
		return exporter;
	}

	// @Bean(name = "accountService")
	// public IAccountService invokeService() {
	// RmiProxyFactoryBean invoke = new RmiProxyFactoryBean();
	// invoke.setServiceUrl("rmi://112.78.15.60:9000/AccountService");
	// invoke.setServiceInterface(IAccountService.class);
	// invoke.afterPropertiesSet();
	// IAccountService object = (IAccountService) invoke.getObject();
	// IUser user = object.getUser("g31epQyTuhrVI87T9FepPhWvQVar");
	// System.out.println(user.toString());
	// return object;
	// }

	@Bean
	public EmployeeDAO employeeDAO() {
		return new EmployeeDAO();
	}
}
