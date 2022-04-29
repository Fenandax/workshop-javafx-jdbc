package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.ServicoDepartamento;
import model.services.ServicoVendedor;

public class ListSellerController implements Initializable, DataChangeListener {

	private ServicoVendedor servico;

	@FXML
	private TableView<Seller> tabelaSeller;

	@FXML
	private TableColumn<Seller, Integer> colunaId;

	@FXML
	private TableColumn<Seller, String> colunaNome;
	
	@FXML
	private TableColumn<Seller, String> colunaEmail;
	
	@FXML
	private TableColumn<Seller, Date> colunaNascimento;
	
	@FXML
	private TableColumn<Seller, Double> colunaSalario;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	private ObservableList<Seller> obsLista;

	@FXML
	public void onBtNewAction(ActionEvent evento) {
		Stage parentStage = Utils.currentStage(evento);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/FormularioSeller.fxml", parentStage);
	}

	public void setServicoSeller(ServicoVendedor servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNodes();
	}

	private void initializaNodes() {
		colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("name"));
		colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colunaNascimento.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(colunaNascimento, "dd/MM/yyyy");
		colunaSalario.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(colunaSalario, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tabelaSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTabela() {
		if (servico == null) {
			throw new IllegalStateException("Serviço está nulo");
		}
		List<Seller> lista = servico.encontrarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tabelaSeller.setItems(obsLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String nomeAbsoluto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane pane = loader.load();

			FormularioSellerController controller = loader.getController();
			controller.setSeller(obj);
			controller.setServicos(new ServicoVendedor(), new ServicoDepartamento());
			controller.loadAssociatedObjects();
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
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Erro ao carregar view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTabela();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/FormularioSeller.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmar", "Tem certeza que quer deletar?");

		if (result.get() == ButtonType.OK) {
			if (servico == null) {
				throw new IllegalStateException("O serviço está nulo");
			}
			try {
				servico.remover(obj);
				updateTabela();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}