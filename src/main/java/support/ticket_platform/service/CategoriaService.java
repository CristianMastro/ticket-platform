package support.ticket_platform.service;

import java.util.List;

import support.ticket_platform.model.Categoria;

public interface CategoriaService {
    
    List<Categoria> findAll();
    Categoria findById(Long id);
}
