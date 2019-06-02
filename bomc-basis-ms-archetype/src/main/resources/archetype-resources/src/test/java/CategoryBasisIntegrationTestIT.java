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
 * A category marker interface for basis integration tests. These tests are coming with the given archetype and must NOT be tested again.
 * <p>
 * <pre>
 *
 * We can now select which categories we want to run on the command line by passing the groups parameter to the Maven executable. Examples:
 *
 *  mvn test -Dgroups="${package}.CategoryBasisIntegrationTestIT"
 *  mvn test -Dgroups="${package}.CategoryBasisIntegrationTestIT,${package}.CategoryBasisUnitTest"
 *
 *  Alternatively, we can set up the category directly in the pom.xml file
 *
 *  &lt;plugin&gt;
 *      &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *      &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *          &lt;configuration&gt;
 *              &lt;groups&gt;${package}.CategoryBasisIntegrationTestIT&lt;/groups&gt;
 *          &lt;/configuration&gt;
 *  &lt;/plugin&gt;
 *
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public interface CategoryBasisIntegrationTestIT {
    //
}
