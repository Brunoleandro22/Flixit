package br.com.flixit.Principal;

import br.com.flixit.model.*;
import br.com.flixit.repository.EpisodioRepository;
import br.com.flixit.repository.SerieRepository;
import br.com.flixit.service.ConsumoApi;
import br.com.flixit.service.ConverteDados;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private EpisodioRepository episodioRepository;
    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio, EpisodioRepository episodioRepository) {
        this.repositorio = repositorio;
        this.episodioRepository = episodioRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar série por ator
                    6 - Buscar as 5 mais bem avaliadas
                    7 - Buscar por gênero
                    8 - Filtrar Séries
                    9 - Buscar episódio por trecho
                    10 - Buscar melhores episódios
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1 -> buscarSerieWeb();
                case 2 -> buscarEpisodioPorSerie();
                case 3 -> listarSeriesBuscadas();
                case 4 -> buscarSeriePorTitulo();
                case 5 -> buscarSeriePorAtor();
                case 6 -> buscarTop5Series();
                case 7 -> buscarSeriesPorCategoria();
                case 8 -> filtrarSeriesPorTemporadaEAvaliacao();
                case 9 -> buscarEpisodioPorTrecho();
                case 10 -> topEpisodiosPorSerie();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);

        if (!repositorio.existsByTitulo(serie.getTitulo())) {
            repositorio.save(serie);
        }
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();

            // Verifica se já existem episódios associados no banco
            List<Episodio> episodiosSalvos = episodioRepository.findBySerie(serieEncontrada);

            if (!episodiosSalvos.isEmpty()) {
                System.out.println("Episódios já cadastrados:");
            } else {
                System.out.println("Buscando episódios na API...");

                List<DadosTemporada> temporadas = new ArrayList<>();

                for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                    var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                    DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                    temporadas.add(dadosTemporada);
                }

                List<Episodio> episodios = temporadas.stream()
                        .flatMap(temp -> temp.episodios().stream()
                                .map(e -> new Episodio(temp.numero(), e)))
                        .collect(Collectors.toList());

                serieEncontrada.setEpisodios(episodios);
                repositorio.save(serieEncontrada); // salva com episódios
                episodiosSalvos = episodios;
            }

            // Mostra temporadas e número de episódios
            Map<Integer, List<Episodio>> agrupadosPorTemporada = episodiosSalvos.stream()
                    .collect(Collectors.groupingBy(Episodio::getTemporada));

            System.out.println("\nTemporadas disponíveis:");
            agrupadosPorTemporada.keySet().stream().sorted()
                    .forEach(temp -> System.out.println("Temporada " + temp + " - " + agrupadosPorTemporada.get(temp).size() + " episódios"));
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome:");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Deseja ver a série de qual ator?");
        var nomeAtor = leitura.nextLine();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCase(nomeAtor);

        if (seriesEncontradas.isEmpty()) {
            System.out.println("Nenhuma série desse(a) ator/atriz cadastrada.");
        } else {
            System.out.println("Séries em que o(a) ator/atriz " + nomeAtor + " trabalhou:");
            seriesEncontradas.forEach(s -> System.out.println("Série: " + s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
        }
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> System.out.println("Série: " + s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Qual categoria deseja? ");
        var nomeCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria " + nomeCategoria);
        seriesPorCategoria.forEach(System.out::println);
    }
    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas?");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Avaliação a partir de qual valor?");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacoes( totalTemporadas,  avaliacao);
        System.out.println("*** Séries Fitradas ***");
        filtroSeries.forEach(s -> System.out.println( s.getTitulo() + "- avaliação:" + s.getAvaliacao()));
    }
    private void buscarEpisodioPorTrecho(){
        System.out.println("Nome do Episódio:");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Serie: %s\nTemporada: %s\nEpisodio %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(),
                e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = episodioRepository.findTop5BySerieOrderByAvaliacaoDesc(serie);

            if (topEpisodios.isEmpty()) {
                System.out.println("Essa série ainda não tem episódios cadastrados.");
            } else {
                topEpisodios.forEach(e -> System.out.printf("Série: %s\nTemporada: %s\nEpisódio %s - %s\n\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
            }
        } else {
            System.out.println("Série não encontrada.");
        }
    }
}

