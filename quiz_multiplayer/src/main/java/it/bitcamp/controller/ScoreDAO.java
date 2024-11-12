package it.bitcamp.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import it.bitcamp.model.PlayerEntity;
import it.bitcamp.model.ScoreEntity;

public class ScoreDAO {
	private static SessionFactory factory;
//add score
	/*
	 * public void addScore(int playerId, int score) { Session session =
	 * factory.openSession(); Transaction tx = null;
	 * 
	 * try { tx = session.beginTransaction();
	 * 
	 * // Crea un'istanza di ScoreEntity ScoreEntity scoreEntity = new
	 * ScoreEntity(); scoreEntity.setPlayerId(playerId);
	 * scoreEntity.setScore(score); scoreEntity.setCreatedAt(new
	 * Timestamp(System.currentTimeMillis())); // Imposta la data e ora attuali
	 * 
	 * // Salva il punteggio nel database session.save(scoreEntity);
	 * 
	 * tx.commit(); // Commit della transazione } catch (Exception e) {
	 * e.printStackTrace(); if (tx != null) { tx.rollback(); // Rollback in caso di
	 * errore } } finally { session.close(); // Chiusura della sessione } }
	 */

	public void addScoreToPlayer(ScoreEntity scoreEntity) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			// Aggiungi il punteggio alla tabella ScoreEntity
			session.save(scoreEntity);

			// Recupera il giocatore e aggiorna il punteggio totale
			PlayerEntity player = session.get(PlayerEntity.class, scoreEntity.getPlayerId());
			if (player != null) {
				player.addToTotalScore(scoreEntity.getScore()); // Aggiungi il punteggio al totale
				session.update(player); // Salva l'aggiornamento nel database
			}

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
	}

	// getDailyScores
	public List<ScoreEntity> getDailyScores() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<ScoreEntity> dailyScores = null;

		try {
			tx = session.beginTransaction();

			// Otteniamo la data odierna senza l'orario
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(new Date());

			// Creiamo una query per ottenere i punteggi di oggi
			String hql = "SELECT s FROM ScoreEntity s " + "JOIN PlayerEntity p ON s.playerId = p.nickname "
					+ "WHERE DATE(s.createdAt) = :today";

			Query<ScoreEntity> query = session.createQuery(hql, ScoreEntity.class);
			query.setParameter("today", today);

			dailyScores = query.list(); // Eseguiamo la query e otteniamo il risultato

			tx.commit(); // Commit della transazione
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback(); // Rollback in caso di errore
			}
		} finally {
			session.close(); // Chiusura della sessione
		}

		return dailyScores; // Restituisce la lista dei punteggi del giorno
	}

	// getMonthlyScores
	public List<PlayerEntity> getMonthlyScores() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<PlayerEntity> playerScores = new ArrayList<>();

		try {
			tx = session.beginTransaction();

			// Query HQL per ottenere i punteggi totali dei giocatori nel mese corrente
			String hql = "SELECT p, SUM(s.score) " + "FROM ScoreEntity s "
					+ "JOIN PlayerEntity p ON s.playerId = p.nickname "
					+ "WHERE MONTH(s.createdAt) = MONTH(CURRENT_DATE) " + "AND YEAR(s.createdAt) = YEAR(CURRENT_DATE) "
					+ "GROUP BY p.nickname " + "ORDER BY SUM(s.score) DESC";

			// Esegui la query
			Query<Object[]> query = session.createQuery(hql);
			List<Object[]> result = query.list();

			// Creiamo una lista di PlayerEntity e impostiamo il punteggio totale
			for (Object[] row : result) {
				PlayerEntity player = (PlayerEntity) row[0];
				Integer score = (Integer) row[1];

				// Aggiungi il punteggio totale all'oggetto player
				player.setTotalScore(score); // Imposta il punteggio totale nel campo 'totalScore'
				playerScores.add(player);
			}

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}

		return playerScores;
	}

	// saveScore
}
