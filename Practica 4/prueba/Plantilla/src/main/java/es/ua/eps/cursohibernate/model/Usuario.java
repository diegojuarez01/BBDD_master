package es.ua.eps.cursohibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import java.util.Date;
import java.util.Set;
import java.io.Serializable;

@Entity
@Table(name="Usuario")
public class Usuario {
	
	 @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     @Column(name="id_usuario")
     private int UsuId;
     @Column(name="password")
     private Serializable password;
     @Column(name="apodo")
     private String apodo;
     @Column(name="email")
     private String email;
     @Column(name="nombre")
     private String nombre;
     @Column(name="apellidos")
     private String apellidos;
     @Column(name="nacido")
     private Date nacido;
   
     //@OneToOne(mappedBy="InformacionPublica")
     
     @OneToOne
     @JoinColumn(name="id_usuario", nullable=false)
     private InformacionPublica informacionPublica;
     @ManyToOne
     @JoinColumn(name="id_perfil", nullable=false)
     private Perfil perfil;
	 @OneToMany(mappedBy="Usuario")
	 
	// @ManyToMany
     //@JoinTable(name = "Sigue",
     //joinColumns = { @JoinColumn(name = "id_seguido") },
     //inverseJoinColumns = { @JoinColumn(name = "id_seguidor") })
     //private Set<Usuario> Siguea;

     

  //   public Set<Usuario> getSiguea() {
	//	return Siguea;
//	}

//	public void setSiguea(Set<Usuario> siguea) {
	//	Siguea = siguea;
	//}

	public int getUsuId() {
		return UsuId;
	}

	public void setUsuId(int usuId) {
		UsuId = usuId;
	}

	public String getNombre() {
             return nombre;
     }

     public void setNombre(String nombre) {
             this.nombre = nombre;
     }

     public String getApellidos() {
             return apellidos;
     }

     public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
     }

     public Serializable getPassword() {
		return password;
	}

	public void setPassword(Serializable password) {
		this.password = password;
	}

	public String getApodo() {
		return apodo;
	}

	public void setApodo(String apodo) {
		this.apodo = apodo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getNacido() {
		return nacido;
	}

	public void setNacido(Date nacido) {
		this.nacido = nacido;
	}

	public InformacionPublica getInformacionPublica() {
             return informacionPublica;
     }

     public void setInformacionPublica(InformacionPublica informacion_publica) {
             this.informacionPublica = informacion_publica;
     }        
     
     public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	//private Set<Conexion> conexionesset;
	 
	// public Set<Conexion> getConexionesset() {
		//return conexionesset;
	//}

//	public void setConexionesset(Set<Conexion> conexionesset) {
	//	this.conexionesset = conexionesset;
	//}

}
