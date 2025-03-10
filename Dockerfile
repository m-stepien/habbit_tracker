FROM quay.io/keycloak/keycloak:latest
COPY keycloak-listener/target/custom-keycloak-listener.jar /opt/keycloak/providers/
# RUN /opt/keycloak/bin/kc.sh build --verbose
CMD ["/opt/keycloak/bin/kc.sh", "start-dev"]