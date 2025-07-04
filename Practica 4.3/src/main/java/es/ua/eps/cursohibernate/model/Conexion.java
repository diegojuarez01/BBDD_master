package es.ua.eps.cursohibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import java.io.Serializable;
import java.util.Date;


//1* namequeries --- la lista de fechas y horas en que se ha conectado el usuario con ID 99 
//2* namequeries --- todas las fechas de conexiones de usuarios con perfil Premium
//3* namequeries --- borre todas las conexiones de usuarios con perfil Básico




@Entity
@Table(name="CONEXION")


	@NamedNativeQueries({
	    @NamedNativeQuery(
	    name = "getFechasConexionesId",
	    query = "select momento_entrada,id_usuario from conexion where id_usuario= :usuario_id ",
	    resultClass=Conexion.class
	    ), 
	    @NamedNativeQuery(
	    name = "getFechasConexionesPerfilPremium",
	    query = "select * from conexion c join usuario u ON c.id_usuario = u.id_usuario WHERE u.id_perfil = :id_perfil ",
	    resultClass=Conexion.class
	    )   
	})
	
	@NamedQueries({
		@NamedQuery(
		name = "deleteConexionesPerfilBasico",
		query = "delete FROM Conexion c WHERE c.Usuario.UsuId = :id_usuario"
		),
		@NamedQuery(
		name = "getUsuariosPerfilBasico",
		query = "select u.UsuId from Usuario u WHERE u.perfil.id_perfil = :id_perfil"
		),
		
		})


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

	public Date getEntra() {
		return Entra;
	}

	public void setEntra(Date entra) {
		this.Entra = entra;
	}

	public Usuario getUsuario() {
		return Usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.Usuario = usuario;
	}
	 	
}

