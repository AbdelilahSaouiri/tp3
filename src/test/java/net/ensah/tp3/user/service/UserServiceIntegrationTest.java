package net.ensah.tp3.user.service;

import net.ensah.tp3.user.dto.UserRequest;
import net.ensah.tp3.user.dto.UserResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    void create_and_getById_and_getAll() {
        UserRequest req = new UserRequest("John", "Doe", "john.doe@example.com");
        UserResponse created = userService.create(req);
        assertNotNull(created.id());
        assertEquals("John", created.firstName());

        UserResponse byId = userService.getById(created.id());
        assertEquals(created.id(), byId.id());

        List<UserResponse> all = userService.getAll();
        assertEquals(1, all.size());
    }

    @Test
    @Order(2)
    void update_user_and_prevent_duplicate_email() {
        UserResponse u1 = userService.create(new UserRequest("A", "A", "a@example.com"));
        UserResponse u2 = userService.create(new UserRequest("B", "B", "b@example.com"));

        // Update u2 ok
        UserResponse updated = userService.update(u2.id(), new UserRequest("B2", "B2", "b2@example.com"));
        assertEquals("B2", updated.firstName());

        // Try to set duplicate email
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.update(u2.id(), new UserRequest("B3", "B3", "a@example.com"))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("email"));
    }

    @Test
    @Order(3)
    void delete_user_and_not_found() {
        UserResponse u = userService.create(new UserRequest("X", "Y", "xy@example.com"));
        assertDoesNotThrow(() -> userService.getById(u.id()));
        userService.delete(u.id());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getById(u.id()));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }
}
