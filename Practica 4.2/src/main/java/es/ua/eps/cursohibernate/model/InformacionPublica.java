package es.ua.eps.cursohibernate.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="INFORMACION_PUBLICA")
public class InformacionPublica implements Serializable{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@OneToOne
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario; 
    @Column(name="mostrar_email")
    private Boolean mostrar_email;
    @Column(name="mostrar_nacido")
    private Boolean mostrar_nacido;
    @Column(name="mostrar_nombre")
    private Boolean mostrar_nombre;
    

	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mostrar_email == null) ? 0 : mostrar_email.hashCode());
		result = prime * result + ((mostrar_nacido == null) ? 0 : mostrar_nacido.hashCode());
		result = prime * result + ((mostrar_nombre == null) ? 0 : mostrar_nombre.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InformacionPublica other = (InformacionPublica) obj;
		if (mostrar_email == null) {
			if (other.mostrar_email != null)
				return false;
		} else if (!mostrar_email.equals(other.mostrar_email))
			return false;
		if (mostrar_nacido == null) {
			if (other.mostrar_nacido != null)
				return false;
		} else if (!mostrar_nacido.equals(other.mostrar_nacido))
			return false;
		if (mostrar_nombre == null) {
			if (other.mostrar_nombre != null)
				return false;
		} else if (!mostrar_nombre.equals(other.mostrar_nombre))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}
	public Boolean getMostrar_email() {
		return mostrar_email;
	}
	public void setMostrar_email(Boolean mostrar_email) {
		this.mostrar_email = mostrar_email;
	}
	public Boolean getMostrar_nacido() {
		return mostrar_nacido;
	}
	public void setMostrar_nacido(Boolean mostrar_nacido) {
		this.mostrar_nacido = mostrar_nacido;
	}
	public Boolean getMostrar_nombre() {
		return mostrar_nombre;
	}
	public void setMostrar_nombre(Boolean mostrar_nombre) {
		this.mostrar_nombre = mostrar_nombre;
	}
	
}
