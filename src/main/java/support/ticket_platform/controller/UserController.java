package support.ticket_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import support.ticket_platform.model.Ticket;
import support.ticket_platform.model.Ticket.Stato;
import support.ticket_platform.model.User;
import support.ticket_platform.service.TicketService;
import support.ticket_platform.service.UserService;



@Controller
public class UserController {

    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("utenti", userService.findAll());
        return "user/index";
    }

    @GetMapping("/user/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        
        User user = userService.findById(id);
        List<Ticket> ticketAssegnati = ticketService.findByUser(user);

        //FILTER TIENE CONTO SOLO DEI TICKET DELL'ESPRESIONE LAMBDA TRUE
        //COUNT CONTA QUANTI SONO DA FARE
        long countDaFare = ticketAssegnati.stream()
        .filter(t -> t.getStato() == Stato.DA_FARE)
        .count();

        long countInCorso = ticketAssegnati.stream()
        .filter(t -> t.getStato() == Stato.IN_CORSO)
        .count();

        model.addAttribute("user", userService.findById(id));
        model.addAttribute("ticketAssegnati", ticketAssegnati);
        model.addAttribute("countDaFare", countDaFare);
        model.addAttribute("countInCorso", countInCorso);
        return "user/show";
    }

    @PostMapping("/user/{id}/disponibilita")
    public String cambiaDisponibilita(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        User user = userService.findById(id);
        List<Ticket> ticketAssegnati = ticketService.findByUser(user);

        //STREAM: TRASFORMA LA LISTA IN FLUSSO DI ELEMENTI
        //ANYMATCH:SI COLLEGA A STREAM PER VERIFICARE SE C'è ALMENO UN ELEMENTO TRUE O FALSE
        //LAMBDA CHE ITERA OGNI TICKET PER VEDERE SE è COMPLETO
        boolean ticketIncompleti = ticketAssegnati.stream()
            .anyMatch(t -> t.getStato() != Stato.COMPLETATO);

        if (!ticketIncompleti) {
            user.setDisponibile(!user.isDisponibile());
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Disponibilità aggiornata con successo.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Non puoi cambiare disponibilità perché hai ticket non completati.");
        }

        return "redirect:/user/" + id;
    }
    
    
}
