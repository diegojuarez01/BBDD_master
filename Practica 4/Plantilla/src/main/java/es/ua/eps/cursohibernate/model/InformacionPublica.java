package es.ua.eps.cursohibernate.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="informacion_publica")
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
