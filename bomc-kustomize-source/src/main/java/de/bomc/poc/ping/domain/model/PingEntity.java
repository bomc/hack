/**
 * Project: ping
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.ping.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.bomc.poc.ping.domain.model.basis.AbstractEntity;
import lombok.ToString;

//LOMBOK
@ToString
@Entity
@Table(name = "t_ping")//, schema = "bomcdb")
public class PingEntity extends AbstractEntity<PingEntity> implements Serializable {
	
	/**
	 * The serial uid
	 */
	private static final long serialVersionUID = 5856275651933429705L;
	
	/* ----------------------------- columns ----------------------------- */

	/**
	 * Unique identifier of this <code>UserEntity</code> (not nullable).
	 */
	@Column(name = "c_pong")
	private String pong = "pong";

	/* ----------------------------- constructors ------------------------ */

	/**
	 * Accessed by persistence provider.
	 */
	public PingEntity() {
		//
		// Accessed by the jpa provider.
	}
	
	/* ----------------------------- methods ----------------------------- */

	@Override
	protected Class<PingEntity> getEntityClass() {
		
		return PingEntity.class;
	}
	
	public String getPong() {
		
		return pong;
	}

	public void setPong(final String pong) {
		
		this.pong = pong;
	}
}


