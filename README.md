# Projeto-EA

App para utentes/utilizadores de meios de transporte localizada (uma cidade)

## HTTPS em produção com Caddy

Em telemóveis, `getUserMedia` e bibliotecas de QR normalmente só funcionam em contexto seguro (`https://`). Por isso o caminho certo aqui é terminar TLS em produção com Caddy, à frente do frontend, em vez de expor a app diretamente por `http`.

### Como funciona

- O deploy publica a app em `https://<vm-ip>.nip.io`.
- O Caddy recebe tráfego nas portas `80` e `443` e obtém o certificado TLS automaticamente.
- O Caddy faz proxy para o container `app`.
- O `app` continua a fazer proxy de `/api` para `backend:8080`, portanto o browser fica sempre no mesmo origin HTTPS.

### O que o deploy precisa

- DNS dinâmico via `nip.io`, usando o IP público da VM.
- Portas `80` e `443` abertas até à VM para o ACME challenge e tráfego HTTPS.
- Origem OAuth do Google configurada com o host HTTPS público.

### Limitação importante

Isto resolve o secure context para a câmara no browser do telemóvel. Se o Google OAuth falhar, o problema já não é HTTPS da app; normalmente passa a ser configuração de Authorized JavaScript Origins e redirect URIs no Google Cloud.

### Diagrama

Existe um diagrama Mermaid da topologia em [docs/caddy-phone-flow.mmd](docs/caddy-phone-flow.mmd).

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.4/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.4/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.4/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/4.0.4/reference/io/validation.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.
