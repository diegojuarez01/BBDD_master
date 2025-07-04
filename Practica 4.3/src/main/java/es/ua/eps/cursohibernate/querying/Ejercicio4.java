package es.ua.eps.cursohibernate.querying;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import es.ua.eps.cursohibernate.model.Conexion;
import es.ua.eps.cursohibernate.model.Usuario;
import es.ua.eps.cursohibernate.util.HibernateUtil;


/*
Implementa las 3 consultas anteriores como named queries (dentro de Conexion.java) 
y llámalas desde la clase main correspondiente a este ejercicio.

*/

public class Ejercicio4 {
	
	@PersistenceContext
	private EntityManager manager;
	
	public static void main(String[] args) {


				Session session = HibernateUtil.getSessionFactory().getCurrentSession();	
				session.beginTransaction(); 
				int usuario_id = 99; //Valor por defecto.
				
				//Guardamos la primeraQuery
	            TypedQuery query = session.getNamedQuery("getFechasConexionesId");    
	            query.setParameter("usuario_id",usuario_id);   
	                    
	            List<Conexion> conexiones= query.getResultList();   
	            
	        	System.out.print("\n" + "Lista fechas de conexion para el usuario: " + usuario_id + "\n\n");
				for(int i=0;i<conexiones.size();i++) {
				System.out.print(i+1 +") " + conexiones.get(i).getEntra() + "\n");	
				}
				System.out.print("\n");
				
				//Guardamos la segundaQuery
	            query = session.getNamedQuery("getFechasConexionesPerfilPremium");    
	            query.setParameter("id_perfil",2);    // Perfil premium id_perfil = 2
	            
	            conexiones= query.getResultList();  
	            
	        	System.out.print("\n" + "Lista fechas de conexion usuarios premium:" + "\n\n");
				for(int i=0;i<conexiones.size();i++) {
				System.out.print(i+1 +") " + conexiones.get(i).getEntra() + "\n");	
				}
				System.out.print("\n");
					
				//Guardamos la lista de usuario con perfil basico desde otra namedquery para luego recorrerla.
				
				query = session.getNamedQuery("getUsuariosPerfilBasico");
				query.setParameter("id_perfil", 0);  // Perfil basico id_perfil = 0
				List<Usuario> usuarios = query.getResultList();
				
	        	System.out.print("\n" + "Borrar conexion de usuario con perfil Básico:"  + "\n\n");
	    		//Para cada usuario con perfil basico borrarmos sus conexiones.
	    		for(int i=0;i<usuarios.size();i++) {
	    			//Guardamos la terceraQuery
		           query = session.getNamedQuery("deleteConexionesPerfilBasico");    
		           query.setParameter("id_usuario",usuarios.get(i)).executeUpdate();
		           // conexiones= query.getResultList();  
	    		}
	    		
	    		session.getTransaction().commit(); //session.getTransaction().rollback();
	    		HibernateUtil.getSessionFactory().close();
		}
}


