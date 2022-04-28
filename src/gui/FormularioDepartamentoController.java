package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.ServicoDepartamento;

public class FormularioDepartamentoController implements Initializable {

	private Department entidade;
	private ServicoDepartamento servico;

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

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (entidade == null) {
			throw new IllegalStateException("Estidade está nula");
		}
		if (servico == null) {
			throw new IllegalStateException("Serviço está nulo");
		}
		try {
			entidade = getDadosFormulados();
			servico.salveOuUpdate(entidade);
			Utils.estagioAtual(evento).close();
		}
		catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private Department getDadosFormulados() {
		Department obj = new Department();

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtId.getText());
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent evento) {
		Utils.estagioAtual(evento).close();
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

}
