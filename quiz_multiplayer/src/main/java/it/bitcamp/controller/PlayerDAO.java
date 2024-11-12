package it.bitcamp.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;
import it.bitcamp.model.PlayerEntity;

public class PlayerDAO {

	private static SessionFactory factory;

	static {
		try {
			// configuriamo la factory con il file di configurazione di hibernate
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.out.println("Connessione fallita dell'oggetto session factory" + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public String addPlayer(String nickname, String password) {
		Session session = factory.openSession();
		Transaction tx = null;
		PlayerEntity player = null;
		String playerId = null;
		try {
			// inizio della transazione garantendo l'integrita dei dati
			tx = session.beginTransaction();
			// creazione di una istanza della classe user
			player.setNickname(nickname);
			player.setPassword(password);

			// salviamo utente sul database e ci faccioamo tornare il valore dell id
			playerId = (String) session.save(player);
			// confwermiamo la transazione
			tx.commit();

		} catch (Exception e) {
			// annullamento della transazione
			e.printStackTrace();
			// altro codice
			if (tx != null) {
				tx.rollback(); // annullare transazione
			}
		} finally {
			// chiudiamo la sessione di lavoro e rilasciamo le risorse occupate
			session.close();

		}
		return playerId;
	}

	public PlayerEntity getPlayer(String nickname) {
		Session session = factory.openSession();
		Transaction tx = null;
		PlayerEntity player = null;
		try {
			tx = session.beginTransaction();
			player = session.get(PlayerEntity.class, nickname);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}

		} finally {
			session.close();
		}
		return player;
	}

	// Quando un nuovo giocatore si registra, la password viene hashata prima di
	// essere salvata nel database.
	public static class PasswordUtils {

		// Metodo per hashare una password
		public static String hashPassword(String password) {
			return BCrypt.hashpw(password, BCrypt.gensalt()); // bcrypt genera un salt e lo aggiunge all'hash
		}

		// Metodo per verificare se la password fornita corrisponde all'hash nel DB
		public static boolean checkPassword(String password, String hashedPassword) {
			return BCrypt.checkpw(password, hashedPassword); // Confronta la password con l'hash
		}
	}

	

	// loginPlayer
	public String loginPlayer(String nickname, String password) {
		Session session = factory.openSession();
		Transaction tx = null;
		String role = "Player"; // Default role is "Player"

		try {
			tx = session.beginTransaction();

			// Prima verifica se il nickname è quello dell'amministratore
			if (nickname.equals("marta") && password.equals("1234")) {
				role = "Admin"; // Se il nickname e la password corrispondono all'amministratore
			} else {
				// Se non è l'amministratore, recupera il giocatore dal database
				Query<PlayerEntity> query = session.createQuery("FROM PlayerEntity WHERE nickname = :nickname",
						PlayerEntity.class);
				query.setParameter("nickname", nickname);
				PlayerEntity player = query.uniqueResult(); // Restituisce null se il giocatore non esiste

				if (player != null) {
					// Se il giocatore esiste, confronta la password fornita con quella memorizzata
					// (hashata)
					if (PasswordUtils.checkPassword(password, player.getPassword())) {
						role = "Player"; // Se la password è corretta, il login è riuscito come Player
					} else {
						return "Invalid password"; // Password errata
					}
				} else {
					return "Player not found"; // Giocatore non trovato
				}
			}

			tx.commit(); // Commit della transazione
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback(); // Rollback in caso di errore
			}
			return "An error occurred";
		} finally {
			session.close(); // Chiusura della sessione
		}

		return role; // Restituisce "Admin" o "Player" a seconda del ruolo
	}

}
