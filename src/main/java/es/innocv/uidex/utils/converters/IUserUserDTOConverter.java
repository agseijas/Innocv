package es.innocv.uidex.utils.converters;

import es.innocv.uidex.model.User;
import es.innocv.uidex.service.dto.UserDTO;

public interface IUserUserDTOConverter {

	UserDTO convertToUserDTO(User user);
	User convertToUser(UserDTO userDTO);
}
