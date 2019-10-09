FROM woahbase/alpine-ng as ngbuild
COPY --chown=alpine . /tmp/client
WORKDIR /tmp/client
RUN npm install --no-interactive \
    && ng build --prod

FROM nginx
COPY --from=ngbuild /tmp/client/dist/client /usr/share/nginx/html
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --no-check-certificate --spider \
  http://localhost:80 || exit 1
