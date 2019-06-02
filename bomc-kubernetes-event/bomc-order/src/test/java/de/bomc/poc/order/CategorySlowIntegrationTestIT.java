/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order;

/**
 * A category marker interface for integration tests.
 * <p>
 * <pre>
 *
 * We can now select which categories we want to run on the command line by passing the groups parameter to the Maven executable. Examples:
 *
 *  mvn test -Dgroups="de.bomc.poc.order.CategoryIntegrationTestIT"
 *  mvn test -Dgroups="de.bomc.poc.order.CategoryIntegrationTestIT,de.bomc.poc.order.CategoryFastUnitTest"
 *
 *  Alternatively, we can set up the category directly in the pom.xml file
 *
 *  &lt;plugin&lt;
 *      &lt;groupId&lt;org.apache.maven.plugins&lt;/groupId&lt;
 *      &lt;artifactId&lt;maven-surefire-plugin&lt;/artifactId&lt;
 *          &lt;configuration&lt;
 *              &lt;groups&lt;de.bomc.poc.order.CategorySlowIntegrationTestIT&lt;/groups&lt;
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
