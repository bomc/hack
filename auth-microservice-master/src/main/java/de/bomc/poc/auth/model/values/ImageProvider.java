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
package de.bomc.poc.auth.model.values;

/**
 * An ImageProvider stores an image file.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface ImageProvider {

	/**
	 * Return the image of the <code>User</code>.
	 * 
	 * @return The current image
	 */
	byte[] getImage();

	/**
	 * Change the image of the <code>User</code>.
	 * 
	 * @param img
	 *            The new image to set
	 */
	void setImage(byte[] img);
}
