package it.bitcamp.controller;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import it.bitcamp.model.QuestionEntity;

public class QuestionDAO {
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
	
	public Integer addQuestion(String questionText, String correctOption, String otherOption1, String otherOption2, String otherOption3) {
		Session session = factory.openSession();
		Transaction tx = null;
		QuestionEntity question = null;
		Integer questionId = null;
		try {
			// inizio della transazione garantendo l'integrita dei dati
			tx = session.beginTransaction();
			// creazione di una istanza della classe question
			question = new QuestionEntity();
			question.setQuestionText(questionText);
			question.setCorrectOption(correctOption);
			question.setOtherOption1(otherOption1);
			question.setOtherOption2(otherOption2);
			question.setOtherOption3(otherOption3);

			// salviamo domanda sul database e ci faccioamo tornare il valore dell id
			questionId = (Integer) session.save(question);
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
		return questionId;
	}
	
	public void deleteQuestion(Integer questionId) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			// otteniamo la domanda dal database usando questionid
			QuestionEntity question = session.get(QuestionEntity.class, questionId);
			// elimina la domanda
			session.delete(question);
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
	
	public List<QuestionEntity> getAllQuestion() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<QuestionEntity> questions = null;
		try {
			tx = session.beginTransaction();
			//otteniamo tutte le domande QUERY
			questions = session.createQuery("FROM QuestionEntity", QuestionEntity.class).list();
			tx.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		}finally {
			session.close();
		}
		return questions;
	}
	
	//getRandomQuestions
	public List<QuestionEntity> getRandomQuestions(int numQuestions) {
	    Session session = factory.openSession();
	    Transaction tx = null;
	    List<QuestionEntity> questions = null;
	    try {
	        tx = session.beginTransaction();
	        // Query  getRandomQuestions(5) per ottenere una lista di 5 domande casuali, o un altro numero passato come argomento.
	        Query<QuestionEntity> query = session.createQuery("FROM QuestionEntity ORDER BY RAND()", QuestionEntity.class);
	        query.setMaxResults(numQuestions); // Limita il numero di risultati al valore desiderato
	        questions = query.list();
	        tx.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        if (tx != null) {
	            tx.rollback();
	        }
	    } finally {
	        session.close();
	    }
	    return questions;
	}
}
