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
}
