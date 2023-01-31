package LogicLayer;

import DataLayer.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DipendenteTest extends CreateNewNames{

    String[] PartiteIVA = CreateNewPartitaIVAAziende(2);
    String[] Codici = CreateNewCodiceDipendente(2);

    @BeforeEach
    public final void PreTest(){
        UserDatabaseRequest.SendTransaction(new String[] {"delete from dipendente where PartitaIVA = '"+PartiteIVA[0]+"'",
                "delete from dipendente where PartitaIVA = '"+PartiteIVA[0]+"' and CodiceDipendente = '"+Codici[0]+"'"});

        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
    }

    @AfterEach
    public final void PostTest(){
        DatabaseQuery[] Response = UserDatabaseRequest.SendTransaction(new String[] {
                "select * from azienda where PartitaIVA = '"+PartiteIVA[0]+"' or PartitaIVA = '"+PartiteIVA[1]+"'",

                "select * from dipendente where (PartitaIVA = '"+PartiteIVA[0]+"' and CodiceDipendente = '"+Codici[0]+"') or " +
                        "(PartitaIVA = '"+PartiteIVA[1]+"' and CodiceDipendente = '"+Codici[1]+"')",

                "delete from piano_vantaggi where PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFInish = '"+PartiteIVA[0]+"'"});

        assertEquals(UserDatabaseRequest.GetLastExecuteDone(), true);
        assertEquals(Response[0].GetRowCount(), 0);
        assertEquals(Response[1].GetRowCount(), 0);
    }

    @Test
    public final void GestioneAccountDipendente(){

        assertEquals(GeneralManagement.GetDipendente(Codici[0], PartiteIVA[0], "Pass1"), null);
        assertEquals(GeneralManagement.RemoveDipendente(Codici[0], PartiteIVA[0], "Pass1"), false);

        assertEquals(GeneralManagement.AddDipendente("a", "b", "c", PartiteIVA[0], "Pass1", Codici[0], "f"), false);
        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), true);
        assertEquals(GeneralManagement.AddDipendente("a", "b", "c", PartiteIVA[0], "Pass1", Codici[0], "f"), true);

        assertEquals(GeneralManagement.GetDipendente(Codici[0], PartiteIVA[0], "Pass1") != null, true);

        //Con il togliere l' azienda, tolgo anche i dipendenti dentro
        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[0], "Pass1"), true);
    }

    @Test
    public final void GetSetDataDipendente() throws SQLException {

        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), true);

        assertEquals(GeneralManagement.AddDipendente("a", "b", "c", PartiteIVA[0], "Pass1", Codici[0], "f"), true);

        Dipendente a = GeneralManagement.GetDipendente(Codici[0], PartiteIVA[0], "Pass1");

        assertEquals(a.getNome(), "a");
        assertEquals(a.getCognome(), "b");
        assertEquals(a.getEmail(), "c");
        assertEquals(a.getPartitaIVA(), PartiteIVA[0]);
        assertEquals(a.Password, "Pass1");
        assertEquals(a.getCodiceDipendente(), Codici[0]);
        assertEquals(a.getNumeroDiTelefono(), "f");

        assertEquals(a.RequestSetData("a1", "b1", "Email1", PartiteIVA[1], Codici[1], "f1"), true);

        assertEquals(a.getNome(), "a1");
        assertEquals(a.getCognome(), "b1");
        assertEquals(a.getEmail(), "Email1");
        assertEquals(a.getPartitaIVA(), PartiteIVA[1]);
        assertEquals(a.Password, "Pass1");
        assertEquals(a.getCodiceDipendente(), Codici[1]);
        assertEquals(a.getNumeroDiTelefono(), "f1");

        assertEquals(a.RequestGetCurrentData(), true);

        assertEquals(a.getNome(), "a1");
        assertEquals(a.getCognome(), "b1");
        assertEquals(a.getEmail(), "Email1");
        assertEquals(a.getPartitaIVA(), PartiteIVA[1]);
        assertEquals(a.Password, "Pass1");
        assertEquals(a.getCodiceDipendente(), Codici[1]);
        assertEquals(a.getNumeroDiTelefono(), "f1");

        assertEquals(a.RequestSetPassword("Pass1"), true);
        assertEquals(a.RequestSetPassword("Pass2"), true);

        assertEquals(a.Password.equals("Pass2"), true);

        new Dipendente(Codici[1], PartiteIVA[1], "Pass2");

        assertEquals(a.RequestSetData("a1", "b1", "Email1", PartiteIVA[0], Codici[1], "f1"), true);
        assertEquals(a.RequestSetData("a1", "b1", "Email1", PartiteIVA[0], Codici[1], "f1"), true);

        assertEquals(new Azienda(PartiteIVA[0], "Pass1").RequestSetData("nome1", PartiteIVA[1], "Sito1", "Email1"), true);

        //C' è un buco, quando azienda modifica la PartitaIVA, tutti gl' oggetti Dipendente non valgono più
        assertThrows(IllegalArgumentException.class, () -> {new Dipendente(Codici[1], PartiteIVA[1], "Pass2"); });

        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[1], "Pass1"), true);
        assertEquals(GeneralManagement.RemoveDipendente(Codici[1], PartiteIVA[0], "Pass2"), true);
    }
}