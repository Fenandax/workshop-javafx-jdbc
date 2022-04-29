package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.ServicoDepartamento;
import model.services.ServicoVendedor;

public class FormularioSellerController implements Initializable {

	private Seller entidade;

	private ServicoVendedor servico;

	private ServicoDepartamento servicoDepartamento;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpNascimento;

	@FXML
	private TextField txtSalario;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErroNome;

	@FXML
	private Label labelErroEmail;

	@FXML
	private Label labelErroNascimento;

	@FXML
	private Label labelErroSalario;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	private ObservableList<Department> obsList;

	public void setSeller(Seller entidade) {
		this.entidade = entidade;
	}

	public void setServicos(ServicoVendedor servico, ServicoDepartamento servicoDepartamento) {
		this.servico = servico;
		this.servicoDepartamento = servicoDepartamento;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (entidade == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (servico == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entidade = getDadosFormulados();
			servico.salveOuUpdate(entidade);
			notifyDataChangeListener();
			Utils.currentStage(evento).close();
		} catch (ValidationException e) {
			setMensagemErro(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getDadosFormulados() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Exceção de validação");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErros("nome", "O nome não pode ficar vazio");
		}
		obj.setName(txtNome.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErros("email", "O e-mail não pode ficar vazio");
		}
		obj.setEmail(txtEmail.getText());

		if (dpNascimento.getValue() == null) {
			exception.addErros("nascimento", "A data de nascimento não pode ficar vazia");
		}
		else {
		Instant instant = Instant.from(dpNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
		obj.setBirthDate(Date.from(instant));
		}

		if (txtSalario.getText() == null || txtSalario.getText().trim().equals("")) {
			exception.addErros("salario", "O salário não pode ficar vazio");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtSalario.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());

		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent evento) {
		Utils.currentStage(evento).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalario);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpNascimento, "dd/MM/yyyy");

		initializeComboBoxDepartment();
	}

	public void updateDadosFormulario() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula");
		}

		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getName());
		txtEmail.setText(entidade.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entidade.getBaseSalary()));
		if (entidade.getBirthDate() != null) {
			dpNascimento.setValue(LocalDate.ofInstant(entidade.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entidade.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entidade.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (servicoDepartamento == null) {
			throw new IllegalStateException("Departamento está nulo");
		}
		List<Department> lista = servicoDepartamento.encontrarTodos();
		obsList = FXCollections.observableArrayList(lista);
		comboBoxDepartment.setItems(obsList);
	}

	private void setMensagemErro(Map<String, String> erros) {
		Set<String> espacos = erros.keySet();

		if (espacos.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}
		else {
			labelErroNome.setText(erros.get(""));
		}

		if (espacos.contains("email")) {
			labelErroEmail.setText(erros.get("email"));
		}
		else {
			labelErroEmail.setText(erros.get(""));
		}

		if (espacos.contains("salario")) {
			labelErroSalario.setText(erros.get("salario"));
		}
		else {
			labelErroSalario.setText(erros.get(""));
		}
		
		if (espacos.contains("nascimento")) {
			labelErroNascimento.setText(erros.get("nascimento"));
		}
		else {
			labelErroNascimento.setText(erros.get(""));
		}
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
