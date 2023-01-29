import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Scanner;

public class UI {

    static Utente utente = null;
    static Azienda azienda = null;
    static Dipendente dipendente = null;
    static String[] Fields;

    //static int i, r, f;

    static float k;

    public static void main(String[] args){

        int i, r;

        while(true){

            //Scelta del personaggio (Azienda, Utente, o Dipendente)
            i = UI.Choice("Sei?", new String[]{
                    "Un punto vendita",
                    "Un cliente",
                    "Un dipendente"});

            r = UI.Choice("Ti devi iscrivere, identificare od eliminare l' account?", new String[]{
                    "Identificare",
                    "Registrare",
                    "Eliminare"});

            utente = null;
            azienda = null;
            dipendente = null;

            //System.out.println(i);
            //System.out.println(r);

            //Punto vendita Login
            if(i == 0 && r == 0) if(UI.LoginAzienda()) UI.AccountAzienda();

            //Punto vendita Register
            if(i == 0 && r == 1) if(UI.RegisterAzienda()) UI.AccountAzienda();

            //Punto vendita DeleteAccount
            if(i == 0 && r == 2) UI.DeleteAzienda();

            //Cliente Login
            if(i == 1 && r == 0) if (UI.LoginUtente()) UI.AccountUtente();

            //Cliente Register
            if(i == 1 && r == 1) if(UI.RegisterUtente()) UI.AccountUtente();

            //Cliente DeleteAccount
            if(i == 1 && r == 2)  UI.DeleteUtente();

            //Dipendente Login
            if(i == 2 && r == 0) if(UI.LoginDipendente()) UI.AccountDipendente();

            //Dipendente Register
            if(i == 2 && r == 1) if(UI.RegisterDipendente()) UI.AccountDipendente();

            //Dipendente DeleteAccount
            if(i == 2 && r == 2)  UI.DeleteDipendente();

            System.out.println("");
        }
    }

    private static int Choice(String Question, String[] Choices){
        System.out.println(Question);

        for(int i=0; i<Choices.length; i++) System.out.println((i+1)+") "+Choices[i]);

        Scanner scan = new Scanner(System.in);
        String Response;
        int Result;

        while(true){

            System.out.print("Risposta: ");
            Response = scan.nextLine();
            System.out.print("");

            try{
                Result = Integer.parseInt(Response)-1;
                if(Result < 0 || Result >= Choices.length) continue;
                break;
            }

            catch(Exception e){ continue; }
        }

        System.out.println("\n");

        return Result;
    }

    private static String[] CompileForm(String Title, String[] Fields){

        System.out.println(Title);

        String[] Result = new String[Fields.length];

        Scanner scan = new Scanner(System.in);

        for(int i=0; i<Result.length; i++){

            System.out.print(Fields[i]+": ");
            Result[i] = scan.nextLine();
            System.out.print("");
        }

        System.out.println("");

        return Result;
    }

