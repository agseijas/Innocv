package es.innocv.uidex.utils.converters.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import es.innocv.uidex.model.User;
import es.innocv.uidex.service.dto.UserDTO;
import es.innocv.uidex.utils.converters.IUserUserDTOConverter;

@Component
public class UserUserDTOConverterImpl implements IUserUserDTOConverter{

	public UserDTO convertToUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(user, userDTO);
		return userDTO;
	}

	public User convertToUser(UserDTO userDTO) {
		User user = new User();
		BeanUtils.copyProperties(userDTO, user);
		return user;
	}

}
