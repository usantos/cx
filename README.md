# Introdução

A proposta desse documento/iniciativa é de criarmos um manual contendo práticas e padrões que utilizaremos no dia a dia no desenvolvimento dos apps. Com isso visamos facilitar a disseminação do conhecimento e nivelar a equipe como um todo, desde novos membros a profissionais mais antigos.
A ideia é que esse arquivo seja “vivo”, que evolua constantemente, desde que seja um acordo de todo o time de mobile.

# Arquitetura

Antes de entender como funciona a arquitetura, precisamos alinhar o contexto que o projeto nasceu e como tem sido tocado. O App nasceu em um contexto de fábrica de software baseado em pontos de função e nem sempre, teve um foco extremo em qualidade, logo alguns padrões utilizados, serão evoluídos com o tempo.

Vamos ao que interessa, como padrão de arquitetura adotado, utilizamos o MVC com adaptações (Model, View e Controller) um padrão altamente disseminado no mundo mobile e que costuma atender uma grande gama de projetos.

Como funciona?
Primeiro, precisamos conhecer as três camadas.
- Model — é a camada de dados, responsável por todo controle negocial e de banco de dados, quando houver.
- View — é a camada de layout, onde construímos toda UI do app.
- Controller — é a camada de lógica, responsável por controlar todo o comportamento do usuário na tela e responsável por se comunicar com a Model para realizar atualizações.
Sabendo o que cada camada deve fazer, os projetos são organizados em pacotes ou pastas.

Você vai encontrar os pacotes organizados em Model / Controllers / View. Dentro da Model, temos pacotes importantes, são eles:
- BusinessObject ou BO, onde realizamos as chamadas a API.
- DAO - Data access object, onde são realizadas as operações de banco de dados local.
- SP - SharedPreferences, controle do armazenamento do shared preferences

O gerenciamento de dependências no iOS é executado através do CocoaPods e no Android pelo Gradle. Antes de incluirmos uma dependência no projeto, devemos verificar a licença adotada pelo dono e a real necessidade da dependência. A Caixa possui tipos de licença específicas que podem ser utilizadas. (Elencar todas as licenças permitidas)

Podemos fazer o que foi feito de forma relativamente rápida e com qualidade? Se sim, não adicione. É uma biblioteca que trata sobre assuntos de segurança? Chame outros membros da equipe para analisar juntamente contigo. A biblioteca ainda possui commits/releases, ou é uma biblioteca antiga?

# Banco de dados local

Utilizamos armazenamento local para controlar diversas operações do app, para isso cada SO possui seu formato.

Como banco de dados local, foi adotado o SQLite.

Possuímos uma classe onde centralizamos as atividades do CRUD chamada DBLoteriasCrud.

Também utilizamos o SharedPreferences para armazenar informações menores, como contadores ou booleanos de apoio ao app.

Para auxiliar as operações de armazenamento, utilizamos a classe SharedPreferencesUtils.

# Consumo REST/Api

Utilizamos a biblioteca Volley para facilitar as chamadas HTTP/HTTPS. É uma biblioteca suportada pelo próprio google o que nos fornece segurança ao utilizá-la.

Para conhecer mais detalhes acesse [Volley](https://developer.android.com/training/volley/index.html)

Assim como no iOS criamos uma service de conexão, no Android também possuímos o mesmo conceito. A classe é chamado de SilceConnection e tem exatamente a mesma ideia da do iOS, nela fazemos criptografia do corpo da requisição, dos query params e da url.

No Android foram construídos Business Objects para centralizar as chamadas ao SilceConnection. Exemplo: AcessoBO, ApostaBO, etc.

# Definições de Layout

Nesse ponto trataremos sobre as diretrizes para se construir layout nos apps, primeiramente existe um trabalho de criação, executado pelos designers, onde o foco é tornar a experiência do usuário mais cômoda e agradável.
A equipe de design, entrega o layout no Invision e também um protótipo navegável para a área negocial e para equipe de desenvolvimento.

Para guiar nossa experiência de design e o que tentamos adotar, segue alguns artigos que nos auxiliam.

https://developer.apple.com/design/human-interface-guidelines/ios/overview/themes/
https://medium.com/blueprint-by-intuit/native-mobile-app-design-overall-principles-and-common-patterns-26edee8ced10

Além de pensarmos na forma de criar o layout, ainda vale ressaltar como está organizado o projeto para desenvolver.

Todo layout está na pasta res como padrão em todos os projetos android e para o Android a diretiva é simples, sempre utilizar ConstraintLayout quando for construir qualquer tela. Antes de criar um novo drawable, animator ou algo do tipo, procure no projeto se já não possui o que você deseja, assim evitamos duplicidade de componentes.

# Boas práticas

Vamos dividir em boas práticas gerais e boas práticas específicas.

### Gerais

* Utilize camel case na definição de métodos e variáveis.
* Utilize nomes significativos para métodos e variáveis.
* Evite criar linhas de código muito extensas, separe em outras linhas para facilitar a leitura.
* Evite criar métodos “ninja" onde o mesmo faz muito mais do que se propôs a fazer.
* Não sobrecarregue os métodos de início das activity’s nem das controllers (onCreate/init/
onResume e viewDidLoad/viewWillAppear) evite chamar inúmeras lógicas nesse momento.
* Evite nomes gigantescos e minúsculos para variáveis, se preciso for, mantenha três letras
significativas da palavra. Ex: quantidadeDeTeimosinhas poderia ser qtdTeimosinhas a regra é ver se outro programador irá entender logo ao ler a variável, se ficar ambíguo, melhor deixar o nome por completo.
* Evite aninhamentos extensivos (if dentro de if que tá dentro de outro if e por ai vai).
* Procure as pastas certas para encaixar suas classes e arquivos.
* Oriente sempre a equipe de design a desenvolver layouts, pensando nas duas plataformas,
existem elementos e comportamentos diferentes que devem ser levados em conta.

### Específicas

*  Não utilizar Android annotation.
*  Sempre que puder crie componentes genéricos reutilizáveis (principalmente como
Fragments).
*  Repetindo, dê prioridade para ConstraintLayout ao criar layouts.
*  Utilieze lambda sempre que possível.
*  Não adicione Strings fixas no XML, utilize o string.xml.
