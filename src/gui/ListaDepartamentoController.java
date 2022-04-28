package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.ServicoDepartamento;

public class ListaDepartamentoController implements Initializable {

	private ServicoDepartamento servico;
	
	@FXML
	private TableView<Departamento> tabelaDepartamento;
	
	@FXML
	private TableColumn<Departamento, Integer> colunaId;
	
	@FXML
	private TableColumn<Departamento, String> colunaNome;
	
	@FXML
	private Button btNovo;
	
	private ObservableList<Departamento> obsLista;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	public void setServicoDepartamento(ServicoDepartamento servico) {
		this.servico = servico;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNodes();
	}

	private void initializaNodes() {
		colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tabelaDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTabela() {
		if (servico == null) {
			throw new IllegalStateException("Serviço está nulo");
		}
		List<Departamento> lista = servico.encontrarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tabelaDepartamento.setItems(obsLista);
	}
}
