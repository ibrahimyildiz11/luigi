package be.vdab.luigi.domain;

public class Adres {
    private String straat;
    private String huisNr;
    private int postcode;
    private String gemeente;

    public String getStraat() {
        return straat;
    }

    public String getHuisNr() {
        return huisNr;
    }


    public int getPostcode() {
        return postcode;
    }

    public String getGemeente() {
        return gemeente;
    }
    public Adres(String straat, String huisNr, int postcode, String gemeente) {
        this.straat = straat;
        this.huisNr = huisNr;
        this.postcode = postcode;
        this.gemeente = gemeente;
    }
}
