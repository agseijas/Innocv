package es.innocv.uidex.service;

import java.util.List;

import es.innocv.uidex.model.User;

public interface IUserService {

	List<User> getAllUsers(String name);
	
	User getUserById(Integer id);
	
	User createUser(User user);
	
	void updateUser(User user, Integer id);
	
	void deleteUser(Integer id);
	
}
