import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe rappresentante una Query ridata
 */
public class DatabaseQuery {

    //Matrice di valori
    private final String[][] Query;

    //Array di nomi delle colonne
    private final String[] ColumnsName;

    /**
     * Prendo una risposta SQL e la trasformo con questa classe, per comodità
     * @param QueryResult
     * @throws SQLException
     */
    public DatabaseQuery(ResultSet QueryResult) throws SQLException {

        //Questo oggetto mi serve per ottenere le colonne a disposizione
        ResultSetMetaData MetaResult = QueryResult.getMetaData();

        //Risultato da ridare
        List<String[]> Rows = new ArrayList<>();

        //Variabili che mi serviranno per raccogliere i dati che mi servono
        String[] row;
        int i;

        //Creo la mia matrice con il numero di righe variabili
        while(QueryResult.next()) {

            row = new String[MetaResult.getColumnCount()];
            for (i=0; i<MetaResult.getColumnCount(); i++) row[i] = QueryResult.getString(MetaResult.getColumnName(i+1));
            Rows.add(row);
        }

        //Tiro fuori le lunghezze della Query
        int RowCount = Rows.size();
        int ColumnCount = MetaResult.getColumnCount();

        //Matrice di valori con lunghezze fisse
        this.Query = new String[ColumnCount][RowCount];

        //Riempio la Matrix, ho assegnato in maniera "inversa" così un array corrisponde effettivamente ad una colonna
        for(i=0; i<ColumnCount; i++) for(int r=0; r < RowCount; r++) Query[i][r] = Rows.get(r)[i];

        //Array di nomi delle colonne
        this.ColumnsName = new String[ColumnCount];

        //Riempio ColumnsName
        for(i=0; i<ColumnCount; i++) ColumnsName[i] = MetaResult.getColumnName(i+1);

        if(this.Query.length != this.ColumnsName.length) throw new IllegalArgumentException("Le lunghezze delle righe "+this.Query.length+" ed il numero di colonne "+this.ColumnsName.length+" non combaciano");

    }

    /**
     * Mi trovo l' indice di un valore
     * @param x
     * @return l' indice del valore x nella ColumnsName
     */
    private int IndexArray(String x){
        for(int i=0; i<this.ColumnsName.length; i++) if(this.ColumnsName[i].equals(x)) return i;
        throw new IndexOutOfBoundsException("Non esiste una colonna '"+x+"'");
    }  //Trovo l' indice del nome della colonna chiamata "x"

    /**
     * Ottengo la lista dei nomi delle colonne
     * @return la lista dei nomi
     */
    public String[] GetColumnsName(){ return this.ColumnsName; }  //Testato

    /**
     * Ottengo il nome della i-esima colonna
     * @param i
     * @return nome della i-esima colonna
     */
    public String GetColumnName(int i) {return this.ColumnsName[i]; }  //Testato

    /**
     * Ottengo i-esima colonna
     * @param i
     * @return i-esima colonna
     */
    public String[] GetColumn(int i){ return this.Query[i]; }  //Testato

    /**
     * Ottengo la colonna chiamata name
     * @param name
     * @return colonna chiamata name
     */
    public String[] GetColumn(String name){ return this.Query[this.IndexArray(name)]; }  //Testato

    /**
     * Ottengo i-esima riga
     * @param index
     * @return i-esima riga
     */
    public String[] GetRow(int index){

        String[] Result = new String[this.ColumnsName.length];
        for(int i=0; i<this.ColumnsName.length; i++) Result[i] = this.Query[i][index];

        return Result;
    }  //Testato

    /**
     * Ottengo il valore della column-esima colonna nella row-esima riga
     * @param row
     * @param column
     * @return valore della column-esima colonna nella row-esima riga
     */
    public String GetValue(int row, int column){ return this.Query[column][row]; }  //Testato

    /**
     * Ottengo il valore della colonna con il nome column nella row-esima riga
     * @param row
     * @param column
     * @return valore della colonna con il nome column nella row-esima riga
     */
    public String GetValue(int row, String column){ return this.Query[this.IndexArray(column)][row]; }  //Testato

    /**
     * Ottengo il numero di colonne
     * @return numero di colonne
     */
    public int GetColumnCount() {return this.ColumnsName.length; }    //Testato

    /**
     * Ottengo il numero di righe
     * @return numero di righe
     */
    public int GetRowCount() {
        if(this.Query.length > 0) return this.Query[0].length;
        else return 0;
    }    //Testato

}
