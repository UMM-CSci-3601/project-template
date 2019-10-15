FROM woahbase/alpine-ng as ngbuild
COPY --chown=alpine . /tmp/client
WORKDIR /tmp/client
RUN npm install --no-interactive \
    && ng build --prod

FROM abiosoft/caddy
COPY --from=ngbuild /tmp/client/dist/client /www/client
COPY ./Caddyfile /etc/Caddyfile
