package es.innocv.uidex.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import es.innocv.uidex.exception.UserFoundException;
import es.innocv.uidex.exception.UserNotFoundException;
import es.innocv.uidex.model.User;
import es.innocv.uidex.repository.IUserRepository;
import es.innocv.uidex.service.IUserService;

//FIXME: Not a fan of these annotations that require component scans. Would try to inject a explicitly defined @Configuration
@Service
public class UserService implements IUserService{

	private final IUserRepository userRepository;
	
	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public List<User> getAllUsers(String name) {

		return Optional.ofNullable(name) //FIXME: I would create a parameter-less method instead.
				.map(userRepository::findByNameContainsIgnoreCase)
				.orElse(userRepository.findAll());
	}
	
	@Override
	public User getUserById(Integer id) {
		return userRepository
				.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}
	
	@Override
	public User createUser(User user) { //FIXME: By this method signature we're leaking JPA entities (User) to domain and web code.
		if(user.getId() != null) { //FIXME: Would recommend creating a User.isNewUser method
			userRepository
				.findById(user.getId())
				.ifPresent( (u) -> {throw new UserFoundException(u.getId());} );
		}
		return userRepository.save(user);
	}
	
	@Override
	public void updateUser(User user, Integer id) {
		userRepository
			.findById(id)
			.orElseThrow( () -> new UserNotFoundException(id) );
		
		user.setId(id);
		userRepository.save(user);
	}
	
	@Override
	public void deleteUser(Integer id) {
		userRepository
			.findById(id)
			.orElseThrow( () -> new UserNotFoundException(id) );
		userRepository.deleteById(id);
	}
}
