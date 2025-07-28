package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.service.ConsumoApiService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {
        Scanner sc = new Scanner(System.in);
        private ConsumoApiService consumoApi = new ConsumoApiService();

        private final String END = "https://www.omdbapi.com/?t=";
        private final String API_KEY = "&apikey=6585022c";
        private ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){
        System.out.println("Digite o nome da série para buscar: ");
        var serie = sc.nextLine();
        var json = consumoApi.obterDados(END+ serie.replace(" ","+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

		for(int i = 1; i<=dados.totalTemporadas(); i++){
			json = consumoApi.obterDados(END+ serie.replace(" ","+")+"&season=" +i+ API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);
//
//        for(int i = 0; i<dados.totalTemporadas(); i++){
//            List<DadosEpisodio> epTemp = temporadas.get(i).episodios();
//            temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//            temporadas.forEach(System.out::println);
//
//        }
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<String> nomes = Arrays.asList("Jacques","Iasmin","Filipe","Jorge","Paulo","Rodrigo","Nico");
//        nomes.stream()
//                .sorted()
//                .limit(3)
//                .filter(n-> n.startsWith("I"))
//                .map(String::toUpperCase)
//                .forEach(System.out::println);

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\n Top 5 Episódios: ");
        dadosEpisodios.stream().filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()).limit(5).forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());;

    episodios.forEach(System.out::println);
    }
}
