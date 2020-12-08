package com.movie.model;

public class MovieResponsethemoviedb {
	
	private String original_title;
	private String release_date;
	private StringBuilder criadores;
	private StringBuilder elenco;
	
	public String getOriginal_title() {
		return original_title;
	}
	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}
	public String getRelease_date() {
		return release_date;
	}
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	
	public StringBuilder getCriadores() {
		return criadores;
	}
	public void setCriadores(StringBuilder criadores) {
		this.criadores = criadores;
	}
	public StringBuilder getElenco() {
		return elenco;
	}
	public void setElenco(StringBuilder elenco) {
		this.elenco = elenco;
	}
	
}
