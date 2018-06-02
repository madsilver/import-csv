package br.com.silver.model;

public class Client {

	private int id;
	private String name;
	private String vc;
	private String cpf;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVc() {
		return vc;
	}
	public void setVc(String vc) {
		this.vc = vc;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
}