    public static boolean RegisterUtente(){

        Fields = UI.CompileForm("Registrazione utente", new String[]{
                "Nome", "Cognome", "Numero di telefono", "Città", "Età (in numeri)", "Regione", "Comune",
                "Sei maschio (1 = si/altro = no)?", "Email", "Nickname", "Password",
                "Se accetti, i tuoi dati VERRANNO INVIATI ad altre aziende SOLO per PROPORTI NUOVI PRODOTTI. Accetti (1 = si/altro = no)?"
        });

        String[] Codici = UserDatabaseRequest.SendRequest("select CodiceCarta from utente").GetColumn("CodiceCarta");

        boolean search = true;

        byte[] array;
        String NuovoCodice = "";

        //Continuo a generare codici finchè non ne trovo uno nuovo
        while(search){

            array = new byte[100];
            new Random().nextBytes(array);
            NuovoCodice = new String(array, Charset.forName("UTF-8"));

            search = false;

            for(String s: Codici) if(s.equals(NuovoCodice)){
                search = true;
                break;
            }
        }

        try{
            if(GeneralManagement.AddUtente(Fields[0], Fields[1], Integer.parseInt(Fields[4]), Fields[5], Fields[3],
                    Fields[6], Fields[7].equals("1"), Fields[8], Fields[2], Fields[9], Fields[10],
                    false, NuovoCodice, Fields[11].equals("1"))){

                utente = GeneralManagement.GetUtente(Fields[9], Fields[10]);

                return true;
            }
        }
        catch(Exception e){}

        System.out.println("Dati errati od utente già presente");

        return false;
    }
    public static boolean RegisterAzienda(){
        Fields = UI.CompileForm("Registrazione punto vendita", new String[]{
                "Nome", "Partita IVA", "Email", "Link del tuo sito Web", "Password"
        });

        if(GeneralManagement.AddAzienda(Fields[0], Fields[1], Fields[4], Fields[3], Fields[2])){

            azienda = GeneralManagement.GetAzienda(Fields[1], Fields[4]);

            return true;
        }

        System.out.println("Dati errati od azienda già presente");

        return false;
    }
    public static boolean RegisterDipendente(){
        Fields = UI.CompileForm("Registrazione punto vendita", new String[]{
                "Nome", "Cognome", "Creati un tuo codice", "Email", "Partita IVA del punto vendita in cui lavori", "Numero di telefono", "Password"
        });

        String[] Codici = UserDatabaseRequest.SendRequest("select CodiceDipendente from dipendente").GetColumn("CodiceDipendente");

        boolean search = true;

        byte[] array;
        String NuovoCodice = "";

        //Continuo a generare codici finchè non ne trovo uno nuovo
        while(search){

            array = new byte[100];
            new Random().nextBytes(array);
            NuovoCodice = new String(array, Charset.forName("UTF-8"));

            search = false;

            for(String s: Codici) if(s.equals(NuovoCodice)){
                search = true;
                break;
            }
        }

        if(GeneralManagement.AddDipendente(Fields[0], Fields[1], Fields[3], Fields[4], Fields[6], NuovoCodice, Fields[5])){

            dipendente = GeneralManagement.GetDipendente(Fields[2], Fields[4], Fields[6]);
            return true;

        }

        System.out.println("Dati errati o dipendente già presente");

        return false;
    }

    public static boolean LoginUtente(){

        Fields = UI.CompileForm("Identificazione Utente", new String[]{
                "Nickname",
                "Password"});

        utente = GeneralManagement.GetUtente(Fields[0], Fields[1]);
        if(utente != null) System.out.println("Bentornato "+utente.getNome()+" "+utente.getCognome()+" detto anche '"+utente.getNickname()+"'");
        else System.out.println("Questo utente non esiste");
        return false;
    }
    public static boolean LoginAzienda(){
        Fields = UI.CompileForm("Identificazione Punto vendita", new String[]{
                "Partita IVA",
                "Password"});

        azienda = GeneralManagement.GetAzienda(Fields[0], Fields[1]);
        if(azienda != null) System.out.println("Punto vendita ("+utente.getNickname()+") identificato");
        else System.out.println("Questo punto vendita non esiste");
        return false;
    }
    public static boolean LoginDipendente(){

        Fields = UI.CompileForm("Identificazione Dipendente", new String[]{
                "Partita IVA per cui lavori",
                "Il tuo codice dipendente",
                "Password"});

        dipendente = GeneralManagement.GetDipendente(Fields[1], Fields[0], Fields[2]);
        if(dipendente != null) System.out.println("Bentornato al lavoro "+dipendente.getNome()+" "+dipendente.getCognome());
        else System.out.println("Questo dipendente non è registrato come lavoratore di questo punto vendita ("+dipendente.getPartitaIVA()+")");
        return false;
    }

    public static void DeleteUtente(){

        Fields = UI.CompileForm("Cancella Utente", new String[]{
                "Nickname",
                "Password"});

        if(GeneralManagement.RemoveUtente(Fields[0], Fields[1])) System.out.println("Utente cancellato");
        else System.out.println("Questo utente non esiste");
    }
    public static void DeleteAzienda(){

        Fields = UI.CompileForm("Cancella Punto Vendita", new String[]{
                "PartitaIVA",
                "Password"});

        if(GeneralManagement.RemoveAzienda(Fields[0], Fields[1])) System.out.println("Punto vendita cancellato");
        else System.out.println("Punto vendita non esiste");
    }
    public static void DeleteDipendente(){

        Fields = UI.CompileForm("Cancella Dipendente", new String[]{
                "CodiceDipendente",
                "PartitaIVA",
                "Password"});

        if(GeneralManagement.RemoveDipendente(Fields[0], Fields[1], Fields[2])) System.out.println("Dipendente cancellato");
        else System.out.println("Questo Dipendente non esiste");
    }

