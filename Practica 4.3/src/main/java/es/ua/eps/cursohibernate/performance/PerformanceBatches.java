package es.ua.eps.cursohibernate.performance;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.hibernate.Session;

import es.ua.eps.cursohibernate.model.InformacionPublica;
import es.ua.eps.cursohibernate.model.Perfil;
import es.ua.eps.cursohibernate.model.Usuario;
import es.ua.eps.cursohibernate.util.HibernateUtil;

public class PerformanceBatches{

	public static void main(String[] args) {

		NameGenerator generator = new NameGenerator();
		List<Name> names = generator.generateNames( 1000 );
		Random rnd = new Random();
		
		//Get Session
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		System.out.println("Session factory creada");
		List<Usuario> createdUserIds = new LinkedList<Usuario>();
		
		//Adding 1000 users
		session.beginTransaction();
		for(Name n: names) {
			Usuario user=new Usuario();
			user.setNombre(n.getFirstName());
			user.setApellidos(n.getLastName());
			user.setEmail(n.getFirstName().toLowerCase()+"."+n.getLastName().toLowerCase()+"@dlsi.ua.es");
			user.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())).getBytes());
			user.setApodo(null);
			int perfil_id = rnd.nextInt(3);
			Perfil perfil = session.get(Perfil.class, perfil_id);
			user.setPerfil(perfil);
			user.setNacido(new Date(-946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000))));
			session.persist(user);
			createdUserIds.add(user);
		}

		session.getTransaction().commit();
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		for(Usuario curUser: createdUserIds) {
			InformacionPublica info=new InformacionPublica();
			info.setUsuario(curUser);
			info.setMostrar_email(Math.random() < 0.5);
			info.setMostrar_nacido(Math.random() < 0.5);
			info.setMostrar_nombre(Math.random() < 0.5);
			session.save(info);
			session.delete(info);
		}
		
		session.getTransaction().commit();
		//terminate session factory, otherwise program won't end
		HibernateUtil.getSessionFactory().close();
	}
}