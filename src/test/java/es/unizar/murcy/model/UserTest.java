package es.unizar.murcy.model;


import es.unizar.murcy.model.User.Rol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class UserTest {

    @Test
    public void testSetIdUser(){
        User newUser=new User();
        newUser.setId(1);
        assertEquals(1, newUser.getId());
    }

    @Test
    public void testGetIdUser(){
        User newUser=new User();
        newUser.setId(1);
        long id=newUser.getId();
        assertEquals(id, 1);
    }

    @Test
    public void testSetUsernameUser(){
        User newUser=new User();
        newUser.setUsername("Joaquin");
        assertEquals(newUser.getUsername(), "Joaquin");
    }

    @Test
    public void testGetUsernameUser(){
        User newUser=new User();
        newUser.setUsername("Joaquin");
        String userName=newUser.getUsername();
        assertEquals(userName,"Joaquin");
    }

    @Test
    public void testSetPasswordUser(){
        User newUser=new User();
        newUser.setPassword("pancakes");
        assertEquals(newUser.getPassword(),"pancakes");
    }

    @Test
    public void testGetPasswordUser(){
        User newUser=new User();
        newUser.setPassword("pancakes");
        assertEquals(newUser.getPassword(), "pancakes");
    }

    @Test
    public void testSetFullNameUser(){
        User newUser=new User();
        newUser.setFullName("Joaquin Puyuelo Maynard");
        assertEquals(newUser.getFullName(), "Joaquin Puyuelo Maynard");
    }

    @Test
    public void testGetFullNameUser() {
        User newUser=new User();
        newUser.setFullName("Joaquin Puyuelo Maynard");
        assertEquals("Joaquin Puyuelo Maynard", newUser.getFullName());
    }

    @Test
    public void testSetEmailUser(){
        User newUser=new User();
        newUser.setEmail("JoaquinEmail");
        assertEquals(newUser.getEmail(), "JoaquinEmail");
    }

    @Test
    public void testGetEmailUser(){
        User newUser=new User();
        newUser.setEmail("JoaquinEmail");
        String userEmail=newUser.getEmail();
        assertEquals(userEmail, "JoaquinEmail");
    }

    @Test
    public void testSetLastIpUser(){
        User newUser=new User();
        newUser.setLastIp("1234");
        assertEquals(newUser.getLastIp(), "1234");
    }

    @Test
    public void testGetLastIpUser(){
        User newUser=new User();
        newUser.setLastIp("1234");
        String lastIp=newUser.getLastIp();
        assertEquals(lastIp, "1234");
    }

    @Test
    public void testSetConfirmedUser(){
        User newConfirmedUser=new User();
        newConfirmedUser.setConfirmed(true);
        boolean confirmation=newConfirmedUser.getConfirmed();
        assertEquals(confirmation, true);
    }

    @Test
    public void testGetConfirmedUser(){
        User newConfirmedUser=new User();
        newConfirmedUser.setConfirmed(true);
        boolean confirmation=newConfirmedUser.getConfirmed();
        assertEquals(confirmation, true);
    }

    @Test
    public void testSetCreateDateUser(){
        Date date=new Date();
        User newUser=new User();
        newUser.setCreateDate(date);
        assertEquals(newUser.getCreateDate(), date);
    }

    @Test
    public void testGetCreateDateUser(){
        Date date=new Date();
        User newUser=new User();
        newUser.setCreateDate(date);
        Date testDate=newUser.getCreateDate();
        assertEquals(testDate, date);
    }

    @Test
    public void testSetModifiedDateUser(){
        Date date=new Date();
        User newUser=new User();
        newUser.setModifiedDate(date);
        assertEquals(newUser.getModifiedDate(), date);
    }

    @Test
    public void testGetModifiedDateUser(){
        Date date=new Date();
        User newUser=new User();
        newUser.setModifiedDate(date);
        Date testDate=newUser.getModifiedDate();
        assertEquals(testDate, date);
    }

    @Test
    public void testSetRolesOfUser(){
        User newUser=new User();
        Rol listOfRoles[]={Rol.EDITOR, Rol.REVIEWER, Rol.USER};
        Set<Rol> setOfRoles=new HashSet<>(Arrays.asList(listOfRoles));
        newUser.setRoles(setOfRoles);
        assertEquals(newUser.getRoles(), setOfRoles);
    }

    @Test
    public void testGetRolesOfUser(){
        User newUser=new User();
        Rol listOfRoles[]={Rol.EDITOR, Rol.REVIEWER, Rol.USER};
        Set<Rol> setOfRoles=new HashSet<>(Arrays.asList(listOfRoles));
        newUser.setRoles(setOfRoles);
        Set<Rol> testSetOfRoles=newUser.getRoles();
        assertEquals(testSetOfRoles, setOfRoles);
    }

    @Test
    public void testCreateUser(){
        User newUser = new User("Test", "testpass", "test@test.com", "Test Test");
        assertEquals(newUser.getUsername(),"Test");
    }
}