    public static boolean AggiuntaClientiDipendente(){

        System.out.println("Ora aggiungi i dati per il cliente");
        return RegisterUtente();
    }
    public static boolean RecensioneUtente(){

        Fields = UI.CompileForm("Recensione sito dall' utente "+utente.getNickname(), new String[]{
                "PartitaIVA",
                "Descrizione",
                "Stelle"});

        try{ return GeneralManagement.AddRecensioneSito(Fields[0], utente.getNickname(), Fields[1], Float.parseFloat(Fields[2]), new Timestamp(System.currentTimeMillis())); }
        catch(Exception e){ return false; }

    }
    public static boolean GestioneDatiUtente(){
        Fields = UI.CompileForm("Modifica dei dati dell' utente (se non vuoi modificare il parametro, basta premere Invio) "+utente.getNickname(), new String[]{
                "Nome (Actual = '"+utente.getNome()+"')",
                "Cognome (Actual = '"+utente.getCognome()+"')",
                "Numero di telefono (Actual = '"+utente.getNumeroDiTelefono()+"')",
                "Città (Actual = '"+utente.getCittà()+"')",
                "Età (Actual = "+utente.getEtà()+")",
                "Regione (Actual = '"+utente.getRegione()+"')",
                "Comune (Actual = '"+utente.getComune()+"')",
                "Sei maschio (1 = si/altro = no)? (Actual sei un maschio = "+utente.getMale()+")",
                "Email (Actual = "+utente.getEmail()+")",
                "Nickname (Actual = "+utente.getNickname()+")",
                "Accettazione (1 = si/altro = no)? (Al momento hai accettato = "+utente.getConsensoDati()+")"});

        //Sistemo la situazione, controllando quali sono quelli uguali e quelli no
        if(Fields[0].equals("")) Fields[0] = utente.getNome();
        if(Fields[1].equals("")) Fields[1] = utente.getCognome();
        if(Fields[2].equals("")) Fields[2] = utente.getNumeroDiTelefono();
        if(Fields[3].equals("")) Fields[3] = utente.getCittà();
        if(Fields[4].equals("")) Fields[4] = String.valueOf(utente.getEtà());
        if(Fields[5].equals("")) Fields[5] = utente.getRegione();
        if(Fields[6].equals("")) Fields[6] = utente.getComune();
        if(Fields[7].equals("")) Fields[7] = String.valueOf(utente.getMale());
        if(Fields[8].equals("")) Fields[8] = utente.getEmail();
        if(Fields[9].equals("")) Fields[9] = utente.getNickname();
        if(Fields[10].equals("")) Fields[10] = String.valueOf(utente.getConsensoDati());

        return utente.RequestSetData(Fields[9], Fields[0], Fields[1], Fields[2], Fields[8], utente.getStatoVIP(), utente.getCodiceCarta(), Fields[3], Integer.parseInt(Fields[4]), Fields[5], Fields[6], Boolean.parseBoolean(Fields[7]), Boolean.parseBoolean(Fields[10]));
    }
    public static boolean GestioneDatiAzienda(){
        Fields = UI.CompileForm("Modifica dei dati dell' azienda (se non vuoi modificare il parametro, basta premere Invio) "+azienda.getPartitaIVA(), new String[]{
                "Nome azienda (Actual = '"+azienda.getNomeAzienda()+"')",
                "Partita IVA (Actual = '"+azienda.getPartitaIVA()+"')",
                "Sito web (Actual = '"+azienda.getSitoWeb()+"')",
                "Email (Actual = "+azienda.getEmail()+")"
        });

        //Sistemo la situazione, controllando quali sono quelli uguali e quelli no
        if(Fields[0].equals("")) Fields[0] = azienda.getNomeAzienda();
        if(Fields[1].equals("")) Fields[1] = azienda.getPartitaIVA();
        if(Fields[2].equals("")) Fields[2] = azienda.getSitoWeb();
        if(Fields[3].equals("")) Fields[3] = azienda.getEmail();

        return azienda.RequestSetData(Fields[0], Fields[1], Fields[2], Fields[3]);
    }

