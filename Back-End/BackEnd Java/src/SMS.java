import java.sql.Timestamp;

/**
 * Classe rappresentante un SMS, SOLO LETTURA delle informazioni
 */
public class SMS {

	public final String StartNumber;
	public final String FinishNumber;
	public final String Text;
	public final Timestamp TimeStamp;

	public SMS(String StartNumber, String FinishNumber, String Text, Timestamp TimeStamp){
		this.StartNumber = StartNumber;
		this.FinishNumber = FinishNumber;
		this.Text = Text;
		this.TimeStamp = TimeStamp;
	}

	public boolean equals(Object o){
		if(this == o) return true;

		SMS A = (SMS) o;

		return (A.StartNumber.equals(this.StartNumber) &&
				A.FinishNumber.equals(this.FinishNumber) &&
				A.Text.equals(this.Text) &&
				A.TimeStamp.equals(this.TimeStamp));
	}

}