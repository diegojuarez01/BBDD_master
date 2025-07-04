package es.ua.eps.cursohibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="PERFIL")
public class Perfil implements Serializable {

	 @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     @Column(name="id_perfil")
     private int id_perfil;
	 @Column(name="descripcion")
	 private String descripcion;
	 
	// @OneToMany(mappedBy="Perfil")
    // private Set<Usuario> usuariosset;
	
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

//	public Set<Usuario> getUsuariosset() {
	//	return usuariosset;
	//}

//	public void setUsuariosset(Set<Usuario> usuariosset) {
	//	this.usuariosset = usuariosset;
	//}

}
