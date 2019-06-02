package de.bomc.poc.model.mock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.bomc.poc.model.AbstractEntity;

import java.io.Serializable;

/**
 * Subject under test.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@Entity
@Table(name = "T_SUT")
public class Sut extends AbstractEntity<Sut> implements Serializable {

    /**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 1208831927181001819L;
	@Column
    private String attributeA;
    @Column
    private String attributeB;
    @Column
    private String attributeC;

    public Sut() {
        // Indicates a Bean.
    }

    public Sut(final String attributeA) {
        this.attributeA = attributeA;
    }

    public Sut(final String attributeA, final String attributeB, final String attributeC) {
        this.attributeA = attributeA;
        this.attributeB = attributeB;
        this.attributeC = attributeC;
    }

	
    @Override
    protected Class<Sut> getEntityClass() {
        return Sut.class;
    }
    
    public String getAttributeA() {
        return this.attributeA;
    }

    public void setAttributeA(final String attributeA) {
        this.attributeA = attributeA;
    }

    public String getAttributeB() {
        return this.attributeB;
    }

    public void setAttributeB(final String attributeB) {
        this.attributeB = attributeB;
    }

    public String getAttributeC() {
        return this.attributeC;
    }

    public void setAttributeC(final String attributeC) {
        this.attributeC = attributeC;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sut)) {
            return false;
        }

        final Sut sut = (Sut)o;

        if (this.getAttributeA() != null ?
            !this.getAttributeA()
                 .equals(sut.getAttributeA()) :
            sut.getAttributeA() != null) {
            return false;
        }
        if (this.getAttributeB() != null ?
            !this.getAttributeB()
                 .equals(sut.getAttributeB()) :
            sut.getAttributeB() != null) {
            return false;
        }
        return !(this.getAttributeC() != null ?
                 !this.getAttributeC()
                      .equals(sut.getAttributeC()) :
                 sut.getAttributeC() != null);
    }

    @Override
    public int hashCode() {
        int
            result =
            this.getAttributeA() != null ?
            this.getAttributeA()
                .hashCode() :
            0;
        result =
            31 * result + (this.getAttributeB() != null ?
                           this.getAttributeB()
                               .hashCode() :
                           0);
        result =
            31 * result + (this.getAttributeC() != null ?
                           this.getAttributeC()
                               .hashCode() :
                           0);
        return result;
    }

    @Override
    public String toString() {
        return "Sut{" +
               "attributeA='" + this.attributeA + '\'' +
               ", attributeB='" + this.attributeB + '\'' +
               ", attributeC='" + this.attributeC + '\'' +
               '}';
    }
} // end Sut
