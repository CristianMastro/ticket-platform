package support.ticket_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import support.ticket_platform.model.Categoria;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.User;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCategoria(Categoria categoria);

    List<Ticket> findByStato(String stato);

    List<Ticket> findByUser(User user);

    List<Ticket> findByTitoloContainingIgnoreCase(String titolo);
}
