package net.ensah.tp3.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ensah.tp3.common.RestExceptionHandler;
import net.ensah.tp3.user.dto.UserRequest;
import net.ensah.tp3.user.dto.UserResponse;
import net.ensah.tp3.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(RestExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void create_user_success() throws Exception {
        UserResponse response = new UserResponse(1L, "John", "Doe", "john@example.com", Instant.now());
        Mockito.when(userService.create(any(UserRequest.class))).thenReturn(response);

        UserRequest req = new UserRequest("John", "Doe", "john@example.com");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void create_user_validation_error() throws Exception {
        // Empty firstName should trigger 400 Bad Request
        UserRequest req = new UserRequest("", "Doe", "not-an-email");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_user_not_found() throws Exception {
        Mockito.when(userService.getById(99L)).thenThrow(new RuntimeException("User not found"));
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void update_user_conflict_on_duplicate_email() throws Exception {
        Mockito.when(userService.update(eq(1L), any(UserRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already in use"));

        UserRequest req = new UserRequest("John", "Doe", "dup@example.com");
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email")));
    }
}
