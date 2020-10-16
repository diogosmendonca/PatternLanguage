# PatternLanguage

## Requisitos para usar a ferramenta
Para executar a ferramenta é necessário possuir o ambiente de execução Java (JRE) na versão igual ou superior a 13.0.1. Além disso, deve-se ter conhecimento básico de programação Java, para realizar a programação dos padrões e utilizar todas as suas funcionalidades.

## CLI (Command User Interface) - Prompt de Comando

O CLI é a interface utilizada pela ferramenta para interagir com o usuário, onde o mesmo através de comandos executa as ações desejadas no sistema.  
A ferramenta deve ser executada via Prompt de Comando passando os comandos abaixo :

```
java -jar scpl.jar <comandos_CLI>
```

  - "java" faz referência ao JRE(caso não esteja no path do sistema operacional, é necessário especificar o caminho completo do arquivo)
  - "scpl.jar" representa a aplicação já compilada 
  - “<comandos_CLI>” representa os comandos que serão passados para o sistema interpretar e executar ações. 

### Comandos CLI
  - Search
    ``` 
    java -jar scpl.jar search -code ./CaminhoCodigoAlvoDaBusca -pattern ./CaminhoCodigosDosPadroes
    ```

  - Debug
   
    ``` 
    java -jar scpl.jar search -code ./CaminhoDoCodigoAlvoDaBusca -pattern ./CaminhoComCodigosDosPadroes
    ```
  
  - Version
   
    ```
    java -jar scpl.jar version 
    ```

  - Help
   
    ```
    java -jar scpl.jar help
    ```

  - Verbose
  
    ```
    java -jar scpl.jar verbose
    ```
