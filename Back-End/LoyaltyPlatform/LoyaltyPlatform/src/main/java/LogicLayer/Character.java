package LogicLayer;

import java.nio.charset.Charset;
import java.util.Random;

public abstract class Character implements SendNotification {

	protected String Email;
	protected String Password;

	//Request Set Data che varia in base alla classe, questo dipende dai parametri
	public abstract boolean RequestGetCurrentData();

	public abstract boolean RequestSetPassword(String Password);

	//Recupero password sottoforma di commenti, potrebbe cambiare il testo in base ad: azienda, utente e dipendente
	public final boolean RecuperoPassword() {

		//Genero casualmente una nuova password
		byte[] array = new byte[7];
		new Random().nextBytes(array);
		String NewPassword = new String(array, Charset.forName("UTF-8"));

		//Invio l' email
		this.SendEmail("Your new password is: "+NewPassword, this.Email);

		//Riaggiorno la password nel Database
		return this.RequestSetPassword(NewPassword);
	}

	public String getEmail() { return this.Email; }

}