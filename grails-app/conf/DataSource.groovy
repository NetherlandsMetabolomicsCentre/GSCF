dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
			//println "DATASOURCE DEBUG :: user.home = "+System.properties["user.home"]
			switch (System.properties["user.home"]) {
				case "/Users/adem/TURNEDOFFBYDEFAULT":
					// Development Postgres Database is turned off by default
					// if you do want to keep your data you can:
					// 	- reformate 'case' to your user.home (/Users/adem ?)
					//	- define development data in the BootStrap.groovy instead
					dbCreate = "update"
					username = "gscf"
					password = "dbnp"

					// PostgreSQL
					driverClassName = "org.postgresql.Driver"
					url = "jdbc:postgresql://localhost:5432/gscf"
					dialect = org.hibernate.dialect.PostgreSQLDialect
					break;
				default:
					// by default we use an in memory development database
					dbCreate = "create-drop" // one of 'create', 'create-drop','update'
					url = "jdbc:hsqldb:mem:devDB"
					//loggingSql = true
			   		break;
			}
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:mem:testDb"
		}
	}
	production {
		dataSource {
			/*
			 * when releasing a new stable to the live environment
			 * you would probably comment out the dbCreate option
			 * so hibernate won't try to update (which is does not
			 * do so well) and you update the live database yourself
			 *
			 * @see http://grails.org/plugin/autobase
			 * @see http://wiki.github.com/RobertFischer/autobase/example-usage
			 */
			dbCreate = "update"
			username = "gscf"
			password = "dbnp"

			// PostgreSQL
			driverClassName = "org.postgresql.Driver"
			url = "jdbc:postgresql://localhost:5432/gscf"
			dialect = org.hibernate.dialect.PostgreSQLDialect
			logSql = true	// enable logging while not yet final

			/* Apparently Hibernate performs two queries on inserting, one before
			 * to generate the unique id, and then the insert itself. In PostgreSQL
			 * > 8.2 this behaviour has changed, however hibernate has not implemented
			 * this change. In case we might optimize in the future, more info is here:
			 * http://blog.wolfman.com/articles/2009/11/11/using-postgresql-with-grails
			 * - Jeroen
			 */

			// MySQL
			//driverClassName = "com.mysql.jdbc.Driver"
			//url = "jdbc:mysql://localhost/gscf"
			//dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"

			//In memory
			//url = "jdbc:hsqldb:file:prodDb;shutdown=true"
		}
	}
}