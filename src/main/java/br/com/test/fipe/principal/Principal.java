package br.com.test.fipe.principal;

import br.com.test.fipe.model.DadosVeiculo;
import br.com.test.fipe.model.Modelos;
import br.com.test.fipe.model.Veiculo;
import br.com.test.fipe.service.ConsumoAPI;
import br.com.test.fipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private final String MARCAS = "/marcas";
    private final String MODELOS = "/modelos";
    private final String ANOS = "/anos";
    private String endereco;

    public void exibeMenu() {
        // Pergunta inicial com bordas e instruções claras
        System.out.println("======================================");
        System.out.println("|  Bem-vindo ao Sistema de Consulta  |");
        System.out.println("======================================");
        System.out.println("Escolha o tipo de veículo para consultar:");
        System.out.println("[1] Carro");
        System.out.println("[2] Moto");
        System.out.println("[3] Caminhão");
        System.out.print("Digite o número correspondente: ");

        String tipoVeiculoInput = leitura.nextLine().toLowerCase();
        String tipoVeiculo;

        switch (tipoVeiculoInput) {
            case "1":
            case "carro":
                tipoVeiculo = "carro";
                break;
            case "2":
            case "moto":
                tipoVeiculo = "moto";
                break;
            case "3":
            case "caminhao":
                tipoVeiculo = "caminhao";
                break;
            default:
                System.out.println("Opção inválida! Por favor, escolha uma opção válida.");
                return;
        }

        Map<String, String> veiculoMap = Map.of(
                "carro", "carros",
                "moto", "motos",
                "caminhao", "caminhoes"
        );

        String tipoVeiculoFormatado = veiculoMap.get(tipoVeiculo);

        endereco = URL_BASE + tipoVeiculoFormatado + MARCAS;
        String json = consumo.obterDados(endereco);

        var marcaLista = conversor.obterLista(json, DadosVeiculo.class);

        // Separador visual e instruções para pesquisar marcas
        System.out.println("\n--------------------------------------");
        System.out.println("Digite um trecho do nome da marca a ser buscada:");
        System.out.print("> ");
        String trechoMarca = leitura.nextLine();

        List<DadosVeiculo> marcasFiltradas = marcaLista.stream()
                .filter(m -> m.nome().toLowerCase().contains(trechoMarca.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nMarcas encontradas:");
        marcasFiltradas.forEach(System.out::println);

        if (marcasFiltradas.isEmpty()) {
            System.out.println("Nenhuma marca encontrada para o trecho fornecido.");
            return;
        }

        // Solicitação de entrada do código da marca com instruções claras
        System.out.println("\n--------------------------------------");
        System.out.println("Digite o código da marca desejada:");
        System.out.print("> ");
        String codigoMarca = leitura.nextLine();

        endereco = URL_BASE + tipoVeiculoFormatado + MARCAS + "/" + codigoMarca + MODELOS;
        json = consumo.obterDados(endereco);

        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos disponíveis da marca:");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(DadosVeiculo::codigo))
                .forEach(System.out::println);

        // Solicitação de entrada para pesquisar modelos
        System.out.println("\n--------------------------------------");
        System.out.println("Digite um trecho do nome do modelo a ser buscado:");
        System.out.print("> ");
        String trechoModelo = leitura.nextLine();

        List<DadosVeiculo> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(trechoModelo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados:");
        modelosFiltrados.forEach(System.out::println);

        if (modelosFiltrados.isEmpty()) {
            System.out.println("Nenhum modelo encontrado para o trecho fornecido.");
            return;
        }

        // Solicitação de entrada do código do modelo
        System.out.println("\n--------------------------------------");
        System.out.println("Digite o código do modelo para buscar os anos disponíveis:");
        System.out.print("> ");
        String codigoModelo = leitura.nextLine();

        endereco = URL_BASE + tipoVeiculoFormatado + MARCAS + "/" + codigoMarca + MODELOS + "/" + codigoModelo + ANOS;
        json = consumo.obterDados(endereco);
        List<DadosVeiculo> anos = conversor.obterLista(json, DadosVeiculo.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (DadosVeiculo ano : anos) {
            var enderecoAnos = URL_BASE + tipoVeiculoFormatado + MARCAS + "/" + codigoMarca + MODELOS + "/" + codigoModelo + ANOS + "/" + ano.codigo();
            json = consumo.obterDados(enderecoAnos);
            var veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n--------------------------------------");
        System.out.println("Detalhes dos veículos:");
        veiculos.forEach(System.out::println);
        System.out.println("--------------------------------------");
    }
}
