package it.bitcamp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.bitcamp.controller.GameMatchDAO;
import it.bitcamp.controller.PlayerDAO;
import it.bitcamp.controller.QuestionDAO;
import it.bitcamp.model.GameMatchEntity;
import it.bitcamp.model.PlayerEntity;
import it.bitcamp.model.QuestionEntity;

@SuppressWarnings("restriction")
public class QuizMultiplayer {
	
	private static final int NUMBER_OF_QUESTIONS = 3;

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/", new StaticFileHandler());
		server.createContext("/login", new LoginFormHandler());
		server.createContext("/sign-up", new SignUpFormHandler());
		server.createContext("/creatore", new CreatorHandler());
		server.createContext("/question-create", new CreateQuestionFormHandler());
		server.createContext("/question-delete", new DeleteQuestionFormHandler());
		server.createContext("/punteggi_gestore", new AdminScoresHandler());
		server.createContext("/punteggi", new ScoresHandler());
		server.createContext("/gioca_gestore", new AdminGameHandler());
		server.createContext("/gioca", new GameHandler());
		server.createContext("/submitScore", new AddScoreHandler());
		server.start();
		System.out.println("Server avviato su porta 8080");
	}

	// HANDLER FILE STATICI Quando viene richiesta la root ("/"), restituisce il file index.html. Se il file richiesto non esiste, restituisce un errore 404.
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

	}

	// HANDLER LOGIN
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

				PlayerDAO playerDAO = new PlayerDAO();
				PlayerEntity player = playerDAO.loginPlayer(nickname, password);

				if (player != null && player.isAdmin()) {
					redirectToCreatorPage(exchange);
				} else if (player != null && !player.isAdmin()) {
					gameHandler(exchange, "gioca.html");
				} else {
					redirectTo(exchange, "/accedi_errore.html");
//                    response = "Invalid username or password.";
				}
			} else {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			}
		}

		private Map<String, String> parseFormData(String formData) {
			return Arrays.stream(formData.split("&")).map(s -> s.split("="))
					.collect(Collectors.toMap(a -> a[0], a -> a[1]));
		}
	}

	// HANDLER SIGN UP
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

				if (nickname == null || nickname.length() < 3 || nickname.length() > 20) {
					response400(exchange,
							"Il campo nickname è obbligatorio e deve essere compreso tra 3 e 20 caratteri");
				} else if (password == null || password.length() < 8 || password.length() > 20) {
					response400(exchange,
							"Il campo password è obbligatorio e deve essere compreso tra 8 e 20 caratteri");
				} else {
					PlayerDAO playerDao = new PlayerDAO();
					PlayerEntity existingPlayer = playerDao.getPlayer(nickname);
					if (existingPlayer != null) {
						redirectTo(exchange, "/iscriviti_errore.html");
					} else {
						playerDao.addPlayer(nickname, password);
						gameHandler(exchange, "gioca.html");
					}
				}
			} else {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			}
		}
	}

	// HANDLER CREATOR
	static class CreatorHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("GET".equals(exchange.getRequestMethod())) {
				// Definisci la cartella dei file statici
				redirectToCreatorPage(exchange);
			} else {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			}
		}
	}

	// HANDLER CREATE QUESTION Quando vengono inviati i dati tramite una richiesta POST, la domanda viene aggiunta al database.
	static class CreateQuestionFormHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("POST".equals(exchange.getRequestMethod())) {
				// Leggi i dati dal form
				InputStream inputStream = exchange.getRequestBody();
				String formData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

				// Elabora i parametri
				Map<String, String> parameters = parseFormData(formData);
				String questionText = formatString(parameters.get("questionText"));
				String correctOption = formatString(parameters.get("correctOption"));
				String otherOption1 = formatString(parameters.get("otherOption1"));
				String otherOption2 = formatString(parameters.get("otherOption2"));
				String otherOption3 = formatString(parameters.get("otherOption3"));

				if (questionText == null || correctOption == null || otherOption1 == null || otherOption2 == null
						|| otherOption3 == null) {
					response400(exchange, "Tutti i campi sono obbligatori");
				} else {
					QuestionDAO questionDAO = new QuestionDAO();
					questionDAO.addQuestion(questionText, correctOption, otherOption1, otherOption2, otherOption3);
					redirectToCreatorPage(exchange);
				}
			} else {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			}
		}
	}

	// HANDLER DELETE QUESTION 
	static class DeleteQuestionFormHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("POST".equals(exchange.getRequestMethod())) {
				// Leggi i dati dal form
				InputStream inputStream = exchange.getRequestBody();
				String formData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

				// Elabora i parametri
				Map<String, String> parameters = parseFormData(formData);
				String questionId = parameters.get("questionId").trim();

				if (questionId == null) {
					response400(exchange, "Il campo id non può essere vuoto");
				} else {
					QuestionDAO questionDAO = new QuestionDAO();
					questionDAO.deleteQuestion(Integer.parseInt(questionId));
					redirectToCreatorPage(exchange);
				}
			} else {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			}
		}
	}

	// HANDLER PUNTEGGI GESTORE
	static class AdminScoresHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			scoreHandler(exchange, "punteggi_gestore.html");
		}
	}

	// HANDLER PUNTEGGI NON GESTORE
	static class ScoresHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			scoreHandler(exchange, "punteggi.html");
		}
	}

	// HANDLER PUNTEGGI GESTORE Le domande vengono prese dal database e mostrate dinamicamente nella pagina HTML.
	static class AdminGameHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			gameHandler(exchange, "gioca_gestore.html");
		}
	}

	// HANDLER PUNTEGGI NON GESTORE
	static class GameHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			gameHandler(exchange, "gioca.html");
		}
	}

	// HANDLER LOGIN  Quando un giocatore termina il quiz, il punteggio viene salvato nel database tramite una richiesta POST.
	static class AddScoreHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("POST".equals(exchange.getRequestMethod())) {
				// Leggi i dati dal form
				InputStream inputStream = exchange.getRequestBody();
				String formData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

				// Elabora i parametri
				Map<String, String> parameters = parseFormData(formData);//li converte in una mappa di coppie chiave-valore
				String nickname = parameters.get("nickname");
				int score = Integer.parseInt(parameters.get("score"));

				GameMatchDAO gameMatchDAO = new GameMatchDAO();
				gameMatchDAO.addGameMatch(nickname, score);
				exchange.sendResponseHeaders(204, -1); // -1 indica nessun contenuto di risposta
	            exchange.close();
			} else {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			}
		}
	}

	private static void scoreHandler(HttpExchange exchange, String fileRequested) throws IOException {
		if ("GET".equals(exchange.getRequestMethod())) {
			// Definisci la cartella dei file statici
			Path filePath = Paths.get("src/main/resources/statics", fileRequested);
			if (Files.exists(filePath)) {
				GameMatchDAO gameMatchDAO = new GameMatchDAO();
				String htmlContent = new String(Files.readAllBytes(filePath));
				StringBuilder questionTableHtml = new StringBuilder();

				DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
				// DAILY SCORES
				List<GameMatchEntity> dailyScores = gameMatchDAO.getDailyGameMatch();
				Collections.reverse(dailyScores);
				questionTableHtml.append("<tbody id=\"daily-scores\">");
				for (GameMatchEntity dailyScore : dailyScores) {
					questionTableHtml.append("<tr>").append("<td>").append(dailyScore.getNickname()).append("</td>")
							.append("<td>").append(dailyScore.getScore()).append("</td>").append("<td>")
							.append(LocalDateTime.parse(dailyScore.getDatePlayed().toString(), inputFormatter).format(outputFormatter)).append("</td>")
							.append("</tr>");
				}
				questionTableHtml.append("</tbody>");
				// Sostituisco il segnaposto con la lista di domande
				htmlContent = htmlContent.replace("<tbody id=\"daily-scores\"></tbody>", questionTableHtml.toString());

				// TOTAL SCORES
				List<GameMatchEntity> allScores = gameMatchDAO.getAllGameMatch();
				Collections.reverse(allScores);
				questionTableHtml = new StringBuilder();
				questionTableHtml.append("<tbody id=\"all-scores\">");
				for (GameMatchEntity score : allScores) {
					questionTableHtml.append("<tr>").append("<td>").append(score.getNickname()).append("</td>")
							.append("<td>").append(score.getScore()).append("</td>").append("<td>")
							.append(LocalDateTime.parse(score.getDatePlayed().toString(), inputFormatter).format(outputFormatter)).append("</td>")
							.append("</tr>");
				}
				questionTableHtml.append("</tbody>");
				// Sostituisco il segnaposto con la lista di domande
				htmlContent = htmlContent.replace("<tbody id=\"all-scores\"></tbody>", questionTableHtml.toString());

				exchange.getResponseHeaders().set("Content-Type", "text/html");
				exchange.sendResponseHeaders(200, htmlContent.getBytes().length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(htmlContent.getBytes());
				}
			} else {
				response404(exchange);
			}
		} else {
			exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
		}
	}

	private static void gameHandler(HttpExchange exchange, String fileRequested) throws IOException {
		// Definisci la cartella dei file statici
		Path filePath = Paths.get("src/main/resources/statics", fileRequested);
		if (Files.exists(filePath)) {
			QuestionDAO questionDAO = new QuestionDAO();
			String htmlContent = new String(Files.readAllBytes(filePath));
			StringBuilder questionTableHtml = new StringBuilder();

			// DAILY SCORES
			List<QuestionEntity> questions = questionDAO.getRandomQuestions(NUMBER_OF_QUESTIONS);
			questionTableHtml.append("<tbody id=\"questions\">");
			for (QuestionEntity question : questions) {
				questionTableHtml.append("<tr>").append("<td>").append(question.getQuestionText()).append("</td>")
						.append("<td>").append(question.getCorrectOption()).append("</td>").append("<td>")
						.append(question.getOtherOption1()).append("</td>").append("<td>")
						.append(question.getOtherOption2()).append("</td>").append("<td>")
						.append(question.getOtherOption3()).append("</td>").append("</tr>");
			}
			questionTableHtml.append("</tbody>");
			// Sostituisco il segnaposto con la lista di domande
			htmlContent = htmlContent.replace("<tbody id=\"questions\"></tbody>", questionTableHtml.toString());
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, htmlContent.getBytes().length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(htmlContent.getBytes());
			}
		} else {
			response404(exchange);
		}
	}
