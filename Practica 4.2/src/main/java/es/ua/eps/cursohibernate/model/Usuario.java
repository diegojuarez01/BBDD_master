package es.ua.eps.cursohibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name="USUARIO")
public class Usuario{
	
	 @Id
     @GeneratedValue(strategy=GenerationType.IDENTITY)
     @Column(name="id_usuario")
     private int UsuId;
     @Column(name="password")
     private byte[] password;
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
      
     @ManyToOne
     @JoinColumn(name="id_perfil", nullable=false)
     private Perfil perfil;
      
     @ManyToMany
     @JoinTable(name = "Sigue", joinColumns = { @JoinColumn(name = "id_seguido") }, inverseJoinColumns = { @JoinColumn(name = "id_seguidor") })
     private Set<Usuario> Siguea;

     @OneToMany(mappedBy="Usuario")
     private Set<Conexion> conexion;
     
     
     @OneToOne(mappedBy="usuario")
     private InformacionPublica informacionpublica;
     

	public Set<Usuario> getSiguea() {
		return Siguea;
	}

	public void setSiguea(Set<Usuario> siguea) {
		Siguea = siguea;
	}

	public Set<Conexion> getConexion() {
		return conexion;
	}

	public void setConexion(Set<Conexion> conexion) {
		this.conexion = conexion;
	}

	public InformacionPublica getInformacionpublica() {
		return informacionpublica;
	}

	public void setInformacionpublica(InformacionPublica informacionpublica) {
		this.informacionpublica = informacionpublica;
	}

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

 
	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
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

     public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

}
