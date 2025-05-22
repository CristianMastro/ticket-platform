package support.ticket_platform.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import support.ticket_platform.model.Nota;
import support.ticket_platform.repository.NotaRepository;
import support.ticket_platform.service.NotaService;

@Service
public class NotaServiceImpl implements NotaService {
    
    private final NotaRepository notaRepository;

    public NotaServiceImpl(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }

    @Override
    public Nota save(Nota nota) {
        return notaRepository.save(nota);
    }

    @Override
    public Nota findById(Long id) {
        return notaRepository.findById(id).orElseThrow(() -> new RuntimeException("Nota non trovata"));
    }

    @Override
    public List<Nota> findByTicketId(Long ticketId) {
        return notaRepository.findByTicketId(ticketId);
    }

    @Override
    public void deleteById(Long id) {
        notaRepository.deleteById(id);
    }
}
