import java.util.ArrayList;
import java.util.List;

/**
 * Piano a livelli di un azienda. Ogni livello non può avere lo sconto normale più alto di quello VIP, e le percentuali di sconto
 * (sia normale che VIP) dell' n-esimo livello devono essere maggiori od uguali di quelle del livello precedente.
 */
public class Livelli {

	//Lista di livelli salvati in locale
	private final List<Level> Levels = new ArrayList<>();

	//PartitaIVA di riferimento
	private String PartitaIVAStart, PartitaIVAFinish;

	public Livelli(String PartitaIVAStart, String PartitaIVAFinish){
		this.PartitaIVAStart = PartitaIVAStart;
		this.PartitaIVAFinish = PartitaIVAFinish;
		if(!this.RequestCurrentData()) throw new IllegalArgumentException();
	}

	/**
	 * Metodo che aggiorna i livelli
	 * @return se ho aggiornato in modo corretto
	 */
	public boolean RequestCurrentData() {

		//Richiesta per chiedere quanti punti
		DatabaseQuery Response = UserDatabaseRequest.SendRequest("select PointsNextLivello, AumentoSconto, " +
				"AumentoScontoVIP from livelli where PartitaIVAStart = '"+this.PartitaIVAStart +"' and PartitaIVAFinish = '"+this.PartitaIVAFinish+
				"' order by AumentoSconto, AumentoScontoVIP asc");

		if(Response == null) return false;

		this.Levels.clear();

		for(int i=0; i<Response.GetRowCount(); i++){
			this.Levels.add(new Level(
					Float.parseFloat(Response.GetValue(i, "AumentoSconto")),
					Float.parseFloat(Response.GetValue(i, "AumentoScontoVIP")),
					Integer.parseInt(Response.GetValue(i, "PointsNextLivello"))));
		}

		return true;
	}

	/**
	 * Metodo che setta tutti i livelli. Per facilità, poichè non esiste un indice che tiene in ordine i livelli (e questa
	 * operazione verrà eseguita poche volte), si cancellano tutti i livelli dal Database e si riaggiungono uno alla volta,
	 * con una singola transazione
	 * @return esito dell' operazione
	 */
	public boolean RequestSetData(){

		String levels = "";

		for(int i=0; i<this.Levels.size(); i++){
			levels += "('"+PartitaIVAStart+"', '"+PartitaIVAFinish+"', "+this.Levels.get(i).PointsToNextLevel+", "+this.Levels.get(i).ScontoAdvantage+", "+this.Levels.get(i).ScontoAdvantageVIP+")";
			if(i < this.Levels.size()-1) levels += ", ";
		}

		if(this.Levels.size() == 0) UserDatabaseRequest.SendRequest("delete from livelli where PartitaIVAStart = '" + PartitaIVAStart + "' and PartitaIVAFinish = '" + PartitaIVAFinish + "'");
		else {
			UserDatabaseRequest.SendTransaction(new String[]{
					"delete from livelli where PartitaIVAStart = '" + PartitaIVAStart + "' and PartitaIVAFinish = '" + PartitaIVAFinish + "'",
					"insert into livelli (PartitaIVAStart, PartitaIVAFinish, PointsNextLivello, AumentoSconto, AumentoScontoVIP) VALUES " + levels});
		}

		return UserDatabaseRequest.GetLastExecuteDone();
	}

	/**
	 * Ottengo il numero di livelli
	 */
	public int GetLevelsCount() {return this.Levels.size(); }

	/**
	 * Ottengo lì i-esimo livello
	 * @param i indice del livello voluto
	 * @return livello
	 */
	public Level GetLivello(int i) { return this.Levels.get(i); }

	/**
	 * Ottengo percentuale sconto del i-esimo livello
	 * @param i
	 * @return percentuale sconto dell' i-esimo livello
	 */
	public float GetActualSconto(int i) { return this.Levels.get(i).ScontoAdvantage; }

	/**
	 * Ottengo percentuale di sconto per VIP del i-esimo livello
	 * @param i
	 * @return percentuale di sconto per VIP dell' i-esimo livello
	 */
	public float GetActualScontoVIP(int i) { return this.Levels.get(i).ScontoAdvantageVIP; }

	/**
	 * Ottengo percentuale di punti necessari del i-esimo livello
	 * @param i
	 * @return percentuale di punti necessari dell' i-esimo livello
	 */
	public int GetPointsToNextLevel(int i) { return this.Levels.get(i).PointsToNextLevel; }

	/**
	 * Setto lo sconto normale dell i-esimo livello
	 * @param
	 * @return se lo sconto normale dell' i-esimo livello è stato settato
	 */
	public boolean SetActualSconto(int i, float Sconto) {

		if(Sconto >= this.Levels.get(i+1).ScontoAdvantage || Sconto <= this.Levels.get(i-1).ScontoAdvantage || Sconto > this.Levels.get(i).ScontoAdvantageVIP) return false;
		this.Levels.get(i).ScontoAdvantage = Sconto;
		return true;
	}

