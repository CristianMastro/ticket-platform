package support.ticket_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import support.ticket_platform.model.Nota;
import support.ticket_platform.model.Ticket;

public interface NotaRepository extends JpaRepository<Nota, Long>{

    List<Nota> findByTicket(Ticket ticket);
}
