#**3º Relatório - Design da Arquitetura de Software**

##**Índice**

1. [Introdução](#intro)
2. [Arquitetura de Software](#arch)
3. [Modelo de Vistas de Arquitetura 4+1](#fourplusone)
    1. [Logical View](#logical)
    2. [Development View](#development)
    3. [Deployment View](#deployment)
    4. [Process View](#process)
4. [Contribuidores](#contributors)

***
##**Introdução** <a name ="intro"></a>

O objetivo deste relatório é a explicitação de alguns aspetos relativos à arquitetura do projeto em estudo, QIS Malaria Case Surveillance, seguindo o Modelo de Vistas de Arquitetura 4+1. 

Numa primeira fase, serão apresentados alguns conceitos que consideramos pertinentes para a compreensão do relatório.           

Posteriormente, serão apresentadas as quatros vistas constituintes do modelo de vistas acima referido, com diagramas respetivos:
   
   - Vista lógica - diagrama de pacotes;
   - Vista de desenvolvimento - diagrama de componentes;
   - Vista de deployment - diagrama de deployment;
   - Vista de processo - diagrama de atividade;

O "+1" refere-se à vista de casos de usos, sendo que o diagrama referente a esta vista está implementado no [relatório anterior](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Project%20Requirements.md#usecases)



****
##**Arquitetura de Software** <a name ="arch"></a>

A arquitetura de software é a organização de um sistema, incorporando as suas componentes e relações entre as mesmas e o ambiente. No geral, trata-se de tomar todas as decisões estruturais fundamentais, cuja mudança depois de implementadas seria dispendiosa.
Estas decisões são fortemente influenciadas pelos requisitos não funcionais, como performance, segurança, manutenção, entre outros.

Geralmente, os sistemas de software têm uma grande variedade de partes integrantes, que têm interesses diferentes relativamente ao sistema, sendo necessário fazer um balanço destes interesses. De modo a diminuir a complexidade deste processo, separam-se os vários interesses, associando-os em diferentes vistas, como vimos anteriormente com o Modelo de Vistas Arquitetura 4 + 1.




****
##**Modelo de Vistas de Arquitetura 4+1** <a name ="fourplusone"></a>




###**Logical View** <a name="logical"></a>

Esta vista é utilizada pelos arquitetos de design de software para uma análise funcional. Esta vista foca na necessidade de perceber a funcionalidade da a aplicação em termos de elementos estruturais, mecanismos e abstrações-chave, serparação de interesses e distribuição de responsabilidades.
Esta representação pode ser feita através de um diagrama UML de pacotes, sendo o seguinte diagrama relativamente à aplicação em estudo.


![logicalview](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/logicalView.png?raw=true)




###**Development View** <a name="development"></a>

Development view abrange as componenentes que dizem respeito à parte física do sistema. O software é dividido em componentes que pode ser desenvolvido e testado pela equipa de desenvolvimento.


![components](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/ComponentView.png?raw=true)




###**Deployment View** <a name="deployment"></a>

Os diagramas de deployment são usados para representar uma estrutura física (normalmente de hardware), onde um conjunto de artefatos de software sãoinstalados para compor uma configuração de um sistema. 


![deployment](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/deployment%20view.png?raw=true)




###**Process View** <a name="process"></a>

É com esta vista que são explicados os processos do programa, bem como a comunicação entre eles. Relativamente a esta vista, são feitos os diagramas de atividades.
Este tipo de diagrama é essencialmente um gráfico de fluxo, mostrando o fluxo de uma atividade para outra. Costumam conter estados de atividade e estados de ação, transições e objetos.


![activity](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/Process%20View.png)

***
##**Contribuidores**<a name="contributors"></a>

* [Inês Ferreira](https://github.com/inesferreira7)
* [João Gomes](https://github.com/joaogomes04)
* [Tomás Caldas](https://github.com/tomasvcaldas)
