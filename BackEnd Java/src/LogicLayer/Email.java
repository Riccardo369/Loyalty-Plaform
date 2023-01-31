package LogicLayer;

import java.sql.Timestamp;

/**
 * Classe rappresentante un EMAIL, SOLO LETTURA delle informazioni
 */
public class Email {

	public final String StartEmail;
	public final String FinishEmail;
	public final String Text;
	public final Timestamp Date;

	public Email(String StartEmail, String FinishEmail, String Text, Timestamp Date){
		this.StartEmail = StartEmail;
		this.FinishEmail = FinishEmail;
		this.Text = Text;
		this.Date = Date;
	}

	public boolean equals(Object o){

		if(this == o) return true;

		Email A = (Email) o;

		return (A.StartEmail.equals(this.StartEmail) &&
				A.FinishEmail.equals(this.FinishEmail) &&
				A.Text.equals(this.Text) &&
				A.Date.equals(this.Date));

	}

}