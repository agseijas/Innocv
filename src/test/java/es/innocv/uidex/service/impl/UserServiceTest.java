package es.innocv.uidex.service.impl;

import static java.time.LocalDate.now;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
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
	void whenGetAllUsers_thenReturnsAllUsers() throws Exception { //FIXME unneeded `throws Exception` through this class
//		GIVEN
		List<User> expectedUserList = Stream.of(
				User.from(1, "David", now()),
				User.from(2, "Laura", now()))
				.collect(toList());
		
		when(userRepository.findAll()).thenReturn(expectedUserList);
	    
//		WHEN
		List<User> allUsers = userService.getAllUsers(null); //FIXME: null ? why not using Optional? (or even empty string)
		
//		THEN
		assertNotNull(allUsers); //FIXME: unnecessary assertion, either return empty list or a filled list
		assertEquals(expectedUserList, allUsers); //FIXME: asserting it's the same object: would use the Matchers.contains or Matchers.containsInAnyOrder methods instead

		//FIXME: You don't have to verify interactions with collaborators (userRepository), assertions above are just enough, usually `verify` is used for asserting collaborators interaction (and normally that happens when testing void returning methods)
		verify(userRepository, times(1)).findAll();  //FIXME: Mockito's verify is 1 by default
		verify(userRepository, times(0)).findByNameContainsIgnoreCase(any()); //FIXME: use verifyNoInteractions
	}
	
	@Test
	void whenGetAllUsersWithName_thenReturnsAllUsersMatch() throws Exception {
//		GIVEN		
		List<User> expectedUserList = Stream.of(
				User.from(1, "David", now()))
				.collect(toList());

		//FIXME: I would change the `any` for the actual parameter, in this case we are making sure that's `david` the name that has to be search. But beware!, often, in other cases, `any` is the better choice
		when(userRepository.findByNameContainsIgnoreCase(any())).thenReturn(expectedUserList);
	    
//		WHEN
		List<User> allUsers = userService.getAllUsers("david");
		
//		THEN
		assertNotNull(allUsers); //FIXME: same as with previous test
		assertEquals(expectedUserList, allUsers);

		//FIXME: Same like the previous test, mixing collaboration vs behaviour, I would remove these verifications
		verify(userRepository, times(0)).findAll(); //FIXME: same
		verify(userRepository, times(1)).findByNameContainsIgnoreCase(any());
	}
	
	@Test
	void givenValidUser_whenGetUser_thenReturnsUser() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", now());

		//FIXME: same as previous test
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
	        
		User user = userService.getUserById(1);
		
		assertNotNull(user);
		assertEquals(expectedUser, user);
		
		verify(userRepository, times(1)).findById(any());
	}

	//FIXME: Rest of the test have the same patterns that have been already expressed on previous tests FIXME's
	
	@Test
	void givenNoValidUser_whenGetUser_thenReturnsUserNotFound() throws Exception {
//		GIVEN
		when(userRepository.findById(any())).thenThrow(UserNotFoundException.class);
		
//		WHEN - THEN
		assertThrows(UserNotFoundException.class, 
				() -> userService.getUserById(1));
		
//		THEN
		verify(userRepository, times(1)).findById(any());
	}
	
	@Test
	void givenUser_whenCreateUser_thenReturnsUser() throws Exception {
//		GIVEN
		User expectedUser = User.from(1, "David", now());
		User user = User.from(null, "David", now());
		
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
		User expectedUser = User.from(1, "David", now());
		
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
		User expectedUser = User.from(1, "David", now());
		
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
		User expectedUser = User.from(1, "David", now());
		
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
		User expectedUser = User.from(1, "David", now());
		
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		Mockito.doNothing().when(userRepository).deleteById(any()); //FIXME: With mockito, by default, void methods do nothing, no need to explicitly define this interaction.

//		WHEN
		userService.deleteUser(1);
		
//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(1)).deleteById(any());
	}
	
	@Test
	void whenDeleteNonExistentUser_thenReturnsUserNotFound() throws Exception {
		//FIXME: This test approach might be improved see the next test below, you shouldn't be throwing the exception from test code
//		GIVEN
		Mockito.doThrow(UserNotFoundException.class).when(userRepository).findById(any());
		
//		WHEN - THEN
		assertThrows(UserNotFoundException.class, 
				() -> userService.deleteUser( 1));
		
//		THEN
		verify(userRepository, times(1)).findById(any());
		verify(userRepository, times(0)).delete(any());
	}

	@Test
	void whenDeleteNonExistentUser_thenReturnsUserNotFound_NEW() throws Exception {
		//		GIVEN
		when(userRepository.findById(1)).thenReturn(empty());

		//		WHEN - THEN
		assertThrows(UserNotFoundException.class,
				() -> userService.deleteUser( 1));
	}
}
