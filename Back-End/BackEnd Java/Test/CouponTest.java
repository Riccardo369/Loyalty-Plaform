import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CouponTest extends CreateNewNames {

    String[] PartiteIVA = CreateNewPartitaIVAAziende(1);
    String[] Nicknames = CreateNewNicknameUsers(1);

    @Test
    public final void GeneralTest(){

        //Creo le basi
        assertEquals(GeneralManagement.AddAzienda("nome1", PartiteIVA[0], "Pass1", "Sito1", "Email1"), true);
        assertEquals(GeneralManagement.AddUtente("nome1", "cognome1", 1, "Regione1", "Città1", "Comune1", true, "Email1", "numero1", Nicknames[0], "Pass1", true, "Codice1", false), true);

        //Remove (Fallita)
        assertEquals(GeneralManagement.RemoveCoupon("Codice1", PartiteIVA[0]), false);

        //Get (Fallito)
        assertEquals(GeneralManagement.GetCoupon("Codice1", PartiteIVA[0]), null);

        //Add (successo)
        assertEquals(GeneralManagement.AddCoupon("Codice1", PartiteIVA[0], Nicknames[0], 0.1f, 0.2f, 0.3f), true);

        //Get (successo)
        Coupon C = GeneralManagement.GetCoupon("Codice1", PartiteIVA[0]);

        //Lettura dati
        assertEquals(C.Codice, "Codice1");
        assertEquals(C.PartitaIVA, PartiteIVA[0]);
        assertEquals(C.Nickname, Nicknames[0]);
        assertEquals(C.Costo, 0.1f);
        assertEquals(C.Percentuale, 0.2f);
        assertEquals(C.MinimoPrezzoAttivazione, 0.3f);

        //Add (Fallito)
        assertEquals(GeneralManagement.AddCoupon("Codice1", PartiteIVA[0], Nicknames[0], 0.0f, 0.0f, 0.0f), false);

        //Remove (Successo)
        assertEquals(GeneralManagement.RemoveCoupon("Codice1", PartiteIVA[0]), true);

        assertEquals(GeneralManagement.RemoveAzienda(PartiteIVA[0], "Pass1"), true);
        assertEquals(GeneralManagement.RemoveUtente(Nicknames[0], "Pass1"), true);

    }
}
