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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import support.ticket_platform.model.Categoria;
import support.ticket_platform.model.Nota;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.Ticket.Stato;
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
    @GetMapping("/tickets")     //RICEVE UN PARAMETRO DALLA QUERY STRING PER FILTRARE TICKET, NON + OBBLIGATORIO
    public String listTickets(@RequestParam(value = "keyword", required = false) String keyword, Model model) {

        //RICERCA PER NOME TRAMITE IL METODO
        List<Ticket> tickets;

        //SE IL TITOLO CONTIENE LA KEYWORD TROVA TUTTI I TICKET CON QUEL NOME ALTRIMENTI PRENDE TUTTI
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


        //CREA UN TICKET VUOTO CON USER E CATEGORIE PER EVITARE ERRORI NEL FORM
        Ticket ticket = new Ticket();
        ticket.setUser(new User());
        ticket.setCategoria(new Categoria());

        //RECUPERA GLI UTENTI CHE SONO DISPONIBILI
        //E POI FILTRA GLI UTENTI PER QUELLI CHE NON SONO ADMIN
        List<User> utentiDisponibili = userService.findByDisponibile(true).stream()
        .filter(user -> user.getRoles().stream()
            .noneMatch(role -> role.getName().equals("ADMIN")))
        .toList();

        //AGGIUNGE AL MODELLO I DATI PER IL FORM
        model.addAttribute("ticket", ticket);
        model.addAttribute("categorie", categoriaService.findAll());
        model.addAttribute("utentiDisponibili", utentiDisponibili);
        return "ticket/create";
    }

    //POST PER LA CREAZIONE
    @PostMapping("/ticket/create")
    public String create(@Valid @ModelAttribute("ticket") Ticket ticket, 
                                BindingResult bindingResult, Model model) {

        //CONTROLLA GLI ERRORI SUL FORM AL MOMENTO DELLA POST E MOSTRA UN ERRORE RICARICANDO LA PAGINA DI CREAZIONE
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoriaService.findAll());
            model.addAttribute("utentiDisponibili", userService.findByDisponibile(true));
            return "ticket/create";
        }

        //CONTROLLA SE CI SONO UTENTI DISPONIBILI E MODELLA UN MESSAGGIO DI ERRORE AL MOMENTO DI INVIO DEL FORM
        List<User> utentiDisponibili = userService.findByDisponibile(true);
        if (utentiDisponibili.isEmpty()) {
            model.addAttribute("errorMessage", "Nessun operatore disponibile al momento. Impossibile creare il ticket.");
            return "ticket/create";
        }

        //RECUPERA L'UTENTE DAL DB
        User user = userService.findById(ticket.getUser().getId());
        ticket.setUser(user);

        //RECUPERA LA CATEGORIA
        Categoria categoria = categoriaService.findById(ticket.getCategoria().getId());
        ticket.setCategoria(categoria);

        //IMPOSTA DATA
        ticket.setDataCreazione(LocalDateTime.now());

        //IMPOSTA LO STATO DA FARE PER OGNI TICKET CREATO
        if (ticket.getStato() == null) {
            ticket.setStato(Ticket.Stato.DA_FARE);
        }

        //SALVA
        ticketService.save(ticket);

        //RITORNA ALLA PAGINA DEI TICKET
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
                           Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoriaService.findAll());
            model.addAttribute("utentiDisponibili", userService.findByDisponibile(true));
            return "ticket/edit";
        }

        //RECUPERA IL TICKET DAL DB
        Ticket ticketCopia = ticketService.findById(id);

        //SE LO STATO DEL TICKET E' COMPLETATO NON FA MODIFICARE E MOSTRA UN MESSAGGIO D'ERRORE
        if (ticketCopia.getStato() == Ticket.Stato.COMPLETATO) {
            redirectAttributes.addFlashAttribute("errorMessage","Il ticket è già completato e non può essere modificato.");
            return "redirect:/tickets";
        }

        //AGGIORNA TITOLO E DESCRIZIONE
        ticketCopia.setTitolo(ticket.getTitolo());
        ticketCopia.setDescrizione(ticket.getDescrizione());

        //RECUPERA E REIMPOSTA L'UTENTE
        User user = userService.findById(ticket.getUser().getId());
        ticketCopia.setUser(user);

        //RECUPERA LE CATEGORIA E LA REIMPOSTA
        Categoria categoria = categoriaService.findById(ticket.getCategoria().getId());
        ticketCopia.setCategoria(categoria);

        //SALVA
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
        model.addAttribute("stati", Stato.values()); //Aggiungi gli stati al modello
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

    @PostMapping("/ticket/stato/{id}")
    public String updateStato(@PathVariable Long id, @RequestParam("stato") Stato stato) {
        
        //RECUPERA IL TICKET E LO SALVA
        Ticket ticket = ticketService.findById(id);
        ticket.setStato(stato);
        ticketService.save(ticket);
        return "redirect:/ticket/show/" + id;  // torna sempre al dettaglio aggiornato
    }

}
