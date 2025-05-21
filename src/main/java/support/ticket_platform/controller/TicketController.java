package support.ticket_platform.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import support.ticket_platform.model.Categoria;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.User;
import support.ticket_platform.service.CategoriaService;
import support.ticket_platform.service.TicketService;
import support.ticket_platform.service.UserService;

@Controller
public class TicketController {
    
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/tickets")
    public String index(Model model) {

        List<Ticket> listaTicket = ticketService.findAll();
        model.addAttribute("tickets", listaTicket);
        return "ticket/index";
    }

    @GetMapping("/ticket/create")
    public String create(Model model) {
   
        Ticket ticket = new Ticket();
        ticket.setUser(new User());
        ticket.setCategoria(new Categoria());

        model.addAttribute("ticket", ticket);
        model.addAttribute("categorie", categoriaService.findAll());
        model.addAttribute("utentiDisponibili", userService.findByDisponibile(true));
        return "ticket/create";
    }

    @PostMapping("/ticket/create")
    public String createTicket(@Valid @ModelAttribute("ticket") Ticket ticket, 
                                BindingResult bindingResult, Model model) {

    // Se ci sono errori di validazione torna al form con i dati necessari
    if (bindingResult.hasErrors()) {
        model.addAttribute("categorie", categoriaService.findAll());
        model.addAttribute("utentiDisponibili", userService.findByDisponibile(true));
        return "ticket/create";
    }

    // Recupera l'utente completo dal DB tramite id selezionato
    User user = userService.findById(ticket.getUser().getId());
    ticket.setUser(user);

    // Recupera la categoria completa dal DB tramite id selezionato
    Categoria categoria = categoriaService.findById(ticket.getCategoria().getId());
    ticket.setCategoria(categoria);

    // Imposta la data di creazione con l'ora corrente
    ticket.setDataCreazione(LocalDateTime.now());

    // Imposta lo stato di default (es. DA_FARE) se non è stato già settato
    if (ticket.getStato() == null) {
        ticket.setStato(Ticket.Stato.DA_FARE);
    }

    // Salva il ticket
    ticketService.save(ticket);

    // Redirect alla lista ticket dopo il salvataggio
    return "redirect:/tickets";
}
}
