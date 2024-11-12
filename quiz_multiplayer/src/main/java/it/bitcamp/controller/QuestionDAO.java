package it.bitcamp.controller;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import it.bitcamp.model.QuestionEntity;
import old.Question;

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
	
	public Integer addQuestion(String questionText, String option1, String option2, String option3, String option4, int correctOption) {
		Session session = factory.openSession();
		Transaction tx = null;
		QuestionEntity question = null;
		Integer questionId = null;
		try {
			// inizio della transazione garantendo l'integrita dei dati
			tx = session.beginTransaction();
			// creazione di una istanza della classe question
			question.setQuestionText(questionText);
			question.setOption1(option1);
			question.setOption2(option2);
			question.setOption3(option3);
			question.setOption4(option4);
			question.setCorrectOption(correctOption);

			// salviamo domanda sul database e ci faccioamo tornare il valore dell id
			questionId = (Integer) session.save(question);
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
		return questionId;
	}
	
	public void deleteQuestion(Integer questionId) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			// otteniamo la domanda dal database usando questionid
			Question question = session.get(Question.class, questionId);
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
	
	public List<Question> getAllQuestion() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Question> questions = null;
		try {
			tx = session.beginTransaction();
			//otteniamo tutte le domande QUERY
			questions = session.createQuery("FROM Question").list();
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
	public List<Question> getRandomQuestions(int numQuestions) {
	    Session session = factory.openSession();
	    Transaction tx = null;
	    List<Question> questions = null;
	    try {
	        tx = session.beginTransaction();
	        // Query  getRandomQuestions(5) per ottenere una lista di 5 domande casuali, o un altro numero passato come argomento.
	        Query<Question> query = session.createQuery("FROM Question ORDER BY RAND()", Question.class);
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
