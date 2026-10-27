package interfazEspia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import logica.Arista;
import logica.Grafo;
import logica.Kruskal;
import logica.Prim;
import util.Coordenada;
import util.Ruta;

import java.awt.Color;

public class MenuMapa {
	
	private HashMap<String,Coordenada> marcadoresMapa;
	private Grafo redEspias;
	
	private JFrame frameMapa;
	private Mapa mapa;
	private String argentina, china, rusia,usa;
	private Coordinate argentinaCoordenadas, chinaCoordenadas, rusiaCoordenadas, usaCoordenadas;
	private JTextField txtNumeroProbabilidad;
	private JTextField txtProbabilidadDeIntercepcion;
	private JTextField txtLatitudEntre;
	private JTextField txtLongitudEntre;
	private JTextField txtValorLatitudEntre;
	private JTextField txtValorLongitudEntre;

	
	public MenuMapa(String ubicacion) {
		inicializarCoordenadas();
		initialize(ubicacion);
	}

	private void initialize(String ubicacion) {
		
		this.marcadoresMapa = new HashMap<String,Coordenada>();
	
		crearFrameMapa();
		crearMapa();
		
		frameMapa.getContentPane().add(mapa);
		
		JButton archivoButton = new JButton("Leer Archivo");
		archivoButton.setBackground(new Color(0, 0, 0));
		archivoButton.setForeground(new Color(255, 255, 255));
		archivoButton.setBounds(240, 527, 107, 23);
		archivoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					redEspias = new Grafo();
					if(obtenerEspiasJson(ubicacion)) {
						obtenerRutasJson();
						
						Double max = buscarConexionPeligrosa();
						
						ubicarEspias();
						ubicarConexionesEspias(max);
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		mapa.add(archivoButton);
		
		JButton enviarMensajeButton = new JButton("Enviar Mensaje");
		enviarMensajeButton.setBackground(new Color(0, 0, 0));
		enviarMensajeButton.setForeground(new Color(255, 255, 255));
		enviarMensajeButton.setBounds(217, 502, 154, 23);
		enviarMensajeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					if(redEspias != null) {
						mapa.removeAllMapPolygons();
						enviarMensaje();
						
						Double max = buscarConexionPeligrosa();
						
						ubicarConexionesEspias(max);
						
						JOptionPane.showMessageDialog(frameMapa, "Tiempo ejecucion Kruskal: " + Kruskal.obtenerTiempoKruskal() + " milisegundos\n"
													+ "Tiempo ejecucion Prim: " + Prim.getTiempoEjecucion() + " milisegundos");
					}
					else {
						JOptionPane.showMessageDialog(frameMapa, "La red de espias no existe");
					}
				
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
		});
		mapa.add(enviarMensajeButton);
				
		visualizarProbabilidadIntercepcion();
		mostrarRangoCoordenadas(ubicacion);
		
