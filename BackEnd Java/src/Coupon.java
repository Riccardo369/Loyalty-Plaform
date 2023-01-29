/**
 * Classe rappresentante un coupon fisso, SOLO LETTURA delle informazioni
 */
public class Coupon {

	public final String Codice;
	public final String PartitaIVA;
	public final String Nickname;
	public final float Costo;
	public final float Percentuale;
	public final float MinimoPrezzoAttivazione;

	public Coupon(String Codice, String PartitaIVA) throws IllegalArgumentException {

		DatabaseRequest DR = new UserDatabaseRequest();

		DatabaseQuery Response = DR.SendRequest("select Nickname, Costo, Percentuale, MinimoPrezzoAttivazione from codici_coupon where Codice = '"+Codice+"' and PartitaIVA = '"+PartitaIVA+"'");
		if(!DR.GetLastExecuteDone() || Response.GetRowCount() == 0) throw new IllegalArgumentException();

		this.Codice = Codice;
		this.PartitaIVA = PartitaIVA;
		this.Nickname = Response.GetValue(0, "Nickname");
		this.Costo = Float.parseFloat(Response.GetValue(0, "Costo"));
		this.Percentuale = Float.parseFloat(Response.GetValue(0, "Percentuale"));
		this.MinimoPrezzoAttivazione = Float.parseFloat(Response.GetValue(0, "MinimoPrezzoAttivazione"));
	}

	public boolean equals(Object o){
		if(this == o) return true;

		Coupon A = (Coupon) o;

		return (A.Codice.equals(this.Codice) &&
				A.PartitaIVA.equals(this.PartitaIVA) &&
				A.Nickname.equals(this.Nickname) &&
				A.Costo == this.Costo);
	}
}