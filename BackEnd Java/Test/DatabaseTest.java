import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTest {

    @BeforeAll
    public static final void PreTest(){ UserDatabaseRequest.SendRequest("drop table if exists testing"); }

    @AfterAll
    public static final void PostTest(){
        UserDatabaseRequest.SendRequest("drop table if exists testing");
    }

    @Test
    public final void AdministratorTest() {

        //Creiamo la tabella che non esiste
        assertEquals(UserDatabaseRequest.SendRequest("create table if not exists testing(nome VARCHAR(100), cognome VARCHAR(100), età int(10), sesso boolean)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        //Puliamo la tabella (nel caso in cui esiste già)
        assertEquals(UserDatabaseRequest.SendRequest("delete from testing") == null, true);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        //Inseriamo i dati
        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUE ('nome1', 'cognome1', 10, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUE ('nome2', 'cognome2', 40, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUE ('nome3', 'cognome3', 20, false)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUE ('nome4', 'cognome4', 1, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUE ('nome5', 'cognome5', 40, false)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUE ('nome6', 'cognome6', 1238, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("select * from testing") != null, true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        //Leggiamo tutti i dati
        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select * from testing");
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        //Testo le lunghezze
        assertEquals(Response.GetRowCount() == 6, true);
        assertEquals(Response.GetColumnCount() == 4, true);

        //Testo i nomi delle colonne
        String[] NomiProva = {"nome", "cognome", "età", "sesso"};
        for (int i = 0; i < NomiProva.length; i++) {
            assertEquals(NomiProva[i].equals(Response.GetColumnsName()[i]), true);
            assertEquals(NomiProva[i].equals(Response.GetColumnName(i)), true);
        }

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nomecognome, età, sesso) VALUE ('nome1', 'cognome1', 10, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), false);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        //Testo i valori
        String[][] Values = {
                {"nome1", "cognome1", "10", "1"},
                {"nome2", "cognome2", "40", "1"},
                {"nome3", "cognome3", "20", "0"},
                {"nome4", "cognome4", "1", "1"},
                {"nome5", "cognome5", "40", "0"},
                {"nome6", "cognome6", "1238", "1"}};

        //Test dei valori
        for (int i = 0; i < Values.length; i++) {
            for (int r = 0; r < Values[i].length; r++) {
                assertEquals(Values[i][r].equals(Response.GetValue(i, r)), true);
                assertEquals(Values[i][r].equals(Response.GetValue(i, NomiProva[r])), true);
                assertEquals(Values[i][r].equals(Response.GetColumn(r)[i]), true);
                assertEquals(Values[i][r].equals(Response.GetColumn(NomiProva[r])[i]), true);
                assertEquals(Values[i][r].equals(Response.GetRow(i)[r]), true);
            }
        }

        assertEquals(UserDatabaseRequest.SendRequest("drop table tesing"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), false);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        //Cancello la tabella
        assertEquals(UserDatabaseRequest.SendRequest("drop table testing"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("create table if not exists testing(nome VARCHAR(100), cognome VARCHAR(100), età int(10), sesso boolean)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("create table ifnot exists testing(nome VARCHAR(100), cognome VARCHAR(100), età int(10), sesso boolean)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), false);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

    }

    @Test
    public final void MultipleCompactSend() throws SQLException {

        String[] Commands1 = {
                "update testing set name = 1",
                "insert into testing(nome, cognome, età, sesso) VALUE ('nome4', 'cognome4', 40, false)",
                "delete from testing"};

        String[] Commands2 = {
                "update testing set Nome = 1",
                "insert into testing(nome, cognome, età, sesso) VALE ('nome4', 'cognome4', 40, false)",
                "delete from testing"};

        String[] Commands3 = {
                "update testing set Nome = 1",
                "insert into testing(nome, cognome, età, sesso) VALUE ('nome4', 'cognome4', 40, false)",
                "delete fom testing"};

        String[] Commands4 = {
                "update testing set Nome = 1",
                "insert into testing(nome, cognome, età, sesso) VALUE ('nome4', 'cognome4', 40, false)",
                "delete from testing"};

        assertEquals(UserDatabaseRequest.SendRequest("create table if not exists testing(nome VARCHAR(100), cognome VARCHAR(100), età int(10), sesso boolean)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUES ('nome1', 'cognome1', 10, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUES ('nome2', 'cognome2', 20, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("insert into testing(nome, cognome, età, sesso) VALUES ('nome3', 'cognome3', 30, true)"), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendTransaction(Commands1), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), false);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendTransaction(Commands2), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), false);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendTransaction(Commands3), null);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), false);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("select * from testing").GetRowCount(), 3);

        assertEquals(UserDatabaseRequest.SendTransaction(Commands4) != null, true);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

        assertEquals(UserDatabaseRequest.SendRequest("select * from testing").GetRowCount(), 0);
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(UserDatabaseRequest.GetConnectionIsOpen(), false);

    }
}
