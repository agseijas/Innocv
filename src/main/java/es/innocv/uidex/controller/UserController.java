package es.innocv.uidex.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.innocv.uidex.model.User;
import es.innocv.uidex.service.IUserService;
import es.innocv.uidex.service.dto.UserDTO;
import es.innocv.uidex.utils.converters.IUserUserDTOConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Users API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	private final IUserService userService;
	
	private final IUserUserDTOConverter userConverter;
	
	public UserController(IUserService userService, IUserUserDTOConverter userConverter) {
		this.userService = userService;
		this.userConverter = userConverter;
	}
	
	@ApiOperation(value = "Retrieve a list of all available users", 
			response = UserDTO.class, responseContainer="List", 
			notes = "This operation accepts a parameter 'name' to filter the users by their names")
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "List of users successfully retrieved")
		})
	@GetMapping
	public List<UserDTO> getAllUsers(
			@ApiParam(value = "Filter users by name", required = false)
			@RequestParam(required = false) String name) {
		logger.info(" Get all users ");
		return userService
				.getAllUsers(name)
				.stream()
				.map(u -> userConverter.convertToUserDTO(u))
				.collect(Collectors.toList())
				;
	}
	
	@ApiOperation(value = "Retrieve the user corresponding with the ID passed", response = UserDTO.class)
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "User successfully retrieved"),
		    @ApiResponse(code = 404, message = "User is not found in the system")
		})
	@GetMapping("/{id}")
	public UserDTO getUser(
			@ApiParam(value = "ID of the user to retrieve", required = true)
			@PathVariable Integer id) {
		logger.info(" Get user id = " + id);
		return userConverter.convertToUserDTO(userService.getUserById(id));
	}
	
	@ApiOperation(value = "Create an user")
	@ApiResponses(value = {
		    @ApiResponse(code = 201, message = "User successfully retrieved"),
		    @ApiResponse(code = 400, message = "Bad request"),
		    @ApiResponse(code = 409, message = "The user is already found in the system")
		})
	@PostMapping
	public ResponseEntity<UserDTO> createUser(
			@ApiParam(value = "User object to store in database table", required = true)
			@Valid @RequestBody UserDTO userDTO) {
		logger.info(" Create user ");
		
		User user = userConverter.convertToUser(userDTO);
		UserDTO savedUser = userConverter.convertToUserDTO(userService.createUser(user));
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();
		
		return ResponseEntity
				.created(location)
				.build();
	}
	
	@ApiOperation(value = "Update an user")
	@ApiResponses(value = {
		    @ApiResponse(code = 204, message = "User successfully updated"),
		    @ApiResponse(code = 400, message = "Bad request"),
		    @ApiResponse(code = 404, message = "User is not found in the system")
		})
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateUser(
			@ApiParam(value = "User object to store in database table", required = true)
			@Valid @RequestBody UserDTO userDTO, 
			@ApiParam(value = "ID of the user to update", required = true)
			@PathVariable Integer id) {
		logger.info(" Update user with id = " + id);
		
		
		userService.updateUser(userConverter.convertToUser(userDTO), id);
		
		return ResponseEntity.noContent().build();
	}
	
	@ApiOperation(value = "Delete an user")
	@ApiResponses(value = {
		    @ApiResponse(code = 204, message = "User successfully deleted"),
		    @ApiResponse(code = 404, message = "User is not found in the system")
		})
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUser(
			@ApiParam(value = "ID of the user to delete", required = true)
			@PathVariable Integer id) {
		logger.info(" Delete user with id = " + id);
		
		userService.deleteUser(id);
		
		return ResponseEntity.noContent().build();
	}
}
