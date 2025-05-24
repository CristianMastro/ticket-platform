package support.ticket_platform.controller.api;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import support.ticket_platform.model.Ticket;
import support.ticket_platform.service.CategoriaService;
import support.ticket_platform.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {
    
    @Autowired
    private TicketService ticketService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Ticket> getTickets() {

        return ticketService.findAll();
    }

    @GetMapping("/categoria")
    public List<Ticket> getTicketsByCategoria(@RequestParam String categoria) {

        //RECUPERA TUTTE LE CATEGORIE
        List<String> categorieValide = categoriaService.findAll()
            .stream()
            .map(c -> c.getTipo().toLowerCase()) 
            .collect(Collectors.toList());

        // CONTROLLA SE LA STRINGA INSERITA è VALIDA
        if (!categorieValide.contains(categoria.toLowerCase())) {
            throw new IllegalArgumentException("Categoria non valida: " + categoria +
                ". Categorie disponibili: " + String.join(", ", categorieValide));
        }

        // RITORNA UNA LISTA DELLA CATEGORIA SCELTA
        return ticketService.findAll().stream()
            .filter(t -> t.getCategoria().getTipo().equalsIgnoreCase(categoria))
            .collect(Collectors.toList());
    }

    @GetMapping("/stato")
    public List<Ticket> getTicketsByStato(@RequestParam String stato) {
        
        try {
            //CONTROLLA LA STRINGA E RITORNA LA LISTA DEI TICKET CON STATO SCELTO
            Ticket.Stato statoEnum = Ticket.Stato.valueOf(stato.toUpperCase());
            return ticketService.findAll().stream()
                    .filter(t -> t.getStato() == statoEnum)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Stato non valido: " + stato + ". Stati diponibili: da_fare, in_corso o completato.");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidParam(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body("Errore: " + ex.getMessage());
    }

}
