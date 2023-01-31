package LogicLayer;

import DataLayer.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecensioneTest extends CreateNewNames {

    String[] PartiteIVA = CreateNewPartitaIVAAziende(2);
    String[] Nicknames = CreateNewNicknameUsers(1);

    @BeforeEach
    public final void PreTest(){

        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), true);

        PianoFedeltà a = new PianoFedeltà(PartiteIVA[0]);

        UserDatabaseRequest.SendRequest("update piano_vantaggi set Recensione = true where PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFinish = '"+PartiteIVA[0]+"'");

        assertEquals(GeneralManagement.AddUtente("nome1", "cognome1", 1, "regione1", "città1", "comune1", false, "email1", "numero1", Nicknames[0], "pass1", true, "codice1", false), true);
    }

    @AfterEach
    public final void PostTest(){

        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[0], "Pass1"), true);

        assertEquals(GeneralManagement.RemoveUtente(Nicknames[0], "pass1"), true);

    }

    @Test
    public final void GeneralTest(){

        //Ridare (Fallimento)
        assertEquals(GeneralManagement.GetRecensioneSito(PartiteIVA[0], Nicknames[0]), null);

        //Eliminare (Fallimento)
        assertEquals(GeneralManagement.RemoveRecensioneSito(PartiteIVA[0], Nicknames[0]), false);

        Timestamp tempo = Timestamp.valueOf("2019-01-01 00:00:00.0");

        //Creare (Successo)
        assertEquals(GeneralManagement.AddRecensioneSito(PartiteIVA[0], Nicknames[0], "Descrizione1", 4.3f, tempo), true);

        //Creare non possibilità recensione (Fallimento)
        assertEquals(GeneralManagement.AddRecensioneSito(PartiteIVA[0], Nicknames[0], "Descrizione1", 4.3f, tempo), false);

        //Ridare (Successo)
        Recensione recensione = GeneralManagement.GetRecensioneSito(PartiteIVA[0], Nicknames[0]);

        //Controllare i dati
        assertEquals(recensione.getPartitaIVA(), PartiteIVA[0]);
        assertEquals(recensione.getNickname(), Nicknames[0]);
        assertEquals(recensione.getDescrizione(), "Descrizione1");
        assertEquals(recensione.getStelle(), 4.3f);
        assertEquals(recensione.getTimeStamp(), tempo);

        //Creare (Fallimento)
        assertEquals(GeneralManagement.AddRecensioneSito(PartiteIVA[0], Nicknames[0], "Descrizione1", 4.3f, tempo), false);

        //Eliminare (Successo)
        assertEquals(GeneralManagement.RemoveRecensioneSito(PartiteIVA[0], Nicknames[0]), true);

    }
}
