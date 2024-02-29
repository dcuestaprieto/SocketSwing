package model;

public class Entrada {
	private String TierEntrada;
	private String CantidadEntradas;
	
	public Entrada(String tierEntrada, String cantidadEntradas) {
		super();
		TierEntrada = tierEntrada;
		CantidadEntradas = cantidadEntradas;
	}

	public String getTierEntrada() {
		return TierEntrada;
	}

	public void setTierEntrada(String tierEntrada) {
		TierEntrada = tierEntrada;
	}

	public String getCantidadEntradas() {
		return CantidadEntradas;
	}

	public void setCantidadEntradas(String cantidadEntradas) {
		CantidadEntradas = cantidadEntradas;
	}
	
	
}
