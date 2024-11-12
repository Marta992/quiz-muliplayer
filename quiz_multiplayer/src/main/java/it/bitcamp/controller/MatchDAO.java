package it.bitcamp.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import it.bitcamp.model.MatchEntity;
import it.bitcamp.model.PlayerEntity;

public class MatchDAO {
	
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

	public Integer addMatch(String nickname, int score, LocalDateTime timestamp) {
		Session session = factory.openSession();
		Transaction tx = null;
		MatchEntity match = null;
		Integer matchId = null;
		try {
			// inizio della transazione garantendo l'integrita dei dati
			tx = session.beginTransaction();
			// creazione di una istanza della classe user
			match = new MatchEntity();
			match.setNickname(nickname);
			match.setScore(score);
			match.setTimestamp(timestamp);

			// salviamo utente sul database e ci faccioamo tornare il valore dell id
			matchId = (Integer) session.save(match);
			PlayerEntity player = session.get(PlayerEntity.class, nickname);
			if (player != null) {
				player.setTotalScore(player.getTotalScore() + score);
				session.update(player); 
			}
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
		return matchId;
	}

	public List<MatchEntity> getAllMatch() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<MatchEntity> matches = new ArrayList<>();
		try {
			tx = session.beginTransaction();
			//otteniamo tutti gli utenti QUERY
			matches = session.createQuery("FROM MatchEntity", MatchEntity.class).list();
			tx.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		}finally {
			session.close();
		}
		return matches;
	}
	
	public List<MatchEntity> getDailyMatches() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<MatchEntity> dailyMatches = null;

		try {
			tx = session.beginTransaction();

			// Otteniamo la data odierna senza l'orario
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(new Date());

			// Creiamo una query per ottenere i punteggi di oggi
			String hql = "SELECT m FROM MatchEntity m WHERE DATE(m.timestamp) = :today";

			Query<MatchEntity> query = session.createQuery(hql, MatchEntity.class);
			query.setParameter("today", today);

			dailyMatches = query.list();

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
		return dailyMatches;
	}

}
