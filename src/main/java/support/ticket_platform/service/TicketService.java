package support.ticket_platform.service;

import java.util.List;

import support.ticket_platform.model.Categoria;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.User;

public interface TicketService {

    List<Ticket> findAll();
    Ticket findById(Long id);
    Ticket save(Ticket ticket);
    void deleteById(Long id);
    List<Ticket> findByUser(User user);
    List<Ticket> findByCategoriaId(Categoria categoriaId);
    List<Ticket> findByStato(String stato);
    List<Ticket> findByTitoloContainingIgnoreCase(String keyword);
}
