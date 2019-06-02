/**
 * Project: MY_POC
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
package de.bomc.poc.test.api.unit.mapper;

import de.bomc.poc.api.mapper.LocalDateTimeMapper;
import de.bomc.poc.api.mapper.UserManagementMapper;
import de.bomc.poc.api.mapper.transfer.GrantDTO;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.api.mapper.transfer.UserDTO;
import de.bomc.poc.api.mapper.transfer.UserManagementDTO;
import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.model.usermanagement.SecurityObject;
import de.bomc.poc.auth.model.usermanagement.User;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * Tests the user management mapping.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserManagementMapperTest {

    private static final Logger LOGGER = Logger.getLogger(UserManagementMapperTest.class);
    private final LocalDateTime localDateTime = LocalDateTime.of(2016, 11, 1, 23, 59, 30);

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test01_simpleUserMapping_Pass
     *
     * Test mapping User to UserDTO.
     */
    @Test
    public void test01_simpleUserMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test01_simpleUserMapping_Pass");
        final User user1 = new User("bomc1");
        user1.setId(2911L);
        user1.setExpirationDate(this.localDateTime);

        final UserDTO dto = UserManagementMapper.INSTANCE.map_User_To_DTO(user1);

        assertThat(dto.getId(), is(equalTo(user1.getId())));
        assertThat(dto.getUsername(), is(equalTo(user1.getUsername())));
        assertThat(dto.getExpirationDate(), is(equalTo(this.localDateTime)));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test02_simpleUserInverseMapping_Pass
     *
     * Test mapping UserDTO to User.
     */
    @Test
    public void test02_simpleUserInverseMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test02_simpleUserInverseMapping_Pass");

        final UserDTO userDTO1 = new UserDTO("bomc1");
        userDTO1.setExpirationDate(this.localDateTime);
        userDTO1.setId(2911L);

        LOGGER.debug("UserManagementMapperTest#test02_simpleUserInverseMapping_Pass [userDTO1=" + userDTO1.toString() + "]");

        final User user = UserManagementMapper.INSTANCE.map_DTO_To_User(userDTO1);

        LOGGER.debug("UserManagementMapperTest#test02_simpleUserInverseMapping_Pass [user=" + user.toString() + "]");

        assertThat(user.getId(), is(equalTo(userDTO1.getId())));
        assertThat(user.getUsername(), is(equalTo(userDTO1.getUsername())));
        assertThat(user.getExpirationDate(), is(equalTo(this.localDateTime)));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test03_simpleRoleMapping_Pass
     *
     * Test mapping Role to RoleDTO.
     */
    @Test
    public void test03_simpleRoleMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test03_simpleRoleMapping_Pass");

        final Role role = new Role("myRole", "myDescription");
        role.setId(2911L);

        final RoleDTO dto = UserManagementMapper.INSTANCE.map_Role_To_DTO(role);

        assertThat(dto.getId(), is(equalTo(role.getId())));
        assertThat(dto.getName(), is(equalTo(role.getName())));
        assertThat(dto.getDescription(), is(equalTo(role.getDescription())));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test04_simpleRoleInverseMapping_Pass
     *
     * Test mapping RoleDTO to Role
     */
    @Test
    public void test04_simpleRoleInverseMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test04_simpleRoleInverseMapping_Pass");

        final RoleDTO roleDTO1 = new RoleDTO("myRolename", "myDescription");
        roleDTO1.setId(2911L);

        final Role role = UserManagementMapper.INSTANCE.map_DTO_To_Role(roleDTO1);

        assertThat(role.getId(), is(equalTo(roleDTO1.getId())));
        assertThat(role.getName(), is(equalTo(roleDTO1.getName())));
        assertThat(role.getDescription(), is(equalTo(roleDTO1.getDescription())));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test05_simpleGrantMapping_Pass
     *
     * Test mapping Grant to GrantDTO.
     */
    @Test
    public void test05_simpleGrantMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test05_simpleGrantMapping_Pass");

        final Grant grant = new Grant("myGrant", "myGrantDescrption");
        grant.setId(2911L);

        final GrantDTO dto = UserManagementMapper.INSTANCE.map_Grant_To_DTO(grant);

        assertThat(dto.getId(), is(equalTo(grant.getId())));
        assertThat(dto.getName(), is(equalTo(grant.getName())));

        // ---------------------------------------------------------

        final Grant grant1 = new Grant("myGrant");
        grant.setId(2911L);

        final GrantDTO dto1 = UserManagementMapper.INSTANCE.map_Grant_To_DTO(grant1);

        assertThat(dto1.getId(), is(equalTo(grant1.getId())));
        assertThat(dto1.getName(), is(equalTo(grant1.getName())));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test06_simpleGrantInverseMapping_Pass
     *
     * Test mapping GrantDTO to Grant.
     */
    @Test
    public void test06_simpleGrantInverseMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test06_simpleGrantInverseMapping_Pass");

        final GrantDTO grantDTO = new GrantDTO("myGrant", "myGrantDescription");
        grantDTO.setId(2911L);

        final Grant grant = UserManagementMapper.INSTANCE.map_DTO_To_Grant(grantDTO);

        assertThat(grant.getId(), is(equalTo(grantDTO.getId())));
        assertThat(grant.getName(), is(equalTo(grantDTO.getName())));

        // ---------------------------------------------------------

        final GrantDTO grantDTO1 = new GrantDTO("myGrant", "myGrantDescription");
        grantDTO1.setId(2911L);

        final Grant grant1 = UserManagementMapper.INSTANCE.map_DTO_To_Grant(grantDTO1);

        assertThat(grant1.getId(), is(equalTo(grantDTO1.getId())));
        assertThat(grant1.getName(), is(equalTo(grantDTO1.getName())));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test07_userListMapping_Pass
     *
     * Test mapping List<User> to List<UserDTO>.
     */
    @Test
    public void test07_userListMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test07_userListMapping_Pass");

        final List<User> userList = new ArrayList<User>();

        final User user1 = new User("bomc1");
        user1.setId(1L);
        user1.setExpirationDate(localDateTime);
        userList.add(user1);
        final User user2 = new User("bomc2");
        user2.setId(2L);
        user2.setExpirationDate(localDateTime);
        userList.add(user2);
        final User user3 = new User("bomc3");
        user3.setId(3L);
        user3.setExpirationDate(localDateTime);
        userList.add(user3);
        final User user4 = new User("bomc4");
        user4.setId(4L);
        user4.setExpirationDate(localDateTime);
        userList.add(user4);
        final User user5 = new User("bomc5");
        user5.setId(5L);
        user5.setExpirationDate(localDateTime);
        userList.add(user5);

        final List<UserDTO> dtoList = UserManagementMapper.INSTANCE.map_UserList_To_DTOList(userList);

        dtoList.forEach(System.out::println);

        assertThat(5, is(dtoList.size()));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test08_userListInverseMapping_Pass
     *
     * Test mapping List<UserDTO> to List<User>.
     */
    @Test
    public void test08_userListInverseMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test08_userListInverseMapping");

        final List<UserDTO> userDTOList = new ArrayList<UserDTO>();

        final UserDTO userDTO1 = new UserDTO("bomc1");
        userDTO1.setExpirationDate(localDateTime);
        userDTO1.setId(2911L);
        userDTOList.add(userDTO1);
        final UserDTO userDTO2 = new UserDTO("bomc2");
        userDTO2.setExpirationDate(localDateTime);
        userDTO1.setId(2912L);
        userDTOList.add(userDTO2);
        final UserDTO userDTO3 = new UserDTO("bomc3");
        userDTO3.setExpirationDate(localDateTime);
        userDTO1.setId(2913L);
        userDTOList.add(userDTO3);
        final UserDTO userDTO4 = new UserDTO("bomc4");
        userDTO4.setExpirationDate(localDateTime);
        userDTO1.setId(2914L);
        userDTOList.add(userDTO4);
        final UserDTO userDTO5 = new UserDTO("bomc5");
        userDTO5.setExpirationDate(localDateTime);
        userDTO1.setId(2915L);
        userDTOList.add(userDTO5);

        final List<User> list = UserManagementMapper.INSTANCE.map_UserDTOList_To_List(userDTOList);

        list.forEach(System.out::println);

        assertThat(5, is(list.size()));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test09_roleListMapping_Pass
     *
     * Test mapping List<Role> to List<RoleDTO>.
     */
    @Test
    public void test09_roleListMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test09_roleListMapping_Pass");

        final List<Role> roleList = new ArrayList<Role>();

        final Role role1 = new Role("bomc1", "myDescription");
        role1.setId(1L);
        roleList.add(role1);
        final Role role2 = new Role("bomc2", "myDescription");
        role2.setId(2L);
        roleList.add(role2);
        final Role role3 = new Role("bomc3", "myDescription");
        role3.setId(3L);
        roleList.add(role3);
        final Role role4 = new Role("bomc4", "myDescription");
        role4.setId(4L);
        roleList.add(role4);
        final Role role5 = new Role("bomc5", "myDescription");
        role5.setId(5L);
        roleList.add(role5);

        final List<RoleDTO> dtoList = UserManagementMapper.INSTANCE.map_RoleList_To_DTOList(roleList);

        dtoList.forEach(System.out::println);

        assertThat(5, is(dtoList.size()));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test10_roleListInverseMapping_Pass
     *
     * Test mapping List<RoleDTO> to List<Role>.
     */
    @Test
    public void test10_roleListInverseMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test10_roleListInverseMapping_Pass");

        final List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();

        final RoleDTO roleDTO1 = new RoleDTO("bomc1", "myDescription");
        roleDTO1.setId(2911L);
        roleDTOList.add(roleDTO1);
        final RoleDTO roleDTO2 = new RoleDTO("bomc2", "myDescription");
        roleDTO1.setId(2912L);
        roleDTOList.add(roleDTO2);
        final RoleDTO roleDTO3 = new RoleDTO("bomc3", "myDescription");
        roleDTO1.setId(2913L);
        roleDTOList.add(roleDTO3);
        final RoleDTO roleDTO4 = new RoleDTO("bomc4", "myDescription");
        roleDTO1.setId(2914L);
        roleDTOList.add(roleDTO4);
        final RoleDTO roleDTO5 = new RoleDTO("bomc5", "myDescription");
        roleDTO1.setId(2915L);
        roleDTOList.add(roleDTO5);

        final List<Role> list = UserManagementMapper.INSTANCE.map_RoleDTOList_To_List(roleDTOList);

        list.forEach(System.out::println);

        assertThat(5, is(list.size()));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test11_grantListMapping_Pass
     *
     * Test mapping List<Grant> to List<GrantDTO>.
     */
    @Test
    public void test11_grantListMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test11_grantListMapping_Pass");

        final List<SecurityObject> grantList = new ArrayList<>();

        final Grant grant1 = new Grant("bomc1", "myDescription");
        grant1.setId(1L);
        grantList.add(grant1);
        final Grant grant2 = new Grant("bomc2", "myDescription");
        grant2.setId(2L);
        grantList.add(grant2);
        final Grant grant3 = new Grant("bomc3", "myDescription");
        grant3.setId(3L);
        grantList.add(grant3);
        final Grant grant4 = new Grant("bomc4", "myDescription");
        grant4.setId(4L);
        grantList.add(grant4);
        final Grant grant5 = new Grant("bomc5", "myDescription");
        grant5.setId(5L);
        grantList.add(grant5);

        final List<GrantDTO> dtoList = UserManagementMapper.INSTANCE.map_GrantList_To_DTOList(grantList);

        dtoList.forEach(System.out::println);

        assertThat(5, is(dtoList.size()));
    }

	@Test
	public void test12_grantDTO_equals_Pass() {
		LOGGER.debug("UserManagementMapperTest#test12_grantDTO_equals_Pass");

		final GrantDTO grantDTO1 = new GrantDTO("myGrant1", "description to myGrant1");
		grantDTO1.setId(1L);
		final GrantDTO grantDTO2 = new GrantDTO("myGrant2", "description to myGrant2");
		grantDTO2.setId(2L);
		
		final List<GrantDTO> grantDTOList = new ArrayList<>();
		grantDTOList.add(grantDTO1);
		grantDTOList.add(grantDTO2);

		// Checks that all of the items match the expected items, in any order.
		assertThat(grantDTOList, containsInAnyOrder(grantDTOList.toArray()));
	}
    
    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test12_grantListInverseMapping_Pass
     *
     * Test mapping List<GrantDTO> to List<Grant>.
     */
    @Test
    public void test13_grantListInverseMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test13_grantListInverseMapping_Pass");

        final List<GrantDTO> grantDTOList = new ArrayList<GrantDTO>();

        final GrantDTO grantDTO1 = new GrantDTO("bomc1", "myDescription");
        grantDTO1.setId(2911L);
        grantDTOList.add(grantDTO1);
        final GrantDTO grantDTO2 = new GrantDTO("bomc2", "myDescription");
        grantDTO1.setId(2912L);
        grantDTOList.add(grantDTO2);
        final GrantDTO grantDTO3 = new GrantDTO("bomc3", "myDescription");
        grantDTO1.setId(2913L);
        grantDTOList.add(grantDTO3);
        final GrantDTO grantDTO4 = new GrantDTO("bomc4", "myDescription");
        grantDTO1.setId(2914L);
        grantDTOList.add(grantDTO4);
        final GrantDTO grantDTO5 = new GrantDTO("bomc5", "myDescription");
        grantDTO1.setId(2915L);
        grantDTOList.add(grantDTO5);

        final List<Grant> list = UserManagementMapper.INSTANCE.map_GrantDTOList_To_List(grantDTOList);

        list.forEach(System.out::println);

        assertThat(5, is(list.size()));
    }

    /**
     * mvn clean install -Dtest=UserManagementMapperTest#test20_userManagementMapping_Pass
     *
     * Test mapping to UsermanagementDTO.
     */
    @Test
    public void test20_userManagementMapping_Pass() {
        LOGGER.debug("UserManagementMapperTest#test20_userManagementMapping_Pass");

        final User user = new User("myUsername");
        user.setExpirationDate(localDateTime);
        user.setId(2911L);

        final Grant grant1 = new Grant("myGrant1", "myGrantDescription");
        grant1.setId(111L);

        final Grant grant2 = new Grant("myGrant2", "myGrantDescription");
        grant2.setId(112L);

        final Role role1 = new Role("myRole1", "myDescription");
        role1.setId(3912L);
        role1.addGrant(grant1);
        role1.addGrant(grant2);

        final Role role2 = new Role("myRole2", "myDescription");
        role2.setId(3913L);
        role2.addGrant(grant1);
        role2.addGrant(grant2);

        user.addRole(role1);
        user.addRole(role2);

        final UserManagementDTO userManagementDTO = UserManagementMapper.INSTANCE.map_User_To_UserManagementDTO(user);

        // Check values.
        assertThat(userManagementDTO.getUserDTO().getId(), is(equalTo(2911L)));
        assertThat(userManagementDTO.getRoleListDTO()
                                    .size(), is(equalTo(user.getRoles()
                                                            .size())));

        final String formattedLocalDateTime = this.localDateTime
        				.format(DateTimeFormatter.ofPattern(LocalDateTimeMapper.DATE_TIME_FORMATTER));
        assertThat(userManagementDTO.getUserExpirationDate(), is(equalTo(formattedLocalDateTime)));

        final GrantDTO grantDTO1 = new GrantDTO("myGrant1", "myGrantDescription");
        grantDTO1.setId(111L);
        final GrantDTO grantDTO2 = new GrantDTO("myGrant2", "myGrantDescription");
        grantDTO2.setId(112L);

        final RoleDTO roleDTO1 = new RoleDTO("myRole1", "myDescription");
        roleDTO1.setId(3912L);
        roleDTO1.setGrantDTOList(Arrays.asList(grantDTO1, grantDTO2));

        final RoleDTO roleDTO2 = new RoleDTO("myRole2", "myDescription");
        roleDTO2.setId(3913L);
        roleDTO2.setGrantDTOList(Arrays.asList(grantDTO1, grantDTO2));

        // Checks that all of the items match the expected items, in any order.
        assertThat(userManagementDTO.getRoleListDTO(), containsInAnyOrder(roleDTO1, roleDTO2));
    }
}
