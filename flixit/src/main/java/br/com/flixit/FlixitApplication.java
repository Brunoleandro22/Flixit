package br.com.flixit;

import br.com.flixit.repository.EpisodioRepository;
import br.com.flixit.repository.SerieRepository;
import br.com.flixit.Principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class FlixitApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FlixitApplication.class, args);
	}

	@Autowired
	private SerieRepository repositorio;

	@Autowired
	private EpisodioRepository episodioRepository;

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorio, episodioRepository);
		principal.exibeMenu();
	}
}
