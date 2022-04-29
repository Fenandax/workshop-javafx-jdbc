package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class ServicoVendedor {

	private SellerDao dao = DaoFactory.createSellerDao();

	public List<Seller> encontrarTodos() {
		return dao.findAll();
	}
	
	public void salveOuUpdate(Seller obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remover(Seller obj) {
		dao.deleteById(obj.getId());
	}
}
