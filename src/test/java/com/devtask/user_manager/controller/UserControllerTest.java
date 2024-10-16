package com.devtask.user_manager.controller;

import com.devtask.user_manager.entity.UserEntity;
import com.devtask.user_manager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock

    private UserService userService;

    @InjectMocks

    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers() throws Exception{
        List<UserEntity> users = Arrays.asList(new UserEntity(1L, "John123", "john123@gmail.com"), new UserEntity(2L, "Jane123", "jane@gmail.com"));
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'id': 1, 'username': 'John123', 'email': 'john123@gmail.com'}, {'id': 2, 'username': 'Jane123', 'email': 'jane@gmail.com'}]"));
    }

    @Test
    void createUser() throws Exception {
        UserEntity createdUser = new UserEntity(1L, "John123", "john123@gmail.com");
        when(userService.createUser(any(UserEntity.class))).thenReturn(createdUser);

        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(createdUser);

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(userJson));
    }

    @Test
    void updateUser() throws Exception {
        UserEntity updatedUser = new UserEntity(1L, "John123", "john123@gmail.com");

        when(userService.updateUser(eq(1L), any(UserEntity.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"username\":\"John123\",\"email\":\"john123@gmail.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value("Updated the User"));

        verify(userService).updateUser(eq(1L), any(UserEntity.class));
    }

    @Test
    void deleteUser() throws Exception {
        UserEntity userToDelete = new UserEntity(1L, "John123", "john123@gmail.com");

        when(userService.getUserById(1L)).thenReturn(userToDelete);

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/delete/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void getUserById() throws Exception{
        UserEntity foundUser = new UserEntity(1L, "John123", "john123@gmail.com");

        when(userService.getUserById(eq(1L))).thenReturn(foundUser);

        mockMvc.perform(get("/users/find/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"John123\",\"email\":\"john123@gmail.com\"}"));

        verify(userService).getUserById(eq(1L));

    }
}