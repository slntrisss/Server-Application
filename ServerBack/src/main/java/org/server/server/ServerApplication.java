package org.server.server;


import org.server.server.model.Server;
import org.server.server.repo.ServerRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.server.server.enumeration.Status.SERVER_DOWN;
import static org.server.server.enumeration.Status.SERVER_UP;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	CommandLineRunner run(ServerRepo serverRepo) {
		return args -> {
			serverRepo.save(new Server(null, "192.168.1.160", "Ubuntu Linux", "16GB", "PC",
					"http://localhost:8080/server/image/server.png", SERVER_UP));
			serverRepo.save(new Server(null, "192.168.1.58", "Kali Linux", "32GB", "PC",
					"http://localhost:8080/server/image/server.png", SERVER_UP));
			serverRepo.save(new Server(null, "127.0.0.1", "Monterey MacOS", "8GB", "PC",
					"http://localhost:8080/server/image/server.png", SERVER_DOWN));
			serverRepo.save(new Server(null, "192.168.1.14", "Windows", "16GB", "PC",
					"http://localhost:8080/server/image/server.png", SERVER_UP));
			serverRepo.save(new Server(null, "8.8.8.8", "Google", null, "Server",
					"http://localhost:8080/server/image/server.png", SERVER_UP));
		};
	}
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}
}
