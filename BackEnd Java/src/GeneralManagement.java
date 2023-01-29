import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Classe singleton con il compito di: aggiungere, ridare e togliere elementi del dominio. Gli oggetti ridati invece si occupano di
 * ridare le informazioni e modificarle. Gli oggetti, se non esistono nel Database, potrebbero "esplodere" (cioè lanciare un eccezione).
 * Questo perchè la ricerca dei dati di un oggetto non la fa questa classe qui, ma il costruttore dell' oggetto stesso.
 */
public class GeneralManagement {

    //SOGGETTI PRINCIPALI

    //Azienda Fatto e Testato
    public static final boolean AddAzienda(String NomeAzienda, String PartitaIVA, String Password, String SitoWeb, String Email){

        //Già esiste
        try{
            new Azienda(PartitaIVA, Password);
            return false;
        }

        //Non esiste ancora
        catch(IllegalArgumentException e){

            UserDatabaseRequest.SendTransaction(new String[] {"insert into azienda (NomeAzienda, Email, PartitaIVA, Password, SitoWeb) VALUE " +
                    "('"+NomeAzienda+"', '"+Email+"', '"+PartitaIVA+"', '"+Password+"', '"+SitoWeb+"')",

                    "insert into piano_vantaggi (PartitaIVAStart, PartitaIVAFinish, PuntiPercentual, PuntiPercentualVIP, PuntiLivelloPercentual, PuntiLivelloPercentualVIP, Recensione, Prenotazione, Coupon, ModuloReffeal, PercentualBase) VALUES " +
                            "('"+PartitaIVA+"', '"+PartitaIVA+"', 0, 0, 0, 0, false, false, false, false, 1)"});

            return UserDatabaseRequest.GetLastExecuteDone();
        }
    }
    public static final Azienda GetAzienda(String PartitaIVA, String Password){
        try{

            Azienda azienda = new Azienda(PartitaIVA, Password);
            return azienda;
        }
        catch (IllegalArgumentException e){
            return null;
        }
    }
    public static final boolean RemoveAzienda(String PartitaIVA, String Password){
        try{
            new Azienda(PartitaIVA, Password);

            UserDatabaseRequest.SendTransaction(new String[]{
                    "delete from azienda where PartitaIVA = '"+PartitaIVA+"' and Password = '"+Password+"'",
                    "delete from piano_vantaggi where PartitaIVAStart = '"+PartitaIVA+"' or PartitaIVAFinish = '"+PartitaIVA+"'",
                    "delete from codici_coupon where PartitaIVA = '"+PartitaIVA+"'",
                    "delete from dipendente where PartitaIVA = '"+PartitaIVA+"'"});

            return UserDatabaseRequest.GetLastExecuteDone();
        }

        catch (IllegalArgumentException e){ return false; }
    }

