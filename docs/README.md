[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

[![Javadocs](https://www.javadoc.io/badge/com.github.imrafaelmerino/json-scala-values_2.13.svg)](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.13/0.5)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values_2.13/0.5/jar)

- [Introduction](#introduction)
- [Requirements](#requirements)
- [What to use _json-scala-values_ for and when to use it](#whatfor)
- [Installation](#installation)
- [Property-base testing](#pbt)

## <a name="introduction"><a/> Introduction
Welcome to **json-scala-values**! A Json is a well-known and simple data structure, but without immutability and all the benefits that it brings to your code, there is still something missing. 
The Json implemented in json-scala-values uses [immutable.map.HashMap](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/HashMap.html) and 
[immutable.seq.Vector](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/Vector.html) as the underlying persistent data structures. It is the Scala version of the Java 
library [json-values](https://github.com/imrafaelmerino/json-values), which uses the same data structures. The current version **0.5** is a pre-release, so a lot new functionality
is coming. This early release has been published to support json-scala-values-generator, the most elegant and declarative
json generator in the whole wide world. If you like Property-base testing and ScalaCheck, you should take
a look!

## <a name="requirements"><a/> Requirements
Scala 2.13.0

## <a name="whatfor"><a/> What to use _json-scala-values_ for and when to use it
json-scala-values fits like a glove to do Functional Programming. All we need to program is values and functions to manipulate them.

## <a name="installation"><a/> Installation
libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "0.5"

## <a name="pbt"><a/> Property-base testing
This library has been tested using Property-base testing with Scala Check. I've released in a different 
project the Json generators I'm using to develop json-scala-values. I think they are the most declarative 
and beautiful Json generators in the world! I challenge every developer to prove me wrong!


