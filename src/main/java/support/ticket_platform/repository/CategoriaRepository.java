package support.ticket_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import support.ticket_platform.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
