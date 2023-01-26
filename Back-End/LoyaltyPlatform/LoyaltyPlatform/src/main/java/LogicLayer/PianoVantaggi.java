package LogicLayer;

import DataLayer.*;

import java.sql.Timestamp;

public class PianoVantaggi {

    protected String StartPartitaIVA;
    protected String FinishPartitaIVA;
    protected float PuntiPercentual;
    protected float PuntiPercentualVIP;
    protected float LivelliPercentual;
    protected float LivelliPercentualVIP;
    protected boolean Recensione;
    protected boolean Prenotazione;
    protected boolean VantaggioCoupon;
    protected boolean VantaggioModuloReffeal;
    protected float PercentualCash;
    protected Timestamp Scadenza;

    public PianoVantaggi(String StartPartitaIVA, String FinishPartitaIVA){
        if(!this.RequestGetCurrentData(StartPartitaIVA, FinishPartitaIVA)) throw new IllegalArgumentException();
    }

    public boolean RequestGetCurrentData(String PartitaIVAStart, String PartitaIVAFinish){

        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select * from piano_vantaggi where" +
                "(PartitaIVAStart = '"+PartitaIVAStart+"' and PartitaIVAFinish = '"+PartitaIVAFinish+"')");

        if(Response.GetRowCount() == 0) return false;

        this.StartPartitaIVA = PartitaIVAStart;
        this.FinishPartitaIVA = PartitaIVAFinish;
        this.PuntiPercentual = Float.parseFloat(Response.GetValue(0, "PuntiPercentual"));
        this.PuntiPercentualVIP = Float.parseFloat(Response.GetValue(0, "PuntiPercentualVIP"));
        this.LivelliPercentual = Float.parseFloat(Response.GetValue(0, "PuntiLivelloPercentual"));
        this.LivelliPercentualVIP = Float.parseFloat(Response.GetValue(0, "PuntiLivelloPercentualVIP"));
        this.Recensione = Response.GetValue(0, "Recensione").equals("1");
        this.Prenotazione = Response.GetValue(0, "Prenotazione").equals("1");
        this.VantaggioCoupon = Response.GetValue(0, "Coupon").equals("1");
        this.VantaggioModuloReffeal = Response.GetValue(0, "ModuloReffeal").equals("1");
        this.PercentualCash = Float.parseFloat(Response.GetValue(0, "PercentualBase"));

        try{ this.Scadenza = Timestamp.valueOf(Response.GetValue(0, "Scadenza")); }
        catch(Exception e){ this.Scadenza = null; }



        return true;
    }

    //Metodo che aggiunge i parametri di vantaggio all' utente quando fa un acquisto, in base ai parametri di questo piano fedeltà
    protected boolean createAdvantage(String NicknameUtente, float Cash){

        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select StatoVIP from utente where Nickname = '"+NicknameUtente+"'");

        if(!UserDatabaseRequest.GetLastExecuteDone() || Response.GetRowCount() != 1) return false;

        boolean StatoVIP = Response.GetValue(0, "StatoVIP").equals("1");

        //Calcolo quanti punti ed i punti-livello che devo sommare all' utente per questa PartitaIVA
        float AddPunti = 0;
        float AddPuntiLivello = 0;

        if(StatoVIP){
            AddPunti = this.PuntiPercentualVIP*Cash;
            AddPuntiLivello = this.LivelliPercentualVIP*Cash;
        }
        else{
            AddPunti = this.PuntiPercentual*Cash;
            AddPuntiLivello = this.LivelliPercentual*Cash;
        }

        //Inserisco la riga del vantaggio per quell' utente nel Database
        UserDatabaseRequest.SendRequest("insert into vantaggi_utente_azienda (PartitaIVA, Nickname, Sconto, Punti, Livello, ActualPointsLivello) VALUES ('"+this.FinishPartitaIVA+"', '"+NicknameUtente+"', 0, 0, 0, 0)");

        int NuovoPunti = (int) AddPunti;
        int NuovoActualPointsLevel = (int) AddPuntiLivello;
        int NuovoLivello = 0;
        float NuovoSconto = 0;

        //Se i vantaggi esistevano già per quel cliente
        if(!UserDatabaseRequest.GetLastExecuteDone()) {

            //Ottengo tutti i dati che mi servono per poi riaggiornare tutta la situazione
            Response = UserDatabaseRequest.SendRequest("select Punti, Livello, ActualPointsLivello from vantaggi_utente_azienda where Nickname = '" + NicknameUtente + "' and PartitaIVA = '" + this.FinishPartitaIVA + "'");

            NuovoPunti += Integer.parseInt(Response.GetValue(0, "Punti"));
            NuovoActualPointsLevel += Integer.parseInt(Response.GetValue(0, "ActualPointsLivello"));
            NuovoLivello = Integer.parseInt(Response.GetValue(0, "Livello"));
        }

        PianoFedeltà PianoArrivo = new PianoFedeltà(this.FinishPartitaIVA);

        if(PianoArrivo.livelli.GetLevelsCount() > 0){

            while(NuovoActualPointsLevel >= PianoArrivo.livelli.GetPointsToNextLevel(NuovoLivello)){
                NuovoActualPointsLevel -= PianoArrivo.livelli.GetPointsToNextLevel(NuovoLivello);
                NuovoLivello++;
            }

            if(StatoVIP) NuovoSconto = PianoArrivo.livelli.GetActualScontoVIP(NuovoLivello);
            else NuovoSconto = PianoArrivo.livelli.GetActualSconto(NuovoLivello);
        }

        //Aggiorno tutta la situazione sul DB
        UserDatabaseRequest.SendRequest("update vantaggi_utente_azienda set " +
                "Sconto = "+NuovoSconto+", "+
                "Punti = "+NuovoPunti+",  "+
                "Livello = "+NuovoLivello+", "+
                "ActualPointsLivello = "+NuovoActualPointsLevel+" "+
                "where PartitaIVA = '"+this.FinishPartitaIVA+"' and Nickname = '"+NicknameUtente+"'");

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    //Getter
    public String getStartPartitaIVA() { return StartPartitaIVA; }
    public String getFinishPartitaIVA() { return FinishPartitaIVA; }
    public float getPuntiPercentual() { return PuntiPercentual; }
    public float getPuntiPercentualVIP() { return PuntiPercentualVIP; }
    public float getLivelliPercentual() { return LivelliPercentual; }
    public float getLivelliPercentualVIP() { return LivelliPercentualVIP; }

}

