FROM quay.io/keycloak/keycloak:latest
COPY keycloak-listener/target/custom-keycloak-listener.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build --verbose
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
