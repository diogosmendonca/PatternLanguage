# Source Code Pattern Language(SCPL)

## 1. O que é?

A Source Code Pattern Language é uma linguagem para localização de padrões em código-fonte, e tem como proposta facilitar este processo, provendo uma abstração amigável e simples para o usuário.

## 2. Funcionalidades

Seguindo a proposta da linguagem de simplificar a programação de padrões, a forma de escrita dos padrões da SCPL é baseada na escrita do código-fonte do próprio defeito. Contudo, existem informações dos defeitos que variam entre suas instâncias e precisam ser abstraídas
(nomes, parâmetros, valores e etc), ou até mesmo o defeito pode ser definido pela presença de um trecho de código com a ausência de outro. 

Para viabilizar estes casos, algumas modificações na escrita do código são necessárias, para isso funcionalidades adicionais foram implementadas, são elas: 

1. Wildcards
1. Padrões de bloco  
1. Operador Exists
1. Agrupamento de Padrões por Pastas

A seguir vai explicar cada um deles.

### 2.1. Wildcards

Os wildcards, servem como uma palavra abstrata que pode ser usada em certas partes do código do padrão de modo a generalizar a sua busca. Por exemplo, ela pode abstrair nome de
variáveis, nome de métodos, nome de parâmetros e etc. A seguir temos a lista de wildcards disponíveis para o usuário.

| Wildcard | Tipo de Nó |
| --- | --- |
| any | Qualquer um |
| some | Um específico que se repete no padrão  |

Todos os wildcards são prefixos e podem vir seguidos por um sufixo que auxilie na leitura do padrão. O “any” faz referências para qualquer um valor, logo toda comparação que o envolve retorna verdadeiro. O “some” serve para guardar a referência de um nó específico durante a comparação de nós, caso exista mais que um “some” no padrão então ele faz a referência ao mesmo nó. No caso do “some” que faz referência a ocorrências passadas, caso existam referências a mais de uma variável é indispensável o uso do sufixo, pois, é a forma com que a aplicação distingue uma variável de outra.

Olhando o código-fonte 1, na linha 1 e 2 existe um defeito, que ocasiona uma exceção do tipo NullPointerException. A escrita desse defeito utilizando o SCPL pode ser feita copiando exatamente o defeito(código-fonte 2). Entretanto, dessa forma a busca será feita apenas para variáveis com o nome “num” e para o método “toString()”. Outras ocorrências equivalentes do mesmo defeito podem existir, porém, com variáveis e métodos distintos, como no código-fonte 1 (linhas 2-6). Sem o uso dos wildcards o padrão é muito particular e não é possível localizar outras instâncias que fogem um pouco do padrão escrito.

##### Código-Fonte 1: Exemplo de código-fonte para realizar uma busca
```java
1 Integer num = null ;
2 num.toString() ;
3 Integer val = null ;
4 Integer x = null ;
5 x.toString() ;
6 val.hashCode() ;
```

##### Código-Fonte 2: Exemplo de padrão sem utilizar wildcards
```java
1 Integer num = null ;
2 num.toString() ;
```

Usando os wildcards para esses casos, o padrão é definido no código-fonte 3. O “someVariable” é aplicado para guardar a referência a qualquer objeto declarado com valor nulo que em seguida invoca um método e o “anyMethod” para abstrair e indicar que não importa o nome do método. Este novo padrão possibilita ampliar a busca, permitindo que as outras ocorrências do defeito presentes no código-fonte 1 sejam encontradas.

##### Código-Fonte 3: Exemplo de padrão utilizando wildcards
```java
1 Integer someVariable = null ;
2 someVariable.anyMethod() ;

```

### 2.2. Padrões de bloco

O padrão de bloco é um bloco de código com início e fim, que possui o defeito que deseja-se procurar, é equivalente ao conceito de bloco presente nas linguagens de programação. Com esta funcionalidade é possível, por exemplo, utilizar os seguintes blocos: controle de fluxo (“if else”), estruturas de repetição (“for” e “while”), tratamento de exceções (“try and catch”) entre outros. 

O código-fonte 4, apresenta um padrão de bloco “while” no caso de um loop infinito causado por uma variável, representada pelo wildcard “someVar”, que só aumenta com o tempo, o que representa um defeito.

##### Código-Fonte 4: Exemplo de uso do padrão de bloco
```java
1 while ( someVariable > 0) {
2 someVariable ++;
3 }

```

