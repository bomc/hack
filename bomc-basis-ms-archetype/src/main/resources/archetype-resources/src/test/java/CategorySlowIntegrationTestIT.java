#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package};

/**
 * A category marker interface for integration tests.
 * <p>
 * <pre>
 *
 * We can now select which categories we want to run on the command line by passing the groups parameter to the Maven executable. Examples:
 *
 *  mvn test -Dgroups="${package}.CategoryIntegrationTestIT"
 *  mvn test -Dgroups="${package}.CategoryIntegrationTestIT,${package}.CategoryFastUnitTest"
 *
 *  Alternatively, we can set up the category directly in the pom.xml file
 *
 *  &lt;plugin&lt;
 *      &lt;groupId&lt;org.apache.maven.plugins&lt;/groupId&lt;
 *      &lt;artifactId&lt;maven-surefire-plugin&lt;/artifactId&lt;
 *          &lt;configuration&lt;
 *              &lt;groups&lt;${package}.CategorySlowIntegrationTestIT&lt;/groups&lt;
 *          &lt;/configuration&lt;
 *  &lt;/plugin&lt;
 *
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public interface CategorySlowIntegrationTestIT {
    //
}
