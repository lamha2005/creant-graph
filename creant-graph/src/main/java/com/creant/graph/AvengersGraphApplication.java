package com.creant.graph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import util.Configs;

/**
 * @author lamhm
 *
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AvengersGraphApplication {
	private static final Logger LOG = Logger.getLogger(AvengersGraphApplication.class);

	public static void main(String[] args) throws FileNotFoundException {
		PropertyConfigurator.configure("configs/log4j.properties");
		LOG.info("=========================== Avenger Graph Starting =========================");
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setServiceAccount(new FileInputStream("configs/avg-graph-account-26f6103dd586.json"))
				.setDatabaseUrl("https://avg-graph.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);
		LOG.info("- inited firebase");
		Configs.init();
		LOG.info("- inited configs");
		SpringApplication.run(AvengersGraphApplication.class, args);
		LOG.info("=========================== Avenger Graph Started =========================");
	}

}
