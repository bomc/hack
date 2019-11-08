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
package de.bomc.poc.hrm.config.git;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration for expose git properties.
 * The properties are read from 'git.properties'.
 * The file is generated during build process by the 'git-commit-id-plugin'.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
// LOMBOK
@Getter
@Setter
// SPRING
@Configuration
@Profile({"local", "dev", "prod"})
@PropertySource("classpath:git.properties")
public class GitConfig {

	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Value("${git.commit.id:Reading from file failed!}")
	private String commitId;
	
	@Value("${git.commit.message.full:Reading from file failed!}")
	private String commitMessage;

	@Value("${git.build.version:Reading from file failed!}")
	private String version;

}
