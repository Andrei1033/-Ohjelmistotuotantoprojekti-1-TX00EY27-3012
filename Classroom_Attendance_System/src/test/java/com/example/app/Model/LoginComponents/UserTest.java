package com.example.app.Model.LoginComponents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final int    ID        = 42;
    private static final String FIRST     = "Matti";
    private static final String LAST      = "Meikäläinen";
    private static final String EMAIL     = "matti@example.com";
    private static final Role   ROLE      = Role.ADMIN;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(ID, FIRST, LAST, EMAIL, ROLE);
    }

    // ------------------------------------------------------------------
    // Constructor + getters
    // ------------------------------------------------------------------

    @Test
    void constructor_storesAllFields() {
        assertEquals(ID,    user.getId());
        assertEquals(FIRST, user.getFirstName());
        assertEquals(LAST,  user.getLastName());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(ROLE,  user.getRole());
    }

    @Test
    void getId_returnsConstructorValue() {
        User other = new User(7, "A", "B", "a@b.c", Role.STUDENT);
        assertEquals(7, other.getId());
    }

    @Test
    void getFullName_joinsFirstAndLastNameWithSpace() {
        assertEquals("Matti Meikäläinen", user.getFullName());
    }

    @Test
    void getFullName_handlesEmptyNames() {
        User empty = new User(1, "", "", "x@y.z", Role.STUDENT);
        assertEquals(" ", empty.getFullName());
    }

    // ------------------------------------------------------------------
    // Setters (mutable fields)
    // ------------------------------------------------------------------

    @Test
    void setFirstName_updatesValue() {
        user.setFirstName("Maija");
        assertEquals("Maija", user.getFirstName());
        assertEquals("Maija Meikäläinen", user.getFullName());
    }

    @Test
    void setLastName_updatesValue() {
        user.setLastName("Virtanen");
        assertEquals("Virtanen", user.getLastName());
        assertEquals("Matti Virtanen", user.getFullName());
    }

    @Test
    void setEmail_updatesValue() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    // ------------------------------------------------------------------
    // Immutability of id and role
    // ------------------------------------------------------------------
    // These aren't enforced by the compiler, but the fields are declared
    // final. The following tests simply document the current behaviour:
    // there is no setter for id or role, so calling one would not compile.
    // We instead assert that they stay the same after mutating the other
    // fields.

    @Test
    void idAndRole_remainUnchanged_afterProfileEdits() {
        user.setFirstName("Maija");
        user.setLastName("Virtanen");
        user.setEmail("maija@example.com");

        assertEquals(ID,   user.getId());
        assertEquals(ROLE, user.getRole());
    }

    // ------------------------------------------------------------------
    // toString
    // ------------------------------------------------------------------

    @Test
    void toString_containsIdEmailAndRole() {
        String s = user.toString();

        assertTrue(s.contains("id=42"),                 s);
        assertTrue(s.contains("email='matti@example.com'"), s);
        assertTrue(s.contains("role=ADMIN"),            s);
    }

    @Test
    void toString_doesNotContainFirstNameOrLastName() {
        String s = user.toString();
        assertFalse(s.contains("Matti"),       s);
        assertFalse(s.contains("Meikäläinen"), s);
    }

    // ------------------------------------------------------------------
    // Equality
    // ------------------------------------------------------------------
    // User does not override equals(), so it uses reference equality.
    // These tests document that behaviour. If you later add a proper
    // equals()/hashCode() based on id, update these tests accordingly.

    @Test
    void equals_isReferenceBased_byDefault() {
        User same = new User(ID, FIRST, LAST, EMAIL, ROLE);

        assertEquals(user, user);            // same instance
        assertNotEquals(user, same);         // different instance, same data
    }
}