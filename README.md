Instalacao
=

Primeiramente instalar o MongoDB ( http://mongodb.org )
-

Não precisa configuração, basta instalar e iniciar uma instancia do mongodb. 

Para instanciar na linha de comando:

    mongod

Para entrar no console do mongodb

    mongo

Vamos criar uma `database` para o nosso banco

    use monitor

Criado! Vamos adicionar um usuário e senha

    db.addUser( "<usuario>", "<senha>", { readOnly: false } )

Adicionado! Pronto.


Baixar o Play! Framework ( http://playframework.org )
-
Ele é um framework para desenvolvimento web full stack, ou seja, não precisa de apache / outro container de servlets para rodar.. ele já vem com um servidor integrado.

Descompactem o .zip, extraiam em alguma lugar e coloquem o binário do play no build path, no site tem a documentação para cada sistema operacional.

Antes de puxar o projeto, alguns detalhes sobre o Play!

* Ele é um framework para desenvolvimento ágil, não precisa de muita configuração para começar a utilizar.
* Ele é escrito em Scala, no entanto Scala roda em cima da JVM. Portanto ele suporta programação tanto em Scala como em Java.
* Para gerenciamento de dependencia / tarefas ele utiliza o SBT ( Scala Build Tool), que é como se fosse um Maven para este framework.

Depois, copiem o projeto desse git, depois de ter instalado o git na máquina é só dar `git clone https://github.com/andersonsoares/monitor` em algum diretório do sistema que o git já puxa o projeto.

Entrem no diretório pela linha de comando

    cd monitor

O projeto consiste em alguns diretórios

* app/ -> todo o projeto em si
* conf/ -> arquivo com configuracoes, como o banco de dados...
* lib/ -> algumas bibliotecas extras que o SBT nao consegue gerenciar
* logs/ 
* project/ -> arquivos com configuracao do SBT para adicionar dependencias do projeto
* public/ -> assets publicos como css / js / imagens
* target/ -> pasta que ele gera para controlar o hot deploy
* test/ -> tests, que neste projeto não tem rsrsrs.

Conhecido a estrutura do projeto, abriremos o arquivo conf/application.conf

Notem que as primeiras linhas são os dados para conexao com o MONGO. Configurem este arquivo  de acordo com os dados inseridos no banco.

Depois de tudo configurado, vamos falar para o Play verificar nossas dependencias(bibliotecas)

    play dependencies -> Este comando vai verificar as dependencias do projeto, baixar e salvar, assim como o maven faz.

Depois é só rodar a aplicação

    play run

Ela pode ser acessada em:

    http://127.0.0.1:9000






