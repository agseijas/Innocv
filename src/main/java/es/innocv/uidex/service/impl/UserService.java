package es.innocv.uidex.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import es.innocv.uidex.exception.UserFoundException;
import es.innocv.uidex.exception.UserNotFoundException;
import es.innocv.uidex.model.User;
import es.innocv.uidex.repository.IUserRepository;
import es.innocv.uidex.service.IUserService;

@Service
public class UserService implements IUserService{

	private final IUserRepository userRepository;
	
	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public List<User> getAllUsers(String name) {
		
		boolean findByName = name != null && !name.isEmpty();
		
		if(findByName) {
			return userRepository.findByNameContainsIgnoreCase(name);
		} else {
			return userRepository.findAll();
		}
	}
	
	@Override
	public User getUserById(Integer id) {
		return userRepository
				.findById(id)
				.orElseThrow(
						() -> new UserNotFoundException(id)
					);
	}
	
	@Override
	public User createUser(User user) {
		if(user.getId() != null) {
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
