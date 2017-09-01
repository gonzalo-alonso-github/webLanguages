
# Loqua

This is an EAR project made with Eclipse IDE. This is a web application which runs with Wildfly 8 Server and the following tools:
* JSF Framework (JSF 2.2 Specification) with Oracle Mojarra 2.2.8 implementation
* EJB Framework (EJB 3.0 Specification) (for business layer)
* Hibernate Framework to implement JPA 2.1 (for persistence layer)
* JAX-RS 2.0 with Resteasy 3.0 implementation

## ABOUT "loqua" PROJECT

The "loqua" project was created from Eclipse IDE as a single Maven project, downloading archetypes list from "http://repo.maven.apache.org/maven2/archetype-catalog.xml" and selecting "wildfly-javaee7-webapp-ear-blank-archetype".

## ABOUT "loqua-aggregator" PROJECT

### Allowing log4j from a .jar file:

The "loqua-aggregator" project was created with Eclipse as an "Application Client Project". It uses log4j so requires the log4j.xml file located at "appClientModule/resources". Its necessary to configure build path to allow log4j works when running the .jar file: open the "Build Path" dialog in Eclipse project ("Configure Build Path"), go into "Source" and add the folder "appClientModule/resources".

In case of ignoring that, log4j will not work when running the .jar file. It will only throw the warn "No appenders could be found for logger" due to cannot find log4j.xml file.

### Exporting "loqua-aggregator" to .jar:

Open the "Export" dialog in Eclipse project, select "Runnable JAR File".

### Launching "loqua-aggregator" .jar file:

Since "loqua-aggregator" is a REST client of the "loqua" web application, it will firstly be necessary to launch the web application, and then to run "loqua-aggregator" as the following example shows:
	sudo nohup java -jar [name_of_jar].jar &