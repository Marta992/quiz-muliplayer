package old;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


public class ExampleDao {
//	//creazione della sessione di hibernate per ottenere la connessione al database
//		private static SessionFactory factory;
//
//		static {
//			try {
//				// configuriamo la factory con il file di configurazione di hibernate
//				factory = new Configuration().configure().buildSessionFactory();
//			} catch (Throwable ex) {
//				System.out.println("Connessione fallita dell'oggetto session factory" + ex);
//				throw new ExceptionInInitializerError(ex);
//			}
//		}
//
//		// metodo per aggiungere un utente al database
//		public Integer addUser(String username, String email, int age) {
//			Session session = factory.openSession();
//			Transaction tx = null;
//			Integer userId = null;
//			try {
//				// inizio della transazione garantendo l'integrita dei dati
//				tx = session.beginTransaction();
//				// creazione di una istanza della classe user
//				User user = new User();
//				user.setUsername(username);
//				user.setEmail(email);
//				user.setAge(age);
//
//				// salviamo utente sul database e ci faccioamo tornare il valore dell id
//				userId = (Integer) session.save(user);
//				// confwermiamo la transazione
//				tx.commit();
//
//			} catch (Exception e) {
//				// annullamento della transazione
//				e.printStackTrace();
//				// altro codice
//				if (tx != null) {
//					tx.rollback(); // annullare transazione
//				}
//			} finally {
//				// chiudiamo la sessione di lavoro e rilasciamo le risorse occupate
//				session.close();
//
//			}
//			return userId;
//		}
//
//		// metodo per aggiornare i dati di un utente nel database
//		public void updateUser(Integer userId, String username, String email, int age) {
//			Session session = factory.openSession();
//			Transaction tx = null;
//			try {
//				tx = session.beginTransaction();
//				// otteniamo l'utente dal database utilizzando il suo userId
//				User user = session.get(User.class, userId);
//				// aggiporniamo i dati di questo utente
//				user.setUsername(username);
//				user.setEmail(email);
//				user.setAge(age);
//
//				session.update(user);
//				tx.commit();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				if (tx != null) {
//					tx.rollback();
//
//				}
//			} finally {
//				session.close();
//			}
//		}
//
//		// metodo per eliminare un utente
//		public void deleteUser(Integer userId) {
//			Session session = factory.openSession();
//			Transaction tx = null;
//			try {
//				tx = session.beginTransaction();
//				// otteniamo lo user dal database usando userId
//				User user = session.get(User.class, userId);
//				// elimina l'utente
//				session.delete(user);
//				tx.commit();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				if (tx != null) {
//					tx.rollback();
//				}
//			} finally {
//				session.close();
//			}
//		}
//		//metodo per ottenere un singolo utente dal dfatabase
//		public  User getUser(Integer userId) {
//			Session session = factory.openSession();
//			Transaction tx = null;
//			User user = null;
//			try {
//				tx = session.beginTransaction();
//				user = session.get(User.class, userId);
//				tx.commit();
//			} catch (Exception e) {
//				e.printStackTrace();
//				if (tx != null) {
//					tx.rollback();
//				}
//					
//			}finally {
//				session.close();
//			}
//			return user;
//		}
//		//metodo per ottenere tutti gli utenti del database
//		public List<User> getAllUsers() {
//			Session session = factory.openSession();
//			Transaction tx = null;
//			List<User> users = null;
//			try {
//				tx = session.beginTransaction();
//				//otteniamo tutti gli utenti QUERY
//				users = session.createQuery("FROM User").list();
//				tx.commit();
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//				if (tx != null) {
//					tx.rollback();
//				}
//			}finally {
//				session.close();
//			}
//			return users;
//		}
}
