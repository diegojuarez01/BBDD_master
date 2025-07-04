package es.ua.eps.cursohibernate.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;

@Entity
@Table(name="PERFIL")
@Cacheable

@Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY, region="Perfil")
public class Perfil implements Serializable {

	 /**
	 * 
	 */
	 private static final long serialVersionUID = 1L;
	
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
