# Histórias de utilizador

Histórias do BookSwipe, agrupadas por épico, com os critérios de aceitação, a prioridade e se entram no MVP.

**Formato:** Como [tipo de utilizador], quero [fazer algo] para [benefício].

**Uma história está boa quando** tem um benefício claro, descreve uma só ação, tem 2 a 4 critérios de aceitação verificáveis e pode ser demonstrada no fim.

**Prioridades:**

- **Essencial:** sem isto a app não funciona.
- **Importante:** melhora muito a app, mas ela funciona sem isto.
- **Se der tempo:** entra só se sobrar tempo.

O MVP é o conjunto das histórias Essencial e das Importante marcadas com "Sim". Se o MVP parecer grande demais para o prazo, cortam-se histórias em vez de qualidade.

## MVP

Um leitor novo cria conta, escolhe os géneros de que gosta e faz swipe num deck que aprende com as suas reações, guardando os livros numa estante com os separadores Para ler, Lidos e Rejeitados.

O MVP está pronto quando:

- Um utilizador novo consegue criar conta, escolher 3 géneros e ver um primeiro deck com livros desses géneros.
- Os swipes para a direita, para a esquerda e para cima levam cada livro ao separador certo da estante, e nenhum livro avaliado volta ao deck.
- O deck muda com os swipes: depois de guardar 5 livros de um género, pelo menos 6 dos 10 cartões seguintes são desse género.
- A estante e os swipes mantêm-se depois de terminar e voltar a iniciar sessão, no browser do telemóvel.

## Índice

