package br.costa.DevJobs.infrastructure.persistence;



import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    Boolean existsByEmail(String email);
}
