#!/bin/bash
kill $(cat ./api.file) &
kill $(cat ./pricing.file) &
kill $(cat ./catalog.file) &
kill $(cat ./registry.file) &