    //Utente Fatto e Testato
    public static final boolean AddUtente(String Nome, String Cognome, int Età, String Regione, String Città, String Comune, boolean Sesso, String Email, String NumeroDiTelefono, String Nickname, String Password, boolean StatoVIP, String CodiceCarta, boolean ConsensoDati) {

        //Già esiste
        try{
            new Utente(Nickname, Password);
            return false;
        }

        //Non esiste ancora
        catch(IllegalArgumentException e){

            UserDatabaseRequest.SendRequest("insert into utente (Nome, Cognome, Età, Regione, Città, Comune, Sesso, Email, NumeroDiTelefono, Nickname, Password, StatoVIP, CodiceCarta, ConsensoDati) VALUE " +
                    "('"+Nome+"', '"+Cognome+"', "+Età+", '"+Regione+"', '"+Città+"', '"+Comune+"', "+String.valueOf(Sesso)+", '"+Email+"', '"+NumeroDiTelefono+"', '"+Nickname+"', '"+Password+"', "+String.valueOf(StatoVIP)+", '"+CodiceCarta+"', "+String.valueOf(ConsensoDati)+")");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
    }
    public static final Utente GetUtente(String Nickname, String Password) {
        try{
            return new Utente(Nickname, Password);
        }
        catch(IllegalArgumentException e) { return null; }
    }
    public static final boolean RemoveUtente(String Nickname, String Password) {
        try{
            new Utente(Nickname, Password);

            UserDatabaseRequest.SendRequest("delete from utente where Nickname = '"+Nickname+"' and Password = '"+Password+"'");

            return UserDatabaseRequest.GetLastExecuteDone();

        }
        catch(IllegalArgumentException e) { return false; }
    }

    //Dipendente Fatto e Testato
    public static final boolean AddDipendente(String Nome, String Cognome, String Email, String PartitaIVA, String Password, String CodiceDipendente, String NumeroDiTelefono) {
        try {
            if(UserDatabaseRequest.SendRequest("select SitoWeb from azienda where PartitaIVA = '"+PartitaIVA+"'").GetRowCount() == 0) return false;

            new Dipendente(CodiceDipendente, PartitaIVA, Password);
            return false;
        }
        catch(IllegalArgumentException e){

            UserDatabaseRequest.SendRequest("insert into dipendente (Nome, Cognome, Email, PartitaIVA, Password, CodiceDipendente, NumeroDiTelefono) VALUES " +
                    "('"+Nome+"', '"+Cognome+"', '"+Email+"', '"+PartitaIVA+"', '"+Password+"', '"+CodiceDipendente+"', '"+NumeroDiTelefono+"')");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
    }
    public static final Dipendente GetDipendente(String CodiceDipendente, String PartitaIVA, String Password) {
        try{ return new Dipendente(CodiceDipendente, PartitaIVA, Password); }
        catch(IllegalArgumentException e) { return null; }
     }
    public static final boolean RemoveDipendente(String CodiceDipendente, String PartitaIVA, String Password) {
        try{
            new Dipendente(CodiceDipendente, PartitaIVA, Password);

            UserDatabaseRequest.SendRequest("delete from dipendente where CodiceDipendente = '"+CodiceDipendente+"' and PartitaIVA = '"+PartitaIVA+"' and Password = '"+Password+"'");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
        catch(IllegalArgumentException e) { return false; }
    }

    //Piano fedeltà Fatto e Testato
    public static final boolean AddPianoFedeltà(String PartitaIVA, float PuntiPercentual, float PuntiPercentualVIP, float PuntiLivelloPercentual, float PuntiLivelloPercentualVIP, boolean Recensione, boolean Prenotazione, boolean VantaggioCoupon, boolean VantaggioModuloReffeal) {

        if(PuntiPercentual < 0 || PuntiPercentual > 1 ||
                PuntiPercentualVIP < 0 || PuntiPercentualVIP > 1 ||
                PuntiLivelloPercentual < 0 || PuntiLivelloPercentual > 1 ||
                PuntiPercentualVIP < 0 || PuntiPercentualVIP > 1) return false;

        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select Recensione from piano_vantaggi where PartitaIVAStart = '"+PartitaIVA+"' and PartitaIVAFinish = '"+PartitaIVA+"'");
        if(Response.GetRowCount() == 1) return false;

        //Arrivati fin qua, posso creare i 2 piani fedeltà
        UserDatabaseRequest.SendRequest(
                "insert into piano_vantaggi (PartitaIVAStart, PartitaIVAFinish, PuntiPercentual, PuntiPercentualVIP, PuntiLivelloPercentual, PuntiLivelloPercentualVIP, Recensione, Prenotazione, Coupon, ModuloReffeal, PercentualBase, Scadenza) VALUES " +
                        "('"+PartitaIVA+"', '"+PartitaIVA+"', "+PuntiPercentual+", "+PuntiPercentualVIP+", "+PuntiLivelloPercentual+", "+PuntiLivelloPercentualVIP+", "+String.valueOf(Recensione)+", "+String.valueOf(Prenotazione)+", "+String.valueOf(VantaggioCoupon)+", "+String.valueOf(VantaggioModuloReffeal)+", 1, null)");

        return UserDatabaseRequest.GetLastExecuteDone();
    }
    public static final PianoFedeltà GetPianoFedeltà(String PartitaIVA) {
        try{ return new PianoFedeltà(PartitaIVA); }
        catch(IllegalArgumentException e){ return null; }
    }
    public static final boolean RemovePianoFedeltà(String PartitaIVA) {
        try{
            new PianoFedeltà(PartitaIVA);

            UserDatabaseRequest.SendRequest("delete from piano_vantaggi where PartitaIVAStart = '"+PartitaIVA+"' and PartitaIVAFinish = '"+PartitaIVA+"'");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
        catch(IllegalArgumentException e){ return false; }
    }

    //Collaborazione Fatto e Testato
    public static final boolean AddCollaborazione(String PartitaIVAStart, String PartitaIVAFinish, float PuntiPercentual, float PuntiPercentualVIP, float PuntiLivelloPercentual, float PuntiLivelloPercentualVIP, float PercentualBase, Timestamp Scadenza) {

        if(PuntiPercentual < 0 || PuntiPercentual > 1 ||
                PuntiPercentualVIP < 0 || PuntiPercentualVIP > 1 ||
                PuntiLivelloPercentual < 0 || PuntiLivelloPercentual > 1 ||
                PuntiPercentualVIP < 0 || PuntiPercentualVIP > 1 ||
                PercentualBase < 0 || PercentualBase > 1) return false;

        if(Scadenza.compareTo(new Timestamp(System.currentTimeMillis())) < 0) return false;

        boolean Exist1 = true;
        boolean Exist2 = true;

        //Se una dei 2 piani fedeltà non esiste, non posso creare i piani di collaborazione
        try{ new PianoFedeltà(PartitaIVAStart); }
        catch(IllegalArgumentException e){ Exist1 = false; }

        try{ new PianoFedeltà(PartitaIVAFinish); }
        catch(IllegalArgumentException e){ Exist2 = false; }

        if(!Exist1 || !Exist2) return false;

        //Arrivati fin qua, posso creare i 2 piani fedeltà
        UserDatabaseRequest.SendTransaction(new String[]{
                "insert into piano_vantaggi (PartitaIVAStart, PartitaIVAFinish, PuntiPercentual, PuntiPercentualVIP, PuntiLivelloPercentual, PuntiLivelloPercentualVIP, Recensione, Prenotazione, Coupon, ModuloReffeal, PercentualBase, Scadenza) VALUES " +
                        "('" + PartitaIVAStart + "', '" + PartitaIVAFinish + "', " + PuntiPercentual + ", " + PuntiPercentualVIP + ", " + PuntiLivelloPercentual + ", " + PuntiLivelloPercentualVIP + ", false, false, false, false, " + PercentualBase + ", '" + Scadenza.toString() + "'), " +
                        "('" + PartitaIVAFinish + "', '" + PartitaIVAStart + "', " + PuntiPercentual + ", " + PuntiPercentualVIP + ", " + PuntiLivelloPercentual + ", " + PuntiLivelloPercentualVIP + ", false, false, false, false, " + PercentualBase + ", '" + Scadenza.toString() + "')",

                "insert into cassetta_collaborazioni (PartitaIVARichiedente, PartitaIVARicevente, AccettazioneRicevente) VALUES " +
                        "('"+PartitaIVAStart+"', '"+PartitaIVAFinish+"', null)"});

        return true;
    }
    public static final Collaborazione GetCollaborazione(String PartitaIVAStart, String PartitaIVAFinish) {
        try{ return new Collaborazione(PartitaIVAStart, PartitaIVAFinish); }
        catch(IllegalArgumentException e){ return null; }
    }
    public static final boolean RemoveCollaborazione(String PartitaIVAStart, String PartitaIVAFinish) {

        try{
            new Collaborazione(PartitaIVAStart, PartitaIVAFinish);

            UserDatabaseRequest.SendRequest("delete from piano_vantaggi where PartitaIVAStart = '"+PartitaIVAStart+"' and PartitaIVAFinish = '"+PartitaIVAFinish+"'");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
        catch(IllegalArgumentException e){ return false; }

    }

    //Metodo che ogni volta che viene richiamato, elimina le collaborazioni scadute dal Database
    public static final boolean DestroyCollaborazioniScadute(){
        DatabaseQuery[] Responses = UserDatabaseRequest.SendTransaction(new String[]{
                "select PartitaIVAStart, PartitaIVAFinish, Scadenza from piano_vantaggi",
                "select PartitaIVARichiedente, PartitaIVARicevente from cassetta_collaborazioni where AccettazioneRicevente = false"
        });

        ArrayList<String> Result = new ArrayList<>();

        for(int i=0; i<Responses[0].GetRowCount(); i++)
            if(Timestamp.valueOf(Responses[0].GetValue(i, "Scadenza")).compareTo(new Timestamp(System.currentTimeMillis())) < 0) Result.add("(PartitaIVAStart = '"+Responses[0].GetValue(i, "PartitaIVAStart")+"' and PartitaIVAFinish = '"+Responses[0].GetValue(i, "PartitaIVAFinish")+"')");

        String s;
        for(int i=0; i<Responses[1].GetRowCount(); i++){

            s = "(PartitaIVAStart = '"+Responses[0].GetValue(i, "PartitaIVAStart")+"' and PartitaIVAFinish = '"+Responses[0].GetValue(i, "PartitaIVAFinish")+"')";
            if(!Result.contains(s)) Result.add(s);

        }

        String Stringa = "";

        for(int i=0; i<Result.size(); i++){
            Stringa += Result.get(i);

            if(i < Result.size()-1) Stringa += " or ";
        }

        UserDatabaseRequest.SendTransaction(new String[]{"delete from piano_vantaggi where "+Stringa,
                "delete from cassetta_collaborazioni where AccettazioneRicevente = false or AccettazioneRicevente = true"});

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    //Recensione sito
    public static final boolean AddRecensioneSito(String PartitaIVA, String Nickname, String Descrizione, float Stelle, Timestamp TemporalPoint) {

        if(Stelle < 0.0f || Stelle > 5.0f) return false;

        try{
            new Recensione(PartitaIVA, Nickname);
            return false;
        }
        catch(IllegalArgumentException e){

            DatabaseQuery[] Response = UserDatabaseRequest.SendTransaction(new String[]{
                    "select Recensione from piano_vantaggi where PartitaIVAStart = '"+PartitaIVA+"' and PartitaIVAFinish = '"+PartitaIVA+"'",
                    "select StatoVIP from utente where Nickname ='"+Nickname+"'"});

            if(Response[0].GetRowCount() == 0 || Response[0].GetRowCount() == 0 || Response[0].GetValue(0, "Recensione").equals("0")) return false;

            UserDatabaseRequest.SendRequest("insert into recensione_sito (PartitaIVA, Nickname, Descrizione, Stelle, MomentoTemporale) VALUES " +
                    "('"+PartitaIVA+"', '"+Nickname+"', '"+Descrizione+"', "+Stelle+", '"+TemporalPoint.toString()+"')");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
    }
    public static final Recensione GetRecensioneSito(String PartitaIVA, String Nickname) {
        try{ return new Recensione(PartitaIVA, Nickname); }
        catch(IllegalArgumentException e){ return null; }
    }
    public static final boolean RemoveRecensioneSito(String PartitaIVA, String Nickname) {

        try{
            new Recensione(PartitaIVA, Nickname);

            UserDatabaseRequest.SendRequest("delete from recensione_sito where PartitaIVA = '"+PartitaIVA+"' and Nickname = '"+Nickname +"'");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
        catch(IllegalArgumentException e){ return false; }
    }

    //Coupon Fatto e Testato
    public static final boolean AddCoupon(String Codice, String PartitaIVA, String Nickname, float Costo, float PercentualeSconto, float MinimoPrezzo) {
        try{
            DatabaseQuery Response = UserDatabaseRequest.SendRequest("select StatoVIP from utente where Nickname = '"+Nickname+"'");

            if(Response.GetRowCount() == 0) return false;

            new Coupon(Codice, PartitaIVA);
            return false;
        }
        catch(IllegalArgumentException e){

            //Controllo che il Nickname e la PartitaIVA esistono
            DatabaseQuery[] Response = UserDatabaseRequest.SendTransaction(new String[]{
                    "select Nickname from utente where Nickname = '"+Nickname+"'",
                    "select PartitaIVA from azienda where PartitaIVA = '"+PartitaIVA+"'"});

            if(Response[0].GetRowCount() == 0 || Response[1].GetRowCount() == 0) return false;

            //Nel caso in cui sono sia l' azienda che il Nickname, procedo con l' iscrizione
            UserDatabaseRequest.SendRequest("insert into codici_coupon (Codice, PartitaIVA, Nickname, Costo, Percentuale, MinimoPrezzoAttivazione) VALUES " +
                    "('"+Codice+"', '"+PartitaIVA+"', '"+Nickname+"', "+Costo+", "+PercentualeSconto+", "+MinimoPrezzo+")");

            return UserDatabaseRequest.GetLastExecuteDone();
        }
    }
    public static final Coupon GetCoupon(String Codice, String PartitaIVA) {
        try{
            return new Coupon(Codice, PartitaIVA);
        }
        catch(IllegalArgumentException e){ return null; }
    }
    public static final boolean RemoveCoupon(String Codice, String PartitaIVA) {
        try{
            new Coupon(Codice, PartitaIVA);

            UserDatabaseRequest.SendRequest("delete from codici_coupon where Codice = '"+Codice+"' and PartitaIVA = '"+PartitaIVA+"'");

            return UserDatabaseRequest.GetLastExecuteDone();

        }
        catch(IllegalArgumentException e){ return false; }
    }

    //Ancora da fare

    //Ponte

    //SOGGETTI SECONDARI

    //ModuloReffeal
    public static final boolean AddModuloReffeal(String PartitaIVA, String NicknameBeneficario, float ScontoPercentuale, float PercentualeTrattenuta,  String Codice) { return false; }
    public static final Object GetModuloReffeal(String Codice) { return null; }
    public static final boolean RemoveModuloReffeal(String Codice) { return false; }

    //Prenotazione
    public static final boolean AddPrenotazione(String Nickname, String PartitaIVA, Timestamp Inizio, Timestamp Fine, String Descrizione) { return false; }
    public static final Object GetPrenotazione(String Nickname, String PartitaIVA, Timestamp Inizio, Timestamp Fine) { return null; }
    public static final boolean RemovePrenotazione(String Nickname, String PartitaIVA, Timestamp Inizio, Timestamp Fine) { return false; }

    //AdvantageUser

    //LISTE

    //Posizioni punto vendita
    public static final boolean AddPosizione(String PartitaIVA, Object posizione) { return false; }
    public static final Object GetPosizione(String PartitaIVA, Object posizione) { return null; }
    public static final boolean RemovePosizione(String PartitaIVA, Object posizione) { return false; }

    //Premi a punti

}
