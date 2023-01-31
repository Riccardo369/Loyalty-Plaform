public class Dipendente extends Character {

    private String Nome;
    private String Cognome;
    private String NumeroDiTelefono;
    private String PartitaIVA;
    private String CodiceDipendente;

    public Dipendente(String CodiceDipendente, String PartitaIVA, String Password){
        if(!this.RequestGetCurrentData(CodiceDipendente, PartitaIVA, Password)) throw new IllegalArgumentException();
    }

    public boolean RequestGetCurrentData(){
        return this.RequestGetCurrentData(this.CodiceDipendente, this.PartitaIVA, this.Password);
    }

    private boolean RequestGetCurrentData(String CodiceDipendente, String PartitaIVA, String Password){

        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select Nome, Cognome, Email, NumeroDiTelefono from dipendente where CodiceDipendente = '"+CodiceDipendente+"' and PartitaIVA = '"+PartitaIVA+"' and Password = '"+Password+"'");

        if(Response == null || Response.GetRowCount() == 0) return false;

        this.Nome = Response.GetValue(0, "Nome");
        this.Cognome = Response.GetValue(0, "Cognome");
        this.NumeroDiTelefono = Response.GetValue(0, "NumeroDiTelefono");
        this.Email = Response.GetValue(0, "Email");
        this.CodiceDipendente = CodiceDipendente;
        this.PartitaIVA = PartitaIVA;
        this.Password = Password;

        return true;
    }

    public boolean RequestSetData(String Nome, String Cognome, String Email, String PartitaIVA, String CodiceDipendente, String NumeroDiTelefono){

        String[] Campi = {"", "", "", "", "", ""};

        if(!this.Nome.equals(Nome)) Campi[0] = "Nome = '"+Nome+"'";
        if(!this.Cognome.equals(Nome)) Campi[1] = "Cognome = '"+Cognome+"'";
        if(!this.Email.equals(Nome)) Campi[2] = "Email = '"+Email+"'";
        if(!this.PartitaIVA.equals(PartitaIVA)) Campi[3] = "PartitaIVA = '"+PartitaIVA+"'";
        if(!this.CodiceDipendente.equals(CodiceDipendente)) Campi[4] = "CodiceDipendente = '"+CodiceDipendente+"'";
        if(!this.NumeroDiTelefono.equals(NumeroDiTelefono)) Campi[5] = "NumeroDiTelefono = '"+NumeroDiTelefono+"'";

        //Vedo se devo fare una richiesta. Se Requests = '', non devo modificare niente, perchè tutti i parametri presi dal DB sono uguali
        // a quelli in locale
        String Requests = "";
        for(String s: Campi) Requests += s;
        if(Requests.equals("")) return true;

        Requests = "";
        int cont = -1;

        for(int i=0; i<Campi.length; i++) if(!Campi[i].equals("")) cont++;

        for(int i=0; i<Campi.length; i++){

            if(Campi[i].equals(""))  continue;

            Requests += Campi[i];

            if(cont > 0){
                Requests += ", ";
                cont--;
            }
        }

        UserDatabaseRequest.SendRequest("update dipendente set "+Requests+" where PartitaIVA = '"+this.PartitaIVA+"' and CodiceDipendente = '"+this.CodiceDipendente+"'");

        if(UserDatabaseRequest.GetLastExecuteDone()){

            this.Nome = Nome;
            this.Cognome = Cognome;
            this.Email = Email;
            this.PartitaIVA = PartitaIVA;
            this.CodiceDipendente = CodiceDipendente;
            this.NumeroDiTelefono = NumeroDiTelefono;

        }

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    @Override
    public boolean RequestSetPassword(String Password) {

        if(this.Password.equals(Password)) return true;

        UserDatabaseRequest.SendRequest("update dipendente set " +
                "Password = '"+Password+"' " +
                "where PartitaIVA = '"+this.PartitaIVA+"' and CodiceDipendente = '"+this.CodiceDipendente+"'");

        if(UserDatabaseRequest.GetLastExecuteDone()) this.Password = Password;

        return UserDatabaseRequest.GetLastExecuteDone();
    }

    //Getter
    public String getNome() { return Nome; }
    public String getCognome() { return Cognome; }
    public String getNumeroDiTelefono() { return NumeroDiTelefono; }
    public String getPartitaIVA() { return PartitaIVA; }
    public String getCodiceDipendente() { return CodiceDipendente; }
}
