package support.ticket_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import support.ticket_platform.model.Operatore;

public interface OperatoreRepository extends JpaRepository<Operatore, Long> {
    
}
