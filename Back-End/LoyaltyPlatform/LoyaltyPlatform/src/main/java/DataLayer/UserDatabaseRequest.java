package DataLayer;

/**
 * Classe di gestione di connessione e richieste al Database da parte di chi usa la piattaforma
 */
public class UserDatabaseRequest extends DatabaseRequest {
    public UserDatabaseRequest(){
        super("127.0.0.1", "root", "IDS", "");
    }
}
