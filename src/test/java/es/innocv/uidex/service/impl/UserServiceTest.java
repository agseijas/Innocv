package es.innocv.uidex.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.innocv.uidex.exception.UserFoundException;
import es.innocv.uidex.exception.UserNotFoundException;
import es.innocv.uidex.model.User;
import es.innocv.uidex.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private IUserRepository userRepository;
	
	@InjectMocks
	private UserService userService;
	
	@Test
	void whenGetAllUsers_thenReturnsAllUsers() throws Exception {
//		GIVEN
		List<User> expectedUserList = Stream.of(
				User.from(1, "David", LocalDate.now()),
				User.from(2, "Laura", LocalDate.now())
				)
				.collect(Collectors.toList());
		
		when(userRepository.findAll()).thenReturn(expectedUserList);
	    
//		WHEN
		List<User> allUsers = userService.getAllUsers(null);
		
//		THEN
		assertNotNull(allUsers);
		assertEquals(expectedUserList, allUsers);
		
		verify(userRepository, times(1)).findAll();
		verify(userRepository, times(0)).findByNameContainsIgnoreCase(any());
	}
	
	@Test
	void whenGetAllUsersWithName_thenReturnsAllUsersMatch() throws Exception {
//		GIVEN		
		List<User> expectedUserList = Stream.of(
				User.from(1, "David", LocalDate.now())
				)
				.collect(Collectors.toList());
		
		when(userRepository.findByNameContainsIgnoreCase(any())).thenReturn(expectedUserList);
	    
//		WHEN
		List<User> allUsers = userService.getAllUsers("david");
		
//		THEN
		assertNotNull(allUsers);
		assertEquals(expectedUserList, allUsers);
		
		verify(userRepository, times(0)).findAll();
		verify(userRepository, times(1)).findByNameContainsIgnoreCase(any());
	}
	
	@Test
	void givenValidUser_whenGetUser_thenReturnsUser() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", LocalDate.now());
		
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
	        
		User user = userService.getUserById(1);
		
		assertNotNull(user);
		assertEquals(expectedUser, user);
		
		verify(userRepository, times(1)).findById(any());
	}
	
	@Test
	void givenNoValidUser_whenGetUser_thenReturnsUserNotFound() throws Exception {
//		GIVEN
		when(userRepository.findById(any())).thenThrow(UserNotFoundException.class);
		
//		WHEN - THEN
		assertThrows(UserNotFoundException.class, 
				() -> { userService.getUserById(1); });
		
//		THEN
		verify(userRepository, times(1)).findById(any());
	}
	
	@Test
	void givenUser_whenCreateUser_thenReturnsUser() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", LocalDate.now());
		User user = User.from(null, "David", LocalDate.now());
		
		when(userRepository.save(any())).thenReturn(expectedUser);
	    
//		WHEN
		User savedUser = userService.createUser(user);
		
//		THEN
		assertNotNull(savedUser);
		assertEquals(expectedUser, savedUser);
		
		verify(userRepository, times(0)).findById(any());
		verify(userRepository, times(1)).save(any());
	}
	
	@Test
	void givenExistentUser_whenCreateUser_thenReturnsUserFound() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", LocalDate.now());
		
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		
//		WHEN - THEN
		assertThrows(UserFoundException.class, 
				() -> { userService.createUser(expectedUser); });

//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(0)).save(any());
	}
	
	@Test
	void givenUser_whenUpdateUser_thenReturnsNothing() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", LocalDate.now());
		
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		when(userRepository.save(any())).thenReturn(expectedUser);
	        
//		WHEN
		userService.updateUser(expectedUser, 1);
		
//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(1)).save(any());
	}
	
	@Test
	void givenExistentUser_whenUpdateUser_thenReturnsUserNotFound() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", LocalDate.now());
		
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		when(userRepository.save(any())).thenThrow(UserNotFoundException.class);
		
//		WHEN - THEN
		assertThrows(UserNotFoundException.class, 
				() -> { userService.updateUser(expectedUser, 1); });
		
//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(1)).save(any());
	}
	
	@Test
	void whenDeleteValidUser_thenReturnsNothing() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", LocalDate.now());
		
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		Mockito.doNothing().when(userRepository).deleteById(any());
	        
//		WHEN
		userService.deleteUser(1);
		
//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(1)).deleteById(any());
	}
	
	@Test
	void whenDeleteNonExistentUser_thenReturnsUserNotFound() throws Exception {
//		GIVEN
		Mockito.doThrow(UserNotFoundException.class).when(userRepository).findById(any());
		
//		WHEN - THEN
		assertThrows(UserNotFoundException.class, 
				() -> { userService.deleteUser( 1); });
		
//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(0)).delete(any());
	}
}
