package az.project.elibrary.repository;

import az.project.elibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsernameAndPasswordAndActive(String username, String password, Integer active);
    User findUserByIdAndTokenAndActive(Long id, String token, Integer active);

    User findUserByTokenAndActive(String toke, Integer active);

    User findUserByUsernameAndActive(String username, Integer active);

    User findUserByIdAndActive( Long id, Integer active);

}
