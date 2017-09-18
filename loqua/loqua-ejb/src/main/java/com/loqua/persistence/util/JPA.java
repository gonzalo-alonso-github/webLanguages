package com.loqua.persistence.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

/**
 * Clase de utilidad que permite manejar al EntityManager para efectuar
 * operaciones 'CRUD' sobre la base de datos
 * @author Gonzalo
 */
public class JPA {
	
	/**
	 * Representa al EntityManager, objeto que facilita la comunicación del
	 * negocio con la persistencia.
	 * Cada distinto hilo de ejecucion que utilice esta clase tiene su propia
	 * variable EntityManager 'emThread' que, al ser estatica,
	 * es identica para todos ellos. Evitando que exista un unico EntityManager
	 * para todos los hilos se consigue evitar el acceso
	 * sincrono a un solo EntityManager, es decir, que cada hilo tuviera que
	 * esperar a que el unico EntityManager estuviera desocupado para poder
	 * utilizarlo 
	 */
	private static ThreadLocal<EntityManager> emThread = 
			new ThreadLocal<EntityManager>();
	
	/**
	 * Inicializa, en caso de que sea null, el EntityManager de esta clase
	 * @return el atributo estatico EntityManager
	 */
	public static EntityManager getManager() {
		EntityManager entityManager = emThread.get();
		if (entityManager == null) {
			entityManager = jndiFind("java:/LoquaJpaEntityManager");
			emThread.set(entityManager);
		}
		return entityManager;
	}

	/**
	 * Busca en el registro JNDI el nombre del datasource dado (el cual indica
	 * las propiedades de conexion a la base de datos), 
	 * y genera un EntityManager a partir de su
	 * persistence-unit (set de todas las entidades del modelo)
	 * @param name nombre (definido en el fichero 'persistence.xml')
	 * del datasource (cuyas propiedades se especifican en el fichero
	 * 'loqua-ds.xml') en el registro JNDI
	 * @return instancia del EntityManager que se genera
	 */
	private static EntityManager jndiFind(String name) {
		Context ctx;
		try {
			ctx = new InitialContext();
			return (EntityManager) ctx.lookup(name);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}