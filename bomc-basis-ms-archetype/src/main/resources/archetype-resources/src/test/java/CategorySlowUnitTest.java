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
 * 	mvn test -Dgroups="${package}.CategorySlowUnitTest"
 * 	mvn test -Dgroups="${package}.CategorySlowUnitTest,${package}.CategoryFastUnitTest"
 *
 *  Alternatively, we can set up the category directly in the pom.xml file
 *
 *  &lt;plugin&gt;
 *      &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *      &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *          &lt;configuration&gt;
 *              &lt;groups&gt;${package}.CategorySlowUnitTest&lt;/groups&gt;
 *          &lt;/configuration&gt;
 *  &lt;/plugin&gt;
 *
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public interface CategorySlowUnitTest {
    //
}
