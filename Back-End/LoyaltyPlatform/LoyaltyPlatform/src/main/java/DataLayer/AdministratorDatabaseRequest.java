package DataLayer;

/**
 * Classe di gestione di connessione e richieste al Database da parte di chi gestisce la piattaforma
 */
class AdministratorDatabaseRequest extends DatabaseRequest {
    public AdministratorDatabaseRequest(){
        super("127.0.0.1", "root", "IDS", "");
    }
}
