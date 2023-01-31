package LogicLayer;

import DataLayer.*;
public class Utente extends Character {

    private String Nome;
    private String Cognome;
    private String NumeroDiTelefono;
    private boolean StatoVIP;
    private String CodiceCarta;
    private String Città;
    private int Età;
    private String Regione;
    private String Comune;
    private boolean Sesso;
    private boolean ConsensoDati;
    private String Nickname;

    public Utente(String Nickname, String Password) {
        if(!this.RequestGetCurrentData(Nickname, Password)) throw new IllegalArgumentException();
    }

    /*
     * Questo metodo ridà lo schema dei vantaggi di questo utente verso l' azienda con quella partita IVA
     *
     * @param PartitaIVA di riferimento
     */
    /*public AdvantageUser RequestAdvantage(String PartitaIVA) {

        try{
            //Ridò la scheda vantaggi di questo utente per quell' azienda
            return new AdvantageUser(this.Nickname, PartitaIVA);
        }

        //Non esiste ancora un scheda vantaggi di questo utente
        catch(Exception e){ return null; }

    }*/

    @Override
    public boolean RequestGetCurrentData(){
        return this.RequestGetCurrentData(this.Nickname, this.Password);
    }

    private boolean RequestGetCurrentData(String Nickname, String Password) {

        //Ottengo la risposta
        DatabaseQuery Response =  UserDatabaseRequest.SendRequest("select * from utente where Nickname = '"+Nickname+"' and Password = '"+Password+"'");

        //Se ho ottenuto un errore per la sintassi nella Query
        if(Response == null) return false;

        //Se il numero di righe non è una sola, c' è un problema
        if(Response.GetRowCount() != 1) return false;

        //Aggiorno i dati dal sistema
        this.Nome = Response.GetValue(0, "Nome");
        this.Cognome = Response.GetValue(0, "Cognome");
        this.NumeroDiTelefono = Response.GetValue(0, "NumeroDiTelefono");
        this.Email = Response.GetValue(0, "Email");
        this.StatoVIP = Response.GetValue(0, "StatoVIP").equals("1");
        this.CodiceCarta = Response.GetValue(0, "CodiceCarta");
        this.Città = Response.GetValue(0, "Città");
        this.Età = Integer.parseInt(Response.GetValue(0, "Età"));
        this.Regione = Response.GetValue(0, "Regione");
        this.Comune = Response.GetValue(0, "Comune");
        this.Sesso = Response.GetValue(0, "Sesso").equals("1");
        this.ConsensoDati = Response.GetValue(0, "ConsensoDati").equals("1");

        this.Nickname = Nickname;
        this.Password = Password;

        //Ridò il fatto si è conclusa in modo corretto
        return true;
    }

    public boolean RequestSetData(String Nickname, String Nome, String Cognome, String NumeroDiTelefono, String Email, boolean StatoVIP, String CodiceCarta, String Città, int Età, String Regione, String Comune, boolean Sesso, boolean ConsensoDati){

        String Request = "update utente set ";

        String[] Fields = {null, null, null, null, null, null, null, null, null, null, null, null, null};

        if(!this.Nome.equals(Nome)) Fields[0] = "Nome = '"+Nome+"'";
        if(!this.Cognome.equals(Cognome)) Fields[1] = "Cognome = '"+Cognome+"'";
        if(!this.NumeroDiTelefono.equals(NumeroDiTelefono)) Fields[2] = "NumeroDiTelefono = '"+NumeroDiTelefono+"'";
        if(!this.Email.equals(Email)) Fields[3] = "Email = '"+Email+"'";
        if(this.StatoVIP != StatoVIP) Fields[4] = "StatoVIP = "+String.valueOf(StatoVIP)+"";
        if(!this.CodiceCarta.equals(CodiceCarta)) Fields[5] = "CodiceCarta = '"+CodiceCarta+"'";
        if(!this.Città.equals(Città)) Fields[6] = "Città = '"+Città+"'";
        if(this.Età != Età) Fields[7] = "Età = "+Età+"";
        if(!this.Regione.equals(Regione)) Fields[8] = "Regione = '"+Regione+"'";
        if(!this.Comune.equals(Comune)) Fields[9] = "Comune = '"+Comune+"'";
        if(this.Sesso != Sesso) Fields[10] = "Sesso = "+String.valueOf(Sesso)+"";
        if(this.ConsensoDati != ConsensoDati) Fields[11] = "ConsensoDati = "+String.valueOf(ConsensoDati)+"";
        if(!this.Nickname.equals(Nickname)) Fields[12] = "Nickname = '"+Nickname+"'";

        String Result = "";

        for(String s: Fields) if(s != null) Result += s;

        //Se tutti i parametri sono uguali, non faccio niente
        if(Result.equals("")) return true;

        //Conto quante virgole devo mettere. Parte da -1 perche con n campi devo mettere n-1 virgole
        int cont = -1;
        for(int i=0; i<Fields.length; i++) if(Fields[i] != null) cont++;

        for(int i=0; i<Fields.length; i++){
            //Se l' i-esimo campo deve essere aggiornato
            if(Fields[i] != null){
                Request += Fields[i];

                //Mi segno il fatto che ho consumato una virgola
                if(cont > 0){
                    Request += ", ";
                    cont--;
                }
            }
        }

        Request += " where Nickname = '"+this.Nickname+"'";

        UserDatabaseRequest.SendRequest(Request);

        if(UserDatabaseRequest.GetLastExecuteDone()){
            if(!this.Nome.equals(Nome)) this.Nome = Nome;
            if(!this.Cognome.equals(Cognome)) this.Cognome = Cognome;
            if(!this.NumeroDiTelefono.equals(NumeroDiTelefono)) Fields[2] = this.NumeroDiTelefono = NumeroDiTelefono;
            if(!this.Email.equals(Email)) this.Email = Email;
            if(this.StatoVIP != StatoVIP) this.StatoVIP = StatoVIP;
            if(!this.CodiceCarta.equals(CodiceCarta)) this.CodiceCarta = CodiceCarta;
            if(!this.Città.equals(Città)) this.Città = Città;
            if(this.Età != Età) this.Età = Età;
            if(!this.Regione.equals(Regione)) this.Regione = Regione;
            if(!this.Comune.equals(Comune)) this.Comune = Comune;
            if(this.Sesso != Sesso) this.Sesso = Sesso;
            if(this.ConsensoDati != ConsensoDati) this.ConsensoDati = ConsensoDati;
            if(!this.Nickname.equals(Nickname)) this.Nickname = Nickname;
        }

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    @Override
    public boolean RequestSetPassword(String Password){

        //Se la password è la stessa
        if(this.Password.equals(Password)) return true;

        UserDatabaseRequest.SendRequest("update utente set Password = '"+Password+"' where Password = '"+this.Password+"'");

        this.Password = Password;

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    //Getter
    public String getNome() { return Nome; }
    public String getCognome() { return Cognome; }
    public String getNumeroDiTelefono() { return NumeroDiTelefono; }
    public boolean getStatoVIP() { return StatoVIP; }
    public String getCodiceCarta() { return CodiceCarta; }
    public String getCittà() { return Città; }
    public int getEtà() { return Età; }
    public String getRegione() { return Regione; }
    public String getComune() { return Comune; }
    public boolean getMale() { return Sesso; }
    public boolean getConsensoDati() { return ConsensoDati; }
    public String getNickname() { return Nickname; }

    public boolean equals(Object o){
        if(this == o) return true;

        Utente A = (Utente) o;

        return this.Nickname.equals(A.Nickname);
    }

}