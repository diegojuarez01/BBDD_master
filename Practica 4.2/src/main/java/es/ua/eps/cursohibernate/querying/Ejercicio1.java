package es.ua.eps.cursohibernate.querying;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import es.ua.eps.cursohibernate.model.Conexion;
import es.ua.eps.cursohibernate.model.Usuario;
import es.ua.eps.cursohibernate.util.HibernateUtil;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;



/*

	Implementad una clase “Ejercicio1.java” que, dentro de una misma transacción, 
	obtenga la lista de fechas y horas en que se ha conectado el usuario con ID 99 
	(comprueba que tienes un usuario con esa clave; si no es así, prueba con cualquier 
	otra clave de un usuario que sí que haya hecho conexiones) de siete maneras diferentes.

*/

public class Ejercicio1 {

	public static void main(String[] args) {
		
		int contador = 0;
		int usuario_id = 99;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();	
		session.beginTransaction();
		
		System.out.print("\n");
		System.out.print("\t" + "* CONSULTAS NATIVAS *"+ "\n\n");
		//Con un get sobre la entidad Usuario
					//List<Usuario> usuario = session.createNativeQuery("SELECT * FROM usuario where id_usuario = :usuario_id", Usuario.class ).setParameter("usuario_id", usuario_id).list();
		Usuario usuario = session.get(Usuario.class, usuario_id);
		Set<Conexion> ConexionGet = usuario.getConexion();
		
		
		System.out.print("\n" + "Lista fecha con un get entidad Usuario:" + "\n\n");
		System.out.print(ConexionGet + "\n");
		
		
		//Con una consulta nativa que devuelva:
		//la lista de fechas directamente, ordenadas de forma descendente.
		List<Conexion> conexion = session.createNativeQuery("select momento_entrada,id_usuario from conexion where id_usuario= :usuario_id order by momento_entrada desc",Conexion.class)
				.setParameter("usuario_id", usuario_id).list(); 	//si es null coger idrandom y checkear si es null hasta que no lo sea.
		if(conexion.isEmpty()) {
			do {
				usuario_id =  (int) (Math.random()*1000);
				System.out.print(usuario_id);
				conexion = session.createNativeQuery("select momento_entrada,id_usuario from conexion where id_usuario= :usuario_id order by momento_entrada desc",Conexion.class)
						.setParameter("usuario_id", usuario_id).list(); 	//si es null coger idrandom y checkear si es null hasta que no lo sea.
			}while(conexion.isEmpty());
								}
		

			System.out.print("\n" + "Lista fechas ordenadas de forma descendente:" + "\n\n");
			for(int i=0;i<conexion.size();i++) {
			System.out.print(conexion.get(i).getEntra() + "\n");
			
		}
			System.out.print("\n");
		//la lista objetos Conexion para extraer, de cada uno de ellos, las fechas de conexión, cuya fecha esté entre el 1 de enero de 1965 y el 31 de diciembre de 2019.  
		conexion = session.createNativeQuery("select momento_entrada,id_usuario from conexion where id_usuario= :usuario_id AND momento_entrada BETWEEN '1965-01-01' AND '2019-12-31'",Conexion.class)
					.setParameter("usuario_id", usuario_id).list(); 	//si es null coger idrandom y checkear si es null hasta que no lo sea.
		

		System.out.print("\n" + "Lista fechas fecha entre el 1 de enero de 1965 y el 31 de diciembre de 2019:" + "\n\n");
		for(int i=0;i<conexion.size();i++) {
		System.out.print(conexion.get(i).getEntra() + "\n");
		}
		
		
		System.out.print("\n");
		System.out.print("\t" + "* CONSULTAS HQL *"+ "\n\n");
		//Con una consulta HQL que devuelva:
		
		//la lista de fechas directamente, ordenadas de forma descendente.
			
		//org.hibernate.query.Query query = session.createQuery("select Entra from Conexion where Usuario = :usuario_id order by Entra desc" ).setParameter("usuario_id", usuario_id);
		conexion= session.createNativeQuery("SELECT momento_entrada FROM Conexion c JOIN Usuario u ON u.id_usuario = c.id_usuario WHERE c.id_usuario = :usuario_id order by momento_entrada desc", Conexion.class ).setParameter( "usuario_id", usuario_id ).list();
		System.out.print("\n" + "Lista fechas directamente, ordenadas de forma descendente:" + "\n\n");
		for(int i=0;i<conexion.size();i++) {
			System.out.print(conexion.get(i).getEntra() + "\n");
			}
		
		//la lista objetos Conexion para extraer, de cada uno de ellos, las fechas de conexión, cuya fecha sea mayor que el 1 de febrero de 1966.
		conexion = session.createQuery("Select c.Entra from Conexion c WHERE c.Usuario.UsuId = :usuario_id AND c.Entra >= '1966-02-01'").setParameter("usuario_id", usuario_id).list();
		System.out.print("\n" + "Lista fechas mayor que el 1 de febrero de 1966:" + "\n\n");
		for(int i=0;i<conexion.size();i++) {
			System.out.print(conexion.get(i) + "\n");
			}

		System.out.print("\n"); 
		System.out.print("\t" + "* CONSULTAS CRITERIA *"+ "\n\n");	
		//Con una consulta utilizando la API Criteria que devuelva:
		CriteriaBuilder builder = session.getCriteriaBuilder();
		//la lista de tuplas que contenga las fechas de conexión (consulta no tipada)
		
		
		CriteriaQuery<Tuple> criteria = builder.createQuery( Tuple.class );
		Root<Conexion> rootTuple = criteria.from( Conexion.class );
		Path<Date> fechaConexion = rootTuple.get("Entra");
		criteria.multiselect( fechaConexion);
		criteria.where(builder.equal( rootTuple.get( "Usuario" ), usuario_id ));
		List<Tuple> tuples = session.createQuery( criteria ).list();
		
		System.out.print("\n" + "Lista tuplas que contenga las fechas de conexión (NO TIPADA):" + "\n\n");
		for(Tuple tuple : tuples) {
			System.out.print(tuple.get(0) + "\n");
			}
		System.out.print("\n");

		//la lista objetos Conexion para extraer, de cada uno de ellos, las fechas de conexión (consulta tipada)			
		
		CriteriaQuery<Conexion> criteriaConexion = builder.createQuery( Conexion.class );
		Root<Conexion> root = criteriaConexion.from( Conexion.class );
		criteriaConexion.select( root );
		criteriaConexion.where( builder.equal( root.get( "Usuario" ), usuario_id ) );
		conexion = session.createQuery( criteriaConexion ).list();
		System.out.print("\n" + "Lista objetos Conexion extraer fechas de cada uno (TIPADA):" + "\n\n");
	
		//Otra forma de recorrer el objeto
		for(Conexion c : conexion) {			
			System.out.print(conexion.get(contador).getEntra() + "\n");
			contador++;
			}
		
		session.getTransaction().commit();
		HibernateUtil.getSessionFactory().close();

	}
}