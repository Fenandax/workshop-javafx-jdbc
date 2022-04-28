package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class ServicoDepartamento {

	public List<Departamento> encontrarTodos() {
		List<Departamento> lista = new ArrayList<>();
		lista.add(new Departamento(1, "Livros"));
		lista.add(new Departamento(2, "Computadores"));
		lista.add(new Departamento(3, "Eletrônicos"));
		return lista;
	}
}