### 2.3. Operador Exists

O Operador Exists tem como função indicar que determinado trecho de código do padrão deve estar presente ou não no código-fonte buscado. Um trecho de código que não deve estar
presente, sempre deve estar relacionado com outro trecho presente, ou seja, não é possível utilizar um padrão apenas composto por blocos não presentes. Quando não informado, por
convenção, todo padrão deve estar presente e para fazer uso do operador exists e sinalizar a presença ou ausência de um bloco de código, são utilizadas as palavras-chave apresentadas na tabela abaixo.

| Palavra-Chave | Significado |
| --- | --- |
| not-exists | Indica o bloco de código que não deve estar presente |
| exists | Indica o bloco de código que deve estar presente  |

Existem duas formas de uso para essas palavras-chave, utilizando comentários ou labels. Os modificadores de existência aplicados em comentários alteram apenas a próxima instrução após o comentário (linha abaixo), suas variações de uso são apresentadas no código-fonte 5. Já para os labels, é possível demarcar blocos de existência, é possível fazer uso de chaves (“{}”) para delimitar os blocos ou aplicar o label diretamente em estruturas permitidas pelo Java (while, for, try e etc), no código-fonte 6 são apresentados exemplos de uso.

##### Código-Fonte 5: Exemplo dos modificadores de existência com comentários
```java
1 // not_exists
2 try {
3   /* exists */
4   Integer.parseInt( any ) ;
5
6 } catch ( anyException any ) {
7
8 }
```
 
##### Código-Fonte 6: Exemplo dos modificadores de existência com labels
```java
1 not_exists:
2 try {
3   exists:{
4     Integer.parseInt( any ) ;
5   }
6 } catch ( anyException any ) {
7
8 }

```

Em ambos os códigos de exemplos, 5 e 6, o mesmo padrão é escrito utilizando as duas formas de aplicação dos modificadores de existência e suas variações. O padrão definido é a
conversão de qualquer valor(wildcard any) para inteiro que não esteja envolvido por um bloco de tratamento de exceções try catch. O bloco “catch” está vazio, pois, o seu conteúdo não importa para o padrão buscado.

No código-fonte 6, o label “not_exist” é utilizado para indicar que todo o bloco try (inclusive o catch) não deve estar presente no código buscado. Como o trecho de código que deve existir está envolvido pelo try é necessário apontar a sua existência, pois, sem uso do modificador de existência o mesmo estaria designado a não existir por estar envolvido pelo try que não existe. A mesma lógica serve para o código-fonte 5, porém, fazendo o uso de comentários, o comentário “//not_exists” aponta que o bloco try não deve existir e o comentário “/\*exists\*/” aponta que a conversão para inteiro deve existir.

### 2.4. Agrupamento de Padrões por Pastas

Para um determinado padrão de defeito, existem diversas formas de evitá-lo, sendo então necessário durante a busca do mesmo localizar estes códigos de verificação, tornando a localização mais precisa. Porém, a programação de múltiplas verificações em um só arquivo de código-fonte de padrão, pode deixar a sua escrita complexa ou até mesmo inviável. Pensando nesses casos, foi implementado o agrupamento de padrões por pastas, onde um grupo de padrões pertencentes a uma pasta constituem um defeito. Sendo possível trabalhar com a ideia de conjuntos, e incluir ou excluir do resultado final da busca de todo defeito(pasta), a ocorrência de determinado padrão.

Voltando ao padrão do código-fonte 3, apenas o mesmo não é o suficiente para determinar se existe um defeito ou não, pois, algumas verificações não são feitas. Por exemplo, não é verificado se entre a declaração nula e a chamada de método, existe uma atribuição alterando o valor nulo da variável. Ou se antes da chamada do método existe uma verificação para que o método só seja invocado, caso a variável tenha valor diferente de nulo e assegurar que o valor não é nulo.

Esses casos citados anteriormente não se configuram defeitos e não devem ser considerados no resultado final da busca, ou seja, devem ser excluídos. Por regra, todo padrão deve ser incluído ao resultado final, e para marcar um padrão como exclusão, basta adicionar o sufixo Exclude ao nome de seu arquivo, assim deve ser terminado com Exclude.java.

