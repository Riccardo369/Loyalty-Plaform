package LogicLayer;

import DataLayer.*;

import java.sql.Timestamp;

public class Recensione {

    protected String PartitaIVA;
    protected String Nickname;
    protected String Descrizione;
    protected float Stelle;
    protected Timestamp TimeStamp;

    public Recensione(String PartitaIVA, String Nickname){
        if(!this.RequestGetCurrentData(PartitaIVA, Nickname)) throw new IllegalArgumentException();
    }

    public boolean RequestGetCurrentData(){
        return this.RequestGetCurrentData(this.PartitaIVA, this.Nickname);
    }

    private boolean RequestGetCurrentData(String PartitaIVA, String Nickname){

        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select Descrizione, Stelle, MomentoTemporale from recensione_sito where PartitaIVA = '"+PartitaIVA+"' and Nickname = '"+Nickname+"'");

        if(Response.GetRowCount() == 0) return false;

        this.PartitaIVA = PartitaIVA;
        this.Nickname = Nickname;
        this.Descrizione = Response.GetValue(0, "Descrizione");
        this.Stelle = Float.parseFloat(Response.GetValue(0, "Stelle"));
        this.TimeStamp = Timestamp.valueOf(Response.GetValue(0, "MomentoTemporale"));

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    //Getter
    public String getPartitaIVA() { return PartitaIVA; }
    public String getNickname() { return Nickname; }
    public String getDescrizione() { return Descrizione; }
    public float getStelle() { return Stelle; }
    public Timestamp getTimeStamp() { return TimeStamp; }

    public boolean equals(Object o){

        if(o == null) return false;

        if(this == o) return true;

        Recensione A = (Recensione) o;

        return (this.PartitaIVA.equals(A.PartitaIVA) &&
                this.Nickname.equals(A.Nickname) &&
                this.Descrizione.equals(A.Descrizione) &&
                this.Stelle == A.Stelle &&
                this.TimeStamp.compareTo(A.TimeStamp) == 0);
    }
}
