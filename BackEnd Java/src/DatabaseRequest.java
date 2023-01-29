import java.sql.*;

public class DatabaseRequest {
	private static String Host = "127.0.0.1";
	private static String User = "root";
	private static String Database = "IDS";
	private static String Password = "";

	private static boolean LastExecuteDone = false;
	private static boolean ConnectionIsOpen = false;

	private static Connection connection;
	private static Statement statement;

	public DatabaseRequest(String host, String user, String database, String password) {
		Host = host;
		User = user;
		Database = database;
		Password = password;
	}

	/**
	 * Questo metodo apre una connessione, se è già aperta la tiene aperta
	 * @return se la connessione è stata aperta o meno
	 */
	private static final boolean OpenConnection() {

		try {

			//Se la connessione è già aperta
			if(ConnectionIsOpen) return true;

			//System.out.println("Aperta connection");

			//Creo la connessione al Database
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + Host + "/" + Database, User, Password);

			//Attivo la connessione al Database
			statement = connection.createStatement();

			//Mi segno che la connessione è aperta
			ConnectionIsOpen = true;


			return true;
		}
		catch(Exception e){

			//Chiudo la connessione in caso di errore
			CloseConnection();
			return false;
		}
	}

	/**
	 * Questo metodo chiude la connessione, se è già chiusa non fa niente
	 * @return se la connessione è stata chiusa o meno
	 */
	private static final boolean CloseConnection() {
		try{

			//Se la connessione è già chiusa
			if(!ConnectionIsOpen) return true;

			//Chiudo la connessione
			connection.close();

			//Mi segno che la connessione è aperta
			ConnectionIsOpen = false;

			return true;
		}
		catch(Exception e){ return false; }

	}

	/**
	 * Questo metodo invia una richiesta al Database, se ottengo una Query, la tengo dentro un oggetto DatabaseQuery
	 * @param Command
	 * @return query ottenuta, altrimenti un null
	 */
	public static final DatabaseQuery SendRequest(String Command){

		OpenConnection();

		try {

			//Mi segno come default che l' esecuzione è andata a buon fine
			LastExecuteDone = true;

			//Eseguo la query e se non otterrò nessun risultato ridò null
			if(!statement.execute(Command)){
				CloseConnection();
				return null;
			}

			DatabaseQuery Response = new DatabaseQuery(statement.executeQuery(Command));

			CloseConnection();

			//Eseguo il comando che mi interessa e ridò il risultato
			return Response;

		}

		catch (Exception e) {

			System.out.println(e);

			CloseConnection();
			LastExecuteDone = false;
			return null;
		}
	}

	/**
	 * Metodo capace di eseguire più comandi di insert, delete, select ed update in sequenza, se un comando fallisce, nel DB torna tutto
	 * come prima, come se il metodo non fosse mai partito. Questo VALE SOLO PER LE RIGHE di una tabella. Se tutto è andato a buon fine,
	 * ottengo le query richieste, se ho lanciato dei select.
	 * @param Commands
	 * @return lista di DatabaseQuery se tutto è andato bene, se devo avere una query ho un oggetto DatabaseQuery sennò un null. Se non è andato
	 * tutto bene, ottengo un null, nessun array.
	 */
	public static final DatabaseQuery[] SendTransaction(String[] Commands) {

		OpenConnection();

		try{
			try{

				connection.setAutoCommit(true);

				//Mi salvo tutti i risultati
				DatabaseQuery[] Results = new DatabaseQuery[Commands.length];

				connection.setAutoCommit(false);
				for(int i=0; i<Commands.length; i++){
					if(statement.execute(Commands[i])) Results[i] = new DatabaseQuery(statement.executeQuery(Commands[i]));
					else Results[i] = null;
				}
				connection.commit();

				connection.setAutoCommit(true);

				CloseConnection();

				return Results;
			}

			catch(SQLException e){

				System.out.println(e);

				connection.rollback();
				connection.setAutoCommit(true);
				CloseConnection();
				LastExecuteDone = false;
				return null;
			}
		}

		catch(Exception e) {
			System.out.println("PROBLEM TO ROLLBACK:");
			System.out.println(e);

			CloseConnection();
			LastExecuteDone = false;
			return null;
		}
	}

	/**
	 * Ottengo l' esito dell' esecuzione dell' ultima richiesta al Database
	 * @return ultimo esito
	 */
	public static final boolean GetLastExecuteDone(){ return LastExecuteDone; }

	/**
	 * Ottengo lo stato di connessione aperta
	 * @return se la connessione è aperta
	 */
	public static final boolean GetConnectionIsOpen(){ return ConnectionIsOpen; }
}

/**
 * Classe di gestione di connessione e richieste al Database da parte di chi usa la piattaforma
 */
class UserDatabaseRequest extends DatabaseRequest {
	public UserDatabaseRequest(){
		super("127.0.0.1", "root", "IDS", "");
	}
}

/**
 * Classe di gestione di connessione e richieste al Database da parte di chi gestisce la piattaforma
 */
class AdministratorDatabaseRequest extends DatabaseRequest {
	public AdministratorDatabaseRequest(){
		super("127.0.0.1", "root", "IDS", "");
	}
}
