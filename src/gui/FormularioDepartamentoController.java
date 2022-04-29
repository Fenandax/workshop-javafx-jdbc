package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.ServicoDepartamento;

public class FormularioDepartamentoController implements Initializable {

	private Department entidade;
	private ServicoDepartamento servico;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private Label labelErroNome;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setDepartamento(Department entidade) {
		this.entidade = entidade;
	}

	public void setServicoDepartamento(ServicoDepartamento servico) {
		this.servico = servico;
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
		} 
		catch (ValidationException e) {
			setMensagemErro(e.getErros());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getDadosFormulados() {
		Department obj = new Department();

		ValidationException exception = new ValidationException("Exceção de validação");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErros("nome", "O nome não pode ficar vazio");
		}
		obj.setName(txtNome.getText());

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
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}

	public void updateDadosFormulario() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula");
		}

		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getName());
	}

	private void setMensagemErro(Map<String, String> erros) {
		Set<String> espacos = erros.keySet();

		if (espacos.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}
	}
}
