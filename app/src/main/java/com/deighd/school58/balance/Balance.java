package com.deighd.school58.balance;

public class Balance {
    private String name;
    private Float residue;
    private Float previousResidue;
    private String currency;

    public Float getPreviousResidue() {
        return  previousResidue;
    }

    void setPreviousResidue(Float previousResidue) {
        this.previousResidue = previousResidue;
    }

    public Float getResidue() {
        return residue;
    }

    public String getCurrency() {
        return currency;
    }

    public String getResidueWithCurrency() {
        return residue.toString() + " " + currency;
    }

    public String getPreviousResidueWithCurrency() {
        return previousResidue.toString() + " " + currency;
    }

    public String getName() {
        return name;
    }

    public void setResidue(Float residue) {
        this.residue = residue;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setName(String name) {
        this.name = name;
    }
}
