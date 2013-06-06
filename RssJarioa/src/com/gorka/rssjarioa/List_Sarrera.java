package com.gorka.rssjarioa;


public class List_Sarrera {
	private int idImagen; 
	private String tituloa; 
	private String lekua;
    private String egune;
    private String deskribapena;

    public List_Sarrera(int idImagen, String tituloa, String egune, String lekua, String deskribapena) {
	    this.idImagen = idImagen; 
	    this.tituloa = tituloa; 
	    this.lekua = lekua;
        this.egune = egune;
        this.deskribapena = deskribapena;
	}
	
	public String get_tituloa() { 
	    return tituloa; 
	}
	
	public String get_lekua() { 
	    return lekua; 
	}

    public String get_egune(){
        return egune;
    }
	
	public int get_idImagen() {
	    return idImagen; 
	}

    public String get_deskribapena(){
        return deskribapena;
    }

}
