package it.bitcamp.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

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
	
	public PlayerEntity addPlayer(String nickname, String password) {
		Session session = factory.openSession();
		Transaction tx = null;
		PlayerEntity player = null;
		try {
			// inizio della transazione garantendo l'integrita dei dati
			tx = session.beginTransaction();
			// creazione di una istanza della classe user
			player.setNickname(nickname);
			player.setPassword(password);

			// salviamo utente sul database e ci faccioamo tornare il valore dell id
			player = (PlayerEntity) session.save(player);
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
		return player;
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
				
		}finally {
			session.close();
		}
		return player;
	}
	
}
