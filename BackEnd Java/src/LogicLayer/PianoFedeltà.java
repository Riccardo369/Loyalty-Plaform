package LogicLayer;

import DataLayer.UserDatabaseRequest;

public class PianoFedeltà extends PianoVantaggi{

    public final Livelli livelli;
    public PianoFedeltà(String PartitaIVA){
        super(PartitaIVA, PartitaIVA);
        this.livelli = new Livelli(PartitaIVA, PartitaIVA);
    }

    public boolean RequestSetData(float PuntiPercentual, float PuntiPercentualVIP, float LivelliPercentual, float LivelliPercentualVIP, boolean Recensione, boolean Prenotazione, boolean Coupon, boolean ModuloReffeal){

        if(PuntiPercentual > 1 || PuntiPercentual < 0 ||
                PuntiPercentualVIP > 1 || PuntiPercentualVIP < 0 ||
                LivelliPercentual > 1 || LivelliPercentual < 0 ||
                LivelliPercentualVIP > 1 || LivelliPercentualVIP < 0) return false;


        String[] Campi = {"", "", "", "", "", "", "", ""};

        if(this.PuntiPercentual != PuntiPercentual) Campi[0] = "PuntiPercentual = '"+PuntiPercentual+"'";
        if(this.PuntiPercentualVIP != PuntiPercentualVIP) Campi[1] = "PuntiPercentualVIP = '"+PuntiPercentualVIP+"'";
        if(this.LivelliPercentual != LivelliPercentual) Campi[2] = "PuntiLivelloPercentual = '"+LivelliPercentual+"'";
        if(this.LivelliPercentualVIP != LivelliPercentualVIP) Campi[3] = "PuntiLivelloPercentualVIP = '"+LivelliPercentualVIP+"'";
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

        //System.out.println(UserDatabaseRequest.GetLastExecuteDone());

        if(UserDatabaseRequest.GetLastExecuteDone()){

            this.PuntiPercentual = PuntiPercentual;
            this.PuntiPercentualVIP = PuntiPercentualVIP;
            this.LivelliPercentual = LivelliPercentual;
            this.LivelliPercentualVIP = LivelliPercentualVIP;
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