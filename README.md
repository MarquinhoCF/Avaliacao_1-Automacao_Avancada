<h1 align="center"> Avaliação 1 - Automação Avançada (GAT 108) </h1>

Este é o repositório para o projeto da AV1 da disciplina de Automação Avançada. O projeto consiste em simular um ambiente de mobilidade urbana com várias entidades interagindo entre si.

### Autor

Marcos Carvalho Ferreira

Matrícula: 202010203

Email: marcos.ferreira8@estudante.ufla.br

![image](https://github.com/MarquinhoCF/Avaliacao_1-Automacao_Avancada/blob/main/assets/Ilustracao_AV1.png)

## Conteúdo

- [Instruções Gerais](#instruções-gerais)
- [Mobility Company](#mobility-company)
- [Alpha Bank e Account](#alpha-bank-e-account)
- [Driver](#driver)
- [Car](#car)
- [Fuel Station](#fuel-station)
- [Geração de Relatórios e Gráficos](#geração-de-relatórios-e-gráficos)
- [Requisitos de Execução](#requisitos-de-execução)

## Instruções Gerais

O projeto é composto por diversas partes que envolvem a criação de várias classes e entidades para simular um ambiente de mobilidade. Certifique-se de seguir as instruções e requisitos gerais fornecidos pelo professor no início da descrição da AV1.

## Mobility Company

A Mobility Company é a entidade principal que gerencia rotas e realiza pagamentos aos motoristas. Nesta parte do projeto, você deverá implementar a classe Mobility Company como um servidor e criar rotas para serem executadas.

## Alpha Bank e Account

O Alpha Bank é responsável por lidar com as transações financeiras entre a empresa, motoristas e postos de gasolina. Você precisará criar a classe Alpha Bank e implementar a lógica de contas correntes para a empresa, motoristas e postos de gasolina.

## Driver

Os motoristas prestam serviços à empresa, executando rotas designadas. Nesta parte, você criará a classe Driver e implementará a lógica para lidar com rotas e pagamentos.

## Car

Os carros são usados pelos motoristas para executar as rotas. Nesta parte, você criará a classe Car, lidará com o abastecimento e coletará dados para relatórios.

## Fuel Station

Os postos de gasolina permitem que os carros se abasteçam. Crie a classe Fuel Station, gerencie o abastecimento e lide com os pagamentos dos motoristas.

## Geração de Relatórios e Gráficos

Criação de relatórios gerenciais em Excel e gráficos em tempo real para visualizar dados importantes do ambiente de mobilidade.

## Requisitos do Projeto

Para executar este projeto de simulação de mobilidade urbana, você precisará configurar seu ambiente corretamente. Siga estas etapas para preparar seu sistema:

### Instalação de Ferramentas Necessárias

Antes de começar, certifique-se de ter instalado as seguintes ferramentas:

1. **SUMO:** Você precisa instalar o SUMO, uma ferramenta de simulação de tráfego. Baixe e instale a versão mais recente do SUMO no [site oficial](https://sumo.dlr.de/docs/index.html).

2. **Maven:** O Maven é uma ferramenta de automação de compilação e gerenciamento de dependências para Java. Você pode baixar e instalar o Maven do [site oficial](https://maven.apache.org/).

3. **Visual Studio Code (VSCode):** Um ambiente de desenvolvimento de código aberto. Você pode baixar e instalar o VSCode do [site oficial](https://code.visualstudio.com/download).

### Configurando as Dependências do Projeto

Após instalar o SUMO, o Maven e o VSCode, você precisará configurar as dependências do projeto. Siga estas etapas para fazer isso:

1. **Abra um Terminal:** Abra um terminal no seu sistema operacional.

2. **Instale as Dependências com o Maven:** No terminal, navegue até o diretório do seu projeto e execute os seguintes comandos Maven para instalar as dependências listadas no arquivo `Pom.xml`. Por exemplo:

   ```bash
   mvn install:install-file -Dfile="SEUCAMINHO\vscode-workspace\sim\lib\libsumo-1.18.0.jar" -DgroupId="libsumo-1.18.0" -DartifactId="libsumo-1.18.0" -Dversion="libsumo-1.18.0" -Dpackaging="jar" -DgeneratePom=true
   ```

   Certifique-se de substituir `"SEUCAMINHO"` pelo caminho real no qual você baixou o projeto.

3. **Realize as Alterações Necessárias no Código Fonte:** No diretório do seu projeto, navegue até `vscode-workspace\sim\src\main\java\sim\traci4j` e faça as alterações necessárias no código fonte para se conectar corretamente ao SUMO. Certifique-se de seguir as orientações fornecidas na documentação, especialmente para a classe `Vehicle`.

   Importante: Ao atuar em um veículo, tenha cuidado, pois é possível alterar as rotas dos veículos. Se uma rota reprogramada for inviável, o veículo será "teletransportado" para fora da simulação antes de atingir a rota final.

   Tenha em mente que ao alterar a velocidade de um veículo, o SUMO assume que você está controlando o veículo e manterá a velocidade até que você comande outra alteração ou devolva o controle ao SUMO (definindo a velocidade como -1). Isso pode potencialmente causar acidentes, e o SUMO está preparado para lidar com situações como acidentes e congestionamentos que podem afetar as vias e criar atrasos no tráfego, assim como em situações reais.