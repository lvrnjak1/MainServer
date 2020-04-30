package ba.unsa.etf.si.mainserver.models.pr;


public enum ReservationStatusName {
    UNVERIFIED, //dok se ne potvrdi
    VERIFIED, //kada se potvrdi putem koda
    CANCELED, //kada covjek otkaze
    DONE //kada bartender kaze da su ovi ljudi dosli i to je to
}
