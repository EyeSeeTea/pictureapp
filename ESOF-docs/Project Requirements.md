#**2º Relatório - Levantamento de requisitos**

##**Índice**

1. [Introdução](#intro)
2. [Levantamento de requisitos](#elicitation)
4. [Especificação de requisitos](#specification)
    1. [Características](#features)
    2. [Casos de utilizacao](#usecases)
    3. [Modelo de dominio](#domainmodel)
4. [Contribuidores](#contributors)


***
##**Introdução** <a name ="intro"></a>

Para o desenvolvimento de uma aplicação de sucesso, umas das fases mais importantes é a fase de levantamento de requisitos. Neste etapa, a equipa tem como objetivo conhecer o domínio em que o projeto se enquadra, entender as necessidades dos utilizadores, identificar possíveis problemas e apresentar soluções. Podemos afirmar então que esta fase trata-se de um processo iterativo, com uma contínua validação de uma atividade para a outra. 

Antes de continuar, queremos acrescentar que voltamos a contactar os contribuidores principais do projeto, voltando a não ter resposta da sua parte, portanto o que será dito posteriormente serão suposições.


****
##**Levantamento de requisitos** <a name ="elicitation"></a>

Relativamente à aplicação QIS Malaria Case Surveillance, não encontramos nenhuma informação clara relativamente a requisitos. No entanto, sabemos que tinha como objetivo ser utilizada por profissionais de saúde para detetar casos de malária no Cambodja e que, por este motivo, a desenharam para ser user-friendly e exigir pouco ou mesmo nenhum conhecimento de tecnologia.


****
##**Especificação de requisitos** <a name ="specification"></a>

O SRS, software requirements specification, é um documento que descreve o sistema a ser desenvolvido, estabelecendo requisitos funcionais e não funcionais. Os requisitos funcionais especificam determinado comportamento função, sendo definidos na forma de "O sistema deve fazer -requisito-, enquanto que os requisitos não funcionais especificam critérios para determinar a qualidade do sistema, sendo estes definidos como "O sistema deve ser -requisito-. Deve incluir um conjunto de casos de utilização que descrevem a interação dos utilizadores com o sistema.



###**Características** <a name="features"></a>

####**Funcionais:**
1. Efetuar login no servidor ao iniciar a aplicação.
2. Preencher formulário com os valores obtidos no mRDT teste e submeter para o servidor.
3. Visualizar os formulários enviados, por enviar e as estatísticas totais.
    
####**Não funcionais:**
1. Open Source.
2. Interface simples e amigável.
3. Modo offline.
4. Idioma da região.
5. Compatível com SDK API versão 15+.
6. Atualização automática na ligação à internet.



###**Casos de utilização** <a name="usecases"></a>

![usecase](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/UseCaseDiagram.png?raw=true)



###**Modelo de domínio** <a name="domainmodel"></a>

![domainmodel](https://github.com/tomasvcaldas/FEUP-ESOF-MALARIASURV/blob/master/ESOF-docs/Images/DomainModel.png?raw=true)


****
##**Contribuidores**<a name="contributors"></a>

* [Inês Ferreira](https://github.com/inesferreira7)
* [João Gomes](https://github.com/joaogomes04)
* [Tomás Caldas](https://github.com/tomasvcaldas)