	/**
	 * Setto lo sconto VIP dell i-esimo livello
	 * @param
	 * @return lo sconto VIP dell' i-esimo livello è stato settato
	 */
	public boolean SetActualScontoVIP(int i, float Sconto) {

		if(Sconto >= this.Levels.get(i+1).ScontoAdvantageVIP || Sconto <= this.Levels.get(i-1).ScontoAdvantageVIP || Sconto < this.Levels.get(i).ScontoAdvantage) return false;
		this.Levels.get(i).ScontoAdvantageVIP = Sconto;
		return true;
	}

	/**
	 * Setto il numero di punti necessari per passare al prossimo livello
	 * @param i
	 * @param PointsNextLevel
	 * @return se il settaggio è stato fatto
	 */
	public boolean SetPointsToNextLevel(int i, int PointsNextLevel) {

		this.Levels.get(i).PointsToNextLevel = PointsNextLevel;
		return true;
	}

	/**
	 * Rimuove l i-esimo livello
	 * @param livello
	 * @return se ha rimosso il livello
	 */
	public boolean RemoveLevel(int livello) {

		if(livello > this.Levels.size()-1) return false;

		//Livello in questione da rimuovere
		Level l = this.Levels.get(livello);

		Levels.remove(livello);

		return true;
	}

	/**
	 * Aggiungi un livello
	 * @param Sconto
	 * @param ScontoVIP
	 * @param PointsToNextLevel
	 * @return se il livello è stato aggiunto correttamente
	 */
	public boolean AddLevel(float Sconto, float ScontoVIP, int PointsToNextLevel) {

		if(Sconto > ScontoVIP) return false;

		if(this.Levels.size() == 0){
			this.Levels.add(new Level(Sconto, ScontoVIP, PointsToNextLevel));
			return true;
		}

		//Indice dove ho aggiunto il nuovo livello. Se rimane -1 vuol dire che non ho aggiunto il livello
		boolean Added = false;

		//Aggiungo il livello alla lista locale, controllando se sia tutto nella norma
		for(int i=0; i<this.Levels.size(); i++){

			//Inseriamo il livello in mezzo a quelli esistenti (od all' inizio)
			if((Sconto < this.Levels.get(i).ScontoAdvantage && ScontoVIP < this.Levels.get(i).ScontoAdvantageVIP) ||
					(Sconto < this.Levels.get(i).ScontoAdvantage && ScontoVIP == this.Levels.get(i).ScontoAdvantageVIP) ||
					(Sconto == this.Levels.get(i).ScontoAdvantage && ScontoVIP < this.Levels.get(i).ScontoAdvantageVIP)){

				Added = true;

				this.Levels.add(i, new Level(Sconto, ScontoVIP, PointsToNextLevel));
				break;
			}

			if((Sconto < this.Levels.get(i).ScontoAdvantage && ScontoVIP > this.Levels.get(i).ScontoAdvantageVIP) ||
					(Sconto == this.Levels.get(i).ScontoAdvantage && ScontoVIP == this.Levels.get(i).ScontoAdvantageVIP) ||
					(Sconto > this.Levels.get(i).ScontoAdvantage && ScontoVIP < this.Levels.get(i).ScontoAdvantageVIP)) return false;

		}

		//Non ho ancora aggiunto il livello
		if(!Added) this.Levels.add(new Level(Sconto, ScontoVIP, PointsToNextLevel));

		return true;
	}

	public boolean equals(Object o){
		return this == o || ((Livelli) o).PartitaIVAStart.equals(this.PartitaIVAStart) && ((Livelli) o).PartitaIVAFinish.equals(this.PartitaIVAFinish);
	}

	private class Level{
		public int PointsToNextLevel;
		public float ScontoAdvantage;
		public float ScontoAdvantageVIP;

		public Level(float scontoAdvantage, float scontoAdvantageVIP, int pointsToNextLevel){
			this.PointsToNextLevel = pointsToNextLevel;
			this.ScontoAdvantage = scontoAdvantage;
			this.ScontoAdvantageVIP = scontoAdvantageVIP;
		}

		public String toString(){
			return "PointsToNextLevel = "+this.PointsToNextLevel+"\nSconto = "+this.ScontoAdvantage+
					"\nSconto VIP = "+this.ScontoAdvantageVIP+"\n\n";
		}

		public boolean equals(Object o){

			if(o == this) return true;

			Level A = (Level) o;

			return (A.PointsToNextLevel == this.PointsToNextLevel &&
					A.ScontoAdvantage == this.ScontoAdvantage &&
					A.ScontoAdvantageVIP == this.ScontoAdvantageVIP);
		}
	}
}