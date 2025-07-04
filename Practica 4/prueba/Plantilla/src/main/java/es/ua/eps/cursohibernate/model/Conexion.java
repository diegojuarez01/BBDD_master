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
@Table(name="CONEXION")
public class Conexion implements Serializable {
	
	 @Id
	 @ManyToOne
     @JoinColumn(name="id_usuario", nullable=false)
     private Usuario Usuario;
	
     @Column(name="momento_entrada")
     private Date Entra;

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
	   public boolean equals(Object other) {

	        return true;
	    }

	    public int hashCode() {
	        int result=0;
	
	        return result;
	    }
     
}
