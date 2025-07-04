package es.ua.eps.cursohibernate.querying;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import es.ua.eps.cursohibernate.model.Conexion;
import es.ua.eps.cursohibernate.model.Usuario;
import es.ua.eps.cursohibernate.util.HibernateUtil;


/*
Implementa una consulta en HQL y Criteria que borre todas las conexiones de usuarios con perfil Básico. 
Sugerencia: para probar esta consulta, puedes hacer un rollback de la transacción de borrado en vez de un commit.
De esta forma se puede lanzar la prueba más de una vez ya que los registros de la base de datos no serán alterados.
*/

public class Ejercicio3 {

	public static void main(String[] args) {
	
		int id_perfil = 0; //perfil basico
		int contador = 0;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();	
		session.beginTransaction();

		System.out.print("\n"); 
		System.out.print("\t" + "* CONSULTA EN HQL *"+ "\n\n");
		//Consulta en HQL:
		List<Usuario> usuarios = session.createQuery("select u.UsuId from Usuario u WHERE u.perfil.id_perfil = :id_perfil").setParameter( "id_perfil", id_perfil ).list();
		System.out.print("\n" + "Borrar conexion de usuario con perfil Básico:" + "\n\n");
		//Para cada usuario con perfil basico borrarmos sus conexiones.
		for(int i=0;i<usuarios.size();i++) {
			//int affectedRows= session.createQuery("delete FROM Conexion c WHERE c.Usuario = :id_usuario").setParameter("id_usuario",id_usuario).executeUpdate();
			contador = session.createQuery("delete FROM Conexion c WHERE c.Usuario.UsuId = :id_usuario").setParameter("id_usuario",usuarios.get(i)).executeUpdate();
			System.out.print("Numero de registros borrados: " + contador  + "\n");
		}
		
		session.getTransaction().rollback();
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		System.out.print("\n"); 
		System.out.print("\t" + "* CONSULTA EN CRITERIA *"+ "\n\n");
		//Consulta en CRITERIA:
		contador = 0;
		//builder.createCriteriaDelete();
		for(int i=0;i<usuarios.size();i++) {		
			CriteriaBuilder builder = session.getCriteriaBuilder();	
			CriteriaDelete<Conexion> criteria = builder.createCriteriaDelete( Conexion.class) ;
			Root<Conexion> root = criteria.from( Conexion.class );
			criteria.where( builder.equal( root.get( "Usuario" ), usuarios.get(i) ) );
			session.createQuery( criteria ).executeUpdate();
			contador++;
		}
		
		session.getTransaction().rollback();
		HibernateUtil.getSessionFactory().close();
	
	}
}