class PianoFedeltà extends PianoVantaggi{

    protected final Livelli livelli;
    public PianoFedeltà(String PartitaIVA){
        super(PartitaIVA, PartitaIVA);
        this.livelli = new Livelli(PartitaIVA, PartitaIVA);
    }

    public boolean RequestSetData(float PuntiPercentual, float PuntiPercentualVIP, float LivelliPercentual, float LivelliPercentualVIP, boolean Recensione, boolean Prenotazione, boolean Coupon, boolean ModuloReffeal){

        String[] Campi = {"", "", "", "", "", "", "", ""};

        if(this.PuntiPercentual != PuntiPercentual) Campi[0] = "PuntiPercentual = '"+PuntiPercentual+"'";
        if(this.PuntiPercentualVIP != PuntiPercentualVIP) Campi[1] = "PuntiPercentualVIP = '"+PuntiPercentualVIP+"'";
        if(this.LivelliPercentual != LivelliPercentual) Campi[2] = "PuntiPercentual = '"+PuntiPercentual+"'";
        if(this.LivelliPercentualVIP != LivelliPercentualVIP) Campi[3] = "PuntiPercentualVIP = '"+PuntiPercentualVIP+"'";
        if(this.Recensione != Recensione) Campi[4] = "Recensione = "+String.valueOf(Recensione)+"";
        if(this.Prenotazione != Prenotazione) Campi[5] = "Prenotazione = "+String.valueOf(Prenotazione)+"";
        if(this.VantaggioCoupon != Coupon) Campi[6] = "Coupon = "+String.valueOf(Coupon)+"";
        if(this.VantaggioModuloReffeal != ModuloReffeal) Campi[7] = "ModuloReffeal = "+String.valueOf(ModuloReffeal)+"";

        //Vedo se devo fare una richiesta. Se Requests = '', non devo modificare niente, perchè tutti i parametri presi dal DB sono uguali
        // a quelli in locale
        String Requests = "";
        for(String s: Campi) Requests += s;
        if(Requests.equals("")) return true;

        Requests = "";
        int cont = -1;

        for(int i=0; i<Campi.length; i++) if(!Campi[i].equals("")) cont++;

        for(int i=0; i<Campi.length; i++){

            if(Campi[i].equals("")) continue;

            Requests += Campi[i];

            if(cont > 0){
                Requests += ", ";
                cont--;
            }
        }

        //Modifico sia i dati di questa azienda, e sia le partite IVA di tutti gli elementi associati all' azienda (Dipendenti, Coupon, MoudliReffeal, ...)
        UserDatabaseRequest.SendRequest("update piano_vantaggi set "+Requests+" where PartitaIVAStart = '"+this.StartPartitaIVA+"' and PartitaIVAFinish = '"+this.FinishPartitaIVA+"'");

        if(UserDatabaseRequest.GetLastExecuteDone()){

            this.PuntiPercentual = PuntiPercentual;
            this.PuntiPercentualVIP = PuntiPercentualVIP;
            this.LivelliPercentual = PuntiPercentual;
            this.LivelliPercentualVIP = PuntiPercentualVIP;
            this.Recensione = Recensione;
            this.Prenotazione = Prenotazione;
            this.VantaggioCoupon = Coupon;
            this.VantaggioModuloReffeal = ModuloReffeal;

        }

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    public boolean CreateAdvantage(String Nickname, float Cash){
        return this.createAdvantage(Nickname, Cash);
    }

    public boolean getRecensione() { return Recensione; }
    public boolean getPrenotazione() { return Prenotazione; }
    public boolean getVantaggioCoupon() { return VantaggioCoupon; }
    public boolean getVantaggioModuloReffeal() { return VantaggioModuloReffeal; }
}

class Collaborazione extends PianoVantaggi{

    public Collaborazione(String PartitaIVAStart, String PartitaIVAFinish){
        super(PartitaIVAStart, PartitaIVAFinish);
    }

    public boolean CreateAdvantage(String Nickname, float Cash){

        //Se la collaborazione è scaduta, non si attiva niente
        if(this.Scadenza.compareTo(new Timestamp(System.currentTimeMillis())) < 0) return false;
        return this.createAdvantage(Nickname, Cash*this.PercentualCash);
    }

    public Timestamp getScadenza() { return this.Scadenza; }

    public float getPercentualCash() { return PercentualCash; }
}