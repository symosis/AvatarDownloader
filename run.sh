#!/usr/bin/env bash
javac -d bin -cp src:lib/* src/main/Main3.java
java -cp bin:lib/* main.Main3
