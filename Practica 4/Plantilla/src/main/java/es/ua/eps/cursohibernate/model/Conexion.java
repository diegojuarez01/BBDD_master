package es.ua.eps.cursohibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="Conexion")
public class Conexion implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	 @Id
     @Column(name="momento_entrada")
     private Date Entra;
	 
	 @ManyToOne
     @JoinColumn(name="id_usuario", nullable=false)
     private Usuario Usuario;
	 
	public Usuario getUsuario() {
		return Usuario;
	}

	public void setUsuario(Usuario usuario) {
		Usuario = usuario;
	}

	public Date getEntra() {
		return Entra;
	}

	public void setEntra(Date entra) {
		Entra = entra;
	}

}
