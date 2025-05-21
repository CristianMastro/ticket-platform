package support.ticket_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import support.ticket_platform.model.Ticket;
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
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("ticketAssegnati", ticketAssegnati);
        return "user/show";
    }
    
    
}
