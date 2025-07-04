package es.ua.eps.cursohibernate.querying;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import es.ua.eps.cursohibernate.model.Conexion;
import es.ua.eps.cursohibernate.model.Usuario;
import es.ua.eps.cursohibernate.util.HibernateUtil;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;


public class usuarios10_20 {

	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();	
		session.beginTransaction();
	
	//List<Usuario> usuario_list = session.createQuery("select u from Usuario u where u.UsuId BETWEEN 10 AND 20" ).list();

	List<Usuario> usuario_list = session.createQuery("select u from Usuario u LEFT JOIN FETCH u.conexion LEFT JOIN FETCH u.perfil where u.UsuId BETWEEN 10 AND 20").list();
	for (Usuario u: usuario_list) {
        for (Conexion c: u.getConexion()) {
                c.getEntra();
                System.out.print(c.getEntra() + "\n");
        }
	}
	

}
}
