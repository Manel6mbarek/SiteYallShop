package com.facturation.facture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FactureBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FactureBackendApplication.class, args);

		System.out.println("================================");
		System.out.println("🚀 Application Spring Boot démarrée !");
		System.out.println("📍 Serveur : http://localhost:8080");
		System.out.println("📋 API REST : http://localhost:8080/api/");
		System.out.println("📊 Base de données : MySQL");
		System.out.println("================================");
	}
}