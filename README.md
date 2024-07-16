# Authentication Service

# Sessão

[Introdução](#introdução)

[Tecnologis utilizadas](#tecnologis-utilizadas)

[Requisitos](#requisitos)

[Configuração do Ambiente](#configuração-do-ambiente)

[Serviços Necessários](#serviços-necessários)

[Endpoints](#endpoints)

[Conclusão](#conclusão)

# Introdução
O aplicativo de segurança é uma plataforma desenvolvida para gerenciar autenticação, autorização e controle de acesso em sistemas web. Ele oferece funcionalidades para registro de usuários, autenticação baseada em tokens JWT, controle de permissões de acesso e envio de e-mails de verificação.

# Tecnologis Utilizadas
As principais tecnologias utilizadas incluem Spring Boot, Spring Security, Hibernate, Thymeleaf, e JavaMailSender.

# Requisitos
JDK 22

Docker

# Configuração do Ambiente
1. Clone o repositório do aplicativo do GitHub:
```Git Clone
git clone https://github.com/quackcodes/authentication-service.git
```

2. Entre no diretório em `security\src\main\resources\docker`, nele contem o arquivo `docker-compose.yml` no qual possui MySQL, PHPadmin, Mail-dev, abra o terminal e coloque o comando:
```Docker
docker-compose up --build
```

3. Compile o aplicativo usando o Maven:
```Maven
mvn clean install
```

4. Execute:
```Maven
java -jar target/security-app.jar
```

# Serviços Necessários

| Serviço  | Portas              | Descrição                              |
|----------|---------------------|----------------------------------------|
| pgadmin  | http://localhost:80 | E-mail: admin@admin.com e Senha: admin |

# Endpoints

RoleController

| Endpoint       | Método | Descrição                            |
|----------------|--------|--------------------------------------|
| /api/role      |  GET   | Retorna todas as regras cadastradas. |
| /api/role/{id} |  GET   | Retorna a regra do id informado.     |
| /api/role      |  POST  | Cadastra uma nova Regra na aplicação |


AuthController

| Endpoint                 | Método | Descrição                                 |
|--------------------------|--------|-------------------------------------------|
| /api/auth/authenticate   | POST   | Autentica no sistema retornando um Token. |
| /api/auth/refresh-token  | POST   | Gera um novo Token.                       |

UserAccountController

| Endpoint                          | Método | Descrição                              |
|-----------------------------------|--------|----------------------------------------|
| /api/account/register             | POST   | Registra um novo usuário no aplicação. |
| /api/account/{id}                 | GET    | Retorna o usuário do id informado.     |
| /api/account/{id}/change-password | PATCH  | Trocar senha do usuário.               |
| /api/account/{id}/role            | PATCH  | Adicionar nova role ao usuário.        |
| /api/account/{id}/role            | DELETE | Remover role do usuário.               |

# Conclusão
Este guia fornece instruções claras sobre como configurar e usar o aplicativo de segurança. Siga as etapas fornecidas para começar a aproveitar todas as funcionalidades oferecidas pelo aplicativo.
