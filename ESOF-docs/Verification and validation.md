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

O objetivo deste relatório consiste na análise dos processos de verificação e validação seguidos no desenvolvimento do projeto open source em estudo. O processo de verificação pretende verificar se as funcionalidades do software estão a ser ou foram implementadas como planeado de acordo com os requisitos. Por sua vez, o processo de validação tem como objetivo avaliar se o produto final satisfaz as expectativas e necessidade para o qual estava destinado.

Numa primeira fase, explorar-se-á o grau de testabilidade do software, analisando a controlabilidade do estado dos componentes testados, a observabilidade dos resultados e a isolabilidade dos componentes, bem como o grau de separação de funcionalidades, de inteligibilidade dos componentes e de heterogeneidade das tecnologias utilizadas.

Numa segunda fase, serão apresentadas algumas estatísticas pertinentes relacionadas com a verificação e validação da aplicação em estudo. 

Finalmente, abordaremos a útlima fase que consistiu na seleção de um bug report a partir da lista de issues do projeto, na conceção de casos de teste para a sua reprodução e na correção efetiva do erro.


****
##**Testabilidade do Software** <a name ="test"></a>


###**Controlabilidade** <a name="cont"></a>

Após a análise do projeto relativamente à testabilidade do mesmo, verificamos que foram feitos dois tipos de testes, testes unitários e testes instrumentados. 
Para o processo de testing, são criados ficheiros de teste diferentes para cada componente que a equipa pretende testar. São também utilizadas ferramentas, referidas posteriormente, que dispõe de features que acedem a informação dos objetos e a estados de componentes quando ocorrem ações por parte dos utilizadores. Posto isto, pensamos ser razoável admitir que é possível controlar o estado das mesmas, apresentando então um alto nível de controlabilidade.


###**Observabilidade** <a name="observ"></a>

Para o processo de testing, foi utilizada a biblioteca *Android Testing Support Library*, que disponibiliza uma framework para testar aplicações Android. Inclui duas ferramentas, *JAndroidJUnitRunner* e *Expresso*, utilizadas para os dois tipos de testes implementados, testes unitários e testes instrumentados.
Um teste instrumentado é um tipo de teste que é exectuado diretamento no emulador ou dispositivo, simulando o comportamento de um utilizador. Para estes testes, a equipa de desenvolvimento tirou proveito das ferramentas referidas, que dispõem de classes como *Instrumentation* e *Context*, que permitem aceder a informação da componente a ser testada, bem como a sua interação com o sistema.
Foi também usada uma extensão da ferramenta Expresso, Expresso Intents. 

Tendo em conta o que foi referido anteriormente, pensamos ser correto afirmar que esta aplicação tem um alto nível de controlabilidade.


###**Isolabilidade** <a name="iso"></a>


###**Separação de Funcionalidades** <a name="sep"></a>


###**Inteligibilidade** <a name="int"></a>


###**Heterogeneidade** <a name="het"></a>


***
##**Estatísticas de teste**<a name="est"></a>

***
##**Bug report**<a name="bug"></a>

***
##**Contribuidores**<a name="contributors"></a>

* [Inês Ferreira](https://github.com/inesferreira7)
* [João Gomes](https://github.com/joaogomes04)
* [Tomás Caldas](https://github.com/tomasvcaldas)
