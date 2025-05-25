package support.ticket_platform.controller;

import java.security.Principal;
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
    @GetMapping("/note/create")                                    //CON MODEL PASSIAMO I DATI ALLA PAGINA HTML CON THYMELEAF
    public String create(@RequestParam("ticketId") Long ticketId, Model model) {

        //CREA UN OGGETTO NOTA VUOTO PER POPOLARE IL FORM E LO ASSOCIA AD UN TICKET
        Nota nota = new Nota();
        Ticket ticket = ticketService.findById(ticketId);
        nota.setTicket(ticket);

        //AGGIUNGONO I DATI DA PASSARE AL TEMPLATE THYMELEAF
        model.addAttribute("nota", nota);
        model.addAttribute("ticket", ticket);
        return "nota/create";
    }



    //POST PER FORM NOTE 
    //VALID, VALIDA I DATI PRESI DAL FORM
    //BINDING VERIFICA SE CI SONO ERRORI E MOSTRA UN MESSAGGIO NEL CAMPO ERRATO
    //PRINCIPAL PRENDE LE INFORMAZIONI DELL'UTENTE LOGGATO
    @PostMapping("/note/create")
    public String create(@Valid @ModelAttribute("nota") Nota nota,
                       BindingResult bindingResult,
                       Model model, Principal principal) {
        
        //SE CI SONO ERRORI MOSTRA IL MESSAGGIO SETTATO NELLA PAGINA HTML E RITORNA ALLA PAGINA DI CREAZIONE
        if (bindingResult.hasErrors()) {
            model.addAttribute("ticket",
                    ticketService.findById(nota.getTicket().getId()));
            return "nota/create";
        }

        // ASSOCIA IL TICKET ALLA NOTA
        Ticket ticket = ticketService.findById(nota.getTicket().getId());
        nota.setTicket(ticket);

        // ASSOCIA L'USER CHE STA CREANDO LA NOTA
        nota.setAutore(principal.getName());

        // IMPOSTA LA DATA DI CREAZIONE
        nota.setDataCreazione(LocalDateTime.now());

        // SALVA E REINDIRIZZA ALLA PAGINA DEL TICKET
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

    //GET PER LETTURA NOTE IN MODIFICA
    @GetMapping("note/edit/{id}")
    public String editNote(@PathVariable("id") Long id, Model model) {
        
        //RECUPERA LA NOTA CON L'ID
        Nota nota = notaService.findById(id);

        //PASSA IL MODELLO ALLA VIEW
        model.addAttribute("nota", nota);
        
        return "nota/edit";
    }

    @PostMapping("/note/update")
    public String updateNota(@Valid @ModelAttribute("nota") Nota nota,
                         BindingResult bindingResult,
                         Model model,
                         Principal principal) {

    // SE CI SONO ERRORI MOSTRA RITORNA ALLA PAGINA DI MODIFICA E MOSTRA ERRORE
    if (bindingResult.hasErrors()) {
        return "note/edit";
    }

    //RECUPERA IL TICKET ASSOCIATO E LO RIASSOCIA
    Ticket ticket = ticketService.findById(nota.getTicket().getId());
    nota.setTicket(ticket);

    //AGGIORNA AUTORE DELLA NOTA CON L'USER CHE LA STA MODIFICANDO
    nota.setAutore(principal.getName());
    
    //AGGIORNA LA DATA
    nota.setDataCreazione(LocalDateTime.now());

    //SALVA E RITORNA ALLA PAGINA DI DETTAGLIO DEI TICKET
    notaService.save(nota);

    return "redirect:/ticket/show/" + ticket.getId();
    }
    
}