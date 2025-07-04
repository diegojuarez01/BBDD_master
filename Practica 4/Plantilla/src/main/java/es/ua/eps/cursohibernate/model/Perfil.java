package es.ua.eps.cursohibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="Perfil")
public class Perfil {

	 @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     @Column(name="id_perfil")
     private int id_perfil;
	 @Column(name="descripcion")
	 private String descripcion;
	 
	 public int getId() {
		return id_perfil;
	}

	public void setId(short id_perfil) {
		this.id_perfil = id_perfil;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
