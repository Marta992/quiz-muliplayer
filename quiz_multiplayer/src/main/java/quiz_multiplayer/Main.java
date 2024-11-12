package quiz_multiplayer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_multiplayer";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/punteggi", new HandlerPunteggi());
        server.start();
        System.out.println("Server avviato su porta 8080");
    }

    // Handler per la pagina dei punteggi
    static class HandlerPunteggi implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Recupera i punteggi dal database
            String response = getPunteggi();
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String getPunteggi() {
            StringBuilder response = new StringBuilder();
            response.append("{ \"dailyScores\": [");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT p.username, s.score FROM scores s JOIN players p ON s.player_id = p.player_id WHERE DATE(s.date_played) = CURDATE() ORDER BY s.score DESC LIMIT 3")) {

                while (rs.next()) {
                    response.append("{ \"nickname\": \"")
                            .append(rs.getString("username"))
                            .append("\", \"score\": ")
                            .append(rs.getInt("score"))
                            .append("}, ");
                }
                
                // Rimuovi l'ultima virgola
                if (response.length() > 14) {
                    response.setLength(response.length() - 2);
                }
                
                response.append("], \"monthlyScores\": [");

                // Aggiungi punteggi mensili
                try (ResultSet rsMonthly = stmt.executeQuery("SELECT p.username, s.score FROM scores s JOIN players p ON s.player_id = p.player_id WHERE MONTH(s.date_played) = MONTH(CURDATE()) ORDER BY s.score DESC LIMIT 3")) {
                    while (rsMonthly.next()) {
                        response.append("{ \"nickname\": \"")
                                .append(rsMonthly.getString("username"))
                                .append("\", \"score\": ")
                                .append(rsMonthly.getInt("score"))
                                .append("}, ");
                    }

                    // Rimuovi l'ultima virgola
                    if (response.length() > 23) {
                        response.setLength(response.length() - 2);
                    }
                }

                response.append("]}");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return response.toString();
        }
    }
}

