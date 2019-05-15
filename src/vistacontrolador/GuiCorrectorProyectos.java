/* 
 * @author Aritz Romo Pimoulier 
 */


package vistacontrolador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.AlumnoNoExistenteExcepcion;
import modelo.CorrectorProyectos;
import modelo.Proyecto;

public class GuiCorrectorProyectos extends Application
{

	private MenuItem itemLeer;
	private MenuItem itemGuardar;
	private MenuItem itemSalir;

	private TextField txtAlumno;
	private Button btnVerProyecto;

	private RadioButton rbtAprobados;
	private RadioButton rbtOrdenados;
	private Button btnMostrar;

	private TextArea areaTexto;

	private Button btnClear;
	private Button btnSalir;

	private CorrectorProyectos corrector; // el modelo

	@Override
	public void start(Stage stage) {

		corrector = new CorrectorProyectos();

		BorderPane root = crearGui();

		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle("- Corrector de proyectos -");
		scene.getStylesheets().add(getClass()
		                .getResource("/css/application.css").toExternalForm());
		stage.show();
	}

	private BorderPane crearGui() {

		BorderPane panel = new BorderPane();
		MenuBar barraMenu = crearBarraMenu();
		panel.setTop(barraMenu);

		VBox panelPrincipal = crearPanelPrincipal();
		panel.setCenter(panelPrincipal);

		HBox panelBotones = crearPanelBotones();
		panel.setBottom(panelBotones);

		return panel;
	}

	private MenuBar crearBarraMenu() {

		MenuBar barraMenu = new MenuBar();

		Menu menu = new Menu("Archivo");

		itemLeer = new MenuItem("_Leer de fichero");
		itemLeer.setAccelerator(KeyCombination.keyCombination("CTRL+L"));
		itemLeer.setOnAction(event -> leerDeFichero());
		
		itemGuardar = new MenuItem("_Guardar en fichero");
		itemGuardar.setAccelerator(KeyCombination.keyCombination("CTRL+G"));
		itemGuardar.setDisable(true);
		itemGuardar.setOnAction(event -> salvarEnFichero());
		
		itemSalir = new MenuItem("_Salir");
		itemSalir.setAccelerator(KeyCombination.keyCombination("CTRL+S"));
		itemSalir.setOnAction(event -> salir());
		
		menu.getItems().addAll(itemLeer, itemGuardar, new SeparatorMenuItem(), itemSalir);
		
		barraMenu.getMenus().add(menu);
		
		return barraMenu;
	}

	private VBox crearPanelPrincipal() {

		VBox panel = new VBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);

		Label lblEntrada = new Label("Panel de entrada");
		lblEntrada.getStyleClass().add("titulo-panel");
		lblEntrada.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		
		Label lblOpciones = new Label("Panel de opciones");
		lblOpciones.getStyleClass().add("titulo-panel");
		lblOpciones.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		areaTexto = new TextArea();
		areaTexto.setPrefSize( Integer.MAX_VALUE, Integer.MAX_VALUE );
		


		panel.getChildren().addAll(lblEntrada, crearPanelEntrada(), lblOpciones, crearPanelOpciones(), areaTexto);
		
		return panel;
	}

	private HBox crearPanelEntrada() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		
		Label lblAlumno = new Label("Alumno");
		
		txtAlumno = new TextField();
		txtAlumno.setPrefColumnCount(30);
		txtAlumno.setOnAction(event -> verProyecto());

		btnVerProyecto = new Button("Ver proyecto");
		btnVerProyecto.setPrefWidth(120);
		btnVerProyecto.setOnAction(event -> verProyecto());
		
		panel.getChildren().addAll(lblAlumno, txtAlumno, btnVerProyecto);

		return panel;
	}

	private HBox crearPanelOpciones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(50);
		panel.setAlignment(Pos.CENTER);
		
		rbtAprobados = new RadioButton("Mostrar aprobados");
		rbtAprobados.setSelected(true);
		
		rbtOrdenados = new RadioButton("Mostrar ordenados");
		
		ToggleGroup grupo = new ToggleGroup();
		grupo.getToggles().addAll(rbtAprobados, rbtOrdenados);
		
		btnMostrar = new Button("Mostrar");
		btnMostrar.setPrefWidth(120);
		btnMostrar.setOnAction(event -> mostrar());
		
		panel.getChildren().addAll( rbtAprobados, rbtOrdenados, btnMostrar);

		return panel;
	}

	private HBox crearPanelBotones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		panel.setAlignment(Pos.CENTER_RIGHT);

		btnClear = new Button("Clear");
		btnClear.setPrefWidth(90);
		btnClear.setOnAction(event -> clear());
		
		btnSalir = new Button("Salir");
		btnSalir.setPrefWidth(90);
		btnSalir.setOnAction(event -> salir());

		panel.getChildren().addAll(btnClear, btnSalir);

		return panel;
	}

	private void salvarEnFichero() {
		
		areaTexto.clear();
		
		try {
			corrector.guardarOrdenadosPorNota();
			areaTexto.setText("Guardados en fichero de texto los proyectos ordenados");
		}
		catch(IOException e) {
			areaTexto.setText("Error al salvar fichero: ");
		}


	}

	private void leerDeFichero() {
		
		corrector.leerDatosProyectos();
		areaTexto.clear();
		areaTexto.setText("Leído fichero de texto \n\n");
		areaTexto.appendText(corrector.getErrores().toString() + "\n\n");
		areaTexto.appendText("Ya están guardados en memoria los datos de los proyectos");

		itemLeer.setDisable(true);
		itemGuardar.setDisable(false);


	}

	private void verProyecto() {
		cogerFoco();
		
		if(!itemLeer.isDisable()) {
			areaTexto.clear();
			areaTexto.setText("No se han leído todavía los datos del fichero\r\n" + 
					"Vaya a la opción leer del menú");
		}
		
		if(itemLeer.isDisable() && !txtAlumno.getText().isEmpty()) {
			try {
				Proyecto proyecto = corrector.proyectoDe(txtAlumno.getText());
				areaTexto.setText(proyecto.toString());
			}
			catch(AlumnoNoExistenteExcepcion e) {
				areaTexto.setText("Alumno/a no existente");
			}
		}
		else {
			areaTexto.clear();
			areaTexto.setText("Teclee nombre de alumno/a");
		}

	}

	private void mostrar() {

		clear();
		cogerFoco();
		
		if(!itemLeer.isDisable()) {
			areaTexto.setText("Primero tienes que leer del fichero");
		}
		else {
			if (rbtAprobados.isSelected()) {
				areaTexto.setText("Han aprobado el proyecto " + Integer.toString(corrector.aprobados()) + " alumno/as");
			}
			else {
				ArrayList<Map.Entry<String, Proyecto>> ordenados = (ArrayList<Entry<String, Proyecto>>) corrector.ordenadosPorNota();
				Iterator<Map.Entry<String, Proyecto>> it = ordenados.iterator();
				
				while(it.hasNext()) {
					Map.Entry<String, Proyecto> entrada = it.next();
					areaTexto.appendText("\n\t\t" + entrada.getKey() + "\n" );
					areaTexto.appendText(entrada.getValue().toString());
				}
			}
		}




	}

	private void cogerFoco() {

		txtAlumno.requestFocus();
		txtAlumno.selectAll();

	}

	private void salir() {

		System.exit(0);
	}

	private void clear() {

		areaTexto.clear();
		cogerFoco();
	}

	public static void main(String[] args) {

		launch(args);
	}
}
