package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class ServicoDepartamento {

	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> encontrarTodos() {
		return dao.findAll();
	}
	
	public void salveOuUpdate(Department obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
}
