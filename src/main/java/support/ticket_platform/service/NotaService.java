package support.ticket_platform.service;

import java.util.List;

import support.ticket_platform.model.Nota;

public interface NotaService {

    Nota save(Nota nota);
    Nota findById(Long id);
    List<Nota> findByTicketId(Long ticketId);
    void deleteById(Long id);
}
