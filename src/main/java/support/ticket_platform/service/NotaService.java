package support.ticket_platform.service;

import java.util.List;

import support.ticket_platform.model.Nota;
import support.ticket_platform.model.Ticket;

public interface NotaService {
    
    Nota save(Nota nota);
    List<Nota> findByTicket(Ticket ticket);
    void deleteById(Long id);
}
