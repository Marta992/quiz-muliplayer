package it.bitcamp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.bitcamp.controller.PlayerDAO;
import it.bitcamp.model.PlayerEntity;

public class QuizMultiplayer {
	
	public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/login", new LoginFormHandler());
        server.start();
        System.out.println("Server avviato su porta 8080");
        
    }
	
	//HANDLER FILE STATICI
	static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestedFile = exchange.getRequestURI().getPath();
            if (requestedFile.equals("/")) {
                requestedFile = "/index.html"; // default to index.html
            }
            // Definisci la cartella dei file statici
            Path filePath = Paths.get("src/main/resources/statics", requestedFile);
            if (Files.exists(filePath)) {
                byte[] fileContent = Files.readAllBytes(filePath);
                String contentType = getContentType(requestedFile);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileContent.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileContent);
                }
            } else {
            	response404(exchange);
            }
        }
        
        private String getContentType(String fileName) {
            if (fileName.endsWith(".html")) {
                return "text/html";
            } else if (fileName.endsWith(".css")) {
                return "text/css";
            } else if (fileName.endsWith(".js")) {
                return "application/javascript";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else {
                return "application/octet-stream"; // Default binary type for unknown files
            }
        }
    }
	
	//HANDLER LOGIN
    static class LoginFormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Leggi i dati dal form
                InputStream inputStream = exchange.getRequestBody();
                String formData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                
                // Elabora i parametri
                Map<String, String> parameters = parseFormData(formData);
                String nickname = parameters.get("nickname");
                String password = parameters.get("password");

                String response;
                if ("admin".equals(nickname) && "password".equals(password)) {
                	response = redirectTo(exchange, "/gioca.html");
                } else {
                    response = "Invalid username or password.";
                }
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        }

        private Map<String, String> parseFormData(String formData) {
            return Arrays.stream(formData.split("&"))
                    .map(s -> s.split("="))
                    .collect(Collectors.toMap(a -> a[0], a -> a[1]));
        }
    }
    
    //HANDLER SIGN UP
    static class SignUpFormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Leggi i dati dal form
                InputStream inputStream = exchange.getRequestBody();
                String formData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                
                // Elabora i parametri
                Map<String, String> parameters = parseFormData(formData);
                String nickname = parameters.get("nickname").trim();
                String password = parameters.get("password").trim();
                
                if(nickname == null || nickname.length() < 8 || nickname.length() >14) {
                	response400(exchange, "Il campo nickname è obbligatorio e deve essere compreso tra 8 e 12 caratteri");
                } else if (password == null || password.length() < 8 || password.length() >14) {
                	response400(exchange, "Il campo password è obbligatorio e deve essere compreso tra 8 e 12 caratteri");
                } else {
                	PlayerDAO playerDao = new PlayerDAO();
                    PlayerEntity existingPlayer = playerDao.getPlayer(nickname);
                    String response;
                    if(existingPlayer != null) {
                    	response400(exchange, "Nickname già esistente");
                    } else {
                    	playerDao.addPlayer(nickname, password);
                    	response = redirectTo(exchange, "/gioca.html");
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        }

        
    }
    
    private static Map<String, String> parseFormData(String formData) {
        return Arrays.stream(formData.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));
    }
    
    private static String redirectTo(HttpExchange exchange, String requestedFile) throws IOException {
    	String response = null;
    	Path filePath = Paths.get("src/main/resources/statics", requestedFile);
        if (Files.exists(filePath)) {
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = "text/html";
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileContent.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileContent);
            }
            response = "200 OK";
        } else {
        	response404(exchange);
        }
        return response;
    }
    
    private static String response404(HttpExchange exchange) throws IOException {
    	String response = "404 Not Found";
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
		return response;
    }
    
    private static String response400(HttpExchange exchange, String message) throws IOException {
    	String response = "404 Not Found - " + message;
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
		return response;
    }
}