| ID | História | Épico | Prioridade | MVP |
|---|---|---|---|---|
| [US-01](#us-01) | Ver as informações do livro no cartão | Swipe | Essencial | Sim |
| [US-02](#us-02) | Swipe à direita: guardar em "para ler" | Swipe | Essencial | Sim |
| [US-03](#us-03) | Swipe à esquerda: não me interessa | Swipe | Essencial | Sim |
| [US-04](#us-04) | Swipe para cima: já li, com nota opcional | Swipe | Importante | Sim |
| [US-05](#us-05) | Desfazer o último swipe | Swipe | Importante | Sim |
| [US-06](#us-06) | Estante com separadores (para ler, lidos, rejeitados) | Os meus livros | Importante | Sim |
| [US-07](#us-07) | Tirar um livro da lista "para ler" | Os meus livros | Importante | Sim |
| [US-08](#us-08) | Marcar como lido e dar nota | Os meus livros | Importante | Sim |
| [US-09](#us-09) | Reconsiderar um livro rejeitado | Os meus livros | Se der tempo | Não |
| [US-10](#us-10) | Escolher géneros ao criar a conta | Recomendações | Essencial | Sim |
| [US-11](#us-11) | Procurar e marcar livros já lidos | Recomendações | Importante | Não |
| [US-12](#us-12) | Criar conta | Conta | Essencial | Sim |
| [US-13](#us-13) | Iniciar sessão | Conta | Essencial | Sim |
| [US-14](#us-14) | Terminar sessão | Conta | Essencial | Sim |
| [US-15](#us-15) | Apagar a conta | Conta | Se der tempo | Não |
| [US-16](#us-16) | Deck com recomendações pelos meus gostos | Recomendações | Essencial | Sim |
| [US-17](#us-17) | Livros populares no fim das recomendações | Recomendações | Essencial | Sim |
| [US-18](#us-18) | Marcar um livro como "a ler" | Os meus livros | Importante | Não |
| [US-19](#us-19) | Terminar a leitura e escrever review | Os meus livros | Importante | Não |
| [US-20](#us-20) | Escolher livros para ler no questionário | Recomendações | Importante | Não |
| [US-21](#us-21) | Aceitar Termos e Política de Privacidade | Conta | Importante | Não |
| [US-22](#us-22) | Entrar com Google | Conta | Se der tempo | Não |
| [US-23](#us-23) | Contadores de livros no perfil | Os meus livros | Se der tempo | Não |
| [US-24](#us-24) | Objetivo de leitura anual | Os meus livros | Se der tempo | Não |
| [US-25](#us-25) | Editar nome e @utilizador | Conta | Se der tempo | Não |
| [US-26](#us-26) | Editar géneros favoritos | Recomendações | Se der tempo | Não |
| [US-27](#us-27) | Filtrar o deck por género | Swipe | Se der tempo | Não |
| [US-28](#us-28) | Pesquisar na estante | Os meus livros | Se der tempo | Não |

## Conta

### US-12

**Criar conta** · Essencial · MVP

Como visitante, quero criar uma conta com e-mail e palavra-passe para guardar os meus swipes e a minha estante.

- O registo pede nome, e-mail e palavra-passe.
- Não é possível registar duas contas com o mesmo e-mail.
- A palavra-passe tem pelo menos 8 caracteres.
- Depois de criar a conta, avanço para o questionário de géneros (US-10).

### US-13

**Iniciar sessão** · Essencial · MVP

Como leitor, quero iniciar sessão para continuar de onde parei.

- Com e-mail e palavra-passe corretos, entro no deck.
- Com dados errados, vejo uma mensagem que não diz qual dos dois está errado.
- A minha estante e os meus swipes anteriores continuam lá.

### US-14

**Terminar sessão** · Essencial · MVP

Como leitor, quero terminar a sessão para que outra pessoa no mesmo dispositivo não aceda à minha conta.

- Depois de terminar a sessão, volto ao ecrã de entrada.
- Ao tentar abrir o deck sem sessão, sou enviado para o ecrã de entrada.

### US-15

**Apagar a conta** · Se der tempo · v2

Como leitor, quero apagar a minha conta para remover os meus dados da app.

- A app pede confirmação antes de apagar.
- A conta, os swipes e as notas são apagados.
- Depois de apagar, não consigo iniciar sessão com esse e-mail.

### US-21

**Aceitar Termos e Política de Privacidade** · Importante · v2

Como visitante, quero aceitar os Termos e a Política de Privacidade ao criar conta para saber como os meus dados são usados.

- O registo tem uma caixa para aceitar os Termos e a Política de Privacidade, com ligações para os dois textos.
- Não consigo criar a conta sem a marcar.
- A data da aceitação fica guardada.

### US-22

**Entrar com Google** · Se der tempo · v2

Como visitante, quero criar conta ou iniciar sessão com a minha conta Google para entrar sem criar uma palavra-passe.

- O registo e o início de sessão têm o botão "Continuar com Google".
- Na primeira vez, é criada uma conta com o nome e o e-mail da conta Google.
- Se já existir uma conta com esse e-mail, não é criada uma segunda conta.

### US-25

**Editar nome e @utilizador** · Se der tempo · v2

Como leitor, quero editar o meu nome e o meu nome de utilizador para manter o perfil como quero.

- Posso alterar o nome e o @utilizador no ecrã "Editar perfil".
- O @utilizador é único: se já existir, vejo uma mensagem a pedir outro.
- As alterações aparecem no perfil logo depois de guardar.

## Swipe

### US-01

**Ver as informações do livro no cartão** · Essencial · MVP

Como leitor, quero ver as informações do livro (autor, sinopse, número de páginas) antes de fazer swipe para decidir se me interessa.

- O cartão mostra capa, título, autor, sinopse e número de páginas.
- Se faltar algum destes dados, o cartão mostra os restantes sem espaços vazios.

### US-02

**Swipe à direita: guardar em "para ler"** · Essencial · MVP

Como leitor, quero fazer swipe para a direita num livro para o guardar na lista "para ler" e escolher a minha próxima leitura mais tarde.

- O livro é adicionado à lista "para ler".
- O cartão sai do deck e o livro não volta a aparecer.
- A lista mantém-se depois de fechar e abrir a app.

### US-03

**Swipe à esquerda: não me interessa** · Essencial · MVP

Como leitor, quero fazer swipe para a esquerda num livro para indicar que não me interessa e deixar de o ver.

- O cartão sai do deck.
- O livro não volta a aparecer no deck deste utilizador.
- O swipe fica registado como "não interessa".

### US-04

**Swipe para cima: já li, com nota opcional** · Importante · MVP

Como leitor, quero fazer swipe para cima num livro que já li para o marcar como lido e, se quiser, dar-lhe uma nota.

- O cartão sai do deck e o livro não volta a aparecer.
- Aparece a janela de avaliação da US-08 (0,5 a 5 estrelas, com meias estrelas e opção de saltar).

### US-05

**Desfazer o último swipe** · Importante · MVP

Como leitor, quero desfazer o último swipe para corrigir um engano.

- O último livro volta ao topo do deck.
- O registo do swipe é removido e o efeito nas recomendações desaparece.
- Só o último swipe pode ser desfeito.
- Depois de desfazer, o botão fica desativado até haver novo swipe.

### US-27

**Filtrar o deck por género** · Se der tempo · v2

Como leitor, quero filtrar o deck por género para descobrir livros do tipo que me apetece ler agora.

- Há um botão de filtros no deck, com a lista de géneros.
- Com um filtro ativo, o deck só mostra livros desse género.
- Posso limpar o filtro e voltar ao deck normal.
- Os livros que já avaliei continuam a não aparecer.

## Os meus livros

### US-06

**Estante com separadores (para ler, lidos, rejeitados)** · Importante · MVP

Como leitor, quero ver a minha estante com separadores (para ler, lidos, rejeitados) para encontrar rapidamente os meus livros.

- Cada separador mostra capa, título e autor, do mais recente para o mais antigo.
- Posso abrir um livro para ver os seus detalhes.
- O separador Lidos mostra também a nota, quando existe.
- Cada separador vazio mostra uma mensagem a explicar como o preencher.

### US-07

**Tirar um livro da lista "para ler"** · Importante · MVP

Como leitor, quero tirar um livro da lista "para ler" para manter a lista só com o que ainda quero ler.

- Ao escolher remover, pergunto se já li o livro ou se deixei de querer lê-lo.
- Se escolher "deixei de querer lê-lo", o livro sai da lista e não volta ao deck.
- Se escolher "já li", avança para a US-08.

### US-08

**Marcar como lido e dar nota** · Importante · MVP

Como leitor, quero marcar um livro como lido e dar-lhe uma nota para registar o que achei.

- Aparece uma janela com 0,5 a 5 estrelas, em passos de meia estrela, e a opção de saltar.
- A nota fica guardada e aparece no livro, no separador Lidos.
- Se saltar a avaliação, o livro fica marcado como lido, sem nota.

### US-09

**Reconsiderar um livro rejeitado** · Se der tempo · v2

Como leitor, quero reconsiderar um livro rejeitado para o guardar na lista "para ler".

- Posso mover um livro de Rejeitados para Para ler.
- O livro deixa de contar como rejeitado nas recomendações.

### US-18

**Marcar um livro como "a ler"** · Importante · v2

Como leitor, quero marcar um livro como "a ler" para acompanhar o que estou a ler agora.

- Posso mover um livro de Para ler para A ler.
- O livro aparece no separador A ler, com a data em que comecei.
- Posso ter mais do que um livro a ler ao mesmo tempo.

### US-19

**Terminar a leitura e escrever review** · Importante · v2

Como leitor, quero terminar a leitura de um livro e escrever uma review para registar o que achei.

- Ao escolher "terminei", o livro passa de A ler para Lidos.
- Aparece a janela de avaliação da US-08, com um campo de texto opcional para a review.
- A review fica guardada e aparece no livro, no separador Lidos.
- Só eu vejo as minhas reviews.

### US-23

**Contadores de livros no perfil** · Se der tempo · v2

Como leitor, quero ver no meu perfil quantos livros tenho em cada lista para acompanhar a minha atividade.

- O perfil mostra o número de livros em cada lista da estante.
- Os números atualizam-se quando movo um livro de lista.
- Uma lista vazia mostra 0.

### US-24

**Objetivo de leitura anual** · Se der tempo · v2

Como leitor, quero definir um objetivo de livros por ano e ver o meu progresso para me manter motivado a ler.

- Posso definir e alterar o número de livros que quero ler no ano.
- O perfil mostra os livros lidos no ano e uma barra de progresso até ao objetivo.
- Só contam os livros marcados como lidos nesse ano.
- Sem objetivo definido, o perfil convida-me a defini-lo.

### US-28

**Pesquisar na estante** · Se der tempo · v2

Como leitor, quero pesquisar na minha estante por título ou autor para encontrar um livro rapidamente.

- A estante tem uma barra de pesquisa por título ou autor.
- A pesquisa mostra só os livros do separador aberto.
- Sem resultados, aparece uma mensagem a dizer que nenhum livro corresponde.

## Recomendações

### US-10

**Escolher géneros ao criar a conta** · Essencial · MVP

Como leitor novo, quero escolher os géneros de que gosto ao criar a conta para que o primeiro deck já tenha livros do meu interesse.

- Depois de criar a conta, aparece a lista de géneros.
- Tenho de escolher pelo menos 3 géneros para continuar.
- Posso saltar este passo; nesse caso, o deck mostra livros populares.
- O primeiro deck mostra sobretudo livros dos géneros escolhidos.

### US-11

**Procurar e marcar livros já lidos** · Importante · v2

Como leitor novo, quero procurar livros que já li e marcá-los como lidos para que as recomendações partam dos meus gostos reais.

- Há uma barra de pesquisa por título ou autor.
- Ao escolher um livro, aparece a janela de avaliação da US-08.
- O livro fica no separador Lidos e não aparece no deck.
- Posso terminar a qualquer momento e seguir para o deck.

### US-16

**Deck com recomendações pelos meus gostos** · Essencial · MVP

Como leitor, quero que o deck me mostre livros parecidos com os que guardei e avaliei bem para descobrir leituras do meu gosto.

- O deck nunca mostra livros que já avaliei.
- Depois de guardar vários livros de um género, a maioria dos cartões seguintes é desse género.
- Os livros que rejeito e as notas baixas fazem descer os géneros e autores desses livros.
- Sem swipes suficientes, o deck mostra livros dos géneros escolhidos na US-10 ou populares.

### US-17

**Livros populares no fim das recomendações** · Essencial · MVP

Como leitor, quero ver livros populares quando acabam as minhas recomendações para continuar a descobrir livros.

- Quando não há mais livros recomendados, aparece uma mensagem e a opção de ver os mais populares.
- Os populares são livros que ainda não avaliei, ordenados pelo número de vezes que foram guardados.
- Os swipes nos populares funcionam como no deck normal e contam para as recomendações.
- Se já avaliei todos os livros, aparece uma mensagem a dizer que não há mais livros, sem a opção dos populares.

### US-20

**Escolher livros para ler no questionário** · Importante · v2

Como leitor novo, quero escolher alguns livros que quero ler ao criar a conta para começar com a estante preenchida e dar mais pistas às recomendações.

- No questionário aparece uma lista de livros sugeridos para escolher.
- Cada livro escolhido fica no separador "Para ler" da estante.
- Os livros escolhidos não aparecem no deck.
- Posso concluir o registo sem escolher nenhum.

### US-26

**Editar géneros favoritos** · Se der tempo · v2

Como leitor, quero alterar os meus géneros favoritos para que as recomendações acompanhem os meus gostos atuais.

- No perfil posso adicionar e remover géneros favoritos.
- Têm de ficar pelo menos 3 géneros escolhidos.
- Depois de alterar, o deck passa a ter em conta a nova escolha.

## Notas para a v2

- **US-11:** exige decidir se a pesquisa usa só a nossa base de dados ou a Open Library.
- **US-15 e US-21:** obrigatórias antes de abrir a app ao público (RGPD).
- **US-18:** acrescenta um quarto separador, A ler, na estante.