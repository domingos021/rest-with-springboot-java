package com.dinisjovete.restwithspringbootjava.config.seeds.person;

import java.util.List;

// ============================================================================
// LISTAS BASE PARA GERAR PESSOAS DE TESTE
// ============================================================================
// Só dados, sem lógica. Para variar mais os resultados, basta acrescentar itens.
// ATENÇÃO: alterar estas listas muda quem é o usuário de cada índice. Depois do
// primeiro seed, limpe os usuários de paginação antes de rodar de novo.
// ============================================================================
final class PersonSeedData {

    private PersonSeedData() {
    }

    static final List<String> FEMALE_FIRST_NAMES = List.of(
            "Alice", "Ana", "Beatriz", "Bianca", "Camila", "Carla", "Carolina",
            "Cecília", "Cláudia", "Daniela", "Débora", "Diana", "Eduarda", "Elisa",
            "Fernanda", "Gabriela", "Helena", "Inês", "Isabela", "Jéssica",
            "Joana", "Juliana", "Larissa", "Laura", "Letícia", "Luana", "Luísa",
            "Mafalda", "Marcela", "Mariana", "Marta", "Nádia", "Natália", "Patrícia",
            "Paula", "Priscila", "Rafaela", "Renata", "Rita", "Sandra", "Sara",
            "Sílvia", "Sofia", "Tatiana", "Teresa", "Thaís", "Vanessa", "Vera",
            "Vitória", "Yasmin"
    );

    static final List<String> MALE_FIRST_NAMES = List.of(
            "Alexandre", "André", "Bruno", "Caio", "Carlos", "César", "Daniel",
            "David", "Diego", "Eduardo", "Emanuel", "Fábio", "Felipe", "Fernando",
            "Filipe", "Gabriel", "Gustavo", "Henrique", "Hugo", "Igor", "Isaac",
            "João", "Jorge", "José", "Júlio", "Leandro", "Leonardo", "Lucas",
            "Luís", "Marcelo", "Marcos", "Mateus", "Miguel", "Murilo", "Nuno",
            "Otávio", "Paulo", "Pedro", "Rafael", "Ricardo", "Rodrigo", "Samuel",
            "Sérgio", "Tiago", "Thiago", "Vasco", "Vicente", "Vítor", "Wilson",
            "Zacarias"
    );

    static final List<String> LAST_NAMES = List.of(
            "Almeida", "Alves", "Andrade", "Araújo", "Azevedo", "Baptista", "Barbosa",
            "Barros", "Batista", "Bento", "Borges", "Cabral", "Campos", "Cardoso",
            "Carvalho", "Castro", "Cavalcanti", "Coelho", "Correia", "Costa",
            "Cunha", "Dias", "Domingos", "Duarte", "Fernandes", "Ferreira",
            "Fonseca", "Francisco", "Freitas", "Gomes", "Gonçalves", "Guimarães",
            "Henriques", "Lima", "Lopes", "Machado", "Manuel", "Marques", "Martins",
            "Matos", "Medeiros", "Melo", "Mendes", "Miranda", "Monteiro", "Moraes",
            "Moreira", "Moura", "Nascimento", "Neves", "Nunes", "Oliveira",
            "Pereira", "Pinto", "Pires", "Ramos", "Rangel", "Reis", "Ribeiro",
            "Rocha", "Rodrigues", "Sampaio", "Santos", "Sebastião", "Silva",
            "Siqueira", "Soares", "Souza", "Tavares", "Teixeira", "Viana", "Vieira",
            "Xavier"
    );

    // Formato igual ao dos usuários fixos: "Cidade - País".
    static final List<String> ADDRESSES = List.of(
            "Luanda - Angola", "Benguela - Angola", "Huambo - Angola", "Lubango - Angola",
            "São Paulo - Brasil", "Rio de Janeiro - Brasil", "Belo Horizonte - Brasil",
            "Curitiba - Brasil", "Salvador - Brasil", "Porto Alegre - Brasil",
            "Recife - Brasil", "Lisboa - Portugal", "Porto - Portugal",
            "Coimbra - Portugal", "Maputo - Moçambique", "Praia - Cabo Verde"
    );
}
