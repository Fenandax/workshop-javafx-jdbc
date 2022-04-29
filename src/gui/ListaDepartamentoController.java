package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.ServicoDepartamento;

public class ListaDepartamentoController implements Initializable, DataChangeListener {

	private ServicoDepartamento servico;

	@FXML
	private TableView<Department> tabelaDepartamento;

	@FXML
	private TableColumn<Department, Integer> colunaId;

	@FXML
	private TableColumn<Department, String> colunaNome;

	@FXML
	private Button btNovo;

	private ObservableList<Department> obsLista;

	@FXML
	public void onBtNewAction(ActionEvent evento) {
		Stage parentStage = Utils.estagioAtual(evento);
		Department obj = new Department();
		createDialogForm(obj, "/gui/FormularioDepartamento.fxml", parentStage);
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
		List<Department> lista = servico.encontrarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tabelaDepartamento.setItems(obsLista);
	}

	private void createDialogForm(Department obj, String nomeAbsoluto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane pane = loader.load();

			FormularioDepartamentoController controller = loader.getController();
			controller.setDepartamento(obj);
			controller.setServicoDepartamento(new ServicoDepartamento());
			controller.subscribeDataChangeListener(this);
			controller.updateDadosFormulario();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Coloque os dados do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTabela();

	}
}
