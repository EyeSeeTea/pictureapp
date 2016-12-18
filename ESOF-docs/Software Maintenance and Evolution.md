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

![Feature](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/feature.png?raw=true)

****
##**Pull request**<a name="pull"></a>

[Link do pull request](https://github.com/EyeSeeTea/pictureapp/pull/666)



****
##**Contribuidores**<a name="contributors"></a>

* [Inês Ferreira](https://github.com/inesferreira7)
* [João Gomes](https://github.com/joaogomes04)
* [Tomás Caldas](https://github.com/tomasvcaldas)
