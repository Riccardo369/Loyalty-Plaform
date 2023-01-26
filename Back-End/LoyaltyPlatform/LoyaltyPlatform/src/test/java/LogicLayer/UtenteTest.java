package LogicLayer;

import DataLayer.*;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtenteTest extends CreateNewNames {

    String[] Nicknames = CreateNewNicknameUsers(2);

    @BeforeEach
    public final void PreTest(){
        UserDatabaseRequest.SendRequest("delete from utente where Nickname = '"+Nicknames[0]+"'");
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
    }

    @AfterEach
    public final void PostTest(){
        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select * from utente where Nickname = '"+Nicknames[0]+"' or Nickname = '"+Nicknames[1]+"'");
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(Response.GetRowCount(), 0);

    }

    @Test
    public final void GestioneAccountAzienda(){

        assertEquals(GeneralManagement.GetUtente(Nicknames[0], "Pass1"), null);
        assertEquals(GeneralManagement.RemoveUtente(Nicknames[0], "Pass1"), false);

        assertEquals(GeneralManagement.AddUtente("a", "b", 1, "c", "d", "e", true, "f", "g", Nicknames[0], "Pass1", false, "i", true), true);
        assertEquals(GeneralManagement.AddUtente("a", "b", 1, "c", "d", "e", true, "f", "g", Nicknames[0], "Pass1", false, "i", true), false);


        assertEquals(GeneralManagement.GetUtente(Nicknames[0], "Pass1").equals(new Utente(Nicknames[0], "Pass1")), true);
        assertEquals(GeneralManagement.RemoveUtente(Nicknames[0], "Pass1"), true);

    }

    @Test
    public final void GetSetDataAzienda() throws SQLException {

        assertEquals(GeneralManagement.AddUtente("a", "b", 1, "c", "d", "e", true, "f", "g", Nicknames[0], "Pass1", false, "i", true), true);

        Utente a = GeneralManagement.GetUtente(Nicknames[0], "Pass1");

        assertEquals(a.getNome(), "a");
        assertEquals(a.getCognome(), "b");
        assertEquals(a.getNumeroDiTelefono(), "g");
        assertEquals(a.getStatoVIP(), false);
        assertEquals(a.getCodiceCarta(), "i");
        assertEquals(a.getEmail(), "f");
        assertEquals(a.getCittà(), "d");
        assertEquals(a.getEtà(), 1);
        assertEquals(a.getRegione(), "c");
        assertEquals(a.getComune(), "e");
        assertEquals(a.getMale(), true);
        assertEquals(a.getConsensoDati(), true);
        assertEquals(a.getNickname(), Nicknames[0]);
        assertEquals(a.Password,"Pass1");

        assertEquals(a.RequestSetData(Nicknames[1], "a1", "b1", "g1", "f1", true, "i1", "d1", 2, "c1", "e1", false, false), true);

        assertEquals(a.getNome(), "a1");
        assertEquals(a.getCognome(), "b1");
        assertEquals(a.getNumeroDiTelefono(), "g1");
        assertEquals(a.getStatoVIP(), true);
        assertEquals(a.getCodiceCarta(), "i1");
        assertEquals(a.getEmail(), "f1");
        assertEquals(a.getCittà(), "d1");
        assertEquals(a.getEtà(), 2);
        assertEquals(a.getRegione(), "c1");
        assertEquals(a.getComune(), "e1");
        assertEquals(a.getMale(), false);
        assertEquals(a.getConsensoDati(), false);
        assertEquals(a.getNickname(), Nicknames[1]);
        assertEquals(a.Password,"Pass1");

        assertEquals(a.RequestGetCurrentData(), true);

        assertEquals(a.getNome(), "a1");
        assertEquals(a.getCognome(), "b1");
        assertEquals(a.getNumeroDiTelefono(), "g1");
        assertEquals(a.getStatoVIP(), true);
        assertEquals(a.getCodiceCarta(), "i1");
        assertEquals(a.getEmail(), "f1");
        assertEquals(a.getCittà(), "d1");
        assertEquals(a.getEtà(), 2);
        assertEquals(a.getRegione(), "c1");
        assertEquals(a.getComune(), "e1");
        assertEquals(a.getMale(), false);
        assertEquals(a.getConsensoDati(), false);
        assertEquals(a.getNickname(), Nicknames[1]);
        assertEquals(a.Password,"Pass1");

        assertEquals(a.RequestSetPassword("Pass1"), true);
        assertEquals(a.RequestSetPassword("Pass2"), true);

        assertEquals(a.Password.equals("Pass2"), true);

        assertEquals(GeneralManagement.GetUtente(Nicknames[1], "Pass2").equals(new Utente(Nicknames[1], "Pass2")), true);

        assertEquals(a.RequestSetData(Nicknames[0], "a1", "b1", "g1", "f1", true, "i1", "d1", 2, "c1", "e1", false, false), true);
        assertEquals(a.RequestSetData(Nicknames[0], "a1", "b1", "g1", "f1", true, "i1", "d1", 2, "c1", "e1", false, false), true);

        assertEquals(GeneralManagement.RemoveUtente(Nicknames[0], "Pass2"), true);
    }
}