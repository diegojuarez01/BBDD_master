package es.ua.eps.cursohibernate.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="PERFIL")
public class Perfil {

	 @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     @Column(name="id_perfil")
     private int id_perfil;
	 @Column(name="descripcion")
	 private String descripcion;
	 
     @OneToMany(mappedBy="perfil")
     private Set<Usuario> usuario;
     
	
	public Set<Usuario> getUsuario() {
		return usuario;
	}

	public void setUsuario(Set<Usuario> usuario) {
		this.usuario = usuario;
	}

	public int getId() {
		return id_perfil;
	}

	public void setId(int id_perfil) {
		this.id_perfil = id_perfil;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