    //Valido per tutte e 3 le categorie (utente, dipendente, punto vendita)
    public static boolean ResetPassword(){

        if(utente != null) {

            Fields = UI.CompileForm("Modifica password dell' utente "+utente.getNickname(), new String[]{"Nuova Password"});
            return utente.RequestSetPassword(Fields[0]);

        }

        else if(dipendente != null) {

            Fields = UI.CompileForm("Modifica password del dipendente "+dipendente.getCodiceDipendente()+" che lavora nel punto vendita "+dipendente.getPartitaIVA(), new String[]{"Nuova Password"});
            return dipendente.RequestSetPassword(Fields[0]);

        }
        else if(azienda != null) {

            Fields = UI.CompileForm("Modifica password del punto vendita "+azienda.getPartitaIVA(), new String[]{"Nuova Password"});
            return azienda.RequestSetPassword(Fields[0]);
        }

        return false;

    }

    public static boolean AddCollaborazioneAzienda(){

        Fields = UI.CompileForm("Aggiungi una collaborazione: "+utente.getNickname(), new String[]{
                "A quale partita IVA inviare la collaborazione",
                "Percentuale punti base",
                "Percentuale punti VIP",
                "Percentuale punti livello base",
                "Percentuale punti livello VIP",
                "Percentuale di Cash",
                "Inserire la data di scadenza di questa collaborazione"});

        return GeneralManagement.AddCollaborazione(azienda.getPartitaIVA(), Fields[0], Float.parseFloat(Fields[1]), Float.parseFloat(Fields[2]), Float.parseFloat(Fields[3]), Float.parseFloat(Fields[4]), Float.parseFloat(Fields[5]), Timestamp.valueOf(Fields[6]));

    }
    public static boolean AcceptCollaborazionAzienda(){

        int i, r;

        DatabaseQuery AttesaCollaborazioni = UserDatabaseRequest.SendRequest("select cc.PartitaIVARichiedente, pv.PuntiPercentual, pv.PuntiPercentualVIP, pv.PuntiLivelloPercentual, pv.PuntiLivelloPercentualVIP, pv.PercentualBase from cassetta_collaborazioni as cc inner join piano_vantaggi as pv on cc.PartitaIVARichiedente = pv.PartitaIVAStart where cc.PartitaIVARicevente = '"+azienda.getPartitaIVA()+"' and AccettazioneRicevente == null");

        System.out.println("Collaborazioni in attesa ("+AttesaCollaborazioni.GetRowCount()+")");


        for(int k=0; k<AttesaCollaborazioni.GetRowCount(); k++){

            System.out.println((k+1)+"° Collaborazione proposta da "+AttesaCollaborazioni.GetValue(k, "cc.PartitaIVARichiedente"));
            System.out.println("Percentuale punti di base: "+AttesaCollaborazioni.GetValue(k, "pv.PuntiPercentual"));
            System.out.println("Percentuale punti VIP: "+AttesaCollaborazioni.GetValue(k, "pv.PuntiPercentualVIP"));
            System.out.println("Percentuale punti dei livelli di base: "+AttesaCollaborazioni.GetValue(k, "pv.PuntiLivelloPercentual"));
            System.out.println("Percentuale punti dei livelli VIP: "+AttesaCollaborazioni.GetValue(k, "pv.PuntiLivelloPercentualVIP"));
            System.out.println("Percentuale di guadagno di base: "+AttesaCollaborazioni.GetValue(k, "pv.PercentualBase"));

        }

        i = UI.Choice("Scegli a quale partita IVA accettare o rifiutare la collaborazione (sono messe in ordine come sopra)", AttesaCollaborazioni.GetColumn("pv.PartitaIVARichiedente"));

        r = UI.Choice("Scegli se accettare o rifiutare la collaborazione proposta da"+AttesaCollaborazioni.GetValue(i, "cc.PartitaIVARichiedente"), new String[]{"Accettare", "Rifiutare"});

        if(r == 0) UserDatabaseRequest.SendRequest("update cassetta_collaborazione set AccettazioneRicevente = true where PartitaIVARichiedente = '"+AttesaCollaborazioni.GetValue(i, "pv.PartitaIVARichiedente")+"' and PartitaIVARicevente = '"+azienda.getPartitaIVA()+"'");

        if(r == 1) UserDatabaseRequest.SendRequest("update cassetta_collaborazione set AccettazioneRicevente = false where PartitaIVARichiedente = '"+AttesaCollaborazioni.GetValue(i, "pv.PartitaIVARichiedente")+"' and PartitaIVARicevente = '"+azienda.getPartitaIVA()+"'");

        return UserDatabaseRequest.GetLastExecuteDone();
    }
    public static void ModificaPianoFedeltà() {

        int i, r, f;

        PianoFedeltà Piano = new PianoFedeltà(azienda.getPartitaIVA());

        boolean LevelActive = false;
        boolean ScontoActive = false;

        while (true) {

            LevelActive = Piano.getLivelliPercentual() == 0 && Piano.getPuntiPercentualVIP() == 0;
            ScontoActive = Piano.getPuntiPercentual() == 0 && Piano.getPuntiPercentualVIP() == 0;

            i = Choice("Scegli quale parte del piano fedeltà cambiare", new String[]{
                    "Indietro",
                    "Piano livelli (" + Piano.livelli.GetLevelsCount() + " livelli con un normale aumento dei punti di " + Piano.getLivelliPercentual() + " e aumento VIP di " + Piano.getLivelliPercentualVIP() + " attualmente attivo = " + LevelActive + ")",
                    "Piano Sconto di base (con uno sconto di base normale di " + Piano.getPuntiPercentual() + " ed uno sconto VIP di " + Piano.getPuntiPercentualVIP() + ", " + ScontoActive + ")",
                    "Piano Recensione (Attualmente abilitata = " + Piano.getRecensione() + ")",
                    "Piano Prenotazione (Attualmente abilitata = " + Piano.getPrenotazione() + ")",
                    "Piano Gestione Coupon (Attualmente abilitato = " + Piano.getVantaggioCoupon() + ")",
                    "Piano Gestione ModuloReffeal (Attualmente abilitato = " + Piano.getVantaggioModuloReffeal() + ")"});

            if (i == 0) break;

            //Piano livelli
            else if (i == 1) {

                while (true) {

                    if (LevelActive) {

                        System.out.println("Livelli attuali\n");

                        //Stampo i livelli presenti
                        for (int k = 0; k < Piano.livelli.GetLevelsCount(); k++) System.out.println((k + 1) + "° livello: " + Piano.livelli.GetLivello(k));

                        r = Choice("\nCosa vuoi fare?", new String[]{
                                "Indietro",
                                "Disattivare piano livelli",
                                "Modificare Percentuale aumento punti di livello di base (" + Piano.getLivelliPercentual() + ")",
                                "Modificare Percentuale aumento punti di livello VIP (" + Piano.getLivelliPercentualVIP() + ")",
                                "Rimuovere un livello",
                                "Aggiungere un livello"
                        });

                        if (r == 0) break;

                        else if (r == 1) Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), 0, 0, Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                        else if (r == 2) {

                            try {
                                k = Float.parseFloat(UI.CompileForm("Inserisci la percentuale aumento di livello base", new String[]{"Percentuale di aumento di base"})[0]);
                                Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), k, Piano.getLivelliPercentualVIP(), Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                            } catch (Exception e) {
                            }

                        }

                        else if (r == 3) {

                            try {
                                k = Float.parseFloat(UI.CompileForm("Inserisci la percentuale aumento di livello base", new String[]{"Percentuale di aumento di base"})[0]);
                                Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), Piano.getLivelliPercentual(), k, Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                            } catch (Exception e) {
                            }
                        }

                        else if (r == 4) {

                            try {
                                f = Integer.parseInt(UI.CompileForm("Inserisci quale livello vuoi eliminare (Basandosi sull' indice)", new String[]{"Livello"})[0]);
                                Piano.livelli.RemoveLevel(f);
                            } catch (Exception e) {
                            }

                        }

                        else if (r == 5) {

                            try {
                                Fields = UI.CompileForm("Inserisci i campi del livello che vuoi aggiunger", new String[]{"" +
                                        "Sconto base",
                                        "Sconto VIP",
                                        "Punti per il nuovo livello"});

                                Piano.livelli.AddLevel(Float.parseFloat(Fields[0]), Float.parseFloat(Fields[1]), Integer.parseInt(Fields[2]));
                            } catch (Exception e) {
                            }

                        }

                    } else {

                        r = Choice("Cosa vuoi fare?", new String[]{
                                "Indietro",
                                "Attivare piano livelli"
                        });

                        if (r == 0) break;

                        else if (r == 1)
                            Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), 0.1f, 0.1f, Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                    }

