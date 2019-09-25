/**
 * Project: hrm
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
package de.bomc.poc.hrm.domain.model.values;

/**
 * An ImageProvider stores an image file.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface ImageProvider {

	/**
	 * Return the image of the <code>UserEntity</code>.
	 * 
	 * @return The current image
	 */
	byte[] getImage();

	/**
	 * Change the image of the <code>UserEntity</code>.
	 * 
	 * @param img
	 *            The new image to set
	 */
	void setImage(byte[] img);
}
