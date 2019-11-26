package es.innocv.uidex.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.innocv.uidex.exception.UserFoundException;
import es.innocv.uidex.exception.UserNotFoundException;
import es.innocv.uidex.model.User;
import es.innocv.uidex.service.IUserService;
import es.innocv.uidex.service.dto.UserDTO;
import es.innocv.uidex.utils.converters.IUserUserDTOConverter;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IUserService userService;
	
	@MockBean
	private IUserUserDTOConverter userConverter;
	
	@Test
	void whenGetAllUsers_thenReturnsOK() throws Exception {
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		UserDTO userDTO2 = UserDTO.from(2, "Laura", LocalDate.now());
		User user1 = User.from(1, "David", LocalDate.now());
		User user2 = User.from(2, "Laura", LocalDate.now());
		
		List<User> allUserList = Stream.of(
				User.from(1, "David", LocalDate.now()),
				User.from(2, "Laura", LocalDate.now())
				)
				.collect(Collectors.toList());
		
		List<UserDTO> expectedUserList = Stream.of(
				UserDTO.from(1, "David", LocalDate.now()),
				UserDTO.from(2, "Laura", LocalDate.now())
				)
				.collect(Collectors.toList());
		
		when(userService.getAllUsers(null)).thenReturn(allUserList);
		when(userConverter.convertToUserDTO(user1)).thenReturn(userDTO1);
		when(userConverter.convertToUserDTO(user2)).thenReturn(userDTO2);
	        
		MvcResult mvcResult = mockMvc.perform(
				get("/api/v1/users")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
				.andReturn()
			;
		
		
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		assertThat(objectMapper.writeValueAsString(expectedUserList),
				equalToCompressingWhiteSpace(actualResponseBody));
		
		verify(userService, times(1)).getAllUsers(any());
		verify(userConverter, times(2)).convertToUserDTO(any());
	    verifyNoMoreInteractions(userService);
	}
	
	@Test
	void givenParamName_whenGetAllUsers_thenReturnsOK() throws Exception {
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		User user1 = User.from(1, "David", LocalDate.now());
		
		List<User> allUserList = Stream.of(
				User.from(1, "David", LocalDate.now())
				)
				.collect(Collectors.toList());
		
		List<UserDTO> expectedUserList = Stream.of(
				UserDTO.from(1, "David", LocalDate.now())
				)
				.collect(Collectors.toList());
		
	    when(userService.getAllUsers(anyString())).thenReturn(allUserList);
		when(userConverter.convertToUserDTO(user1)).thenReturn(userDTO1);
		
		MvcResult mvcResult = mockMvc.perform(
				get("/api/v1/users")
				.param("name", "david")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
				.andReturn()
				;
		
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		assertThat(objectMapper.writeValueAsString(expectedUserList),
				equalToCompressingWhiteSpace(actualResponseBody));
		
		verify(userService, times(1)).getAllUsers(any());
		verify(userConverter, times(1)).convertToUserDTO(any());
	}
	
	@Test
	void whenGetUser_thenReturnsOK() throws Exception {
		User user1 = User.from(1, "David", LocalDate.now());
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		
	    when(userService.getUserById(anyInt())).thenReturn(user1);
	    when(userConverter.convertToUserDTO(user1)).thenReturn(userDTO1);
		
	    MvcResult mvcResult = mockMvc.perform(
				get("/api/v1/users/{id}", 1)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
				.andReturn()
			;
		
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		assertThat(objectMapper.writeValueAsString(userDTO1),
				equalToCompressingWhiteSpace(actualResponseBody));
		
		verify(userService, times(1)).getUserById(any());
		verify(userConverter, times(1)).convertToUserDTO(any());
	}
	
	@Test
	void givenNonExistentUser_whenGetUser_thenReturnsNotFound() throws Exception {
		
		Mockito.doThrow(UserNotFoundException.class).when(userService).getUserById(any());

        mockMvc.perform(get("/api/v1/users/{id}", 1))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(any());
        verifyNoMoreInteractions(userService);
	}
	
	@Test
	void givenNonExistentUser_whenCreateUser_thenReturnsCreated() throws Exception {
		User user1 = User.from(1, "David", LocalDate.now());
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		
		when(userService.createUser(user1)).thenReturn(user1);
		when(userConverter.convertToUserDTO(user1)).thenReturn(userDTO1);
		when(userConverter.convertToUser(userDTO1)).thenReturn(user1);
		
		mockMvc.perform(
				post("/api/v1/users")
				.content(objectMapper.writeValueAsString(userDTO1))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
			;
		
		verify(userService, times(1)).createUser(any());
		verify(userConverter, times(1)).convertToUser(any());
		verify(userConverter, times(1)).convertToUserDTO(any());
	}
	
	@Test
	void givenExistentUser_whenCreateUser_thenReturnsConflict() throws Exception {
		
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
	    
		when(userService.createUser(any())).thenThrow(UserFoundException.class);
		
		mockMvc.perform(
				post("/api/v1/users")
				.content(objectMapper.writeValueAsString(userDTO1))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isConflict())
			;
		
		verify(userService, times(1)).createUser(any());
		verify(userConverter, times(1)).convertToUser(any());
		verify(userConverter, times(0)).convertToUserDTO(any());
	}
	
	@Test
	void givenNonExistentUser_whenUpdateUser_thenReturnNotFound() throws Exception {
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		
		Mockito.doThrow(UserNotFoundException.class).when(userService).updateUser(any(), any());
		
		mockMvc.perform(
				put("/api/v1/users/{id}",1)
				.content(objectMapper.writeValueAsString(userDTO1))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound())
			;
		
		verify(userService, times(1)).updateUser(any(), any());
		verify(userConverter, times(1)).convertToUser(any());
		verify(userConverter, times(0)).convertToUserDTO(any());
	}
	
	@Test
	void givenExistentUser_whenUpdateUser_thenReturnsNoContent() throws Exception {
		User user1 = User.from(1, "David", LocalDate.now());
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		
		Mockito.doNothing().when(userService).updateUser(any(), any());
		when(userConverter.convertToUser(userDTO1)).thenReturn(user1);
		
		mockMvc.perform(
				put("/api/v1/users/{id}", 1)
				.content(objectMapper.writeValueAsString(userDTO1))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent())
			;
		
		verify(userService, times(1)).updateUser(any(), any());
		verify(userConverter, times(1)).convertToUser(any());
	}
	
	@Test
	void givenNoExistentUser_whenDeleteUser_thenReturnsNotFound() throws Exception {
		
		Mockito.doThrow(UserNotFoundException.class).when(userService).deleteUser(any());
		
		mockMvc.perform(
				delete("/api/v1/users/{id}",1))
				.andDo(print())
				.andExpect(status().isNotFound())
			;
		verify(userService, times(1)).deleteUser(any());
	}
	
	@Test
	void givenExistentUser_whenDeleteUser_thenReturnNoContent() throws Exception {
		UserDTO userDTO1 = UserDTO.from(1, "David", LocalDate.now());
		
		Mockito.doNothing().when(userService).deleteUser(any());
		
		mockMvc.perform(
				delete("/api/v1/users/{id}", 1)
				.content(objectMapper.writeValueAsString(userDTO1)))
				.andDo(print())
				.andExpect(status().isNoContent())
			;
		verify(userService, times(1)).deleteUser(any());
	}
}
