# **f`(`model`)`** - Functional and Reactive Domain Modeling with Java

When you’re developing an information system to automate the activities of the business, you are modeling the business.
The abstractions that you design, the behaviors that you implement, and the UI interactions that you build all reflect
the business — together,
they constitute the model of the domain.

**This project is in experimental phase**

## Requirements

- Java 19

## Project Amber

(https://openjdk.org/projects/amber/)[https://openjdk.org/projects/amber/]

> The goal of Project Amber is to explore and incubate smaller, productivity-oriented Java language features that have
> been accepted as candidate JEPs in the OpenJDK JEP Process. This Project is sponsored by the Compiler Group.

`fmodel-java` is using Amber **productivity-oriented** features to accelerate development of applications:

- exhaustive pattern matching for Switch expresions
- records
- sealed hierarchy

## Driven by Maven

```shell
./mvnw clean verify
```

## Examples

Check [tests](src/test/java/com/fraktalio/fmodel/domain/example)