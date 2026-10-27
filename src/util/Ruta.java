package util;

public class Ruta {
	private String nombreEspia1;
	private String nombreEspia2;
	private Double probabilidadInterceptacion;
	
	public Ruta(String nombreEspia1, String nombreEspia2, Double probabilidadInterceptacion) {
		this.nombreEspia1 = nombreEspia1;
		this.nombreEspia2 = nombreEspia2;
		this.probabilidadInterceptacion = probabilidadInterceptacion;
	}

	public String getNombreCoordenada1() {
		return nombreEspia1;
	}

	public String getNombreCoordenada2() {
		return nombreEspia2;
	}

	public Double getProbabilidadInterceptacion() {
		return probabilidadInterceptacion;
	}
	
}
