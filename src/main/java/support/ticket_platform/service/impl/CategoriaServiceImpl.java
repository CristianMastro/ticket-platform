package support.ticket_platform.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import support.ticket_platform.model.Categoria;
import support.ticket_platform.repository.CategoriaRepository;
import support.ticket_platform.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    
    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }
}
