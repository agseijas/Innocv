package es.innocv.uidex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.innocv.uidex.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

	List<User> findByNameContainsIgnoreCase(String name);
}