Os padrões de exclusão trabalham como exceções a regra dos padrões de inclusão, se um nó é retornado para o padrão do código 3 o mesmo é incluído ao resultado, porém, se o mesmo
também for retornado para o padrão do código-fonte 15 ou 16, deve ser retirado. Para o exemplo atual, uma variável declarada com valor nulo e posteriormente uma chamada de método com a mesma, não é um defeito caso: haja uma outra atribuição de qualquer valor antes da chamada de método (código-fonte 15) ou exista uma verificação de que o valor é diferente de nulo (código-fonte 16).

##### Código-Fonte 15: Exemplo de padrão de exclusão

```java
// Terminado com ’Exclude . java ’
Integer someVariable = null ;
someVariable = anyValue ;
someVariable.anyMethod () ;
```
 
##### Código-Fonte 16: Exemplo de padrão de exclusão

```java
// Terminado com ’Exclude . java ’
Integer someVariable = null ;
if( someVariable != null )
 someVariable . anyMethod () ;

```

#### Alternativa ao agrupamento de padrões por pastas

O agrupamento de padrões por pastas facilita a programação de padrões compostos por múltiplos sub-padrões, porém adiciona o trabalho de criar novos arquivos, demandando um esforço maior. Muitas das vezes esses novos arquivos repetem código-fonte e diferem em apenas um pequeno trecho. Pensando nesses casos, foi implementada uma funcionalidade de marcação no código-fonte do padrão, no qual possibilita escrever as múltiplas variações do padrão que diferem em apenas um ponto todas no mesmo arquivo.

O bloco de opções tem início com o comentário “//#BEGIN”, as opções são separadas pelo comentário “//#OR” e o bloco é finalizado com o comentário “//#END”, como utilizado no
código-fonte 17. Para cada opção é gerado automaticamente, em memória, um novo arquivo de código-fonte, repetindo o código-fonte do padrão substituindo o bloco de opções inteiro apenas pelo termo da respectiva opção. O funcionamento é análogo a propriedade distributiva, presente na operação matemática de multiplicação.


##### Código-Fonte 17: Alternativa ao agrupamento de pastas

```java
// InAnyMethod
//# BEGIN

someVariable = anyMethod() ;
if( someVariable != null ){
 someVariable.any() ;
}

//#OR

anyType someVariable = null ;
// not_exists
someVariable = any ;
someVariable.any() ;

//#END


```
O código-fonte 17 apresenta uma aplicação do bloco de opções. Onde se deseja procurar dois padrões que não dependem de características da classe e método que os evolvem, para isso foi utilizado o comentário “//InAnyMethod”. O bloco de opções facilita e possibilita a escrita dos padrões em apenas um arquivo. A junção destas duas funcionalidades deixa o padrão bem reduzido, sendo é possível notar que o foco principal de esforço é apenas nos pequenos trechos principais dos padrões.

## 3. Manual do Usuário

### 3.1 Requisitos para usar a ferramenta
Para executar a ferramenta é necessário possuir o ambiente de execução Java (JRE) na versão igual ou superior a 13.0.1. Além disso, deve-se ter conhecimento básico de programação Java, para realizar a programação dos padrões e utilizar todas as suas funcionalidades.

### 3.2 CLI (Command User Interface) - Prompt de Comando

O CLI é a interface utilizada pela ferramenta para interagir com o usuário, onde o mesmo através de comandos executa as ações desejadas no sistema.  
A ferramenta deve ser executada via Prompt de Comando passando os comandos abaixo :

```
java -jar scpl.jar <comandos_CLI>
```

  - "java" faz referência ao JRE(caso não esteja no path do sistema operacional, é necessário especificar o caminho completo do arquivo)
  - "scpl.jar" representa a aplicação já compilada 
  - “<comandos_CLI>” representa os comandos que serão passados para o sistema interpretar e executar ações. 

#### 3.2.1 Comandos CLI
  - Search
    ``` 
    java -jar scpl.jar search -code ./CaminhoCodigoAlvoDaBusca -pattern ./CaminhoCodigosDosPadroes
    ```

  - Debug
   
    ``` 
    java -jar scpl.jar -debug search -code ./CaminhoDoCodigoAlvoDaBusca -pattern ./CaminhoComCodigosDosPadroes
    ```
  
  - Version
   
    ```
    java -jar scpl.jar -version 
    ```

  - Help
   
    ```
    java -jar scpl.jar -help
    ```

  - Verbose
  
    ```
    java -jar scpl.jar -verbose
    ```
