#!/bin/bash
nohup java -jar ./registry/build/libs/registry-1.0.jar & echo $! > ./registry.file &

sleep 2

nohup java -jar ./catalog/build/libs/catalog-1.0.jar & echo $! > ./catalog.file &

sleep 2

nohup java -jar ./pricing/build/libs/pricing-1.0.jar & echo $! > ./pricing.file &

sleep 2

nohup java -jar ./api/build/libs/api-1.0.jar & echo $! > ./api.file &

sleep 2