		JLabel fondoLabel = new JLabel("");
		fondoLabel.setIcon(new ImageIcon(MenuMapa.class.getResource("/Imagenes/imagenFondo.png")));
		fondoLabel.setBounds(0, 491, 584, 70);
		mapa.add(fondoLabel);
		
	}

	private void mostrarRangoCoordenadas(String ubicacion) {
		txtValorLongitudEntre = new JTextField();
		txtValorLongitudEntre.setHorizontalAlignment(SwingConstants.CENTER);
		txtValorLongitudEntre.setForeground(Color.WHITE);
		txtValorLongitudEntre.setEditable(false);
		txtValorLongitudEntre.setColumns(10);
		txtValorLongitudEntre.setBackground(Color.BLACK);
		txtValorLongitudEntre.setBounds(106, 528, 124, 20);
		mapa.add(txtValorLongitudEntre);
		
		txtValorLatitudEntre = new JTextField();
		txtValorLatitudEntre.setEditable(false);
		txtValorLatitudEntre.setForeground(new Color(255, 255, 255));
		txtValorLatitudEntre.setBackground(new Color(0, 0, 0));
		txtValorLatitudEntre.setHorizontalAlignment(SwingConstants.CENTER);
		txtValorLatitudEntre.setBounds(100, 503, 107, 20);
		mapa.add(txtValorLatitudEntre);
		txtValorLatitudEntre.setColumns(10);
		
		txtLongitudEntre = new JTextField();
		txtLongitudEntre.setText("Longitud entre");
		txtLongitudEntre.setHorizontalAlignment(SwingConstants.CENTER);
		txtLongitudEntre.setForeground(Color.WHITE);
		txtLongitudEntre.setEditable(false);
		txtLongitudEntre.setColumns(10);
		txtLongitudEntre.setBackground(Color.BLACK);
		txtLongitudEntre.setBounds(10, 528, 86, 20);
		mapa.add(txtLongitudEntre);
		
		txtLatitudEntre = new JTextField();
		txtLatitudEntre.setEditable(false);
		txtLatitudEntre.setHorizontalAlignment(SwingConstants.CENTER);
		txtLatitudEntre.setForeground(new Color(255, 255, 255));
		txtLatitudEntre.setBackground(new Color(0, 0, 0));
		txtLatitudEntre.setText("Latitud entre");
		txtLatitudEntre.setBounds(10, 503, 75, 20);
		mapa.add(txtLatitudEntre);
		txtLatitudEntre.setColumns(10);
		
		if(argentina.equals(ubicacion)) {
			txtValorLatitudEntre.setText("-34.579 / -34.433");
			txtValorLongitudEntre.setText("-58.817 / -58.621");
			mapa.setDisplayPosition(argentinaCoordenadas, 12);
		}
		if(china.equals(ubicacion)) {
			txtValorLatitudEntre.setText("39.614 / 39.487");
			txtValorLongitudEntre.setText("116.345 / 116.148");
			mapa.setDisplayPosition(chinaCoordenadas, 12);
		}
		if(rusia.equals(ubicacion)) {
			txtValorLatitudEntre.setText("55.505 / 55.412");
			txtValorLongitudEntre.setText("37.467 / 37.272");
			mapa.setDisplayPosition(rusiaCoordenadas, 12);
		}
		if(usa.equals(ubicacion)) {
			txtValorLatitudEntre.setText("40.821 / 40.697");
			txtValorLongitudEntre.setText("-73.888 / -74.083");
			mapa.setDisplayPosition(usaCoordenadas, 12);
		}
	}

	private void visualizarProbabilidadIntercepcion() {
		txtProbabilidadDeIntercepcion = new JTextField();
		txtProbabilidadDeIntercepcion.setForeground(new Color(255, 255, 255));
		txtProbabilidadDeIntercepcion.setBackground(new Color(0, 0, 0));
		txtProbabilidadDeIntercepcion.setText("Probabilidad de Intercepcion");
		txtProbabilidadDeIntercepcion.setHorizontalAlignment(SwingConstants.CENTER);
		txtProbabilidadDeIntercepcion.setEditable(false);
		txtProbabilidadDeIntercepcion.setBounds(411, 503, 163, 20);
		mapa.add(txtProbabilidadDeIntercepcion);
		txtProbabilidadDeIntercepcion.setColumns(10);
		
		txtNumeroProbabilidad = new JTextField();
		txtNumeroProbabilidad.setForeground(new Color(255, 255, 255));
		txtNumeroProbabilidad.setBackground(new Color(0, 0, 0));
		txtNumeroProbabilidad.setHorizontalAlignment(SwingConstants.CENTER);
		txtNumeroProbabilidad.setEditable(false);
		txtNumeroProbabilidad.setBounds(444, 528, 86, 20);
		mapa.add(txtNumeroProbabilidad);
		txtNumeroProbabilidad.setColumns(10);
	}

	private void crearFrameMapa() {
		frameMapa = new JFrame();
		frameMapa.setBounds(425, 100, 600, 600);
		frameMapa.setResizable(false);
		frameMapa.setVisible(true);
		frameMapa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMapa.setTitle("Mapa");
	}

	private void crearMapa() {
		mapa = new Mapa();
		mapa.setZoomControlsVisible(false);
	}

	private void inicializarCoordenadas() {
		argentina = "argentina";
		argentinaCoordenadas = new Coordinate(-34.521, -58.719);
		
		china = "china";
		chinaCoordenadas = new Coordinate(39.541, 116.247);
		
		rusia = "rusia";
		rusiaCoordenadas = new Coordinate(55.452, 37.370);
		
		usa = "usa";
		usaCoordenadas = new Coordinate(40.750, -73.985);
	}


	private boolean obtenerEspiasJson(String ubicacion) {
		Gson gson = new Gson();
		
        try {
        	//Ejemplo de ruta de archivo valida: "src/espias.json"
            FileReader reader = new FileReader("src/espias.json");

            // Utiliza JsonParser para analizar el JSON
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Itera sobre cada elemento del array JSON
            for (JsonElement element : jsonArray) {
                // Convierte cada elemento en un objeto Coordenada
                Coordenada coordenada = gson.fromJson(element, Coordenada.class);
                
                if(!coordenadaValida(ubicacion, coordenada)) {
                	this.redEspias = new Grafo();
                	this.marcadoresMapa.clear();
                	
                	JOptionPane.showMessageDialog(frameMapa, "Las coordenadas de: " + coordenada.getNombre() 
                									+ " son invalidas para " + ubicacion);
                	return false;
                }
                
                if(this.redEspias.agregarVertice(coordenada.getNombre())) {
                	this.marcadoresMapa.put(coordenada.getNombre(), coordenada);
                }
                else {
                	JOptionPane.showMessageDialog(frameMapa, "El espia: " + coordenada.getNombre() + " ya existe");
                	return false;
                }
            }

            // Cierra el FileReader
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
	}

	private void obtenerRutasJson() {
		Gson gson = new Gson();
		
        try {
        	//Ejemplo de ruta de archivo valida: "src/espias.json"
            FileReader reader = new FileReader("src/rutas.json");

            // Utiliza JsonParser para analizar el JSON
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Itera sobre cada elemento del array JSON
            for (JsonElement element : jsonArray) {
                // Convierte cada elemento en un objeto Coordenada
                Ruta ruta = gson.fromJson(element, Ruta.class);
                this.redEspias.agregarArista(ruta.getNombreCoordenada1(),ruta.getNombreCoordenada2(),ruta.getProbabilidadInterceptacion());
            }

            // Cierra el FileReader
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	private void ubicarEspias() {
		for(Entry<String, Coordenada> entrada : marcadoresMapa.entrySet()) {
			String nombreEspia = entrada.getKey();
			Double latitud = entrada.getValue().getLatitud();
			Double longitud = entrada.getValue().getLongitud();
			
			Coordinate coordenadasEspia = new Coordinate(latitud, longitud);
			MapMarkerDot marker = new MapMarkerDot(nombreEspia, coordenadasEspia);
			marker.getStyle().setBackColor(Color.BLACK);
			marker.getStyle().setColor(Color.RED);
			
			mapa.addMapMarker(marker);
		}
	}
	
	
	private void ubicarConexionesEspias(Double max) {
		List<Arista> conexiones = redEspias.obtenerAristas();
		
		for(Arista a : conexiones) {
			
			String espia1 = a.getOrigen();
			String espia2 = a.getDestino();
			String probabilidad = a.getPeso() + "";
			
			Double latitudEspia1 = marcadoresMapa.get(espia1).getLatitud();
			Double longitudEspia1 = marcadoresMapa.get(espia1).getLongitud();
			
			Double latitudEspia2 = marcadoresMapa.get(espia2).getLatitud();
			Double longitudEspia2 = marcadoresMapa.get(espia2).getLongitud();
			
			Coordinate coordenadasEspia1 = new Coordinate(latitudEspia1, longitudEspia1);
			Coordinate coordenadasEspia2 = new Coordinate(latitudEspia2, longitudEspia2);
			
			List<Coordinate> coordenadasEspias = new ArrayList<Coordinate>(Arrays.asList(coordenadasEspia1, coordenadasEspia2, coordenadasEspia2));
			
			MapPolygon conexion = new MapPolygonImpl(probabilidad, coordenadasEspias);
			
			if(a.getPeso() == max) {
				conexion.getStyle().setColor(Color.RED);
			}
			else {
				conexion.getStyle().setColor(Color.GREEN.darker());
			}
			
			mapa.addMapPolygon(conexion);
		}
	}
	
	
	private boolean coordenadaValida(String ubicacion,Coordenada c) {

		if(ubicacion ==  "argentina" ) {
			return ((-34.579) < c.getLatitud() && c.getLatitud() < (-34.433))
					&&
					((-58.817) < c.getLongitud() && c.getLongitud() < (-58.621));
		}
		
		if(ubicacion == "china") {
			return (c.getLatitud() <  (39.614) &&  (39.487) < c.getLatitud()) 
					&& 
					(c.getLongitud() < (116.345)   && (116.148) < c.getLongitud());
		}
		
		if(ubicacion == "usa") {
			return (c.getLatitud() <  (40.821) &&  (40.697) < c.getLatitud()) 
					&& 
					(c.getLongitud() < (-73.888)   && (-74.083) < c.getLongitud());
		}
		
		if(ubicacion == "rusia") {
			return (c.getLatitud() <  (55.505) &&  (55.412) < c.getLatitud()) 
					&& 
					(c.getLongitud() < (37.467)   && (37.272) < c.getLongitud());
		}
		
		return false;
	}
	
	
	private Double buscarConexionPeligrosa() {
		List<Arista> conexiones = redEspias.obtenerAristas();
		Double max = 0.0;
		for(Arista a : conexiones) {
			Double actual = a.getPeso();
			
			if(actual > max) {
				max = actual;
			}
		}
		return max;
	}
	
	
	public void enviarMensaje() {
		Prim.prim(redEspias);
		this.redEspias = Kruskal.obtenerAGMComoGrafo(this.redEspias);
		
		List<Arista> conexiones = redEspias.obtenerAristas();
		Double probabilidadMaxima = 0.0;
		for(Arista conexion : conexiones) {
			Double probabilidadActual = conexion.getPeso();
			
			if(probabilidadActual > probabilidadMaxima) {
				probabilidadMaxima = probabilidadActual;
			}
		}
		txtNumeroProbabilidad.setText(probabilidadMaxima + "");	
	}
}
