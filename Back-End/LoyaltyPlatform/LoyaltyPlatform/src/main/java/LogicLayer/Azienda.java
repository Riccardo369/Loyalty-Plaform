package LogicLayer;

import DataLayer.*;

class Azienda extends Character {

	protected String NomeAzienda;
	protected String SitoWeb;

	protected String PartitaIVA;

	//Accesso con richiesta della PartitaIVA e Password
	protected Azienda(String PartitaIVA, String Password) throws IllegalArgumentException{
		if(!this.RequestGetCurrentData(PartitaIVA, Password)) throw new IllegalArgumentException();
	}

	@Override
	public boolean RequestGetCurrentData(){
		return this.RequestGetCurrentData(this.PartitaIVA, this.Password);
	}

	private boolean RequestGetCurrentData(String PartitaIVA, String Password) {

		//Mi prendo i dati
		DatabaseQuery Response = UserDatabaseRequest.SendRequest("select NomeAzienda, Email, SitoWeb from azienda where PartitaIVA = '"+PartitaIVA+"' and Password = '"+Password+"'");

		if(Response.GetRowCount() == 0) return false;

		this.NomeAzienda = Response.GetValue(0, "NomeAzienda");
		this.Email = Response.GetValue(0, "Email");
		this.SitoWeb = Response.GetValue(0, "SitoWeb");

		this.PartitaIVA = PartitaIVA;
		this.Password = Password;

		return true;
	}

	public boolean RequestSetData(String NomeAzienda, String PartitaIVA, String SitoWeb, String Email) {

		String[] Campi = {"", "", "", ""};

		if(!this.NomeAzienda.equals(NomeAzienda)) Campi[0] = "NomeAzienda = '"+NomeAzienda+"'";
		if(!this.PartitaIVA.equals(PartitaIVA)) Campi[1] = "PartitaIVA = '"+PartitaIVA+"'";
		if(!this.SitoWeb.equals(SitoWeb)) Campi[2] = "SitoWeb = '"+SitoWeb+"'";
		if(!this.Email.equals(Email)) Campi[3] = "Email = '"+Email+"'";

		//Vedo se devo fare una richiesta. Se Requests = '', non devo modificare niente, perchè tutti i parametri presi dal DB sono uguali
		// a quelli in locale
		String Requests = "";
		for(String s: Campi) Requests += s;
		if(Requests.equals("")) return true;

		Requests = "";
		int cont = -1;

		for(int i=0; i<Campi.length; i++) if(!Campi[i].equals("")) cont++;

		for(int i=0; i<Campi.length; i++){
			Requests += Campi[i];

			if(cont > 0){
				Requests += ", ";
				cont--;
			}
		}

		//Modifico sia i dati di questa azienda, e sia le partite IVA di tutti gli elementi associati all' azienda (Dipendenti, Coupon, MoudliReffeal, ...)
		UserDatabaseRequest.SendRequest("update azienda set "+Requests+" where PartitaIVA = '"+this.PartitaIVA+"'");

		if(UserDatabaseRequest.GetLastExecuteDone()){
			this.NomeAzienda = NomeAzienda;
			this.PartitaIVA = PartitaIVA;
			this.SitoWeb = SitoWeb;
			this.Email = Email;
		}

		return UserDatabaseRequest.GetLastExecuteDone();
	}

	@Override
	public boolean RequestSetPassword(String Password) {

		if(this.Password.equals(Password)) return true;

		UserDatabaseRequest.SendRequest("update azienda set " +
				"Password = '"+Password+"' " +
				"where PartitaIVA = '"+this.PartitaIVA+"'");

		if(UserDatabaseRequest.GetLastExecuteDone()) this.Password = Password;

		return UserDatabaseRequest.GetLastExecuteDone();
	}

	//Getter
	public String getNomeAzienda() { return this.NomeAzienda; }
	public String getPartitaIVA() { return this.PartitaIVA; }
	public String getSitoWeb() { return this.SitoWeb; }

	public boolean equals(Object o){
		if(this == o) return true;

		Azienda A = (Azienda) o;

		return A.PartitaIVA.equals(this.PartitaIVA);
	}
}