# openmrs-module-protocolos

Modulo backend de OpenMRS para los protocolos institucionales de la Residencia Santa Maria (RF18).

## Compilar

    mvn clean install

Genera `omod/target/protocolos-1.0.0-SNAPSHOT.omod`.

## Desplegar en desarrollo

    docker cp omod/target/protocolos-1.0.0-SNAPSHOT.omod openmrs-backend-1:/openmrs/data/modules/
    docker restart openmrs-backend-1
