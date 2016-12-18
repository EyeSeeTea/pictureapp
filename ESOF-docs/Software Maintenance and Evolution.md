#**5º Relatório - Manutenção e Validação do Software**

##**Índice**

1. [Introdução](#intro)
2. [Evolução e Manutenção](#evol)
3. [Implementação da feature](#imp)
4. [Pull request](#pull)
5. [Contribuidores](#contributors)

****
##**Introdução** <a name ="intro"></a>

Manutenção ou evolução de software é descrito como o processo de otimização e melhoria de um software já desenvolvido. Esta fase, que ocorre durante o desenvolvimento de um software, envolve a melhoria de funcionalidades, correção e prevenção de erros, adaptação a diferentes mudanças do sistema, bem como o requerimento de novas funcionalidades por parte dos utilizadores.

Para se avançar com a alteração feita ao software com o objetivo de melhorá-lo, é necessário ter em conta as partes deste que serão alteradas. Deve ser garantido que as alterações são feitas de forma correta, isto é,as mudanças ou funcionalidades implementadas no código não mudam o comportamento expectável do software.


****
##**Evolução e manutenção** <a name ="evol"></a>

[![BCH compliance](https://bettercodehub.com/edge/badge/inesferreira7/FEUP-ESOF-MALARIASURV)](https://bettercodehub.com)

Para avaliar a qualidade da aplicação, foi utilizada a ferramenta Better Code Hub. Esta ferramenta avalia a aplicação e determina fatores como legibilidade, manutenibilidade e capacidade de evolução. Para tal, os seguintes são calculados os seguintes pontos:
* Escrever pequenas unidades de código
* Escrever unidades simples de código
* Evitar a repetição de código
* Manter as unidades de interface pequenas
* Separar funcionalidades em módulos
* Arquitetura com componentes independentes
* Manter as componentes arquiteturais equilibradas
* Manter a base de código pequena
* Automação de testes
* Desenvolvimento de *clean code*

Após execução da ferramenta no nosso projeto, verificamos que a aplicação obteve aprovação em 7 pontos de avaliação, num total de 10.


![Avaliacao](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/total.PNG?raw=true)

####Escrever pequenas unidades de código

![Short](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/short_1.PNG?raw=true)

Unidades mais pequenas de código permitem uma maior facilidade em percebê-las, testá-las e reutilizá-las.


####Escrever unidades simples de código

![Simple](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/simple.PNG?raw=true)

Por exemplo, podemos ver uma função com 39 *branch points*, (while, if, etc.). Quanto maior o número de *branch points*, mais complicado se torna modificar e testar o código implementado. Este foi um dos pontos em que a nossa aplicação não obteve positiva. 


####Evitar a repetição de código

![Repetido](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/once_2.PNG?raw=true)

Se houver código repetido, e houver uma necessidade de corrigi-lo, será necessário corrigir o número de vezes que este está repetido, sendo um processo ineficiente.


####Manter as unidades de interface pequenas

![Interface](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/interface_3.PNG?raw=true)

Mais uma vez, um menor número de argumentos nas funções facilita a sua compreensão e reutilização.


####Separar funcionalidades em módulos

![Separar](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/new%20modules.PNG?raw=true)

Este foi mais um dos pontos em que a nossa aplicação não obteve pontuação positiva. Podemos ver que existe pelo três funções que são chamadas, pelo menos, 50 vezes.
Deve ser feito um esforço para que as componentes tenham pouco ou nenhum conhecimento das definições de outras componentes separadas, de forma a minimizar as consequências que podem surgir quando se fazem mudanças.


####Arquitetura com componentes independentes

![Arq](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/arquitec_5.PNG?raw=true)

Componentes de um nível acima, como a interface, não deveriam comunicar com componentes do mesmo nível, de modo a permitir uma maior isolabilidade de componentes. 


####Manter as componentes arquiteturais equilibradas

![Balanced](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/comp.PNG?raw=true)

Balançar o número de componentes e o seu tamanho relativo torna mais fácil localizar o código. Segundo o Better Code Hub, deve-se organizar o código de modo a ter entre 2 a 12 componentes, mantendo a uniformidade de tamanho relativo menor que 0,71. Posto isto, a aplicação obteve positiva neste ponto.


####Manter a base de código pequena

![Codebase](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/small.PNG?raw=true)

Torna-se mais fácil fazer mudanças estruturais se o projeto tiver um código base mais pequeno. O BetterCode avalia o projeto em 16 man-month, afirmando ainda que o limite ideal máximo é de 20 man-year, sendo possível concluir que a aplicação tem um bom resultado.


####Automação de testes

![Tests](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/tests.PNG?raw=true)

Segundo o Better Code Hub, a nossa aplicação é um *large system*, pois tem mais de 10000 linhas de código. O total de linhas de teste deve ser, pelo menos, 50% deste número. Deve também apresentar uma *assert density* de 5%. Como se vê na imagem, não foram obtidas nenhuma das estatísticas expectáveis, não obtendo aprovação.


####Desenvolvimento de *clean code*

![Clean](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/clean_10.PNG?raw=true)

Os *code smells* existentes referem-se a comentários não precisos e blocos de código comentados. No entanto, existem poucos casos deste.



****
##**Implementação da feature**<a name="imp"></a>

A forma mais fácil de facilitar a descoberta de possíveis funcionalidades a adicionar a um projeto consiste em explorar as issues listadas no repositorio do projeto. Depois de analisarmos a lista das issues, especificamente as que tinham a tag *Feature* concluímos que não éramos capazes de as desenvolver. Deste modo, decidimos analisar com cuidado a aplicaçao, verificando que, para visualizar as respostas ao questionário, o utilizador tem de fazer slide para ver as seguintes ou anteriores. De modo a melhorar este aspeto, decidimos desenvolver uma *dialog box*, indicando ao utilizador o que tem de fazer, como referido anteriormente.

Após a analise do codigo e da documentação do projeto, o ficheiro a alterar para implementar a feature foi identificado. Este processo foi rápido e simples devido à boa documentação e à boa estruturação do código. O ficheiro alterado foi DynamicTabAdapter.java e acrescentados os seguintes pedaços de código :
```java
import android.os.Bundle;

```

```java

 protected void onCreate(Bundle savedInstanceState) {
 
         AlertDialog.Builder altdial = new AlertDialog.Builder(this);
         altdial.setMessage("Swipe left to advance and right to go back!").setCancelable(false)
                 .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 });
 
         AlertDialog alert = altdial.create();
         alert.setTitle("Instructions");
         alert.show();
     }
```

O código adicionado faz com que mal a página de visualização das respostas seja carregada, uma *dialog box* apareça com as devidas instruçoes de navegação. Primeiro, é criado o *builder* da janela com um tema default. Segue-se a customização da mesma com a mensagem principal, a opção de a janela poder desaparecer e a configuração do botão para a fechar. Finalmente é definido o título e é executada  a função para mostrar imediatamente a mensagem. 

![Feature](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/feature.png?raw=true =100x20)

****
##**Pull request**<a name="pull"></a>

O pull request pode ser visto [aqui](https://github.com/EyeSeeTea/pictureapp/pull/666). Na altura da submissão do relatório, este não tinha sido aceite.



****
##**Contribuidores**<a name="contributors"></a>

* [Inês Ferreira](https://github.com/inesferreira7)
* [João Gomes](https://github.com/joaogomes04)
* [Tomás Caldas](https://github.com/tomasvcaldas)
