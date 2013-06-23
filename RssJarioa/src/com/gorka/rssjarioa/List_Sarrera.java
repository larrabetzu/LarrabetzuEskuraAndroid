package com.gorka.rssjarioa;


public class List_Sarrera {
	private String egune;
	private String tituloa; 
	private String lekua;
    private String ordue;
    private String deskribapena;

    public List_Sarrera( String tituloa,String egune, String ordue, String lekua, String deskribapena) {
	    this.egune = egune;
	    this.tituloa = tituloa; 
	    this.lekua = lekua;
        this.ordue = ordue;
        this.deskribapena = deskribapena;
	}
	
	public String get_tituloa() { 
	    return tituloa; 
	}
	
	public String get_lekua() { 
	    return lekua; 
	}

    public String get_ordue(){
        return ordue;
    }
	
	public String get_egune() {
	    return egune;
	}

    public String get_deskribapena(){
        return deskribapena;
    }

}