//Determina il tipo di contenuto di un file in base alla sua estensione (HTML, CSS, JavaScript, immagine, ecc
	private static String getContentType(String fileName) {
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

	private static Map<String, String> parseFormData(String formData) {
		return Arrays.stream(formData.split("&")).map(s -> s.split("="))
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

	private static String redirectToCreatorPage(HttpExchange exchange) throws IOException {
		// Definisci la cartella dei file statici
		Path filePath = Paths.get("src/main/resources/statics", "/creatore.html");
		if (Files.exists(filePath)) {
			QuestionDAO questionDAO = new QuestionDAO();
			List<QuestionEntity> questions = questionDAO.getAllQuestion();
			String htmlContent = new String(Files.readAllBytes(filePath));
			StringBuilder questionTableHtml = new StringBuilder();
			questionTableHtml.append("<tbody>");
			for (QuestionEntity question : questions) {
				questionTableHtml.append("<tr>").append("<td>").append(question.getId()).append("</td>").append("<td>")
						.append(question.getQuestionText()).append("</td>").append("<td>")
						.append(question.getCorrectOption()).append("</td>").append("</tr>");
			}
			questionTableHtml.append("</tbody>");

			// Sostituisci il segnaposto con la lista di domande
			htmlContent = htmlContent.replace("<tbody></tbody>", questionTableHtml.toString());

//            byte[] fileContent = Files.readAllBytes(filePath);
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, htmlContent.getBytes().length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(htmlContent.getBytes());
			}
			return "200 OK";
		} else {
			return response404(exchange);
		}
	}

	private static String formatString(String string) {
		return string.trim().replaceAll("%3A", ":").replaceAll("%3B", ";").replaceAll("%3F", "?").replaceAll("%2C", ",")
				.replaceAll("%21", "!").replaceAll("\\+", " ");
	}
}
