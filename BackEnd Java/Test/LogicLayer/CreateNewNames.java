package LogicLayer;

import DataLayer.*;

public class CreateNewNames {

    static char Char = 't';

    private static final String[] CreateNewNames(int len, String Column, String Table){

        //Prendo tutti i codici-dipendenti esistenti
        DatabaseQuery Response = UserDatabaseRequest.SendRequest("select "+Column+" from "+Table+"");

        String[] Nomi = Response.GetColumn(Column);

        //Creo la lunghezza massima che il nome del mio utente deve avere (in modo tale da non entrare in conflitto con altri nomi)
        int Max = 0;
        for(int i=0; i<Nomi.length; i++) if(Nomi[i].length() > Max) Max = Nomi[i].length();

        String[] Result = new String[len];

        for(int i=0; i<Result.length; i++){
            Result[i] = "";
            for(int r=0; r<i; r++) Result[i] += Char;
        }

        for(int i=0; i<Max+1; i++) for(int r=0; r<Result.length; r++) Result[r] += Char;

        return Result;
    }

    public static final String[] CreateNewNicknameUsers(int len) { return CreateNewNames(len, "Nickname", "utente"); }

    public static final String[] CreateNewPartitaIVAAziende(int len) { return CreateNewNames(len, "PartitaIVA", "azienda"); }

    public static final String[] CreateNewCodiceDipendente(int len) { return CreateNewNames(len, "CodiceDipendente", "dipendente"); }
}
