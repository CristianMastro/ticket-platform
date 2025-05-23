package support.ticket_platform.controller;

import java.time.LocalDateTime;

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
import support.ticket_platform.model.Nota;
import support.ticket_platform.model.Ticket;
import support.ticket_platform.service.NotaService;
import support.ticket_platform.service.TicketService;

@Controller
public class NotaController {

    @Autowired
    private NotaService notaService;

    @Autowired
    private TicketService ticketService;

    //GET PER FORM NOTE
    @GetMapping("/note/create")
    public String create(@RequestParam("ticketId") Long ticketId, Model model) {

        Nota nota = new Nota();
        Ticket ticket = ticketService.findById(ticketId);
        nota.setTicket(ticket);
        model.addAttribute("nota", nota);
        model.addAttribute("ticket", ticket);
        return "nota/create";
    }

    //POST PER FORM NOTE (DA SISTEMARE QUANDO IMPLEMENTO SECURITY)
    @PostMapping("/note/create")
    public String create(@Valid @ModelAttribute("nota") Nota nota,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("ticket",
                    ticketService.findById(nota.getTicket().getId()));
            return "nota/create";
        }

        // recupera ticket
        Ticket ticket = ticketService.findById(nota.getTicket().getId());
        nota.setTicket(ticket);

        // autore default prima della security
        nota.setAutore("SYSTEM");

        // imposta la data
        nota.setDataCreazione(LocalDateTime.now());

        // salvataggio
        notaService.save(nota);
        return "redirect:/ticket/show/" + ticket.getId();
    }

    //POST PER CANCELLARE NOTE
    @PostMapping("/note/delete/{id}")
        public String delete(@PathVariable("id") Long id) {

            Nota nota = notaService.findById(id);
            Long ticketId = nota.getTicket().getId();
            notaService.deleteById(id);
            return "redirect:/ticket/show/" + ticketId;
        }
}