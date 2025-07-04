package es.ua.eps.cursohibernate.querying;

import java.util.Date;
import java.util.List;


import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

import es.ua.eps.cursohibernate.model.Conexion;
import es.ua.eps.cursohibernate.model.Usuario;
import es.ua.eps.cursohibernate.util.HibernateUtil;


/*

Implementa una consulta en SQL, una en HQL 
y otra con Criteria que devuelvan todas las fechas de conexiones de usuarios con perfil Premium

*/
public class Ejercicio2 {

	public static void main(String[] args) {
			
		int id_perfil = 2; // Perfil premium
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();	
		session.beginTransaction();
		
		System.out.print("\n");
		System.out.print("\t" + "* CONSULTA EN SQL *"+ "\n\n");
	
		//Consulta en SQL:
		List<Conexion> conexion = session.createNativeQuery("select * from conexion c join usuario u ON c.id_usuario = u.id_usuario WHERE u.id_perfil = :id_perfil",Conexion.class)
				.setParameter( "id_perfil", id_perfil ).list(); 	

		System.out.print("\n" + "Conexiones de usuarios con perfil Premium:" + "\n\n");
		for(int i=0;i<conexion.size();i++) {
			System.out.print(i + ") " + conexion.get(i).getEntra() + "\n");		
		}
		
		System.out.print("\n");
		System.out.print("\t" + "* CONSULTA EN HQL *"+ "\n\n");
	
		//Consulta en HQL:
		conexion = session.createQuery("select c.Entra from Conexion c WHERE c.Usuario.perfil.id_perfil = :id_perfil").setParameter( "id_perfil", id_perfil ).list();
		System.out.print("\n" + "Conexiones de usuarios con perfil Premium:" + "\n\n");
		for(int i=0;i<conexion.size();i++) {
			System.out.print(i + ") " + conexion.get(i) + "\n");
		}

		System.out.print("\n"); 
		System.out.print("\t" + "* CONSULTA EN CRITERIA *"+ "\n\n");
		
		//Consulta en Criteria:
		CriteriaBuilder builder = session.getCriteriaBuilder();	
		CriteriaQuery<Tuple> criteria = builder.createQuery( Tuple.class );
		Root<Usuario> root = criteria.from( Usuario.class );
		Join<Usuario, Conexion> join_usuario_conexion = root.join("conexion", JoinType.INNER);	
		Path<Date> fecha = join_usuario_conexion.get( "Entra" );
		criteria.multiselect( fecha );
		criteria.where( builder.equal( root.get( "perfil" ), id_perfil ) );
		List<Tuple> tuples = session.createQuery( criteria ).list();

		System.out.print("\n" + "Conexiones de usuarios con perfil Premium:" + "\n\n");
		for(Tuple tuple : tuples) {
			System.out.print(tuple.get(0) + "\n");
		}
		System.out.print("\n");

		session.getTransaction().commit();
		HibernateUtil.getSessionFactory().close();

	}
}