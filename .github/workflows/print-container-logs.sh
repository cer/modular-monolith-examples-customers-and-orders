#! /bin/bash -e

CONTAINER_IDS=$(docker ps -a -q)

for id in $CONTAINER_IDS ; do
  printf '\n--------------------\n'
  printf 'logs of:\n\n'
  (docker ps -a -f "id=$id" || echo "container gone: $id")
  printf '\n\n'
  (docker logs "$id" || echo "container gone: $id")
  printf -- '--------------------\n\n'
done

mkdir -p "$HOME"/container-logs

docker ps -a > "$HOME"/container-logs/containers.txt

for name in $(docker ps -a --format "{{.Names}}") ; do
  (docker logs "$name" || echo "container gone: $name") > "$HOME"/container-logs/"${name}".log
done
