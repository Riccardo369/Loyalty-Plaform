DROP database if exists ids;

Create database if not exists ids;

USE ids;

create table if not exists UTENTE(
Nome VARCHAR(100),
Cognome VARCHAR(100),
Età int(3),
Regione VARCHAR(100),
Città VARCHAR(100),
Comune VARCHAR(100),
Sesso VARCHAR(100),
Email VARCHAR(100),
NumeroDiTelefono VARCHAR(100),
Nickname VARCHAR(100),
Password VARCHAR(100),
StatoVIP boolean,
CodiceCarta VARCHAR(100),
ConsensoDati boolean,
Primary Key (Nickname));

create table if not exists AZIENDA(
NomeAzienda VARCHAR(100),
Email VARCHAR(100),
PartitaIVA VARCHAR(100) not null,
Password VARCHAR(100),
SitoWeb VARCHAR(100),
Primary Key (PartitaIVA));

create table if not exists DIPENDENTE(
Nome VARCHAR(100),
Cognome VARCHAR(100),
Email VARCHAR(100),
PartitaIVA VARCHAR(100) not null,
Password VARCHAR(100),
CodiceDipendente VARCHAR(100) not null,
NumeroDiTelefono VARCHAR(100),
Primary Key (CodiceDipendente, PartitaIVA));

create table if not exists PRENOTAZIONE(
Nickname VARCHAR(100), 
PartitaIVA VARCHAR(100),
Inizio TimeStamp not null default CURRENT_TIMESTAMP, 
Fine TimeStamp not null default CURRENT_TIMESTAMP, 
Descrizione VARCHAR(100),
Primary Key (Nickname, PartitaIVA, Inizio, Fine));

create table if not exists RECENSIONE_SITO(
PartitaIVA VARCHAR(100) not null,
Nickname VARCHAR(100),
Descrizione VARCHAR(100),
Stelle float(10),
MomentoTemporale TimeStamp not null default CURRENT_TIMESTAMP,
Primary Key (Nickname, PartitaIVA));

create table if not exists PIANO_VANTAGGI(
PartitaIVAStart VARCHAR(100) not null,
PartitaIVAFinish VARCHAR(100) not null,
PuntiPercentual float(10),
PuntiPercentualVIP float(10),
PuntiLivelloPercentual float(10),
PuntiLivelloPercentualVIP float(10),
Recensione boolean,
Prenotazione boolean,
Coupon boolean,
ModuloReffeal boolean,
PercentualBase float(10),
Scadenza TimeStamp null,
Primary Key (PartitaIVAStart, PartitaIVAFinish));

create table if not exists CATALOGO_PREMI_PUNTI(
PartitaIVA VARCHAR(100),
NomePremio VARCHAR(100),
Quantità int(10),
Punti int(10),
Costo float(10));

create table if not exists LIVELLI(
PartitaIVAStart VARCHAR(100),
PartitaIVAFinish VARCHAR(100),
PointsNextLivello int(10),
AumentoSconto float(10),
AumentoScontoVIP float(10));

create table if not exists VANTAGGI_UTENTE_AZIENDA(
PartitaIVA VARCHAR(100),
Nickname VARCHAR(100) not null,
Sconto float(10),
Punti int(10),
Livello int(10),
ActualPointsLivello int(10),
Primary Key (PartitaIVA, Nickname));

create table if not exists CODICI_COUPON(
Codice VARCHAR(100) not null,
PartitaIVA VARCHAR(100) not null,
Nickname VARCHAR(100),
Costo float(10),
Percentuale float(10),
MinimoPrezzoAttivazione float(10),
Primary Key (Codice, PartitaIVA));

create table if not exists CODICI_MODULI_REFFEAL(
PartitaIVA VARCHAR(100),
NicknameBeneficario VARCHAR(100),
ScontoPercentuale float(10),
PercentualeTrattenuta float(10),
Codice VARCHAR(100),
Primary Key (Codice));

create table if not exists ABBONAMENTO_PUNTO_VENDITA(
PartitaIVA VARCHAR(100) Primary Key,
Costo float(10),
Inizio TimeStamp null,
Fine TimeStamp null,
Data TimeStamp null);

create table if not exists ABBONAMENTO_SMS(
PartitaIVA VARCHAR(100),
Data TimeStamp null,
SMS int(100));

create table if not exists SMS_AZIENDE_INVIATI(
PartitaIVA VARCHAR(100),
NumeroMittente VARCHAR(100),
NumeroDestinatario VARCHAR(100),
Messaggio VARCHAR(100),
Data TimeStamp null);

create table if not exists EMAIL_AZIENDE_INVIATI(
EmailAzienda VARCHAR(100),
EmailDestinatario VARCHAR(100),
Messaggio VARCHAR(100),
Data TimeStamp null);

create table if not exists VISITE_SITI(
PartitaIVA VARCHAR(100),
Nickname VARCHAR(100),
Data TimeStamp null);

create table if not exists ACQUISTO_UTENTE(
PartitaIVA VARCHAR(100),
Nickname VARCHAR(100),
Prodotto VARCHAR(100),
Costo float(10),
Data TimeStamp null);

create table if not exists PUNTI_GEOGRAFICI(
PartitaIVA VARCHAR(100),
PuntoGeografico POINT,
Primary Key (PartitaIVA, PuntoGeografico));

create table if not exists CASSETTA_COLLABORAZIONI(
PartitaIVARichiedente VARCHAR(100),
PartitaIVARicevente VARCHAR(100),
AccettazioneRicevente boolean default null,
Primary key (PartitaIVARichiedente, PartitaIVARicevente)
);