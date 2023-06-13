# Teste de clusterização do RabbitMQ

Tudo nesse repositório foi feito para uma apresentação da disciplina de Sistemas Distribuídos. O objetivo é testar como as filas do RabbitMQ se comportam.

# Como rodar

## Pre-requisitos:

- Docker
- Docker-Compose
- Java

## Levantando a infra

Para os testes foram utilizados os seguintes componentes:
- 3x Replicas do Rabbit
- Prometheus
- HAProxy

Para subir todos esses componentes basta rodar o seguinte comando:

```shell
docker-compose -f docker-compose-infra.yml up -d
```

Com isso temos três instancias do Rabbit trabalhando em um cluster, para verificar isso basta acessar o [console](http://localhost:15672). Para fazer 
o balanceamento das requisições foi utilizado uma instância do HAProxy, que distribui as requisições para as portas `5672` e `15672` para os nós do Rabbit.
Por fim, temos uma instância do Prometheus para exportar métricas, por padrão as métricas estão sendo exportadas para o meu Grafana, basta trocar as informações
do campo `remote_write` no arquivo [prometheus.yml](prometheus.yml).

## Realizando os testes

Para fazer os testes de carga foi utilizado a ferramenta disponibilizada pelo próprio RabbitMQ, [perf-test](https://github.com/rabbitmq/rabbitmq-perf-test). 
Dentro da documentação presente no projeto tem as instruções detalhadas para a utilização do mesmo, mas aqui eu vou deixar um resumo básico de comandos que podem ajudar.

Para baixar uma versão do .jar:

```shell
wget https://github.com/rabbitmq/rabbitmq-java-tools-binaries-dev/releases/download/v-rabbitmq-perf-test-latest/perf-test-latest.jar
```

Com o .jar em mãos basta executar o seguinte para o teste mais básico:

```shell
java -jar perf-test-latest.jar
```

Adicionalmente alguns argumentos podem ajudar a criar testes mais interessantes, segue uma lista com algum deles:

|       Argumentos      |                          Explicação                         |
| --------------------- | ----------------------------------------------------------- |
| -u                    | Nome da fila a ser usada                                    |
| --producers           | Quantidade de produtores                                    |
| --consumers           | Quantidade de consumidores                                  |
| --rate                | Quantidade de mensagens por produtor/consumidor por segundo |
| --metrics-prometheus  | Habilita as métricas para o Prometheus                      |
| --quorum-queue        | Faz com que a fila criada seja do tipo Quorum               |
| --metrics-tags        | Especifica os tipos das métricas geradas                    |
| --auto-delete         | Especifica se a fila criada se auto deleta quando inativa   |

Exemplo de comando utilizando todos os argumentos citados:

```shell
java -jar perf-test-latest.jar --quorum-queue -u "queue-quorum-test" --metrics-prometheus --producers 5 --consumers 5 --rate 1000 --metrics-tags type=publisher,type=consumer
```

## Referências

- [RabbitMQ - Clustering Guide](https://www.rabbitmq.com/clustering.html)
- [RabbitMQ - PerfTest](https://perftest.rabbitmq.com/)
- [RabbitMQ - Prometheus](https://www.rabbitmq.com/prometheus.html)
- [Grafana - RabbitMQ-Overview](https://grafana.com/grafana/dashboards/10991-rabbitmq-overview/)
- [HAProxy - Configuração](https://docs.haproxy.org/1.7/configuration.html)
