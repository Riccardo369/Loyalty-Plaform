package LogicLayer;

import DataLayer.DatabaseQuery;
import DataLayer.UserDatabaseRequest;

import java.sql.Timestamp;

public class Collaborazione extends PianoVantaggi{

    public Collaborazione(String PartitaIVAStart, String PartitaIVAFinish){
        super(PartitaIVAStart, PartitaIVAFinish);
    }

    public boolean CreateAdvantage(String Nickname, float Cash){

        //Se la collaborazione è scaduta, non si attiva niente
        if(this.Scadenza.compareTo(new Timestamp(System.currentTimeMillis())) < 0) return false;

        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select AccettazioneRicevente from cassetta_collaborazioni where PartitaIVARichiedente = '"+this.StartPartitaIVA+"' and PartitaIVARicevente = '"+this.FinishPartitaIVA+"'");

        //Se la collaborazione non è stata ancora accettata dall' altra parte, non posso fare niente
        if(Response.GetRowCount() > 0  && (Response.GetValue(0, "AccettazioneRicevente") == null || !Response.GetValue(0, "AccettazioneRicevente").equals("1"))) return false;

        return this.createAdvantage(Nickname, Cash*this.PercentualCash);
    }

    public Timestamp getScadenza() { return this.Scadenza; }

    public float getPercentualCash() { return PercentualCash; }
}