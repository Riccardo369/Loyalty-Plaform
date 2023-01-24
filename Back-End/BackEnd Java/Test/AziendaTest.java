import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AziendaTest extends CreateNewNames {

    String[] PartiteIVA = CreateNewPartitaIVAAziende(2);

    @BeforeEach
    public final void PreTest(){
        UserDatabaseRequest.SendRequest("delete from azienda where PartitaIVA = '"+PartiteIVA[0]+"'");
        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
    }

    @AfterEach
    public final void PostTest(){
        UserDatabaseRequest.SendTransaction(new String[] {
                "select * from azienda where PartitaIVA = '"+PartiteIVA[0]+"' or PartitaIVA = '"+PartiteIVA[1]+"'",
                "select * from piano_vantaggi where PartitaIVAStart = '"+PartiteIVA[0]+"' or PartitaIVAStart = '"+PartiteIVA[1]+"'"});

        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
    }

    @Test
    public final void GestioneAccountAzienda(){

        assertEquals(GeneralManagement.GetAzienda(PartiteIVA[0], "Pass1"), null);
        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[0], "Pass1"), false);

        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), true);
        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), false);

        assertEquals(GeneralManagement.GetAzienda(PartiteIVA[0], "Pass1").equals(new Azienda(PartiteIVA[0], "Pass1")), true);
        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[0], "Pass1"), true);

    }

    @Test
    public final void GetSetDataAzienda() throws SQLException {

        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), true);

        Azienda a = GeneralManagement.GetAzienda(PartiteIVA[0], "Pass1");

        assertEquals(a.getNomeAzienda(), "nome1");
        assertEquals(a.getPartitaIVA(), PartiteIVA[0]);
        assertEquals(a.getSitoWeb(), "Sito1");
        assertEquals(a.Password, "Pass1");
        assertEquals(a.getEmail(), "Email1");

        assertEquals(a.RequestSetData("Nome1", PartiteIVA[1], "sito1", "email1"), true);

        assertEquals(a.getNomeAzienda(), "Nome1");
        assertEquals(a.getPartitaIVA(), PartiteIVA[1]);
        assertEquals(a.getSitoWeb(), "sito1");
        assertEquals(a.Password, "Pass1");
        assertEquals(a.getEmail(), "email1");

        assertEquals(a.RequestGetCurrentData(), true);

        assertEquals(a.getNomeAzienda(), "Nome1");
        assertEquals(a.getPartitaIVA(), PartiteIVA[1]);
        assertEquals(a.getSitoWeb(), "sito1");
        assertEquals(a.Password, "Pass1");
        assertEquals(a.getEmail(), "email1");

        assertEquals(a.RequestSetPassword("Pass1"), true);
        assertEquals(a.RequestSetPassword("Pass2"), true);

        assertEquals(a.Password.equals("Pass2"), true);

        new Azienda(PartiteIVA[1], "Pass2");

        assertEquals(a.RequestSetData("Nome1", PartiteIVA[0], "sito1", "email1"), true);
        assertEquals(a.RequestSetData("Nome1", PartiteIVA[0], "sito1", "email1"), true);

        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[0], "Pass2"), true);
    }
}