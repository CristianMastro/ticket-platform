package support.ticket_platform.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import support.ticket_platform.model.Categoria;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.User;
import support.ticket_platform.repository.TicketRepository;
import support.ticket_platform.service.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket con id " + id + " non trovato"));
    }

    @Override
    public List<Ticket> findByUser(User user) {
        return ticketRepository.findByUser(user);
    }

    @Override
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> findByCategoriaId(Categoria categoria) {
        return ticketRepository.findByCategoria(categoria);
    }

    @Override
    public List<Ticket> findByStato(String stato) {
        return ticketRepository.findByStato(stato);
    }

    @Override
    public List<Ticket> findByTitoloContainingIgnoreCase(String keyword) {
        return ticketRepository.findByTitoloContainingIgnoreCase(keyword);
    }
}
