package it.bitcamp.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
			player = new PlayerEntity();
			player.setNickname(nickname);
			player.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));

			// salviamo utente sul database e ci faccioamo tornare il valore dell id
			playerId = (String) session.save(player);
			// confermiamo la transazione
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
	
//	Uso il metodo getPlayer per recuperare il player con quel nickname, se esiste
//	confronto la password con quella del player trovato
	public PlayerEntity loginPlayer(String nickname, String password) {
		PlayerEntity player = getPlayer(nickname);
		if(player != null && BCrypt.checkpw(password, player.getPasswordHash())) {
			return player;
		}
		return null;
	}

}
