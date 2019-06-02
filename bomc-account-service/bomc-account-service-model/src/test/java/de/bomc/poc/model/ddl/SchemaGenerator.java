package de.bomc.poc.model.ddl;

import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;

import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that helps generating the database schema.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public class SchemaGenerator {

    private static final String RELATIVE_BASE_PATH = ".\\src\\test\\java\\de\\bomc\\poc\\model\\ddl\\";
    private static final String PU_NAME = "bomc-PU";
    
    public static void main(final String[] args) {
        try {
            toConsole();
            toFile();

            System.exit(0);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void toConsole() {
        final Map<String, Object> properties = new HashMap<>();

        final StringWriter create = new StringWriter();
        final StringWriter drop = new StringWriter();

        properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        properties.put("javax.persistence.schema-generation.scripts.create-target", create);
        properties.put("javax.persistence.schema-generation.scripts.drop-target", drop);

        Persistence.generateSchema(PU_NAME, properties);

        System.out.println("Create script:");
        pretty(create.toString());

        System.out.println("Drop script:");
        pretty(drop.toString());
    }

    private static void toFile() {
        final Map<String, Object> properties = new HashMap<>();

        properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        properties.put("javax.persistence.schema-generation.scripts.create-target", RELATIVE_BASE_PATH + "create.sql");
        properties.put("javax.persistence.schema-generation.scripts.drop-target", RELATIVE_BASE_PATH + "drop.sql");

        Persistence.generateSchema(PU_NAME, properties);
    }

    private static void pretty(final String unformatted) {
        final StringReader lowLevel = new StringReader(unformatted);
        final BufferedReader highLevel = new BufferedReader(lowLevel);
        final DDLFormatterImpl formatter = new DDLFormatterImpl();

        highLevel.lines()
                 .forEach(x -> {
                     final String formatted = formatter.format(x + ";");
                     System.out.println(formatted);
                 });
    }
}
