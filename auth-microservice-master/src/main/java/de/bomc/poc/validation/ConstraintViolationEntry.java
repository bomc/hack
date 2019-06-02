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
package de.bomc.poc.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Exposes the validation error to the client.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConstraintViolationEntry implements Serializable {

    /**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 8276757835723675L;
	private String fieldName;
    private String wrongValue;
    private String errorMessage;

    public ConstraintViolationEntry() {
        //
    }

    public ConstraintViolationEntry(ConstraintViolation<?> violation) {
        final Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();
        final Path.Node currentNode = iterator.next();
        String invalidValue = "";

        if (violation.getInvalidValue() != null) {
            invalidValue = violation.getInvalidValue().toString();
        }

        this.fieldName = currentNode.getName();
        this.wrongValue = invalidValue;
        this.errorMessage = violation.getMessage();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getWrongValue() {
        return this.wrongValue;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String toString() {
        return "ConstraintViolationEntry [fieldName=" + fieldName + ", wrongValue=" + wrongValue + ", errorMessage=" + errorMessage + "]";
    }
}
