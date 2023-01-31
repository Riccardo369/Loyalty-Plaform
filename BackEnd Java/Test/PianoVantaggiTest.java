

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PianoVantaggiTest extends  CreateNewNames{

    public String[] PartiteIVA = CreateNewPartitaIVAAziende(2);
    public String[] Nicknames = CreateNewNicknameUsers(1);

    @AfterEach
    public final void PostTest(){
        UserDatabaseRequest.SendTransaction(new String[] {"delete from piano_vantaggi where " +
                "(PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFinish = '"+PartiteIVA[0]+"') or" +
                "(PartitaIVAStart = '"+PartiteIVA[1]+"' and PartitaIVAFinish = '"+PartiteIVA[0]+"') or " +
                "(PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFinish = '"+PartiteIVA[1]+"') or " +
                "(PartitaIVAStart = '"+PartiteIVA[1]+"' and PartitaIVAFinish = '"+PartiteIVA[1]+"')",

                "delete from utente where Nickname = '"+Nicknames[0]+"'",

                "delete from azienda where PartitaIVA = '"+PartiteIVA[0]+"' and PartitaIVA = '"+PartiteIVA[1]+"'",

                "delete from vantaggi_utente_azienda where Nickname = '"+Nicknames[0]+"'",

                "delete from livelli where " +
                        "(PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFinish = '"+PartiteIVA[0]+"') or " +
                        "(PartitaIVAStart = '"+PartiteIVA[1]+"' and PartitaIVAFinish = '"+PartiteIVA[0]+"') or " +
                        "(PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFinish = '"+PartiteIVA[1]+"') or " +
                        "(PartitaIVAStart = '"+PartiteIVA[1]+"' and PartitaIVAFinish = '"+PartiteIVA[1]+"')"});
    }

    @Test
    public final void GeneralTest(){

        Timestamp Scadenza1 = Timestamp.valueOf("2030-01-01 00:00:00.0");
        Timestamp Scadenza2 = Timestamp.valueOf("2018-01-01 00:00:00.0");

        assertEquals(GeneralManagement.RemovePianoFedeltà(PartiteIVA[0]), false);
        assertEquals(GeneralManagement.RemovePianoFedeltà(PartiteIVA[1]), false);
        assertEquals(GeneralManagement.RemoveCollaborazione(PartiteIVA[0], PartiteIVA[1]), false);

        assertEquals(GeneralManagement.AddCollaborazione(PartiteIVA[0], PartiteIVA[1], 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, Scadenza1), false);
        assertEquals(GeneralManagement.AddPianoFedeltà( PartiteIVA[0], 0.1f, 0.2f, 0.3f, 0.4f,  true, true, true, true), true);
        assertEquals(GeneralManagement.AddPianoFedeltà( PartiteIVA[0], 0.1f, 0.2f, 0.3f, 0.4f,  true, true, true, true), false);
        assertEquals(GeneralManagement.AddCollaborazione(PartiteIVA[0], PartiteIVA[1], 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, Scadenza1), false);
        assertEquals(GeneralManagement.AddPianoFedeltà( PartiteIVA[1], 0.1f, 0.2f, 0.3f, 0.4f,  true, true, true, true), true);
        assertEquals(GeneralManagement.AddCollaborazione(PartiteIVA[0], PartiteIVA[1], 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, Scadenza2), false);
        assertEquals(GeneralManagement.AddCollaborazione(PartiteIVA[0], PartiteIVA[1], 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, Scadenza1), true);

        assertEquals(GeneralManagement.GetPianoFedeltà(PartiteIVA[0]).CreateAdvantage(Nicknames[0], 1), false);

        assertEquals(GeneralManagement.AddUtente("a", "b", 1, "c", "d", "e", true, "f", "g", Nicknames[0], "Pass1", false, "i", true), true);

        //Inviamo denaro a PartiteIVA[0] attraverso il suo piano Fedeltà
        assertEquals(GeneralManagement.GetPianoFedeltà(PartiteIVA[0]).CreateAdvantage(Nicknames[0], 5), true);

        //Controlliamo i dati
        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select * from vantaggi_utente_azienda where Nickname = '"+Nicknames[0]+"'");

        assertEquals(Response.GetValue(0, "Sconto"), "0.0");
        assertEquals(Response.GetValue(0, "Punti"), "0");
        assertEquals(Response.GetValue(0, "Livello"), "0");
        assertEquals(Response.GetValue(0, "ActualPointsLivello"), "1");

        PianoFedeltà a = GeneralManagement.GetPianoFedeltà(PartiteIVA[0]);

        //Aggiungo i livelli e rifaccio il reinserimento
        assertEquals(a.livelli.AddLevel(0.2f, 0.3f, 2), true);
        assertEquals(a.livelli.AddLevel(0.3f, 0.4f, 10), true);

        assertEquals(a.livelli.RequestSetData(), true);

        assertEquals(a.CreateAdvantage(Nicknames[0], 5), true);

        Response = UserDatabaseRequest.SendRequest("select * from vantaggi_utente_azienda where Nickname = '"+Nicknames[0]+"' and PartitaIVA = '"+PartiteIVA[0]+"'");

        assertEquals(Response.GetValue(0, "Sconto"), "0.3");
        assertEquals(Response.GetValue(0, "Punti"), "0");
        assertEquals(Response.GetValue(0, "Livello"), "1");
        assertEquals(Response.GetValue(0, "ActualPointsLivello"), "0");

        //Metto il vincolo della cassetta delle collaborazioni, CreateAdvantage di Collaborazione deve ridare false se: il booleano è null, oppure false
        UserDatabaseRequest.SendRequest("insert into cassetta_collaborazioni (PartitaIVARichiedente, PartitaIVARicevente, AccettazioneRicevente) VALUES ('"+this.PartiteIVA[0]+"', '"+this.PartiteIVA[1]+"', null)");

        assertEquals(GeneralManagement.GetCollaborazione(PartiteIVA[0], PartiteIVA[1]).CreateAdvantage(Nicknames[0], 10), false);

        UserDatabaseRequest.SendRequest("update cassetta_collaborazioni set AccettazioneRicevente = false where PartitaIVARichiedente = '"+PartiteIVA[0]+"' and PartitaIVARicevente = '"+PartiteIVA[1]+"'");

        assertEquals(GeneralManagement.GetCollaborazione(PartiteIVA[0], PartiteIVA[1]).CreateAdvantage(Nicknames[0], 10), false);

        UserDatabaseRequest.SendRequest("delete from cassetta_collaborazioni where PartitaIVARichiedente = '"+PartiteIVA[0]+"' and PartitaIVARicevente = '"+PartiteIVA[1]+"'");

        //Inviamo denaro a PartiteIVA[1] attraverso la collaborazione
        assertEquals(GeneralManagement.GetCollaborazione(PartiteIVA[0], PartiteIVA[1]).CreateAdvantage(Nicknames[0], 10), true);

        Response = UserDatabaseRequest.SendRequest("select * from vantaggi_utente_azienda where Nickname = '"+Nicknames[0]+"' and PartitaIVA = '"+PartiteIVA[1]+"'");

        assertEquals(Response.GetValue(0, "Sconto"), "0.0");
        assertEquals(Response.GetValue(0, "Punti"), "0");
        assertEquals(Response.GetValue(0, "Livello"), "0");
        assertEquals(Response.GetValue(0, "ActualPointsLivello"), "1");

        //Cancelliamo ciò che abbiamo creato
        assertEquals(GeneralManagement.RemovePianoFedeltà(PartiteIVA[0]), true);
        assertEquals(GeneralManagement.RemovePianoFedeltà(PartiteIVA[1]), true);
        assertEquals(GeneralManagement.RemoveCollaborazione(PartiteIVA[0], PartiteIVA[1]), true);
    }


    @Test
    public final void DestryoCollaborazioniTest(){

        UserDatabaseRequest.SendTransaction(new String[]{"insert into piano_vantaggi (PartitaIVAStart, PartitaIVAFinish, PuntiPercentual, PuntiPercentualVIP, PuntiLivelloPercentual, PuntiLivelloPercentualVIP, Recensione, Prenotazione, Coupon, ModuloReffeal, PercentualBase, Scadenza) VALUES " +
                "('"+PartiteIVA[0]+"', '"+PartiteIVA[1]+"', 0.0, 0.0, 0.0, 0.0, false, false, false, false, 0.0, '2018-01-01 00:00:00.0')",

                "insert into cassetta_collaborazioni (PartitaIVARichiedente, PartitaIVARicevente, AccettazioneRicevente) VALUES ('"+PartiteIVA[0]+"', '"+PartiteIVA[1]+"', false)"});

        GeneralManagement.DestroyCollaborazioniScadute();

        assertEquals(UserDatabaseRequest.SendRequest("select * from piano_vantaggi where PartitaIVAStart = '"+PartiteIVA[0]+"' and PartitaIVAFinish = '"+PartiteIVA[1]+"'").GetRowCount(), 0);
        assertEquals(UserDatabaseRequest.SendRequest("select * from cassetta_collaborazioni where PartitaIVARichiedente = '"+PartiteIVA[0]+"' and PartitaIVARicevente = '"+PartiteIVA[1]+"'").GetRowCount(), 0);

    }
}
