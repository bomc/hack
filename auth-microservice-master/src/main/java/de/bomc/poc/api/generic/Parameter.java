/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.api.generic;

import de.bomc.poc.api.generic.types.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * <pre>
 * Diese Klasse funktioniert als Datencontainer und besteht aus einem String-Wert, der einem Key entspricht und einem {@link Type} Objekt.
 * Das Attribut 'name' soll dem Attributnamen entsprechen unter dem das Objekt in der darunterliegenden Schicht weiter verarbeitet werden soll.
 * Um eine Typsicherheit zu erreichen, gibt es das {@link Type} Objekt. Type besteht aus dem Wert und dem dazugehörenden Datentypen.
 * Beispiel:
 *
 *     Person.persLaufnummer - entspricht der Id
 *
 *     Parameter(final String name, final Type type)
 *
 *     Parameter("persLaufnummer", new LongType(1234L));
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter implements Serializable {

	private static final long serialVersionUID = 90940540485965013L;
	// Attributname unter dem das Objekt in der darunterliegenden Schicht weiter verarbeitet werden soll.
    @XmlAttribute
    @NotBlank
    private String name;
    // Definiert den Typ des Parameters und den Wert.
    @XmlElement
    @NotNull
    private Type<?> type;

    protected Parameter() {
        //
        // Wird von jaxb benötigt.
    }

    /**
     * Erzeugt ein Parameter-Objekt.
     * @param name Attributname unter dem das Objekt in der darunterliegenden Schicht weiter verarbeitet werden soll.
     * @param type Definiert den Typ des Parameters und den Wert.
     */
    public Parameter(final String name, final Type<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Type<?> getType() {
        return this.type;
    }

    public Object getValue() {
        return this.type.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parameter)) {
            return false;
        }

        final Parameter that = (Parameter)o;

        if (!this.name.equals(that.name)) {
            return false;
        }
        return this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter [name=" + this.name + ", type=" + this.type + "]";
    }
}
