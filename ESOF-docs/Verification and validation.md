#**4º Relatório - Verificação e Validação de Software**

##**Índice**

1. [Introdução](#intro)
2. [Testabilidade do Software](#test)
    1. [Controlabilidade](#cont)
    2. [Observabilidade](#observ)
    3. [Isolabilidade](#iso)
    4. [Separação de Funcionalidades](#sep)
    5. [Inteligibilidade](#int)
    6. [Heterogeneidade](#het)
3. [Estatísticas de teste](#est)
4. [Bug report](#bug)
5. [Contribuidores](#contributors)

***
##**Introdução** <a name ="intro"></a>

O objetivo deste relatório consiste na análise dos processos de verificação e validação usados no desenvolvimento do projeto open source em estudo. O processo de verificação pretende verificar se as funcionalidades do software estão a ser ou foram implementadas como planeado de acordo com os requisitos. Por sua vez, o processo de validação tem como objetivo avaliar se o produto final satisfaz as expectativas e necessidade para o qual estava destinado.

Numa primeira fase, explorar-se-á o grau de testabilidade do software, analisando a controlabilidade do estado dos componentes testados, a observabilidade dos resultados e a isolabilidade dos componentes, bem como o grau de separação de funcionalidades, de inteligibilidade dos componentes e de heterogeneidade das tecnologias utilizadas.

Numa segunda fase, serão apresentadas algumas estatísticas pertinentes relacionadas com a verificação e validação da aplicação em estudo. 

Finalmente, abordaremos a útlima fase que consistiu na seleção de um bug report a partir da lista de issues do projeto, na conceção de casos de teste para a sua reprodução e na correção efetiva do erro.


****
##**Testabilidade do Software** <a name ="test"></a>


###**Controlabilidade** <a name="cont"></a>

Após a análise do projeto relativamente à testabilidade do mesmo, verificamos que foram feitos dois tipos de testes, testes unitários e testes instrumentados. 

Para o processo de testing, são criados ficheiros de teste diferentes para cada componente que a equipa pretende testar. São também utilizadas ferramentas, referidas posteriormente, que dispõe de features que acedem a informação dos objetos e a estados de componentes quando ocorrem ações por parte dos utilizadores. 

Encontramos um ficheiro *tests.md* algumas indicações da equipa de desenvolvimento, o que nos permitiu perceber que estes dão importância ao estado das componentes quando são testadas, caso contrário, pode levar a resultados positivos que não correspondem à realidade.

*"Because of that it is really important to ensure that each tests with its preconditions and checks is run under the right context no matter what test has been run before."*

Posto isto, pensamos ser razoável admitir que é possível controlar o estado das mesmas, apresentando então um bom nível de controlabilidade.


###**Observabilidade** <a name="observ"></a>

Para o processo de testing, foi utilizada a biblioteca *Android Testing Support Library*, que disponibiliza uma framework para testar aplicações Android. Inclui duas ferramentas, *JAndroidJUnitRunner* e *Expresso*, utilizadas para os dois tipos de testes implementados, testes unitários e testes instrumentados.

Um teste instrumentado é um tipo de teste que é exectuado diretamento no emulador ou dispositivo, simulando o comportamento de um utilizador. Para estes testes, a equipa de desenvolvimento tirou proveito das ferramentas referidas, que dispõem de classes como *Instrumentation* e *Context*, que permitem aceder a informação da componente a ser testada, bem como a sua interação com o sistema.

Também é utilizada a biblioteca Hamcrest, que se integra com o JUnit e tem versões para várias linguagens. Permite verificar os resultados dos nossos testes muito mais concisamente. Além disso, quando uma verificação do Hamcrest falha, a mensagem de erro detalha o problema encontrado. 

Um Matcher do Hamcrest nada mais é do que uma classe cuja função é verificar se um dado objeto tem as propriedades desejadas. Apesar de o Hamcrest já incluir diversos Matchers, nem sempre encontramos um que se adeque às nossas necessidades, sendo possível criar um novo que satisfaça estas mesmas necessidades. 

Portanto, pensamos também ser correto afirmar que esta aplicação tem um alto nível de observabilidade.


###**Isolabilidade** <a name="iso"></a>

Um dos principais desafios na definição de testes unitários reside no isolamento de cada componente, isto é, na definição de testes cujo resultado não seja condicionado por eventuais dependências exteriores à unidade de código a ser testada. Relativamente a *Android Testing Support Library*, sabemos que dispõe de uma outra ferramenta *UI Automator*, que segue um modelo black-box, servindo para escrever testes que precisar de ter conhecer detalhes internos da aplicação, o que, na nossa opinião, pode ser útil para aumentar a capacidade de isolabilidade.

No entanto, de acordo com as duas caractéristicas anteriores, controlabilidade e observabilidade, achamos que os testes foram desenvolvidos no sentido de conseguirem isolar componentes e que, de facto, o conseguiram. 


###**Separação de Funcionalidades** <a name="sep"></a>

Ao desenvolver software, é importante garantir que cada funcionalidade implementada fique confinada, o mais possível, ao componente ao qual diz respeito, sob pena de o código resultar mais confuso e, por conseguinte, menos testável. Uma classe que assuma demasiadas responsabilidades ou responsabilidades muito diferentes é não só mais suscetível à introdução de falhas quando alterada, como também mais difícil de testar e validar.

Analisando a estrutura interna, podemos ver que a aplicação separa de forma explícita a sua funcionalidade por diversas classes, o que permite uma maior facilidade nos testes, como foi referido anterioremente.


###**Inteligibilidade** <a name="int"></a>

Durante o desenvolvimento dos vários relatórios, consultamos várias vezes a Wiki disponível no projeto, que, na nossa opinião, se encontra completa, fornecendo vários diagramas e explicações que facilitam a compreensão das várias partes do projeto.

No que toca a comentários, concluímos que está relativamente bem. No entanto, existem algumas partes do código que estão pouco ou nada comentadadas. Analisando mais aprofundadamente os commits, percebemos que esta falta de comentários se encontra nos ficheiros que foram implementados no início do desenvolvimento da aplicação. Inicialmente, a aplicação era desenvolvida pela *core team*, constituída por três elementos, sendo provável ter havido um descuido por este motivo. No entanto, tendo em conta que é uma aplicação open source, pensamos ser do interesse deles e de todos que fazem ou poderão vir a contribuir para a aplicação que haja comentários explícitos, de modo a haver uma maior compreensão por parte de todos. 


###**Heterogeneidade** <a name="het"></a>

Uma vez que a "Malaria Case Surveillance" é uma aplicação open-source é necessário garantir que, após a incorporação do código modificado submetido num dado pull request, o sistema permanece globalmente funcional.

Por um lado, a realização de testes unitários permite garantir o correto funcionamento dos componentes da aplicação. Por outro lado, é preciso averiguar o comportamento da aplicação durante sua utilização, surgindo a necessidade de realizar teste de instrumentação. A ferramente usada para automatizar os testes unitarios foi o JUnit e para os testes de instrumentação foi a Espresso Framework.

Conclui-se, então que o projeto é heterogéneo nas ferramentas de teste utilizadas.
***
##**Estatísticas de teste**<a name="est"></a>

Com a ferramenta Codacy, conseguimos ter acesso a algumas estatísticas.

![stat1](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/stat1.PNG?raw=true)

![stat2](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/stat2.PNG?raw=true)

A percentagem de Error Prone está a 0%, porque, como podemos ver, existem 518 issues. Relativamente a erros, são 379. No entanto, existem apenas 4 tipos de erros:  
     - Use explicit scoping instead of the default package private level      
     - Switch statements should have a default label       
     - Use equals() to compare object references  
     - Avoid reassigning parameters  
     
     
Devido a erros no Android Studio, os quais não conseguimos resolver, não foi possível obter a *test coverage*.

***
##**Bug report**<a name="bug"></a>

Por fim, procedemos à identificação de um bug. Correndo a aplicação e procedendo ao preenchimento das 9 perguntas, verificamos que na primeira pergunta aparece 1/1, sendo que nas restantes aparece 2/9, 3/9, e as restantes até 9/9 corretamente. Podemos ver o sucedido nas três imagens seguintes.

![bug1](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/1.png?raw=true)
![bug2](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/2.png?raw=true)
![bug3](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/3.png?raw=true)

***
##**Contribuidores**<a name="contributors"></a>

* [Inês Ferreira](https://github.com/inesferreira7)
* [João Gomes](https://github.com/joaogomes04)
* [Tomás Caldas](https://github.com/tomasvcaldas)
