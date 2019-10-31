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
package de.bomc.poc.hrm.domain.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.objmother.PermissionMother;
import de.bomc.poc.hrm.objmother.RoleMother;

/**
 * Tests the permission entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RolePermissionEntityTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "RolePermissionEntityTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionEntityTest.class);

	// _______________________________________________
	// Test constants
	// -----------------------------------------------

	// _______________________________________________
	// Member variables
	// -----------------------------------------------

	// _______________________________________________
	// Test preparation
	// -----------------------------------------------
	@Before
	public void initialize() {
		LOGGER.debug(LOG_PREFIX + "initialize");

		this.entityManager1 = this.emProvider1.getEntityManager();

		assertThat(this.entityManager1, notNullValue());
	}

	// _______________________________________________
	// Test methods
	// -----------------------------------------------
	@Test
	public void test010_createPermission_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createPermission_pass");

		// GIVEN
		final PermissionEntity permissionEntity = AbstractBaseUnit.createNonPersistedPermissionEntity();

		// WHEN
		this.emProvider1.tx().begin();
		this.entityManager1.persist(permissionEntity);
		this.emProvider1.tx().commit();

		// THEN
		assertThat(permissionEntity.getId(), notNullValue());

		final SecurityObjectEntity securityObjectEntity = (SecurityObjectEntity) permissionEntity;
		assertThat(securityObjectEntity.getId(), notNullValue());

		assertThat(securityObjectEntity.getCreateDateTime(), notNullValue());
		assertThat(securityObjectEntity.getEntityClass(), equalTo(SecurityObjectEntity.class));
		assertThat(securityObjectEntity.getName(), equalTo(PERMISSION_NAME));
		assertThat(securityObjectEntity.getDescription(), equalTo(PERMISSION_DESCRIPTION));
	}

	@Test
	public void test020_createRole_pass() {
		LOGGER.debug(LOG_PREFIX + "test020_createRole_pass");

		// GIVEN
		final RoleEntity roleEntity = AbstractBaseUnit.createNonPersistedRoleEntity();

		// THEN
		this.emProvider1.tx().begin();
		this.entityManager1.persist(roleEntity);
		this.emProvider1.tx().commit();

		// WHEN
		assertThat(roleEntity.getId(), notNullValue());

		final SecurityObjectEntity securityObjectEntity = (SecurityObjectEntity) roleEntity;
		assertThat(securityObjectEntity.getId(), notNullValue());

		assertThat(securityObjectEntity.getCreateDateTime(), notNullValue());
		assertThat(securityObjectEntity.getEntityClass(), equalTo(SecurityObjectEntity.class));
		assertThat(securityObjectEntity.getName(), equalTo(ROLE_NAME));
		assertThat(securityObjectEntity.getDescription(), equalTo(ROLE_DESCRIPTION));
	}

	@Test
	public void test030_permissionMother_pass() {
		LOGGER.debug(LOG_PREFIX + "test030_permissionMother_pass");

		// GIVEN
		final PermissionMother permissionMother = new PermissionMother(this.emProvider1, PERMISSION_NAME);

		// WHEN
		final PermissionEntity permissionEntity = permissionMother.instance();

		// THEN
		assertThat(permissionEntity, notNullValue());
		assertThat(permissionEntity.getId(), notNullValue());
		assertThat(permissionEntity.getCreateDateTime(), notNullValue());
		assertThat(permissionEntity.getName(), equalTo(AbstractBaseUnit.PERMISSION_NAME));
		assertThat(permissionEntity.getDescription(), equalTo(AbstractBaseUnit.PERMISSION_DESCRIPTION));
	}

	@Test
	public void test040_roleBuilder_pass() {
		LOGGER.debug(LOG_PREFIX + "test040_roleBuilder_pass");

		final RoleEntity.Builder builder = new RoleEntity.Builder(AbstractBaseUnit.ROLE_NAME);
		// Set as immutable = true.
		final RoleEntity roleEntity = builder.asImmutable().withDescription(ROLE_DESCRIPTION).build();

		assertThat(roleEntity.getName(), equalTo(AbstractBaseUnit.ROLE_NAME));
		assertThat(roleEntity.getDescription(), equalTo(AbstractBaseUnit.ROLE_DESCRIPTION));
		assertThat(roleEntity.isImmutable(), equalTo(true));
	}

	@Test
	public void test050_roleMother_pass() {
		LOGGER.debug(LOG_PREFIX + "test050_roleMother_pass");

		// GIVEN
		final RoleMother roleMother = new RoleMother(this.emProvider1, ROLE_NAME);

		// WHEN
		final RoleEntity roleEntity = roleMother.instance();

		// THEN
		assertThat(roleEntity, notNullValue());
		assertThat(roleEntity.getId(), notNullValue());
		assertThat(roleEntity.getCreateDateTime(), notNullValue());
		assertThat(roleEntity.getName(), equalTo(AbstractBaseUnit.ROLE_NAME));
		assertThat(roleEntity.getDescription(), equalTo(AbstractBaseUnit.ROLE_DESCRIPTION));
	}

	/**
	 * Error occurs with message: object references an unsaved transient instance -
	 * save the transient instance before flushing:
	 * de.bomc.poc.hrm.domain.model.PermissionEntity
	 */
	@Test
	public void test060_addPermissionToRole_fail() {
		LOGGER.debug(LOG_PREFIX + "test060_addPermissionToRole_fail");

		this.thrown.expect(Exception.class);
		
		// GIVEN
		final PermissionEntity permissionEntity = new PermissionEntity(AbstractBaseUnit.PERMISSION_NAME,
				AbstractBaseUnit.PERMISSION_DESCRIPTION);

		// THEN
		final RoleEntity roleEntity = new RoleEntity(AbstractBaseUnit.ROLE_NAME, AbstractBaseUnit.ROLE_DESCRIPTION);
		roleEntity.addPermission(permissionEntity);

		// WHEN
		this.emProvider1.tx().begin();

		this.entityManager1.persist(roleEntity);

		this.emProvider1.tx().commit();
	}
	
	@Test
	public void test070_addPermissionToRole_pass() {
		LOGGER.debug(LOG_PREFIX + "test070_addPermissionToRole_pass");
		
		// GIVEN
		final PermissionMother permissionMother = new PermissionMother(this.emProvider1, AbstractBaseUnit.PERMISSION_NAME);
		final PermissionEntity permissionEntity = permissionMother.instance();
		
		// WHEN
		final RoleEntity roleEntity = new RoleEntity(AbstractBaseUnit.ROLE_NAME, AbstractBaseUnit.ROLE_DESCRIPTION);
		roleEntity.addPermission(permissionEntity);

		this.emProvider1.tx().begin();
		this.entityManager1.persist(roleEntity);
		this.emProvider1.tx().commit();
		
		// THEN
		assertThat(roleEntity.getPermissions().stream().count(), equalTo(1L));
	}
}
