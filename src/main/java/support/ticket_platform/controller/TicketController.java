package support.ticket_platform.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import support.ticket_platform.model.Categoria;
import support.ticket_platform.model.Nota;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.User;
import support.ticket_platform.service.CategoriaService;
import support.ticket_platform.service.NotaService;
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

    @Autowired
    private NotaService notaService;

    //GET PER LISTA DI TICKET
    @GetMapping("/tickets")
    public String listTickets(@RequestParam(value = "keyword", required = false) String keyword, Model model) {

        //RICERCA PER NOME
        List<Ticket> tickets;
        if (keyword != null && !keyword.isEmpty()) {
            tickets = ticketService.findByTitoloContainingIgnoreCase(keyword);
        } else {
            tickets = ticketService.findAll();
        }
        model.addAttribute("tickets", tickets);
        return "ticket/index";
    }

    //GET PER CREAZIONE
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

    //POST PER LA CREAZIONE
    @PostMapping("/ticket/create")
    public String create(@Valid @ModelAttribute("ticket") Ticket ticket, 
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

    //GET PER LETTURA TICKET IN MODIFICA
    @GetMapping("/ticket/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Ticket ticket = ticketService.findById(id);

        model.addAttribute("ticket", ticket);
        model.addAttribute("categorie", categoriaService.findAll());
        model.addAttribute("utentiDisponibili", userService.findByDisponibile(true));

        return "ticket/edit";
    }

    //POST PER MODIFICA TICKET
    @PostMapping("/ticket/edit/{id}")
    public String edit(@PathVariable Long id,
                            @Valid @ModelAttribute("ticket") Ticket ticket,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoriaService.findAll());
            model.addAttribute("utentiDisponibili", userService.findByDisponibile(true));
            return "ticket/edit";
        }

        // Recupera il ticket originale dal DB
        Ticket ticketCopia = ticketService.findById(id);

        // Aggiorna i campi modificabili
        ticketCopia.setTitolo(ticket.getTitolo());
        ticketCopia.setDescrizione(ticket.getDescrizione());

        // Recupera e imposta l'utente 
        User user = userService.findById(ticket.getUser().getId());
        ticketCopia.setUser(user);

        // Recupera e imposta la categoria
        Categoria categoria = categoriaService.findById(ticket.getCategoria().getId());
        ticketCopia.setCategoria(categoria);

        //copia lo stato
        if (ticket.getStato() == null) {
        ticket.setStato(Ticket.Stato.DA_FARE);
        }

        // Salva 
        ticketService.save(ticketCopia);

        return "redirect:/tickets";
    }

    //GET PER LETTURA DEL SINGOLO TICKET
    @GetMapping("/ticket/show/{id}")
    public String show(@PathVariable Long id, Model model) {

        Ticket ticket = ticketService.findById(id);
        List<Nota> note = notaService.findByTicketId(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("note", note);  // Aggiungi le note al modello
        return "ticket/show";
    }

    //POST PER CANCELLARE TICKET
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        
        //PER OGNI TICKET CANCELLO LE NOTE ASSOCIATE//
        Ticket ticket = ticketService.findById(id);
        for (Nota note : ticket.getNote()){
            notaService.deleteById(note.getId());
        }
        ticketService.deleteById(id);
        return "redirect:/tickets";
    }
    
}