                    //Aggiorno sul database ed in locale i livelli
                    Piano.livelli.RequestSetData();
                    Piano.livelli.RequestCurrentData();
                }
            }

            //Piano sconto
            else if (i == 2) {

                while (true) {

                    if (ScontoActive) {

                        r = Choice("Cosa vuoi fare?", new String[]{
                                "Indietro",
                                "Disattivare piano sconto",
                                "Modificare Percentuale sconto di base (" + Piano.getPuntiPercentual() + ")",
                                "Modificare Percentuale sconto VIP (" + Piano.getPuntiPercentualVIP() + ")"
                        });

                        if (r == 0) break;

                        else if (r == 1)
                            Piano.RequestSetData(0, 0, Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), !Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                        else if (r == 2) {
                            try {
                                k = Float.parseFloat(UI.CompileForm("Inserisci la percentuale di sconto base", new String[]{"Percentuale di sconto di base"})[0]);
                                Piano.RequestSetData(k, Piano.getPuntiPercentualVIP(), Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                            } catch (Exception e) {}

                        } else if (r == 3) {
                            try {
                                k = Float.parseFloat(UI.CompileForm("Inserisci la percentuale di sconto VIP", new String[]{"Percentuale di sconto VIP"})[0]);
                                Piano.RequestSetData(Piano.getPuntiPercentual(), k, Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());
                            } catch (Exception e) {}
                        }
                    } else {
                        r = Choice("Cosa vuoi fare?", new String[]{
                                "Indietro",
                                "Attivare piano sconto"});

                        if (r == 0) break;

                        else if (r == 1) Piano.RequestSetData(0.1f, 0.1f, Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), !Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                    }
                }
            }

            //Recensione
            else if (i == 3) Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), !Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                //Prenotazione
            else if (i == 4) Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), Piano.getRecensione(), !Piano.getPrenotazione(), Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                //Coupon
            else if (i == 5) Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), Piano.getRecensione(), Piano.getPrenotazione(), !Piano.getVantaggioCoupon(), Piano.getVantaggioModuloReffeal());

                //ModuloReffeal
            else if (i == 6) Piano.RequestSetData(Piano.getPuntiPercentual(), Piano.getPuntiPercentualVIP(), Piano.getLivelliPercentual(), Piano.getLivelliPercentualVIP(), Piano.getRecensione(), Piano.getPrenotazione(), Piano.getVantaggioCoupon(), !Piano.getVantaggioModuloReffeal());
        }
    }

    public static void AccountAzienda(){

        int i, r;

        while(true){

            i = UI.Choice("Punto vendita ("+azienda.getPartitaIVA()+"), scegli cosa fare", new String[]{
                    "Indietro",
                    "Modifica il tuo piano fedeltà",
                    "Aggiungi una collaborazione",
                    "Accetta una collaborazione",
                    "Modifica i tuoi dati",
                    "Resetta la tua password"
            });

            if(i == 0) break;
            else if(i == 1) UI.ModificaPianoFedeltà();
            else if(i == 2) UI.AddCollaborazioneAzienda();
            else if (i == 3) UI.AcceptCollaborazionAzienda();
            else if(i == 4) UI.GestioneDatiAzienda();
            else if(i == 5) UI.ResetPassword();
        }
    }

    public static void AccountUtente(){

        int i, r;

        while(true){

            i = UI.Choice("Utente("+utente.getNickname()+"), scegli cosa fare", new String[]{
                    "Indietro",
                    "Lascia una recensione",
                    "Modifica i tuoi dati",
                    "Resetta la tua password"
            });

            if(i == 0) break;
            else if(i == 1) UI.RecensioneUtente();
            else if(i == 2) UI.GestioneDatiUtente();
            else if(i == 3) UI.ResetPassword();
        }
    }

    public static void AccountDipendente(){

        int i, r;

        while(true){

            i = UI.Choice("Dipendente ("+dipendente.getCodiceDipendente()+") nel punto vendita ("+dipendente.getPartitaIVA()+"), scegli cosa fare", new String[]{
                    "Indietro",
                    "Aggiungi un utente dal vivo",
                    "Resetta la tua password"
            });

            if(i == 0) break;
            else if(i == 1) UI.AggiuntaClientiDipendente();
            else if(i == 2) UI.ResetPassword();

        }
    }
}
