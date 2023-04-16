# To run project

$ mvn spring-boot:run

## Endpoints available

###To create an ATM transaction:

    (Post) localhost:8080/transaction/create/atm

###To create a CLIENT transaction:
    (Post) localhost:8080/transaction/create/client

###To create an INTERNAL transaction:
    (Post) localhost:8080/transaction/create/client

###To get transactions filtered by iban and sorted on amount:
     (Get) localhost:8080/transaction/{iban}/{sort}

###To get status of transaction:
     (Get) localhost:8080/transaction/status
     
###To run tests with example payloads in requirements:
go to src/test-integration/java -->integration.flow (run as Junit tests)
     
     


