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
 * A category marker interface for basis integration tests. These tests are coming with the given archetype and must NOT be tested again.
 * <p>
 * <pre>
 *
 * We can now select which categories we want to run on the command line by passing the groups parameter to the Maven executable. Examples:
 *
 *  mvn test -Dgroups="de.bomc.poc.order.CategoryBasisIntegrationTestIT"
 *  mvn test -Dgroups="de.bomc.poc.order.CategoryBasisIntegrationTestIT,de.bomc.poc.order.CategoryBasisUnitTest"
 *
 *  Alternatively, we can set up the category directly in the pom.xml file
 *
 *  &lt;plugin&gt;
 *      &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *      &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *          &lt;configuration&gt;
 *              &lt;groups&gt;de.bomc.poc.order.CategoryBasisIntegrationTestIT&lt;/groups&gt;
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
