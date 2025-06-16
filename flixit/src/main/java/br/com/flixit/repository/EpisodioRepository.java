package br.com.flixit.repository;

import br.com.flixit.model.Episodio;
import br.com.flixit.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodioRepository extends JpaRepository<Episodio, Integer> {
    List<Episodio> findBySerie(Serie serieEncontrada);
}

